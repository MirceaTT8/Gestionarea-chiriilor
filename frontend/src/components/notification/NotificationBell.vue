<script setup>
import { ref, onMounted, computed, onBeforeUnmount, watch } from 'vue';
import { useNotificationStore } from '@/stores/notificationStore.js';
import { storeToRefs } from 'pinia';
import { markNotificationAsRead, markAllNotificationAsReadByEmail } from '@/services/notificationService';
import { formatTimeAgo } from '@/utils/dateUtils';

const notificationStore = useNotificationStore();
const { unreadCount, notifications } = storeToRefs(notificationStore);
const showDropdown = ref(false);
const bellRinging = ref(false);
const dropdownRef = ref(null);

const handleClickOutside = (event) => {
  if (dropdownRef.value && !dropdownRef.value.contains(event.target)) {
    showDropdown.value = false;
  }
};

const animateBell = () => {
  bellRinging.value = true;
  setTimeout(() => {
    bellRinging.value = false;
  }, 2000);
};

watch(unreadCount, (newCount, oldCount) => {
  if (newCount > oldCount && oldCount !== undefined) {
    animateBell();
  }
});

onMounted(() => {
  notificationStore.fetchNotification();
  document.addEventListener('click', handleClickOutside);

  if (unreadCount.value > 0) {
    setTimeout(() => {
      animateBell();
    }, 1000);
  }
});

onBeforeUnmount(() => {
  document.removeEventListener('click', handleClickOutside);
});

const handleNotificationClick = async (notification) => {
  try {
    if (!notification.read) {
      await markNotificationAsRead(notification.notificationId);
      notification.read = true;
      notification.status = 'READ';
      unreadCount.value--;
    }
  } catch (error) {
    console.error('Failed to mark notification as read:', error);
  }
};

const getUserEmail = () => {
  const userEmail = localStorage.getItem('userEmail');
  if (userEmail) return userEmail;

  try {
    const token = localStorage.getItem('token');
    if (token) {
      const payload = JSON.parse(atob(token.split('.')[1]));
      return payload.email || payload.sub || payload.username;
    }
  } catch (error) {
    console.error('Error decoding token:', error);
  }

  return null;
};

const handleMarkAllAsRead = async () => {
  try {
    const userEmail = getUserEmail();
    if (!userEmail) {
      console.error('User email not found');
      return;
    }

    await markAllNotificationAsReadByEmail(userEmail);

    notifications.value.forEach(notification => {
      if (!notification.read) {
        notification.read = true;
        notification.status = 'READ';
      }
    });

    unreadCount.value = 0;

  } catch (error) {
    console.error('Failed to mark all notifications as read:', error);
  }
};

const formatStatusText = (text) => {
  if (!text) return text;

  return text
      .replace(/\bIN_PROGRESS\b/g, 'In Progress')
      .replace(/\bCANCELLED\b/g, 'Cancelled')
      .replace(/\bCOMPLETED\b/g, 'Completed')
      .replace(/\bPENDING\b/g, 'Pending');
};

const sortedNotifications = computed(() => {
  return [...notifications.value].sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
});

const groupedNotifications = computed(() => {
  const groups = {};
  const today = new Date().setHours(0, 0, 0, 0);
  const yesterday = new Date(today - 86400000).setHours(0, 0, 0, 0);

  sortedNotifications.value.forEach(notification => {
    const date = new Date(notification.createdAt).setHours(0, 0, 0, 0);
    let group;

    if (date === today) {
      group = 'Today';
    } else if (date === yesterday) {
      group = 'Yesterday';
    } else {
      group = new Date(date).toLocaleDateString('en-US', { month: 'short', day: 'numeric' });
    }

    if (!groups[group]) {
      groups[group] = [];
    }

    groups[group].push(notification);
  });

  return groups;
});

const getNotificationIcon = (notification) => {
  const type = notification.type?.toLowerCase() || '';

  if (type.includes('payment')) return 'pi pi-money-bill';
  if (type.includes('lease')) return 'pi pi-file';
  if (type.includes('maintenance')) return 'pi pi-wrench';
  if (type.includes('property')) return 'pi pi-home';
  if (type.includes('message')) return 'pi pi-envelope';

  return 'pi pi-bell';
};

const getNotificationColor = (notification) => {
  const type = notification.type?.toLowerCase() || '';

  if (type.includes('payment')) return 'bg-green-100 text-green-700';
  if (type.includes('lease')) return 'bg-blue-100 text-blue-700';
  if (type.includes('maintenance')) return 'bg-yellow-100 text-yellow-700';
  if (type.includes('property')) return 'bg-indigo-100 text-indigo-700';
  if (type.includes('message')) return 'bg-purple-100 text-purple-700';

  return 'bg-gray-100 text-gray-700';
};
</script>

<template>
  <div class="relative" ref="dropdownRef">
    <!-- Notification Bell Button -->
    <button
        @click="showDropdown = !showDropdown"
        class="relative flex items-center justify-center w-10 h-10 rounded-full focus:outline-none focus:ring-2 focus:ring-blue-300 focus:ring-offset-2 focus:ring-offset-blue-600 hover:bg-blue-500 transition-colors"
        :class="{ 'bg-blue-500': showDropdown }"
    >
      <!-- Bell Icon -->
      <i
          class="pi pi-bell text-white text-xl"
          :class="{ 'bell-animation': bellRinging }"
      ></i>

      <!-- Notification Badge -->
      <span
          v-if="unreadCount > 0"
          class="absolute top-0 right-0 flex items-center justify-center min-w-[1.25rem] h-5 px-1 text-xs font-bold text-white bg-red-600 rounded-full shadow-sm transform translate-x-1/3 -translate-y-1/3 ring-2 ring-blue-600"
      >
        {{ unreadCount > 99 ? '99+' : unreadCount }}
      </span>
    </button>

    <!-- Dropdown Panel -->
    <transition
        enter-active-class="transition ease-out duration-200"
        enter-from-class="opacity-0 scale-95"
        enter-to-class="opacity-100 scale-100"
        leave-active-class="transition ease-in duration-150"
        leave-from-class="opacity-100 scale-100"
        leave-to-class="opacity-0 scale-95"
    >
      <div
          v-if="showDropdown"
          class="absolute right-0 mt-3 w-96 bg-white rounded-xl shadow-xl overflow-hidden z-50 border border-gray-200"
      >
        <!-- Header -->
        <div class="px-4 py-3 bg-gradient-to-r from-blue-600 to-blue-700 text-white flex justify-between items-center">
          <h3 class="font-semibold flex items-center">
            <i class="pi pi-bell mr-2"></i>
            Notifications
          </h3>
          <div class="text-sm">
            <span v-if="unreadCount > 0">{{ unreadCount }} unread</span>
          </div>
        </div>

        <!-- Notification List -->
        <div class="py-2 max-h-[28rem] overflow-y-auto">
          <div v-if="notifications.length === 0" class="flex flex-col items-center justify-center py-8 px-4">
            <div class="w-16 h-16 bg-blue-100 rounded-full flex items-center justify-center mb-4">
              <i class="pi pi-bell text-blue-500 text-2xl"></i>
            </div>
            <p class="text-gray-500 text-center">No notifications to display</p>
          </div>

          <template v-else>
            <div v-for="(notifs, date) in groupedNotifications" :key="date">
              <!-- Date Header -->
              <div class="px-4 py-2 bg-gray-50 text-xs font-medium text-gray-500 uppercase tracking-wider sticky top-0 z-10 border-b border-t first:border-t-0">
                {{ date }}
              </div>

              <!-- Notifications for this date -->
              <div
                  v-for="notification in notifs"
                  :key="notification.notificationId"
                  @click="handleNotificationClick(notification)"
                  class="px-4 py-3 hover:bg-gray-50 cursor-pointer border-b border-gray-100 transition-all flex items-start gap-3"
                  :class="{ 'opacity-75': notification.read }"
              >
                <!-- Icon -->
                <div :class="`w-10 h-10 rounded-full flex items-center justify-center ${getNotificationColor(notification)}`">
                  <i :class="`${getNotificationIcon(notification)}`"></i>
                </div>

                <!-- Content -->
                <div class="flex-1 min-w-0">
                  <div class="flex justify-between items-start">
                    <div class="font-medium text-gray-900 truncate" :class="{ 'text-gray-600': notification.read }">
                      {{ notification.title }}
                    </div>
                    <div class="text-xs text-gray-500 whitespace-nowrap ml-2">
                      {{ formatTimeAgo(notification.createdAt) }}
                    </div>
                  </div>
                  <div class="text-sm text-gray-500 mt-1">{{ formatStatusText(notification.message) }}</div>
                </div>

                <!-- Unread Indicator -->
                <div v-if="!notification.read" class="w-2 h-2 bg-blue-500 rounded-full mt-2 flex-shrink-0"></div>
              </div>
            </div>
          </template>
        </div>
        <div class="px-4 py-3 text-sm bg-gray-50 border-t border-gray-200 flex justify-between items-center">

          <button
              v-if="unreadCount > 0"
              class="text-gray-600 hover:text-gray-800 text-sm"
              @click="handleMarkAllAsRead"
          >
            Mark all as read
          </button>
        </div>
      </div>
    </transition>
  </div>
</template>

<style scoped>
/* Bell Animation */
@keyframes bell-ring {
  0% { transform: rotate(0); }
  10% { transform: rotate(15deg); }
  20% { transform: rotate(-10deg); }
  30% { transform: rotate(8deg); }
  40% { transform: rotate(-6deg); }
  50% { transform: rotate(4deg); }
  60% { transform: rotate(-2deg); }
  70% { transform: rotate(1deg); }
  80% { transform: rotate(-1deg); }
  100% { transform: rotate(0); }
}

.bell-animation {
  animation: bell-ring 0.9s cubic-bezier(.36,.07,.19,.97) both;
  transform-origin: top center;
}

/* Smooth transitions */
.transition-all {
  transition: all 0.2s ease;
}

/* Custom scrollbar */
.overflow-y-auto {
  scrollbar-width: thin;
  scrollbar-color: rgba(156, 163, 175, 0.5) transparent;
}

.overflow-y-auto::-webkit-scrollbar {
  width: 6px;
}

.overflow-y-auto::-webkit-scrollbar-track {
  background: transparent;
}

.overflow-y-auto::-webkit-scrollbar-thumb {
  background-color: rgba(156, 163, 175, 0.5);
  border-radius: 6px;
}

/* Hover effect for notifications */
.hover\:bg-gray-50:hover {
  background-color: #f9fafb;
}
</style>