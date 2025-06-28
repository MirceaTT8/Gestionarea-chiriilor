<script setup>
import { ref } from 'vue'

const emit = defineEmits(['submit', 'cancel'])

const newRequest = ref({
  description: '',
  attachments: []
})

const fileInput = ref(null)
const loading = ref(false)
const error = ref(null)

const submitRequest = async () => {
  try {
    loading.value = true
    error.value = null

    emit('submit', {
      description: newRequest.value.description,
      attachments: newRequest.value.attachments
    })
  } catch (err) {
    error.value = err.message || 'Something went wrong.'
  } finally {
    loading.value = false
  }
}

const handleFileUpload = (event) => {
  const files = Array.from(event.target.files)
  if (files.length > 0) {
    const selectedFiles = files.slice(0, 5)
    newRequest.value.attachments = selectedFiles.map(file => ({
      file,
      preview: URL.createObjectURL(file)
    }))
  }
}

const removeAttachment = (index) => {
  URL.revokeObjectURL(newRequest.value.attachments[index].preview)
  newRequest.value.attachments.splice(index, 1)
}
</script>

<template>
  <div class="bg-white rounded-lg shadow p-6 space-y-6">
    <h2 class="text-xl font-semibold text-gray-700">Create Maintenance Request</h2>

    <div v-if="error" class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded">
      {{ error }}
    </div>

    <form @submit.prevent="submitRequest" class="space-y-4">
      <div>
        <label for="description" class="block font-medium text-gray-700 mb-1">Description *</label>
        <textarea
            id="description"
            v-model="newRequest.description"
            required
            rows="5"
            placeholder="Describe the issue in detail..."
            class="w-full p-3 border border-gray-300 rounded resize-none focus:outline-none focus:ring focus:ring-green-400"
            :disabled="loading"
        ></textarea>
      </div>

      <div>
        <label class="block font-medium text-gray-700 mb-1">Attach Photos (Max 5)</label>
        <input
            ref="fileInput"
            type="file"
            multiple
            accept="image/*"
            @change="handleFileUpload"
            :disabled="loading || newRequest.attachments.length >= 5"
            class="block w-full text-sm text-gray-500 file:mr-4 file:py-2 file:px-4 file:rounded file:border-0 file:text-sm file:font-semibold file:bg-green-50 file:text-green-700 hover:file:bg-green-100"
        />
        <p class="text-sm text-gray-500 mt-1">
          {{ newRequest.attachments.length }} / 5 files selected
        </p>
        <div class="mt-4 flex flex-wrap gap-4" v-if="newRequest.attachments.length > 0">
          <div
              v-for="(attachment, index) in newRequest.attachments"
              :key="index"
              class="relative w-24 h-24 border rounded overflow-hidden"
          >
            <img :src="attachment.preview" alt="Preview" class="object-cover w-full h-full" />
            <button
                type="button"
                class="absolute top-0 right-0 bg-red-500 text-white rounded-bl px-2 py-0.5 text-sm"
                :disabled="loading"
            >
              ×
            </button>
          </div>
        </div>
      </div>

      <div class="flex justify-end gap-4">
        <button
            type="button"
            @click="$emit('cancel')"
            class="bg-gray-200 hover:bg-gray-300 text-gray-800 py-2 px-4 rounded"
            :disabled="loading"
        >
          Cancel
        </button>
        <button
            type="submit"
            class="bg-green-600 hover:bg-green-700 text-white py-2 px-4 rounded flex items-center justify-center"
            :disabled="loading"
        >
          <span v-if="loading">
            <svg class="animate-spin -ml-1 mr-2 h-4 w-4 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
            Processing...
          </span>
          <span v-else>
            Submit Request
          </span>
        </button>
      </div>
    </form>
  </div>
</template>