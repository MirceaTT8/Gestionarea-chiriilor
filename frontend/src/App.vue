<script setup>
import { ref } from 'vue'
import { useRouter, RouterView } from 'vue-router'
import { jwtDecode } from 'jwt-decode'
import NavBar from '@/components/NavBar.vue'
import { useNotificationStore } from '@/stores/notificationStore'
import { useAuthStore} from "@/stores/authStore.js";

const router = useRouter()
const isAuthenticated = ref(false)
const notificationStore = useNotificationStore()
const authStore = useAuthStore()
const token = localStorage.getItem("token")


if (token) {
  try {
    const decoded = jwtDecode(token)
    const roles = decoded.authorities || []
    isAuthenticated.value = true
    notificationStore.startPolling()
  } catch (err) {
    localStorage.removeItem("token")
    router.replace("/login")
  }
}
</script>

<template>
  <main>
    <NavBar v-if="authStore.isAuthenticated" />
    <RouterView class="router-view" />
  </main>
</template>

<style scoped>
main {
  width: 100%;
}
</style>
