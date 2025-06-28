<script setup>
import { ref, computed } from 'vue'

const props = defineProps({
  modelValue: {
    type: Array,
    required: true
  },
  isLoading: Boolean
})

const emit = defineEmits(['update:modelValue'])

const attachments = computed({
  get() {
    return props.modelValue
  },
  set(value) {
    emit('update:modelValue', value)
  }
})

const fileInput = ref(null)
const currentSlide = ref(0)

const handleFileUpload = (event) => {
  const files = event.target.files
  if (!files) return

  const newAttachments = [...attachments.value]

  for (let i = 0; i < files.length; i++) {
    newAttachments.push({
      file: files[i],
      id: Date.now() + i,
      name: files[i].name,
      size: files[i].size,
      preview: files[i].type.startsWith('image/') ? URL.createObjectURL(files[i]) : null
    })
  }

  attachments.value = newAttachments
  event.target.value = ''
}

const removeAttachment = (index) => {
  const newAttachments = [...attachments.value]
  newAttachments.splice(index, 1)
  attachments.value = newAttachments
  if (currentSlide.value >= newAttachments.length) {
    currentSlide.value = Math.max(0, newAttachments.length - 1)
  }
}

const nextSlide = () => {
  if (currentSlide.value < attachments.value.length - 1) {
    currentSlide.value++
  }
}

const prevSlide = () => {
  if (currentSlide.value > 0) {
    currentSlide.value--
  }
}
</script>

<template>
  <div>
    <label class="block mb-1">Property Images</label>
    <div class="border-2 border-dashed rounded-lg p-4">
      <input
          ref="fileInput"
          type="file"
          multiple
          accept="image/*"
          @change="handleFileUpload"
          class="hidden"
      >
      <button
          type="button"
          @click="fileInput.click()"
          class="w-full py-2 px-4 bg-gray-100 hover:bg-gray-200 rounded transition"
          :disabled="isLoading"
      >
        Select Images
      </button>
      <p class="mt-2 text-sm text-gray-500">
        Upload property photos (JPEG, PNG)
      </p>
    </div>

    <!-- Image Slider -->
    <div v-if="attachments.length > 0" class="mt-4">
      <div class="relative">
        <!-- Current Image -->
        <div class="w-full h-64 bg-gray-100 rounded-lg overflow-hidden">
          <img
              v-if="attachments[currentSlide]?.preview"
              :src="attachments[currentSlide].preview"
              class="w-full h-full object-contain"
              alt="Property image"
          >
          <div v-else class="w-full h-full flex items-center justify-center text-gray-400">
            No preview available
          </div>
        </div>

        <!-- Navigation Arrows -->
        <button
            v-if="attachments.length > 1"
            @click="prevSlide"
            class="absolute left-2 top-1/2 -translate-y-1/2 bg-white/80 hover:bg-white p-2 rounded-full shadow"
            :disabled="currentSlide === 0"
        >
          <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
            <path fill-rule="evenodd" d="M12.707 5.293a1 1 0 010 1.414L9.414 10l3.293 3.293a1 1 0 01-1.414 1.414l-4-4a1 1 0 010-1.414l4-4a1 1 0 011.414 0z" clip-rule="evenodd" />
          </svg>
        </button>
        <button
            v-if="attachments.length > 1"
            @click="nextSlide"
            class="absolute right-2 top-1/2 -translate-y-1/2 bg-white/80 hover:bg-white p-2 rounded-full shadow"
            :disabled="currentSlide === attachments.length - 1"
        >
          <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
            <path fill-rule="evenodd" d="M7.293 14.707a1 1 0 010-1.414L10.586 10 7.293 6.707a1 1 0 011.414-1.414l4 4a1 1 0 010 1.414l-4 4a1 1 0 01-1.414 0z" clip-rule="evenodd" />
          </svg>
        </button>
      </div>

      <!-- Thumbnail Indicators -->
      <div v-if="attachments.length > 1" class="flex justify-center mt-2 space-x-2">
        <button
            v-for="(attachment, index) in attachments"
            :key="attachment.id"
            @click="currentSlide = index"
            class="w-2 h-2 rounded-full transition"
            :class="{
            'bg-blue-500': currentSlide === index,
            'bg-gray-300': currentSlide !== index
          }"
            aria-label="Go to slide"
        ></button>
      </div>

      <!-- File Info and Delete Button -->
      <div class="mt-2 flex items-center justify-between p-2 border rounded">
        <div class="flex-1 min-w-0">
          <p class="text-sm font-medium truncate">{{ attachments[currentSlide].name }}</p>
          <p class="text-xs text-gray-500">{{ (attachments[currentSlide].size / 1024).toFixed(2) }} KB</p>
        </div>
        <button
            type="button"
            @click="removeAttachment(currentSlide)"
            class="ml-2 p-1 text-red-500 hover:text-red-700"
            :disabled="isLoading"
        >
          <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
            <path fill-rule="evenodd" d="M9 2a1 1 0 00-.894.553L7.382 4H4a1 1 0 000 2v10a2 2 0 002 2h8a2 2 0 002-2V6a1 1 0 100-2h-3.382l-.724-1.447A1 1 0 0011 2H9zM7 8a1 1 0 012 0v6a1 1 0 11-2 0V8zm5-1a1 1 0 00-1 1v6a1 1 0 102 0V8a1 1 0 00-1-1z" clip-rule="evenodd" />
          </svg>
        </button>
      </div>
    </div>
  </div>
</template>