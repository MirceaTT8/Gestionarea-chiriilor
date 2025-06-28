<!-- LeasePropertyDetails.vue -->
<template>
  <div class="space-y-6">
    <!-- Property Card -->
    <div class="bg-gray-50 rounded-xl overflow-hidden">
      <div class="h-48 bg-gradient-to-r from-blue-100 to-indigo-100 flex items-center justify-center">
        <svg xmlns="http://www.w3.org/2000/svg" class="h-12 w-12 text-blue-300" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6" />
        </svg>
      </div>

      <!-- Property Info -->
      <div class="p-6">
        <h3 class="text-xl font-semibold text-gray-800 mb-4">Property Information</h3>

        <div class="grid grid-cols-1 sm:grid-cols-2 gap-6">
          <div class="space-y-4">
            <div>
              <h4 class="text-sm font-medium text-gray-500 uppercase tracking-wider mb-2">Address</h4>
              <p class="text-gray-800">{{ property?.address || 'Address not available' }}</p>
            </div>

            <div>
              <h4 class="text-sm font-medium text-gray-500 uppercase tracking-wider mb-2">Property Type</h4>
              <p class="text-gray-800">{{ formatStatus(property?.type) || 'Type not available' }}</p>
            </div>
          </div>

<!--          </div>-->
        </div>
      </div>
    </div>

  </div>
</template>

<script setup>
import { formatStatus } from "@/utils/formatters.js";

const props = defineProps({
  property: {
    type: Object,
    required: true
  }
})

</script>