<script setup>
import { ref, onMounted, computed } from 'vue'
import { useUserStore } from '@/stores/admin/userStore.js'

const userStore = useUserStore()

const newUser = ref({
  firstName: '',
  lastName: '',
  email: '',
  role: '',
  password: 'TempPassword123!'
})
const showUserDialog = ref(false)
const statusFilter = ref('ALL')

const resetNewUser = () => {
  newUser.value = {
    firstName: '',
    lastName: '',
    email: '',
    role: '',
    password: 'TempPassword123!'
  }
}

const saveNewUser = async () => {
  try {
    await userStore.addUser(newUser.value)
    alert('User created successfully')
    showUserDialog.value = false
    resetNewUser()
  } catch (error) {
    alert('Failed to create user: ' + error.message)
  }
}

const toggleUserSelection = (userId, event) => {
  if (event.target.checked) {
    userStore.selectedUsers.add(userId)
  } else {
    userStore.selectedUsers.delete(userId)
  }
}

const toggleSelectAll = (event) => {
  if (event.target.checked) {
    userStore.users.forEach(user => userStore.selectedUsers.add(user.userId))
  } else {
    userStore.selectedUsers.clear()
  }
}

const confirmDelete = (userId) => {
  if (confirm('Are you sure you want to delete this user?')) {
    deleteUserById(userId)
  }
}

const deleteUserById = async (userId) => {
  try {
    await userStore.removeUser(userId)
    alert('User deleted successfully')
  } catch (error) {
    alert('Failed to delete user: ' + error.message)
  }
}

const toggleUserStatus = async (user) => {
  const action = user.isActive ? 'deactivate' : 'activate'
  const confirmMessage = `Are you sure you want to ${action} user ${user.firstName} ${user.lastName}?`

  if (confirm(confirmMessage)) {
    try {
      await userStore.toggleStatus(user)
      alert(`User ${action}d successfully`)
    } catch (error) {
      alert(`Failed to ${action} user: ${error.message}`)
    }
  }
}

const bulkToggleStatus = async (activate) => {
  const action = activate ? 'activate' : 'deactivate'
  const confirmMessage = `Are you sure you want to ${action} ${userStore.selectedUsers.size} selected user(s)?`

  if (userStore.selectedUsers.size === 0) {
    alert('Please select at least one user')
    return
  }

  if (confirm(confirmMessage)) {
    try {
      await userStore.bulkToggleStatus(activate)
      alert(`Successfully ${action}d users`)
    } catch (error) {
      alert(`Failed to ${action} users: ${error.message}`)
    }
  }
}

const filteredUsers = computed(() => {
  return userStore.filteredUsers.filter(user => {
    if (statusFilter.value === 'ACTIVE' && !user.isActive) return false
    if (statusFilter.value === 'INACTIVE' && user.isActive) return false
    return true
  })
})

onMounted(() => {
  userStore.loadUsers()
})
</script>

<template>
  <div class="max-w-7xl mx-auto px-6 py-10 space-y-8">
    <!-- Header with gradient background -->
    <div class="bg-gradient-to-r from-purple-600 to-indigo-600 rounded-2xl shadow-lg">
      <div class="px-8 py-6">
        <div class="flex flex-col md:flex-row justify-between items-start md:items-center gap-4">
          <div class="flex items-center gap-4">
            <div class="bg-white/20 p-3 rounded-xl">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-8 w-8 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z" />
              </svg>
            </div>
            <div>
              <h1 class="text-3xl font-bold text-white">User Management</h1>
              <p class="text-purple-100 mt-1">Manage your system users and permissions</p>
            </div>
          </div>

          <div class="flex flex-col sm:flex-row gap-3 w-full md:w-auto mt-4 md:mt-0">
            <button
                @click="showUserDialog = true"
                class="flex items-center justify-center gap-2 px-4 py-2 bg-white text-purple-700 text-sm font-medium rounded-xl hover:bg-purple-50 transition-colors shadow-sm"
            >
              <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
              </svg>
              Add User
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Search and Filter Controls -->
    <div class="bg-white rounded-xl shadow-md p-6">
      <div class="flex flex-col md:flex-row gap-4">
        <div class="flex-1">
          <label class="block text-sm font-medium text-gray-700 mb-2">Search Users</label>
          <input
              v-model="userStore.searchQuery"
              type="text"
              placeholder="Search by name or email..."
              class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-purple-500"
          />
        </div>
        <div class="w-full md:w-48">
          <label class="block text-sm font-medium text-gray-700 mb-2">Filter by Status</label>
          <select
              v-model="statusFilter"
              class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-purple-500"
          >
            <option value="ALL">All Users</option>
            <option value="ACTIVE">Active Only</option>
            <option value="INACTIVE">Inactive Only</option>
          </select>
        </div>
      </div>
    </div>

    <!-- Statistics Cards -->
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
      <div class="bg-white rounded-xl shadow-md p-6">
        <div class="flex items-center gap-4">
          <div class="bg-blue-100 p-3 rounded-lg">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-blue-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z" />
            </svg>
          </div>
          <div>
            <h3 class="text-gray-500 text-sm">Total Users</h3>
            <p class="font-bold text-xl text-gray-800">{{ userStore.users.length || 0 }}</p>
          </div>
        </div>
      </div>

      <div class="bg-white rounded-xl shadow-md p-6">
        <div class="flex items-center gap-4">
          <div class="bg-green-100 p-3 rounded-lg">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-green-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
          </div>
          <div>
            <h3 class="text-gray-500 text-sm">Active Users</h3>
            <p class="font-bold text-xl text-gray-800">{{ userStore.users.filter(u => u.isActive).length || 0 }}</p>
          </div>
        </div>
      </div>

      <div class="bg-white rounded-xl shadow-md p-6">
        <div class="flex items-center gap-4">
          <div class="bg-red-100 p-3 rounded-lg">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-red-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 14l2-2m0 0l2-2m-2 2l-2-2m2 2l2 2m7-2a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
          </div>
          <div>
            <h3 class="text-gray-500 text-sm">Inactive Users</h3>
            <p class="font-bold text-xl text-gray-800">{{ userStore.users.filter(u => !u.isActive).length || 0 }}</p>
          </div>
        </div>
      </div>

      <div class="bg-white rounded-xl shadow-md p-6">
        <div class="flex items-center gap-4">
          <div class="bg-purple-100 p-3 rounded-lg">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-purple-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z" />
            </svg>
          </div>
          <div>
            <h3 class="text-gray-500 text-sm">Selected</h3>
            <p class="font-bold text-xl text-gray-800">{{ userStore.selectedUsers.size || 0 }}</p>
          </div>
        </div>
      </div>
    </div>

    <!-- Loading -->
    <div v-if="userStore.loading" class="bg-white rounded-xl shadow-md p-10">
      <div class="flex flex-col items-center justify-center">
        <svg class="animate-spin h-10 w-10 text-purple-600 mb-4" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
          <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
          <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
        </svg>
        <p class="text-gray-500 text-lg">Loading users...</p>
      </div>
    </div>

    <!-- User Table -->
    <div v-else class="bg-white shadow-xl rounded-xl overflow-hidden">
      <div class="border-b border-gray-100 px-6 py-4">
        <h2 class="text-lg font-semibold text-gray-800">User Directory</h2>
        <p class="text-sm text-gray-500">{{ filteredUsers.length }} users found</p>
      </div>

      <div class="overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
          <tr>
            <th class="px-6 py-4 text-left">
              <div class="flex items-center">
                <input
                    type="checkbox"
                    @change="toggleSelectAll"
                    class="h-4 w-4 text-purple-600 rounded border-gray-300 focus:ring-purple-500"
                >
                <span class="ml-3 text-xs font-semibold text-gray-500 uppercase tracking-wider">Select</span>
              </div>
            </th>
            <th class="px-6 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">ID</th>
            <th class="px-6 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">Name</th>
            <th class="px-6 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">Email</th>
            <th class="px-6 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">Role</th>
            <th class="px-6 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">Status</th>
            <th class="px-6 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">Actions</th>
          </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
          <tr v-for="user in filteredUsers" :key="user.userId" class="hover:bg-gray-50">
            <td class="px-6 py-4 whitespace-nowrap">
              <input
                  type="checkbox"
                  :checked="userStore.selectedUsers.has(user.userId)"
                  @change="toggleUserSelection(user.userId, $event)"
                  class="h-4 w-4 text-purple-600 rounded border-gray-300 focus:ring-purple-500"
              >
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-sm font-mono text-gray-900">
              {{ user.userId }}
            </td>
            <td class="px-6 py-4 whitespace-nowrap">
              <div class="text-sm font-medium text-gray-900">{{ user.firstName }} {{ user.lastName }}</div>
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
              {{ user.email }}
            </td>
            <td class="px-6 py-4 whitespace-nowrap">
              <span class="inline-flex px-2 py-1 text-xs font-semibold rounded-full bg-blue-100 text-blue-800">
                {{ user.role }}
              </span>
            </td>
            <td class="px-6 py-4 whitespace-nowrap">
              <span :class="user.isActive ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'"
                    class="inline-flex px-2 py-1 text-xs font-semibold rounded-full">
                {{ user.isActive ? 'ACTIVE' : 'INACTIVE' }}
              </span>
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
              <div class="flex gap-2">
                <button
                    @click="toggleUserStatus(user)"
                    class="inline-flex items-center px-2.5 py-1.5 border border-gray-300 text-xs font-medium rounded text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-purple-500"
                >
                  {{ user.isActive ? 'Deactivate' : 'Activate' }}
                </button>
                <button
                    @click="confirmDelete(user.userId)"
                    class="inline-flex items-center px-2.5 py-1.5 border border-red-300 text-xs font-medium rounded text-red-700 bg-white hover:bg-red-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-red-500"
                >
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-3.5 w-3.5 mr-1" viewBox="0 0 20 20" fill="currentColor">
                    <path fill-rule="evenodd" d="M9 2a1 1 0 00-.894.553L7.382 4H4a1 1 0 000 2v10a2 2 0 002 2h8a2 2 0 002-2V6a1 1 0 100-2h-3.382l-.724-1.447A1 1 0 0011 2H9zM7 8a1 1 0 012 0v6a1 1 0 11-2 0V8zm5-1a1 1 0 00-1 1v6a1 1 0 102 0V8a1 1 0 00-1-1z" clip-rule="evenodd" />
                  </svg>
                  Delete
                </button>
              </div>
            </td>
          </tr>
          </tbody>
        </table>
      </div>

      <!-- Bulk Actions -->
      <div v-if="userStore.selectedUsers.size > 0" class="bg-gray-50 px-6 py-4 flex flex-wrap gap-3 border-t border-gray-200">
        <div class="flex items-center mr-2">
          <span class="text-sm font-medium text-gray-700">{{ userStore.selectedUsers.size }} users selected</span>
        </div>
        <button
            @click="bulkToggleStatus(true)"
            class="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-lg shadow-sm text-white bg-green-600 hover:bg-green-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-green-500"
        >
          <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-1.5" viewBox="0 0 20 20" fill="currentColor">
            <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd" />
          </svg>
          Activate
        </button>
        <button
            @click="bulkToggleStatus(false)"
            class="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-lg shadow-sm text-white bg-yellow-500 hover:bg-yellow-600 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-yellow-500"
        >
          <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-1.5" viewBox="0 0 20 20" fill="currentColor">
            <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clip-rule="evenodd" />
          </svg>
          Deactivate
        </button>
      </div>
    </div>

    <!-- Add User Modal -->
    <div v-if="showUserDialog" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div class="bg-white rounded-xl shadow-xl p-6 w-full max-w-md">
        <div class="flex items-center justify-between mb-6">
          <h2 class="text-xl font-bold text-gray-900">Add New User</h2>
          <button
              @click="showUserDialog = false"
              class="text-gray-400 hover:text-gray-600 focus:outline-none"
          >
            <svg class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>

        <div class="space-y-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">First Name</label>
            <input
                v-model="newUser.firstName"
                type="text"
                class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-purple-500"
                placeholder="Enter first name"
                required
            />
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">Last Name</label>
            <input
                v-model="newUser.lastName"
                type="text"
                class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-purple-500"
                placeholder="Enter last name"
                required
            />
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">Email</label>
            <input
                v-model="newUser.email"
                type="email"
                class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-purple-500"
                placeholder="Enter email address"
                required
            />
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">Role</label>
            <select
                v-model="newUser.role"
                class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-purple-500"
                required
            >
              <option value="">Select a role</option>
              <option v-for="role in userStore.roles" :key="role" :value="role">{{ role }}</option>
            </select>
          </div>
        </div>

        <div class="flex gap-3 mt-6">
          <button
              @click="showUserDialog = false"
              class="flex-1 px-4 py-2 border border-gray-300 text-sm font-medium rounded-lg text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-purple-500"
          >
            Cancel
          </button>
          <button
              @click="saveNewUser"
              class="flex-1 px-4 py-2 border border-transparent text-sm font-medium rounded-lg text-white bg-purple-600 hover:bg-purple-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-purple-500"
          >
            Add User
          </button>
        </div>
      </div>
    </div>
  </div>
</template>