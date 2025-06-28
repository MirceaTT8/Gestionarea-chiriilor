import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import {
    fetchPendingLeaseTerminations,
    fetchPendingLeases,
    approveLeaseTermination,
    approveLease,
    rejectLease,
    fetchLeaseTrends
} from '@/services/leaseService.js'
import { fetchAllPropertiesAdmin, fetchPendingProperties, validateProperty } from '@/services/propertyService.js'
import { fetchAllUsers } from '@/services/userService.js'
import { fetchAllMaintenanceRequests } from '@/services/maintenanceService.js'
import { BASE_URL } from "@/configs/config.js"

export const useAdminDashboardStore = defineStore('adminDashboardStore', () => {
    const showTerminateModal = ref(false)
    const showPendingLeasesModal = ref(false)
    const showPendingPropertiesModal = ref(false)

    const loading = ref(false)
    const chartLoading = ref(false)
    const statsLoading = ref(false)

    const pendingLeaseTerminations = ref([])
    const pendingLeaseApprovals = ref([])
    const pendingProperties = ref([])

    const kpiMetrics = ref({})
    const statsData = ref({
        totalUsers: 0,
        userGrowth: 0,
        totalProperties: 0,
        propertyGrowth: 0,
        maintenanceRequests: 0,
        urgentRequests: 0
    })

    const leaseChartData = ref({
        labels: [],
        datasets: []
    })

    const stats = computed(() => ({
        activeLeases: kpiMetrics.value['Total Leases'] || 0,
        pendingApprovals: pendingLeaseTerminations.value.length + pendingLeaseApprovals.value.length + pendingProperties.value.length,
        monthlyRevenue: kpiMetrics.value['Total Payments'] || '$0',
        totalUsers: kpiMetrics.value['Total Users'] || 0,
        pendingTerminations: pendingLeaseTerminations.value.length,
        pendingLeases: pendingLeaseApprovals.value.length,
        pendingPropertiesCount: pendingProperties.value.length
    }))

    // Chart options with improved styling
    const leaseChartOptions = ref({
        responsive: true,
        maintainAspectRatio: false,
        interaction: {
            intersect: false,
            mode: 'index'
        },
        plugins: {
            legend: {
                position: 'bottom',
                labels: {
                    padding: 20,
                    usePointStyle: true,
                    font: {
                        size: 12
                    }
                }
            },
            tooltip: {
                backgroundColor: 'rgba(0, 0, 0, 0.8)',
                titleColor: '#ffffff',
                bodyColor: '#ffffff',
                borderColor: 'rgba(255, 255, 255, 0.1)',
                borderWidth: 1
            }
        },
        scales: {
            x: {
                grid: {
                    display: false
                },
                ticks: {
                    color: '#6B7280'
                }
            },
            y: {
                beginAtZero: true,
                grid: {
                    color: 'rgba(0, 0, 0, 0.1)'
                },
                ticks: {
                    color: '#6B7280'
                }
            }
        },
        elements: {
            bar: {
                borderRadius: 4
            }
        }
    })

    const getCurrentPeriod = (periodType) => {
        const now = new Date()

        switch (periodType) {
            case 'monthly':
                return {
                    key: now.toISOString().slice(0, 7), // YYYY-MM format (2025-06)
                    label: now.toLocaleString(undefined, { month: 'long', year: 'numeric' }), // June 2025
                    start: new Date(now.getFullYear(), now.getMonth(), 1),
                    end: new Date(now.getFullYear(), now.getMonth() + 1, 0)
                }

            case 'quarterly':
                const quarter = Math.floor(now.getMonth() / 3)
                const year = now.getFullYear()
                const startMonth = quarter * 3
                return {
                    key: `${year}-Q${quarter + 1}`,
                    label: `Q${quarter + 1} ${year}`,
                    start: new Date(year, startMonth, 1),
                    end: new Date(year, startMonth + 3, 0)
                }

            case 'yearly':
                const currentYear = now.getFullYear()
                return {
                    key: currentYear.toString(),
                    label: currentYear.toString(),
                    start: new Date(currentYear, 0, 1),
                    end: new Date(currentYear, 11, 31)
                }
        }

        return null
    }

    const isDateInPeriod = (dateString, period) => {
        if (!dateString) return false
        const date = new Date(dateString)
        return date >= period.start && date <= period.end
    }

    const fetchLeaseTerminations = async () => {
        pendingLeaseTerminations.value = await fetchPendingLeaseTerminations()
    }

    const fetchLeaseApprovals = async () => {
        pendingLeaseApprovals.value = await fetchPendingLeases()
    }

    const fetchProperties = async () => {
        pendingProperties.value = await fetchPendingProperties()
    }

    const fetchUsersCount = async () => {
        const users = await fetchAllUsers()
        return users.length
    }

    const fetchPropertiesCount = async () => {
        const properties = await fetchAllPropertiesAdmin()
        return properties.length
    }

    const fetchMaintenanceRequestsCount = async () => {
        const maintenance = await fetchAllMaintenanceRequests()
        return maintenance.length
    }

    const fetchStatsData = async () => {
        statsLoading.value = true
        try {
            const [usersCount, propertiesCount, maintenanceCount] = await Promise.all([
                fetchUsersCount(),
                fetchPropertiesCount(),
                fetchMaintenanceRequestsCount()
            ])

            statsData.value = {
                totalUsers: usersCount,
                userGrowth: 0,
                totalProperties: propertiesCount,
                propertyGrowth: 0,
                maintenanceRequests: maintenanceCount,
                urgentRequests: 0
            }
        } catch (error) {
            console.error('Failed to fetch stats data:', error)
        } finally {
            statsLoading.value = false
        }
    }

    const fetchMockAnalytics = () => {
        kpiMetrics.value = {
            'Total Users': 250,
            'Active Landlords': 45,
            'Active Tenants': 93,
            'Total Properties': 162,
            'Rented Properties': 115,
            'Total Leases': 128,
            'Avg. Monthly Rent': '$1200',
            'Total Payments': '$93600',
            'Pending Maintenance': 8,
            'Avg. Maintenance Response': '2.5 days'
        }
    }

    const fetchLeaseChartData = async (periodType = 'monthly') => {
        chartLoading.value = true

        try {
            const leases = await fetchLeaseTrends()

            const currentPeriod = getCurrentPeriod(periodType)

            if (!currentPeriod) {
                setFallbackChartData()
                return
            }

            let leaseStartCount = 0
            let leaseEndCount = 0

            leases.forEach(lease => {
                if (isDateInPeriod(lease.startDate, currentPeriod)) {
                    leaseStartCount++
                }

                const isTerminated = lease.status === 'TERMINATED' ||
                    lease.terminationStatus === 'APPROVED' ||
                    lease.terminationRequestedAt

                if (isTerminated) {
                    const terminationDate = lease.terminationRequestedAt ||
                        (lease.status === 'TERMINATED' ? lease.createdAt : null)

                    if (isDateInPeriod(terminationDate, currentPeriod)) {
                        leaseEndCount++
                    }
                }
            })

            leaseChartData.value = {
                labels: [currentPeriod.label],
                datasets: [
                    {
                        label: 'New Leases',
                        backgroundColor: '#4F46E5',
                        borderColor: '#4F46E5',
                        borderWidth: 2,
                        borderRadius: 4,
                        borderSkipped: false,
                        data: [leaseStartCount]
                    },
                    {
                        label: 'Terminated Leases',
                        backgroundColor: '#F87171',
                        borderColor: '#F87171',
                        borderWidth: 2,
                        borderRadius: 4,
                        borderSkipped: false,
                        data: [leaseEndCount]
                    }
                ]
            }

        } catch (err) {
            console.error('Failed to fetch lease trends:', err)
            setFallbackChartData()
        } finally {
            chartLoading.value = false
        }
    }

    const setFallbackChartData = () => {
        leaseChartData.value = {
            labels: ['No Data Available'],
            datasets: [
                {
                    label: 'New Leases',
                    backgroundColor: '#4F46E5',
                    borderColor: '#4F46E5',
                    borderWidth: 2,
                    borderRadius: 4,
                    borderSkipped: false,
                    data: [0]
                },
                {
                    label: 'Terminated Leases',
                    backgroundColor: '#F87171',
                    borderColor: '#F87171',
                    borderWidth: 2,
                    borderRadius: 4,
                    borderSkipped: false,
                    data: [0]
                }
            ]
        }
    }

    const openTerminationModal = async () => {
        showTerminateModal.value = true
        await fetchLeaseTerminations()
    }

    const openPendingLeasesModal = async () => {
        showPendingLeasesModal.value = true
        await fetchLeaseApprovals()
    }

    const openPendingPropertiesModal = async () => {
        showPendingPropertiesModal.value = true
        await fetchProperties()
    }

    const approveTermination = async (id) => {
        await approveLeaseTermination(id)
        pendingLeaseTerminations.value = pendingLeaseTerminations.value.filter(l => l.leaseId !== id)
    }

    const rejectTermination = async (id) => {
        await rejectLeaseTermination(id)
        pendingLeaseTerminations.value = pendingLeaseTerminations.value.filter(l => l.leaseId !== id)
    }

    const approveLeaseRequest = async (id) => {
        await approveLease(id)
        pendingLeaseApprovals.value = pendingLeaseApprovals.value.filter(l => l.leaseId !== id)
    }

    const rejectLeaseRequest = async (id) => {
        await rejectLease(id)
        pendingLeaseApprovals.value = pendingLeaseApprovals.value.filter(l => l.leaseId !== id)
    }

    const approveProperty = async (propertyId) => {
        try {
            await validateProperty(propertyId, 'APPROVED')
            pendingProperties.value = pendingProperties.value.filter(p => p.propertyId !== propertyId)
            return { success: true, message: 'Property approved successfully' }
        } catch (error) {
            console.error('Failed to approve property:', error)
            return { success: false, message: error.message || 'Failed to approve property' }
        }
    }

    const rejectProperty = async (propertyId) => {
        try {
            await validateProperty(propertyId, 'REJECTED')
            pendingProperties.value = pendingProperties.value.filter(p => p.propertyId !== propertyId)
            return { success: true, message: 'Property rejected successfully' }
        } catch (error) {
            console.error('Failed to reject property:', error)
            return { success: false, message: error.message || 'Failed to reject property' }
        }
    }

    const initDashboard = async () => {
        loading.value = true
        try {
            await Promise.all([
                fetchLeaseTerminations(),
                fetchLeaseApprovals(),
                fetchProperties(),
                fetchLeaseChartData('monthly'),
                fetchMockAnalytics(),
                fetchStatsData()
            ])
        } catch (error) {
            console.error('Failed to initialize dashboard:', error)
        } finally {
            loading.value = false
        }
    }

    return {
        showTerminateModal,
        showPendingLeasesModal,
        showPendingPropertiesModal,

        loading,
        chartLoading,
        statsLoading,

        pendingLeaseTerminations,
        pendingLeaseApprovals,
        pendingProperties,
        kpiMetrics,
        statsData,

        leaseChartData,
        leaseChartOptions,

        stats,

        initDashboard,
        fetchLeaseChartData,
        fetchStatsData,

        openTerminationModal,
        openPendingLeasesModal,
        openPendingPropertiesModal,

        approveTermination,
        rejectTermination,
        approveLease: approveLeaseRequest,
        rejectLease: rejectLeaseRequest,
        approveProperty,
        rejectProperty
    }
})