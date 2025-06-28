<script setup>
import { PrimeIcons } from '@primevue/core/api';
import { useRouter } from 'vue-router';
import { ref, onMounted, onBeforeUnmount } from 'vue';
import { useAuthStore } from '@/stores/authStore.js';
import {fetchUserByEmail} from "@/services/userService.js";
import { jwtDecode} from "jwt-decode";

const router = useRouter();
const authStore = useAuthStore();
const showMenu = ref(false);
const userInitial = ref('U');
const userName = ref('User');

const isLoading = ref(true);

const handleClickOutside = (event) => {
  const dropdown = document.getElementById('user-dropdown');
  if (dropdown && !dropdown.contains(event.target)) {
    showMenu.value = false;
  }
};

onMounted(async () => {
  try {
    const token = localStorage.getItem('token');
    if (token) {
      const decoded = jwtDecode(token);
      const userEmail = decoded.sub;
      const user = await fetchUserByEmail(userEmail);

      if (user) {
        userName.value = `${user.firstName} ${user.lastName}`.trim();

        if (user.firstName && user.lastName) {
          userInitial.value = `${user.firstName.charAt(0)}${user.lastName.charAt(0)}`.toUpperCase();
        } else if (user.firstName) {
          userInitial.value = user.firstName.charAt(0).toUpperCase();
        } else if (user.lastName) {
          userInitial.value = user.lastName.charAt(0).toUpperCase();
        } else {
          userInitial.value = userEmail.charAt(0).toUpperCase();
        }
      }
    }
  } catch (error) {
    console.error('Error fetching user data:', error);
  } finally {
    isLoading.value = false;
  }

  document.addEventListener('click', handleClickOutside);
});

onBeforeUnmount(() => {
  document.removeEventListener('click', handleClickOutside);
});

const logout = () => {
  localStorage.removeItem('token');
  authStore.logout && authStore.logout();
  router.push('/login');
};

const settings = ref([
  {
    label: "My Profile",
    icon: PrimeIcons.USER,
    route: "/profile",
    description: "View and edit your profile"
  },
  {
    label: "Logout",
    icon: PrimeIcons.SIGN_OUT,
    command: logout,
    description: "Sign out of your account"
  }
]);

const navigateTo = (route) => {
  showMenu.value = false;
  router.push(route);
};

const handleClick = () => {
  showMenu.value = !showMenu.value;
};
</script>

<template>
  <div id="user-dropdown" class="relative">
    <button
        @click="handleClick"
        class="flex items-center rounded-full transition-all duration-300 focus:outline-none"
        :class="{ 'ring-2 ring-blue-300 ring-offset-2 ring-offset-blue-600': showMenu }"
    >
      <div class="flex items-center">
        <!-- User Avatar -->
        <div class="relative">
          <div class="w-10 h-10 bg-white text-blue-600 font-bold rounded-full flex items-center justify-center shadow-sm">
            {{ userInitial }}
          </div>
          <div class="absolute bottom-0 right-0 w-3 h-3 bg-green-500 border-2 border-blue-700 rounded-full"></div>
        </div>

        <!-- Down Arrow - Hide on Mobile -->
        <div class="hidden md:flex ml-2 text-white items-center">
          <span class="text-xs font-medium mr-1">
            <i class="pi pi-chevron-down"></i>
          </span>
        </div>
      </div>
    </button>

    <!-- Dropdown Menu -->
    <transition
        enter-active-class="transition ease-out duration-200"
        enter-from-class="opacity-0 -translate-y-2 scale-95"
        enter-to-class="opacity-100 translate-y-0 scale-100"
        leave-active-class="transition ease-in duration-150"
        leave-from-class="opacity-100 translate-y-0 scale-100"
        leave-to-class="opacity-0 -translate-y-2 scale-95"
    >
      <div
          v-if="showMenu"
          class="absolute right-0 mt-2 w-72 bg-white rounded-xl shadow-xl z-50 overflow-hidden border border-gray-200"
      >
        <!-- User Header Section -->
        <div class="bg-gradient-to-r from-blue-600 to-blue-700 text-white px-5 py-4">
          <div class="flex items-center">
            <div class="w-12 h-12 bg-white text-blue-600 font-bold rounded-full flex items-center justify-center shadow">
              {{ userInitial }}
            </div>
            <div class="ml-3">
              <div class="font-bold text-lg">{{ userName }}</div>
              <div class="text-xs text-blue-100">
                {{ authStore.userRole ? authStore.userRole.replace('ROLE_', '') : 'User' }}
              </div>
            </div>
          </div>
        </div>

        <!-- Divider -->
        <div class="border-b border-gray-200"></div>

        <!-- Menu Items -->
        <div class="py-1">
          <div
              v-for="item in settings"
              :key="item.label"
              @click="item.command ? item.command() : navigateTo(item.route)"
              class="px-4 py-3 hover:bg-gray-50 cursor-pointer transition-colors"
          >
            <div class="flex items-center">
              <div class="w-9 h-9 rounded-full flex items-center justify-center bg-blue-100 text-blue-600">
                <i :class="item.icon"></i>
              </div>
              <div class="ml-3">
                <div class="text-sm font-medium text-gray-900">{{ item.label }}</div>
                <div class="text-xs text-gray-500">{{ item.description }}</div>
              </div>
            </div>
          </div>
        </div>

        <!-- Footer with version info -->
        <div class="bg-gray-50 px-4 py-2 text-xs text-gray-500 text-center border-t border-gray-200">
          Immobille v1.2.0
        </div>
      </div>
    </transition>
  </div>
</template>

<style scoped>
/* Custom animation for the dropdown */
.dropdown-enter-active, .dropdown-leave-active {
  transition: all 0.3s;
}
.dropdown-enter-from, .dropdown-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

/* Enhance the avatar appearance */
button:hover .w-10 {
  transform: scale(1.05);
  transition: transform 0.2s ease;
}
</style>