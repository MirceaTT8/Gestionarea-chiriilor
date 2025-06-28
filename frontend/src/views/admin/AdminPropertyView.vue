<script setup>
import { onMounted } from 'vue'
import { useAdminPropertyStore } from '@/stores/admin/adminPropertyStore.js'

const store = useAdminPropertyStore()

const statusColors = {
  AVAILABLE: { bg: 'bg-emerald-50', text: 'text-emerald-700', border: 'border-emerald-200' },
  RENTED: { bg: 'bg-amber-50', text: 'text-amber-700', border: 'border-amber-200' },
  INACTIVE: { bg: 'bg-gray-50', text: 'text-gray-600', border: 'border-gray-200' },
  MAINTENANCE: { bg: 'bg-red-50', text: 'text-red-700', border: 'border-red-200' },
  ACTIVE: { bg: 'bg-green-50', text: 'text-green-700', border: 'border-green-200' },

  APPROVED: { bg: 'bg-green-50', text: 'text-green-700', border: 'border-green-200' },
  PENDING: { bg: 'bg-yellow-50', text: 'text-yellow-700', border: 'border-yellow-200' },
  REJECTED: { bg: 'bg-red-50', text: 'text-red-600', border: 'border-red-200' }
}

const getStatusStyle = (status) => {
  return statusColors[status] || statusColors.INACTIVE
}

const getStatusDot = (status) => {
  switch (status) {
    case 'ACTIVE':
    case 'AVAILABLE':
    case 'APPROVED':
      return 'bg-green-500'
    case 'PENDING':
      return 'bg-yellow-500'
    case 'INACTIVE':
    case 'REJECTED':
      return 'bg-red-500'
    case 'RENTED':
      return 'bg-amber-500'
    case 'MAINTENANCE':
      return 'bg-red-500'
    default:
      return 'bg-gray-500'
  }
}

const retryLoading = async () => {
  await store.fetchProperties()
}

onMounted(() => {
  store.fetchProperties()
})
</script>

<template>
  <div class="max-w-7xl mx-auto px-6 py-10 space-y-8">
    <!-- Header with gradient background -->
    <div class="bg-gradient-to-r from-slate-600 to-blue-700 rounded-2xl shadow-lg">
      <div class="px-8 py-6">
        <div class="flex flex-col md:flex-row justify-between items-start md:items-center gap-4">
          <div class="flex items-center gap-4">
            <div class="bg-white/20 p-3 rounded-xl">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-8 w-8 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4" />
              </svg>
            </div>
            <div>
              <h1 class="text-3xl font-bold text-white">Property Management</h1>
              <p class="text-blue-100 mt-1">Manage your property portfolio</p>
            </div>
          </div>

          <div class="flex flex-col sm:flex-row gap-3 w-full md:w-auto mt-4 md:mt-0">
            <button
                class="flex items-center justify-center gap-2 px-4 py-2 bg-white text-blue-700 text-sm font-medium rounded-xl hover:bg-blue-50 transition-colors shadow-sm"
            >
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
                <path fill-rule="evenodd" d="M10 3a1 1 0 011 1v5h5a1 1 0 110 2h-5v5a1 1 0 11-2 0v-5H4a1 1 0 110-2h5V4a1 1 0 011-1z" clip-rule="evenodd" />
              </svg>
              Add Property
            </button>
            <button
                @click="store.refresh"
                :disabled="store.loading"
                class="flex items-center justify-center gap-2 px-4 py-2 bg-white/10 text-white text-sm font-medium rounded-xl hover:bg-white/20 transition-colors disabled:opacity-50"
            >
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
              </svg>
              Refresh
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Enhanced Stats cards with more metrics -->
    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
      <div class="bg-white rounded-xl shadow-md p-6">
        <div class="flex items-center gap-4">
          <div class="bg-blue-100 p-3 rounded-lg">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-blue-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6" />
            </svg>
          </div>
          <div>
            <h3 class="text-gray-500 text-sm">Total Properties</h3>
            <p class="font-bold text-xl text-gray-800">{{ store.properties?.length || 0 }}</p>
            <p class="text-xs text-gray-500">Portfolio size</p>
          </div>
        </div>
      </div>

      <div class="bg-white rounded-xl shadow-md p-6">
        <div class="flex items-center gap-4">
          <div class="bg-green-100 p-3 rounded-lg">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-green-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
          </div>
          <div>
            <h3 class="text-gray-500 text-sm">Approved</h3>
            <p class="font-bold text-xl text-gray-800">{{ store.properties?.filter(p => p.validationStatus === 'APPROVED').length || 0 }}</p>
            <p class="text-xs text-gray-500">Ready for listing</p>
          </div>
        </div>
      </div>

      <div class="bg-white rounded-xl shadow-md p-6">
        <div class="flex items-center gap-4">
          <div class="bg-red-100 p-3 rounded-lg">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-red-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </div>
          <div>
            <h3 class="text-gray-500 text-sm">Rejected Properties</h3>
            <p class="font-bold text-xl text-gray-800">{{ store.properties?.filter(p => p.validationStatus === 'REJECTED').length || 0 }}</p>
            <p class="text-xs text-gray-500">Not approved</p>
          </div>
        </div>
      </div>

      <div class="bg-white rounded-xl shadow-md p-6">
        <div class="flex items-center gap-4">
          <div class="bg-purple-100 p-3 rounded-lg">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-purple-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4" />
            </svg>
          </div>
          <div>
            <h3 class="text-gray-500 text-sm">Available</h3>
            <p class="font-bold text-xl text-gray-800">{{ store.properties?.filter(p => p.status === 'AVAILABLE').length || 0 }}</p>
            <p class="text-xs text-gray-500">Ready to rent</p>
          </div>
        </div>
      </div>
    </div>

    <!-- Filters and Search -->
    <div class="bg-white rounded-xl shadow-md overflow-hidden">
      <div class="border-b border-gray-100 px-6 py-4 flex justify-between items-center">
        <div>
          <h2 class="text-lg font-semibold text-gray-800">Find Properties</h2>
          <p class="text-sm text-gray-500">Search and filter your property portfolio</p>
        </div>
        <button
            @click="store.clearFilters"
            class="text-sm text-blue-600 hover:text-blue-800 font-medium"
        >
          Clear Filters
        </button>
      </div>

      <div class="p-6">
        <div class="grid grid-cols-1 sm:grid-cols-3 gap-6">
          <!-- Status Filter -->
          <div class="space-y-2">
            <label for="status" class="block text-sm font-medium text-gray-700">Property Status</label>
            <div class="relative">
              <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 4a1 1 0 011-1h16a1 1 0 011 1v2.586a1 1 0 01-.293.707l-6.414 6.414a1 1 0 00-.293.707V17l-4 4v-6.586a1 1 0 00-.293-.707L3.293 7.293A1 1 0 013 6.586V4z" />
                </svg>
              </div>
              <select
                  id="status"
                  v-model="store.selectedStatus"
                  class="block w-full pl-10 pr-10 py-2.5 text-gray-700 bg-white border border-gray-300 rounded-xl shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 sm:text-sm appearance-none"
              >
                <option v-for="status in store.statuses" :key="status" :value="status">
                  {{ status === 'ALL' ? 'All Statuses' : status }}
                </option>
              </select>
              <div class="absolute inset-y-0 right-0 flex items-center px-2 pointer-events-none">
                <svg class="h-5 w-5 text-gray-400" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" aria-hidden="true">
                  <path fill-rule="evenodd" d="M5.293 7.293a1 1 0 011.414 0L10 10.586l3.293-3.293a1 1 0 111.414 1.414l-4 4a1 1 0 01-1.414 0l-4-4a1 1 0 010-1.414z" clip-rule="evenodd" />
                </svg>
              </div>
            </div>
          </div>

          <!-- Search -->
          <div class="sm:col-span-2 space-y-2">
            <label for="search" class="block text-sm font-medium text-gray-700">Search Properties</label>
            <div class="relative">
              <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
                </svg>
              </div>
              <input
                  id="search"
                  v-model="store.searchQuery"
                  type="text"
                  placeholder="Search by name, address or ID"
                  class="block w-full pl-10 pr-3 py-2.5 text-gray-700 bg-white border border-gray-300 rounded-xl shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
              />
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Properties Grid -->
    <div v-if="store.loading" class="flex justify-center py-12">
      <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
    </div>

    <div v-else-if="store.properties.length === 0" class="text-center py-12">
      <div class="w-16 h-16 mx-auto bg-gray-100 rounded-full flex items-center justify-center mb-4">
        <svg xmlns="http://www.w3.org/2000/svg" class="h-8 w-8 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4" />
        </svg>
      </div>
      <h3 class="text-lg font-medium text-gray-900 mb-2">No properties found</h3>
      <p class="text-gray-500">Properties will appear here once landlords start adding them.</p>
    </div>

    <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
      <div
          v-for="property in store.properties"
          :key="property.propertyId"
          class="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden hover:shadow-lg transition-all duration-300"
      >
        <!-- Property Image -->
        <div class="h-48 bg-gradient-to-br from-blue-50 to-indigo-100 flex items-center justify-center">
          <div v-if="property.imageUrls?.length" class="w-full h-full">
            <img
                :src="`http://localhost:8080/image/${property.imageUrls[0]}`"
                :alt="property.name"
                class="w-full h-full object-cover"
            />
          </div>
          <div v-else class="text-center">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-12 w-12 text-blue-300 mx-auto mb-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4" />
            </svg>
            <p class="text-blue-400 text-sm font-medium">No Image</p>
          </div>
        </div>

        <!-- Property Details -->
        <div class="p-6">
          <!-- Header with title and status badges -->
          <div class="mb-4">
            <div class="flex justify-between items-start mb-3">
              <h3 class="text-lg font-semibold text-gray-900 line-clamp-1">{{ property.name }}</h3>
              <div class="flex flex-col gap-2 ml-3">
                <!-- Property Status Badge -->
                <span
                    v-if="property.status"
                    :class="[
                      statusColors[property.status].bg,
                      statusColors[property.status].text,
                      statusColors[property.status].border
                    ]"
                    class="px-2 py-1 rounded-full text-xs font-semibold capitalize border inline-flex items-center gap-1"
                >
                  <span class="w-1.5 h-1.5 rounded-full bg-current"></span>
                  {{ property.status.toLowerCase() }}
                </span>

                <!-- Validation Status Badge -->
                <span
                    v-if="property.validationStatus"
                    :class="[
                      statusColors[property.validationStatus].bg,
                      statusColors[property.validationStatus].text,
                      statusColors[property.validationStatus].border
                    ]"
                    class="px-2 py-1 rounded-full text-xs font-semibold capitalize border inline-flex items-center gap-1"
                >
                  <span class="w-1.5 h-1.5 rounded-full bg-current"></span>
                  {{ property.validationStatus.toLowerCase() }}
                </span>
              </div>
            </div>

            <p class="text-sm text-gray-600 line-clamp-2">{{ property.address }}</p>
          </div>

          <!-- Property Info Grid -->
          <div class="grid grid-cols-2 gap-4 mb-4 text-sm">
            <div>
              <p class="text-gray-500 font-medium">Property ID</p>
              <p class="text-gray-700 font-mono">{{ property.propertyId }}</p>
            </div>
            <div>
              <p class="text-gray-500 font-medium">Monthly Rent</p>
              <p class="text-gray-700 font-semibold">${{ property.rentAmount || 'N/A' }}</p>
            </div>
            <div>
              <p class="text-gray-500 font-medium">Type</p>
              <p class="text-gray-700 capitalize">{{ property.type?.toLowerCase() || 'N/A' }}</p>
            </div>
            <div>
              <p class="text-gray-500 font-medium">Owner ID</p>
              <p class="text-gray-700 font-mono">{{ property.ownerId || 'N/A' }}</p>
            </div>
          </div>

          <!-- Actions -->
          <div class="border-t border-gray-100 pt-4 space-y-3">
            <!-- Approval buttons for pending properties -->
            <div v-if="property.validationStatus === 'PENDING'" class="flex gap-2">
              <button
                  @click="store.approveProperty(property.propertyId)"
                  class="flex-1 text-sm font-medium bg-green-600 text-white px-4 py-2 rounded-lg hover:bg-green-700 transition-colors flex items-center justify-center gap-2"
              >
                <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
                </svg>
                Approve
              </button>
              <button
                  @click="store.rejectProperty(property.propertyId)"
                  class="flex-1 text-sm font-medium bg-red-600 text-white px-4 py-2 rounded-lg hover:bg-red-700 transition-colors flex items-center justify-center gap-2"
              >
                <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
                </svg>
                Reject
              </button>
            </div>

            <!-- View details link -->
            <router-link
                :to="{ name: 'PropertyDetails', params: { id: property.propertyId } }"
                class="block w-full text-center text-sm font-medium text-blue-600 hover:text-blue-800 transition-colors py-2 border border-blue-200 rounded-lg hover:bg-blue-50"
            >
              View Details
            </router-link>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>