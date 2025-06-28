<script setup>
import { ref, onMounted, computed } from 'vue'
import { useLeaseStore } from '@/stores/landlord/leaseStore.js'
import LeaseList from '@/components/landlord/lease/LeaseList.vue';
import LeaseSummaryCards from '@/components/landlord/lease/LeaseSummaryCards.vue';
import LeaseTabs from '@/components/landlord/lease/LeaseTabs.vue';
import LeaseCreateModal from "@/components/landlord/lease/LeaseCreateModal.vue";
import { getReviewedLeaseIds } from '@/services/reviewService'

const leaseStore = useLeaseStore()
const activeTab = ref('ACTIVE')
const showCreateModal = ref(false)
const showSuccessMessage = ref(false)
const successMessage = ref('')
const reviewedLeases = ref([])
onMounted(async () => {
  await leaseStore.loadLeasesAndData()
  reviewedLeases.value = await getReviewedLeaseIds()
})

const filteredLeases = computed(() => {
  return activeTab.value === 'all'
      ? leaseStore.leases
      : leaseStore.leases.filter(lease => lease.status === activeTab.value)
})

const totalMonthlyRent = computed(() => {
  return leaseStore.leases
      .filter(l => l.status === 'ACTIVE')
      .reduce((sum, lease) => sum + lease.monthlyRent, 0)
})

const activeLeasesCount = computed(() => {
  return leaseStore.leases.filter(l => l.status.toUpperCase() === 'ACTIVE').length
})

const handleTerminate = (leaseId) => {
  console.log('Terminating lease:', leaseId)
}

const handleReviewSubmitted = async () => {
  successMessage.value = 'Review submitted successfully!'
  showSuccessMessage.value = true

  reviewedLeases.value = await getReviewedLeaseIds()

  setTimeout(() => {
    showSuccessMessage.value = false
  }, 5000)
}

const hideSuccessMessage = () => {
  showSuccessMessage.value = false
}

</script>

<template>
  <div class="max-w-6xl mx-auto px-6 py-10 min-h-screen bg-gray-50">
    <!-- Success Message -->
    <div v-if="showSuccessMessage" class="fixed top-4 right-4 z-50">
      <div class="bg-green-100 border border-green-400 text-green-700 px-4 py-3 rounded-lg shadow-lg flex items-center max-w-md">
        <svg class="w-5 h-5 mr-2 flex-shrink-0" fill="currentColor" viewBox="0 0 20 20">
          <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd"/>
        </svg>
        <span>{{ successMessage }}</span>
        <button @click="hideSuccessMessage" class="ml-3 text-green-500 hover:text-green-700">
          <svg class="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
            <path fill-rule="evenodd" d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z" clip-rule="evenodd"/>
          </svg>
        </button>
      </div>
    </div>

    <div class="bg-gradient-to-r from-blue-600 to-indigo-700 rounded-2xl shadow-lg mb-8">
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
              <p class="text-blue-100 mt-1">Manage all your property leases in one place</p>
            </div>
          </div>

          <button
              @click="showCreateModal = true"
              class="bg-white/20 hover:bg-white/30 text-white px-5 py-2.5 rounded-lg transition-colors flex items-center gap-2 font-medium shadow-sm border border-white/20"
          >
            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
              <path fill-rule="evenodd" d="M10 3a1 1 0 011 1v5h5a1 1 0 110 2h-5v5a1 1 0 11-2 0v-5H4a1 1 0 110-2h5V4a1 1 0 011-1z" clip-rule="evenodd" />
            </svg>
            Add New Lease
          </button>
        </div>
      </div>
    </div>

    <LeaseCreateModal
        v-if="showCreateModal"
        @cancel="showCreateModal = false"
        @success="() => {
        showCreateModal = false
        leaseStore.loadLeasesAndData()
      }"
    />

    <div v-if="leaseStore.loading" class="flex flex-col items-center justify-center py-32">
      <div class="w-12 h-12 border-4 border-gray-200 border-t-green-500 rounded-full animate-spin mb-4"></div>
      <p class="text-gray-600 font-medium">Loading your leases...</p>
    </div>

    <div v-else-if="leaseStore.error" class="bg-red-50 p-8 rounded-xl shadow-sm text-center my-10">
      <div class="inline-block p-3 bg-red-100 rounded-full mb-4">
        <svg xmlns="http://www.w3.org/2000/svg" class="h-8 w-8 text-red-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
        </svg>
      </div>
      <h3 class="text-lg font-semibold text-red-700 mb-2">Unable to load leases</h3>
      <p class="text-red-600 mb-6">{{ leaseStore.error }}</p>
      <button
          @click="$router.go(0)"
          class="bg-white hover:bg-gray-100 text-gray-800 px-5 py-2.5 rounded-lg transition-colors shadow-sm font-medium border border-gray-200"
      >
        Try Again
      </button>
    </div>

    <div v-else class="space-y-8">
      <LeaseSummaryCards
          :total-leases="leaseStore.leases.length"
          :active-leases="activeLeasesCount"
          :monthly-revenue="totalMonthlyRent"
      />

      <div class="bg-white rounded-xl shadow-sm p-6">
        <LeaseTabs
            v-model:active-tab="activeTab"
            :tabs="[
            { value: 'ACTIVE', label: 'Active' },
            { value: 'TERMINATED', label: 'Terminated' },
            { value: 'all', label: 'All Leases' }
          ]"
        />

        <div class="mt-6">
          <LeaseList
              v-if="filteredLeases.length > 0"
              :leases="filteredLeases"
              :properties="leaseStore.properties"
              :tenants="leaseStore.tenants"
              :reviewed-leases="reviewedLeases"
              :status-colors="{
              ACTIVE: { bg: 'bg-green-100', text: 'text-green-700' },
              TERMINATED: { bg: 'bg-red-100', text: 'text-red-700' },
              PENDING: { bg: 'bg-yellow-100', text: 'text-yellow-700' }
            }"
              @terminate="handleTerminate"
              @review-submitted="handleReviewSubmitted"
          />

          <div v-else class="bg-gray-50 p-12 rounded-lg text-center border border-gray-100">
            <div class="mx-auto w-16 h-16 bg-gray-100 rounded-full flex items-center justify-center mb-4">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-8 w-8 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
              </svg>
            </div>
            <h3 class="text-xl font-semibold text-gray-800 mb-2">No leases found</h3>
            <p class="text-gray-600 mb-6">You don't have any {{ activeTab.toLowerCase() === 'all' ? '' : activeTab.toLowerCase() }} leases yet.</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>