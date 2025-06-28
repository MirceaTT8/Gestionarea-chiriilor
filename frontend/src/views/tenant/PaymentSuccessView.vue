<script setup>
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { confirmStripePayment } from '@/services/paymentService.js'

const route = useRoute()
const router = useRouter()
const loading = ref(true)
const error = ref(null)

onMounted(async () => {
  const sessionId = route.query.session_id
  if (!sessionId) {
    error.value = 'No session ID found.'
    loading.value = false
    return
  }

  try {
    await new Promise(resolve => setTimeout(resolve, 1500));
    await confirmStripePayment(sessionId);
    await new Promise(resolve => setTimeout(resolve, 1500));
    setTimeout(() => router.push('/tenant/leases'), 3000)
  } catch (err) {
    error.value = 'Payment confirmation failed.'
    console.error(err)
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <div class="min-h-screen bg-gradient-to-br from-emerald-50 via-blue-50 to-purple-50 flex items-center justify-center p-6 relative overflow-hidden">
    <!-- Background decorative elements -->
    <div class="absolute inset-0 overflow-hidden">
      <div class="absolute -top-40 -right-40 w-80 h-80 bg-gradient-to-br from-green-200/30 to-blue-200/30 rounded-full blur-3xl"></div>
      <div class="absolute -bottom-40 -left-40 w-80 h-80 bg-gradient-to-tr from-purple-200/30 to-green-200/30 rounded-full blur-3xl"></div>
    </div>

    <div class="bg-white/80 backdrop-blur-lg w-full max-w-lg p-10 rounded-3xl shadow-2xl border border-white/20 space-y-8 animate-fade-in relative z-10">
      <!-- Loading State -->
      <div v-if="loading" class="text-center space-y-6">
        <div class="mx-auto w-20 h-20 bg-gradient-to-br from-blue-500 to-indigo-600 rounded-2xl flex items-center justify-center shadow-lg">
          <div class="w-8 h-8 border-4 border-white/30 border-t-white rounded-full animate-spin"></div>
        </div>
        <div class="space-y-3">
          <h2 class="text-2xl font-bold bg-gradient-to-r from-blue-600 to-indigo-600 bg-clip-text text-transparent">Verifying Payment</h2>
          <p class="text-gray-600">Please wait while we confirm your payment...</p>
        </div>
      </div>

      <!-- Error State -->
      <div v-else-if="error" class="text-center space-y-6">
        <div class="mx-auto w-20 h-20 bg-gradient-to-br from-red-500 to-rose-600 rounded-2xl flex items-center justify-center shadow-lg">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-10 w-10 text-white" viewBox="0 0 20 20" fill="currentColor">
            <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clip-rule="evenodd" />
          </svg>
        </div>
        <div class="space-y-3">
          <h2 class="text-2xl font-bold bg-gradient-to-r from-red-600 to-rose-600 bg-clip-text text-transparent">Payment Failed</h2>
          <p class="text-gray-600">{{ error }}</p>
        </div>
        <button
            @click="router.push('/tenant/leases')"
            class="w-full bg-gradient-to-r from-red-500 to-rose-600 text-white px-6 py-3 rounded-xl font-semibold shadow-lg hover:shadow-xl transform hover:-translate-y-0.5 transition-all duration-200"
        >
          Return to Dashboard
        </button>
      </div>

      <!-- Success State -->
      <div v-else class="text-center space-y-6">
        <div class="mx-auto w-20 h-20 bg-gradient-to-br from-green-500 to-emerald-600 rounded-2xl flex items-center justify-center shadow-lg animate-success-bounce">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-10 w-10 text-white" viewBox="0 0 20 20" fill="currentColor">
            <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd" />
          </svg>
        </div>

        <div class="space-y-3">
          <h2 class="text-3xl font-bold bg-gradient-to-r from-green-600 to-emerald-600 bg-clip-text text-transparent">Payment Successful!</h2>
          <p class="text-gray-600 text-lg">Thank you for your rent payment. Your transaction has been processed successfully.</p>
        </div>

        <!-- Success details card -->
        <div class="bg-white/50 backdrop-blur-sm rounded-2xl p-6 border border-white/30 space-y-4">
          <div class="flex items-center justify-center space-x-2 text-green-600">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
            <p class="text-sm font-medium">Redirecting to dashboard in a few seconds...</p>
          </div>
        </div>

        <button
            @click="router.push('/tenant/leases')"
            class="w-full bg-gradient-to-r from-green-500 to-emerald-600 text-white px-6 py-4 rounded-xl font-semibold shadow-lg hover:shadow-xl transform hover:-translate-y-0.5 transition-all duration-200 flex items-center justify-center space-x-2"
        >
          <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6" />
          </svg>
          <span>Go to Dashboard</span>
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
@keyframes fade-in {
  0% {
    opacity: 0;
    transform: translateY(20px);
  }
  100% {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes success-bounce {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.05);
  }
}

@keyframes float {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10px);
  }
}

.animate-fade-in {
  animation: fade-in 0.6s ease-out;
}

.animate-success-bounce {
  animation: success-bounce 0.8s ease-in-out;
}

.rounded-full {
  animation: float 8s ease-in-out infinite;
}

button {
  transition: all 0.2s ease;
}

button:active {
  transform: translateY(0) !important;
}

button:focus {
  outline: none;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.3);
}
</style>