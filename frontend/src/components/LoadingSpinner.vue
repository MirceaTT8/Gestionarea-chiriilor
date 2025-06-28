<script setup>
defineProps({
  message: {
    type: String,
    default: 'Loading...'
  }
});
</script>

<template>
  <div class="flex flex-col items-center justify-center text-center py-16">
    <div class="w-12 h-12 border-4 border-t-green-500 border-gray-200 rounded-full animate-spin mb-4"></div>
    <p class="text-gray-600">{{ message }}</p>
  </div>
</template>