<script setup>
import { ref } from 'vue';
import {formatDate} from "@/utils/dateUtils.js";

const props = defineProps({
  log: {
    type: Object,
    required: true
  }
});

const showFullDetails = ref(false);

const formatTime = (timestamp) => {
  const date = new Date(timestamp);
  return date.toLocaleTimeString();
};

const getActionStyles = (actionType) => {
  if (actionType.includes('CREATE')) {
    return 'bg-green-100 text-green-800 border border-green-200';
  }
  if (actionType.includes('UPDATE')) {
    return 'bg-blue-100 text-blue-800 border border-blue-200';
  }
  if (actionType.includes('DELETE')) {
    return 'bg-red-100 text-red-800 border border-red-200';
  }
  if (actionType.includes('VIEW')) {
    return 'bg-yellow-100 text-yellow-800 border border-yellow-200';
  }
  return 'bg-gray-100 text-gray-800 border border-gray-200';
};

const getActionDotClass = (actionType) => {
  if (actionType.includes('CREATE')) return 'bg-green-500';
  if (actionType.includes('UPDATE')) return 'bg-blue-500';
  if (actionType.includes('DELETE')) return 'bg-red-500';
  if (actionType.includes('VIEW')) return 'bg-yellow-500';
  return 'bg-gray-500';
};

const formatDetails = (details) => {
  try {
    const parsed = typeof details === 'string' ? JSON.parse(details) : details;
    return JSON.stringify(parsed, null, 2);
  } catch {
    return details;
  }
};
</script>

<template>
  <tr class="hover:bg-gray-50 transition-colors">
    <td class="px-6 py-4 whitespace-nowrap">
      <div class="flex flex-col">
        <span class="text-sm font-medium text-gray-900">{{ formatTime(log.createdAt) }}</span>
        <span class="text-xs text-gray-500">{{ formatDate(log.createdAt) }}</span>
      </div>
    </td>

    <td class="px-6 py-4 whitespace-nowrap">
      <div class="flex items-center">
        <div class="h-8 w-8 flex-shrink-0 mr-3">
          <div class="h-full w-full rounded-full bg-gray-200 flex items-center justify-center">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 text-gray-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
            </svg>
          </div>
        </div>
        <div>
          <div class="text-sm font-medium text-gray-900">{{ log.userId }}</div>
        </div>
      </div>
    </td>

    <td class="px-6 py-4 whitespace-nowrap">
      <span
          class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium"
          :class="getActionStyles(log.actionType)"
      >
        <span :class="getActionDotClass(log.actionType)" class="w-1.5 h-1.5 mr-1.5 rounded-full"></span>
        {{ log.actionType }}
      </span>
    </td>

    <td class="px-6 py-4 whitespace-nowrap">
      <div class="flex items-center">
        <span class="text-sm text-gray-900">{{ log.entityType }}</span>
      </div>
    </td>

    <td class="px-6 py-4 whitespace-nowrap">
      <span class="text-sm font-mono bg-gray-100 text-gray-800 px-2.5 py-0.5 rounded">{{ log.entityId }}</span>
    </td>

    <td class="px-6 py-4">
      <div class="relative max-w-xs">
        <div class="text-sm text-gray-500 truncate hover:text-clip" @click="showFullDetails = !showFullDetails">
          {{ log.details }}
        </div>

        <button
            @click="showFullDetails = !showFullDetails"
            class="mt-1 inline-flex items-center text-xs font-medium text-blue-600 hover:text-blue-800 transition-colors"
        >
          {{ showFullDetails ? 'Hide Details' : 'View Details' }}
          <svg
              xmlns="http://www.w3.org/2000/svg"
              class="h-3.5 w-3.5 ml-1 transition-transform duration-200"
              :class="showFullDetails ? 'rotate-180' : ''"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
          >
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
          </svg>
        </button>

        <div
            v-if="showFullDetails"
            class="absolute z-10 mt-2 w-96 bg-white rounded-lg shadow-lg border border-gray-200 p-4"
        >
          <div class="flex justify-between items-start mb-2">
            <h3 class="text-sm font-semibold text-gray-900">Log Details</h3>
            <button @click="showFullDetails = false" class="text-gray-400 hover:text-gray-500">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
              </svg>
            </button>
          </div>
          <div class="text-xs text-gray-700 whitespace-pre-wrap bg-gray-50 p-3 rounded-md font-mono">
            {{ formatDetails(log.details) }}
          </div>
        </div>
      </div>
    </td>
  </tr>
</template>


<style scoped>
.text-clip {
  text-overflow: clip;
  white-space: normal;
}
</style>