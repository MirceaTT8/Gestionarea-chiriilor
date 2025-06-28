<script setup>
defineProps({
  currentPage: Number,
  totalPages: Number
})
const emit = defineEmits(['update:currentPage'])
</script>

<template>
  <div v-if="totalPages > 1" class="flex justify-center mt-6 space-x-2">
    <button
        v-for="page in totalPages"
        :key="page"
        @click="emit('update:currentPage', page)"
        class="px-4 py-2 rounded-lg text-sm font-medium transition-all border"
        :class="page === currentPage ? 'bg-blue-600 text-white border-blue-600' : 'bg-white text-gray-700 border-gray-300 hover:bg-gray-100'"
    >
      {{ page }}
    </button>
  </div>
</template>