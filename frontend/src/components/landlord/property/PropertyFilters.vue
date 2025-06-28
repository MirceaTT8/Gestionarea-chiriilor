<script setup>
import { ref, watch } from 'vue'

const props = defineProps({
  filters: Object,
  filterOptions: Object
})

const emit = defineEmits(['update:filters', 'reset'])

const internalFilters = ref({ ...props.filters })

watch(() => props.filters, (newFilters) => {
  if (JSON.stringify(internalFilters.value) !== JSON.stringify(newFilters)) {
    internalFilters.value = { ...newFilters }
  }
}, { deep: true })

let updateTimeout = null
watch(internalFilters, (newVal) => {
  clearTimeout(updateTimeout)
  updateTimeout = setTimeout(() => {
    emit('update:filters', { ...newVal })
  }, 100)
}, { deep: true })

watch(() => props.filterOptions.availableStatuses, (newStatuses) => {
  if (internalFilters.value.status && !newStatuses.includes(internalFilters.value.status)) {
    internalFilters.value.status = ''
  }
}, { deep: true })

const reset = () => {
  internalFilters.value = {
    location: '',
    type: '',
    status: ''
  }
  emit('reset')
}
</script>

<template>
  <div class="bg-white rounded-xl shadow-md p-6 mb-8 border border-gray-100">
    <div class="flex items-center mb-4">
      <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-gray-500 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 4a1 1 0 011-1h16a1 1 0 011 1v2.586a1 1 0 01-.293.707l-6.414 6.414a1 1 0 00-.293.707V17l-4 4v-6.586a1 1 0 00-.293-.707L3.293 7.293A1 1 0 013 6.586V4z" />
      </svg>
      <h2 class="text-lg font-semibold text-gray-800">Filter Properties</h2>
    </div>

    <div class="flex flex-col md:flex-row md:items-end gap-6">
      <div class="flex-1">
        <label class="block text-sm font-medium text-gray-700 mb-2">Location</label>
        <div class="relative">
          <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z" />
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z" />
            </svg>
          </div>
          <input
              v-model="internalFilters.location"
              type="text"
              placeholder="Search by address"
              class="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500"
          >
        </div>
      </div>

      <div class="w-full md:w-56">
        <label class="block text-sm font-medium text-gray-700 mb-2">Type</label>
        <div class="relative">
          <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4" />
            </svg>
          </div>
          <select
              v-model="internalFilters.type"
              class="appearance-none w-full pl-10 pr-10 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 bg-white"
          >
            <option value="">All Types</option>
            <option
                v-for="type in filterOptions.types"
                :key="type"
                :value="type"
                :disabled="!filterOptions.availableTypes?.includes(type)"
                class="disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {{ type }}
            </option>
          </select>
          <div class="absolute inset-y-0 right-0 flex items-center px-2 pointer-events-none">
            <svg class="h-5 w-5 text-gray-400" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor">
              <path fill-rule="evenodd" d="M5.293 7.293a1 1 0 011.414 0L10 10.586l3.293-3.293a1 1 0 111.414 1.414l-4 4a1 1 0 01-1.414 0l-4-4a1 1 0 010-1.414z" clip-rule="evenodd" />
            </svg>
          </div>
        </div>
      </div>

      <div class="w-full md:w-56">
        <label class="block text-sm font-medium text-gray-700 mb-2">Status</label>
        <div class="relative">
          <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
          </div>
          <select
              v-model="internalFilters.status"
              class="appearance-none w-full pl-10 pr-10 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 bg-white"
          >
            <option value="">All Statuses</option>
            <option
                v-for="status in filterOptions.statuses"
                :key="status"
                :value="status"
                :disabled="!filterOptions.availableStatuses?.includes(status)"
                class="disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {{ status }}
            </option>
          </select>
          <div class="absolute inset-y-0 right-0 flex items-center px-2 pointer-events-none">
            <svg class="h-5 w-5 text-gray-400" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor">
              <path fill-rule="evenodd" d="M5.293 7.293a1 1 0 011.414 0L10 10.586l3.293-3.293a1 1 0 111.414 1.414l-4 4a1 1 0 01-1.414 0l-4-4a1 1 0 010-1.414z" clip-rule="evenodd" />
            </svg>
          </div>
        </div>
      </div>

      <button
          @click="reset"
          class="h-12 px-6 py-3 bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200 transition-colors font-medium flex items-center justify-center gap-2"
      >
        <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
        </svg>
        Reset
      </button>
    </div>
  </div>
</template>

<style scoped>
option:disabled {
  @apply text-gray-400 bg-gray-50;
}
</style>