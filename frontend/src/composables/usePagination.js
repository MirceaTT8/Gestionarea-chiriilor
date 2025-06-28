import { ref, computed } from 'vue'

export function usePagination(itemsRef, pageSize = 10) {
    const currentPage = ref(1)

    const totalPages = computed(() =>
        Math.ceil(itemsRef.value.length / pageSize)
    )

    const paginatedItems = computed(() => {
        const start = (currentPage.value - 1) * pageSize
        return itemsRef.value.slice(start, start + pageSize)
    })

    const resetPagination = () => {
        currentPage.value = 1
    }

    return {
        currentPage,
        totalPages,
        paginatedItems,
        resetPagination
    }
}
