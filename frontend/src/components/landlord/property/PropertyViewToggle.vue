<script setup>
const props = defineProps({
  showMap: {
    type: Boolean,
    required: true
  }
})

const emit = defineEmits(['update:showMap'])

const handleListView = () => {
  console.log('Switching to list view')
  emit('update:showMap', false)
}

const handleMapView = () => {
  console.log('Switching to map view')
  emit('update:showMap', true)
}
</script>

<template>
  <div class="bg-white rounded-xl shadow-md mb-6 p-4 border border-gray-100">
    <div class="flex justify-between items-center">
      <h2 class="text-lg font-semibold text-gray-800">View Options</h2>

      <div class="flex bg-gray-100 p-1 rounded-lg">
        <button
            @click="handleListView"
            :class="[
              'px-4 py-2 rounded-lg transition-all duration-200 font-medium',
              !showMap ? 'bg-white text-indigo-600 shadow-sm' : 'text-gray-600 hover:text-gray-800'
            ]"
        >
          <div class="flex items-center gap-2">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 10h16M4 14h16M4 18h16" />
            </svg>
            <span>List View</span>
          </div>
        </button>

        <button
            @click="handleMapView"
            :class="[
              'px-4 py-2 rounded-lg transition-all duration-200 font-medium',
              showMap ? 'bg-white text-indigo-600 shadow-sm' : 'text-gray-600 hover:text-gray-800'
            ]"
        >
          <div class="flex items-center gap-2">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 20l-5.447-2.724A1 1 0 013 16.382V5.618a1 1 0 011.447-.894L9 7m0 13l6-3m-6 3V7m6 10l4.553 2.276A1 1 0 0021 18.382V7.618a1 1 0 00-.553-.894L15 4m0 13V4m0 0L9 7" />
            </svg>
            <span>Map View</span>
          </div>
        </button>
      </div>
    </div>
  </div>
</template>