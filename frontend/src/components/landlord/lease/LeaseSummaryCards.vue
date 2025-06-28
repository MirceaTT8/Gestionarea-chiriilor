<script setup>
defineProps({
  totalLeases: {
    type: Number,
    required: true
  },
  activeLeases: {
    type: Number,
    required: true
  },
  monthlyRevenue: {
    type: Number,
    required: true
  }
});
</script>

<template>
  <div class="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
    <!-- Total Leases Card -->
    <div class="bg-white p-6 rounded-xl shadow-sm border border-gray-100 hover:shadow-md transition-shadow duration-300 relative overflow-hidden">
      <div class="absolute top-0 right-0 w-24 h-24 bg-indigo-50 rounded-full -mr-12 -mt-12 opacity-60"></div>
      <div class="relative z-10">
        <div class="flex items-center mb-3">
          <div class="bg-indigo-100 p-2 rounded-lg mr-3">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-indigo-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
            </svg>
          </div>
          <h3 class="text-sm font-medium text-gray-500">Total Leases</h3>
        </div>
        <div class="flex items-end">
          <p class="text-3xl font-bold text-gray-800">{{ totalLeases }}</p>
          <span class="text-sm text-gray-500 ml-2 mb-1">agreements</span>
        </div>
      </div>
    </div>

    <!-- Active Leases Card -->
    <div class="bg-white p-6 rounded-xl shadow-sm border border-gray-100 hover:shadow-md transition-shadow duration-300 relative overflow-hidden">
      <div class="absolute top-0 right-0 w-24 h-24 bg-blue-50 rounded-full -mr-12 -mt-12 opacity-60"></div>
      <div class="relative z-10">
        <div class="flex items-center mb-3">
          <div class="bg-blue-100 p-2 rounded-lg mr-3">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-blue-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
          </div>
          <h3 class="text-sm font-medium text-gray-500">Active Leases</h3>
        </div>
        <div class="flex items-end">
          <p class="text-3xl font-bold text-gray-800">{{ activeLeases }}</p>
          <span class="text-sm text-gray-500 ml-2 mb-1">active</span>
        </div>
      </div>
    </div>

    <!-- Monthly Revenue Card -->
    <div class="bg-white p-6 rounded-xl shadow-sm border-l-4 border-green-500 hover:shadow-md transition-shadow duration-300 relative overflow-hidden">
      <div class="absolute top-0 right-0 w-24 h-24 bg-green-50 rounded-full -mr-12 -mt-12 opacity-60"></div>
      <div class="relative z-10">
        <div class="flex items-center mb-3">
          <div class="bg-green-100 p-2 rounded-lg mr-3">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-green-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
          </div>
          <h3 class="text-sm font-medium text-gray-500">Monthly Revenue</h3>
        </div>
        <div class="flex items-end">
          <p class="text-3xl font-bold text-gray-800">${{ monthlyRevenue.toLocaleString() }}</p>
          <span class="text-sm text-gray-500 ml-2 mb-1">per month</span>
        </div>
      </div>
    </div>
  </div>
</template>