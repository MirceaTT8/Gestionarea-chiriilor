<script setup>
import { onMounted, ref, computed, onUnmounted } from 'vue'
import { useTenantLeaseStore } from "@/stores/tenant/leaseTenantStore.js"
import { usePaymentTenantStore } from "@/stores/tenant/paymentTenantStore.js"
import { formatDate, formatCurrencyCompact } from '@/utils/formatters.js'
import { checkIfPropertyReviewed } from '@/services/reviewService'

import LeaseHeader from '@/components/tenant/lease/LeaseHeader.vue'
import LeasePropertyDetails from '@/components/tenant/lease/LeasePropertyDetails.vue'
import LeaseTerms from '@/components/tenant/lease/LeaseTerms.vue'
import LoadingSpinner from '@/components/LoadingSpinner.vue'
import NoLeaseMessage from '@/components/tenant/lease/NoLeaseMessage.vue'
import PendingLeaseMessage from "@/components/tenant/lease/PendingLeaseMessage.vue";
import ReviewModal from '@/components/tenant/review/ReviewModal.vue'

const tenantStore = useTenantLeaseStore()
const paymentStore = usePaymentTenantStore()

const showReviewModal = ref(false)
const selectedLeaseForReview = ref(null)
const isLeaseReviewed = ref(false)
const checkingReviewStatus = ref(false)

const today = ref(new Date())

const shouldShowPendingMessage = computed(() => {
  return !tenantStore.loading && tenantStore.lease?.status === 'PENDING'
})

const shouldShowLeaseDashboard = computed(() => {
  return !tenantStore.loading && tenantStore.tenantHasActiveLease && tenantStore.lease
})

// Check if current lease is terminated and can be reviewed
const hasTerminatedLeaseForReview = computed(() => {
  return !tenantStore.loading &&
      tenantStore.lease?.status === 'TERMINATED' &&
      !isLeaseReviewed.value &&
      !checkingReviewStatus.value
})

// Show reviewed state when lease is terminated and already reviewed
const hasReviewedTerminatedLease = computed(() => {
  return !tenantStore.loading &&
      tenantStore.lease?.status === 'TERMINATED' &&
      isLeaseReviewed.value &&
      !checkingReviewStatus.value
})

const shouldShowNoLeaseMessage = computed(() => {
  return !tenantStore.loading &&
      !tenantStore.tenantHasActiveLease &&
      tenantStore.lease?.status !== 'PENDING' &&
      tenantStore.lease?.status !== 'TERMINATED'
})

// Check if the current terminated lease has been reviewed
const checkCurrentLeaseReviewStatus = async () => {
  if (tenantStore.lease?.status === 'TERMINATED' && tenantStore.lease?.propertyId) {
    try {
      checkingReviewStatus.value = true
      const reviewed = await checkIfPropertyReviewed(tenantStore.lease.propertyId)
      isLeaseReviewed.value = reviewed
    } catch (error) {
      console.error('Error checking review status:', error)
      isLeaseReviewed.value = false
    } finally {
      checkingReviewStatus.value = false
    }
  }
}

const handleRetryLease = async () => {
  await tenantStore.retryLoadLease()
  if (tenantStore.lease) {
    await paymentStore.fetchPayments()
    await checkCurrentLeaseReviewStatus()
  }
}

const handleContactSupport = () => {
  window.location.href = 'mailto:support@yourapp.com?subject=Lease Inquiry'
}

const handleWriteReview = (lease) => {
  selectedLeaseForReview.value = lease || tenantStore.lease
  showReviewModal.value = true
}

const handleReviewSubmitted = async () => {
  showReviewModal.value = false
  selectedLeaseForReview.value = null
  // Update review status after submission
  await checkCurrentLeaseReviewStatus()
}

const handleReviewModalClose = () => {
  showReviewModal.value = false
  selectedLeaseForReview.value = null
}

const handleRefreshPending = async () => {
  await tenantStore.retryLoadLease()
}

// Quick review function for terminated leases
const openReviewForTerminatedLease = () => {
  if (hasTerminatedLeaseForReview.value) {
    handleWriteReview(tenantStore.lease)
  }
}

onMounted(async () => {
  await tenantStore.loadTenantLeaseData()

  // Check review status for terminated leases
  await checkCurrentLeaseReviewStatus()

  if (tenantStore.tenantHasActiveLease) {
    await paymentStore.fetchPayments()
  }

  const interval = setInterval(() => {
    today.value = new Date()
  }, 60000)

  window.dateUpdateInterval = interval
})

onUnmounted(() => {
  if (window.dateUpdateInterval) {
    clearInterval(window.dateUpdateInterval)
    window.dateUpdateInterval = null
  }
})
</script>

<template>
  <div class="max-w-6xl mx-auto px-4 py-10 sm:px-6 lg:px-8">
    <LoadingSpinner v-if="tenantStore.loading || checkingReviewStatus" message="Loading your lease information..." />

    <template v-else>
      <!-- Pending Lease Message -->
      <PendingLeaseMessage
          v-if="shouldShowPendingMessage"
          @refresh="handleRefreshPending"
      />

      <!-- No Lease Message (only for tenants with no lease history or truly no active lease) -->
      <NoLeaseMessage
          v-else-if="shouldShowNoLeaseMessage"
          :has-lease="tenantStore.tenantHasAnyLease"
          :loading="tenantStore.loading"
          :error="tenantStore.error"
          @retry="handleRetryLease"
          @contact-support="handleContactSupport"
          @write-review="handleWriteReview"
      />

      <!-- Terminated Lease Review Section (if not reviewed yet) -->
      <div v-else-if="hasTerminatedLeaseForReview" class="max-w-2xl mx-auto px-4 py-16">
        <div class="bg-white rounded-xl shadow-sm border border-gray-200 p-8 text-center">
          <!-- Icon -->
          <div class="mx-auto w-16 h-16 bg-blue-100 rounded-full flex items-center justify-center mb-6">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-8 w-8 text-blue-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" />
            </svg>
          </div>

          <!-- Title -->
          <h2 class="text-2xl font-bold text-gray-900 mb-2">Lease Completed</h2>

          <!-- Subtitle -->
          <p class="text-gray-600 mb-6">Your lease has ended. Would you like to review your experience?</p>

          <!-- Lease info -->
          <div class="bg-gray-50 rounded-lg p-4 mb-6 text-left">
            <p class="text-sm text-gray-600">
              <strong>Lease Period:</strong> {{ formatDate(tenantStore.lease.startDate) }} - {{ formatDate(tenantStore.lease.endDate) }}
            </p>
            <p class="text-sm text-gray-600 mt-1">
              <strong>Monthly Rent:</strong> {{ formatCurrencyCompact(tenantStore.lease.monthlyRent) }}
            </p>
          </div>

          <!-- Action buttons -->
          <div class="space-y-4">
            <button
                @click="openReviewForTerminatedLease"
                class="w-full inline-flex items-center justify-center px-6 py-3 bg-blue-600 text-white font-medium rounded-lg hover:bg-blue-700 transition-colors"
            >
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" />
              </svg>
              Write Review
            </button>

            <button
                @click="handleContactSupport"
                class="w-full inline-flex items-center justify-center px-6 py-3 bg-gray-100 text-gray-700 font-medium rounded-lg hover:bg-gray-200 transition-colors"
            >
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
              </svg>
              Contact Support
            </button>
          </div>
        </div>
      </div>

      <!-- Reviewed Terminated Lease State -->
      <div v-else-if="hasReviewedTerminatedLease" class="max-w-2xl mx-auto px-4 py-16">
        <div class="bg-white rounded-xl shadow-sm border border-gray-200 p-8 text-center">
          <!-- Green Star Icon -->
          <div class="mx-auto w-16 h-16 bg-green-100 rounded-full flex items-center justify-center mb-6">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-8 w-8 text-green-600" fill="currentColor" viewBox="0 0 24 24">
              <path d="M11.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" />
            </svg>
          </div>

          <!-- Title -->
          <h2 class="text-2xl font-bold text-gray-900 mb-2">Lease Completed</h2>

          <!-- Subtitle -->
          <p class="text-gray-600 mb-6">Your lease has ended. Would you like to review your experience?</p>

          <!-- Thank you message -->
          <p class="text-green-700 mb-6">Thank you for completing your review! Your feedback helps other tenants make informed decisions.</p>

          <!-- Action buttons -->
          <div class="space-y-4">
            <button
                @click="handleContactSupport"
                class="w-full inline-flex items-center justify-center px-6 py-3 bg-green-600 text-white font-medium rounded-lg hover:bg-green-700 transition-colors"
            >
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
              </svg>
              Contact Support
            </button>

            <button
                @click="handleRetryLease"
                class="w-full inline-flex items-center justify-center px-6 py-3 bg-gray-100 text-gray-700 font-medium rounded-lg hover:bg-gray-200 transition-colors"
            >
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
              </svg>
              Refresh
            </button>
          </div>

          <!-- Additional Info -->
          <div class="mt-8 p-4 bg-green-50 rounded-lg border border-green-100">
            <div class="flex items-start">
              <div class="flex-shrink-0 mt-0.5">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-green-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
              </div>
              <div class="ml-3 text-left">
                <p class="text-sm text-green-700">
                  <strong>What's Next?</strong>
                  You can start looking for new rental opportunities or contact your property manager if you need assistance with future housing.
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Active Lease Dashboard -->
      <div v-else-if="shouldShowLeaseDashboard" class="space-y-8">
        <div class="bg-gradient-to-r from-blue-500 to-indigo-600 rounded-xl shadow-md overflow-hidden">
          <div class="px-6 py-8 sm:px-8">
            <LeaseHeader
                v-if="tenantStore.lease && !paymentStore.loading"
                :lease="tenantStore.lease"
                :nextPaymentDate="paymentStore.nextPaymentDate"
                :daysUntilNextPayment="paymentStore.daysUntilNextPayment"
            />

            <div class="mt-8 grid grid-cols-1 md:grid-cols-3 gap-6">
              <div class="bg-white/10 backdrop-blur-sm rounded-xl p-6 text-white">
                <div class="flex items-center">
                  <div class="p-3 bg-white/20 rounded-lg mr-4">
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                    </svg>
                  </div>
                  <div>
                    <p class="text-sm text-white/80">Monthly Rent</p>
                    <p class="text-2xl font-bold mt-1">{{ formatCurrencyCompact(tenantStore.lease.monthlyRent) }}</p>
                  </div>
                </div>
              </div>

              <div class="bg-white/10 backdrop-blur-sm rounded-xl p-6 text-white">
                <div class="flex items-center">
                  <div class="p-3 bg-white/20 rounded-lg mr-4">
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                    </svg>
                  </div>
                  <div>
                    <p class="text-sm text-white/80">Next Payment</p>
                    <p class="text-2xl font-bold mt-1">{{ paymentStore.daysUntilNextPayment }} days</p>
                    <p class="text-xs mt-1 text-white/70">{{ paymentStore.nextPaymentDate ? formatDate(paymentStore.nextPaymentDate) : 'No payment due' }}</p>
                  </div>
                </div>
              </div>

              <div class="bg-white/10 backdrop-blur-sm rounded-xl p-6 text-white">
                <div class="flex items-center">
                  <div class="p-3 bg-white/20 rounded-lg mr-4">
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                    </svg>
                  </div>
                  <div>
                    <p class="text-sm text-white/80">Lease Status</p>
                    <p class="text-lg font-bold mt-1 capitalize">{{ tenantStore.lease.status.toLowerCase() }}</p>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="bg-white rounded-xl shadow-sm border border-gray-200 overflow-hidden">
          <div class="border-b border-gray-200 bg-gray-50">
            <div class="px-6 py-4">
              <h2 class="text-lg font-semibold text-gray-800">Lease Overview</h2>
            </div>
          </div>

          <div class="p-6 space-y-6">
            <LeasePropertyDetails
                v-if="tenantStore.property"
                :property="tenantStore.property"
            />
            <LeaseTerms v-if="tenantStore.lease" :lease="tenantStore.lease" />
          </div>
        </div>
      </div>
    </template>

    <ReviewModal
        v-if="showReviewModal && selectedLeaseForReview"
        :lease="selectedLeaseForReview"
        @close="handleReviewModalClose"
        @review-submitted="handleReviewSubmitted"
    />
  </div>
</template>