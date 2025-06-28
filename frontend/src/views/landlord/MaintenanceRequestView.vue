<script setup>
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useMaintenanceLandlordStore } from "@/stores/landlord/maintenanceStore.js";
import MaintenanceStatusFilter from '@/components/landlord/maintenance/MaintenanceStatusFilter.vue'
import MaintenanceListLandlord from '@/components/landlord/maintenance/MaintenanceListLandlord.vue'

const router = useRouter()
const store = useMaintenanceLandlordStore()

onMounted(async () => {
  await store.loadRequests()
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
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z" />
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
              </svg>
            </div>
            <div>
              <h1 class="text-3xl font-bold text-white">Maintenance Requests</h1>
              <p class="text-blue-100 mt-1">Manage property maintenance and repairs</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Loading State -->
    <div v-if="store.loading" class="flex flex-col items-center justify-center p-12 bg-white rounded-xl shadow-md">
      <div class="w-16 h-16 border-4 border-gray-200 border-t-blue-600 rounded-full animate-spin mb-6"></div>
      <p class="text-gray-600 text-lg">Loading maintenance requests...</p>
    </div>

    <!-- Error State -->
    <div v-else-if="store.error" class="bg-red-50 border-l-4 border-red-500 p-6 rounded-lg shadow-md">
      <div class="flex items-center mb-4">
        <svg xmlns="http://www.w3.org/2000/svg" class="h-8 w-8 text-red-500 mr-3" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
        </svg>
        <p class="text-red-700 font-medium text-lg">{{ store.error }}</p>
      </div>
      <button
          @click="router.go(0)"
          class="inline-flex items-center bg-white hover:bg-gray-50 text-gray-800 font-medium py-2 px-4 border border-gray-300 rounded-lg shadow-sm transition-colors">
        <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-2 text-gray-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
        </svg>
        Try Again
      </button>
    </div>

    <div v-else class="space-y-6">
      <div class="bg-white rounded-xl shadow-md p-6">
        <div class="flex items-center mb-4">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-gray-500 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 4a1 1 0 011-1h16a1 1 0 011 1v2.586a1 1 0 01-.293.707l-6.414 6.414a1 1 0 00-.293.707V17l-4 4v-6.586a1 1 0 00-.293-.707L3.293 7.293A1 1 0 013 6.586V4z" />
          </svg>
          <h2 class="text-lg font-medium text-gray-700">Filter Requests</h2>
        </div>

        <MaintenanceStatusFilter
            v-model:selectedStatus="store.selectedStatus"
            :availableStatuses="store.availableStatuses"
        />
      </div>

      <div class="bg-white rounded-xl shadow-md overflow-hidden">
        <div class="p-6 border-b border-gray-200">
          <h2 class="text-xl font-semibold text-gray-800">Maintenance Requests</h2>
          <p class="text-gray-500 text-sm mt-1">
            {{ store.selectedStatus === 'all' ? 'All maintenance requests' : `Filtered by: ${store.selectedStatus.replace('_', ' ').toLowerCase().replace(/(^\w|\s\w)/g, l => l.toUpperCase())}` }}
          </p>
        </div>

        <MaintenanceListLandlord
            :requests="store.filteredRequests"
            :leases="store.leases"
            :tenants="store.tenants"
            :properties="store.properties"
            :selected-status="store.selectedStatus"
            @update-status="store.updateStatus"
        />
      </div>
    </div>
  </div>
</template>