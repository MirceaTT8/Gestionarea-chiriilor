<script setup>
import { ref, computed } from 'vue'
import { createReview } from '@/services/reviewService'
import { useTenantLeaseStore } from '@/stores/tenant/leaseTenantStore.js'

const props = defineProps({
  lease: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['close', 'submitted'])

const tenantStore = useTenantLeaseStore()

const form = ref({
  rating: 5,
  comment: '',
  landlordRating: 5,
  propertyRating: 5,
  maintenanceRating: 5,
  communicationRating: 5
})

const loading = ref(false)
const error = ref('')

const isFormValid = computed(() => {
  return form.value.rating >= 1 &&
      form.value.rating <= 5 &&
      form.value.comment.trim().length >= 10
})

const propertyName = computed(() => {
  // If we have the property directly from the store
  if (tenantStore.property?.name) {
    return tenantStore.property.name
  }

  // Fallback to using property ID
  return `Property #${props.lease.propertyId}`
})

const formatDate = (dateString) => {
  return new Date(dateString).toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}

const submitReview = async () => {
  if (!isFormValid.value) {
    error.value = 'Please fill in all required fields with valid values.'
    return
  }

  try {
    loading.value = true
    error.value = ''

    const reviewData = {
      propertyId: props.lease.propertyId,
      leaseId: props.lease.leaseId,
      rating: form.value.rating,
      comment: form.value.comment.trim(),
      displayName: `Tenant-${Date.now()}`
    }

    console.log('Sending review data:', reviewData)

    await createReview(reviewData)

    // Show success message briefly then refresh page
    emit('submitted')

    // Refresh the page to show updated status
    setTimeout(() => {
      window.location.reload()
    }, 1000)

  } catch (err) {
    console.error('Review submission error:', err)
    error.value = err.message || 'Failed to submit review. Please try again.'
    loading.value = false
  }
}

const closeModal = () => {
  emit('close')
}

const setRating = (field, rating) => {
  form.value[field] = rating
}

const getStarClass = (field, star) => {
  return form.value[field] >= star
      ? 'text-yellow-400 fill-current'
      : 'text-gray-300'
}
</script>

<template>
  <div class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
    <div class="bg-white rounded-xl shadow-2xl max-w-2xl w-full max-h-[90vh] overflow-y-auto">
      <!-- Header -->
      <div class="sticky top-0 bg-white px-6 py-4 border-b border-gray-200 rounded-t-xl">
        <div class="flex items-center justify-between">
          <h2 class="text-xl font-bold text-gray-900">Write a Review</h2>
          <button
              @click="closeModal"
              class="text-gray-400 hover:text-gray-600 transition-colors"
              :disabled="loading"
          >
            <svg class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>
      </div>

      <!-- Content -->
      <div class="p-6">
        <!-- Lease Information -->
        <div class="bg-gray-50 rounded-lg p-4 mb-6">
          <h3 class="font-semibold text-gray-900 mb-2">Lease Information</h3>
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4 text-sm">
            <div>
              <span class="text-gray-600">Property:</span>
              <span class="font-medium ml-1">{{ propertyName }}</span>
            </div>
            <div>
              <span class="text-gray-600">Lease Period:</span>
              <span class="font-medium ml-1">
                {{ formatDate(lease.startDate) }} - {{ formatDate(lease.endDate) }}
              </span>
            </div>
          </div>
        </div>

        <!-- Error Message -->
        <div v-if="error" class="mb-6 p-4 bg-red-50 border border-red-200 rounded-lg">
          <p class="text-red-600 text-sm">{{ error }}</p>
        </div>

        <!-- Success Message (shown while loading after submission) -->
        <div v-if="loading" class="mb-6 p-4 bg-green-50 border border-green-200 rounded-lg">
          <div class="flex items-center">
            <svg class="animate-spin h-5 w-5 text-green-600 mr-2" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
            <p class="text-green-600 text-sm font-medium">Lease reviewed! Refreshing page...</p>
          </div>
        </div>

        <!-- Review Form -->
        <form @submit.prevent="submitReview" class="space-y-6">
          <!-- Overall Rating -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">
              Overall Rating <span class="text-red-500">*</span>
            </label>
            <div class="flex items-center space-x-1">
              <button
                  v-for="star in 5"
                  :key="`overall-${star}`"
                  type="button"
                  @click="setRating('rating', star)"
                  class="text-2xl transition-colors hover:text-yellow-400"
                  :class="getStarClass('rating', star)"
                  :disabled="loading"
              >
                ★
              </button>
              <span class="ml-2 text-sm text-gray-600">({{ form.rating }}/5)</span>
            </div>
          </div>

          <!-- Comment -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">
              Your Experience <span class="text-red-500">*</span>
            </label>
            <textarea
                v-model="form.comment"
                rows="4"
                class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 resize-none"
                placeholder="Share your experience with this property and landlord. What did you like? What could be improved?"
                :disabled="loading"
                required
            />
            <p class="text-xs text-gray-500 mt-1">Minimum 10 characters required</p>
          </div>

          <!-- Form Actions -->
          <div class="flex justify-end space-x-3 pt-6 border-t border-gray-200">
            <button
                type="button"
                @click="closeModal"
                class="px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 transition-colors"
                :disabled="loading"
            >
              Cancel
            </button>
            <button
                type="submit"
                class="px-4 py-2 text-sm font-medium text-white bg-blue-600 border border-transparent rounded-lg hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
                :disabled="loading || !isFormValid"
            >
              <span v-if="loading">Submitting...</span>
              <span v-else>Submit Review</span>
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>