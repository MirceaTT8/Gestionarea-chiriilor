<script setup>
import { reactive, onMounted, ref, computed } from 'vue';
import { useRoute } from 'vue-router';
import { fetchUserByEmail, fetchUserById, updateUserPhone } from '@/services/userService';
import { jwtDecode } from 'jwt-decode';
import ProfileAvatar from "@/views/profile/ProfileAvatar.vue";
import ProfileDetailItem from "@/views/profile/ProfileDetailItem.vue";
import ProfileInputField from "@/views/profile/ProfileInputField.vue";
import LoadingSpinner from "@/components/LoadingSpinner.vue";
import ErrorMessage from "@/components/ErrorMessage.vue";

const route = useRoute();

const user = reactive({
  userId: null,
  firstName: '',
  lastName: '',
  email: '',
  phone: ''
});

const currentUser = reactive({
  userId: null,
  email: ''
});

const isEditing = ref(false);
const isLoading = ref(true);
const error = ref(null);
const tempProfile = reactive({});

const canEdit = computed(() => {
  return currentUser.userId && user.userId && currentUser.userId === user.userId;
});

const isOwnProfile = computed(() => {
  return canEdit.value;
});

const fetchUserData = async () => {
  try {
    isLoading.value = true;
    error.value = null;

    const token = localStorage.getItem('token');
    if (!token) throw new Error('User not authenticated');

    const decoded = jwtDecode(token);

    const currentUserData = await fetchUserByEmail(decoded.sub);
    Object.assign(currentUser, {
      userId: currentUserData.userId,
      email: currentUserData.email
    });

    const userIdParam = route.params.userId;

    let userData;
    if (userIdParam) {
      userData = await fetchUserById(parseInt(userIdParam));
    } else {
      userData = currentUserData;
    }

    Object.assign(user, userData);
  } catch (err) {
    error.value = err.message || 'Failed to load profile data';
    console.error('Error:', err);
  } finally {
    isLoading.value = false;
  }
};

const startEditing = () => {
  if (!canEdit.value) {
    alert('You can only edit your own profile');
    return;
  }
  Object.assign(tempProfile, user);
  isEditing.value = true;
};

const cancelEditing = () => {
  isEditing.value = false;
};

const saveProfile = async () => {
  try {
    isLoading.value = true;

    if (tempProfile.phone !== user.phone) {
      const updatedUser = await updateUserPhone(user.userId, tempProfile.phone);

      user.phone = updatedUser.phone;
    }

    isEditing.value = false;
    alert('Phone number updated successfully');
  } catch (err) {
    alert('Failed to update phone number: ' + err.message);
    console.error('Phone update error:', err);
  } finally {
    isLoading.value = false;
  }
};

onMounted(fetchUserData);
</script>

<template>
  <div class="max-w-3xl mx-auto p-6 bg-white rounded-xl shadow-md my-8">
    <!-- Header Section with Background -->
    <div class="relative mb-8">
      <!-- Background Banner -->
      <div class="h-32 bg-gradient-to-r from-blue-600 to-blue-700 rounded-t-xl"></div>

      <!-- Profile Header -->
      <div class="relative px-6 -mt-16 flex flex-col md:flex-row justify-between items-center">
        <!-- Avatar and Name -->
        <div class="flex flex-col items-center md:items-start md:flex-row md:space-x-4">
          <div class="w-24 h-24 bg-white rounded-full p-1 shadow-lg mb-4 md:mb-0">
            <ProfileAvatar :user="user" />
          </div>
          <div class="text-center md:text-left mt-2">
            <div class="flex items-center gap-2">
              <h1 class="text-2xl font-bold text-white">{{ user.firstName }} {{ user.lastName }}</h1>
              <!-- Badge to indicate if viewing own profile -->
              <span v-if="isOwnProfile" class="px-2 py-1 bg-white/20 text-white text-xs font-medium rounded-full">
                Your Profile
              </span>
            </div>
          </div>
        </div>

        <!-- Action Buttons -->
        <div class="mt-4 md:mt-0">
          <!-- Edit button only shown for own profile -->
          <button
              v-if="!isEditing && canEdit"
              @click="startEditing"
              class="px-5 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition flex items-center shadow-sm"
          >
            <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15.232 5.232l3.536 3.536m-2.036-5.036a2.5 2.5 0 113.536 3.536L6.5 21.036H3v-3.572L16.732 3.732z" />
            </svg>
            Edit Profile
          </button>

          <!-- Save/Cancel buttons when editing -->
          <div v-if="isEditing" class="flex gap-2">
            <button
                @click="saveProfile"
                class="px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 transition flex items-center shadow-sm"
            >
              <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
              </svg>
              Save
            </button>
            <button
                @click="cancelEditing"
                class="px-4 py-2 bg-gray-500 text-white rounded-lg hover:bg-gray-600 transition flex items-center shadow-sm"
            >
              <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
              </svg>
              Cancel
            </button>
          </div>

          <!-- Read-only indicator for other users - removed -->
        </div>
      </div>
    </div>

    <!-- Loading State -->
    <div v-if="isLoading" class="flex justify-center py-12">
      <LoadingSpinner />
    </div>

    <!-- Error State -->
    <div v-else-if="error" class="bg-red-50 border-l-4 border-red-500 p-4 rounded-md my-6">
      <ErrorMessage :message="error" @retry="fetchUserData" />
    </div>

    <!-- Profile Content -->
    <div v-else class="space-y-6">
      <div class="bg-gray-50 p-6 rounded-lg border border-gray-100">
        <h2 class="text-lg font-medium text-gray-800 mb-6 flex items-center border-b border-gray-200 pb-3">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-2 text-blue-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
          </svg>
          Personal Information
        </h2>

        <!-- View Mode -->
        <div v-if="!isEditing" class="grid grid-cols-1 md:grid-cols-2 gap-y-6 gap-x-8">
          <div class="bg-white p-4 rounded-lg border border-gray-200 shadow-sm transition-all hover:shadow">
            <p class="text-sm font-medium text-gray-500 mb-1">First Name</p>
            <p class="text-lg font-medium text-gray-800 flex items-center">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-2 text-blue-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
              </svg>
              {{ user.firstName }}
            </p>
          </div>

          <div class="bg-white p-4 rounded-lg border border-gray-200 shadow-sm transition-all hover:shadow">
            <p class="text-sm font-medium text-gray-500 mb-1">Last Name</p>
            <p class="text-lg font-medium text-gray-800 flex items-center">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-2 text-blue-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
              </svg>
              {{ user.lastName }}
            </p>
          </div>

          <div class="bg-white p-4 rounded-lg border border-gray-200 shadow-sm transition-all hover:shadow">
            <p class="text-sm font-medium text-gray-500 mb-1">Email Address</p>
            <p class="text-lg font-medium text-gray-800 flex items-center">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-2 text-blue-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
              </svg>
              {{ user.email }}
            </p>
          </div>

          <div class="bg-white p-4 rounded-lg border border-gray-200 shadow-sm transition-all hover:shadow">
            <p class="text-sm font-medium text-gray-500 mb-1">Phone Number</p>
            <p class="text-lg font-medium text-gray-800 flex items-center">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-2 text-blue-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 5a2 2 0 012-2h3.28a1 1 0 01.948.684l1.498 4.493a1 1 0 01-.502 1.21l-2.257 1.13a11.042 11.042 0 005.516 5.516l1.13-2.257a1 1 0 011.21-.502l4.493 1.498a1 1 0 01.684.949V19a2 2 0 01-2 2h-1C9.716 21 3 14.284 3 6V5z" />
              </svg>
              {{ user.phone || 'Not provided' }}
            </p>
          </div>
        </div>

        <!-- Edit Mode (only phone number can be edited) -->
        <div v-if="isEditing" class="grid grid-cols-1 md:grid-cols-2 gap-y-6 gap-x-8">
          <div class="bg-white p-4 rounded-lg border border-gray-200 shadow-sm">
            <p class="text-sm font-medium text-gray-500 mb-1">First Name</p>
            <p class="text-lg font-medium text-gray-400 flex items-center">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-2 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
              </svg>
              {{ user.firstName }}
            </p>
            <p class="text-xs text-gray-500 mt-1">Cannot be changed</p>
          </div>

          <div class="bg-white p-4 rounded-lg border border-gray-200 shadow-sm">
            <p class="text-sm font-medium text-gray-500 mb-1">Last Name</p>
            <p class="text-lg font-medium text-gray-400 flex items-center">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-2 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
              </svg>
              {{ user.lastName }}
            </p>
            <p class="text-xs text-gray-500 mt-1">Cannot be changed</p>
          </div>

          <div class="bg-white p-4 rounded-lg border border-gray-200 shadow-sm">
            <p class="text-sm font-medium text-gray-500 mb-1">Email Address</p>
            <p class="text-lg font-medium text-gray-400 flex items-center">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-2 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
              </svg>
              {{ user.email }}
            </p>
            <p class="text-xs text-gray-500 mt-1">Cannot be changed</p>
          </div>

          <div class="space-y-1">
            <label class="block text-sm font-medium text-gray-700">Phone Number</label>
            <input
                v-model="tempProfile.phone"
                type="tel"
                class="mt-1 block w-full rounded-md border border-gray-300 px-3 py-2 shadow-sm focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500"
                placeholder="Enter phone number"
            />
            <p class="text-xs text-blue-600 mt-1">This field can be updated</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>