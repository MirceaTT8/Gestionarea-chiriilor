<script setup>
import MaintenanceCardTenant from './MaintenanceCardTenant.vue'

defineProps({
  requests: {
    type: Array,
    required: true
  },
  loading: {
    type: Boolean,
    default: false
  },
  error: {
    type: [String, null],
    default: null
  },
  statusFilter: {
    type: String,
    required: true
  }
})

const emit = defineEmits(['create-new', 'retry', 'feedback-submitted'])

const getStatusLabel = (status) => {
  switch(status) {
    case 'PENDING': return 'Pending';
    case 'IN_PROGRESS': return 'In Progress';
    case 'COMPLETED': return 'Completed';
    case 'ALL': return 'All';
    default: return status;
  }
}

const handleFeedbackSubmitted = (feedbackData) => {
  emit('feedback-submitted', feedbackData)
}
</script>

<template>
  <!-- Loading State -->
  <div v-if="loading" class="bg-white rounded-xl shadow-sm overflow-hidden p-8">
    <div class="flex flex-col items-center justify-center py-16 space-y-4">
      <div class="w-12 h-12 rounded-full border-4 border-t-teal-500 border-teal-200 animate-spin"></div>
      <p class="text-gray-600 font-medium">Loading your maintenance requests...</p>
      <p class="text-sm text-gray-500">This may take a moment</p>
    </div>
  </div>

  <!-- Error State -->
  <div v-else-if="error" class="bg-white rounded-xl shadow-sm overflow-hidden p-8">
    <div class="bg-red-50 border-l-4 border-red-400 p-4 rounded-md">
      <div class="flex">
        <div class="flex-shrink-0">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-red-400" viewBox="0 0 20 20" fill="currentColor">
            <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clip-rule="evenodd" />
          </svg>
        </div>
        <div class="ml-3">
          <p class="text-sm text-red-700">{{ error }}</p>
          <div class="mt-4">
            <button
                @click="$emit('retry')"
                class="inline-flex items-center px-3 py-2 border border-transparent text-sm leading-4 font-medium rounded-md text-red-700 bg-red-100 hover:bg-red-200 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-red-500"
            >
              <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-1.5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
              </svg>
              Try Again
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>

  <!-- Empty State -->
  <div v-else-if="requests.length === 0" class="bg-white rounded-xl shadow-md overflow-hidden p-8">
    <div class="text-center py-16">
      <svg xmlns="http://www.w3.org/2000/svg" class="mx-auto h-12 w-12 text-gray-300" fill="none" viewBox="0 0 24 24" stroke="currentColor">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1" d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10" />
      </svg>
      <h3 class="mt-2 text-sm font-medium text-gray-900">No maintenance requests</h3>
      <p class="mt-1 text-sm text-gray-500">
        {{ statusFilter !== 'ALL' ? `You don't have any ${getStatusLabel(statusFilter).toLowerCase()} requests.` : 'You haven\'t created any maintenance requests yet.' }}
      </p>
      <div class="mt-6">
        <button
            @click="$emit('create-new')"
            class="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-teal-600 hover:bg-teal-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-teal-500"
        >
          <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
          </svg>
          Create New Request
        </button>
      </div>
    </div>
  </div>

  <!-- Requests List -->
  <div v-else class="space-y-6">
    <!-- Status Filter Indicator -->
    <div v-if="statusFilter !== 'ALL'" class="bg-gray-50 rounded-lg mb-4 py-2 px-4 flex items-center">
      <span
          class="inline-block w-2 h-2 rounded-full mr-2"
          :class="{
          'bg-yellow-500': statusFilter === 'PENDING',
          'bg-blue-500': statusFilter === 'IN_PROGRESS',
          'bg-green-500': statusFilter === 'COMPLETED',
          'bg-gray-500': statusFilter === 'ALL'
        }"
      ></span>
      <span class="text-sm text-gray-700">Showing {{ requests.length }} {{ getStatusLabel(statusFilter) }} request{{ requests.length !== 1 ? 's' : '' }}</span>
    </div>

    <MaintenanceCardTenant
        v-for="request in requests"
        :key="request.requestId"
        :request="request"
        @feedback-submitted="handleFeedbackSubmitted"
    />
  </div>
</template>