<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'
import { useAuthStore } from '@/stores/authStore.js'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const firstName = ref('')
const lastName = ref('')
const password = ref('')
const email = ref('')
const token = ref('')
const error = ref('')

onMounted(() => {
  token.value = route.query.token
})

const register = async () => {
  try {
    const response = await axios.post('http://localhost:8080/auth/register/invited', {
      token: token.value,
      firstName: firstName.value,
      lastName: lastName.value,
      password: password.value
    })

    const jwt = response.data.token
    authStore.login(jwt)

    router.push('/tenant/leases')
  } catch (err) {
    error.value = err.response?.data || 'Registration failed. Please try again.'
  }
}
</script>

<template>
  <div class="min-h-screen bg-gradient-to-br from-emerald-50 via-blue-50 to-purple-50 flex items-center justify-center p-6 relative overflow-hidden">
    <!-- Background decorative elements -->
    <div class="absolute inset-0 overflow-hidden">
      <div class="absolute -top-40 -right-40 w-80 h-80 bg-gradient-to-br from-green-200/30 to-blue-200/30 rounded-full blur-3xl"></div>
      <div class="absolute -bottom-40 -left-40 w-80 h-80 bg-gradient-to-tr from-purple-200/30 to-green-200/30 rounded-full blur-3xl"></div>
    </div>

    <div class="bg-white/80 backdrop-blur-lg w-full max-w-lg p-10 rounded-3xl shadow-2xl border border-white/20 space-y-8 animate-fade-in relative z-10">
      <div class="text-center space-y-3">
        <div class="mx-auto w-16 h-16 bg-gradient-to-br from-green-500 to-emerald-600 rounded-2xl flex items-center justify-center shadow-lg mb-4">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-8 w-8 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z" />
          </svg>
        </div>
        <h1 class="text-3xl font-bold bg-gradient-to-r from-green-600 to-emerald-600 bg-clip-text text-transparent">Welcome to Your Lease</h1>
        <p class="text-gray-600">Complete your registration to access your dashboard.</p>
      </div>

      <form @submit.prevent="register" class="space-y-6" autocomplete="off">
        <div class="relative group">
          <input
              v-model="firstName"
              type="text"
              placeholder="First Name"
              class="w-full border-2 border-gray-200 rounded-2xl px-12 py-4 focus:border-green-500 focus:ring-4 focus:ring-green-500/20 focus:outline-none transition-all duration-300 bg-gray-50/50 group-hover:bg-white group-focus-within:bg-white"
              autocomplete="off"
              autocorrect="off"
              autocapitalize="off"
              spellcheck="false"
              data-form-type="other"
              required
          />
          <div class="absolute left-4 top-1/2 transform -translate-y-1/2 w-8 h-8 bg-green-100 rounded-xl flex items-center justify-center group-focus-within:bg-green-500 group-focus-within:text-white transition-all duration-300">
            <i class="pi pi-user text-green-600 group-focus-within:text-white text-sm"></i>
          </div>
        </div>

        <div class="relative group">
          <input
              v-model="lastName"
              type="text"
              placeholder="Last Name"
              class="w-full border-2 border-gray-200 rounded-2xl px-12 py-4 focus:border-green-500 focus:ring-4 focus:ring-green-500/20 focus:outline-none transition-all duration-300 bg-gray-50/50 group-hover:bg-white group-focus-within:bg-white"
              autocomplete="off"
              autocorrect="off"
              autocapitalize="off"
              spellcheck="false"
              data-form-type="other"
              required
          />
          <div class="absolute left-4 top-1/2 transform -translate-y-1/2 w-8 h-8 bg-green-100 rounded-xl flex items-center justify-center group-focus-within:bg-green-500 group-focus-within:text-white transition-all duration-300">
            <i class="pi pi-user text-green-600 group-focus-within:text-white text-sm"></i>
          </div>
        </div>

        <div class="relative group">
          <input
              v-model="password"
              type="password"
              placeholder="Password"
              class="w-full border-2 border-gray-200 rounded-2xl px-12 py-4 focus:border-green-500 focus:ring-4 focus:ring-green-500/20 focus:outline-none transition-all duration-300 bg-gray-50/50 group-hover:bg-white group-focus-within:bg-white"
              autocomplete="new-password"
              autocorrect="off"
              autocapitalize="off"
              spellcheck="false"
              data-form-type="other"
              required
          />
          <div class="absolute left-4 top-1/2 transform -translate-y-1/2 w-8 h-8 bg-green-100 rounded-xl flex items-center justify-center group-focus-within:bg-green-500 group-focus-within:text-white transition-all duration-300">
            <i class="pi pi-lock text-green-600 group-focus-within:text-white text-sm"></i>
          </div>
        </div>

        <button
            type="submit"
            class="w-full bg-gradient-to-r from-green-600 to-emerald-600 hover:from-green-700 hover:to-emerald-700 text-white font-semibold py-4 rounded-2xl transition-all duration-300 transform hover:scale-[1.02] hover:shadow-xl focus:ring-4 focus:ring-green-500/20 focus:outline-none"
        >
          Create Account
        </button>

        <div v-if="error" class="bg-red-50 border border-red-200 rounded-2xl p-4">
          <p class="text-sm text-red-600 text-center flex items-center justify-center gap-2">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4c-.77-.833-1.732-.833-2.464 0L4.35 16.5c-.77.833.192 2.5 1.732 2.5z" />
            </svg>
            {{ error }}
          </p>
        </div>
      </form>
    </div>
  </div>
</template>

<style scoped>
@keyframes fade-in {
  from {
    opacity: 0;
    transform: translateY(20px) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

.animate-fade-in {
  animation: fade-in 0.6s cubic-bezier(0.4, 0, 0.2, 1) forwards;
}
</style>