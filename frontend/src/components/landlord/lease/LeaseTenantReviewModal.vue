<script setup>
import { ref, computed } from 'vue'
import { createLandlordReview } from '@/services/reviewService.js'

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  lease: {
    type: Object,
    required: true
  },
  tenant: {
    type: Object,
    default: null
  },
  property: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['close', 'success'])

const loading = ref(false)
const error = ref(null)

const reviewForm = ref({
  rating: 5,
  comment: ''
})

const tenantName = computed(() => {
  if (props.tenant) {
    return `${props.tenant.firstName} ${props.tenant.lastName}`
  }
  return `Tenant #${props.lease.tenantId}`
})

const propertyName = computed(() => {
  if (props.property) {
    return props.property.name || props.property.address
  }
  return `Property #${props.lease.propertyId}`
})

const handleSubmit = async () => {
  if (!reviewForm.value.comment.trim()) {
    error.value = 'Please provide a comment for your review.'
    return
  }

  loading.value = true
  error.value = null

  try {
    const reviewData = {
      tenantId: props.lease.tenantId,
      propertyId: props.lease.propertyId,
      leaseId: props.lease.leaseId,
      rating: reviewForm.value.rating,
      comment: reviewForm.value.comment.trim()
    }

    await createLandlordReview(reviewData)
    emit('success')
    handleClose()
  } catch (err) {
    error.value = err.message || 'Failed to submit review. Please try again.'
  } finally {
    loading.value = false
  }
}

const handleClose = () => {
  reviewForm.value = {
    rating: 5,
    comment: ''
  }
  error.value = null
  emit('close')
}

const setRating = (rating) => {
  reviewForm.value.rating = rating
}
</script>

<template>
  <div
      v-if="visible"
      class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50"
      @click.self="handleClose"
  >
    <div class="bg-white rounded-xl shadow-xl w-full max-w-lg animate-fadeIn">
      <div class="p-6">
        <!-- Header -->
        <div class="flex items-center justify-between mb-6">
          <div class="flex items-center">
            <div class="bg-blue-100 p-2 rounded-full mr-3">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-blue-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" />
              </svg>
            </div>
            <div>
              <h2 class="text-xl font-bold text-gray-800">Review Tenant</h2>
              <p class="text-sm text-gray-500">Share your experience with {{ tenantName }}</p>
            </div>
          </div>
          <button
              @click="handleClose"
              class="text-gray-400 hover:text-gray-600 transition-colors"
          >
            <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>

        <!-- Lease Info -->
        <div class="bg-gray-50 rounded-lg p-4 mb-6">
          <div class="grid grid-cols-2 gap-4 text-sm">
            <div>
              <span class="text-gray-500">Tenant:</span>
              <p class="font-medium text-gray-800">{{ tenantName }}</p>
            </div>
            <div>
              <span class="text-gray-500">Property:</span>
              <p class="font-medium text-gray-800">{{ propertyName }}</p>
            </div>
<!--            <div>-->
<!--              <span class="text-gray-500">Lease ID:</span>-->
<!--              <p class="font-medium text-gray-800">#{{ lease.leaseId }}</p>-->
<!--            </div>-->
            <div>
              <span class="text-gray-500">Monthly Rent:</span>
              <p class="font-medium text-gray-800">${{ lease.monthlyRent?.toLocaleString() }}</p>
            </div>
          </div>
        </div>

        <!-- Error Message -->
        <div v-if="error" class="bg-red-50 border border-red-200 rounded-lg p-3 mb-4">
          <div class="flex items-center">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-red-500 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
            </svg>
            <span class="text-red-700 text-sm">{{ error }}</span>
          </div>
        </div>

        <!-- Rating Section -->
        <div class="mb-6">
          <label class="block text-sm font-medium text-gray-700 mb-3">Rating</label>
          <div class="flex items-center gap-1">
            <button
                v-for="star in 5"
                :key="star"
                @click="setRating(star)"
                class="focus:outline-none transition-colors"
            >
              <svg
                  xmlns="http://www.w3.org/2000/svg"
                  class="h-8 w-8"
                  :class="star <= reviewForm.rating ? 'text-yellow-400' : 'text-gray-300'"
                  fill="currentColor"
                  viewBox="0 0 24 24"
              >
                <path d="M11.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" />
              </svg>
            </button>
            <span class="ml-3 text-sm text-gray-600">{{ reviewForm.rating }} out of 5 stars</span>
          </div>
        </div>

        <!-- Comment Section -->
        <div class="mb-6">
          <label class="block text-sm font-medium text-gray-700 mb-2">
            Comment <span class="text-red-500">*</span>
          </label>
          <textarea
              v-model="reviewForm.comment"
              rows="4"
              class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 resize-none"
              placeholder="Share your experience with this tenant. Was rent paid on time? How was the communication? Any property maintenance issues?"
              maxlength="500"
          ></textarea>
          <div class="text-right text-xs text-gray-500 mt-1">
            {{ reviewForm.comment.length }}/500 characters
          </div>
        </div>

        <!-- Action Buttons -->
        <div class="flex justify-end gap-3">
          <button
              @click="handleClose"
              :disabled="loading"
              class="px-4 py-2 text-gray-700 hover:text-gray-900 hover:bg-gray-100 rounded-lg transition-colors disabled:opacity-50"
          >
            Cancel
          </button>
          <button
              @click="handleSubmit"
              :disabled="loading || !reviewForm.comment.trim()"
              class="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:bg-blue-300 disabled:cursor-not-allowed transition-colors flex items-center"
          >
            <svg
                v-if="loading"
                class="animate-spin -ml-1 mr-2 h-4 w-4 text-white"
                xmlns="http://www.w3.org/2000/svg"
                fill="none"
                viewBox="0 0 24 24"
            >
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
            {{ loading ? 'Submitting...' : 'Submit Review' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: scale(0.95);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

.animate-fadeIn {
  animation: fadeIn 0.2s ease-out;
}
</style>