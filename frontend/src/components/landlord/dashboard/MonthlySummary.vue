
<script setup>
import { formatCurrencyCompact } from '@/utils/formatters.js'

const props = defineProps({
  rentPaymentsLastMonth: Number,
  occupancyRate: Number,
  vacantUnits: Number,
  maintenanceCostThisMonth: Number
})
</script>

<template>
  <section class="col-span-full md:col-span-4 bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
    <div class="px-6 py-4 border-b border-gray-100 flex items-center">
      <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-gray-500 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z" />
      </svg>
      <h2 class="text-lg font-semibold text-gray-800">Monthly Summary</h2>
    </div>

    <div class="p-6 space-y-6">
      <div class="rounded-xl bg-gradient-to-r from-green-50 to-emerald-50 border border-green-100 overflow-hidden">
        <div class="px-6 py-5 flex justify-between items-center">
          <div>
            <p class="text-sm text-green-700 font-medium">Collected Rent</p>
            <div class="mt-1">
              <p class="text-2xl font-bold text-gray-800">{{ formatCurrencyCompact(rentPaymentsLastMonth) }}</p>
            </div>
            <p class="text-xs text-green-600 mt-1">Past 30 days</p>
          </div>
          <div class="h-12 w-12 flex items-center justify-center rounded-lg bg-green-100">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-green-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
          </div>
        </div>
        <div class="w-full bg-green-200 h-1.5">
          <div class="bg-green-500 h-1.5" style="width: 85%"></div>
        </div>
      </div>

      <div class="rounded-xl bg-gradient-to-r from-purple-50 to-indigo-50 border border-purple-100 overflow-hidden">
        <div class="px-6 py-5 flex justify-between items-center">
          <div>
            <p class="text-sm text-purple-700 font-medium">Occupancy Rate</p>
            <div class="mt-1">
              <p class="text-2xl font-bold text-gray-800">{{ occupancyRate }}%</p>
            </div>
            <p class="text-xs text-purple-600 mt-1">{{ vacantUnits }} vacant units</p>
          </div>
          <div class="h-12 w-12 flex items-center justify-center rounded-lg bg-purple-100">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-purple-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4" />
            </svg>
          </div>
        </div>
        <div class="w-full bg-purple-200 h-1.5">
          <div class="bg-purple-500 h-1.5" :style="`width: ${occupancyRate}%`"></div>
        </div>
      </div>

      <div class="rounded-xl bg-gradient-to-r from-orange-50 to-amber-50 border border-orange-100 overflow-hidden">
        <div class="px-6 py-5 flex justify-between items-center">
          <div>
            <p class="text-sm text-orange-700 font-medium">Maintenance Costs</p>
            <div class="mt-1">
              <p class="text-2xl font-bold text-gray-800">{{ formatCurrencyCompact(maintenanceCostThisMonth) }}</p>
            </div>
            <p class="text-xs text-orange-600 mt-1">Past 30 days</p>
          </div>
          <div class="h-12 w-12 flex items-center justify-center rounded-lg bg-orange-100">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-orange-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5.882V19.24a1.76 1.76 0 01-3.417.592l-2.147-6.15M18 13a3 3 0 100-6M5.436 13.683A4.001 4.001 0 017 6h1.832c4.1 0 7.625-1.234 9.168-3v14c-1.543-1.766-5.067-3-9.168-3H7a3.988 3.988 0 01-1.564-.317z" />
            </svg>
          </div>
        </div>
        <div class="w-full bg-orange-200 h-1.5">
          <div class="bg-orange-500 h-1.5" style="width: 40%"></div>
        </div>
      </div>
    </div>
  </section>
</template>
