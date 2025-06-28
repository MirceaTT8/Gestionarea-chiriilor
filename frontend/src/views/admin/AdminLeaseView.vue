<script setup>
import { onMounted, ref, computed } from 'vue'
import { fetchAllLeases } from '@/services/leaseService'

const leases = ref([])
const loading = ref(true)
const error = ref(null)
const searchQuery = ref('')
const selectedStatus = ref('ALL')

const totalLeases = computed(() => leases.value.length)
const activeLeases = computed(() => leases.value.filter(lease => lease.status === 'ACTIVE').length)
const pendingLeases = computed(() => leases.value.filter(lease => lease.status === 'PENDING').length)
const expiringLeases = computed(() => {
  const threeMonthsFromNow = new Date();
  threeMonthsFromNow.setMonth(threeMonthsFromNow.getMonth() + 3);

  return leases.value.filter(lease => {
    const endDate = new Date(lease.endDate);
    return lease.status === 'ACTIVE' && endDate <= threeMonthsFromNow;
  }).length;
});

const filteredLeases = computed(() => {
  return leases.value.filter(lease => {
    const matchesSearch = !searchQuery.value ||
        lease.leaseId.toString().includes(searchQuery.value) ||
        (lease.tenantId && lease.tenantId.toString().includes(searchQuery.value)) ||
        (lease.propertyId && lease.propertyId.toString().includes(searchQuery.value));

    const matchesStatus = selectedStatus.value === 'ALL' || lease.status === selectedStatus.value;

    return matchesSearch && matchesStatus;
  });
});

const getStatusStyle = (status) => {
  switch (status) {
    case 'ACTIVE':
      return 'bg-green-100 text-green-800 border border-green-200';
    case 'PENDING':
      return 'bg-yellow-100 text-yellow-800 border border-yellow-200';
    case 'TERMINATED':
      return 'bg-gray-100 text-gray-800 border border-gray-200';
    default:
      return 'bg-gray-100 text-gray-800 border border-gray-200';
  }
};

const getStatusDot = (status) => {
  switch (status) {
    case 'ACTIVE':
      return 'bg-green-500';
    case 'PENDING':
      return 'bg-yellow-500';
    case 'TERMINATED':
      return 'bg-gray-500';
    default:
      return 'bg-gray-500';
  }
};

onMounted(async () => {
  try {
    leases.value = await fetchAllLeases()
  } catch (err) {
    error.value = 'Failed to load leases.'
    console.error(err)
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <div class="max-w-7xl mx-auto px-6 py-10 space-y-8">
    <!-- Header with gradient background -->
    <div class="bg-gradient-to-r from-violet-600 to-indigo-600 rounded-2xl shadow-lg">
      <div class="px-8 py-6">
        <div class="flex flex-col md:flex-row justify-between items-start md:items-center gap-4">
          <div class="flex items-center gap-4">
            <div class="bg-white/20 p-3 rounded-xl">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-8 w-8 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
              </svg>
            </div>
            <div>
              <h1 class="text-3xl font-bold text-white">Lease Management</h1>
              <p class="text-indigo-100 mt-1">Manage all property lease agreements</p>
            </div>
          </div>

          <div class="flex flex-col sm:flex-row gap-3 w-full md:w-auto mt-4 md:mt-0">
            <button
                class="flex items-center justify-center gap-2 px-4 py-2 bg-white text-indigo-700 text-sm font-medium rounded-xl hover:bg-indigo-50 transition-colors shadow-sm"
            >
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
                <path fill-rule="evenodd" d="M10 3a1 1 0 011 1v5h5a1 1 0 110 2h-5v5a1 1 0 11-2 0v-5H4a1 1 0 110-2h5V4a1 1 0 011-1z" clip-rule="evenodd" />
              </svg>
              Create Lease
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Stats cards -->
    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
      <div class="bg-white rounded-xl shadow-md p-6">
        <div class="flex items-center gap-4">
          <div class="bg-indigo-100 p-3 rounded-lg">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-indigo-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
            </svg>
          </div>
          <div>
            <h3 class="text-gray-500 text-sm">Total Leases</h3>
            <p class="font-bold text-xl text-gray-800">{{ totalLeases }}</p>
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
            <h3 class="text-gray-500 text-sm">Active Leases</h3>
            <p class="font-bold text-xl text-gray-800">{{ activeLeases }}</p>
          </div>
        </div>
      </div>

      <div class="bg-white rounded-xl shadow-md p-6">
        <div class="flex items-center gap-4">
          <div class="bg-yellow-100 p-3 rounded-lg">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-yellow-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
          </div>
          <div>
            <h3 class="text-gray-500 text-sm">Pending Leases</h3>
            <p class="font-bold text-xl text-gray-800">{{ pendingLeases }}</p>
          </div>
        </div>
      </div>

      <div class="bg-white rounded-xl shadow-md p-6">
        <div class="flex items-center gap-4">
          <div class="bg-orange-100 p-3 rounded-lg">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-orange-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
            </svg>
          </div>
          <div>
            <h3 class="text-gray-500 text-sm">Expiring Soon</h3>
            <p class="font-bold text-xl text-gray-800">{{ expiringLeases }}</p>
          </div>
        </div>
      </div>
    </div>

    <div class="bg-white rounded-xl shadow-md overflow-hidden">
      <div class="border-b border-gray-100 px-6 py-4">
        <h2 class="text-lg font-semibold text-gray-800">Find Leases</h2>
        <p class="text-sm text-gray-500">Search and filter lease agreements</p>
      </div>

      <div class="p-6">
        <div class="grid grid-cols-1 sm:grid-cols-3 gap-6">
          <div class="sm:col-span-2 space-y-2">
            <label for="search" class="block text-sm font-medium text-gray-700">Search Leases</label>
            <div class="relative">
              <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
                </svg>
              </div>
              <input
                  id="search"
                  v-model="searchQuery"
                  type="text"
                  placeholder="Search by lease ID, tenant ID or property ID"
                  class="block w-full pl-10 pr-3 py-2.5 text-gray-700 bg-white border border-gray-300 rounded-xl shadow-sm focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
              />
            </div>
          </div>

          <!-- Status Filter -->
          <div class="space-y-2">
            <label for="status" class="block text-sm font-medium text-gray-700">Lease Status</label>
            <div class="relative">
              <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 4a1 1 0 011-1h16a1 1 0 011 1v2.586a1 1 0 01-.293.707l-6.414 6.414a1 1 0 00-.293.707V17l-4 4v-6.586a1 1 0 00-.293-.707L3.293 7.293A1 1 0 013 6.586V4z" />
                </svg>
              </div>
              <select
                  id="status"
                  v-model="selectedStatus"
                  class="block w-full pl-10 pr-10 py-2.5 text-gray-700 bg-white border border-gray-300 rounded-xl shadow-sm focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm appearance-none"
              >
                <option value="ALL">All Statuses</option>
                <option value="ACTIVE">Active</option>
                <option value="PENDING">Pending</option>
                <option value="TERMINATED">Terminated</option>
              </select>
              <div class="absolute inset-y-0 right-0 flex items-center px-2 pointer-events-none">
                <svg class="h-5 w-5 text-gray-400" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" aria-hidden="true">
                  <path fill-rule="evenodd" d="M5.293 7.293a1 1 0 011.414 0L10 10.586l3.293-3.293a1 1 0 111.414 1.414l-4 4a1 1 0 01-1.414 0l-4-4a1 1 0 010-1.414z" clip-rule="evenodd" />
                </svg>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="bg-white rounded-xl shadow-md p-10">
      <div class="flex flex-col items-center justify-center">
        <svg class="animate-spin h-10 w-10 text-indigo-600 mb-4" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
          <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
          <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
        </svg>
        <p class="text-gray-500 text-lg">Loading leases...</p>
      </div>
    </div>

    <!-- Error State -->
    <div v-else-if="error" class="bg-white rounded-xl shadow-md p-10">
      <div class="flex items-center justify-center">
        <div class="bg-red-100 border border-red-200 rounded-lg p-4 w-full max-w-2xl">
          <div class="flex">
            <div class="flex-shrink-0">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-red-500" viewBox="0 0 20 20" fill="currentColor">
                <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clip-rule="evenodd" />
              </svg>
            </div>
            <div class="ml-3">
              <p class="text-sm text-red-700">{{ error }}</p>
              <button class="mt-2 text-sm font-medium text-red-600 hover:text-red-500">
                Try Again
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Empty State -->
    <div v-else-if="filteredLeases.length === 0" class="bg-white rounded-xl shadow-md p-10">
      <div class="flex flex-col items-center justify-center">
        <svg xmlns="http://www.w3.org/2000/svg" class="h-16 w-16 text-gray-300 mb-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
        </svg>
        <p class="text-gray-600 font-medium mb-1">No leases found</p>
        <p class="text-gray-500 text-sm">Try adjusting your filters or search criteria</p>
      </div>
    </div>

    <!-- Lease Table -->
    <div v-else class="bg-white rounded-xl shadow-md overflow-hidden">
      <div class="border-b border-gray-100 px-6 py-4">
        <h2 class="text-lg font-semibold text-gray-800">Lease Agreements</h2>
        <p class="text-sm text-gray-500">{{ filteredLeases.length }} leases found</p>
      </div>

      <div class="overflow-x-auto">
        <table class="min-w-full table-auto">
          <thead>
          <tr class="bg-gray-50 border-b border-gray-200">
            <th class="px-6 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">Lease ID</th>
            <th class="px-6 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">Tenant</th>
            <th class="px-6 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">Property</th>
            <th class="px-6 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">Duration</th>
            <th class="px-6 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">Status</th>
          </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-100">
          <tr
              v-for="lease in filteredLeases"
              :key="lease.leaseId"
              class="hover:bg-gray-50 transition-colors"
          >
            <td class="px-6 py-4 whitespace-nowrap">
              <div class="text-sm font-medium text-gray-900">{{ lease.leaseId }}</div>
            </td>
            <td class="px-6 py-4 whitespace-nowrap">
              <div class="flex items-center">
                <div class="h-8 w-8 flex-shrink-0 mr-3">
                  <div class="h-full w-full rounded-full bg-indigo-100 flex items-center justify-center">
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 text-indigo-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                    </svg>
                  </div>
                </div>
                <div>
                  <div class="text-sm font-medium text-gray-900">{{ lease.tenantId || 'N/A' }}</div>
                  <div class="text-xs text-gray-500">Tenant ID</div>
                </div>
              </div>
            </td>
            <td class="px-6 py-4 whitespace-nowrap">
              <div class="flex items-center">
                <div class="h-8 w-8 flex-shrink-0 mr-3">
                  <div class="h-full w-full rounded-full bg-blue-100 flex items-center justify-center">
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 text-blue-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6" />
                    </svg>
                  </div>
                </div>
                <div>
                  <div class="text-sm font-medium text-gray-900">{{ lease.propertyId || 'N/A' }}</div>
                  <div class="text-xs text-gray-500">Property ID</div>
                </div>
              </div>
            </td>
            <td class="px-6 py-4 whitespace-nowrap">
              <div class="text-sm text-gray-900">{{ new Date(lease.startDate).toLocaleDateString() }}</div>
              <div class="text-xs text-gray-500">to {{ new Date(lease.endDate).toLocaleDateString() }}</div>
            </td>
            <td class="px-6 py-4 whitespace-nowrap">
                <span
                    class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium"
                    :class="getStatusStyle(lease.status)"
                >
                  <span :class="getStatusDot(lease.status)" class="w-1.5 h-1.5 mr-1.5 rounded-full"></span>
                  {{ lease.status }}
                </span>
            </td>
<!--            <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">-->
<!--              <button class="text-indigo-600 hover:text-indigo-900 mr-3">View</button>-->
<!--              <button class="text-gray-600 hover:text-gray-900">Edit</button>-->
<!--            </td>-->
          </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<style scoped>
table {
  border-collapse: separate;
  border-spacing: 0;
}

thead th:first-child {
  border-top-left-radius: 0.5rem;
}

thead th:last-child {
  border-top-right-radius: 0.5rem;
}

tbody tr:last-child td:first-child {
  border-bottom-left-radius: 0.5rem;
}

tbody tr:last-child td:last-child {
  border-bottom-right-radius: 0.5rem;
}
</style>