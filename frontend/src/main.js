import './assets/main.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from '@/router/index.js'
import PrimeVue from 'primevue/config'
import { ToastService, Dialog } from 'primevue'
import 'vue-cal/dist/vuecal.css'

const app = createApp(App)

const store = createPinia()
app.use(store)

import { useAuthStore } from '@/stores/authStore.js'
const authStore = useAuthStore()
authStore.initialize()

app.use(router)
app.use(PrimeVue)
app.use(ToastService)
app.component('Dialog', Dialog)

app.mount('#app')
