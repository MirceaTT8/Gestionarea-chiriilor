<script setup>
import { ref } from 'vue';
import { Swiper, SwiperSlide } from 'swiper/vue';
import 'swiper/css';
import 'swiper/css/navigation';
import 'swiper/css/pagination';
import { Navigation, Pagination } from 'swiper/modules';
import { useMaintenanceTenantStore } from "@/stores/tenant/leaseMaintenanceTenantStore.js";

const showModal = ref(false);
const activeIndex = ref(0);
const isExpanded = ref(false);

const maintenanceStore = useMaintenanceTenantStore();

const openSlider = (index = 0) => {
  activeIndex.value = index;
  showModal.value = true;
};

const props = defineProps({
  request: {
    type: Object,
    required: true
  }
});

const userFixedResponse = ref(props.request.is_fixed ?? null);
const hasSubmittedResponse = ref(props.request.is_fixed !== null);

const submitFixedStatus = async (isFixed) => {
  try {
    if (isFixed) {
      userFixedResponse.value = true;
      hasSubmittedResponse.value = true;
    } else {
      await maintenanceStore.markAsNotFixed(props.request.requestId);

      userFixedResponse.value = false;
      hasSubmittedResponse.value = true;

      // Emit event to parent component
      emit('feedback-submitted', {
        requestId: props.request.requestId,
        isFixed: false
      });
    }
  } catch (error) {
    console.error('Failed to submit feedback:', error);
    alert('Failed to submit feedback. Please try again.');
  }
};

const emit = defineEmits(['feedback-submitted']);

const statusDisplay = {
  PENDING: {
    label: 'Pending',
    bgColor: 'bg-yellow-100',
    textColor: 'text-yellow-800',
    borderColor: 'border-yellow-200',
    dotColor: 'bg-yellow-500',
    icon: `<svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 text-yellow-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
           </svg>`
  },
  IN_PROGRESS: {
    label: 'In Progress',
    bgColor: 'bg-blue-100',
    textColor: 'text-blue-800',
    borderColor: 'border-blue-200',
    dotColor: 'bg-blue-500',
    icon: `<svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 text-blue-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5.882V19.24a1.76 1.76 0 01-3.417.592l-2.147-6.15M18 13a3 3 0 100-6M5.436 13.683A4.001 4.001 0 017 6h1.832c4.1 0 7.625-1.234 9.168-3v14c-1.543-1.766-5.067-3-9.168-3H7a3.988 3.988 0 01-1.564-.317z" />
           </svg>`
  },
  COMPLETED: {
    label: 'Completed',
    bgColor: 'bg-green-100',
    textColor: 'text-green-800',
    borderColor: 'border-green-200',
    dotColor: 'bg-green-500',
    icon: `<svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 text-green-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
           </svg>`
  }
};

const formatDate = (dateString) => {
  return new Date(dateString).toLocaleString();
};

const getRequestTypeIcon = (type) => {
  switch (type?.toLowerCase() || '') {
    case 'plumbing':
      return `<svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-blue-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 12h14M5 12a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v4a2 2 0 01-2 2M5 12a2 2 0 00-2 2v4a2 2 0 002 2h14a2 2 0 002-2v-4a2 2 0 00-2-2m-2-4h.01M17 16h.01" />
</svg>`;
    case 'electrical':
      return `<svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-yellow-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z" />
</svg>`;
    case 'hvac':
      return `<svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-red-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9.75 17L9 20l-1 1h8l-1-1-.75-3M3 13h18M5 17h14a2 2 0 002-2V5a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
</svg>`;
    case 'paint':
      return `<svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-indigo-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 21a4 4 0 01-4-4V5a2 2 0 012-2h4a2 2 0 012 2v12a4 4 0 01-4 4zm0 0h12a2 2 0 002-2v-4a2 2 0 00-2-2h-2.343M11 7.343l1.657-1.657a2 2 0 012.828 0l2.829 2.829a2 2 0 010 2.828l-8.486 8.485M7 17h.01" />
</svg>`;
    case 'appliance':
      return `<svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-gray-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 12h14M5 12a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v4a2 2 0 01-2 2M5 12a2 2 0 00-2 2v4a2 2 0 002 2h14a2 2 0 002-2v-4a2 2 0 00-2-2m-2-4h.01M17 16h.01" />
</svg>`;
    default:
      return `<svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-teal-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5.882V19.24a1.76 1.76 0 01-3.417.592l-2.147-6.15M18 13a3 3 0 100-6M5.436 13.683A4.001 4.001 0 017 6h1.832c4.1 0 7.625-1.234 9.168-3v14c-1.543-1.766-5.067-3-9.168-3H7a3.988 3.988 0 01-1.564-.317z" />
</svg>`
  }
}
</script>

<template>
  <div class="bg-white shadow-sm rounded-xl border border-gray-200 overflow-hidden transition-all hover:shadow-md">
    <!-- Card Header -->
    <div class="px-6 py-5 border-b border-gray-100">
      <div class="flex flex-col sm:flex-row sm:justify-between sm:items-center gap-3">
        <div class="flex items-center">
          <!-- Request Type Icon -->
          <div class="inline-flex items-center justify-center h-10 w-10 rounded-full bg-teal-100 mr-3">
            <span v-html="getRequestTypeIcon(request.type)"></span>
          </div>

          <div>
            <h3 class="text-base font-semibold text-gray-800">Request #{{ request.requestId }}</h3>
            <p class="text-sm text-gray-600 mt-1">{{ request.type }}</p>
          </div>
        </div>

        <!-- Status Badge -->
        <div
            class="inline-flex items-center px-2.5 py-1 rounded-full text-xs font-medium"
            :class="`${statusDisplay[request.status]?.bgColor || 'bg-gray-100'} ${statusDisplay[request.status]?.textColor || 'text-gray-800'} border ${statusDisplay[request.status]?.borderColor || 'border-gray-200'}`"
        >
          <span
              :class="statusDisplay[request.status]?.dotColor || 'bg-gray-500'"
              class="h-1.5 w-1.5 rounded-full mr-1.5"
          ></span>
          {{ statusDisplay[request.status]?.label || request.status }}
        </div>
      </div>
    </div>

    <!-- Card Content -->
    <div class="px-6 py-4">
      <!-- Description (truncated if long) -->
      <div>
        <p
            class="text-sm text-gray-700 whitespace-pre-wrap mb-4"
            :class="{'line-clamp-3': !isExpanded && request.description.length > 200}"
        >
          {{ request.description }}
        </p>

        <button
            v-if="request.description.length > 200"
            @click="isExpanded = !isExpanded"
            class="text-xs text-teal-600 hover:text-teal-800 font-medium mb-4"
        >
          {{ isExpanded ? 'Show less' : 'Read more' }}
        </button>
      </div>

      <!-- View Photos Button (Only show if images exist) -->
      <div v-if="request.imageUrls?.length" class="mb-4">
        <button
            @click="openSlider(0)"
            class="inline-flex items-center px-4 py-2.5 bg-gradient-to-r from-teal-500 to-cyan-600 hover:from-teal-600 hover:to-cyan-700 text-white text-sm font-medium rounded-lg transition-all duration-200 shadow-sm hover:shadow-md transform hover:-translate-y-0.5"
        >
          <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
          </svg>
          View Photos ({{ request.imageUrls.length }})
        </button>
      </div>

      <!-- Meta Information -->
      <div class="grid grid-cols-1 sm:grid-cols-2 gap-4 mt-6 text-xs text-gray-500 border-t border-gray-100 pt-4">
        <div class="space-y-2">
          <div class="flex items-center">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-3.5 w-3.5 text-gray-400 mr-1.5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
            </svg>
            <span class="font-medium">Submitted:</span>
            <span class="ml-1">{{ formatDate(request.createdAt) }}</span>
          </div>

          <div class="flex items-center">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-3.5 w-3.5 text-gray-400 mr-1.5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
            <span class="font-medium">Last Updated:</span>
            <span class="ml-1">{{ formatDate(request.updatedAt) }}</span>
          </div>
        </div>

        <div class="space-y-2">
          <div class="flex items-center">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-3.5 w-3.5 text-gray-400 mr-1.5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
            <span class="font-medium">Status:</span>
            <span class="ml-1">{{ statusDisplay[request.status]?.label || request.status }}</span>
          </div>

          <!-- Show image count in meta if there are images -->
          <div v-if="request.imageUrls?.length" class="flex items-center">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-3.5 w-3.5 text-gray-400 mr-1.5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
            </svg>
            <span class="font-medium">Photos:</span>
            <span class="ml-1">{{ request.imageUrls.length }} attached</span>
          </div>
        </div>
      </div>
    </div>

    <!-- Image Slider Modal -->
    <div v-if="showModal" class="fixed inset-0 bg-black bg-opacity-90 z-50 flex items-center justify-center p-4">
      <div class="bg-white rounded-xl overflow-hidden w-full max-w-6xl shadow-xl relative">
        <!-- Header with image counter and close button -->
        <div class="absolute top-0 left-0 right-0 z-10 bg-gradient-to-b from-black/50 to-transparent p-4">
          <div class="flex justify-between items-center">
            <div class="bg-black/30 backdrop-blur-sm text-white px-3 py-1 rounded-full text-sm font-medium">
              Photo {{ activeIndex + 1 }} of {{ request.imageUrls.length }}
            </div>
            <button
                @click="showModal = false"
                class="bg-black/30 hover:bg-black/50 backdrop-blur-sm text-white rounded-full p-2 transition-all duration-200"
            >
              <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
              </svg>
            </button>
          </div>
        </div>

        <Swiper
            :initial-slide="activeIndex"
            :modules="[Navigation, Pagination]"
            :navigation="true"
            :pagination="{ clickable: true }"
            class="h-[85vh]"
            @slide-change="(swiper) => activeIndex = swiper.activeIndex"
        >
          <SwiperSlide v-for="(imageId, index) in request.imageUrls" :key="index">
            <div class="h-full w-full flex items-center justify-center bg-gray-900">
              <img
                  :src="`http://localhost:8080/image/${imageId}`"
                  :alt="`Maintenance request photo ${index + 1}`"
                  class="max-w-full max-h-full object-contain"
                  loading="lazy"
              />
            </div>
          </SwiperSlide>
        </Swiper>
      </div>
    </div>
  </div>
</template>