<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import FileUploader from '@/components/landlord/property/PropertyFileUploader.vue'

const propertyTypes = [
  { value: 'APARTMENT', label: 'Apartment' },
  { value: 'HOUSE', label: 'House' },
  { value: 'COMMERCIAL', label: 'Commercial' },
  { value: 'LAND', label: 'Land' }
]

const emit = defineEmits([
  'update:modelValue',
  'update:attachments',
  'location-selected'
])

const props = defineProps({
  isLoading: Boolean,
  modelValue: {
    type: Object,
    required: true
  },
  attachments: {
    type: Array,
    required: true
  }
})

const form = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

const attachments = computed({
  get: () => props.attachments,
  set: (value) => emit('update:attachments', value)
})

const autocompleteInput = ref(null)
const autocomplete = ref(null)

const initAutocomplete = () => {
  const googleApi = window.google

  if (!googleApi?.maps?.places) {
    console.log('Google Maps API not ready yet')
    return false
  }

  autocomplete.value = new googleApi.maps.places.Autocomplete(
      autocompleteInput.value,
      {
        types: ['address'],
        fields: ['address_components', 'geometry', 'formatted_address'],
        componentRestrictions: { country: 'ro' }
      }
  )

  autocomplete.value.addListener('place_changed', () => {
    const place = autocomplete.value.getPlace()

    if (!place.geometry) {
      console.log(`No details available for input: ${place.name}`)
      return
    }

    form.value.address = place.formatted_address
    emit('location-selected', {
      lat: place.geometry.location.lat(),
      lng: place.geometry.location.lng()
    })
  })

  return true
}

onMounted(() => {
  if (initAutocomplete()) return

  const checkInterval = setInterval(() => {
    if (initAutocomplete()) {
      clearInterval(checkInterval)
    }
  }, 200)
})

onUnmounted(() => {
  if (autocomplete.value) {
    google.maps.event.clearInstanceListeners(autocomplete.value)
  }
})
</script>

<template>
  <div class="space-y-6 bg-white p-6 rounded-2xl shadow-md">
    <!-- Property Name -->
    <div class="form-group">
      <label class="block text-sm font-medium text-gray-700 mb-1">
        Property Name <span class="text-red-500">*</span>
      </label>
      <input
          v-model="form.name"
          type="text"
          required
          class="w-full p-3 border border-gray-300 rounded-lg shadow-sm focus:ring-blue-500 focus:border-blue-500"
          :disabled="isLoading"
          placeholder="Enter property name"
      >
    </div>

    <!-- Address with Google Autocomplete -->
    <div class="form-group">
      <label class="block text-sm font-medium text-gray-700 mb-1">
        Address <span class="text-red-500">*</span>
      </label>
      <input
          ref="autocompleteInput"
          v-model="form.address"
          type="text"
          required
          class="w-full p-3 border border-gray-300 rounded-lg shadow-sm focus:ring-blue-500 focus:border-blue-500"
          :disabled="isLoading"
          placeholder="Start typing an address..."
          id="autocomplete"
      >
    </div>

    <!-- Property Type -->
    <div class="form-group">
      <label class="block text-sm font-medium text-gray-700 mb-1">
        Property Type <span class="text-red-500">*</span>
      </label>
      <select
          v-model="form.type"
          class="w-full p-3 border border-gray-300 rounded-lg shadow-sm focus:ring-blue-500 focus:border-blue-500"
          :disabled="isLoading"
          required
      >
        <option disabled value="">Select a property type</option>
        <option
            v-for="type in propertyTypes"
            :key="type.value"
            :value="type.value"
        >
          {{ type.label }}
        </option>
      </select>
    </div>

    <!-- Monthly Rent -->
    <div class="form-group">
      <label class="block text-sm font-medium text-gray-700 mb-1">
        Monthly Rent ($) <span class="text-red-500">*</span>
      </label>
      <input
          v-model.number="form.rentAmount"
          type="number"
          min="0"
          step="0.01"
          required
          class="w-full p-3 border border-gray-300 rounded-lg shadow-sm focus:ring-blue-500 focus:border-blue-500"
          :disabled="isLoading"
          placeholder="0.00"
      >
    </div>

    <!-- File Attachments -->
    <div class="form-group">
      <label class="block text-sm font-medium text-gray-700 mb-2">
        Attachments
      </label>
      <FileUploader
          v-model="attachments"
          :is-loading="isLoading"
      />
    </div>
  </div>
</template>

<style scoped>
.form-group {
  @apply transition-all duration-200;
}

.form-group:hover label {
  @apply text-blue-600;
}

input:invalid, select:invalid {
  @apply border-red-300;
}

input:valid, select:valid {
  @apply border-green-300;
}
</style>