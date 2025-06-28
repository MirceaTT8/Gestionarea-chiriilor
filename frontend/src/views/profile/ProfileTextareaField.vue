<script setup>
defineProps({
  modelValue: {
    type: String,
    default: ''
  },
  label: {
    type: String,
    required: true
  },
  rows: {
    type: Number,
    default: 3
  }
});

const emit = defineEmits(['update:modelValue']);
</script>

<template>
  <div>
    <label class="block text-sm font-medium text-gray-700 mb-1">{{ label }}</label>
    <textarea
        :value="modelValue"
        @input="emit('update:modelValue', $event.target.value)"
        :rows="rows"
        class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
    ></textarea>
  </div>
</template>