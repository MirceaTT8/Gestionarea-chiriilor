import { defineStore } from 'pinia'
import { ref } from 'vue'
import { fetchMyLeases } from '@/services/leaseService.js'
import { fetchUserById } from '@/services/userService.js'
import { fetchPropertyById } from '@/services/propertyService.js'

export const useLeaseStore = defineStore('leaseStore', () => {
    const leases = ref([])
    const tenants = ref([])
    const properties = ref([])
    const loading = ref(false)
    const error = ref(null)

    const loadLeasesAndData = async () => {
        loading.value = true
        error.value = null
        try {
            const leaseData = await fetchMyLeases()
            const propertyData = await Promise.all(
                leaseData.map(lease => fetchPropertyById(lease.propertyId))
            )
            const tenantData = await Promise.all(
                leaseData.map(async lease => {
                    try {
                        return lease.tenantId ? await fetchUserById(lease.tenantId) : null
                    } catch (e) {
                        console.warn('Error loading tenant:', e)
                        return null
                    }
                })
            )

            leases.value = leaseData
            tenants.value = tenantData
            properties.value = propertyData
        } catch (err) {
            error.value = err.message || 'Failed to load data'
        } finally {
            loading.value = false
        }
    }

    const loadLeases = async () => {
        loading.value = true
        error.value = null
        try {
            leases.value = await fetchMyLeases()
            properties.value = await Promise.all(
                leases.value.map(lease => fetchPropertyById(lease.propertyId))
            )
            tenants.value = await Promise.all(
                leases.value.map(async lease => {
                    if (lease.tenantId) {
                        try {
                            return await fetchUserById(lease.tenantId)
                        } catch (e) {
                            console.warn(`Failed to fetch tenant ${lease.tenantId}:`, e)
                            return null
                        }
                    } else {
                        return null
                    }
                })
            )
        } catch (err) {
            error.value = err.message || 'Failed to load leases'
        } finally {
            loading.value = false
        }
    }


    return { leases, tenants, properties, loading, error, loadLeasesAndData, loadLeases }
})
