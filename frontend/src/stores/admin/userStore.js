// stores/userStore.js
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import {
    fetchAllUsers,
    createUser,
    deleteUser,
    activateUser,
    deactivateUser
} from '@/services/userService.js'

export const useUserStore = defineStore('userStore', () => {
    const users = ref([])
    const selectedUsers = ref(new Set())
    const loading = ref(false)
    const error = ref(null)
    const searchQuery = ref('')

    const roles = ['ADMIN', 'LANDLORD', 'TENANT']

    const filteredUsers = computed(() => {
        if (!searchQuery.value) return users.value
        const query = searchQuery.value.toLowerCase()
        return users.value.filter(user =>
            user.firstName.toLowerCase().includes(query) ||
            user.lastName.toLowerCase().includes(query) ||
            user.email.toLowerCase().includes(query)
        )
    })

    const loadUsers = async () => {
        loading.value = true
        error.value = null
        try {
            users.value = await fetchAllUsers()
        } catch (err) {
            error.value = err.message || 'Failed to load users'
        } finally {
            loading.value = false
        }
    }

    const addUser = async (userData) => {
        loading.value = true
        try {
            await createUser(userData)
            await loadUsers()
        } catch (err) {
            error.value = err.message || 'Failed to add user'
            throw err
        } finally {
            loading.value = false
        }
    }

    const removeUser = async (userId) => {
        try {
            await deleteUser(userId)
            await loadUsers()
        } catch (err) {
            error.value = err.message || 'Failed to delete user'
            throw err
        }
    }

    const toggleStatus = async (user) => {
        try {
            if (user.isActive) {
                await deactivateUser(user.userId)
            } else {
                await activateUser(user.userId)
            }
            await loadUsers()
        } catch (err) {
            error.value = err.message || 'Failed to toggle user status'
            throw err
        }
    }

    const bulkToggleStatus = async (activate) => {
        try {
            const promises = [...selectedUsers.value].map(userId =>
                activate ? activateUser(userId) : deactivateUser(userId)
            )
            await Promise.all(promises)
            await loadUsers()
            selectedUsers.value.clear()
        } catch (err) {
            error.value = err.message || 'Failed to toggle status for selected users'
            throw err
        }
    }

    return {
        users,
        selectedUsers,
        loading,
        error,
        searchQuery,
        roles,
        filteredUsers,
        loadUsers,
        addUser,
        removeUser,
        toggleStatus,
        bulkToggleStatus
    }
})