<script setup>
import { computed } from 'vue'
import { formatDate } from '@/utils/formatters.js'

const props = defineProps({
  lease: {
    type: Object,
    required: true
  }
})

const dueDay = new Date(props.lease.startDate).getDate()
const daySuffix = dueDay === 1 ? 'st' : dueDay === 2 ? 'nd' : dueDay === 3 ? 'rd' : 'th'

console.log(props.lease)

const rentClassMap = {
  CURRENT: 'text-green-600 font-semibold',
  OVERDUE: 'text-red-500 font-semibold',
  UNPAID: 'text-yellow-500 font-semibold',
  LATE: 'text-orange-500 font-semibold'
}
</script>

<template>
  <section>
    <div class="grid md:grid-cols-2 lg:grid-cols-4 gap-6">
      <div class="bg-white shadow rounded p-5">
        <h3 class="font-semibold text-gray-800 mb-2">Duration</h3>
        <p class="text-gray-700">
          {{ formatDate(lease.startDate) }} -
          {{ lease.endDate ? formatDate(lease.endDate) : 'Undetermined' }}
        </p>
      </div>

      <div class="bg-white shadow rounded p-5">
        <h3 class="font-semibold text-gray-800 mb-2">Rent Information</h3>
        <p><strong>$</strong>{{ lease.monthlyRent.toFixed(2) }} per month</p>
        <p>Due on the {{ dueDay }}{{ daySuffix }} of each month</p>
        <p :class="rentClassMap[lease.rentStatus]">
          Status: {{ lease.rentStatus?.toLowerCase() || 'unknown' }}
        </p>
        <p v-if="lease.latestPaymentDate">Last Paid On: {{ formatDate(lease.latestPaymentDate) }}</p>
      </div>
    </div>
  </section>
</template>
