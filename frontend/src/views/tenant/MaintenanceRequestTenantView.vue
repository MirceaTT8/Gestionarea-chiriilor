<script setup>
import { computed, onMounted, ref } from 'vue'
import { useMaintenanceTenantStore } from "@/stores/tenant/leaseMaintenanceTenantStore.js";
import MaintenanceListTenant from '@/components/tenant/maintenance/MaintenanceListTenant.vue'
import MaintenanceCreateForm from '@/components/tenant/maintenance/MaintenanceCreateForm.vue'
import MaintenanceFilter from '@/components/tenant/maintenance/MaintenanceFilter.vue'
import MaintenanceHeader from '@/components/tenant/maintenance/MaintenanceHeader.vue'

const maintenanceStore = useMaintenanceTenantStore()

const showCreateForm = ref(false)
const statusFilter = ref('ALL')

const filteredRequests = computed(() => {
  if (statusFilter.value === 'ALL') return maintenanceStore.requests
  return maintenanceStore.requests.filter(request => request.status === statusFilter.value)
})

const totalRequests = computed(() => maintenanceStore.requests.length)
const pendingRequests = computed(() => maintenanceStore.requests.filter(req => req.status === 'PENDING').length)
const inProgressRequests = computed(() => maintenanceStore.requests.filter(req => req.status === 'IN_PROGRESS').length)
const completedRequests = computed(() => maintenanceStore.requests.filter(req => req.status === 'COMPLETED').length)

const loadRequests = async () => {
  await maintenanceStore.fetchRequests()
}

const submitRequest = async (newRequest) => {
  await maintenanceStore.createRequest(newRequest)
  showCreateForm.value = false
}

const handleFeedbackSubmitted = async (feedbackData) => {
  console.log('Feedback submitted:', feedbackData);

}

onMounted(() => {
  loadRequests()
})
</script>

<template>
  <div class="max-w-6xl mx-auto px-4 py-10 sm:px-6 lg:px-8">
    <!-- Header with Gradient Background -->
    <div class="bg-gradient-to-r from-teal-600 to-green-600 rounded-xl shadow-md overflow-hidden mb-8">
      <div class="px-6 py-8 sm:px-8">
        <MaintenanceHeader
            :show-create-button="!showCreateForm"
            @create-new="showCreateForm = true"
        />

        <!-- Quick Stats Cards -->
        <div class="mt-8 grid grid-cols-1 md:grid-cols-4 gap-4">
          <!-- Total Requests Card -->
          <div class="bg-white/10 backdrop-blur-sm rounded-xl p-4 text-white">
            <div class="flex items-center">
              <div class="p-3 bg-white/20 rounded-lg mr-4">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
                </svg>
              </div>
              <div>
                <p class="text-sm text-white/80">Total Requests</p>
                <p class="text-2xl font-bold mt-1">{{ totalRequests }}</p>
              </div>
            </div>
          </div>

          <!-- Pending Card -->
          <div class="bg-white/10 backdrop-blur-sm rounded-xl p-4 text-white">
            <div class="flex items-center">
              <div class="p-3 bg-white/20 rounded-lg mr-4">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
              </div>
              <div>
                <p class="text-sm text-white/80">Pending</p>
                <p class="text-2xl font-bold mt-1">{{ pendingRequests }}</p>
              </div>
            </div>
          </div>

          <!-- In Progress Card -->
          <div class="bg-white/10 backdrop-blur-sm rounded-xl p-4 text-white">
            <div class="flex items-center">
              <div class="p-3 bg-white/20 rounded-lg mr-4">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5.882V19.24a1.76 1.76 0 01-3.417.592l-2.147-6.15M18 13a3 3 0 100-6M5.436 13.683A4.001 4.001 0 017 6h1.832c4.1 0 7.625-1.234 9.168-3v14c-1.543-1.766-5.067-3-9.168-3H7a3.988 3.988 0 01-1.564-.317z" />
                </svg>
              </div>
              <div>
                <p class="text-sm text-white/80">In Progress</p>
                <p class="text-2xl font-bold mt-1">{{ inProgressRequests }}</p>
              </div>
            </div>
          </div>

          <!-- Completed Card -->
          <div class="bg-white/10 backdrop-blur-sm rounded-xl p-4 text-white">
            <div class="flex items-center">
              <div class="p-3 bg-white/20 rounded-lg mr-4">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
              </div>
              <div>
                <p class="text-sm text-white/80">Completed</p>
                <p class="text-2xl font-bold mt-1">{{ completedRequests }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Filter Bar -->
    <div v-if="!showCreateForm" class="bg-white rounded-xl shadow-sm overflow-hidden mb-8">
      <div class="px-6 py-4 border-b border-gray-100 flex justify-between items-center">
        <h2 class="text-lg font-semibold text-gray-800">Maintenance Requests</h2>
        <MaintenanceFilter v-model="statusFilter" />
      </div>
    </div>

    <!-- Form or Listing -->
    <div>
      <MaintenanceCreateForm
          v-if="showCreateForm"
          @submit="submitRequest"
          @cancel="showCreateForm = false"
      />

      <MaintenanceListTenant
          v-else
          :requests="filteredRequests"
          :loading="maintenanceStore.loading"
          :error="maintenanceStore.error"
          :status-filter="statusFilter"
          @create-new="showCreateForm = true"
          @retry="loadRequests"
          @feedback-submitted="handleFeedbackSubmitted"
      />
    </div>
  </div>
</template>