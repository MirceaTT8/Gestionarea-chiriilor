<script setup>
defineProps({
  lease: {
    type: Object,
    required: true
  }
});
</script>

<template>
  <section>
    <h2 class="text-xl font-semibold text-gray-700 border-b pb-2 mb-4">Lease Documents</h2>
    <div class="flex flex-wrap gap-4">
      <button class="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700">
        📄 Download Lease Agreement
      </button>
      <button class="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700">
        🏠 Property Rules
      </button>
    </div>
  </section>
</template>