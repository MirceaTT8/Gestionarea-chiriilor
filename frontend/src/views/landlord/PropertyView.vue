<script setup>
import { ref, computed, onMounted, watch, nextTick } from 'vue'
import { useLandlordPropertyStore } from '@/stores/landlord/propertyStore.js'

import PropertyFilters from '@/components/landlord/property/PropertyFilters.vue'
import PropertyList from '@/components/landlord/property/PropertyList.vue'
import PropertyMap from '@/components/landlord/property/PropertyMap.vue'
import PropertyViewToggle from '@/components/landlord/property/PropertyViewToggle.vue'

const store = useLandlordPropertyStore()
const showMapView = ref(false)
const filters = ref({ location: '', type: '', status: '' })

const filterOptions = {
  types: ['Apartment', 'House', 'Commercial', 'Land'],
  statuses: ['Available', 'Rented', 'Inactive']
}

const filteredProperties = computed(() => {
  return store.properties.filter(property => {
    const matchesLocation = !filters.value.location ||
        property.address.toLowerCase().includes(filters.value.location.toLowerCase())

    const matchesType = !filters.value.type ||
        property.type === filters.value.type.toUpperCase()

    const matchesStatus = !filters.value.status ||
        property.status === filters.value.status.toUpperCase()

    return matchesLocation && matchesType && matchesStatus
  })
})

const resetFilters = () => {
  filters.value = { location: '', type: '', status: '' }
}

const isInternalUpdate = ref(false)
const onFiltersUpdate = (newFilters) => {
  if (!isInternalUpdate.value) {
    filters.value = { ...newFilters }
  }
}

const availableTypes = computed(() => {
  const backendTypes = new Set(store.properties.map(p => p.type))
  return filterOptions.types.filter(frontendType =>
      backendTypes.has(frontendType.toUpperCase())
  )
})

const availableStatuses = computed(() => {
  const filteredByType = store.properties.filter(property => {
    if (!filters.value.type) return true
    return property.type === filters.value.type.toUpperCase()
  })

  const backendStatuses = new Set(filteredByType.map(p => p.status))
  return filterOptions.statuses.filter(frontendStatus =>
      backendStatuses.has(frontendStatus.toUpperCase())
  )
})

watch(() => filters.value.type, (newType, oldType) => {
  if (newType !== oldType) {
    isInternalUpdate.value = true
    filters.value.status = ''
    nextTick(() => {
      isInternalUpdate.value = false
    })
  }
})

onMounted(async () => {
  await store.loadProperties()
})
</script>

<template>
  <div class="max-w-7xl mx-auto px-6 py-10">
    <!-- Header with gradient background -->
    <div class="bg-gradient-to-r from-blue-600 to-indigo-700 rounded-2xl shadow-lg mb-8">
      <div class="px-8 py-6">
        <div class="flex flex-col md:flex-row justify-between items-start md:items-center gap-4">
          <div class="flex items-center gap-4">
            <div class="bg-white/20 p-3 rounded-xl">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-8 w-8 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4" />
              </svg>
            </div>
            <div>
              <h1 class="text-3xl font-bold text-white">My Properties</h1>
              <p class="text-blue-100 mt-1">Manage your property portfolio</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Property Stats -->
    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
      <!-- Total Properties -->
      <div class="bg-white rounded-xl shadow-md p-6">
        <div class="flex items-center gap-4">
          <div class="bg-blue-100 p-3 rounded-lg">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-blue-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6" />
            </svg>
          </div>
          <div>
            <h3 class="text-gray-500 text-sm">Total Properties</h3>
            <p class="font-bold text-xl text-gray-800">{{ store.properties.length }}</p>
          </div>
        </div>
      </div>

      <!-- Available Properties -->
      <div class="bg-white rounded-xl shadow-md p-6">
        <div class="flex items-center gap-4">
          <div class="bg-green-100 p-3 rounded-lg">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-green-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
          </div>
          <div>
            <h3 class="text-gray-500 text-sm">Available Properties</h3>
            <p class="font-bold text-xl text-gray-800">{{ store.properties.filter(p => p.status === 'AVAILABLE').length }}</p>
          </div>
        </div>
      </div>

      <!-- Rented Properties -->
      <div class="bg-white rounded-xl shadow-md p-6">
        <div class="flex items-center gap-4">
          <div class="bg-purple-100 p-3 rounded-lg">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-purple-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V5a2 2 0 012-2h4a2 2 0 012 2v2m-6 9h6m-6 1a1 1 0 001 1h4a1 1 0 001-1v-1m-6-9a1 1 0 00-1 1v9a1 1 0 001 1h6a1 1 0 001-1V8a1 1 0 00-1-1h-6a1 1 0 00-1 1z" />
            </svg>
          </div>
          <div>
            <h3 class="text-gray-500 text-sm">Rented Properties</h3>
            <p class="font-bold text-xl text-gray-800">{{ store.properties.filter(p => p.status === 'RENTED').length }}</p>
          </div>
        </div>
      </div>

      <!-- Inactive Properties -->
      <div class="bg-white rounded-xl shadow-md p-6">
        <div class="flex items-center gap-4">
          <div class="bg-gray-100 p-3 rounded-lg">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-gray-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M18.364 18.364A9 9 0 005.636 5.636m12.728 12.728A9 9 0 015.636 5.636m12.728 12.728L5.636 5.636" />
            </svg>
          </div>
          <div>
            <h3 class="text-gray-500 text-sm">Inactive Properties</h3>
            <p class="font-bold text-xl text-gray-800">{{ store.properties.filter(p => p.status === 'INACTIVE').length }}</p>
          </div>
        </div>
      </div>
    </div>

    <!-- View Toggle -->
    <PropertyViewToggle v-model:showMap="showMapView" />

    <!-- Filters -->
    <PropertyFilters
        :filters="filters"
        :filterOptions="{
        ...filterOptions,
        availableTypes: availableTypes,
        availableStatuses: availableStatuses
      }"
        @update:filters="onFiltersUpdate"
        @reset="resetFilters"
    />

    <!-- Map View -->
    <PropertyMap
        v-if="showMapView"
        :properties="filteredProperties"
    />

    <!-- List View -->
    <PropertyList
        v-else
        :properties="filteredProperties"
        :loading="store.loading"
        :error="store.error"
        @reset-filters="resetFilters"
    />
  </div>
</template>