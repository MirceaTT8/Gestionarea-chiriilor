import { ref, computed } from 'vue'
import { usePagination } from '@/composables/usePagination.js'

export function useBaseAdminStore(config = {}) {
    const {
        entityName = 'items',
        defaultSortField = 'createdAt',
        defaultSortDirection = 'desc',
        pageSize = 10,
        defaultFilters = {},
        searchFields = ['name', 'id'],
        uniqueFields = [],
        countFields = [],
        idField = 'id'
    } = config

    const items = ref([])
    const loading = ref(false)
    const error = ref(null)
    const selectedItems = ref(new Set())
    const searchQuery = ref('')
    const filters = ref({ ...defaultFilters })
    const sortField = ref(defaultSortField)
    const sortDirection = ref(defaultSortDirection)

    const totalItems = computed(() => items.value.length)

    const counts = computed(() => {
        if (!countFields.length) return {}

        return countFields.reduce((acc, field) => {
            if (field.type === 'status') {
                acc[field.name] = items.value.filter(item =>
                    item[field.field] === field.value
                ).length
            } else if (field.type === 'condition') {
                acc[field.name] = items.value.filter(field.condition).length
            }
            return acc
        }, {})
    })

    const uniqueValues = computed(() => {
        if (!uniqueFields.length || !items.value.length) return {}

        return uniqueFields.reduce((acc, fieldName) => {
            const values = new Set()
            items.value.forEach(item => {
                const value = item[fieldName]
                if (value !== null && value !== undefined && value !== '') {
                    values.add(value)
                }
            })
            acc[fieldName] = Array.from(values).sort()
            return acc
        }, {})
    })

    const filteredItems = computed(() => {
        return items.value.filter(item => {
            if (searchQuery.value) {
                const query = searchQuery.value.toLowerCase()
                const matchesSearch = searchFields.some(field => {
                    const value = item[field]
                    return value && value.toString().toLowerCase().includes(query)
                })
                if (!matchesSearch) return false
            }

            for (const [filterKey, filterValue] of Object.entries(filters.value)) {
                if (filterValue && filterValue !== 'ALL' && filterValue !== '') {
                    if (filterKey === 'dateRange' && filterValue !== 'all') {
                        const itemDate = new Date(item.createdAt || item.date)
                        const now = new Date()

                        switch (filterValue) {
                            case 'today':
                                const today = new Date(now.getFullYear(), now.getMonth(), now.getDate())
                                if (itemDate < today) return false
                                break
                            case 'week':
                                const weekAgo = new Date(now.getTime() - 7 * 24 * 60 * 60 * 1000)
                                if (itemDate < weekAgo) return false
                                break
                            case 'month':
                                const monthAgo = new Date(now.getTime() - 30 * 24 * 60 * 60 * 1000)
                                if (itemDate < monthAgo) return false
                                break
                        }
                    } else {
                        if (item[filterKey] !== filterValue) return false
                    }
                }
            }

            return true
        }).sort((a, b) => {
            const aVal = a[sortField.value]
            const bVal = b[sortField.value]

            if (typeof aVal === 'string' && typeof bVal === 'string') {
                const comparison = aVal.localeCompare(bVal)
                return sortDirection.value === 'asc' ? comparison : -comparison
            }

            if (aVal instanceof Date && bVal instanceof Date) {
                return sortDirection.value === 'asc' ? aVal - bVal : bVal - aVal
            }

            if (sortField.value.includes('Date') || sortField.value.includes('At')) {
                const dateA = new Date(aVal)
                const dateB = new Date(bVal)
                return sortDirection.value === 'asc' ? dateA - dateB : dateB - dateA
            }

            if (aVal < bVal) return sortDirection.value === 'asc' ? -1 : 1
            if (aVal > bVal) return sortDirection.value === 'asc' ? 1 : -1
            return 0
        })
    })

    const {
        currentPage,
        totalPages,
        paginatedItems,
        resetPagination
    } = usePagination(filteredItems, pageSize)

    const fetchItems = async (fetchFunction) => {
        loading.value = true
        error.value = null
        try {
            const result = await fetchFunction()
            items.value = Array.isArray(result) ? result : []
        } catch (err) {
            error.value = err.message || `Failed to fetch ${entityName}`
            console.error(`Error fetching ${entityName}:`, err)
            items.value = []
        } finally {
            loading.value = false
        }
    }

    const executeAction = async (actionFunction, ...args) => {
        loading.value = true
        error.value = null
        try {
            const result = await actionFunction(...args)
            return result
        } catch (err) {
            error.value = err.message || 'Action failed'
            throw err
        } finally {
            loading.value = false
        }
    }

    const setSort = (field) => {
        if (sortField.value === field) {
            sortDirection.value = sortDirection.value === 'asc' ? 'desc' : 'asc'
        } else {
            sortField.value = field
            sortDirection.value = 'asc'
        }
    }

    const toggleSelection = (itemId) => {
        if (selectedItems.value.has(itemId)) {
            selectedItems.value.delete(itemId)
        } else {
            selectedItems.value.add(itemId)
        }
    }

    const selectAll = () => {
        filteredItems.value.forEach(item => {
            selectedItems.value.add(item[idField])
        })
    }

    const clearSelection = () => {
        selectedItems.value.clear()
    }

    const isSelected = (itemId) => selectedItems.value.has(itemId)

    const selectedCount = computed(() => selectedItems.value.size)

    const updateFilter = (filterKey, value) => {
        filters.value[filterKey] = value
        resetPagination()
    }

    const clearFilters = () => {
        filters.value = { ...defaultFilters }
        searchQuery.value = ''
        resetPagination()
    }

    const updateSearch = (query) => {
        searchQuery.value = query
        resetPagination()
    }

    return {
        items,
        loading,
        error,
        selectedItems,
        searchQuery,
        filters,
        sortField,
        sortDirection,
        totalItems,
        counts,
        uniqueValues,
        filteredItems,
        currentPage,
        totalPages,
        paginatedItems,
        selectedCount,
        fetchItems,
        executeAction,
        setSort,
        toggleSelection,
        selectAll,
        clearSelection,
        isSelected,
        updateFilter,
        clearFilters,
        updateSearch,
        resetPagination
    }
}