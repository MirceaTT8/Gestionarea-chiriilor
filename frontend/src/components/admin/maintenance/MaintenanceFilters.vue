<script setup>
const props = defineProps({
  filters: Object
})
const emit = defineEmits(['update:filters'])

const update = (key, value) => {
  emit('update:filters', { ...props.filters, [key]: value })
}
</script>

<template>
  <div class="flex flex-col sm:flex-row gap-4 mb-6 items-start sm:items-end">
    <div>
      <label class="block text-sm font-medium text-gray-700 mb-1">Status</label>
      <select
          class="w-full border border-gray-300 rounded px-3 py-2 text-sm shadow-sm"
          :value="filters.status"
          @change="update('status', $event.target.value)"
      >
        <option value="ALL">All</option>
        <option value="PENDING">Pending</option>
        <option value="IN_PROGRESS">In Progress</option>
        <option value="COMPLETED">Completed</option>
        <option value="CANCELLED">Cancelled</option>
      </select>
    </div>

    <div class="flex-1">
      <label class="block text-sm font-medium text-gray-700 mb-1">Search (Lease ID or Request ID)</label>
      <input
          type="text"
          class="w-full border border-gray-300 rounded px-3 py-2 text-sm shadow-sm"
          :value="filters.query"
          @input="update('query', $event.target.value)""
      />
    </div>
  </div>
</template>
