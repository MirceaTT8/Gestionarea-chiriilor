<script setup>
import { computed } from 'vue'
import { getPropertyNameByLeaseId, getTenantNameByLeaseId } from '@/utils/leaseNameUtils.js'
import { formatPaymentMethod } from '@/utils/paymentUtils.js'
import { formatDateShort, formatCurrencyPrecise, formatCurrencyCompact } from '@/utils/formatters.js'

const props = defineProps({
  payments: Array,
  leases: Array,
  tenants: Array,
  properties: Array,
  loading: Boolean
})

const getTenantName = (leaseId) =>
    getTenantNameByLeaseId(leaseId, props.leases, props.tenants)

const getPropertyName = (leaseId) =>
    getPropertyNameByLeaseId(leaseId, props.leases, props.properties)

const getStatusClasses = (status) => {
  switch(status?.toLowerCase()) {
    case 'completed':
      return 'bg-green-100 text-green-800 border border-green-200'
    case 'pending':
      return 'bg-yellow-100 text-yellow-800 border border-yellow-200'
    case 'failed':
      return 'bg-red-100 text-red-800 border border-red-200'
    case 'refunded':
      return 'bg-purple-100 text-purple-800 border border-purple-200'
    default:
      return 'bg-gray-100 text-gray-800 border border-gray-200'
  }
}

const getStatusDot = (status) => {
  switch(status?.toLowerCase()) {
    case 'completed':
      return 'bg-green-500'
    case 'pending':
      return 'bg-yellow-500'
    case 'failed':
      return 'bg-red-500'
    case 'refunded':
      return 'bg-purple-500'
    default:
      return 'bg-gray-500'
  }
}
</script>

<template>
  <div class="bg-white shadow-lg rounded-xl border border-gray-200 overflow-hidden">
    <table class="min-w-full table-auto">
      <thead class="bg-gradient-to-r from-gray-50 to-gray-100 border-b border-gray-200">
      <tr>
<!--        <th class="px-6 py-4 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">Payment ID</th>-->
        <th class="px-6 py-4 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">Property</th>
        <th class="px-6 py-4 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">Tenant</th>
        <th class="px-6 py-4 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">Date</th>
        <th class="px-6 py-4 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">Amount</th>
        <th class="px-6 py-4 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">Method</th>
        <th class="px-6 py-4 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">Status</th>
      </tr>
      </thead>
      <tbody class="bg-white divide-y divide-gray-100">
      <tr v-if="loading" class="text-center text-gray-500">
        <td colspan="7" class="px-6 py-12 text-sm">
          <div class="flex items-center justify-center space-x-2">
            <div class="animate-spin rounded-full h-6 w-6 border-b-2 border-indigo-600"></div>
            <span>Loading payments...</span>
          </div>
        </td>
      </tr>

      <tr v-else-if="!payments?.length" class="text-center text-gray-500">
        <td colspan="7" class="px-6 py-12">
          <div class="text-center">
            <svg class="w-12 h-12 text-gray-400 mx-auto mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"/>
            </svg>
            <p class="text-lg font-medium text-gray-900 mb-2">No payments found</p>
            <p class="text-gray-600">There are no payment records to display.</p>
          </div>
        </td>
      </tr>

      <tr
          v-else
          v-for="payment in payments"
          :key="payment.paymentId"
          class="hover:bg-gray-50 transition-colors duration-200"
      >
        <td class="px-6 py-4 whitespace-nowrap">
          <div class="text-sm font-medium text-gray-900">
            {{ getPropertyName(payment.leaseId) || 'Unknown Property' }}
          </div>
        </td>
        <td class="px-6 py-4 whitespace-nowrap">
          <div class="text-sm text-gray-900">
            {{ getTenantName(payment.leaseId) || 'Unknown Tenant' }}
          </div>
        </td>
        <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-600">
          {{ formatDateShort(payment.paymentDate) }}
        </td>
        <td class="px-6 py-4 whitespace-nowrap">
          <div class="text-sm font-semibold text-gray-900">
            {{ formatCurrencyCompact(payment.amount) }}
          </div>
        </td>
        <td class="px-6 py-4 whitespace-nowrap">
          <div class="flex items-center text-sm text-gray-600">
            <span class="capitalize">{{ formatPaymentMethod(payment.paymentMethod) }}</span>
          </div>
        </td>
        <td class="px-6 py-4 whitespace-nowrap">
            <span class="inline-flex items-center px-2.5 py-1 rounded-full text-xs font-medium" :class="getStatusClasses(payment.status)">
              <span class="w-1.5 h-1.5 mr-1.5 rounded-full" :class="getStatusDot(payment.status)"></span>
              {{ payment.status || 'Unknown' }}
            </span>
        </td>
      </tr>
      </tbody>
    </table>

    <div v-if="payments?.length" class="bg-gray-50 px-6 py-3 border-t border-gray-200">
      <div class="flex items-center justify-between">
        <div class="text-sm text-gray-500">
          Showing {{ payments.length }} payment{{ payments.length !== 1 ? 's' : '' }}
        </div>
        <div class="text-sm text-gray-900 font-medium">
          Total: {{ formatCurrencyCompact(payments.reduce((sum, p) => sum + (p.amount || 0), 0)) }}
        </div>
      </div>
    </div>
  </div>
</template>