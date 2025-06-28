<script setup>
import VueCal from 'vue-cal'
import 'vue-cal/dist/vuecal.css'
import { ref } from 'vue'

const props = defineProps({
  calendarEvents: Array
})

const selectedEvent = ref(null)

const selectEvent = (event) => {
  selectedEvent.value = event;
}

const getEventColor = (eventClass) => {
  switch (eventClass) {
    case 'payment-event':
      return {
        bgColor: 'bg-blue-500',
        textColor: 'text-white',
        badgeColor: 'bg-blue-50 text-blue-700',
        ringColor: 'ring-blue-100'
      };
    case 'lease-event':
      return {
        bgColor: 'bg-green-500',
        textColor: 'text-white',
        badgeColor: 'bg-green-50 text-green-700',
        ringColor: 'ring-green-100'
      };
    case 'maintenance-event':
      return {
        bgColor: 'bg-orange-500',
        textColor: 'text-white',
        badgeColor: 'bg-orange-50 text-orange-700',
        ringColor: 'ring-orange-100'
      };
    default:
      return {
        bgColor: 'bg-gray-500',
        textColor: 'text-white',
        badgeColor: 'bg-gray-50 text-gray-700',
        ringColor: 'ring-gray-100'
      };
  }
}

const formatDate = (dateString) => {
  const date = new Date(dateString);
  return date.toLocaleDateString('en-US', {
    weekday: 'long',
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  });
}

const getEventIcon = (eventClass) => {
  switch (eventClass) {
    case 'payment-event':
      return '<svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 9V7a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2m2 4h10a2 2 0 002-2v-6a2 2 0 00-2-2H9a2 2 0 00-2 2v6a2 2 0 002 2zm7-5a2 2 0 11-4 0 2 2 0 014 0z" /></svg>';
    case 'lease-event':
      return '<svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" /></svg>';
    case 'maintenance-event':
      return '<svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z" /><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" /></svg>';
    default:
      return '<svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" /></svg>';
  }
}
</script>

<template>
  <div class="fixed inset-0 bg-black/20 z-50 flex items-center justify-center p-4">
    <div class="bg-white rounded-xl shadow-lg max-w-6xl w-full max-h-[90vh] overflow-hidden">
      <!-- Modal Header -->
      <div class="px-6 py-4 border-b border-gray-100 flex justify-between items-center bg-white">
        <div class="flex items-center gap-3">
          <div class="w-10 h-10 rounded-lg bg-blue-500 flex items-center justify-center">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
            </svg>
          </div>
          <div>
            <h2 class="text-xl font-semibold text-gray-900">Event Calendar</h2>
            <p class="text-sm text-gray-500">{{ calendarEvents?.length || 0 }} events scheduled</p>
          </div>
        </div>

        <button
            @click="$emit('close')"
            class="w-8 h-8 rounded-lg text-gray-400 hover:text-gray-600 hover:bg-gray-100 flex items-center justify-center transition-colors"
        >
          <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
          </svg>
        </button>
      </div>

      <!-- Calendar Content -->
      <div class="flex flex-col lg:flex-row h-[calc(90vh-8rem)]">
        <!-- Calendar View -->
        <div class="lg:w-3/4 border-r border-gray-100">
          <div class="p-4 h-full">
            <VueCal
                :events="calendarEvents"
                :time="false"
                default-view="month"
                @event-focus="selectEvent"
                :hide-weekends="false"
                style="height: 100%; min-height: 500px;"
                :event-content="
                ({ event }) => `
                  <div class='text-white font-medium text-xs px-2 py-1 rounded'>
                    ${event.title}
                  </div>
                `
              "
                :events-on-month-view="'short'"
                :on-event-click="selectEvent"
                class="calendar-clean rounded-lg overflow-hidden border border-gray-200"
            />
          </div>
        </div>

        <!-- Event Details Sidebar -->
        <div class="lg:w-1/4 bg-gray-50">
          <div v-if="selectedEvent" class="p-6 h-full overflow-y-auto">
            <div class="mb-6">
              <div class="inline-flex items-center px-3 py-1.5 rounded-lg text-sm font-medium mb-3"
                   :class="getEventColor(selectedEvent.class).badgeColor">
                <span v-html="getEventIcon(selectedEvent.class)" class="mr-2"></span>
                {{ selectedEvent.title }}
              </div>
              <p class="text-gray-600 text-sm leading-relaxed">{{ selectedEvent.description || 'No additional details provided' }}</p>
            </div>

            <div class="space-y-4">
              <div class="bg-white rounded-lg p-4 border border-gray-200">
                <div class="flex items-center gap-2 mb-2">
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                  </svg>
                  <span class="text-xs font-medium text-gray-500 uppercase tracking-wide">Date</span>
                </div>
                <p class="text-gray-900 font-medium">{{ formatDate(selectedEvent.start) }}</p>
              </div>



              <div v-if="selectedEvent.location" class="bg-white rounded-lg p-4 border border-gray-200">
                <div class="flex items-center gap-2 mb-2">
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z" />
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z" />
                  </svg>
                  <span class="text-xs font-medium text-gray-500 uppercase tracking-wide">Location</span>
                </div>
                <p class="text-gray-900 font-medium">{{ selectedEvent.location }}</p>
              </div>
            </div>
          </div>

          <div v-else class="flex flex-col items-center justify-center h-full p-6 text-center">
            <div class="w-16 h-16 rounded-lg bg-gray-200 flex items-center justify-center mb-4">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-8 w-8 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
              </svg>
            </div>
            <h3 class="text-lg font-medium text-gray-900 mb-2">Select an Event</h3>
            <p class="text-gray-500 text-sm">Click on any event in the calendar to view its details.</p>
          </div>
        </div>
      </div>

      <!-- Modal Footer -->
      <div class="px-6 py-4 border-t border-gray-100 bg-gray-50 flex justify-end">
        <button
            @click="$emit('close')"
            class="px-4 py-2 bg-white border border-gray-300 rounded-lg text-gray-700 font-medium hover:bg-gray-50 transition-colors"
        >
          Close Calendar
        </button>
      </div>
    </div>
  </div>
</template>

<style>
.calendar-clean {
  font-family: inherit;
}

.calendar-clean .vuecal__header {
  background: #4f46e5;
  color: white;
  border: none;
  padding: 1rem;
  border-radius: 0.5rem 0.5rem 0 0;
}

.calendar-clean .vuecal__title {
  color: white;
  font-weight: 600;
  font-size: 1.125rem;
}

.calendar-clean .vuecal__arrow {
  color: white;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 0.375rem;
  padding: 0.5rem;
  transition: all 0.2s;
}

.calendar-clean .vuecal__arrow:hover {
  background: rgba(255, 255, 255, 0.2);
}

.calendar-clean .vuecal__weekdays-headings {
  background: #f9fafb;
  border-bottom: 1px solid #e5e7eb;
  font-weight: 500;
  color: #6b7280;
}

.calendar-clean .vuecal__weekday-label {
  padding: 0.75rem 0.5rem;
  text-transform: uppercase;
  font-size: 0.75rem;
  letter-spacing: 0.05em;
}

.calendar-clean .vuecal__cell {
  border: 1px solid #f3f4f6;
  background: white;
  transition: background-color 0.2s;
}

.calendar-clean .vuecal__cell:hover {
  background: #f9fafb;
}

.calendar-clean .vuecal__cell.today {
  background: #eff6ff;
  border-color: #dbeafe;
}

.calendar-clean .vuecal__cell-content {
  padding: 0.5rem;
  min-height: 4rem;
}

.calendar-clean .vuecal__cell-date {
  font-weight: 500;
  color: #374151;
  margin-bottom: 0.25rem;
  font-size: 0.875rem;
}

.calendar-clean .vuecal__cell.today .vuecal__cell-date {
  color: #2563eb;
  font-weight: 600;
}

.calendar-clean .vuecal__event {
  color: white !important;
  font-size: 0.75rem;
  margin: 0.125rem 0;
  padding: 0.25rem 0.5rem;
  border-radius: 0.375rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  border: none;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
}

.calendar-clean .vuecal__event:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.15);
}

.calendar-clean .vuecal__event.payment-event {
  background: #3b82f6;
}

.calendar-clean .vuecal__event.lease-event {
  background: #10b981;
}

.calendar-clean .vuecal__event.maintenance-event {
  background: #f59e0b;
}

.calendar-clean .vuecal__event.default-event {
  background: #6b7280;
}

/* Scrollbar styling for sidebar */
.lg\:w-1\/4 {
  scrollbar-width: thin;
  scrollbar-color: #d1d5db #f9fafb;
}

.lg\:w-1\/4::-webkit-scrollbar {
  width: 4px;
}

.lg\:w-1\/4::-webkit-scrollbar-track {
  background: #f9fafb;
}

.lg\:w-1\/4::-webkit-scrollbar-thumb {
  background-color: #d1d5db;
  border-radius: 2px;
}

.lg\:w-1\/4::-webkit-scrollbar-thumb:hover {
  background-color: #9ca3af;
}
</style>