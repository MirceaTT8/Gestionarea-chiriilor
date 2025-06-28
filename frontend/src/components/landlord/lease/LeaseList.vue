<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import {
  getPropertyNameByLeaseId,
  getTenantNameByLeaseId
} from '@/utils/leaseNameUtils';

import { formatDate } from '@/utils/formatters.js'
import { requestLeaseTermination } from "@/services/leaseService.js";
import LeaseTenantReviewModal from "@/components/landlord/lease/LeaseTenantReviewModal.vue";

const router = useRouter()

const props = defineProps({
  leases: {
    type: Array,
    required: true
  },
  statusColors: {
    type: Object,
    required: true
  },
  properties: {
    type: Array,
    required: true
  },
  tenants: {
    type: Array,
    required: true
  },
  reviewedLeases: {
    type: Array,
    default: () => []
  }
});

const emit = defineEmits(['terminate', 'review-submitted']);

const showReviewModal = ref(false)
const selectedLease = ref(null)

const isLeaseReviewed = (lease) => {
  return props.reviewedLeases.includes(lease.leaseId)
}

const handleTerminate = async (leaseId) => {
  try {
    await requestLeaseTermination(leaseId);

    const lease = props.leases.find(l => l.leaseId === leaseId);
    if (lease) {
      lease.terminationStatus = 'PENDING';
    }

    emit('terminate', leaseId);
  } catch (error) {
    console.error('Termination request failed:', error);
    alert('Failed to request lease termination.');
  }
};

const openReviewModal = (lease) => {
  selectedLease.value = lease
  showReviewModal.value = true
}

const closeReviewModal = () => {
  showReviewModal.value = false
  selectedLease.value = null
}

const handleReviewSuccess = () => {
  emit('review-submitted')
  closeReviewModal()
}

const getSelectedTenant = () => {
  if (!selectedLease.value) return null
  return props.tenants.find(t => t.userId === selectedLease.value.tenantId)
}

const getSelectedProperty = () => {
  if (!selectedLease.value) return null
  return props.properties.find(p => p.propertyId === selectedLease.value.propertyId)
}

const canReviewLease = (lease) => {
  return lease.status === 'TERMINATED' ||
      lease.terminationStatus === 'APPROVED' ||
      (lease.endDate && new Date(lease.endDate) < new Date())
}

const navigateToPropertyDetails = (lease) => {
  const propertyId = lease.propertyId
  if (propertyId) {
    router.push(`/landlord/property/${propertyId}`)
  }
}

const navigateToTenantProfile = (lease) => {
  const tenantId = lease.tenantId
  if (tenantId) {
    router.push(`/profile/${tenantId}`)
  }
}
</script>

<template>
  <div class="space-y-8">
    <div
        v-for="lease in leases"
        :key="lease.leaseId"
        class="bg-white rounded-xl shadow-md hover:shadow-lg transition-shadow duration-300 overflow-hidden border border-gray-100"
    >
      <!-- Header Section with Gradient Background -->
      <div class="bg-gradient-to-r from-blue-50 to-indigo-50 p-6">
        <div class="flex justify-between items-center">
          <div class="flex items-center gap-3">
            <div class="bg-blue-600 text-white rounded-full w-10 h-10 flex items-center justify-center">
              <span class="font-bold">{{ lease.leaseId }}</span>
            </div>
            <h3 class="text-lg font-semibold text-gray-800">
              Lease Agreement
              <span
                  :class="[statusColors[lease.status].bg, statusColors[lease.status].text]"
                  class="ml-2 px-3 py-1 rounded-full text-xs font-medium capitalize inline-flex items-center"
              >
                <span class="w-2 h-2 rounded-full bg-current mr-1"></span>
                {{ lease.status.toLowerCase() }}
              </span>
            </h3>
          </div>
          <div class="text-right">
            <span class="text-sm text-gray-500">Monthly Rent</span>
            <div class="text-xl font-bold text-blue-700">${{ lease.monthlyRent }}</div>
          </div>
        </div>
      </div>

      <!-- Content Section -->
      <div class="p-6">
        <div class="grid grid-cols-1 md:grid-cols-3 gap-6 mb-6">
          <!-- Property Card -->
          <div class="bg-gray-50 rounded-lg p-4 transition-all hover:bg-blue-50">
            <span class="block text-sm text-gray-500 mb-2">Property</span>
            <div class="flex items-center">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-blue-600 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6" />
              </svg>
              <button
                  @click="navigateToPropertyDetails(lease)"
                  class="text-blue-600 hover:text-blue-800 hover:underline font-medium transition-colors cursor-pointer"
              >
                {{ getPropertyNameByLeaseId(lease.leaseId, props.leases, props.properties) }}
              </button>
            </div>
          </div>

          <!-- Tenant Card -->
          <div class="bg-gray-50 rounded-lg p-4 transition-all hover:bg-blue-50">
            <span class="block text-sm text-gray-500 mb-2">Tenant</span>
            <div class="flex items-center">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-blue-600 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
              </svg>
              <button
                  @click="navigateToTenantProfile(lease)"
                  class="text-blue-600 hover:text-blue-800 hover:underline font-medium transition-colors cursor-pointer"
              >
                {{ getTenantNameByLeaseId(lease.leaseId, props.leases, props.tenants) }}
              </button>
            </div>
          </div>

          <!-- Duration Card -->
          <div class="bg-gray-50 rounded-lg p-4 transition-all hover:bg-blue-50">
            <span class="block text-sm text-gray-500 mb-2">Duration</span>
            <div class="flex items-center">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-blue-600 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
              </svg>
              <span class="font-medium">
                {{ formatDate(lease.startDate) }} - {{ formatDate(lease.endDate) }}
              </span>
            </div>
          </div>
        </div>

        <!-- Actions Section -->
        <div class="flex justify-end gap-3 pt-4 border-t border-gray-100">
          <!-- Review Tenant Button -->
          <div v-if="canReviewLease(lease)">
            <div
                v-if="isLeaseReviewed(lease)"
                class="flex items-center text-green-700 font-medium px-4 py-2 rounded-md bg-green-50"
            >
              <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
              </svg>
              Lease Reviewed
            </div>
            <button
                v-else
                @click="openReviewModal(lease)"
                class="flex items-center bg-yellow-500 hover:bg-yellow-600 text-white px-4 py-2 rounded-md transition-colors"
            >
              <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" />
              </svg>
              Review Tenant
            </button>
          </div>

          <!-- Termination Status or Action -->
          <div
              v-if="lease.terminationStatus === 'PENDING'"
              class="flex items-center text-orange-600 font-medium px-4 py-2 rounded-md bg-orange-50"
          >
            <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
            Termination Requested
          </div>

          <div
              v-else-if="lease.terminationStatus === 'APPROVED'"
              class="flex items-center text-green-700 font-medium px-4 py-2 rounded-md bg-green-50"
          >
            <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
            </svg>
            Termination Approved
          </div>

          <button
              v-else-if="lease.status === 'ACTIVE'"
              @click="handleTerminate(lease.leaseId)"
              class="flex items-center bg-orange-500 hover:bg-orange-600 text-white px-4 py-2 rounded-md transition-colors"
          >
            <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
            </svg>
            Terminate
          </button>
        </div>
      </div>
    </div>

    <LeaseTenantReviewModal
        :visible="showReviewModal"
        :lease="selectedLease"
        :tenant="getSelectedTenant()"
        :property="getSelectedProperty()"
        @close="closeReviewModal"
        @success="handleReviewSuccess"
    />
  </div>
</template>