<script setup>
const props = defineProps({
  activities: {
    type: Array,
    required: true
  }
})

const emit = defineEmits(['close'])

const getActivityIcon = (activity) => {

  if (activity.icon) return activity.icon;

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

const parseRelativeTime = (timeString) => {
  const now = new Date();

  if (timeString === 'yesterday') {
    const yesterday = new Date(now);
    yesterday.setDate(now.getDate() - 1);
    return yesterday;
  } else if (timeString.includes('days ago')) {
    const daysAgo = parseInt(timeString.match(/(\d+) days ago/)[1]);
    const date = new Date(now);
    date.setDate(now.getDate() - daysAgo);
    return date;
  } else if (timeString === 'today') {
    return now;
  } else {
    const parsed = new Date(timeString);
    return isNaN(parsed.getTime()) ? now : parsed;
  }
}

const groupActivitiesByDate = () => {
  const groups = {};

  props.activities.forEach(activity => {
    const activityDate = parseRelativeTime(activity.time);
    const dateKey = activityDate.toDateString();

    if (!groups[dateKey]) {
      groups[dateKey] = [];
    }
    groups[dateKey].push(activity);
  });

  const sortedGroups = {};
  const sortedDates = Object.keys(groups).sort((b, a) => {
    const dateA = new Date(a);
    const dateB = new Date(b);
    return dateA - dateB;
  });

  sortedDates.forEach(date => {
    sortedGroups[date] = groups[date];
  });

  return sortedGroups;
}


const getInitial = (description) => {
  const nameMatch = description.match(/\b([A-Z][a-z]+)\b/);
  return nameMatch ? nameMatch[1].charAt(0) : '?';
}
</script>

<template>
  <div class="fixed inset-0 bg-black/20 z-40 flex items-center justify-center p-4 animate-in fade-in duration-200">
    <div class="bg-white rounded-2xl shadow-2xl max-w-2xl w-full max-h-[85vh] overflow-hidden relative animate-in zoom-in-95 duration-200 border border-gray-200/50 z-50">
      <!-- Modal Header -->
      <div class="px-6 py-5 border-b border-gray-100/80 flex justify-between items-center sticky top-0 bg-white/95 backdrop-blur z-10">
        <div class="flex items-center gap-3">
          <div class="w-10 h-10 rounded-xl bg-gradient-to-br from-indigo-500 to-purple-600 flex items-center justify-center">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
          </div>
          <div>
            <h2 class="text-xl font-bold text-gray-900">Activity History</h2>
            <p class="text-sm text-gray-500">{{ activities.length }} recent activities</p>
          </div>
        </div>

        <button
            @click="emit('close')"
            class="w-10 h-10 rounded-xl text-gray-400 hover:text-gray-600 hover:bg-gray-100 focus:outline-none focus:ring-2 focus:ring-indigo-500/20 transition-all duration-200 flex items-center justify-center"
        >
          <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
          </svg>
        </button>
      </div>

      <!-- Modal Content -->
      <div class="overflow-y-auto px-6 py-4 max-h-[calc(85vh-8rem)] custom-scrollbar">
        <!-- Activities List -->
        <div v-for="(activities, date) in groupActivitiesByDate()" :key="date" class="space-y-3 mb-8 last:mb-4">
          <div
              v-for="activity in activities"
              :key="activity.id"
              class="group flex gap-4 items-start p-4 rounded-xl hover:bg-gradient-to-r hover:from-gray-50 hover:to-indigo-50/30 transition-all duration-200 border border-transparent hover:border-indigo-100/50"
          >
            <!-- Activity Icon/Avatar -->
            <div v-if="activity.icon === '👤' || activity.icon === '👥'"
                 class="w-12 h-12 rounded-xl bg-white border-2 border-blue-100 text-blue-700 flex items-center justify-center text-sm font-bold flex-shrink-0 shadow-sm group-hover:scale-105 transition-transform duration-200">
              {{ getInitial(activity.description) }}
            </div>
            <div v-else
                 class="w-12 h-12 rounded-xl flex items-center justify-center text-xl flex-shrink-0 shadow-sm group-hover:scale-105 transition-transform duration-200 border-2"
                 :class="{
                     'bg-gradient-to-br from-green-50 to-emerald-100 border-green-200': getActivityIcon(activity) === '💰',
                     'bg-gradient-to-br from-orange-50 to-amber-100 border-orange-200': getActivityIcon(activity) === '🔧',
                     'bg-gradient-to-br from-purple-50 to-violet-100 border-purple-200': getActivityIcon(activity) === '📄',
                     'bg-gradient-to-br from-blue-50 to-sky-100 border-blue-200': getActivityIcon(activity) === '✉️',
                     'bg-gradient-to-br from-gray-50 to-slate-100 border-gray-200': getActivityIcon(activity) === '📌'
                   }">
              {{ getActivityIcon(activity) }}
            </div>

            <!-- Description and time -->
            <div class="flex-1 min-w-0">
              <p class="font-medium text-gray-900 leading-relaxed mb-2 group-hover:text-gray-800 transition-colors">
                {{ activity.description }}
              </p>
              <div class="flex items-center text-xs text-gray-500 bg-gray-50 rounded-lg px-3 py-1.5 w-fit">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-3.5 w-3.5 mr-1.5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
                <span class="font-medium">{{ activity.time }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- Empty State -->
        <div v-if="activities.length === 0" class="flex flex-col items-center justify-center py-16">
          <div class="w-20 h-20 rounded-2xl bg-gradient-to-br from-gray-100 to-gray-200 flex items-center justify-center mb-4">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-10 w-10 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M7 8h10M7 12h4m1 8l-4-4H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-3l-4 4z" />
            </svg>
          </div>
          <h3 class="text-lg font-semibold text-gray-900 mb-2">No Activity Yet</h3>
          <p class="text-gray-500 text-center max-w-sm">Your activity history will appear here as you manage your properties.</p>
        </div>
      </div>

      <!-- Modal Footer -->
      <div class="px-6 py-4 border-t border-gray-100/80 bg-gradient-to-r from-gray-50/50 to-white flex justify-end">
        <button
            @click="emit('close')"
            class="px-6 py-2.5 bg-white border border-gray-200 rounded-xl text-gray-700 font-medium hover:bg-gray-50 hover:border-gray-300 focus:outline-none focus:ring-2 focus:ring-indigo-500/20 transition-all duration-200 shadow-sm"
        >
          Close
        </button>
      </div>
    </div>
  </div>


</template>

<style scoped>
.custom-scrollbar {
  scrollbar-width: thin;
  scrollbar-color: rgb(203 213 225) transparent;
}

.custom-scrollbar::-webkit-scrollbar {
  width: 6px;
}

.custom-scrollbar::-webkit-scrollbar-track {
  background: transparent;
}

.custom-scrollbar::-webkit-scrollbar-thumb {
  background-color: rgb(203 213 225);
  border-radius: 3px;
}

.custom-scrollbar::-webkit-scrollbar-thumb:hover {
  background-color: rgb(148 163 184);
}

.animate-in {
  animation-fill-mode: both;
}

.fade-in {
  animation: fadeIn 0.2s ease-out;
}

.zoom-in-95 {
  animation: zoomIn95 0.2s ease-out;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

@keyframes zoomIn95 {
  from {
    opacity: 0;
    transform: scale(0.95);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}
</style>