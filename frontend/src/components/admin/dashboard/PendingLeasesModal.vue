<script setup>
import {getPropertyNameById, getUserNameById} from "../../../utils/leaseNameUtils.js";

defineProps({
  visible: Boolean,
  leases: Array,
  loading: Boolean,
  users: Array,
  properties: Array
})

defineEmits(['update:visible', 'approve', 'reject'])

function formatDate(dateString) {
  if (!dateString) return 'N/A';
  const options = { year: 'numeric', month: 'short', day: 'numeric' };
  return new Date(dateString).toLocaleDateString(undefined, options);
}
</script>

<template>
  <Dialog
      :visible="visible"
      modal
      :header="false"
      class="w-full max-w-2xl rounded-xl shadow-lg overflow-hidden"
      @update:visible="$emit('update:visible', $event)"
  >
    <div class="bg-gradient-to-r from-green-500 to-green-600 px-6 py-4 flex justify-between items-center">
      <div class="flex items-center">
        <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-white mr-3" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
        </svg>
        <h2 class="text-xl font-semibold text-white">Pending Lease Approvals</h2>
      </div>
      <button
          @click="$emit('update:visible', false)"
          class="text-white/80 hover:text-white transition-colors focus:outline-none"
      >
        <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
        </svg>
      </button>
    </div>

    <div v-if="loading" class="flex flex-col items-center justify-center p-12 bg-white min-h-[400px]">
      <div class="w-12 h-12 border-4 border-gray-200 border-t-green-500 rounded-full animate-spin mb-4"></div>
      <p class="text-gray-600">Loading lease applications...</p>
    </div>

    <div v-else class="bg-white min-h-[400px]">
      <div v-if="leases.length > 0" class="p-6 space-y-5">
        <div
            v-for="lease in leases"
            :key="lease.leaseId"
            class="bg-white border border-green-200 rounded-lg overflow-hidden shadow-sm"
        >
          <div class="bg-green-50 px-5 py-3 border-b border-green-200 flex justify-between items-center">
            <div class="flex items-center">
              <span class="bg-green-100 text-green-800 text-xs font-medium px-2.5 py-1 rounded-full mr-2">
                New Application
              </span>
              <h3 class="font-semibold text-green-900">Lease #{{ lease.leaseId }}</h3>
            </div>
          </div>

          <div class="p-5">
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4 mb-5">
              <div class="flex items-start">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-gray-500 mt-0.5 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                </svg>
                <div>
                  <p class="text-sm font-medium text-gray-500">Tenant </p>
                  <p class="font-medium text-gray-900">{{ getUserNameById(lease.tenantId, users) }}</p>
                </div>
              </div>

              <div class="flex items-start">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-gray-500 mt-0.5 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6" />
                </svg>
                <div>
                  <p class="text-sm font-medium text-gray-500">Property </p>
                  <p class="font-medium text-gray-900">{{ getPropertyNameById(lease.propertyId, properties) }}</p>
                </div>
              </div>

              <div class="flex items-start">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-gray-500 mt-0.5 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                </svg>
                <div>
                  <p class="text-sm font-medium text-gray-500">Start Date</p>
                  <p class="font-medium text-gray-900">{{ formatDate(lease.startDate) }}</p>
                </div>
              </div>

              <div class="flex items-start">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-gray-500 mt-0.5 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
                <div>
                  <p class="text-sm font-medium text-gray-500">Monthly Rent</p>
                  <p class="font-medium text-gray-900">${{ lease.monthlyRent }}</p>
                </div>
              </div>
            </div>

            <div class="flex gap-3 justify-end mt-6">
              <button
                  @click="$emit('reject', lease.leaseId)"
                  class="px-4 py-2 bg-white hover:bg-gray-50 text-red-600 font-medium border border-red-600 rounded-lg transition-colors"
              >
                Reject
              </button>
              <button
                  @click="$emit('approve', lease.leaseId)"
                  class="px-4 py-2 bg-green-600 hover:bg-green-700 text-white font-medium rounded-lg transition-colors flex items-center"
              >
                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-1.5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
                </svg>
                Approve Lease
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- Enhanced Empty State -->
      <div v-else class="flex flex-col items-center justify-center px-8 py-16 bg-white text-center min-h-[400px]">
        <!-- Icon Container -->
        <div class="mx-auto w-24 h-24 bg-gradient-to-br from-green-100 to-green-200 rounded-full flex items-center justify-center mb-6 shadow-sm">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-12 w-12 text-green-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
          </svg>
        </div>

        <div class="max-w-sm">
          <h3 class="text-xl font-semibold text-gray-900 mb-3">Up to Date!</h3>
          <p class="text-gray-600 mb-6 leading-relaxed">
            No pending lease applications right now. New applications from prospective tenants will appear here for your approval.
          </p>

        </div>
      </div>
    </div>
  </Dialog>
</template>
