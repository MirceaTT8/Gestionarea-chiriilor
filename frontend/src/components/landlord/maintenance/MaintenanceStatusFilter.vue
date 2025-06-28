<script setup>
const props = defineProps({
  selectedStatus: {
    type: String,
    required: true
  },
  availableStatuses: {
    type: Array,
    default: () => []
  }
});

const emit = defineEmits(['update:selectedStatus']);

const statusMap = {
  PENDING: {
    label: 'Pending',
    bgColor: 'bg-yellow-100',
    textColor: 'text-yellow-800',
    icon: '<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />'
  },
  IN_PROGRESS: {
    label: 'In Progress',
    bgColor: 'bg-blue-100',
    textColor: 'text-blue-800',
    icon: '<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />'
  },
  CANCELLED: {
    label: 'Cancelled',
    bgColor: 'bg-red-100',
    textColor: 'text-red-800',
    icon: '<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />'
  },
  COMPLETED: {
    label: 'Completed',
    bgColor: 'bg-green-100',
    textColor: 'text-green-800',
    icon: '<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />'
  }
};

const formatStatus = (status) => {
  if (status === 'all') return 'All Requests';
  return statusMap[status]?.label || status.replace('_', ' ').toLowerCase().replace(/(^\w|\s\w)/g, l => l.toUpperCase());
};

const isStatusAvailable = (status) => {
  return status === 'all' || props.availableStatuses.includes(status);
};
</script>

<template>
  <div class="space-y-3">
    <div class="flex flex-wrap gap-3">
      <!-- Status Buttons for larger screens -->
      <button
          v-for="status in ['all', 'PENDING', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED']"
          :key="status"
          @click="emit('update:selectedStatus', status)"
          :class="[
          'hidden sm:flex items-center gap-2 px-4 py-2 rounded-lg border transition-all',
          selectedStatus === status ? 'border-blue-500 bg-blue-50 text-blue-700 shadow-sm' : 'border-gray-300 hover:bg-gray-50',
          !isStatusAvailable(status) ? 'opacity-50 cursor-not-allowed' : 'cursor-pointer'
        ]"
          :disabled="!isStatusAvailable(status)"
      >
        <!-- All Requests -->
        <template v-if="status === 'all'">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
          </svg>
          <span>All Requests</span>
        </template>

        <!-- Other statuses -->
        <template v-else>
          <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" v-html="statusMap[status].icon"></svg>
          <span>{{ formatStatus(status) }}</span>

        </template>
      </button>
    </div>

    <!-- Dropdown for mobile -->
    <div class="sm:hidden relative w-full">
      <div class="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none">
        <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 4a1 1 0 011-1h16a1 1 0 011 1v2.586a1 1 0 01-.293.707l-6.414 6.414a1 1 0 00-.293.707V17l-4 4v-6.586a1 1 0 00-.293-.707L3.293 7.293A1 1 0 013 6.586V4z" />
        </svg>
      </div>

      <select
          :value="selectedStatus"
          @change="$emit('update:selectedStatus', $event.target.value)"
          class="block w-full pl-10 pr-10 py-3 border border-gray-300 rounded-lg shadow-sm focus:ring-2 focus:ring-blue-500 focus:outline-none appearance-none bg-white"
      >
        <option value="all">All Requests</option>
        <option
            v-for="status in ['PENDING', 'IN_PROGRESS', 'CANCELLED', 'COMPLETED']"
            :key="status"
            :value="status"
            :disabled="!availableStatuses.includes(status)"
        >
          {{ formatStatus(status) }}
        </option>
      </select>

      <div class="pointer-events-none absolute inset-y-0 right-0 flex items-center px-2 text-gray-700">
        <svg class="h-5 w-5" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor">
          <path fill-rule="evenodd" d="M5.293 7.293a1 1 0 011.414 0L10 10.586l3.293-3.293a1 1 0 111.414 1.414l-4 4a1 1 0 01-1.414 0l-4-4a1 1 0 010-1.414z" clip-rule="evenodd" />
        </svg>
      </div>
    </div>
  </div>
</template>

<style scoped>
option:disabled {
  @apply text-gray-400 bg-gray-50;
}
</style>