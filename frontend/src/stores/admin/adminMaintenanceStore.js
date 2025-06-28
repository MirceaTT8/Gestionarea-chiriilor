import { defineStore } from 'pinia'
import { useBaseAdminStore } from '@/composables/useBaseAdminStore.js'
import { fetchAllMaintenanceRequests } from '@/services/maintenanceService.js'

export const useMaintenanceAdminStore = defineStore('maintenanceAdmin', () => {
    const baseStore = useBaseAdminStore({
        entityName: 'maintenance requests',
        defaultSortField: 'createdAt',
        defaultSortDirection: 'desc',
        pageSize: 10,
        defaultFilters: {
            status: 'ALL',
            query: ''
        },
        searchFields: ['leaseId', 'requestId'],
        idField: 'requestId'
    })

    const fetchRequests = () => baseStore.fetchItems(fetchAllMaintenanceRequests)

    return {
        ...baseStore,
        fetchRequests,
        requests: baseStore.items,
        filteredRequests: baseStore.filteredItems,
        paginatedRequests: baseStore.paginatedItems
    }
})