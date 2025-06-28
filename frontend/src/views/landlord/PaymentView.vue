<script setup>
import {onMounted, ref} from 'vue'
import { usePaymentLandlordStore } from "@/stores/landlord/paymentStore.js";
import PaymentSummaryCard from '@/components/landlord/payment/PaymentSummaryCard.vue'
import PaymentTable from '@/components/landlord/payment/PaymentTable.vue'
import PaymentManualModal from '@/components/landlord/payment/PaymentManualModal.vue'
import PaymentLeaseFilter from '@/components/landlord/payment/PaymentLeaseFilter.vue'

const store = usePaymentLandlordStore()
const showManualModal = ref(false)
const newPayment = ref({
  leaseId: null,
  amount: 0,
  paymentDate: '',
  paymentMethod: 'BANK_TRANSFER'
})

const handleSubmitManualPayment = async (payment) => {
  await store.submitManualPayment(payment)
  showManualModal.value = false
}

onMounted(() => {
  store.loadPayments()
})
</script>

<template>
  <div class="max-w-6xl mx-auto p-6 space-y-8">
    <div class="bg-gradient-to-r from-blue-600 to-indigo-700 rounded-2xl shadow-lg mb-8">
      <div class="px-8 py-6">
        <div class="flex flex-col md:flex-row justify-between items-start md:items-center gap-4">
          <div class="flex items-center gap-4">
            <div class="bg-white/20 p-3 rounded-xl">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-8 w-8 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 9V7a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2m2 4h10a2 2 0 002-2v-6a2 2 0 00-2-2H9a2 2 0 00-2 2v6a2 2 0 002 2zm7-5a2 2 0 11-4 0 2 2 0 014 0z" />
              </svg>
            </div>
            <div>
              <h1 class="text-3xl font-bold text-white">Payments</h1>
              <p class="text-blue-100 mt-1">Manage and track all your property payments</p>
            </div>
          </div>

          <button
              @click="showManualModal = true"
              class="bg-white/20 hover:bg-white/30 text-white px-5 py-2.5 rounded-lg transition-colors flex items-center font-medium shadow-sm border border-white/20"
          >
            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
            </svg>
            Add Payment
          </button>
        </div>
      </div>
    </div>

    <div v-if="store.error" class="bg-red-50 border-l-4 border-red-500 text-red-700 p-4 rounded-md shadow-sm">
      <div class="flex items-center">
        <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
        </svg>
        <span>{{ store.error }}</span>
      </div>
    </div>

    <div class="bg-white rounded-xl shadow-md overflow-hidden">
      <PaymentSummaryCard :payments="store.filteredPayments" />
    </div>

    <div class="p-6 border-b border-gray-200">
      <div class="flex flex-col sm:flex-row justify-between items-start sm:items-center">
        <div class="flex items-center mb-4 sm:mb-0">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-gray-500 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 4a1 1 0 011-1h16a1 1 0 011 1v2.586a1 1 0 01-.293.707l-6.414 6.414a1 1 0 00-.293.707V17l-4 4v-6.586a1 1 0 00-.293-.707L3.293 7.293A1 1 0 013 6.586V4z" />
          </svg>
          <h2 class="text-xl font-semibold text-gray-800">Filter Payments</h2>
        </div>

        <PaymentLeaseFilter
            class="w-full sm:w-auto"
            :payments="store.payments"
            :leases="store.leases"
            v-model:selectedLeaseId="store.selectedLeaseId"
        />
      </div>
    </div>

    <div class="bg-white rounded-xl shadow-md overflow-hidden">
      <div class="p-6 border-b border-gray-200">
        <h2 class="text-xl font-semibold text-gray-800">Payment History</h2>
        <p class="text-gray-500 text-sm mt-1">View and manage all transaction records</p>
      </div>
      <PaymentTable
          :payments="store.filteredPayments"
          :leases="store.leases"
          :tenants="store.tenants"
          :properties="store.properties"
          :loading="store.loading"
      />
    </div>

    <PaymentManualModal
        v-if="showManualModal"
        :leases="store.leases"
        :tenants="store.tenants"
        :properties="store.properties"
        :newPayment="newPayment"
        @close="showManualModal = false"
        @cancel="showManualModal = false"
        @submit="handleSubmitManualPayment"
    />
  </div>
</template>