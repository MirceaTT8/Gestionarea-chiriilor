<script setup>
import { ref, reactive } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';
import { useAuthStore } from '@/stores/authStore.js';

const credentials = reactive({
  email: '',
  password: ''
});
const error = ref('');
const isLoading = ref(false);
const rememberMe = ref(false);
const router = useRouter();
const authStore = useAuthStore();

const login = async () => {
  if (!credentials.email || !credentials.password) {
    error.value = 'Please enter both email and password.';
    return;
  }

  try {
    isLoading.value = true;
    error.value = '';

    const response = await axios.post('http://localhost:8080/auth/login', {
      email: credentials.email,
      password: credentials.password
    });

    const token = response.data.token;
    authStore.login(token, rememberMe.value);

    switch (authStore.userRole) {
      case 'ROLE_LANDLORD':
        router.push('/landlord');
        break;
      case 'ROLE_TENANT':
        router.push('/tenant/leases');
        break;
      case 'ROLE_ADMIN':
        router.push('/admin/dashboard');
        break;
      default:
        error.value = 'Unauthorized role.';
    }
  } catch (err) {
    if (err.response && err.response.status === 401) {
      error.value = 'Invalid email or password. Please try again.';
    } else {
      error.value = 'An error occurred. Please try again later.';
    }
  } finally {
    isLoading.value = false;
  }
};
</script>

<template>
  <div class="min-h-screen flex items-center justify-center bg-gradient-to-r from-blue-500 to-blue-700 p-6">
    <div class="w-full max-w-md">
      <!-- Logo/Brand -->
      <div class="text-center mb-6">
        <div class="inline-flex items-center justify-center w-16 h-16 bg-white rounded-full shadow-lg mb-4">
          <span class="text-blue-600 text-3xl font-bold">I</span>
        </div>
        <h1 class="text-2xl font-bold text-white">Immobille</h1>
        <p class="text-blue-100 text-sm">Lease Management Simplified</p>
      </div>

      <!-- Login Card -->
      <div class="relative bg-white rounded-2xl shadow-2xl overflow-hidden">
        <!-- Decorative Elements -->
        <div class="absolute top-0 left-0 w-full h-2 bg-gradient-to-r from-blue-400 to-blue-600"></div>
        <div class="absolute top-0 right-0 w-16 h-16 transform translate-x-8 -translate-y-8 bg-blue-400 rounded-full opacity-20"></div>
        <div class="absolute bottom-0 left-0 w-24 h-24 transform -translate-x-12 translate-y-12 bg-blue-500 rounded-full opacity-10"></div>

        <!-- Content -->
        <div class="relative z-10 p-8 md:p-10">
          <div class="text-center mb-8">
            <h2 class="text-2xl font-bold text-gray-800">Welcome Back</h2>
            <p class="text-gray-500 text-sm mt-1">Sign in to access your account</p>
          </div>

          <form @submit.prevent="login" class="space-y-6">
            <!-- Email Field -->
            <div class="space-y-2">
              <label for="email" class="block text-sm font-medium text-gray-700">Email Address</label>
              <div class="relative rounded-md shadow-sm">
                <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                  <i class="pi pi-envelope text-gray-400"></i>
                </div>
                <input
                    id="email"
                    v-model="credentials.email"
                    type="email"
                    autocomplete="email"
                    required
                    class="block w-full pl-10 pr-3 py-3 border border-gray-300 rounded-lg text-gray-900 placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors"
                    placeholder="Your email address"
                />
              </div>
            </div>

            <!-- Password Field -->
            <div class="space-y-2">
              <div class="flex justify-between">
                <label for="password" class="block text-sm font-medium text-gray-700">Password</label>
                <a href="#" class="text-xs text-blue-600 hover:text-blue-800 font-medium">Forgot password?</a>
              </div>
              <div class="relative rounded-md shadow-sm">
                <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                  <i class="pi pi-lock text-gray-400"></i>
                </div>
                <input
                    id="password"
                    v-model="credentials.password"
                    type="password"
                    autocomplete="current-password"
                    required
                    class="block w-full pl-10 pr-3 py-3 border border-gray-300 rounded-lg text-gray-900 placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors"
                    placeholder="Your password"
                />
              </div>
            </div>

            <!-- Remember Me -->
            <div class="flex items-center">
              <input
                  id="remember-me"
                  v-model="rememberMe"
                  type="checkbox"
                  class="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded"
              />
              <label for="remember-me" class="ml-2 block text-sm text-gray-700">
                Remember me
              </label>
            </div>

            <!-- Error Message -->
            <div v-if="error" class="bg-red-50 border-l-4 border-red-500 p-4 rounded">
              <div class="flex">
                <div class="flex-shrink-0">
                  <i class="pi pi-exclamation-circle text-red-500"></i>
                </div>
                <div class="ml-3">
                  <p class="text-sm text-red-600">{{ error }}</p>
                </div>
              </div>
            </div>

            <!-- Login Button -->
            <button
                type="submit"
                :disabled="isLoading"
                class="w-full flex justify-center items-center px-4 py-3 border border-transparent rounded-lg shadow-sm text-base font-medium text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 transition-colors"
            >
              <svg v-if="isLoading" class="animate-spin -ml-1 mr-3 h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
              </svg>
              {{ isLoading ? 'Signing in...' : 'Sign in' }}
            </button>
          </form>

          <!-- Divider -->
<!--          <div class="relative my-8">-->
<!--            <div class="absolute inset-0 flex items-center">-->
<!--              <div class="w-full border-t border-gray-200"></div>-->
<!--            </div>-->
<!--            <div class="relative flex justify-center text-sm">-->
<!--              <span class="px-2 bg-white text-gray-500">Or continue with</span>-->
<!--            </div>-->
<!--          </div>-->

<!--          &lt;!&ndash; Social Login Options &ndash;&gt;-->
<!--          <div class="grid grid-cols-2 gap-3">-->
<!--            <button type="button" class="inline-flex justify-center items-center px-4 py-2 border border-gray-300 shadow-sm rounded-md text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500">-->
<!--              <svg class="h-5 w-5 mr-2" viewBox="0 0 24 24" fill="#4285F4" xmlns="http://www.w3.org/2000/svg">-->
<!--                <path d="M12.24 10.285V14.4h6.806c-.275 1.765-2.056 5.174-6.806 5.174-4.095 0-7.439-3.389-7.439-7.574s3.345-7.574 7.439-7.574c2.33 0 3.891.989 4.785 1.849l3.254-3.138C18.189 1.186 15.479 0 12.24 0c-6.635 0-12 5.365-12 12s5.365 12 12 12c6.926 0 11.52-4.869 11.52-11.726 0-.788-.085-1.39-.189-1.989H12.24z" />-->
<!--              </svg>-->
<!--              Google-->
<!--            </button>-->
<!--            <button type="button" class="inline-flex justify-center items-center px-4 py-2 border border-gray-300 shadow-sm rounded-md text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500">-->
<!--              <svg class="h-5 w-5 mr-2" viewBox="0 0 24 24" fill="#1877F2" xmlns="http://www.w3.org/2000/svg">-->
<!--                <path d="M24 12.073c0-6.627-5.373-12-12-12s-12 5.373-12 12c0 5.99 4.388 10.954 10.125 11.854v-8.385H7.078v-3.47h3.047V9.43c0-3.007 1.792-4.669 4.533-4.669 1.312 0 2.686.235 2.686.235v2.953H15.83c-1.491 0-1.956.925-1.956 1.874v2.25h3.328l-.532 3.47h-2.796v8.385C19.612 23.027 24 18.062 24 12.073z" />-->
<!--              </svg>-->
<!--              Facebook-->
<!--            </button>-->
<!--          </div>-->

          <!-- Register Link -->
          <div class="text-center mt-8">
            <p class="text-sm text-gray-600">
              Don't have an account?
              <router-link to="/register" class="text-blue-600 hover:text-blue-800 font-medium">Create an account</router-link>
            </p>
          </div>
        </div>
      </div>

      <!-- Footer -->
      <div class="mt-6 text-center text-xs text-blue-200">
        <p>&copy; 2025 Immobille. All rights reserved.</p>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* Subtle animation for form elements */
input, button {
  transition: all 0.2s ease;
}

/* Card appearance animations */
@keyframes slide-up {
  0% {
    opacity: 0;
    transform: translateY(20px);
  }
  100% {
    opacity: 1;
    transform: translateY(0);
  }
}

.bg-white {
  animation: slide-up 0.6s ease forwards;
}

/* Button hover effects */
button:hover:not(:disabled) {
  transform: translateY(-1px);
}

button:active:not(:disabled) {
  transform: translateY(1px);
}

input:focus, button:focus {
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.3);
}

.min-h-screen {
  min-height: 100vh;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10px);
  }
}

.rounded-full {
  animation: float 8s ease-in-out infinite;
}
</style>