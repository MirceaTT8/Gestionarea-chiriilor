<script setup>
import { computed } from 'vue'
import { useLogStore } from '@/stores/admin/logStore.js'

const logStore = useLogStore()

const hasActiveFilters = computed(() => {
  return logStore.filters.actionType !== '' ||
      logStore.filters.entityType !== '' ||
      logStore.filters.userId !== '' ||
      logStore.filters.dateRange !== 'all'
})

const activeFilterCount = computed(() => {
  let count = 0
  if (logStore.filters.actionType) count++
  if (logStore.filters.entityType) count++
  if (logStore.filters.userId) count++
  if (logStore.filters.dateRange !== 'all') count++
  return count
})

const applyFilters = () => {
  logStore.applyFilters()
}

const resetFilters = () => {
  logStore.resetFilters()
}

const getDateRangeLabel = (range) => {
  const labels = {
    'today': 'Today',
    'week': 'This Week',
    'month': 'This Month',
    'all': 'All Time'
  }
  return labels[range] || range
}
</script>

<template>
  <div class="space-y-6">
    <!-- Filter Controls -->
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
      <div>
        <label for="actionType" class="block text-sm font-medium text-gray-700 mb-2">
          Action Type
        </label>
        <select
            id="actionType"
            v-model="logStore.filters.actionType"
            @change="logStore.updateFilter('actionType', $event.target.value)"
            class="w-full px-3 py-2 border border-gray-300 rounded-lg shadow-sm focus:ring-2 focus:ring-blue-500 focus:border-blue-500 bg-white"
        >
          <option value="">All Actions</option>
          <option
              v-for="actionType in logStore.uniqueActionTypes"
              :key="actionType"
              :value="actionType"
          >
            {{ actionType }}
          </option>
        </select>
      </div>

      <div>
        <label for="entityType" class="block text-sm font-medium text-gray-700 mb-2">
          Entity Type
        </label>
        <select
            id="entityType"
            v-model="logStore.filters.entityType"
            @change="logStore.updateFilter('entityType', $event.target.value)"
            class="w-full px-3 py-2 border border-gray-300 rounded-lg shadow-sm focus:ring-2 focus:ring-blue-500 focus:border-blue-500 bg-white"
        >
          <option value="">All Entities</option>
          <option
              v-for="entityType in logStore.uniqueEntityTypes"
              :key="entityType"
              :value="entityType"
          >
            {{ entityType }}
          </option>
        </select>
      </div>

      <div>
        <label for="userId" class="block text-sm font-medium text-gray-700 mb-2">
          User ID
        </label>
        <select
            id="userId"
            v-model="logStore.filters.userId"
            @change="logStore.updateFilter('userId', $event.target.value)"
            class="w-full px-3 py-2 border border-gray-300 rounded-lg shadow-sm focus:ring-2 focus:ring-blue-500 focus:border-blue-500 bg-white"
        >
          <option value="">All Users</option>
          <option
              v-for="userId in logStore.uniqueUserIds"
              :key="userId"
              :value="userId"
          >
            User {{ userId }}
          </option>
        </select>
      </div>

      <div>
        <label for="dateRange" class="block text-sm font-medium text-gray-700 mb-2">
          Date Range
        </label>
        <select
            id="dateRange"
            v-model="logStore.filters.dateRange"
            @change="logStore.updateFilter('dateRange', $event.target.value)"
            class="w-full px-3 py-2 border border-gray-300 rounded-lg shadow-sm focus:ring-2 focus:ring-blue-500 focus:border-blue-500 bg-white"
        >
          <option value="all">All Time</option>
          <option value="today">Today</option>
          <option value="week">This Week</option>
          <option value="month">This Month</option>
        </select>
      </div>
    </div>

    <div class="flex flex-col sm:flex-row gap-3 pt-4 border-t border-gray-200">
      <button
          @click="applyFilters"
          class="inline-flex items-center justify-center px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white font-medium rounded-lg transition-colors focus:ring-2 focus:ring-blue-500 focus:ring-offset-2"
      >
        <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 4a1 1 0 011-1h16a1 1 0 011 1v2.586a1 1 0 01-.293.707l-6.414 6.414a1 1 0 00-.293.707V17l-4 4v-6.586a1 1 0 00-.293-.707L3.293 7.293A1 1 0 013 6.586V4z" />
        </svg>
        Apply Filters
      </button>

      <button
          @click="resetFilters"
          class="inline-flex items-center justify-center px-4 py-2 bg-gray-600 hover:bg-gray-700 text-white font-medium rounded-lg transition-colors focus:ring-2 focus:ring-gray-500 focus:ring-offset-2"
      >
        <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
        </svg>
        Reset Filters
      </button>
    </div>
  </div>
</template>
