
<script setup>
import { ref, onMounted, computed } from 'vue'
import { fetchPropertyById } from '@/services/propertyService.js'
import { fetchUserById } from '@/services/userService.js'
import { formatDate, formatCurrencyCompact } from '@/utils/formatters.js'

const props = defineProps({
  lease: Object,
  nextPaymentDate: Date,
  daysUntilNextPayment: Number
})

const nextPaymentDate = computed(() => props.nextPaymentDate)
const daysUntilNextPayment = computed(() => props.daysUntilNextPayment)

const property = ref(null)
const owner = ref(null)

const calculateLeaseProgress = (startDate, endDate, currentDate = new Date()) => {
  const start = new Date(startDate).getTime()
  const end = new Date(endDate).getTime()
  const today = currentDate.getTime()

  if (isNaN(start) || isNaN(end)) return 0

  const totalDuration = end - start
  const elapsedTime = today - start
  const progress = (elapsedTime / totalDuration) * 100

  return Math.max(Math.min(progress, 100), 0)
}

const calculateDaysBetween = (startDate, endDate) => {
  const start = new Date(startDate)
  const end = new Date(endDate)

  if (isNaN(start.getTime()) || isNaN(end.getTime())) return 0

  const diffTime = Math.abs(end - start)
  return Math.ceil(diffTime / (1000 * 60 * 60 * 24))
}

const leaseProgress = computed(() => {
  if (!props.lease?.startDate || !props.lease?.endDate) return 0
  return calculateLeaseProgress(props.lease.startDate, props.lease.endDate)
})

const daysRemaining = computed(() => {
  if (!props.lease?.endDate) return 0
  const today = new Date()
  const endDate = new Date(props.lease.endDate)

  if (endDate < today) return 0

  return calculateDaysBetween(today, endDate)
})

onMounted(async () => {
  try {
    if (props.lease?.propertyId) {
      property.value = await fetchPropertyById(props.lease.propertyId)

      if (property.value?.ownerId) {
        owner.value = await fetchUserById(property.value.ownerId)
      }
    }
  } catch (err) {
    console.error('Error loading lease header details:', err)
  }
})

const statusClass = (status) => {
  switch (status) {
    case 'ACTIVE': return 'bg-green-400 bg-opacity-20 text-green-100 border border-green-400 border-opacity-30'
    case 'PENDING': return 'bg-yellow-400 bg-opacity-20 text-yellow-100 border border-yellow-400 border-opacity-30'
    case 'TERMINATED': return 'bg-red-400 bg-opacity-20 text-red-100 border border-red-400 border-opacity-30'
    default: return 'bg-gray-400 bg-opacity-20 text-gray-100 border border-gray-400 border-opacity-30'
  }
}

const statusDot = (status) => {
  switch (status) {
    case 'ACTIVE': return 'bg-green-400'
    case 'PENDING': return 'bg-yellow-400'
    case 'TERMINATED': return 'bg-red-400'
    default: return 'bg-gray-400'
  }
}
</script>

<template>
  <div class="text-white">
    <div class="flex flex-col sm:flex-row sm:justify-between sm:items-start gap-4 mb-4">
      <div class="flex-1">
        <h1 class="text-3xl font-bold text-white">{{ property?.name || 'Your Property' }}</h1>
        <p class="text-blue-100 mt-1">
          <span class="opacity-80">Owned by:</span> {{ owner?.firstName || 'N/A' }} {{ owner?.lastName || 'N/A' }}
        </p>
      </div>
      <span
          class="inline-flex items-center px-3 py-1.5 rounded-full text-sm font-medium shadow-sm"
          :class="statusClass(lease.status)"
      >
        <span :class="statusDot(lease.status)" class="h-2 w-2 mr-1.5 rounded-full"></span>
        {{ lease.status }}
      </span>
    </div>

    <div class="bg-white/10 backdrop-blur-sm rounded-xl p-6 mt-4">
      <div class="flex justify-between items-center mb-3">
        <h3 class="text-lg font-semibold text-white">Lease Duration</h3>
        <span class="text-sm text-blue-100">{{ Math.round(leaseProgress) }}% Complete</span>
      </div>

      <div class="w-full bg-white/20 rounded-full h-3 mb-4">
        <div
            class="bg-gradient-to-r from-green-400 to-blue-400 h-3 rounded-full transition-all duration-500 ease-out"
            :style="{ width: `${leaseProgress}%` }"
        ></div>
      </div>

      <div class="flex justify-between items-center text-sm">
        <div class="text-center">
          <p class="text-blue-200 opacity-80">Start Date</p>
          <p class="text-white font-medium">{{ formatDate(lease.startDate) }}</p>
        </div>

        <div class="text-center">
          <p class="text-blue-200 opacity-80">Days Remaining</p>
          <p class="text-white font-medium">{{ daysRemaining }} days</p>
        </div>

        <div class="text-center">
          <p class="text-blue-200 opacity-80">End Date</p>
          <p class="text-white font-medium">{{ formatDate(lease.endDate) }}</p>
        </div>
      </div>

      <div class="mt-4 pt-4 border-t border-white/20">
        <div class="flex justify-between items-center">
          <div>
            <p class="text-blue-200 opacity-80 text-sm">Next Payment Due</p>
            <p class="text-white font-medium">
              {{ nextPaymentDate ? formatDate(nextPaymentDate) : 'No payments due' }}
            </p>
          </div>
          <div class="text-right">
            <p class="text-blue-200 opacity-80 text-sm">Amount</p>
            <p class="text-white font-bold text-lg">{{ formatCurrencyCompact(lease.monthlyRent) }}</p>
          </div>
        </div>
        <div v-if="nextPaymentDate" class="mt-2 text-center">
          <p class="text-blue-200 opacity-80 text-sm">
            {{ daysUntilNextPayment === 0 ? 'Due today!' : `${daysUntilNextPayment} days remaining` }}
          </p>
        </div>
      </div>
    </div>
  </div>
</template>
