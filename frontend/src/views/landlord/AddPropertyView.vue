<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useLandlordPropertyStore } from '@/stores/landlord/propertyStore.js'
import PropertyFormFields from "@/components/landlord/property/property-add/PropertyFormFields.vue";
import PropertyLocationMap from "@/components/landlord/property/property-add/PropertyLocationMap.vue";

const router = useRouter()
const propertyStore = useLandlordPropertyStore()

const form = ref({
  owner_id: 1,
  name: '',
  address: '',
  type: 'APARTMENT',
  rentAmount: '',
  status: 'AVAILABLE',
  longitude: null,
  latitude: null
})

const isLoading = ref(false)
const errorMessage = ref('')
const attachments = ref([])
const activeTab = ref('details')
const windowWidth = ref(0)

const updateWindowWidth = () => {
  windowWidth.value = window.innerWidth
}

onMounted(() => {
  updateWindowWidth()
  window.addEventListener('resize', updateWindowWidth)
})

onUnmounted(() => {
  window.removeEventListener('resize', updateWindowWidth)
})

const reverseGeocode = async (location) => {
  if (!window.google) return form.value.address

  const geocoder = new google.maps.Geocoder()
  return new Promise((resolve) => {
    geocoder.geocode({ location }, (results, status) => {
      if (status === 'OK' && results[0]) {
        resolve(results[0].formatted_address)
      } else {
        resolve(form.value.address)
      }
    })
  })
}

const handleMapClick = async (location) => {
  form.value.longitude = location.lng
  form.value.latitude = location.lat

  try {
    const address = await reverseGeocode(location)
    form.value.address = address
  } catch (error) {
    console.error('Reverse geocoding failed:', error)
  }
}

const handleLocationSelected = (location) => {
  form.value.longitude = location.lng
  form.value.latitude = location.lat
}

const handleMarkerDragged = async (location) => {
  form.value.longitude = location.lng
  form.value.latitude = location.lat

  try {
    const address = await reverseGeocode(location)
    form.value.address = address
  } catch (error) {
    console.error('Reverse geocoding failed:', error)
  }
}

function cancel() {
  router.push('/properties')
}

async function saveProperty() {
  try {
    isLoading.value = true
    errorMessage.value = ''

    if (!form.value.longitude || !form.value.latitude) {
      errorMessage.value = 'Please select a location on the map'
      return
    }

    const payload = {
      ...form.value,
      rent_amount: parseFloat(form.value.rentAmount),
      longitude: parseFloat(form.value.longitude),
      latitude: parseFloat(form.value.latitude)
    }

    await propertyStore.createProperty(payload, attachments.value)
    await router.push('/landlord/properties')
  } catch (error) {
    errorMessage.value = 'Failed to add property. Please try again.'
    console.error('Error adding property:', error)
  } finally {
    isLoading.value = false
  }
}
</script>

<template>
  <div class="max-w-7xl mx-auto px-6 py-10">
    <!-- Header area with decorative elements -->
    <div class="bg-gradient-to-r from-blue-600 to-indigo-700 rounded-2xl shadow-lg mb-8">
      <div class="px-8 py-6">
        <div class="flex flex-col md:flex-row justify-between items-start md:items-center gap-4">
          <div class="flex items-center gap-4">
            <div class="bg-white/20 p-3 rounded-xl">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-8 w-8 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6" />
              </svg>
            </div>
            <div>
              <h1 class="text-3xl font-bold text-white">Add New Property</h1>
              <p class="text-blue-100 mt-1">Fill in the details below to list your property</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Main content -->
    <div class="max-w-6xl mx-auto px-6 py-8">
      <form @submit.prevent="saveProperty" class="relative">
        <!-- Error message -->
        <div v-if="errorMessage" class="mb-6 p-4 bg-red-50 border-l-4 border-red-500 text-red-700 rounded shadow-sm animate-pulse">
          <div class="flex items-center">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-2" viewBox="0 0 20 20" fill="currentColor">
              <path fill-rule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7 4a1 1 0 11-2 0 1 1 0 012 0zm-1-9a1 1 0 00-1 1v4a1 1 0 102 0V6a1 1 0 00-1-1z" clip-rule="evenodd" />
            </svg>
            {{ errorMessage }}
          </div>
        </div>

        <!-- Mobile/small screens: Tab navigation -->
        <div class="lg:hidden mb-6">
          <div class="flex rounded-lg bg-white shadow-sm p-1 border border-gray-200">
            <button
                type="button"
                @click="activeTab = 'details'"
                :class="[
                'flex-1 py-3 px-2 rounded-md text-sm font-medium transition',
                activeTab === 'details' ? 'bg-blue-50 text-blue-700 shadow-sm' : 'text-gray-600'
              ]"
            >
              <span class="flex items-center justify-center">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-1" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                </svg>
                Details
              </span>
            </button>
            <button
                type="button"
                @click="activeTab = 'location'"
                :class="[
                'flex-1 py-3 px-2 rounded-md text-sm font-medium transition',
                activeTab === 'location' ? 'bg-blue-50 text-blue-700 shadow-sm' : 'text-gray-600'
              ]"
            >
              <span class="flex items-center justify-center">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-1" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z" />
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z" />
                </svg>
                Location
              </span>
            </button>
          </div>
        </div>

        <!-- Changed from lg:grid-cols-3 to lg:grid-cols-2 for 50/50 split -->
        <div class="grid grid-cols-1 lg:grid-cols-2 gap-8">
          <!-- Form Section (removed lg:col-span-2, now takes 1/2) -->
          <div
              class="space-y-6 bg-white p-6 md:p-8 rounded-2xl shadow-lg border border-gray-200 transition-all duration-300"
              :class="{'hidden': activeTab === 'location' && windowWidth < 1024}"
          >
            <div class="border-b border-gray-100 pb-4 mb-4">
              <h2 class="text-xl font-bold text-gray-800">Property Details</h2>
              <p class="text-gray-500 text-sm">Basic information about your property</p>
            </div>

            <PropertyFormFields
                v-model="form"
                v-model:attachments="attachments"
                :is-loading="isLoading"
                @location-selected="handleLocationSelected"
                required
            />
          </div>

          <!-- Map Section (now takes 1/2 instead of 1/3) -->
          <div
              class="lg:sticky lg:top-6 bg-white p-6 rounded-2xl shadow-lg border border-gray-200 h-fit transition-all duration-300"
              :class="{'hidden': activeTab === 'details' && windowWidth < 1024}"
          >
            <div class="border-b border-gray-100 pb-4 mb-4">
              <h2 class="text-xl font-bold text-gray-800">Location</h2>
              <p class="text-gray-500 text-sm">Select the property's exact location</p>
            </div>

            <PropertyLocationMap
                v-model:longitude="form.longitude"
                v-model:latitude="form.latitude"
                :is-loading="isLoading"
                @map-click="handleMapClick"
                @marker-dragged="handleMarkerDragged"
                class="rounded-xl overflow-hidden border border-gray-200 shadow-inner"
                required
            />

            <div class="mt-4 flex items-start space-x-2 text-sm">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-blue-500 flex-shrink-0 mt-0.5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
              <p class="text-gray-600">Click on the map or use the search box to select your property's location</p>
            </div>

            <div v-if="form.address" class="mt-4 p-3 bg-blue-50 rounded-md border border-blue-100">
              <p class="text-sm font-medium text-gray-700">Selected Address:</p>
              <p class="text-sm text-blue-700">{{ form.address }}</p>
            </div>
            
          </div>
        </div>

        <!-- Action Buttons -->
        <div class="flex justify-end mt-8 gap-4">
          <button
              type="button"
              @click="cancel"
              class="px-6 py-3 text-gray-700 bg-white border border-gray-300 rounded-xl hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-blue-500/20 transition-colors font-medium"
          >
            Cancel
          </button>
          <button
              type="submit"
              :disabled="isLoading || !form.longitude || !form.latitude"
              class="px-6 py-3 bg-blue-600 text-white rounded-xl hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500/20 disabled:opacity-50 disabled:cursor-not-allowed transition-colors font-medium flex items-center"
          >
            <svg v-if="isLoading" class="animate-spin -ml-1 mr-3 h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
            {{ isLoading ? 'Saving...' : 'Save Property' }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>