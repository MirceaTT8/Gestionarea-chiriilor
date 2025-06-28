
<script setup>
import { ref, computed } from 'vue'
import { Swiper, SwiperSlide } from 'swiper/vue'
import 'swiper/css'
import 'swiper/css/navigation'
import 'swiper/css/pagination'
import SwiperCore from 'swiper'
import { Navigation, Pagination } from 'swiper/modules'
import { setMaintenanceCost } from '@/services/maintenanceService.js'
import { getTenantNameByLeaseId, getPropertyNameByLeaseId} from "@/utils/leaseNameUtils.js";
import {formatCurrencyCompact, formatDate, formatTimeAgo} from "@/utils/formatters.js";


SwiperCore.use([Navigation, Pagination])

const emit = defineEmits(['update-status'])

const { request, leases, tenants, properties } = defineProps({
  request: Object,
  leases: Array,
  tenants: Array,
  properties: Array
});

const statusDisplay = {
  PENDING: {
    bg: 'bg-gradient-to-br from-amber-50 to-orange-50',
    text: 'text-amber-700',
    border: 'border-amber-300',
    accent: 'bg-amber-400',
    icon: '<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />'
  },
  IN_PROGRESS: {
    bg: 'bg-gradient-to-br from-blue-50 to-indigo-50',
    text: 'text-blue-700',
    border: 'border-blue-300',
    accent: 'bg-blue-400',
    icon: '<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />'
  },
  COMPLETED: {
    bg: 'bg-gradient-to-br from-emerald-50 to-green-50',
    text: 'text-emerald-700',
    border: 'border-emerald-300',
    accent: 'bg-emerald-400',
    icon: '<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />'
  },
  CANCELLED: {
    bg: 'bg-gradient-to-br from-red-50 to-rose-50',
    text: 'text-red-700',
    border: 'border-red-300',
    accent: 'bg-red-400',
    icon: '<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 14l2-2m0 0l2-2m-2 2l-2-2m2 2l2 2m7-2a9 9 0 11-18 0 9 9 0 0118 0z" />'
  }
}

const cost = ref(request.cost || '')
const isSaving = ref(false)
const saveError = ref(null)
const isCostSaved = computed(() => !!request.cost)


const isRecentlyUpdated = (updatedAt) => {
  const updated = new Date(updatedAt)
  const now = new Date()
  return (now - updated) / (1000 * 60 * 60) < 24
}

const showModal = ref(false)
const activeIndex = ref(0)

const openSlider = (index) => {
  activeIndex.value = index
  showModal.value = true
}

const saveCost = async () => {
  if (!cost.value || isNaN(cost.value)) {
    saveError.value = 'Please enter a valid number'
    return
  }

  try {
    isSaving.value = true
    saveError.value = null
    await setMaintenanceCost(request.requestId, parseFloat(cost.value))
    request.cost = parseFloat(cost.value) // mark as saved in UI
  } catch (err) {
    saveError.value = 'Failed to save cost'
    console.error(err)
  } finally {
    isSaving.value = false
  }
}

</script>

<template>
  <div class="bg-white rounded-xl overflow-hidden border border-gray-200 shadow-sm hover:shadow-lg transition-all duration-200 flex flex-col h-full">

    <!-- Card Header with Status -->
    <div class="px-6 py-4 relative" :class="statusDisplay[request.status].bg">

      <!-- Status Badge -->
      <div class="flex justify-end mb-2">
        <div class="inline-flex items-center gap-2 px-4 py-2 rounded-full text-sm font-semibold capitalize backdrop-blur-sm shadow-lg border"
             :class="[statusDisplay[request.status].text, statusDisplay[request.status].border, 'bg-white/80']">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" v-html="statusDisplay[request.status].icon"></svg>
          {{ request.status.replace('_', ' ').toLowerCase() }}
        </div>
      </div>

      <!-- Active Status Indicator for IN_PROGRESS -->
      <div v-if="request.status === 'IN_PROGRESS'" class="absolute top-4 right-4">
        <div class="flex h-3 w-3">
          <span class="animate-ping absolute inline-flex h-full w-full rounded-full opacity-75" :class="statusDisplay[request.status].accent"></span>
          <span class="relative inline-flex rounded-full h-3 w-3 shadow-sm" :class="statusDisplay[request.status].accent"></span>
        </div>
      </div>

      <!-- Property and Tenant Info -->
      <div class="pt-2">
        <h3 class="text-lg font-semibold text-gray-900 line-clamp-1 mb-2">
          {{ getPropertyNameByLeaseId(request.leaseId, leases, properties) }}
        </h3>

        <div class="inline-flex items-center px-3 py-1.5 bg-white/70 backdrop-blur-sm rounded-lg text-sm text-gray-700 border border-white/50 shadow-sm">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-2 text-gray-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
          </svg>
          <span class="font-medium">{{ getTenantNameByLeaseId(request.leaseId, leases, tenants) }}</span>
        </div>
      </div>

      <!-- Recently Updated Indicator -->
      <div v-if="isRecentlyUpdated(request.updatedAt)" class="absolute top-4 left-4">
        <div class="bg-green-500 text-white text-xs px-2 py-1 rounded-full font-medium shadow-md animate-pulse">
          New
        </div>
      </div>

    </div>

    <!-- Card Body -->
    <div class="px-6 py-4 flex-grow">

      <div class="mb-4">
        <p class="text-sm text-gray-600 line-clamp-3">{{ request.description }}</p>
      </div>

      <div class="grid grid-cols-2 gap-4 mb-4">
        <div class="bg-gray-50 rounded-lg p-3 border border-gray-100">
          <h4 class="text-xs font-bold text-gray-500 uppercase tracking-wide mb-1">Submitted</h4>
          <div class="flex items-center">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 text-blue-500 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
            </svg>
            <span class="text-sm font-medium text-gray-700">{{ formatTimeAgo(request.createdAt) }}</span>
          </div>
        </div>

        <div class="bg-gray-50 rounded-lg p-3 border border-gray-100">
          <h4 class="text-xs font-bold text-gray-500 uppercase tracking-wide mb-1">Updated</h4>
          <div class="flex items-center">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 text-green-500 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
            <span class="text-sm font-medium text-gray-700">{{ formatTimeAgo(request.updatedAt) }}</span>
          </div>
        </div>
      </div>

      <!-- Images Section -->
      <div v-if="request.imageUrls?.length" class="mt-4">
        <div class="flex items-center justify-between">
          <span class="bg-purple-100 text-purple-700 text-xs px-2 py-1 rounded-full font-bold">
            {{ request.imageUrls.length }} photo{{ request.imageUrls.length > 1 ? 's' : '' }}
          </span>

          <!-- View Images Button -->
          <button
              @click="openSlider(0)"
              class="inline-flex items-center gap-2 bg-purple-600 hover:bg-purple-700 text-white text-xs font-semibold px-3 py-1.5 rounded-lg transition-all duration-200 shadow-sm hover:shadow-md transform hover:scale-105"
          >
            <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
            </svg>
            View Images
          </button>
        </div>
      </div>

      <!-- No Images State -->
      <div v-else class="mt-4 text-center py-6 bg-gray-50 rounded-lg border border-gray-200">
        <svg xmlns="http://www.w3.org/2000/svg" class="h-8 w-8 text-gray-400 mx-auto mb-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
        </svg>
        <p class="text-gray-500 text-sm">No images attached</p>
      </div>
    </div>

    <!-- Card Footer -->
    <div class="px-6 py-4 bg-gray-50 border-t border-gray-200">

      <!-- Status Update -->
      <div class="flex flex-col sm:flex-row sm:items-center gap-3 mb-4">
        <template v-if="['COMPLETED', 'CANCELLED'].includes(request.status)">
          <div class="flex-1 py-2 px-4 bg-gray-100 border border-gray-200 rounded-lg flex items-center text-gray-600 font-medium">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z" />
            </svg>
            Status is locked
          </div>
        </template>

        <div v-else class="relative flex-1">
          <select
              :value="request.status"
              @change="emit('update-status', request.requestId, $event.target.value)"
              class="block w-full py-2 pl-3 pr-10 border border-gray-300 rounded-lg bg-white shadow-sm text-sm focus:ring-2 focus:ring-blue-500 focus:border-blue-500 focus:outline-none transition appearance-none hover:border-gray-400"
          >
            <option value="PENDING">Pending</option>
            <option value="IN_PROGRESS">In Progress</option>
            <option value="COMPLETED">Completed</option>
            <option value="CANCELLED">Cancelled</option>
          </select>
          <div class="pointer-events-none absolute inset-y-0 right-0 flex items-center px-2 text-gray-700">
            <svg class="h-4 w-4" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor">
              <path fill-rule="evenodd" d="M5.293 7.293a1 1 0 011.414 0L10 10.586l3.293-3.293a1 1 0 111.414 1.414l-4 4a1 1 0 01-1.414 0l-4-4a1 1 0 010-1.414z" clip-rule="evenodd" />
            </svg>
          </div>
        </div>
      </div>

      <!-- Cost Input (for completed requests) -->
      <div v-if="request.status === 'COMPLETED'" class="space-y-2">
        <div v-if="!isCostSaved" class="flex gap-2 items-center">
          <div class="relative flex-1">
            <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
              <span class="text-gray-500 font-medium">$</span>
            </div>
            <input
                v-model="cost"
                type="number"
                placeholder="Enter cost"
                class="pl-7 w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 transition"
            />
          </div>
          <button
              @click="saveCost"
              :disabled="isSaving"
              class="bg-blue-600 hover:bg-blue-700 text-white text-sm font-semibold px-4 py-2 rounded-lg transition flex items-center whitespace-nowrap disabled:opacity-50 disabled:cursor-not-allowed shadow-sm hover:shadow-md">
            <svg v-if="isSaving" class="animate-spin -ml-1 mr-2 h-4 w-4 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
            {{ isSaving ? 'Saving...' : 'Save' }}
          </button>
        </div>

        <div v-else class="flex items-center text-sm text-green-700 font-semibold py-3 px-4 bg-green-50 border border-green-200 rounded-lg">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
          </svg>
          Cost recorded: {{ formatCurrencyCompact(request.cost) }}
        </div>

        <p v-if="saveError" class="text-red-600 text-xs mt-1 flex items-center font-medium">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-1" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
          </svg>
          {{ saveError }}
        </p>
      </div>
    </div>

    <!-- Image Lightbox Modal -->
    <div v-if="showModal" class="fixed inset-0 bg-black bg-opacity-75 z-50 flex items-center justify-center backdrop-blur-sm">
      <div class="bg-white rounded-xl overflow-hidden w-full max-w-5xl shadow-xl relative">

        <!-- Close Button -->
        <button @click="showModal = false"
                class="absolute top-4 right-4 z-10 bg-white rounded-full p-2 shadow-md text-gray-700 hover:text-red-600 transition-colors focus:outline-none">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
          </svg>
        </button>

        <!-- Image Counter -->
        <div class="absolute top-4 left-4 z-10 bg-black bg-opacity-50 text-white px-3 py-1.5 rounded-md text-sm">
          {{ activeIndex + 1 }} / {{ request.imageUrls.length }}
        </div>

        <!-- Swiper -->
        <Swiper :initialSlide="activeIndex" :slidesPerView="1" navigation pagination class="h-[90vh]">
          <SwiperSlide v-for="(imageId, index) in request.imageUrls" :key="index">
            <div class="w-full h-full flex items-center justify-center bg-gray-900 p-4">
              <img
                  :src="`http://localhost:8080/image/${imageId}`"
                  :alt="`Image ${index + 1}`"
                  class="max-w-full max-h-full object-contain"
              />
            </div>
          </SwiperSlide>
        </Swiper>
      </div>
    </div>
  </div>
</template>
