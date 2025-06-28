import { defineStore } from 'pinia'
import { useBaseAdminStore } from '@/composables/useBaseAdminStore.js'
import { getAllPayments } from '@/services/paymentService.js'

export const usePaymentAdminStore = defineStore('paymentAdmin', () => {
    const baseStore = useBaseAdminStore({
        entityName: 'payments',
        defaultSortField: 'paymentDate',
        defaultSortDirection: 'desc',
        pageSize: 10,
        defaultFilters: {
            status: 'ALL',
            query: ''
        },
        searchFields: ['paymentId', 'leaseId'],
        idField: 'paymentId'
    })

    const fetchPayments = () => baseStore.fetchItems(getAllPayments)

    return {
        ...baseStore,
        fetchPayments,
        payments: baseStore.items,
        filteredPayments: baseStore.filteredItems
    }
})