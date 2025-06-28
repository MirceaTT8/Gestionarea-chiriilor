import { defineStore } from 'pinia'
import { computed } from 'vue'
import { useBaseAdminStore } from '@/composables/useBaseAdminStore.js'
import {
    fetchAllPropertiesAdmin,
    validateProperty,
    fetchPendingProperties
} from '@/services/propertyService.js'

export const useAdminPropertyStore = defineStore('adminPropertyStore', () => {
    const baseStore = useBaseAdminStore({
        entityName: 'properties',
        defaultSortField: 'createdAt',
        defaultSortDirection: 'desc',
        pageSize: 10,
        defaultFilters: {
            status: 'ALL'
        },
        searchFields: ['name', 'address', 'propertyId'],
        idField: 'propertyId',
        countFields: [
            { name: 'activeProperties', type: 'status', field: 'status', value: 'ACTIVE' },
            { name: 'pendingProperties', type: 'status', field: 'validationStatus', value: 'PENDING' },
            { name: 'rentedProperties', type: 'status', field: 'status', value: 'RENTED' },
            { name: 'inactiveProperties', type: 'status', field: 'status', value: 'INACTIVE' },
            { name: 'maintenanceProperties', type: 'status', field: 'status', value: 'MAINTENANCE' }
        ]
    })

    const statuses = ['ALL', 'ACTIVE', 'PENDING', 'INACTIVE', 'RENTED', 'MAINTENANCE']

    const fetchProperties = () => baseStore.fetchItems(fetchAllPropertiesAdmin)

    const validatePropertyAction = (propertyId) =>
        baseStore.executeAction(validateProperty, propertyId)

    const selectedStatus = computed({
        get: () => baseStore.filters.status,
        set: (value) => baseStore.updateFilter('status', value)
    })

    return {
        ...baseStore,
        fetchProperties,
        validatePropertyAction,
        statuses,
        properties: baseStore.items,
        selectedProperties: baseStore.selectedItems,
        filteredProperties: baseStore.filteredItems,
        totalProperties: baseStore.totalItems,
        selectedStatus,
        activeProperties: computed(() => baseStore.counts.activeProperties),
        pendingProperties: computed(() => baseStore.counts.pendingProperties),
        rentedProperties: computed(() => baseStore.counts.rentedProperties),
        inactiveProperties: computed(() => baseStore.counts.inactiveProperties),
        maintenanceProperties: computed(() => baseStore.counts.maintenanceProperties)
    }
})