<script setup>
import { ref } from 'vue';
import LogTableRow from './LogTableRow.vue';

defineProps({
  logs: {
    type: Array,
    required: true
  }
});

const columns = ref([
  { key: 'createdAt', label: 'Timestamp' },
  { key: 'userId', label: 'User ID' },
  { key: 'actionType', label: 'Action' },
  { key: 'entityType', label: 'Entity' },
  { key: 'entityId', label: 'Entity ID' },
  { key: 'details', label: 'Details' }
]);
</script>

<template>
  <div class="overflow-x-auto">
    <table class="min-w-full table-auto">
      <thead>
      <tr class="bg-gray-50 border-b border-gray-200">
        <th
            v-for="column in columns"
            :key="column.key"
            @click="$emit('sort', column.key)"
            class="px-6 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider cursor-pointer transition-colors hover:bg-gray-100"
        >
          <div class="flex items-center space-x-1">
            <span>{{ column.label }}</span>
            <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 9l4-4 4 4m0 6l-4 4-4-4" />
            </svg>
          </div>
        </th>
      </tr>
      </thead>
      <tbody class="bg-white divide-y divide-gray-100">
      <template v-if="logs.length > 0">
        <LogTableRow v-for="log in logs" :key="log.id" :log="log" />
      </template>
      <tr v-else>
        <td colspan="6" class="px-6 py-10 text-center text-gray-500">
          <div class="flex flex-col items-center justify-center">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-12 w-12 text-gray-300 mb-3" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
            </svg>
            <p class="text-sm font-medium">No log entries found</p>
            <p class="text-xs mt-1">Try adjusting your filters or search criteria</p>
          </div>
        </td>
      </tr>
      </tbody>
    </table>
  </div>
</template>


<style scoped>
table {
  border-collapse: separate;
  border-spacing: 0;
}

thead th:first-child {
  border-top-left-radius: 0.5rem;
}

thead th:last-child {
  border-top-right-radius: 0.5rem;
}

tbody tr:last-child td:first-child {
  border-bottom-left-radius: 0.5rem;
}

tbody tr:last-child td:last-child {
  border-bottom-right-radius: 0.5rem;
}
</style>