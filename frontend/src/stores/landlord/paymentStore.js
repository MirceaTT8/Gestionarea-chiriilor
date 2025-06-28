import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import {
    fetchPaymentsForOwner,
    createPayment
} from '@/services/paymentService.js'
import {
    fetchMyLeases
} from '@/services/leaseService.js'
import {
    fetchAllUsers,
    fetchUserById
} from '@/services/userService.js'
import {
    fetchPropertyById
} from '@/services/propertyService.js'

export const usePaymentLandlordStore = defineStore('paymentLandlordStore', () => {
    const payments = ref([])
    const leases = ref([])
    const tenants = ref([])
    const properties = ref([])
    const users = ref([])
    const loading = ref(false)
    const error = ref(null)
    const selectedLeaseId = ref(null)

    const filteredPayments = computed(() => {
        if (!selectedLeaseId.value) return payments.value
        return payments.value.filter(p => p.leaseId === selectedLeaseId.value)
    })

    const loadPayments = async () => {
        loading.value = true
        error.value = null
        try {
            payments.value = await fetchPaymentsForOwner()
            leases.value = await fetchMyLeases()
            users.value = await fetchAllUsers()
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
                        console.warn('Skipped null tenantId for lease:', lease.leaseId)
                        return null
                    }
                })
            )

            leases.value = leases.value.map(lease => ({
                ...lease,
                property: properties.value.find(p => p.propertyId === lease.propertyId)
            }))
        } catch (err) {
            error.value = err.message || 'Failed to load payments'
        } finally {
            loading.value = false
        }
    }

    const submitManualPayment = async (payment) => {
        loading.value = true
        try {
            await createPayment(payment.leaseId, payment)
            await loadPayments()
        } catch (err) {
            error.value = err.message || 'Failed to record payment'
        } finally {
            loading.value = false
        }
    }

    return {
        payments,
        leases,
        tenants,
        properties,
        users,
        loading,
        error,
        selectedLeaseId,
        filteredPayments,
        loadPayments,
        submitManualPayment
    }
})
