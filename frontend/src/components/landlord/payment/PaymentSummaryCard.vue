<script setup>
import { computed } from 'vue'
import { formatCurrencyCompact } from '@/utils/formatters.js'

const props = defineProps({
  payments: {
    type: Array,
    default: () => []
  }
})

const totalAmount = computed(() =>
    props.payments.reduce((sum, p) => sum + (p.amount || 0), 0)
)

const completedCount = computed(() =>
    props.payments.filter(p => p.status === 'COMPLETED').length
)

const pendingCount = computed(() =>
    props.payments.filter(p => p.status !== 'COMPLETED').length
)
</script>

<template>
  <div class="grid grid-cols-1 sm:grid-cols-3 gap-6 p-6">
    <div class="bg-white rounded-xl shadow-md overflow-hidden transition-transform hover:shadow-lg hover:-translate-y-1">
      <div class="bg-gradient-to-r from-green-500 to-green-600 h-2"></div>
      <div class="p-6 flex items-center">
        <div class="bg-green-100 rounded-full p-3 mr-4">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-8 w-8 text-green-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
          </svg>
        </div>
        <div>
          <p class="text-sm font-medium text-gray-500 mb-1">Total Collected</p>
          <p class="text-2xl font-bold text-gray-800">{{ formatCurrencyCompact(totalAmount) }}</p>
        </div>
      </div>
    </div>

    <div class="bg-white rounded-xl shadow-md overflow-hidden transition-transform hover:shadow-lg hover:-translate-y-1">
      <div class="bg-gradient-to-r from-blue-500 to-blue-600 h-2"></div>
      <div class="p-6 flex items-center">
        <div class="bg-blue-100 rounded-full p-3 mr-4">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-8 w-8 text-blue-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
          </svg>
        </div>
        <div>
          <p class="text-sm font-medium text-gray-500 mb-1">Completed Payments</p>
          <div class="flex items-end">
            <p class="text-2xl font-bold text-gray-800 mr-2">{{ completedCount }}</p>
            <p class="text-sm text-gray-500 mb-1">transaction{{ completedCount !== 1 ? 's' : '' }}</p>
          </div>
        </div>
      </div>
    </div>

    <div class="bg-white rounded-xl shadow-md overflow-hidden transition-transform hover:shadow-lg hover:-translate-y-1">
      <div class="bg-gradient-to-r from-orange-500 to-red-600 h-2"></div>
      <div class="p-6 flex items-center">
        <div class="bg-orange-100 rounded-full p-3 mr-4">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-8 w-8 text-orange-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
          </svg>
        </div>
        <div>
          <p class="text-sm font-medium text-gray-500 mb-1">Pending Payments</p>
          <div class="flex items-end">
            <p class="text-2xl font-bold text-gray-800 mr-2">{{ pendingCount }}</p>
            <p class="text-sm text-gray-500 mb-1">transaction{{ pendingCount !== 1 ? 's' : '' }}</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>