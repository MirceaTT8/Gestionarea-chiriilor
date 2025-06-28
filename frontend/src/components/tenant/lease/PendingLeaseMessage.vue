<!-- PendingLeaseMessage.vue -->
<template>
  <div class="max-w-2xl mx-auto px-4 py-16">
    <div class="bg-white rounded-xl shadow-sm border border-gray-200 p-8 text-center">
      <div class="mx-auto w-16 h-16 bg-yellow-100 rounded-full flex items-center justify-center mb-6">
        <svg xmlns="http://www.w3.org/2000/svg" class="h-8 w-8 text-yellow-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
        </svg>
      </div>

      <h2 class="text-2xl font-bold text-gray-900 mb-2">Waiting for Lease Approval</h2>

      <p class="text-gray-600 mb-6">Your lease application is under review by the property manager.</p>

      <div class="inline-flex items-center px-4 py-2 bg-yellow-50 border border-yellow-200 rounded-full text-sm font-medium text-yellow-800 mb-8">
        <div class="w-2 h-2 bg-yellow-500 rounded-full mr-2 animate-pulse"></div>
        Status: Pending Approval
      </div>

<!--      <div class="space-y-4">-->
<!--        <button-->
<!--            @click="$emit('refresh')"-->
<!--            class="inline-flex items-center px-6 py-3 bg-blue-600 text-white font-medium rounded-lg hover:bg-blue-700 transition-colors"-->
<!--        >-->
<!--          <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">-->
<!--            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />-->
<!--          </svg>-->
<!--          Check Status-->
<!--        </button>-->
<!--      </div>-->

<!--      <div class="mt-8 p-4 bg-gray-50 rounded-lg">-->
<!--        <p class="text-sm text-gray-600">-->
<!--          <strong>What happens next?</strong><br>-->
<!--          Your property manager will review your application and contact you once approved.-->
<!--          This usually takes 1-3 business days.-->
<!--        </p>-->
<!--      </div>-->
    </div>
  </div>
</template>

<script setup>
defineEmits(['refresh'])
</script>