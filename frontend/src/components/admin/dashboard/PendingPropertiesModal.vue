<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import {getUserNameById} from "../../../utils/leaseNameUtils.js";

const router = useRouter()

const props = defineProps({
  visible: Boolean,
  properties: Array,
  users: Array,
  loading: Boolean
})

const emit = defineEmits(['update:visible', 'approve-property', 'reject-property'])

const processingPropertyId = ref(null)

const handleApprove = async (propertyId) => {
  processingPropertyId.value = propertyId
  try {
    emit('approve-property', propertyId)
  } finally {
    processingPropertyId.value = null
  }
}

const handleReject = async (propertyId) => {
  processingPropertyId.value = propertyId
  try {
    emit('reject-property', propertyId)
  } finally {
    processingPropertyId.value = null
  }
}

const viewPropertyDetails = (propertyId) => {
  emit('update:visible', false)

  router.push({ name: 'PropertyDetails', params: { id: propertyId } })
}
</script>

<template>
  <Dialog
      :visible="visible"
      modal
      :header="false"
      class="w-full max-w-4xl rounded-xl shadow-lg overflow-hidden"
      @update:visible="$emit('update:visible', $event)"
  >
    <!-- Header -->
    <div class="bg-gradient-to-r from-blue-500 to-blue-600 px-6 py-4 flex justify-between items-center">
      <div class="flex items-center">
        <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-white mr-3" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4" />
        </svg>
        <h2 class="text-xl font-semibold text-white">Pending Properties</h2>
        <span v-if="properties?.length" class="ml-3 bg-white/20 text-white text-sm px-2 py-1 rounded-full">
          {{ properties.length }} pending
        </span>
      </div>
      <button
          @click="$emit('update:visible', false)"
          class="text-white/80 hover:text-white transition-colors focus:outline-none"
      >
        <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
        </svg>
      </button>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="flex flex-col items-center justify-center p-12 bg-white min-h-[400px]">
      <div class="w-12 h-12 border-4 border-gray-200 border-t-blue-500 rounded-full animate-spin mb-4"></div>
      <p class="text-gray-600">Loading pending properties...</p>
    </div>

    <!-- Content -->
    <div v-else class="bg-white min-h-[400px] max-h-[80vh] overflow-y-auto">
      <div v-if="properties.length > 0" class="p-6 space-y-4">
        <div
            v-for="property in properties"
            :key="property.propertyId"
            class="bg-white border border-blue-200 rounded-lg overflow-hidden shadow-sm hover:shadow-md transition-shadow"
        >
          <div class="bg-blue-50 px-5 py-3 border-b border-blue-200 flex justify-between items-center">
            <div class="flex items-center">
              <span class="bg-blue-100 text-blue-800 text-xs font-medium px-2.5 py-1 rounded-full mr-3">
                {{ property.validationStatus || property.status }}
              </span>
              <h3 class="font-semibold text-blue-900">Property #{{ property.propertyId }}</h3>
            </div>
            <div class="text-xs text-blue-600">
              <span v-if="property.createdAt">
                Submitted {{ new Date(property.createdAt).toLocaleDateString() }}
              </span>
            </div>
          </div>

          <div class="p-5">
            <!-- Property Details Grid -->
            <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4 mb-4">
              <div class="flex items-start">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-gray-500 mt-0.5 mr-2 flex-shrink-0" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6" />
                </svg>
                <div>
                  <p class="text-sm font-medium text-gray-500">Property Name</p>
                  <p class="font-medium text-gray-900">{{ property.title || property.name || property.address || 'Unnamed Property' }}</p>
                </div>
              </div>

              <div class="flex items-start">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-gray-500 mt-0.5 mr-2 flex-shrink-0" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                </svg>
                <div>
                  <p class="text-sm font-medium text-gray-500">Owner </p>
                  <p class="font-medium text-gray-900">{{ getUserNameById(property.ownerId, users) }}</p>
                </div>
              </div>

              <div v-if="property.type" class="flex items-start">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-gray-500 mt-0.5 mr-2 flex-shrink-0" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 7h.01M7 3h5c.512 0 1.024.195 1.414.586l7 7a2 2 0 010 2.828l-7 7a2 2 0 01-2.828 0l-7-7A1.994 1.994 0 013 12V7a4 4 0 014-4z" />
                </svg>
                <div>
                  <p class="text-sm font-medium text-gray-500">Type</p>
                  <p class="font-medium text-gray-900 capitalize">{{ property.type.toLowerCase() }}</p>
                </div>
              </div>
            </div>

            <!-- Address -->
            <div v-if="property.address" class="mb-4 flex items-start">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-gray-500 mt-0.5 mr-2 flex-shrink-0" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z" />
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z" />
              </svg>
              <div class="flex-1">
                <p class="text-sm font-medium text-gray-500">Address</p>
                <p class="font-medium text-gray-900">{{ property.address }}</p>
              </div>
            </div>

            <div v-if="property.description" class="mb-4 flex items-start">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-gray-500 mt-0.5 mr-2 flex-shrink-0" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
              </svg>
              <div class="flex-1">
                <p class="text-sm font-medium text-gray-500">Description</p>
                <p class="text-gray-900">{{ property.description }}</p>
              </div>
            </div>

            <!-- Action buttons and status -->
            <div class="mt-6 border-t border-gray-100 pt-4">
              <div class="flex justify-between items-center">
                <div class="flex items-center">
                  <span class="w-2 h-2 rounded-full mr-2" :class="{
                    'bg-yellow-400': (property.validationStatus || property.status) === 'PENDING',
                    'bg-green-400': (property.validationStatus || property.status) === 'APPROVED',
                    'bg-red-400': (property.validationStatus || property.status) === 'REJECTED',
                    'bg-gray-400': !['PENDING', 'APPROVED', 'REJECTED'].includes(property.validationStatus || property.status)
                  }"></span>
                  <span class="text-sm text-gray-600">
                    Status: {{ property.validationStatus || property.status }}
                  </span>
                </div>

                <!-- Action Buttons -->
                <div class="flex gap-3">
                  <button
                      @click="handleApprove(property.propertyId)"
                      :disabled="processingPropertyId === property.propertyId"
                      class="inline-flex items-center px-3 py-1.5 border border-transparent text-xs font-medium rounded-md text-white bg-green-600 hover:bg-green-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-green-500 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
                  >
                    <svg v-if="processingPropertyId === property.propertyId" class="animate-spin -ml-1 mr-2 h-3 w-3 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                      <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                      <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                    </svg>
                    <svg v-else xmlns="http://www.w3.org/2000/svg" class="h-3 w-3 mr-1" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
                    </svg>
                    Approve
                  </button>

                  <button
                      @click="handleReject(property.propertyId)"
                      :disabled="processingPropertyId === property.propertyId"
                      class="inline-flex items-center px-3 py-1.5 border border-transparent text-xs font-medium rounded-md text-white bg-red-600 hover:bg-red-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-red-500 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
                  >
                    <svg v-if="processingPropertyId === property.propertyId" class="animate-spin -ml-1 mr-2 h-3 w-3 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                      <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                      <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                    </svg>
                    <svg v-else xmlns="http://www.w3.org/2000/svg" class="h-3 w-3 mr-1" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
                    </svg>
                    Reject
                  </button>

                  <button
                      @click="viewPropertyDetails(property.propertyId)"
                      class="inline-flex items-center px-3 py-1.5 border border-gray-300 text-xs font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 transition-colors"
                  >
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-3 w-3 mr-1" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
                    </svg>
                    View Details
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Empty State -->
      <div v-else class="flex flex-col items-center justify-center px-8 py-16 bg-white text-center min-h-[400px]">
        <div class="mx-auto w-24 h-24 bg-gradient-to-br from-blue-100 to-blue-200 rounded-full flex items-center justify-center mb-6 shadow-sm">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-12 w-12 text-blue-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4" />
          </svg>
        </div>

        <div class="max-w-sm">
          <h3 class="text-xl font-semibold text-gray-900 mb-3">All Caught Up!</h3>
          <p class="text-gray-600 mb-6 leading-relaxed">
            No properties are currently pending review. New property submissions from owners will appear here when they need your attention.
          </p>
          <div class="bg-blue-50 border border-blue-200 rounded-lg p-4">
            <p class="text-sm text-blue-700">
              💡 Properties will automatically appear here when landlords submit new listings for approval.
            </p>
          </div>
        </div>
      </div>
    </div>
  </Dialog>
</template>