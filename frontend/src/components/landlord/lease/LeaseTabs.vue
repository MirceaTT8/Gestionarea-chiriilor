<script setup>
defineProps({
  tabs: {
    type: Array,
    required: true,
    validator: (tabs) => tabs.every(tab => tab.value && tab.label)
  },
  activeTab: {
    type: String,
    required: true
  }
});

const emit = defineEmits(['update:activeTab']);

const setActiveTab = (tabValue) => {
  emit('update:activeTab', tabValue);
};

const tabColors = {
  ACTIVE: {
    active: 'text-green-600 border-b-2 border-green-600',
    icon: 'M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z'
  },
  TERMINATED: {
    active: 'text-red-600 border-b-2 border-red-600',
    icon: 'M10 14l2-2m0 0l2-2m-2 2l-2-2m2 2l2 2m7-2a9 9 0 11-18 0 9 9 0 0118 0z'
  },
  all: {
    active: 'text-blue-600 border-b-2 border-blue-600',
    icon: 'M4 6h16M4 10h16M4 14h16M4 18h16'
  },
  PENDING: {
    active: 'text-orange-600 border-b-2 border-orange-600',
    icon: 'M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z'
  },
  INACTIVE: {
    active: 'text-gray-600 border-b-2 border-gray-600',
    icon: 'M18.364 18.364A9 9 0 005.636 5.636m12.728 12.728A9 9 0 015.636 5.636m12.728 12.728L5.636 5.636'
  }
};

const defaultIcon = 'M19 9l-7 7-7-7';

const getTabIcon = (tabValue) => {
  return tabColors[tabValue]?.icon || defaultIcon;
};

const getTabCount = (tab) => {
  return tab.count !== undefined ? tab.count : null;
};
</script>

<template>
  <div class="bg-white rounded-lg shadow-sm mb-8">
    <div class="flex overflow-x-auto scrollbar-hide">
      <button
          v-for="tab in tabs"
          :key="tab.value"
          @click="setActiveTab(tab.value)"
          :class="[
            'px-6 py-4 font-medium text-gray-500 hover:text-gray-700 focus:outline-none transition-all duration-200 whitespace-nowrap flex items-center',
            activeTab === tab.value
              ? tabColors[tab.value]?.active || 'text-blue-600 border-b-2 border-blue-600'
              : 'hover:bg-gray-50'
          ]"
      >
        <svg
            xmlns="http://www.w3.org/2000/svg"
            class="h-5 w-5 mr-2"
            :class="activeTab === tab.value ? 'opacity-100' : 'opacity-70'"
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
        >
          <path
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              :d="getTabIcon(tab.value)"
          />
        </svg>
        {{ tab.label }}
        <span
            v-if="getTabCount(tab) !== null"
            :class="[
            'ml-2 px-2 py-0.5 text-xs rounded-full',
            activeTab === tab.value
              ? 'bg-current text-white'
              : 'bg-gray-200 text-gray-600'
          ]"
        >
          {{ getTabCount(tab) }}
        </span>
      </button>
    </div>

    <!-- Subtle gradient effect at bottom of tabs -->
    <div class="h-1 w-full bg-gradient-to-r from-blue-50 via-indigo-50 to-blue-50"></div>
  </div>
</template>

<style scoped>
.scrollbar-hide::-webkit-scrollbar {
  display: none;
}

</style>