<script setup>
import PropertyCard from './PropertyCard.vue'
import {onMounted} from "vue";

const props = defineProps({
  properties: {
    type: Array,
    required: true
  },
  loading: {
    type: Boolean,
    default: false
  },
  error: {
    type: [Error, null],
    default: null
  }
})

const emit = defineEmits(['reset-filters'])
</script>

<template>
  <div v-if="loading" class="flex flex-col items-center justify-center gap-4 py-12">
    <div class="w-12 h-12 border-4 border-gray-200 border-t-blue-500 rounded-full animate-spin"></div>
    <p class="text-gray-600">Loading properties...</p>
  </div>

  <div v-else-if="error" class="flex items-center gap-2 p-4 bg-red-50 rounded-lg text-red-700">
    <span class="text-xl">⚠️</span>
    <p>Error loading properties: {{ error.message }}</p>
  </div>

  <div v-else-if="!properties.length" class="text-center py-12">
    <div class="w-24 h-24 mx-auto mb-4 bg-gray-100 rounded-full flex items-center justify-center">
      <svg xmlns="http://www.w3.org/2000/svg" class="h-12 w-12 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6" />
      </svg>
    </div>
    <h3 class="text-xl text-gray-600 mb-2">No properties found</h3>
    <p class="text-gray-500">No properties match your current filters.</p>
    <button
        @click="$emit('reset-filters')"
        class="mt-4 px-4 py-2 bg-blue-50 text-blue-600 rounded-md hover:bg-blue-100 transition-colors text-sm"
    >
      Clear all filters
    </button>
  </div>

  <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
    <PropertyCard
        v-for="property in properties"
        :key="property.propertyId"
        :property="property"
    />
  </div>
</template>