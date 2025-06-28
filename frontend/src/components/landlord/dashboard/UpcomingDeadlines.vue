<script setup>
import { computed } from 'vue'

const props = defineProps({
  calendarEvents: Array
})

defineEmits(['view-calendar'])

const latestEvents = computed(() => {
  if (!props.calendarEvents?.length) return []

  return props.calendarEvents
      .sort((a, b) => new Date(b.start || b.date) - new Date(a.start || a.date))
      .slice(0, 5)
})
</script>

<template>
  <section class="col-span-full md:col-span-4 bg-white rounded-lg shadow p-6">
    <div class="flex justify-between items-center mb-4">
      <h2 class="text-xl font-semibold text-gray-700">Calendar Events</h2>
      <button class="text-sm text-blue-600 hover:underline" @click="$emit('view-calendar')">
        View Calendar
      </button>
    </div>

    <div v-if="latestEvents.length === 0" class="flex flex-col items-center justify-center py-12">
      <div class="w-16 h-16 rounded-2xl bg-gradient-to-br from-blue-100 to-indigo-200 flex items-center justify-center mb-4">
        <svg xmlns="http://www.w3.org/2000/svg" class="h-8 w-8 text-blue-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
        </svg>
      </div>
      <h3 class="text-lg font-medium text-gray-900 mb-2">No Events</h3>
      <p class="text-sm text-gray-500 text-center leading-relaxed">
        When you have calendar events, they'll appear here.
      </p>
    </div>

    <div v-else class="space-y-3">
      <div
          v-for="(event, index) in latestEvents"
          :key="index"
          class="p-3 border-l-4 bg-blue-50 rounded-r-lg transition-colors hover:bg-blue-100"
          :class="{
          'border-blue-500': event.class === 'payment-event',
          'border-green-500': event.class === 'lease-event',
          'border-red-500': event.class === 'maintenance-event'
        }"
      >
        <p class="font-medium text-gray-800">{{ event.title }}</p>
        <p class="text-sm text-gray-600">{{ new Date(event.start).toLocaleDateString() }}</p>
      </div>
    </div>
  </section>
</template>