<script setup>
import { computed, watch, ref } from 'vue'
import {
  getPropertyNameByLeaseId,
  getTenantNameByLeaseId
} from '@/utils/leaseNameUtils.js'
import { isPaymentMadeThisCycle } from '@/services/paymentService.js'

const props = defineProps({
  leases: Array,
  newPayment: Object,
  properties: Array,
  tenants: Array
})

const emit = defineEmits(['submit', 'cancel', 'close'])

const isLeaseActive = ref(true)
const alreadyPaid = ref(false)
const errorMsg = ref('')

const availableLeases = computed(() =>
    props.leases?.filter(l => l.status === 'ACTIVE' && l.terminationStatus !== 'APPROVED') || []
)

const hasAvailableLeases = computed(() => availableLeases.value.length > 0)

const isLeaseSelected = computed(() => !!props.newPayment.leaseId)

watch(
    () => props.newPayment.leaseId,
    async (leaseId) => {
      if (!leaseId) return

      const selectedLease = props.leases.find(lease => lease.leaseId === leaseId)

      if (!selectedLease) return

      isLeaseActive.value = selectedLease.status === 'ACTIVE'

      try {
        alreadyPaid.value = await isPaymentMadeThisCycle(leaseId)
      } catch (error) {
        alreadyPaid.value = false
        console.error('Failed to check payment status:', error)
      }

      if (!isLeaseActive.value) {
        errorMsg.value = 'This lease is not active.'
      } else if (alreadyPaid.value) {
        errorMsg.value = 'Payment has already been made for this billing cycle.'
      } else {
        errorMsg.value = ''
        props.newPayment.amount = selectedLease.monthlyRent
      }
    }
)

const isFormValid = computed(() =>
    props.newPayment.leaseId &&
    props.newPayment.amount > 0 &&
    props.newPayment.paymentDate &&
    props.newPayment.paymentMethod &&
    isLeaseActive.value &&
    !alreadyPaid.value
)

const resolvePropertyName = (leaseId) =>
    getPropertyNameByLeaseId(leaseId, props.leases, props.properties)

const resolveTenantName = (leaseId) =>
    getTenantNameByLeaseId(leaseId, props.leases, props.tenants)
</script>

<template>
  <div class="fixed inset-0 bg-black bg-opacity-60 flex items-center justify-center p-4 z-50 backdrop-blur-sm">
    <div class="bg-white rounded-xl shadow-xl max-w-md w-full transform transition-all">
      <!-- Modal Header -->
      <div class="bg-gray-50 px-6 py-4 rounded-t-xl border-b border-gray-200 flex justify-between items-center">
        <div>
          <h2 class="text-xl font-semibold text-gray-800">Record Manual Payment</h2>
          <p class="text-gray-500 text-sm mt-1">Add a payment record to the system</p>
        </div>
        <button
            @click="emit('close')"
            class="text-gray-400 hover:text-gray-600 focus:outline-none"
            aria-label="Close"
        >
          <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
          </svg>
        </button>
      </div>

      <!-- Modal Body -->
      <div class="p-6">
        <!-- No Leases Available Message -->
        <div v-if="!hasAvailableLeases" class="text-center py-8">
          <div class="inline-flex items-center justify-center w-16 h-16 bg-gray-100 rounded-full mb-4">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-8 w-8 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
            </svg>
          </div>
          <h3 class="text-lg font-medium text-gray-900 mb-2">No Active Leases Available</h3>
          <p class="text-gray-500 text-sm mb-6">There are currently no active leases available for payment recording.</p>
          <button
              @click="emit('close')"
              class="px-4 py-2 bg-gray-100 hover:bg-gray-200 text-gray-700 font-medium rounded-lg transition-colors"
          >
            Close
          </button>
        </div>

        <!-- Form (only shown when leases are available) -->
        <form v-else @submit.prevent="emit('submit', props.newPayment)">
          <!-- Lease Selection -->
          <div class="mb-5">
            <label class="block text-sm font-medium text-gray-700 mb-2">Lease</label>
            <div class="relative">
              <select
                  v-model="props.newPayment.leaseId"
                  required
                  class="block w-full pl-3 pr-10 py-2.5 border border-gray-300 rounded-lg shadow-sm focus:ring-2 focus:ring-green-500 focus:border-green-500 text-gray-700 appearance-none"
              >
                <option value="" disabled selected>Select a lease</option>
                <option
                    v-for="lease in availableLeases"
                    :key="lease.leaseId"
                    :value="lease.leaseId"
                >
                  {{ resolvePropertyName(lease.leaseId) }} ({{ resolveTenantName(lease.leaseId) }})
                </option>
              </select>
              <div class="pointer-events-none absolute inset-y-0 right-0 flex items-center px-2 text-gray-700">
                <svg class="h-5 w-5" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" aria-hidden="true">
                  <path fill-rule="evenodd" d="M5.293 7.293a1 1 0 011.414 0L10 10.586l3.293-3.293a1 1 0 111.414 1.414l-4 4a1 1 0 01-1.414 0l-4-4a1 1 0 010-1.414z" clip-rule="evenodd" />
                </svg>
              </div>
            </div>
          </div>

          <!-- Error Message -->
          <div v-if="errorMsg" class="mb-5 p-3 bg-red-50 text-red-600 rounded-lg border-l-4 border-red-500 flex items-start">
            <svg class="h-5 w-5 mr-2 mt-0.5 flex-shrink-0" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor">
              <path fill-rule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7 4a1 1 0 11-2 0 1 1 0 012 0zm-1-9a1 1 0 00-1 1v4a1 1 0 102 0V6a1 1 0 00-1-1z" clip-rule="evenodd" />
            </svg>
            <span class="text-sm">{{ errorMsg }}</span>
          </div>

          <!-- Amount Field -->
          <div class="mb-5">
            <label class="block text-sm font-medium text-gray-700 mb-2">Amount</label>
            <div class="relative">
              <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                <span class="text-gray-500">$</span>
              </div>
              <input
                  v-model.number="props.newPayment.amount"
                  type="number"
                  min="0"
                  step="0.01"
                  required
                  readonly
                  :disabled="!isLeaseSelected"
                  :class="[
                    'pl-7 block w-full px-3 py-2.5 border rounded-lg shadow-sm',
                    isLeaseSelected
                      ? 'border-gray-300 bg-gray-50 text-gray-700 cursor-not-allowed'
                      : 'border-gray-200 bg-gray-100 text-gray-400 cursor-not-allowed'
                  ]"
              />
            </div>
            <p class="mt-1 text-xs text-gray-500">
              {{ isLeaseSelected ? 'Automatically set based on the selected lease' : 'Select a lease to set the amount' }}
            </p>
          </div>

          <!-- Payment Date -->
          <div class="mb-5">
            <label class="block text-sm font-medium text-gray-700 mb-2">Payment Date</label>
            <div class="relative">
              <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                </svg>
              </div>
              <input
                  v-model="props.newPayment.paymentDate"
                  type="date"
                  required
                  :disabled="!isLeaseSelected"
                  :class="[
                    'pl-10 block w-full px-3 py-2.5 border rounded-lg shadow-sm',
                    isLeaseSelected
                      ? 'border-gray-300 focus:ring-2 focus:ring-green-500 focus:border-green-500 text-gray-700'
                      : 'border-gray-200 bg-gray-100 text-gray-400 cursor-not-allowed'
                  ]"
              />
            </div>
            <p v-if="!isLeaseSelected" class="mt-1 text-xs text-gray-500">Select a lease to enable this field</p>
          </div>

          <!-- Payment Method -->
          <div class="mb-6">
            <label class="block text-sm font-medium text-gray-700 mb-2">Payment Method</label>
            <div class="relative">
              <select
                  v-model="props.newPayment.paymentMethod"
                  required
                  :disabled="!isLeaseSelected"
                  :class="[
                    'block w-full pl-10 pr-10 py-2.5 border rounded-lg shadow-sm appearance-none',
                    isLeaseSelected
                      ? 'border-gray-300 focus:ring-2 focus:ring-green-500 focus:border-green-500 text-gray-700'
                      : 'border-gray-200 bg-gray-100 text-gray-400 cursor-not-allowed'
                  ]"
              >
                <option value="" disabled selected>{{ isLeaseSelected ? 'Select payment method' : 'Select a lease first' }}</option>
                <option v-if="isLeaseSelected" value="BANK_TRANSFER">Bank Transfer</option>
                <option v-if="isLeaseSelected" value="CASH">Cash</option>
              </select>
              <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 9V7a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2m2 4h10a2 2 0 002-2v-6a2 2 0 00-2-2H9a2 2 0 00-2 2v6a2 2 0 002 2z" />
                </svg>
              </div>
              <div class="pointer-events-none absolute inset-y-0 right-0 flex items-center px-2 text-gray-700">
                <svg class="h-5 w-5" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" aria-hidden="true">
                  <path fill-rule="evenodd" d="M5.293 7.293a1 1 0 011.414 0L10 10.586l3.293-3.293a1 1 0 111.414 1.414l-4 4a1 1 0 01-1.414 0l-4-4a1 1 0 010-1.414z" clip-rule="evenodd" />
                </svg>
              </div>
            </div>
            <p v-if="!isLeaseSelected" class="mt-1 text-xs text-gray-500">Select a lease to enable this field</p>
          </div>

          <!-- Action Buttons -->
          <div class="flex justify-end gap-3 pt-2">
            <button
                type="button"
                @click="emit('cancel')"
                class="px-4 py-2.5 bg-white border border-gray-300 hover:bg-gray-50 text-gray-700 font-medium rounded-lg transition-colors shadow-sm"
            >
              Cancel
            </button>
            <button
                type="submit"
                :disabled="!isFormValid"
                class="px-5 py-2.5 bg-green-600 hover:bg-green-700 text-white font-medium rounded-lg transition-colors shadow-sm disabled:opacity-50 disabled:cursor-not-allowed flex items-center"
            >
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-1.5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
              </svg>
              Record Payment
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>