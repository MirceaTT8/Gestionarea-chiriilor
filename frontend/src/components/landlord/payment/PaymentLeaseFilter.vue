<script setup>
import { computed } from 'vue'

const props = defineProps({
  payments: Array,
  leases: Array,
  selectedLeaseId: Number
})

const emit = defineEmits(['update:selectedLeaseId'])

const leaseOptions = computed(() => {
  const leaseMap = new Map()
  props.leases.forEach(lease => {
    leaseMap.set(lease.leaseId, lease)
  })

  const seen = new Set()
  return props.payments
      .filter(p => !seen.has(p.leaseId) && seen.add(p.leaseId))
      .map(p => {
        const lease = leaseMap.get(p.leaseId)
        const label = lease && lease.property && lease.property.name
            ? lease.property.name
            : `Lease #${p.leaseId}`
        return { value: p.leaseId, label }
      })
})
</script>

<template>
  <div>
    <div class="flex items-center justify-end">
      <div class="relative w-full sm:w-64">
        <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
          </svg>
        </div>
        <select
            class="block appearance-none w-full bg-white border border-gray-300 hover:border-gray-400 px-4 py-2 pl-10 pr-8 rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-green-500 focus:border-green-500 transition-colors"
            :value="selectedLeaseId === null ? '' : selectedLeaseId"
            @change="e => emit('update:selectedLeaseId', e.target.value ? parseInt(e.target.value) : null)"
        >
          <option value="">All Properties</option>
          <option
              v-for="option in leaseOptions"
              :key="option.value"
              :value="option.value"
          >
            {{ option.label }}
          </option>
        </select>
        <div class="pointer-events-none absolute inset-y-0 right-0 flex items-center px-2 text-gray-700">
          <svg class="fill-current h-4 w-4" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20">
            <path fill-rule="evenodd" d="M5.293 7.293a1 1 0 011.414 0L10 10.586l3.293-3.293a1 1 0 111.414 1.414l-4 4a1 1 0 01-1.414 0l-4-4a1 1 0 010-1.414z" clip-rule="evenodd" />
          </svg>
        </div>
      </div>
    </div>
  </div>
</template>