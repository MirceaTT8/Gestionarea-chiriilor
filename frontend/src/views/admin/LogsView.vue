<script setup>
import { onMounted, ref } from 'vue'
import { useLogStore } from '@/stores/admin/logStore.js'
import LogFilters from '@/components/admin/log/LogFilters.vue'
import LogTable from '@/components/admin/log/LogTable.vue'

const logStore = useLogStore()
const loading = ref(true)

onMounted(async () => {
  try {
    await logStore.fetchLogs()
  } finally {
    loading.value = false
  }
})

</script>

<template>
  <div class="max-w-7xl mx-auto px-6 py-10">
    <!-- Header with gradient background -->
    <div class="bg-gradient-to-r from-cyan-600 to-blue-600 rounded-2xl shadow-lg mb-8">
      <div class="px-8 py-6">
        <div class="flex flex-col md:flex-row justify-between items-start md:items-center gap-4">
          <div class="flex items-center gap-4">
            <div class="bg-white/20 p-3 rounded-xl">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-8 w-8 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
              </svg>
            </div>
            <div>
              <h1 class="text-3xl font-bold text-white">Activity Logs</h1>
              <p class="text-blue-100 mt-1">Track and monitor system activity</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Stats cards -->
    <div class="grid grid-cols-1 md:grid-cols-4 gap-6 mb-8">
      <div class="bg-white rounded-xl shadow-md p-6 transition-all hover:shadow-lg">
        <div class="flex items-center gap-4">
          <div class="bg-blue-100 p-3 rounded-lg">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-blue-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 8h10M7 12h4m1 8l-4-4H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-3l-4 4z" />
            </svg>
          </div>
          <div>
            <h3 class="text-gray-500 text-sm font-medium">Total Logs</h3>
            <p class="font-bold text-2xl text-gray-800">{{ logStore.totalLogs.toLocaleString() }}</p>
          </div>
        </div>
      </div>

<!--      <div class="bg-white rounded-xl shadow-md p-6 transition-all hover:shadow-lg">-->
<!--        <div class="flex items-center gap-4">-->
<!--          <div class="bg-red-100 p-3 rounded-lg">-->
<!--            <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-red-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">-->
<!--              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />-->
<!--            </svg>-->
<!--          </div>-->
<!--          <div>-->
<!--            <h3 class="text-gray-500 text-sm font-medium">Error Logs</h3>-->
<!--            <p class="font-bold text-2xl text-gray-800">{{ logStore.errorCount.toLocaleString() }}</p>-->
<!--          </div>-->
<!--        </div>-->
<!--      </div>-->

<!--      <div class="bg-white rounded-xl shadow-md p-6 transition-all hover:shadow-lg">-->
<!--        <div class="flex items-center gap-4">-->
<!--          <div class="bg-yellow-100 p-3 rounded-lg">-->
<!--            <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-yellow-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">-->
<!--              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />-->
<!--            </svg>-->
<!--          </div>-->
<!--          <div>-->
<!--            <h3 class="text-gray-500 text-sm font-medium">Warning Logs</h3>-->
<!--            <p class="font-bold text-2xl text-gray-800">{{ logStore.warningCount.toLocaleString() }}</p>-->
<!--          </div>-->
<!--        </div>-->
<!--      </div>-->

      <div class="bg-white rounded-xl shadow-md p-6 transition-all hover:shadow-lg">
        <div class="flex items-center gap-4">
          <div class="bg-green-100 p-3 rounded-lg">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-green-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
          </div>
          <div>
            <h3 class="text-gray-500 text-sm font-medium">Info Logs</h3>
            <p class="font-bold text-2xl text-gray-800">{{ logStore.infoCount.toLocaleString() }}</p>
          </div>
        </div>
      </div>
    </div>

    <div class="bg-white rounded-xl shadow-md mb-8 overflow-hidden">
      <div class="border-b border-gray-100 px-6 py-4">
        <h2 class="text-lg font-semibold text-gray-800">Filter Logs</h2>
      </div>
      <div class="p-6">
        <LogFilters />
      </div>
    </div>

    <!-- Loading -->
    <div v-if="loading" class="bg-white rounded-xl shadow-md p-10">
      <div class="flex flex-col items-center justify-center">
        <svg class="animate-spin h-10 w-10 text-blue-600 mb-4" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
          <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
          <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
        </svg>
        <p class="text-gray-500 text-lg">Loading logs...</p>
      </div>
    </div>

    <!-- Logs Table with improved styling -->
    <div v-else class="bg-white rounded-xl shadow-md overflow-hidden mb-8">
      <div class="border-b border-gray-100 px-6 py-4 flex justify-between items-center">
        <div>
          <h2 class="text-lg font-semibold text-gray-800">Activity Logs</h2>
          <p class="text-sm text-gray-500">
            Showing {{ logStore.paginatedLogs.length }} of {{ logStore.filteredLogs.length }} logs
            {{ logStore.filteredLogs.length !== logStore.totalLogs ? `(filtered from ${logStore.totalLogs} total)` : '' }}
          </p>
        </div>
        <div v-if="logStore.filteredLogs.length !== logStore.totalLogs" class="text-sm bg-blue-100 text-blue-800 px-3 py-1 rounded-full">
          Filtered Results
        </div>
      </div>

      <!-- Empty State -->
      <div v-if="logStore.filteredLogs.length === 0" class="text-center py-12 px-6">
        <div class="mx-auto w-16 h-16 bg-gray-100 rounded-full flex items-center justify-center mb-4">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-8 w-8 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
          </svg>
        </div>
        <h3 class="text-lg font-medium text-gray-900 mb-2">No logs found</h3>
        <p class="text-gray-500 mb-4">No logs match your current filter criteria.</p>
        <button
            @click="logStore.resetFilters"
            class="inline-flex items-center px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white font-medium rounded-lg transition-colors"
        >
          Clear Filters
        </button>
      </div>

      <!-- Table -->
      <div v-else>
        <LogTable :logs="logStore.paginatedLogs" @sort="logStore.sortBy" />
      </div>
    </div>

    <!-- Improved pagination -->
    <div v-if="logStore.totalPages > 1" class="flex justify-center mt-8 mb-4">
      <nav class="flex items-center space-x-2">
        <button
            @click="logStore.currentPage > 1 && (logStore.currentPage--)"
            :disabled="logStore.currentPage === 1"
            class="inline-flex items-center justify-center w-10 h-10 rounded-lg text-gray-500 bg-white border border-gray-300 disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-50 transition-colors"
            :class="{ 'opacity-50 cursor-not-allowed': logStore.currentPage === 1 }"
        >
          <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
            <path fill-rule="evenodd" d="M12.707 5.293a1 1 0 010 1.414L9.414 10l3.293 3.293a1 1 0 01-1.414 1.414l-4-4a1 1 0 010-1.414l4-4a1 1 0 011.414 0z" clip-rule="evenodd" />
          </svg>
        </button>

        <template v-if="logStore.totalPages <= 7">
          <button
              v-for="page in logStore.totalPages"
              :key="page"
              @click="logStore.currentPage = page"
              class="inline-flex items-center justify-center w-10 h-10 rounded-lg text-sm font-medium transition-all border"
              :class="page === logStore.currentPage ? 'bg-blue-600 text-white border-blue-600' : 'bg-white text-gray-700 border-gray-300 hover:bg-gray-50'"
          >
            {{ page }}
          </button>
        </template>

        <template v-else>
          <!-- First page -->
          <button
              @click="logStore.currentPage = 1"
              class="inline-flex items-center justify-center w-10 h-10 rounded-lg text-sm font-medium transition-all border"
              :class="1 === logStore.currentPage ? 'bg-blue-600 text-white border-blue-600' : 'bg-white text-gray-700 border-gray-300 hover:bg-gray-50'"
          >
            1
          </button>

          <!-- Ellipsis if needed -->
          <span v-if="logStore.currentPage > 3" class="inline-flex items-center justify-center w-10 h-10 text-gray-500">...</span>

          <!-- Middle pages -->
          <button
              v-for="page in logStore.middlePages"
              :key="page"
              @click="logStore.currentPage = page"
              class="inline-flex items-center justify-center w-10 h-10 rounded-lg text-sm font-medium transition-all border"
              :class="page === logStore.currentPage ? 'bg-blue-600 text-white border-blue-600' : 'bg-white text-gray-700 border-gray-300 hover:bg-gray-50'"
          >
            {{ page }}
          </button>

          <!-- Ellipsis if needed -->
          <span v-if="logStore.currentPage < logStore.totalPages - 2" class="inline-flex items-center justify-center w-10 h-10 text-gray-500">...</span>

          <!-- Last page -->
          <button
              @click="logStore.currentPage = logStore.totalPages"
              class="inline-flex items-center justify-center w-10 h-10 rounded-lg text-sm font-medium transition-all border"
              :class="logStore.totalPages === logStore.currentPage ? 'bg-blue-600 text-white border-blue-600' : 'bg-white text-gray-700 border-gray-300 hover:bg-gray-50'"
          >
            {{ logStore.totalPages }}
          </button>
        </template>

        <button
            @click="logStore.currentPage < logStore.totalPages && (logStore.currentPage++)"
            :disabled="logStore.currentPage === logStore.totalPages"
            class="inline-flex items-center justify-center w-10 h-10 rounded-lg text-gray-500 bg-white border border-gray-300 disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-50 transition-colors"
            :class="{ 'opacity-50 cursor-not-allowed': logStore.currentPage === logStore.totalPages }"
        >
          <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
            <path fill-rule="evenodd" d="M7.293 14.707a1 1 0 010-1.414L10.586 10 7.293 6.707a1 1 0 011.414-1.414l4 4a1 1 0 010 1.414l-4 4a1 1 0 01-1.414 0z" clip-rule="evenodd" />
          </svg>
        </button>
      </nav>
    </div>

    <!-- Results summary -->
    <div v-if="!loading && logStore.filteredLogs.length > 0" class="text-center text-sm text-gray-500 mt-4">
      Page {{ logStore.currentPage }} of {{ logStore.totalPages }}
      ({{ ((logStore.currentPage - 1) * 20 + 1).toLocaleString() }} - {{ Math.min(logStore.currentPage * 20, logStore.filteredLogs.length).toLocaleString() }} of {{ logStore.filteredLogs.length.toLocaleString() }} logs)
    </div>
  </div>
</template>

<style scoped>
@import url('https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css');
</style>