
<script setup>
import { ref, watch } from 'vue'

const props = defineProps({
  images: Array,
  show: Boolean
})

defineEmits(['close'])

const currentImageIndex = ref(0)

const nextImage = () => {
  if (!props.images?.length) return
  currentImageIndex.value = (currentImageIndex.value + 1) % props.images.length
}

const prevImage = () => {
  if (!props.images?.length) return
  currentImageIndex.value = (currentImageIndex.value - 1 + props.images.length) % props.images.length
}

watch([() => props.show, () => props.images], () => {
  currentImageIndex.value = 0
})
</script>

<template>
  <div v-if="show" class="fixed inset-0 z-50 bg-black bg-opacity-60 flex items-center justify-center p-4">
    <div class="bg-white p-6 rounded-xl max-w-5xl w-full max-h-[90vh] overflow-hidden relative shadow-2xl">
      <button @click="$emit('close')" class="absolute top-4 right-4 text-gray-600 hover:text-black text-2xl font-bold z-10 w-8 h-8 flex items-center justify-center rounded-full hover:bg-gray-100 transition-colors">&times;</button>

      <h2 class="text-2xl font-semibold mb-6 text-gray-800">Maintenance Images</h2>

      <div v-if="images && images.length > 0" class="space-y-6">
        <div class="relative bg-gray-100 rounded-lg overflow-hidden flex items-center justify-center" style="height: 400px;">
          <img
              :src="`http://localhost:8080/image/${images[currentImageIndex]}`"
              :alt="`Maintenance image ${currentImageIndex + 1}`"
              class="max-w-full max-h-full object-contain"
              style="image-rendering: high-quality;"
          />

          <button
              v-if="images.length > 1"
              @click="prevImage"
              class="absolute left-4 top-1/2 -translate-y-1/2 bg-white/90 shadow-lg p-2 rounded-full hover:bg-white hover:scale-110 transition-all duration-200"
          >
            <svg class="w-5 h-5 text-gray-700" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7"/>
            </svg>
          </button>
          <button
              v-if="images.length > 1"
              @click="nextImage"
              class="absolute right-4 top-1/2 -translate-y-1/2 bg-white/90 shadow-lg p-2 rounded-full hover:bg-white hover:scale-110 transition-all duration-200"
          >
            <svg class="w-5 h-5 text-gray-700" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"/>
            </svg>
          </button>

          <div v-if="images.length > 1" class="absolute bottom-4 right-4 bg-black/70 text-white px-3 py-1 rounded-full text-sm font-medium">
            {{ currentImageIndex + 1 }} / {{ images.length }}
          </div>
        </div>

        <div v-if="images.length > 1" class="flex gap-3 overflow-x-auto pb-2 px-1">
          <button
              v-for="(id, index) in images"
              :key="index"
              @click="currentImageIndex = index"
              class="flex-shrink-0 w-20 h-16 rounded-lg overflow-hidden border-2 transition-all duration-200 hover:scale-105"
              :class="{
              'border-blue-500 shadow-lg': currentImageIndex === index,
              'border-gray-200 hover:border-gray-300': currentImageIndex !== index
            }"
          >
            <img
                :src="`http://localhost:8080/image/${id}`"
                :alt="`Thumbnail ${index + 1}`"
                class="w-full h-full object-cover"
                loading="lazy"
            />
          </button>
        </div>
      </div>

      <div v-else class="flex items-center justify-center py-16">
        <div class="text-center">
          <svg class="w-16 h-16 text-gray-400 mx-auto mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z"/>
          </svg>
          <p class="text-gray-500 text-lg">No maintenance images available</p>
        </div>
      </div>
    </div>
  </div>
</template>


<style scoped>
.overflow-x-auto::-webkit-scrollbar {
  height: 4px;
}

.overflow-x-auto::-webkit-scrollbar-track {
  background: #f1f5f9;
  border-radius: 2px;
}

.overflow-x-auto::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 2px;
}

.overflow-x-auto::-webkit-scrollbar-thumb:hover {
  background: #94a3b8;
}

button {
  transition: all 0.2s ease-in-out;
}
</style>