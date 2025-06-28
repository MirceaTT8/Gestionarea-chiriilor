<template>
  <div class="space-y-6">
    <!-- Notes Content -->
    <div class="bg-white shadow-sm rounded-xl overflow-hidden border border-gray-100">
      <div class="px-6 py-4 bg-gray-50 border-b border-gray-100">
        <div class="flex items-center">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-gray-500 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
          </svg>
          <h3 class="font-semibold text-gray-800">Additional Lease Notes</h3>
        </div>
      </div>
      <div class="p-6">
        <div v-if="lease.notes && lease.notes.trim().length > 0" class="prose max-w-none">
          <p class="text-gray-700 whitespace-pre-wrap leading-relaxed">{{ lease.notes }}</p>
        </div>
        <div v-else class="flex items-center justify-center py-6 px-4 bg-gray-50 rounded-lg">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-gray-400 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
          </svg>
          <span class="text-gray-500">No additional notes for this lease.</span>
        </div>
      </div>
    </div>

    <!-- Important Information Section -->
    <div class="bg-blue-50 rounded-xl p-6 border border-blue-100">
      <div class="flex">
        <div class="flex-shrink-0">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-blue-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
          </svg>
        </div>
        <div class="ml-3">
          <h3 class="text-sm font-medium text-blue-800 mb-2">Important Information</h3>
          <p class="text-sm text-blue-700">
            Your lease contains important terms and conditions. Please review your full lease agreement carefully and contact your property manager if you have any questions.
          </p>
          <div class="mt-4">
            <button class="inline-flex items-center px-3 py-1.5 border border-blue-300 text-xs leading-4 font-medium rounded-md text-blue-700 bg-blue-100 hover:text-blue-500 focus:outline-none focus:border-blue-300 focus:shadow-outline-blue active:text-blue-800 active:bg-blue-50 transition ease-in-out duration-150">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-1.5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
              </svg>
              View Full Lease Agreement
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  lease: {
    type: Object,
    required: true
  }
})
</script>