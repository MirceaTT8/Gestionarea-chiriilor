<script setup>
defineProps({
  message: {
    type: String,
    required: true
  }
});

const emit = defineEmits(['retry']);
</script>

<template>
  <div class="text-center text-red-600 py-16">
    <p>⚠️ {{ message }}</p>
    <button
        @click="$emit('retry')"
        class="mt-4 bg-orange-500 text-white px-4 py-2 rounded-md hover:bg-orange-600 transition"
    >
      Try Again
    </button>
  </div>
</template>