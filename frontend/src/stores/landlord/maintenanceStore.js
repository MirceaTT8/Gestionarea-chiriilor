import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { fetchMaintenanceRequestsByLoggedInOwner, setStatus } from '@/services/maintenanceService.js'
import { fetchMyLeases } from '@/services/leaseService.js'
import { fetchUserById } from '@/services/userService.js'
import { fetchPropertyById } from '@/services/propertyService.js'

export const useMaintenanceLandlordStore = defineStore('maintenanceLandlordStore', () => {
    const requests = ref([])
    const leases = ref([])
    const tenants = ref([])
    const properties = ref([])
    const loading = ref(false)
    const error = ref(null)
    const selectedStatus = ref('all')

    const availableStatuses = computed(() => {
        const statusSet = new Set(requests.value.map(req => req.status))
        return Array.from(statusSet)
    })

    const filteredRequests = computed(() => {
        return selectedStatus.value === 'all'
            ? requests.value
            : requests.value.filter(r => r.status === selectedStatus.value)
    })

    const loadRequests = async () => {
        loading.value = true
        error.value = null
        try {
            requests.value = await fetchMaintenanceRequestsByLoggedInOwner()
            leases.value = await fetchMyLeases()
            properties.value = await Promise.all(leases.value.map(l => fetchPropertyById(l.propertyId)))
            tenants.value = await Promise.all(
                leases.value.map(async l => {
                    if (l.tenantId) {
                        try {
                            return await fetchUserById(l.tenantId)
                        } catch (e) {
                            console.warn(`Could not fetch tenant ${l.tenantId}`)
                            return null
                        }
                    }
                    return null
                })
            )
        } catch (err) {
            error.value = err.message || 'Failed to load maintenance data'
        } finally {
            loading.value = false
        }
    }

    const updateStatus = async (requestId, newStatus) => {
        try {
            const updated = await setStatus(requestId, newStatus)
            const index = requests.value.findIndex(req => req.requestId === requestId)
            if (index !== -1) {
                requests.value[index].status = newStatus
                requests.value[index].updatedAt = new Date().toISOString()
            }
            selectedStatus.value = 'all'
        } catch (err) {
            error.value = err.message || 'Failed to update request status'
        }
    }

    return {
        requests,
        leases,
        tenants,
        properties,
        selectedStatus,
        availableStatuses,
        filteredRequests,
        loading,
        error,
        loadRequests,
        updateStatus
    }
})
