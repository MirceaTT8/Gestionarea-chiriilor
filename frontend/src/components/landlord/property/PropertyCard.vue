<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

defineProps({
  property: {
    type: Object,
    required: true
  }
})

const statusColors = {
  AVAILABLE: { bg: 'bg-emerald-50', text: 'text-emerald-700', border: 'border-emerald-200' },
  RENTED: { bg: 'bg-amber-50', text: 'text-amber-700', border: 'border-amber-200' },
  INACTIVE: { bg: 'bg-gray-50', text: 'text-gray-600', border: 'border-gray-200' }
}

const navigateToDetails = (id) => {
  const numericId = Number(id)
  if (isNaN(numericId)) {
    console.error('Invalid property ID:', id)
    return
  }

  router.push(`/landlord/property/${numericId}`)
}
</script>

<template>
  <div class="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden transition-all duration-300 hover:shadow-xl hover:-translate-y-2 hover:border-gray-200">
    <!-- Property Images -->
    <div v-if="property.imageUrls?.length" class="h-52 overflow-hidden relative cursor-pointer group" @click="navigateToDetails(property.id)">
      <img
          :src="`http://localhost:8080/image/${property.imageUrls[0]}`"
          alt="Property image"
          class="w-full h-full object-cover transition-transform duration-500 group-hover:scale-110"
      >
      <div class="absolute inset-0 bg-gradient-to-t from-black/20 to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-300"></div>
      <div v-if="property.imageUrls.length > 1" class="absolute bottom-3 right-3 bg-black/70 backdrop-blur-sm text-white px-3 py-1.5 rounded-full text-xs font-medium">
        +{{ property.imageUrls.length - 1 }} photos
      </div>
    </div>
    <div v-else class="h-52 overflow-hidden bg-gradient-to-br from-gray-50 to-gray-100 flex items-center justify-center cursor-pointer group" @click="navigateToDetails(property.id)">
      <div class="text-center">
        <img
            src="../../../assets/logo.svg"
            alt="Property placeholder"
            class="w-16 h-16 mx-auto opacity-40 group-hover:opacity-60 transition-opacity duration-300"
        >
        <p class="text-gray-400 text-sm mt-2 font-medium">No photos available</p>
      </div>
    </div>

    <div class="p-6">
      <div class="flex justify-between items-start mb-5">
        <div class="flex-1 pr-4">
          <h3 class="text-xl font-semibold text-gray-900 leading-tight mb-1">{{ property.name }}</h3>
        </div>
        <span
            :class="[
              statusColors[property.status].bg,
              statusColors[property.status].text,
              statusColors[property.status].border
            ]"
            class="px-3 py-1.5 rounded-full text-xs font-semibold capitalize border shrink-0"
        >
          {{ property.status.toLowerCase() }}
        </span>
      </div>

      <div class="space-y-3 mb-6">
        <div class="flex items-start text-gray-600">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-2 mt-0.5 shrink-0 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
            <path stroke-linecap="round" stroke-linejoin="round" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z" />
            <path stroke-linecap="round" stroke-linejoin="round" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z" />
          </svg>
          <span class="text-sm leading-relaxed">{{ property.address }}</span>
        </div>

        <div class="flex items-center text-emerald-700">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-2 text-emerald-600" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
            <path stroke-linecap="round" stroke-linejoin="round" d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
          </svg>
          <span class="text-lg font-bold">${{ property.rentAmount }}</span>
          <span class="text-gray-500 font-normal text-sm ml-1">/month</span>
        </div>
      </div>

      <div v-if="property.imageUrls?.length > 1" class="mb-6">
        <div class="flex items-center justify-between mb-3">
          <h4 class="font-semibold text-gray-800 text-sm">
            Property Photos
          </h4>
          <span class="text-xs text-gray-500 bg-gray-50 px-2 py-1 rounded-full">
            {{ property.imageUrls.length }} photos
          </span>
        </div>
        <div class="grid grid-cols-4 gap-2">
          <div
              v-for="(imageId, index) in property.imageUrls.slice(0, 3)"
              :key="index"
              class="aspect-square border border-gray-200 rounded-lg overflow-hidden cursor-pointer hover:border-gray-300 transition-colors group"
              @click="navigateToDetails(property.propertyId)"
          >
            <img
                :src="`http://localhost:8080/image/${imageId}`"
                :alt="`Property photo ${index + 1}`"
                class="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300"
            >
          </div>
          <div
              v-if="property.imageUrls.length > 3"
              class="aspect-square border-2 border-dashed border-gray-200 rounded-lg bg-gray-50 flex flex-col items-center justify-center text-gray-500 text-xs font-medium cursor-pointer hover:border-gray-300 hover:bg-gray-100 transition-colors"
              @click="navigateToDetails(property.propertyId)"
          >
            <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mb-1" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
              <path stroke-linecap="round" stroke-linejoin="round" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
            </svg>
            <span>+{{ property.imageUrls.length - 3 }}</span>
          </div>
        </div>
      </div>

      <div class="flex justify-end pt-2">
        <button
            @click="navigateToDetails(property.propertyId)"
            class="flex items-center gap-2 px-5 py-2.5 bg-gradient-to-r from-blue-600 to-blue-700 text-white rounded-xl hover:from-blue-700 hover:to-blue-800 transition-all duration-200 text-sm font-semibold shadow-lg hover:shadow-xl transform hover:scale-105 active:scale-95"
        >
          <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
            <path stroke-linecap="round" stroke-linejoin="round" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
            <path stroke-linecap="round" stroke-linejoin="round" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
          </svg>
          View Details
        </button>
      </div>
    </div>
  </div>
</template>