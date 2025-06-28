<script setup>
import { computed } from 'vue'

const props = defineProps({
  statsData: {
    type: Object,
    default: () => ({
      totalUsers: 0,
      userGrowth: 0,
      totalProperties: 0,
      propertyGrowth: 0,
      maintenanceRequests: 0,
      urgentRequests: 0
    })
  },
  loading: {
    type: Boolean,
    default: false
  }
})

const stats = computed(() => props.statsData)

</script>

<template>
  <div class="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
    <!-- Total Users Card -->
    <div class="bg-white rounded-xl shadow-md overflow-hidden transition-all hover:shadow-lg">
      <div class="bg-purple-100 px-6 py-4 flex justify-between items-center">
        <h3 class="text-purple-800 font-semibold">Total Users</h3>
        <div class="bg-purple-500 p-2 rounded-lg">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197m13.5-9a2.5 2.5 0 11-5 0 2.5 2.5 0 015 0z" />
          </svg>
        </div>
      </div>
      <div class="p-6">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-3xl font-bold text-gray-900" :class="{ 'animate-pulse bg-gray-200 rounded w-16 h-8': loading }">
              <span v-if="!loading">{{ stats.totalUsers?.toLocaleString() || '0' }}</span>
            </p>
            <p class="text-sm text-gray-600 mt-1">Registered users</p>
          </div>
        </div>
      </div>
    </div>

    <!-- Total Properties Card -->
    <div class="bg-white rounded-xl shadow-md overflow-hidden transition-all hover:shadow-lg">
      <div class="bg-cyan-100 px-6 py-4 flex justify-between items-center">
        <h3 class="text-cyan-800 font-semibold">Total Properties</h3>
        <div class="bg-cyan-500 p-2 rounded-lg">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4" />
          </svg>
        </div>
      </div>
      <div class="p-6">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-3xl font-bold text-gray-900" :class="{ 'animate-pulse bg-gray-200 rounded w-16 h-8': loading }">
              <span v-if="!loading">{{ stats.totalProperties?.toLocaleString() || '0' }}</span>
            </p>
            <p class="text-sm text-gray-600 mt-1">Listed properties</p>
          </div>
        </div>
      </div>
    </div>

    <!-- Maintenance Requests Card -->
    <div class="bg-white rounded-xl shadow-md overflow-hidden transition-all hover:shadow-lg">
      <div class="bg-orange-100 px-6 py-4 flex justify-between items-center">
        <h3 class="text-orange-800 font-semibold">Maintenance Requests</h3>
        <div class="bg-orange-500 p-2 rounded-lg">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z" />
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
          </svg>
        </div>
      </div>
      <div class="p-6">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-3xl font-bold text-gray-900" :class="{ 'animate-pulse bg-gray-200 rounded w-16 h-8': loading }">
              <span v-if="!loading">{{ stats.maintenanceRequests?.toLocaleString() || '0' }}</span>
            </p>
            <p class="text-sm text-gray-600 mt-1">Maintenance Requests</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

