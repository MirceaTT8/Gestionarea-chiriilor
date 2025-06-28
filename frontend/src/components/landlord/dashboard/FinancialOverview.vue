<script setup>
import Chart from 'primevue/chart'
import { ref, computed } from 'vue'

const props = defineProps({
  rangeOptions: Array,
  activeRange: String,
  filteredChartData: Object,
  chartOptions: Object
})

const activeTab = ref('comparison')

const dataAvailability = computed(() => {
  const enabledRanges = props.rangeOptions?.filter(r => r.enabled) || []
  const totalMonths = props.filteredChartData?.labels?.length || 0

  return {
    enabledRanges,
    totalMonths,
    hasData: totalMonths > 0
  }
})

const displayChartData = computed(() => {
  if (!props.filteredChartData?.datasets) {
    return props.filteredChartData
  }

  if (activeTab.value === 'income') {
    return {
      ...props.filteredChartData,
      datasets: props.filteredChartData.datasets.filter(ds =>
          ds.label === 'Rent Collection'
      )
    }
  } else if (activeTab.value === 'expenses') {
    return {
      ...props.filteredChartData,
      datasets: props.filteredChartData.datasets.filter(ds =>
          ds.label === 'Maintenance Costs'
      )
    }
  } else {
    return props.filteredChartData
  }
})
</script>

<template>
  <section class="col-span-full bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
    <!-- Section Header -->
    <div class="px-6 py-4 border-b border-gray-100 flex flex-col sm:flex-row sm:justify-between sm:items-center">
      <div class="flex items-center mb-4 sm:mb-0">
        <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-gray-500 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z" />
        </svg>
        <h2 class="text-lg font-semibold text-gray-800">Financial Overview</h2>
      </div>

      <!-- Time Range Selector -->
      <div class="flex flex-col sm:flex-row sm:items-center gap-3">
        <div v-if="dataAvailability.hasData" class="text-xs text-gray-500 text-center sm:text-right">
          {{ dataAvailability.totalMonths }} month{{ dataAvailability.totalMonths !== 1 ? 's' : '' }} of data
        </div>

        <div class="inline-flex p-1 bg-gray-100 rounded-lg space-x-1">
          <button
              v-for="range in rangeOptions"
              :key="range.value"
              :disabled="!range.enabled"
              @click="$emit('change-range', range.value)"
              class="px-4 py-2 text-sm font-medium rounded-md transition-colors relative"
              :class="[
              activeRange === range.value
                ? 'bg-white text-indigo-700 shadow-sm'
                : 'text-gray-500 hover:text-gray-700',
              !range.enabled && 'opacity-40 cursor-not-allowed'
            ]"
              :title="!range.enabled ? 'Insufficient data for this range' : ''"
          >
            {{ range.label }}
          </button>
        </div>
      </div>
    </div>

    <div class="p-6">
      <div v-if="!dataAvailability.hasData" class="text-center py-12">
        <svg xmlns="http://www.w3.org/2000/svg" class="h-16 w-16 text-gray-300 mx-auto mb-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z" />
        </svg>
        <h3 class="text-lg font-medium text-gray-900 mb-2">No Financial Data Available</h3>
        <p class="text-gray-500">Start collecting rent and recording maintenance to see your financial overview.</p>
      </div>

      <!-- Chart Content -->
      <div v-else>
        <!-- Financial Chart Tabs -->
        <div class="mb-4 border-b border-gray-200">
          <div class="flex -mb-px">
            <button
                @click="activeTab = 'comparison'"
                class="py-2 px-4 border-b-2 font-medium text-sm transition-colors"
                :class="activeTab === 'comparison'
                ? 'border-indigo-500 text-indigo-600'
                : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'"
            >
              <span class="flex items-center">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 7h6m0 10v-3m-3 3h.01M9 17h.01M9 14h.01M12 14h.01M15 11h.01M12 11h.01M9 11h.01M7 21h10a2 2 0 002-2V5a2 2 0 00-2-2H7a2 2 0 00-2 2v14a2 2 0 002 2z" />
                </svg>
                Comparison
              </span>
            </button>
            <button
                @click="activeTab = 'income'"
                class="ml-8 py-2 px-4 border-b-2 font-medium text-sm transition-colors"
                :class="activeTab === 'income'
                ? 'border-indigo-500 text-indigo-600'
                : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'"
            >
              <span class="flex items-center">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 11l5-5m0 0l5 5m-5-5v12" />
                </svg>
                Income
              </span>
            </button>
            <button
                @click="activeTab = 'expenses'"
                class="ml-8 py-2 px-4 border-b-2 font-medium text-sm transition-colors"
                :class="activeTab === 'expenses'
                ? 'border-indigo-500 text-indigo-600'
                : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'"
            >
              <span class="flex items-center">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 13l-5 5m0 0l-5-5m5 5V6" />
                </svg>
                Expenses
              </span>
            </button>
          </div>
        </div>

        <div class="bg-white rounded-lg border border-gray-100 p-4">
          <div class="h-[350px] relative">
            <Chart
                type="bar"
                :data="displayChartData"
                :options="chartOptions"
                class="w-full h-full"
            />
          </div>

          <!-- Chart Footer with Summary Stats -->
          <div class="mt-4 pt-4 border-t border-gray-100">
            <div class="grid grid-cols-1 sm:grid-cols-3 gap-4 text-center">
              <div class="bg-gray-50 rounded-lg p-3">
                <p class="text-xs text-gray-500 mb-1">Period</p>
                <p class="font-semibold text-gray-900">{{ activeRange === '1M' ? '1 Month' : activeRange === '3M' ? '3 Months' : activeRange === '6M' ? '6 Months' : activeRange === '1Y' ? '1 Year' : '5 Years' }}</p>
              </div>
              <div class="bg-gray-50 rounded-lg p-3">
                <p class="text-xs text-gray-500 mb-1">Data Points</p>
                <p class="font-semibold text-gray-900">{{ displayChartData?.labels?.length || 0 }}</p>
              </div>
              <div class="bg-gray-50 rounded-lg p-3">
                <p class="text-xs text-gray-500 mb-1">View</p>
                <p class="font-semibold text-gray-900 capitalize">{{ activeTab }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>