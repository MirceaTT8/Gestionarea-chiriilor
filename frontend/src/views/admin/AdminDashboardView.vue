<script setup>
import { useAdminDashboardStore } from '@/stores/admin/adminDashboardStore.js'
import { ref, onMounted } from 'vue'
import Chart from 'primevue/chart'
import LeaseTerminationModal from '@/components/admin/dashboard/LeaseTerminationModal.vue'
import PendingLeasesModal from '@/components/admin/dashboard/PendingLeasesModal.vue'
import PendingPropertiesModal from '@/components/admin/dashboard/PendingPropertiesModal.vue'
import StatsCards from '@/components/admin/dashboard/StatsCards.vue'
import {fetchAllUsers} from "@/services/userService.js";
import {fetchAllPropertiesAdmin} from "@/services/propertyService.js";

const store = useAdminDashboardStore()
const chartPeriod = ref('monthly')

const showSuccessMessage = ref(false)
const successMessage = ref('')
const showErrorMessage = ref(false)
const errorMessage = ref('')
const statsLoading = ref(false)
const users = ref([])
const properties = ref([])

onMounted(async () => {
  statsLoading.value = true

  try {
    await Promise.all([
      store.initDashboard(),
      fetchAllUsers().then(data => users.value = data),
      fetchAllPropertiesAdmin().then(data => properties.value = data)
    ])
  } catch (error) {
    console.error('Error loading dashboard data:', error)
  } finally {
    statsLoading.value = false
  }
})

const changeChartPeriod = async (period) => {
  chartPeriod.value = period
  await store.fetchLeaseChartData(period)
}

const handleApproveProperty = async (propertyId) => {
  const result = await store.approveProperty(propertyId)

  if (result.success) {
    await propertyStore.loadProperties()

    successMessage.value = result.message
    showSuccessMessage.value = true
    setTimeout(() => {
      showSuccessMessage.value = false
    }, 3000)
  } else {
    errorMessage.value = result.message
    showErrorMessage.value = true
    setTimeout(() => {
      showErrorMessage.value = false
    }, 5000)
  }
}

const handleRejectProperty = async (propertyId) => {
  const result = await store.rejectProperty(propertyId)

  if (result.success) {
    await propertyStore.loadProperties()

    successMessage.value = result.message
    showSuccessMessage.value = true
    setTimeout(() => {
      showSuccessMessage.value = false
    }, 3000)
  } else {
    errorMessage.value = result.message
    showErrorMessage.value = true
    setTimeout(() => {
      showErrorMessage.value = false
    }, 5000)
  }
}
</script>

<template>
  <div class="max-w-7xl mx-auto px-6 py-10 space-y-8">
    <!-- Success/Error Toast Messages -->
    <div v-if="showSuccessMessage" class="fixed top-4 right-4 z-50">
      <div class="bg-green-100 border border-green-400 text-green-700 px-4 py-3 rounded-lg shadow-lg flex items-center max-w-md">
        <svg class="w-5 h-5 mr-2 flex-shrink-0" fill="currentColor" viewBox="0 0 20 20">
          <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd"/>
        </svg>
        <span>{{ successMessage }}</span>
        <button @click="showSuccessMessage = false" class="ml-3 text-green-500 hover:text-green-700">
          <svg class="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
            <path fill-rule="evenodd" d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z" clip-rule="evenodd"/>
          </svg>
        </button>
      </div>
    </div>

    <div v-if="showErrorMessage" class="fixed top-4 right-4 z-50">
      <div class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded-lg shadow-lg flex items-center max-w-md">
        <svg class="w-5 h-5 mr-2 flex-shrink-0" fill="currentColor" viewBox="0 0 20 20">
          <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clip-rule="evenodd"/>
        </svg>
        <span>{{ errorMessage }}</span>
        <button @click="showErrorMessage = false" class="ml-3 text-red-500 hover:text-red-700">
          <svg class="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
            <path fill-rule="evenodd" d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z" clip-rule="evenodd"/>
          </svg>
        </button>
      </div>
    </div>

    <!-- Header with dashboard title and subtle gradient background -->
    <div class="bg-gradient-to-r from-indigo-600 to-purple-600 rounded-lg shadow-lg">
      <div class="px-8 py-6">
        <h1 class="text-3xl font-bold text-white">Admin Dashboard</h1>
        <p class="text-indigo-100 mt-1">Manage properties, leases and approvals</p>
      </div>
    </div>

    <!-- Stats Cards Section -->
    <StatsCards
        :stats-data="store.statsData"
        :loading="store.statsLoading"
    />

    <!-- Action Cards Section -->
    <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
      <!-- Terminate Leases Card -->
      <div class="bg-white rounded-xl shadow-md overflow-hidden transition-all hover:shadow-lg">
        <div class="bg-red-100 px-6 py-4 flex justify-between items-center">
          <h3 class="text-red-800 font-semibold">Lease Termination</h3>
          <span v-if="store.stats.pendingTerminations > 0"
                class="bg-red-500 text-white text-xs font-bold px-2 py-1 rounded-full min-w-[1.5rem] h-6 flex items-center justify-center">
            {{ store.stats.pendingTerminations }}
          </span>
        </div>
        <div class="p-6">
          <p class="text-gray-600 mb-4 text-sm">Manage and process lease termination requests</p>
          <button
              @click="store.openTerminationModal"
              class="w-full bg-gradient-to-r from-red-500 to-red-600 hover:from-red-600 hover:to-red-700 text-white px-4 py-3 rounded-lg font-medium shadow-sm transition-all flex items-center justify-center gap-2 relative"
          >
            <span>Terminate Leases</span>
            <span v-if="store.stats.pendingTerminations > 0"
                  class="bg-red-700 text-white text-xs font-bold px-2 py-1 rounded-full">
              {{ store.stats.pendingTerminations }}
            </span>
            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
              <path d="M4 3a2 2 0 100 4h12a2 2 0 100-4H4z" />
              <path fill-rule="evenodd" d="M3 8h14v7a2 2 0 01-2 2H5a2 2 0 01-2-2V8zm5 3a1 1 0 011-1h2a1 1 0 110 2H9a1 1 0 01-1-1z" clip-rule="evenodd" />
            </svg>
          </button>
        </div>
      </div>

      <!-- Accept Properties Card -->
      <div class="bg-white rounded-xl shadow-md overflow-hidden transition-all hover:shadow-lg">
        <div class="bg-blue-100 px-6 py-4 flex justify-between items-center">
          <h3 class="text-blue-800 font-semibold">Property Approvals</h3>
          <span v-if="store.stats.pendingPropertiesCount > 0"
                class="bg-blue-500 text-white text-xs font-bold px-2 py-1 rounded-full min-w-[1.5rem] h-6 flex items-center justify-center">
            {{ store.stats.pendingPropertiesCount }}
          </span>
        </div>
        <div class="p-6">
          <p class="text-gray-600 mb-4 text-sm">Review and accept pending property listings</p>
          <button
              @click="store.openPendingPropertiesModal"
              class="w-full bg-gradient-to-r from-blue-500 to-blue-600 hover:from-blue-600 hover:to-blue-700 text-white px-4 py-3 rounded-lg font-medium shadow-sm transition-all flex items-center justify-center gap-2"
          >
            <span>Accept Properties</span>
            <span v-if="store.stats.pendingPropertiesCount > 0"
                  class="bg-blue-700 text-white text-xs font-bold px-2 py-1 rounded-full">
              {{ store.stats.pendingPropertiesCount }}
            </span>
            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
              <path d="M10.707 2.293a1 1 0 00-1.414 0l-7 7a1 1 0 001.414 1.414L4 10.414V17a1 1 0 001 1h2a1 1 0 001-1v-2a1 1 0 011-1h2a1 1 0 011 1v2a1 1 0 001 1h2a1 1 0 001-1v-6.586l.293.293a1 1 0 001.414-1.414l-7-7z" />
            </svg>
          </button>
        </div>
      </div>

      <!-- Approve Leases Card -->
      <div class="bg-white rounded-xl shadow-md overflow-hidden transition-all hover:shadow-lg">
        <div class="bg-green-100 px-6 py-4 flex justify-between items-center">
          <h3 class="text-green-800 font-semibold">Lease Approvals</h3>
          <span v-if="store.stats.pendingLeases > 0"
                class="bg-green-500 text-white text-xs font-bold px-2 py-1 rounded-full min-w-[1.5rem] h-6 flex items-center justify-center">
            {{ store.stats.pendingLeases }}
          </span>
        </div>
        <div class="p-6">
          <p class="text-gray-600 mb-4 text-sm">Review and approve pending lease requests</p>
          <button
              @click="store.openPendingLeasesModal"
              class="w-full bg-gradient-to-r from-green-500 to-green-600 hover:from-green-600 hover:to-green-700 text-white px-4 py-3 rounded-lg font-medium shadow-sm transition-all flex items-center justify-center gap-2"
          >
            <span>Approve Leases</span>
            <span v-if="store.stats.pendingLeases > 0"
                  class="bg-green-700 text-white text-xs font-bold px-2 py-1 rounded-full">
              {{ store.stats.pendingLeases }}
            </span>
            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
              <path fill-rule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clip-rule="evenodd" />
            </svg>
          </button>
        </div>
      </div>
    </div>

    <!-- Ratings Section -->
    <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
      <!-- Landlord Ratings Card -->
      <div class="bg-white rounded-xl shadow-md overflow-hidden">
        <div class="bg-yellow-100 px-6 py-4">
          <h3 class="text-yellow-800 font-semibold">Landlord Ratings</h3>
        </div>
        <div class="p-6">
          <p class="text-gray-600 mb-4">Access and manage landlord ratings and reviews</p>
          <router-link
              to="/admin/ratings"
              class="inline-flex items-center gap-2 bg-gradient-to-r from-yellow-500 to-amber-500 hover:from-yellow-600 hover:to-amber-600 text-white px-6 py-3 rounded-lg font-medium shadow-sm transition-all"
          >
            <span>View Landlord Ratings</span>
            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
              <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" />
            </svg>
          </router-link>
        </div>
      </div>

      <!-- Tenant Ratings Card -->
      <div class="bg-white rounded-xl shadow-md overflow-hidden">
        <div class="bg-blue-100 px-6 py-4">
          <h3 class="text-blue-800 font-semibold">Tenant Ratings</h3>
        </div>
        <div class="p-6">
          <p class="text-gray-600 mb-4">Access and manage tenant ratings and payment scores</p>
          <router-link
              to="/admin/tenant-ratings"
              class="inline-flex items-center gap-2 bg-gradient-to-r from-blue-500 to-indigo-500 hover:from-blue-600 hover:to-indigo-600 text-white px-6 py-3 rounded-lg font-medium shadow-sm transition-all"
          >
            <span>View Tenant Ratings</span>
            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
              <path fill-rule="evenodd" d="M10 9a3 3 0 100-6 3 3 0 000 6zm-7 9a7 7 0 1114 0H3z" clip-rule="evenodd" />
            </svg>
          </router-link>
        </div>
      </div>
    </div>

    <!-- Chart Section with functional period buttons -->
    <div class="bg-white rounded-xl shadow-md overflow-hidden">
      <div class="px-6 py-4 border-b border-gray-100 flex justify-between items-center">
        <h2 class="text-lg font-semibold text-gray-800">Lease Activity Trends</h2>
        <div class="flex gap-2">
          <button
              @click="changeChartPeriod('monthly')"
              :class="[
              'px-3 py-1 text-xs rounded-full font-medium transition-all duration-200',
              chartPeriod === 'monthly'
                ? 'bg-indigo-100 text-indigo-700 ring-2 ring-indigo-200'
                : 'bg-gray-100 text-gray-500 hover:bg-gray-200'
            ]"
          >
            Monthly
          </button>
          <button
              @click="changeChartPeriod('quarterly')"
              :class="[
              'px-3 py-1 text-xs rounded-full font-medium transition-all duration-200',
              chartPeriod === 'quarterly'
                ? 'bg-indigo-100 text-indigo-700 ring-2 ring-indigo-200'
                : 'bg-gray-100 text-gray-500 hover:bg-gray-200'
            ]"
          >
            Quarterly
          </button>
          <button
              @click="changeChartPeriod('yearly')"
              :class="[
              'px-3 py-1 text-xs rounded-full font-medium transition-all duration-200',
              chartPeriod === 'yearly'
                ? 'bg-indigo-100 text-indigo-700 ring-2 ring-indigo-200'
                : 'bg-gray-100 text-gray-500 hover:bg-gray-200'
            ]"
          >
            Yearly
          </button>
        </div>
      </div>
      <div class="p-6">
        <div class="h-[400px] relative">
          <!-- Loading state -->
          <div v-if="store.chartLoading" class="absolute inset-0 bg-white bg-opacity-75 flex items-center justify-center z-10">
            <div class="flex items-center space-x-2">
              <div class="animate-spin rounded-full h-6 w-6 border-b-2 border-indigo-600"></div>
              <span class="text-sm text-gray-600">Loading chart data...</span>
            </div>
          </div>

          <!-- Chart -->
          <Chart
              type="bar"
              :data="store.leaseChartData"
              :options="store.leaseChartOptions"
              class="w-full h-full"
          />
        </div>
      </div>
    </div>

    <!-- Modals -->
    <LeaseTerminationModal
        :visible="store.showTerminateModal"
        :leases="store.pendingLeaseTerminations"
        :loading="store.loading"
        :users="users"
        :properties="properties"
        @update:visible="store.showTerminateModal = $event"
        @approve="store.approveTermination"
        @reject="store.rejectTermination"
    />

    <PendingLeasesModal
        :visible="store.showPendingLeasesModal"
        :leases="store.pendingLeaseApprovals"
        :loading="store.loading"
        :users="users"
        :properties="properties"
        @update:visible="store.showPendingLeasesModal = $event"
        @approve="store.approveLease"
        @reject="store.rejectLease"
    />

    <PendingPropertiesModal
        :visible="store.showPendingPropertiesModal"
        :properties="store.pendingProperties"
        :users="users"
        :loading="store.loading"
        @update:visible="store.showPendingPropertiesModal = $event"
        @approve-property="handleApproveProperty"
        @reject-property="handleRejectProperty"
    />
  </div>
</template>