<script setup>
defineProps({
  showCreateButton: {
    type: Boolean,
    default: true
  }
})

defineEmits(['create-new'])
</script>

<template>
  <header class="flex flex-col sm:flex-row justify-between items-start sm:items-center">
    <div>
      <h1 class="text-3xl font-bold text-white">Maintenance Requests</h1>
      <p class="mt-1 text-teal-100">Track and manage your property maintenance issues</p>
    </div>

    <button
        v-if="showCreateButton"
        @click="$emit('create-new')"
        class="mt-4 sm:mt-0 inline-flex items-center px-5 py-2.5 border border-transparent text-sm font-medium rounded-lg shadow-sm text-teal-700 bg-white hover:bg-teal-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-offset-teal-600 focus:ring-white transition-colors"
    >
      <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
      </svg>
      New Request
    </button>
  </header>
</template>