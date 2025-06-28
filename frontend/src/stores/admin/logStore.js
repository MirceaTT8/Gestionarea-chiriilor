import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { fetchAllLogs } from '@/services/logsService.js'
import { usePagination } from "@/composables/usePagination.js";

export const useLogStore = defineStore('logStore', () => {
    const logs = ref([])
    const loading = ref(false)
    const error = ref(null)

    const filters = ref({
        actionType: '',
        entityType: '',
        userId: '',
        dateRange: 'all'
    })

    const sortField = ref('createdAt')
    const sortDirection = ref('desc')

    const fetchLogs = async () => {
        try {
            loading.value = true
            logs.value = await fetchAllLogs()
        } catch (err) {
            error.value = 'Failed to fetch logs'
            console.error(err)
        } finally {
            loading.value = false
        }
    }

    const totalLogs = computed(() => logs.value.length)

    const errorCount = computed(() => {
        return logs.value.filter(log =>
            log.actionType?.toLowerCase().includes('error') ||
            log.level === 'error' ||
            log.severity === 'error'
        ).length
    })

    const warningCount = computed(() => {
        return logs.value.filter(log =>
            log.actionType?.toLowerCase().includes('warning') ||
            log.level === 'warning' ||
            log.severity === 'warning'
        ).length
    })

    const infoCount = computed(() => {
        return logs.value.filter(log =>
            log.actionType?.toLowerCase().includes('info') ||
            log.level === 'info' ||
            log.severity === 'info' ||
            (!log.level && !log.severity && !log.actionType?.toLowerCase().includes('error') && !log.actionType?.toLowerCase().includes('warning'))
        ).length
    })

    const uniqueActionTypes = computed(() => {
        const actions = new Set()
        logs.value.forEach(log => {
            if (log.actionType) actions.add(log.actionType)
        })
        return Array.from(actions).sort()
    })

    const uniqueEntityTypes = computed(() => {
        const entities = new Set()
        logs.value.forEach(log => {
            if (log.entityType) entities.add(log.entityType)
        })
        return Array.from(entities).sort()
    })

    const uniqueUserIds = computed(() => {
        const users = new Set()
        logs.value.forEach(log => {
            if (log.userId) users.add(log.userId)
        })
        return Array.from(users).sort((a, b) => {
            const aNum = parseInt(a)
            const bNum = parseInt(b)
            if (!isNaN(aNum) && !isNaN(bNum)) {
                return aNum - bNum
            }
            return a.toString().localeCompare(b.toString())
        })
    })

    const filteredLogs = computed(() => {
        return logs.value.filter(log => {
            if (filters.value.actionType && log.actionType !== filters.value.actionType) {
                return false
            }

            if (filters.value.entityType && log.entityType !== filters.value.entityType) {
                return false
            }

            if (filters.value.userId) {
                const filterUserId = filters.value.userId.toString()
                const logUserId = log.userId?.toString()
                if (logUserId !== filterUserId) {
                    return false
                }
            }

            if (filters.value.dateRange !== 'all') {
                const logDate = new Date(log.createdAt)
                const now = new Date()

                switch (filters.value.dateRange) {
                    case 'today':
                        return logDate.toDateString() === now.toDateString()
                    case 'week': {
                        const startOfWeek = new Date(now)
                        startOfWeek.setDate(now.getDate() - now.getDay())
                        startOfWeek.setHours(0, 0, 0, 0)
                        return logDate >= startOfWeek
                    }
                    case 'month': {
                        const startOfMonth = new Date(now.getFullYear(), now.getMonth(), 1)
                        return logDate >= startOfMonth
                    }
                    default:
                        return true
                }
            }

            return true
        })
    })

    const sortedFilteredLogs = computed(() => {
        return [...filteredLogs.value].sort((a, b) => {
            let fieldA = a[sortField.value]
            let fieldB = b[sortField.value]

            if (sortField.value === 'createdAt') {
                fieldA = new Date(fieldA)
                fieldB = new Date(fieldB)
            }

            if (fieldA == null && fieldB == null) return 0
            if (fieldA == null) return sortDirection.value === 'asc' ? -1 : 1
            if (fieldB == null) return sortDirection.value === 'asc' ? 1 : -1

            if (fieldA < fieldB) return sortDirection.value === 'asc' ? -1 : 1
            if (fieldA > fieldB) return sortDirection.value === 'asc' ? 1 : -1
            return 0
        })
    })

    const { currentPage, totalPages, paginatedItems: paginatedLogs, resetPagination } =
        usePagination(sortedFilteredLogs, 20)

    const middlePages = computed(() => {
        const current = currentPage.value
        const total = totalPages.value
        const pages = []

        if (total <= 7) {
            for (let i = 1; i <= total; i++) {
                pages.push(i)
            }
        } else {
            let start = Math.max(2, current - 1)
            let end = Math.min(total - 1, current + 1)

            if (current <= 3) {
                start = 2
                end = Math.min(5, total - 1)
            } else if (current >= total - 2) {
                start = Math.max(total - 4, 2)
                end = total - 1
            }

            for (let i = start; i <= end; i++) {
                pages.push(i)
            }
        }

        return pages
    })

    const sortBy = (field) => {
        if (sortField.value === field) {
            sortDirection.value = sortDirection.value === 'asc' ? 'desc' : 'asc'
        } else {
            sortField.value = field
            sortDirection.value = 'asc'
        }
        resetPagination()
    }

    const applyFilters = () => {
        resetPagination()
    }

    const resetFilters = () => {
        filters.value = {
            actionType: '',
            entityType: '',
            userId: '',
            dateRange: 'all'
        }
        resetPagination()
    }

    const updateFilter = (filterKey, value) => {
        filters.value[filterKey] = value
        resetPagination()
    }

    return {
        logs,
        loading,
        error,
        filters,
        currentPage,
        sortField,
        sortDirection,

        totalLogs,
        errorCount,
        warningCount,
        infoCount,

        uniqueActionTypes,
        uniqueEntityTypes,
        uniqueUserIds,

        filteredLogs,
        paginatedLogs,
        totalPages,
        middlePages,

        fetchLogs,
        sortBy,
        applyFilters,
        resetFilters,
        updateFilter
    }
})