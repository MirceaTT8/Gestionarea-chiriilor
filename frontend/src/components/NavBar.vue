<script setup>
import { onMounted, onUnmounted, ref, computed, watch } from 'vue'
import { PrimeIcons } from '@primevue/core/api'
import UserDropdown from '@/components/UserDropdown.vue'
import NotificationBell from '@/components/notification/NotificationBell.vue'
import { useAuthStore } from '@/stores/authStore.js'
import { useRoute } from 'vue-router'

const route = useRoute()
const authStore = useAuthStore()

const currentMenu = ref([])
const mobileMenuVisible = ref(false)
const activeDropdown = ref(null)

const menuItems = {
  landlord: [
    { label: 'Dashboard', icon: PrimeIcons.HOME, route: '/landlord' },
    {
      label: 'Properties', icon: PrimeIcons.BUILDING, items: [
        { label: 'My Properties', icon: PrimeIcons.LIST, route: '/landlord/properties' },
        { label: 'Add Property', icon: PrimeIcons.PLUS_CIRCLE, route: '/landlord/add-property' }
      ]
    },
    { label: 'Leases', icon: PrimeIcons.FILE, route: '/landlord/leases' },
    { label: 'Payments', icon: PrimeIcons.MONEY_BILL, route: '/landlord/payments' },
    { label: 'Maintenance', icon: PrimeIcons.WRENCH, route: '/landlord/maintenance' }
  ],
  tenant: [
    { label: 'My Home', icon: PrimeIcons.HOME, route: '/tenant/leases' },
    { label: 'Payments', icon: PrimeIcons.MONEY_BILL, route: '/tenant/payments' },
    { label: 'Maintenance', icon: PrimeIcons.WRENCH, route: '/tenant/maintenance' }
  ],
  admin: [
    { label: 'Dashboard', icon: PrimeIcons.TH_LARGE, route: '/admin/dashboard' },
    { label: 'Leases', icon: PrimeIcons.FILE, route: '/admin/leases' },
    { label: 'Properties', icon: PrimeIcons.BUILDING, route: '/admin/properties' },
    { label: 'Users', icon: PrimeIcons.USERS, route: '/admin/users' },
    { label: 'Payments', icon: PrimeIcons.MONEY_BILL, route: '/admin/payments' },
    { label: 'Maintenance', icon: PrimeIcons.WRENCH, route: '/admin/maintenance' },
    { label: 'Logs', icon: PrimeIcons.LIST, route: '/admin/logs' },
  ]
}

const updateMenu = () => {
  const role = authStore.userRole
  if (role === 'ROLE_LANDLORD') currentMenu.value = menuItems.landlord
  else if (role === 'ROLE_TENANT') currentMenu.value = menuItems.tenant
  else if (role === 'ROLE_ADMIN') currentMenu.value = menuItems.admin
  else currentMenu.value = []
}

const toggleMobileMenu = () => {
  mobileMenuVisible.value = !mobileMenuVisible.value
}

const toggleDropdown = (index) => {
  if (activeDropdown.value === index) {
    activeDropdown.value = null
  } else {
    activeDropdown.value = index
  }
}

const closeDropdown = () => {
  activeDropdown.value = null
}

const handleClickOutside = (event) => {
  if (!event.target.closest('.dropdown-container')) {
    activeDropdown.value = null
  }
}

onMounted(() => {
  updateMenu()
  document.addEventListener('click', handleClickOutside)
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
})

watch(() => authStore.userRole, updateMenu)
watch(() => route.path, updateMenu)

const hideRoutes = ['/login', '/register', '/payment/success', '/invite']
const showNavBar = computed(() => {
  return authStore.isAuthenticated && !hideRoutes.includes(route.path)
})

const isActiveRoute = (itemRoute) => {
  if (route.path === itemRoute) {
    return true;
  }

  if (itemRoute === '/landlord' || itemRoute === '/admin/dashboard') {
    return route.path === itemRoute;
  }

  return route.path.startsWith(itemRoute + '/');
}
</script>

<template>
  <header v-if="showNavBar" class="sticky top-0 z-50 shadow-md">
    <!-- Desktop Navigation -->
    <div class="bg-gradient-to-r from-blue-600 to-blue-700 text-white">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex justify-between h-16">
          <!-- Logo & Main Menu -->
          <div class="flex items-center">
            <!-- Logo -->
            <router-link to="/" class="flex-shrink-0 flex items-center">
              <div class="h-10 w-10 bg-white rounded-full flex items-center justify-center">
                <span class="text-blue-600 text-2xl font-bold">I</span>
              </div>
              <span class="logo ml-2 text-white font-bold text-xl">Immobille</span>
            </router-link>

            <!-- Desktop Menu -->
            <nav class="hidden md:flex ml-10 space-x-1">
              <template v-for="(item, index) in currentMenu" :key="index">
                <!-- Dropdown Menu -->
                <div v-if="item.items" class="relative dropdown-container">
                  <button
                      @click="toggleDropdown(index)"
                      class="px-4 py-2 rounded-md text-sm font-medium flex items-center transition-colors hover:bg-blue-500 focus:outline-none focus:bg-blue-500"
                  >
                    <i :class="item.icon" class="mr-2"></i>
                    <span>{{ item.label }}</span>
                    <i :class="['pi ml-1 text-xs transition-transform', activeDropdown === index ? 'pi-angle-up' : 'pi-angle-down']"></i>
                  </button>
                  <div
                      v-if="activeDropdown === index"
                      class="absolute left-0 mt-1 w-48 bg-white rounded-md shadow-lg py-1 z-50"
                  >
                    <router-link
                        v-for="(subItem, subIndex) in item.items"
                        :key="subIndex"
                        :to="subItem.route"
                        @click="closeDropdown"
                        class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 flex items-center"
                    >
                      <i :class="subItem.icon" class="mr-2 text-blue-600"></i>
                      <span>{{ subItem.label }}</span>
                    </router-link>
                  </div>
                </div>

                <!-- Regular Menu Item -->
                <router-link
                    v-else
                    :to="item.route"
                    :class="['px-4 py-2 rounded-md text-sm font-medium flex items-center transition-colors',
                    isActiveRoute(item.route) ? 'bg-blue-800' : 'hover:bg-blue-500']"
                >
                  <i :class="item.icon" class="mr-2"></i>
                  <span>{{ item.label }}</span>
                </router-link>
              </template>
            </nav>
          </div>

          <!-- Right Side Navigation -->
          <div class="flex items-center space-x-4">
            <NotificationBell class="text-white" />
            <UserDropdown />

            <!-- Mobile menu button -->
            <button
                @click="toggleMobileMenu"
                class="md:hidden bg-blue-500 p-2 rounded-md text-white hover:bg-blue-400 focus:outline-none"
            >
              <i class="pi pi-bars"></i>
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Mobile Navigation -->
    <div v-if="mobileMenuVisible" class="md:hidden bg-blue-800">
      <div class="px-2 pt-2 pb-3 space-y-1">
        <template v-for="(item, index) in currentMenu" :key="index">
          <!-- Mobile Dropdown Menu -->
          <div v-if="item.items" class="space-y-1">
            <div class="px-3 py-2 text-white font-medium flex items-center">
              <i :class="item.icon" class="mr-2"></i>
              <span>{{ item.label }}</span>
            </div>
            <div class="pl-4 space-y-1">
              <router-link
                  v-for="(subItem, subIndex) in item.items"
                  :key="subIndex"
                  :to="subItem.route"
                  class="block px-3 py-2 rounded-md text-sm text-blue-100 hover:bg-blue-700 flex items-center"
                  @click="mobileMenuVisible = false"
              >
                <i :class="subItem.icon" class="mr-2"></i>
                <span>{{ subItem.label }}</span>
              </router-link>
            </div>
          </div>

          <!-- Mobile Regular Menu Item -->
          <router-link
              v-else
              :to="item.route"
              :class="['block px-3 py-2 rounded-md text-sm font-medium flex items-center',
              isActiveRoute(item.route) ? 'bg-blue-900 text-white' : 'text-blue-100 hover:bg-blue-700']"
              @click="mobileMenuVisible = false"
          >
            <i :class="item.icon" class="mr-2"></i>
            <span>{{ item.label }}</span>
          </router-link>
        </template>
      </div>
    </div>
  </header>
</template>

<style scoped>
.logo {
  font-family: 'Montserrat', sans-serif;
  letter-spacing: 0.5px;
  font-weight: 700;
}

/* Smooth transitions */
.router-link-active {
  position: relative;
}

.router-link-active::after {
  content: '';
  position: absolute;
  bottom: -3px;
  left: 50%;
  transform: translateX(-50%);
  width: 20px;
  height: 3px;
  background-color: white;
  border-radius: 3px;
  transition: width 0.3s ease;
}

@media (max-width: 768px) {
  .router-link-active::after {
    display: none;
  }
}
</style>