<script setup>
import { useRouter } from 'vue-router'
import { formatDate, formatCurrencyCompact } from '@/utils/formatters.js'
import { ref } from 'vue'

const props = defineProps({
  payments: Array
})

const router = useRouter()
const firstThree = props.payments.slice(0, 3)
const extraPayments = props.payments.slice(3)
const showAllPayments = ref(false)

const getStatusClasses = (status) => {
  switch(status.toLowerCase()) {
    case 'completed':
      return 'bg-green-100 text-green-800 border border-green-200';
    case 'pending':
      return 'bg-yellow-100 text-yellow-800 border border-yellow-200';
    case 'failed':
      return 'bg-red-100 text-red-800 border border-red-200';
    case 'refunded':
      return 'bg-purple-100 text-purple-800 border border-purple-200';
    default:
      return 'bg-gray-100 text-gray-800 border border-gray-200';
  }
};

const getStatusDotClasses = (status) => {
  switch(status.toLowerCase()) {
    case 'completed':
      return 'bg-green-500';
    case 'pending':
      return 'bg-yellow-500';
    case 'failed':
      return 'bg-red-500';
    case 'refunded':
      return 'bg-purple-500';
    default:
      return 'bg-gray-500';
  }
};

const getPaymentMethodIcon = (method) => {
  switch(method) {
    case 'CREDIT_CARD':
      return `<svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 text-indigo-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 10h18M7 15h1m4 0h1m-7 4h12a3 3 0 003-3V8a3 3 0 00-3-3H6a3 3 0 00-3 3v8a3 3 0 003 3z" />
              </svg>`;
    case 'BANK_TRANSFER':
      return `<svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 text-blue-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 14v3m4-3v3m4-3v3M3 21h18M3 10h18M3 7l9-4 9 4M4 10h16v11H4V10z" />
              </svg>`;
    case 'STRIPE':
      return `<svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 text-purple-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 10h18M7 15h1m4 0h1m-7 4h12a3 3 0 003-3V8a3 3 0 00-3-3H6a3 3 0 00-3 3v8a3 3 0 003 3z" />
              </svg>`;
    default:
      return `<svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 text-gray-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>`;
  }
};
</script>

<template>
  <div class="bg-white rounded-xl shadow-sm overflow-hidden border border-gray-100">
    <!-- Header -->
    <div class="px-6 py-4 bg-gray-50 border-b border-gray-100 flex justify-between items-center">
      <div class="flex items-center">
        <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-gray-500 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
        </svg>
        <h2 class="font-semibold text-gray-800">Payment History</h2>
      </div>

      <div class="text-sm text-gray-500">
        {{ payments.length }} payment{{ payments.length !== 1 ? 's' : '' }} total
      </div>
    </div>

    <!-- Empty State -->
    <div v-if="payments.length === 0" class="py-16 px-4">
      <div class="text-center">
        <svg xmlns="http://www.w3.org/2000/svg" class="mx-auto h-12 w-12 text-gray-300" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1" d="M17 9V7a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2m2 4h10a2 2 0 002-2v-6a2 2 0 00-2-2H9a2 2 0 00-2 2v6a2 2 0 002 2zm7-5a2 2 0 11-4 0 2 2 0 014 0z" />
        </svg>
        <h3 class="mt-2 text-sm font-medium text-gray-900">No payment history</h3>
        <p class="mt-1 text-sm text-gray-500">Your payment history will be displayed here when available.</p>
        <div class="mt-6">
          <button
              @click="router.push('/tenant/payments/make')"
              class="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
          >
            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
            </svg>
            Make a Payment
          </button>
        </div>
      </div>
    </div>

    <!-- Table Content -->
    <div v-else>
      <!-- Table Header -->
      <div class="hidden md:grid md:grid-cols-5 gap-2 text-xs font-medium text-gray-500 uppercase tracking-wider px-6 py-3 bg-gray-50 border-b border-gray-100">
        <div>Date</div>
        <div>Amount</div>
        <div>Method</div>
        <div>Status</div>
      </div>

      <!-- Main Three Payments -->
      <div>
        <div
            v-for="payment in firstThree"
            :key="'p1-' + payment.paymentId"
            class="border-b border-gray-100 last:border-0 px-6 py-4 hover:bg-gray-50"
        >
          <!-- Mobile View -->
          <div class="md:hidden space-y-3">
            <div class="flex justify-between items-center">
              <span class="text-sm font-medium text-gray-900">{{ formatDate(payment.paymentDate) }}</span>
              <span
                  class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium"
                  :class="getStatusClasses(payment.status)"
              >
                <span :class="getStatusDotClasses(payment.status)" class="w-1.5 h-1.5 mr-1.5 rounded-full"></span>
                {{ payment.status }}
              </span>
            </div>
            <div class="flex justify-between items-center">
              <span class="text-base font-semibold text-gray-900">{{ formatCurrencyCompact(payment.amount) }}</span>
            </div>
            <div class="flex items-center">
              <span v-html="getPaymentMethodIcon(payment.paymentMethod)" class="mr-1.5"></span>
              <span class="text-xs text-gray-500 capitalize">{{ payment.paymentMethod.replace('_', ' ').toLowerCase() }}</span>
            </div>
          </div>

          <!-- Desktop View -->
          <div class="hidden md:grid md:grid-cols-5 gap-2 items-center">
            <div class="text-sm text-gray-900">{{ formatDate(payment.paymentDate) }}</div>
            <div class="text-sm font-medium text-gray-900">{{ formatCurrencyCompact(payment.amount) }}</div>
            <div class="text-sm text-gray-700 flex items-center">
              <span v-html="getPaymentMethodIcon(payment.paymentMethod)" class="mr-1.5"></span>
              <span class="capitalize">{{ payment.paymentMethod.replace('_', ' ').toLowerCase() }}</span>
            </div>
            <div>
              <span
                  class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium"
                  :class="getStatusClasses(payment.status)"
              >
                <span :class="getStatusDotClasses(payment.status)" class="w-1.5 h-1.5 mr-1.5 rounded-full"></span>
                {{ payment.status }}
              </span>
            </div>
          </div>
        </div>
      </div>

      <!-- Extra Payments (Show All Toggle) -->
      <div v-if="extraPayments.length > 0">
        <div
            v-if="showAllPayments"
            v-for="payment in extraPayments"
            :key="'p2-' + payment.paymentId"
            class="border-b border-gray-100 last:border-0 px-6 py-4 hover:bg-gray-50"
        >
          <!-- Mobile View -->
          <div class="md:hidden space-y-3">
            <div class="flex justify-between items-center">
              <span class="text-sm font-medium text-gray-900">{{ formatDate(payment.paymentDate) }}</span>
              <span
                  class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium"
                  :class="getStatusClasses(payment.status)"
              >
                <span :class="getStatusDotClasses(payment.status)" class="w-1.5 h-1.5 mr-1.5 rounded-full"></span>
                {{ payment.status }}
              </span>
            </div>
            <div class="flex justify-between items-center">
              <span class="text-base font-semibold text-gray-900">{{ formatCurrencyCompact(payment.amount) }}</span>
            </div>
            <div class="flex items-center">
              <span v-html="getPaymentMethodIcon(payment.paymentMethod)" class="mr-1.5"></span>
              <span class="text-xs text-gray-500 capitalize">{{ payment.paymentMethod.replace('_', ' ').toLowerCase() }}</span>
            </div>
          </div>

          <!-- Desktop View -->
          <div class="hidden md:grid md:grid-cols-5 gap-2 items-center">
            <div class="text-sm text-gray-900">{{ formatDate(payment.paymentDate) }}</div>
            <div class="text-sm font-medium text-gray-900">{{ formatCurrencyCompact(payment.amount) }}</div>
            <div class="text-sm text-gray-700 flex items-center">
              <span v-html="getPaymentMethodIcon(payment.paymentMethod)" class="mr-1.5"></span>
              <span class="capitalize">{{ payment.paymentMethod.replace('_', ' ').toLowerCase() }}</span>
            </div>
            <div>
              <span
                  class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium"
                  :class="getStatusClasses(payment.status)"
              >
                <span :class="getStatusDotClasses(payment.status)" class="w-1.5 h-1.5 mr-1.5 rounded-full"></span>
                {{ payment.status }}
              </span>
            </div>
            <div class="text-right">
            </div>
          </div>
        </div>

        <!-- View More/Less Button -->
        <div class="border-t border-gray-100 px-6 py-4 bg-gray-50 text-center">
          <button
              @click="showAllPayments = !showAllPayments"
              class="inline-flex items-center px-4 py-2 border border-gray-300 shadow-sm text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50"
          >
            <svg
                xmlns="http://www.w3.org/2000/svg"
                class="h-4 w-4 mr-1.5"
                :class="{ 'transform rotate-180': showAllPayments }"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
            >
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
            </svg>
            {{ showAllPayments ? 'Show Less' : `Show ${extraPayments.length} More` }}
          </button>
        </div>
      </div>

      <!-- View All Payments Page Button -->
      <div v-if="!showAllPayments && extraPayments.length > 3" class="border-t border-gray-100 px-6 py-4 bg-gray-50 text-center">
        <button
            @click="router.push('/tenant/payments')"
            class="inline-flex items-center text-sm font-medium text-indigo-600 hover:text-indigo-500"
        >
          View all payments in payment center
          <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 ml-1" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M14 5l7 7m0 0l-7 7m7-7H3" />
          </svg>
        </button>
      </div>
    </div>
  </div>
</template>