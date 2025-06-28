<script setup>
import {onMounted, onUnmounted, ref} from 'vue'
import { useLandlordDashboardStore } from "@/stores/landlord/dashboardStore.js";
import MonthlySummary from '@/components/landlord/dashboard/MonthlySummary.vue'
import RecentActivity from '@/components/landlord/dashboard/RecentActivity.vue'
import UpcomingDeadlines from '@/components/landlord/dashboard/UpcomingDeadlines.vue'
import FinancialOverview from '@/components/landlord/dashboard/FinancialOverview.vue'
import ActivityModal from '@/components/landlord/dashboard/ActivityModal.vue'
import CalendarModal from '@/components/landlord/dashboard/CalendarModal.vue'

const store = useLandlordDashboardStore()
const currentTime = ref(new Date())

const formatDate = (date) => {
  return date.toLocaleDateString('en-US', {
    weekday: 'long',
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  });
}

// Safe formatting functions to prevent errors
const formatCurrency = (value) => {
  return (value || 0).toFixed(0)
}

onMounted(() => {
  store.initDashboard()

  const timeInterval = setInterval(() => {
    currentTime.value = new Date()
  }, 60000)

  onUnmounted(() => {
    clearInterval(timeInterval)
  })
})
</script>

<template>
  <div class="max-w-[1400px] mx-auto px-6 py-10 space-y-8">
    <!-- Dashboard Header with gradient background (Consistent with other components) -->
    <div class="bg-gradient-to-r from-blue-600 to-indigo-700 rounded-2xl shadow-lg">
      <div class="px-8 py-6">
        <div class="flex flex-col md:flex-row justify-between items-start md:items-center gap-4">
          <div class="flex items-center gap-4">
            <div class="bg-white/20 p-3 rounded-xl">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-8 w-8 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2V6zM14 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2V6zM4 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2v-2zM14 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2v-2z" />
              </svg>
            </div>
            <div>
              <h1 class="text-3xl font-bold text-white">Landlord Dashboard</h1>
              <p class="text-blue-100 mt-1">Manage your properties and analytics</p>
            </div>
          </div>

          <div class="flex items-center text-white bg-white/10 px-4 py-2 rounded-lg backdrop-blur-sm">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
            <span class="text-sm">Last updated: {{ formatDate(currentTime) }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- Dashboard Quick Stats Cards - ALL TIME DATA -->
    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
      <!-- Total Properties Card (replaces Occupancy Rate) -->
      <div class="rounded-xl bg-gradient-to-r from-indigo-50 to-blue-50 border border-indigo-100 overflow-hidden">
        <div class="px-6 py-5 flex justify-between items-center">
          <div>
            <p class="text-sm text-indigo-700 font-medium">Total Properties</p>
            <div class="mt-1">
              <p class="text-2xl font-bold text-gray-800">{{ store.totalProperties || 0 }}</p>
            </div>
            <p class="text-xs text-indigo-600 mt-1">Total Properties</p>
          </div>
          <div class="h-12 w-12 flex items-center justify-center rounded-lg bg-indigo-100">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-indigo-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-4m-5 0H9m0 0H5m14 0V9a2 2 0 00-2-2M9 7h6m-6 4h6m-2 4h.01" />
            </svg>
          </div>
        </div>
        <!-- Progress Bar -->
        <div class="w-full bg-indigo-200 h-1.5">
          <div class="bg-indigo-500 h-1.5" style="width: 100%"></div>
        </div>
      </div>

      <!-- Total Rent Collected Card -->
      <div class="rounded-xl bg-gradient-to-r from-green-50 to-emerald-50 border border-green-100 overflow-hidden">
        <div class="px-6 py-5 flex justify-between items-center">
          <div>
            <p class="text-sm text-green-700 font-medium">Rent Collected</p>
            <div class="mt-1">
              <p class="text-2xl font-bold text-gray-800">${{ formatCurrency(store.totalRentCollected) }}</p>
            </div>
            <p class="text-xs text-green-600 mt-1">All time</p>
          </div>
          <div class="h-12 w-12 flex items-center justify-center rounded-lg bg-green-100">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-green-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1" />
            </svg>
          </div>
        </div>
        <!-- Progress Bar -->
        <div class="w-full bg-green-200 h-1.5">
          <div class="bg-green-500 h-1.5" style="width: 100%"></div>
        </div>
      </div>

      <!-- Total Maintenance Costs Card -->
      <div class="rounded-xl bg-gradient-to-r from-orange-50 to-amber-50 border border-orange-100 overflow-hidden">
        <div class="px-6 py-5 flex justify-between items-center">
          <div>
            <p class="text-sm text-orange-700 font-medium">Maintenance Costs</p>
            <div class="mt-1">
              <p class="text-2xl font-bold text-gray-800">${{ formatCurrency(store.totalMaintenanceCosts) }}</p>
            </div>
            <p class="text-xs text-orange-600 mt-1">All time</p>
          </div>
          <div class="h-12 w-12 flex items-center justify-center rounded-lg bg-orange-100">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-orange-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z" />
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
            </svg>
          </div>
        </div>
        <!-- Progress Bar -->
        <div class="w-full bg-orange-200 h-1.5">
          <div class="bg-orange-500 h-1.5" style="width: 100%"></div>
        </div>
      </div>

      <!-- Total Net Income Card -->
      <div class="rounded-xl bg-gradient-to-r from-blue-50 to-cyan-50 border border-blue-100 overflow-hidden">
        <div class="px-6 py-5 flex justify-between items-center">
          <div>
            <p class="text-sm text-blue-700 font-medium">Net Income</p>
            <div class="mt-1">
              <p class="text-2xl font-bold text-gray-800">${{ formatCurrency(store.totalNetIncome) }}</p>
            </div>
            <p class="text-xs text-blue-600 mt-1">All time</p>
          </div>
          <div class="h-12 w-12 flex items-center justify-center rounded-lg bg-blue-100">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-blue-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z" />
            </svg>
          </div>
        </div>
        <!-- Progress Bar -->
        <div class="w-full bg-blue-200 h-1.5">
          <div class="bg-blue-500 h-1.5" style="width: 82%"></div>
        </div>
      </div>
    </div>

    <!-- Main Dashboard Content -->
    <div class="grid grid-cols-12 gap-6">
      <!-- Monthly Summary -->
      <MonthlySummary
          :rent-payments-last-month="store.rentPaymentsLastMonth"
          :occupancy-rate="store.occupancyRate"
          :vacant-units="store.vacantUnits"
          :maintenance-cost-this-month="store.maintenanceCostThisMonth"
      />

      <!-- Recent Activity - Always show, component handles empty state -->
      <RecentActivity
          :activities="store.activities || []"
          @view-all="store.showActivityModal = true"
      />

      <UpcomingDeadlines
          :calendar-events="store.calendarEvents || []"
          @view-calendar="store.showCalendar = true"
      />

      <!-- Financial Overview -->
      <FinancialOverview
          :range-options="store.rangeOptions"
          :active-range="store.activeRange"
          :filtered-chart-data="store.filteredChartData"
          :chart-options="store.chartOptions"
          @change-range="store.activeRange = $event"
      />
    </div>

    <!-- Modals -->
    <ActivityModal
        v-if="store.showActivityModal"
        :activities="store.activities"
        @close="store.showActivityModal = false"
    />

    <CalendarModal
        v-if="store.showCalendar"
        :calendar-events="store.calendarEvents"
        @close="store.showCalendar = false"
    />
  </div>
</template>