<script setup>
const props = defineProps({
  activities: {
    type: Array,
    required: true
  }
})

const emit = defineEmits(['view-all'])

const getActivityIcon = (activity) => {
  const description = activity.description.toLowerCase();

  if (description.includes('payment') || description.includes('paid')) {
    return '💰';
  } else if (description.includes('maintenance') || description.includes('repair')) {
    return '🔧';
  } else if (description.includes('lease') || description.includes('signed')) {
    return '📄';
  } else if (description.includes('message') || description.includes('contacted')) {
    return '✉️';
  } else {
    return '📌';
  }
}

const formatRelativeTime = (timeString) => {
  return timeString;
}

const getInitial = (description) => {
  const nameMatch = description.match(/\b([A-Z][a-z]+)\b/);
  return nameMatch ? nameMatch[1].charAt(0) : '?';
}
</script>

<template>
  <section class="col-span-full md:col-span-4 bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
    <!-- Section Header -->
    <div class="px-6 py-4 border-b border-gray-100 flex justify-between items-center">
      <div class="flex items-center">
        <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-gray-500 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 8h10M7 12h4m1 8l-4-4H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-3l-4 4z" />
        </svg>
        <h2 class="text-lg font-semibold text-gray-800">Recent Activity</h2>
      </div>

      <button
          v-if="activities.length > 5"
          @click="emit('view-all')"
          class="text-sm text-blue-600 hover:text-blue-800 font-medium flex items-center"
      >
        View All
        <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 ml-1" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
        </svg>
      </button>
    </div>

    <!-- Empty State -->
    <div v-if="activities.length === 0" class="flex flex-col items-center justify-center py-12 px-6">
      <div class="w-16 h-16 rounded-2xl bg-gradient-to-br from-gray-100 to-gray-200 flex items-center justify-center mb-4">
        <svg xmlns="http://www.w3.org/2000/svg" class="h-8 w-8 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M7 8h10M7 12h4m1 8l-4-4H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-3l-4 4z" />
        </svg>
      </div>
      <h3 class="text-lg font-medium text-gray-900 mb-2">No Recent Activity</h3>
      <p class="text-sm text-gray-500 text-center leading-relaxed">
        When you start managing properties, tenant interactions, and maintenance requests, your recent activity will appear here.
      </p>
    </div>

    <!-- Activity List -->
    <div v-else class="divide-y divide-gray-100">
      <div
          v-for="(activity, index) in activities.slice(0, 5)"
          :key="activity.id"
          class="flex gap-4 items-center px-6 py-4 hover:bg-gray-50 transition-colors"
      >
        <!-- Activity Icon/Avatar -->
        <div v-if="activity.icon === '👤' || activity.icon === '👥'"
             class="w-10 h-10 rounded-full bg-blue-100 text-blue-800 flex items-center justify-center text-sm font-semibold flex-shrink-0">
          {{ getInitial(activity.description) }}
        </div>
        <div v-else
             class="w-10 h-10 rounded-full flex items-center justify-center text-xl flex-shrink-0"
             :class="{
               'bg-green-100': getActivityIcon(activity) === '💰',
               'bg-orange-100': getActivityIcon(activity) === '🔧',
               'bg-purple-100': getActivityIcon(activity) === '📄',
               'bg-blue-100': getActivityIcon(activity) === '✉️',
               'bg-gray-100': getActivityIcon(activity) === '📌'
             }">
          {{ getActivityIcon(activity) }}
        </div>

        <!-- Description and time -->
        <div class="flex-1 min-w-0">
          <p
              class="font-medium text-gray-800 leading-tight"
              :title="activity.description"
          >
            {{ activity.description }}
          </p>
          <p class="text-xs text-gray-500 mt-1 flex items-center">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-3 w-3 mr-1" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
            {{ formatRelativeTime(activity.time) }}
          </p>
        </div>
      </div>
    </div>

    <!-- View All Button (Mobile) -->
    <div v-if="activities.length > 5" class="px-6 py-3 border-t border-gray-100 md:hidden">
      <button
          @click="emit('view-all')"
          class="w-full py-2 bg-gray-100 hover:bg-gray-200 text-gray-800 text-sm font-medium rounded-lg flex items-center justify-center"
      >
        View All Activities
        <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 ml-1" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
        </svg>
      </button>
    </div>
  </section>
</template>