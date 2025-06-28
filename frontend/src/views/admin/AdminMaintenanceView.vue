<script setup>
import { ref, onMounted, computed } from 'vue'
import { useMaintenanceAdminStore } from '@/stores/admin/adminMaintenanceStore.js'
import MaintenanceFilters from '@/components/admin/maintenance/MaintenanceFilters.vue'
import MaintenanceTable from '@/components/admin/maintenance/MaintenanceTable.vue'
import MaintenanceImageModal from '@/components/admin/maintenance/MaintenanceImageModal.vue'
import PaginationControls from "@/components/PaginationControls.vue";

const store = useMaintenanceAdminStore()

const showModal = ref(false)
const modalImageIds = ref([])

const totalRequests = computed(() => store.filteredRequests.length)
const cancelledRequests = computed(() => store.filteredRequests.filter(req => req.status === 'CANCELLED').length)
const completedRequests = computed(() => store.filteredRequests.filter(req => req.status === 'COMPLETED').length)
const inProgressRequests = computed(() => store.filteredRequests.filter(req => req.status === 'IN_PROGRESS').length)

const openImageModal = (ids) => {
  modalImageIds.value = ids
  showModal.value = true
}

const updateFilters = (newFilters) => {
  store.filters = newFilters
}

const sortBy = (field) => {
  store.setSort(field)
}

onMounted(() => {
  store.fetchRequests()
})
</script>

<template>
  <div class="max-w-7xl mx-auto px-6 py-10 space-y-8">
    <!-- Header with gradient background -->
    <div class="bg-gradient-to-r from-amber-600 to-orange-600 rounded-2xl shadow-lg">
      <div class="px-8 py-6">
        <div class="flex flex-col md:flex-row justify-between items-start md:items-center gap-4">
          <div class="flex items-center gap-4">
            <div class="bg-white/20 p-3 rounded-xl">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-8 w-8 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5.882V19.24a1.76 1.76 0 01-3.417.592l-2.147-6.15M18 13a3 3 0 100-6M5.436 13.683A4.001 4.001 0 017 6h1.832c4.1 0 7.625-1.234 9.168-3v14c-1.543-1.766-5.067-3-9.168-3H7a3.988 3.988 0 01-1.564-.317z" />
              </svg>
            </div>
            <div>
              <h1 class="text-3xl font-bold text-white">Maintenance Requests</h1>
              <p class="text-amber-100 mt-1">Manage and track property maintenance</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Stats cards -->
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
      <!-- Total Requests Card -->
      <div class="bg-white rounded-xl shadow-md p-6">
        <div class="flex items-center gap-4">
          <div class="bg-blue-100 p-3 rounded-lg">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-blue-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
            </svg>
          </div>
          <div>
            <h3 class="text-gray-500 text-sm">Total Requests</h3>
            <p class="font-bold text-xl text-gray-800">{{ totalRequests }}</p>
          </div>
        </div>
      </div>

      <!-- Cancelled Requests Card -->
      <div class="bg-white rounded-xl shadow-md p-6">
        <div class="flex items-center gap-4">
          <div class="bg-red-100 p-3 rounded-lg">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-red-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 14l2-2m0 0l2-2m-2 2l-2-2m2 2l2 2m7-2a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
          </div>
          <div>
            <h3 class="text-gray-500 text-sm">Cancelled Requests</h3>
            <p class="font-bold text-xl text-gray-800">{{ cancelledRequests }}</p>
          </div>
        </div>
      </div>

      <!-- Completed Requests Card -->
      <div class="bg-white rounded-xl shadow-md p-6">
        <div class="flex items-center gap-4">
          <div class="bg-green-100 p-3 rounded-lg">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-green-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
          </div>
          <div>
            <h3 class="text-gray-500 text-sm">Completed Requests</h3>
            <p class="font-bold text-xl text-gray-800">{{ completedRequests }}</p>
          </div>
        </div>
      </div>

      <!-- In Progress Requests Card -->
      <div class="bg-white rounded-xl shadow-md p-6">
        <div class="flex items-center gap-4">
          <div class="bg-amber-100 p-3 rounded-lg">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-amber-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
          </div>
          <div>
            <h3 class="text-gray-500 text-sm">In Progress Requests</h3>
            <p class="font-bold text-xl text-gray-800">{{ inProgressRequests }}</p>
          </div>
        </div>
      </div>
    </div>

    <!-- Filters panel with improved styling -->
    <div class="bg-white rounded-xl shadow-md overflow-hidden">
      <div class="border-b border-gray-100 px-6 py-4">
        <h2 class="text-lg font-semibold text-gray-800">Filter Requests</h2>
        <p class="text-sm text-gray-500">Narrow down maintenance requests by various criteria</p>
      </div>
      <div class="p-6">
        <MaintenanceFilters :filters="store.filters" @update:filters="updateFilters" />
      </div>
    </div>

    <!-- Loading -->
    <div v-if="store.loading" class="bg-white rounded-xl shadow-md p-10">
      <div class="flex flex-col items-center justify-center">
        <svg class="animate-spin h-10 w-10 text-amber-600 mb-4" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
          <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
          <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
        </svg>
        <p class="text-gray-500 text-lg">Loading maintenance requests...</p>
      </div>
    </div>

    <!-- Empty State -->
    <div v-else-if="store.filteredRequests.length === 0" class="bg-white rounded-xl shadow-md p-10">
      <div class="flex flex-col items-center justify-center">
        <svg xmlns="http://www.w3.org/2000/svg" class="h-16 w-16 text-gray-300 mb-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1" d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10" />
        </svg>
        <p class="text-gray-600 font-medium mb-1">No maintenance requests found</p>
        <p class="text-gray-500 text-sm">Try adjusting your filters or search criteria</p>
      </div>
    </div>

    <!-- Maintenance Table -->
    <div v-else class="bg-white rounded-xl shadow-md overflow-hidden">
      <div class="border-b border-gray-100 px-6 py-4">
        <h2 class="text-lg font-semibold text-gray-800">Maintenance Requests</h2>
        <p class="text-sm text-gray-500">{{ store.filteredRequests.length }} requests found</p>
      </div>
      <MaintenanceTable
          :requests="store.filteredRequests"
          :sort-field="store.sortField"
          :sort-direction="store.sortDirection"
          @sort="sortBy"
          @view-images="openImageModal"
      />
    </div>

    <div class="flex justify-center mt-8">
      <PaginationControls
          :current-page="store.currentPage"
          :total-pages="store.totalPages"
          @update:currentPage="store.currentPage = $event"
          class="bg-white rounded-xl shadow-md px-6 py-4 inline-flex"
      />
    </div>

    <!-- Maintenance Image Modal -->
    <MaintenanceImageModal
        :images="modalImageIds"
        :show="showModal"
        @close="showModal = false"
    />
  </div>
</template>

<style scoped>
/* Add any component-specific styles here */
</style>