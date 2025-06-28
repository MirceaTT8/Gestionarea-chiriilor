import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import {
    fetchPaymentsForLease,
    createPayment,
    createStripeCheckoutSession,
    confirmStripePayment,
    isPaymentMadeThisCycle,
    isPaymentMadeThisMonth
} from '@/services/paymentService.js'
import { fetchMyLease } from "@/services/leaseService.js"
import { useTenantLeaseStore } from "@/stores/tenant/leaseTenantStore.js"

export const usePaymentTenantStore = defineStore('paymentTenantStore', () => {
    const payments = ref([])
    const loading = ref(false)
    const error = ref(null)
    const yearlyPayments = ref([])
    const hasPaidCurrentCycle = ref(true)
    const currentLease = ref(null)

    const tenantLeaseStore = useTenantLeaseStore()

    const fetchPayments = async () => {
        loading.value = true
        error.value = null
        try {
            if (!tenantLeaseStore.lease) {
                await tenantLeaseStore.loadTenantLeaseData()
            }
            const leaseId = tenantLeaseStore.lease?.leaseId
            if (!leaseId) throw new Error('No active lease found')

            payments.value = await fetchPaymentsForLease(leaseId)

            updateYearlyPayments()
        } catch (err) {
            error.value = err.message || 'Failed to load payments'
        } finally {
            loading.value = false
        }
    }

    const fetchCurrentLease = async () => {
        try {
            currentLease.value = await fetchMyLease()
            return currentLease.value
        } catch (error) {
            console.error('Failed to fetch current lease:', error)
            throw error
        }
    }

    const checkPaymentCycleStatus = async () => {
        try {
            if (!currentLease.value) {
                await fetchCurrentLease()
            }

            if (currentLease.value?.leaseId) {
                hasPaidCurrentCycle.value = await isPaymentMadeThisCycle(currentLease.value.leaseId)
            }

            return hasPaidCurrentCycle.value
        } catch (error) {
            console.error('Failed to check payment status:', error)
            throw error
        }
    }

    const initializePaymentData = async () => {
        loading.value = true
        error.value = null

        try {
            await fetchPayments()
            await checkPaymentCycleStatus()

        } catch (err) {
            error.value = err.message || 'Failed to initialize payment data'
            throw err
        } finally {
            loading.value = false
        }
    }

    const makePayment = async (paymentData) => {
        loading.value = true
        error.value = null
        try {
            const leaseId = tenantLeaseStore.lease?.leaseId
            if (!leaseId) throw new Error('No active lease found')
            const newPayment = await createPayment(leaseId, paymentData)
            if (newPayment) {
                payments.value.unshift(newPayment)
                updateYearlyPayments()
                await checkPaymentCycleStatus()
            }
            return newPayment
        } catch (err) {
            error.value = err.message || 'Payment failed'
            throw err
        } finally {
            loading.value = false
        }
    }

    const startStripeCheckout = async () => {
        const leaseId = tenantLeaseStore.lease?.leaseId
        if (!leaseId) throw new Error('No active lease found')
        return await createStripeCheckoutSession(leaseId)
    }

    const confirmStripePaymentStatus = async (sessionId) => {
        return await confirmStripePayment(sessionId)
    }

    const checkPaymentMadeThisCycle = async () => {
        const leaseId = tenantLeaseStore.lease?.leaseId
        if (!leaseId) throw new Error('No active lease found')
        return await isPaymentMadeThisCycle(leaseId)
    }

    const checkPaymentMadeThisMonth = async () => {
        const leaseId = tenantLeaseStore.lease?.leaseId
        if (!leaseId) throw new Error('No active lease found')
        return await isPaymentMadeThisMonth(leaseId)
    }

    const updateYearlyPayments = () => {
        const currentYear = new Date().getFullYear()
        yearlyPayments.value = payments.value.filter(payment => {
            const paymentDate = new Date(payment.paymentDate)
            return paymentDate.getFullYear() === currentYear && payment.status === 'COMPLETED'
        })
    }

    const hasPaidThisCycle = computed(() => {
        const lease = tenantLeaseStore.lease
        if (!lease || !payments.value.length) return false

        const today = new Date()
        const currentMonth = today.getMonth()
        const currentYear = today.getFullYear()

        return payments.value.some(payment => {
            if (payment.status !== 'COMPLETED') return false

            const paymentDate = new Date(payment.paymentDate)
            return paymentDate.getMonth() === currentMonth &&
                paymentDate.getFullYear() === currentYear
        })
    })

    const currentBalance = computed(() => {
        return hasPaidCurrentCycle.value ? 0 : tenantLeaseStore.lease?.monthlyRent || 0
    })

    const totalPaidYTD = computed(() => {
        return yearlyPayments.value.reduce((sum, payment) => sum + payment.amount, 0)
    })

    const paymentsByMonth = computed(() => {
        const months = Array(12).fill(0)

        yearlyPayments.value.forEach(payment => {
            const paymentDate = new Date(payment.paymentDate)
            months[paymentDate.getMonth()] += payment.amount
        })

        return months
    })

    const nextPaymentDate = computed(() => {
        const lease = tenantLeaseStore.lease
        if (!lease) return new Date()

        const startDate = new Date(lease.startDate)
        const endDate = new Date(lease.endDate)
        const today = new Date()

        const dueDayOfMonth = startDate.getDate()

        let nextDate = new Date(today.getFullYear(), today.getMonth(), dueDayOfMonth)

        if (today.getDate() > dueDayOfMonth || (today.getDate() === dueDayOfMonth && hasPaidThisCycle.value)) {
            nextDate.setMonth(nextDate.getMonth() + 1)
        }

        if (nextDate > endDate) {
            return null
        }

        return nextDate
    })

    const daysUntilNextPayment = computed(() => {
        const nextPayment = nextPaymentDate.value

        if (!nextPayment) return 0

        const today = new Date()

        const todayStart = new Date(today.getFullYear(), today.getMonth(), today.getDate())
        const nextPaymentStart = new Date(nextPayment.getFullYear(), nextPayment.getMonth(), nextPayment.getDate())

        const diffTime = nextPaymentStart - todayStart
        const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))

        return Math.max(0, diffDays)
    })

    return {
        payments,
        yearlyPayments,
        loading,
        error,
        hasPaidCurrentCycle,
        currentLease,
        fetchPayments,
        fetchCurrentLease,
        checkPaymentCycleStatus,
        initializePaymentData,
        makePayment,
        startStripeCheckout,
        confirmStripePaymentStatus,
        checkPaymentMadeThisCycle,
        checkPaymentMadeThisMonth,
        hasPaidThisCycle,
        currentBalance,
        totalPaidYTD,
        paymentsByMonth,
        nextPaymentDate,
        daysUntilNextPayment,
    }
})