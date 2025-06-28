<script setup>
import { computed, ref, onMounted } from 'vue'
import { fetchPendingReviews, fetchPendingReviewsCount, checkIfPropertyReviewed } from '@/services/reviewService'

const props = defineProps({
  hasLease: {
    type: Boolean,
    default: null // null means unknown, true = had lease before, false = never had lease
  },
  loading: {
    type: Boolean,
    default: false
  },
  error: {
    type: String,
    default: null
  }
})

const emit = defineEmits(['retry', 'contact-support', 'write-review'])

// Review-related state
const pendingReviews = ref([])
const pendingReviewsCount = ref(0)
const hasPendingReviews = ref(false)
const reviewsLoading = ref(false)
const showReviewSection = ref(false)
const reviewedProperties = ref(new Set())

// Helper function to check if a property has been reviewed
const isPropertyReviewed = (propertyId) => {
  return reviewedProperties.value.has(propertyId)
}

// Determine what message to show based on tenant's lease history
const messageType = computed(() => {
  if (props.hasLease === null) return 'unknown'
  if (props.hasLease === false) return 'never-had-lease'

  // Check if has lease but no pending reviews = reviewed state
  if (props.hasLease === true && !hasPendingReviews.value && !reviewsLoading.value) {
    return 'lease-reviewed'
  }

  return 'no-active-lease' // Had lease before but no active one
})

const getMessage = computed(() => {
  switch (messageType.value) {
    case 'never-had-lease':
      return {
        title: 'Welcome to Our Platform',
        subtitle: 'Get Started with Your First Lease',
        description: 'You don\'t have any lease agreements in our system yet. Contact your property manager to get started with your rental.',
        icon: 'welcome',
        action: 'Contact Property Manager'
      }
    case 'lease-reviewed':
      return {
        title: 'Lease Completed',
        subtitle: 'Your lease has ended. Would you like to review your experience?',
        description: 'Thank you for completing your review! Your feedback helps other tenants make informed decisions.',
        icon: 'completed',
        action: 'Contact Support'
      }
    case 'no-active-lease':
      return {
        title: 'No Active Lease',
        subtitle: 'Your Current Lease Has Ended',
        description: 'Your previous lease has expired or been terminated. If you need to renew or start a new lease, please contact your property manager.',
        icon: 'expired',
        action: 'Renew Lease'
      }
    default:
      return {
        title: 'Loading Your Lease Information',
        subtitle: 'Please Wait',
        description: 'We\'re checking your lease status...',
        icon: 'loading',
        action: null
      }
  }
})

// Load pending reviews when component mounts and when lease status changes
const loadPendingReviews = async () => {
  if (messageType.value !== 'no-active-lease') return

  try {
    reviewsLoading.value = true
    const [reviewsData, countData] = await Promise.all([
      fetchPendingReviews(),
      fetchPendingReviewsCount()
    ])

    pendingReviews.value = reviewsData || []
    pendingReviewsCount.value = countData.count || 0
    hasPendingReviews.value = countData.hasPendingReviews || false
    showReviewSection.value = hasPendingReviews.value

    // Check each property individually for review status
    for (const lease of pendingReviews.value) {
      const isReviewed = await checkIfPropertyReviewed(lease.propertyId)
      if (isReviewed) {
        reviewedProperties.value.add(lease.propertyId)
      }
    }
  } catch (error) {
    console.error('Error loading pending reviews:', error)
    // Don't show review section if there's an error
    showReviewSection.value = false
  } finally {
    reviewsLoading.value = false
  }
}

const handleRetry = () => {
  emit('retry')
}

const handleContactSupport = () => {
  emit('contact-support')
}

const handleWriteReview = async (lease) => {
  try {
    // Check if property has already been reviewed using the API
    const alreadyReviewed = await checkIfPropertyReviewed(lease.propertyId)

    if (alreadyReviewed) {
      console.log('Property already reviewed')
      // Add to reviewed set to update UI
      reviewedProperties.value.add(lease.propertyId)
      return
    }

    emit('write-review', lease)
  } catch (error) {
    console.error('Error checking review status:', error)
    // If check fails, proceed to allow review (fail safe)
    emit('write-review', lease)
  }
}

const formatDate = (dateString) => {
  return new Date(dateString).toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}

// Watch for changes in lease status and load reviews accordingly
onMounted(() => {
  if (messageType.value === 'no-active-lease' || messageType.value === 'lease-reviewed') {
    loadPendingReviews()
  }
})
</script>

<template>
  <div class="bg-white rounded-xl shadow-md overflow-hidden">
    <div class="px-6 py-8 md:px-10 md:py-12 text-center">
      <!-- Error State -->
      <div v-if="error" class="mb-6">
        <div class="flex justify-center mb-4">
          <div class="p-4 bg-red-50 rounded-full inline-flex">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-16 w-16 text-red-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
            </svg>
          </div>
        </div>
        <h2 class="text-2xl font-bold text-gray-900 mb-3">Unable to Load Lease Information</h2>
        <p class="text-gray-600 max-w-lg mx-auto mb-6">{{ error }}</p>
        <button
            @click="handleRetry"
            class="inline-flex items-center px-6 py-3 bg-blue-600 text-white font-medium rounded-lg hover:bg-blue-700 transition-colors shadow-sm"
        >
          <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
          </svg>
          Try Again
        </button>
      </div>

      <!-- Loading State -->
      <div v-else-if="loading" class="mb-6">
        <div class="flex justify-center mb-4">
          <div class="p-4 bg-blue-50 rounded-full inline-flex">
            <svg class="animate-spin h-16 w-16 text-blue-400" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
          </div>
        </div>
        <h2 class="text-2xl font-bold text-gray-900 mb-3">{{ getMessage.title }}</h2>
        <p class="text-gray-600 max-w-lg mx-auto">{{ getMessage.description }}</p>
      </div>

      <!-- No Lease States -->
      <div v-else>
        <!-- Illustration -->
        <div class="flex justify-center mb-6">
          <div class="p-4 rounded-full inline-flex"
               :class="messageType === 'never-had-lease' || messageType === 'lease-reviewed' ? 'bg-green-50' : 'bg-yellow-50'">
            <!-- Welcome Icon for new tenants -->
            <svg v-if="messageType === 'never-had-lease'"
                 xmlns="http://www.w3.org/2000/svg"
                 class="h-16 w-16 text-green-400"
                 fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6" />
            </svg>

            <!-- Star Icon for completed/reviewed leases -->
            <svg v-else-if="messageType === 'lease-reviewed'"
                 xmlns="http://www.w3.org/2000/svg"
                 class="h-16 w-16 text-green-400"
                 fill="currentColor" viewBox="0 0 24 24">
              <path d="M11.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" />
            </svg>

            <!-- Expired Icon for ended leases -->
            <svg v-else
                 xmlns="http://www.w3.org/2000/svg"
                 class="h-16 w-16 text-yellow-400"
                 fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
            </svg>
          </div>
        </div>

        <!-- Message Content -->
        <h2 class="text-2xl font-bold text-gray-900 mb-2">{{ getMessage.title }}</h2>
        <h3 class="text-lg font-medium text-gray-700 mb-4">{{ getMessage.subtitle }}</h3>
        <p class="text-gray-600 max-w-lg mx-auto mb-8">{{ getMessage.description }}</p>

        <!-- Pending Reviews Section (only for ended leases) -->
        <div v-if="messageType === 'no-active-lease' && showReviewSection && !reviewsLoading"
             class="mb-8 p-6 bg-blue-50 rounded-xl border border-blue-200">
          <div class="flex items-center justify-center mb-4">
            <div class="p-2 bg-blue-100 rounded-lg mr-3">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-blue-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" />
              </svg>
            </div>
            <h3 class="text-lg font-semibold text-blue-900">
              {{ pendingReviewsCount === 1 ? 'Write Your Review' : `Write ${pendingReviewsCount} Reviews` }}
            </h3>
          </div>

          <p class="text-blue-800 mb-6">
            You have {{ pendingReviewsCount }} completed {{ pendingReviewsCount === 1 ? 'lease' : 'leases' }}
            waiting for your review. Help other tenants by sharing your experience!
          </p>

          <!-- List of pending reviews -->
          <div class="space-y-3">
            <div v-for="lease in pendingReviews" :key="lease.leaseId"
                 class="bg-white rounded-lg p-4 border border-blue-200 flex items-center justify-between">
              <div class="text-left">
                <h4 class="font-medium text-gray-900">Property #{{ lease.propertyId }}</h4>
                <p class="text-sm text-gray-600">
                  Lease Period: {{ formatDate(lease.startDate) }} - {{ formatDate(lease.endDate) }}
                </p>
              </div>
              <button
                  @click="handleWriteReview(lease)"
                  :disabled="isPropertyReviewed(lease.propertyId)"
                  :class="[
                    'inline-flex items-center px-4 py-2 text-sm font-medium rounded-lg transition-colors',
                    isPropertyReviewed(lease.propertyId)
                        ? 'bg-gray-300 text-gray-500 cursor-not-allowed'
                        : 'bg-blue-600 text-white hover:bg-blue-700'
                  ]"
              >
                <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15.232 5.232l3.536 3.536m-2.036-5.036a2.5 2.5 0 113.536 3.536L6.5 21.036H3v-3.572L16.732 3.732z" />
                </svg>
                {{ isPropertyReviewed(lease.propertyId) ? 'Already Reviewed' : 'Write Review' }}
              </button>
            </div>
          </div>
        </div>

        <!-- Reviews Loading State -->
        <div v-if="messageType === 'no-active-lease' && reviewsLoading"
             class="mb-8 p-4 bg-blue-50 rounded-lg">
          <div class="flex items-center justify-center">
            <svg class="animate-spin h-5 w-5 text-blue-600 mr-2" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
            <span class="text-blue-700 text-sm">Checking for pending reviews...</span>
          </div>
        </div>

        <!-- Action Buttons -->
        <div class="flex flex-col sm:flex-row gap-4 justify-center">
          <button
              @click="handleContactSupport"
              class="inline-flex items-center px-6 py-3 font-medium rounded-lg transition-colors shadow-sm"
              :class="messageType === 'never-had-lease' || messageType === 'lease-reviewed'
              ? 'bg-green-600 text-white hover:bg-green-700'
              : 'bg-yellow-600 text-white hover:bg-yellow-700'"
          >
            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
            </svg>
            {{ getMessage.action }}
          </button>

          <button
              @click="handleRetry"
              class="inline-flex items-center px-6 py-3 bg-white text-gray-700 font-medium rounded-lg border border-gray-300 hover:bg-gray-50 transition-colors shadow-sm"
          >
            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
            </svg>
            Refresh
          </button>
        </div>

        <!-- Additional Info -->
        <div class="mt-8 p-4 rounded-lg border"
             :class="messageType === 'lease-reviewed' ? 'bg-green-50 border-green-100' : 'bg-blue-50 border-blue-100'">
          <div class="flex items-start">
            <div class="flex-shrink-0 mt-0.5">
              <svg xmlns="http://www.w3.org/2000/svg"
                   class="h-5 w-5"
                   :class="messageType === 'lease-reviewed' ? 'text-green-400' : 'text-blue-400'"
                   fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
            </div>
            <div class="ml-3 text-left">
              <p class="text-sm"
                 :class="messageType === 'lease-reviewed' ? 'text-green-700' : 'text-blue-700'">
                <strong>{{ messageType === 'lease-reviewed' ? 'What\'s Next?' : 'Need Help?' }}</strong>
                <span v-if="messageType === 'lease-reviewed'">
                  You can start looking for new rental opportunities or contact your property manager if you need assistance with future housing.
                </span>
                <span v-else>
                  If you believe this is an error or need assistance setting up your lease,
                  please contact our support team or your property manager.
                  <span v-if="messageType === 'no-active-lease' && hasPendingReviews">
                    Don't forget to complete your pending reviews to help other tenants!
                  </span>
                </span>
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>