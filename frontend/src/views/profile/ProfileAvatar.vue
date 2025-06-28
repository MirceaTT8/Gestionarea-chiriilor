<script setup>
defineProps({
  user: {
    type: Object,
    required: true
  },
  size: {
    type: String,
    default: 'w-24 h-24'
  }
});
</script>

<template>
  <div
      class="rounded-full bg-gray-200 flex items-center justify-center overflow-hidden"
      :class="size"
  >
    <template v-if="user.avatar">
      <img :src="user.avatar" :alt="`${user.firstName}'s avatar`" class="w-full h-full object-cover">
    </template>
    <template v-else>
      <span class="text-3xl text-gray-600 font-semibold">
        {{ user.firstName?.charAt(0) }}{{ user.lastName?.charAt(0) }}
      </span>
    </template>
  </div>
</template>