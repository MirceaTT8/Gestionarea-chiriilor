import { defineStore } from 'pinia'
import { ref } from 'vue'
import {
    fetchMyProperties,
    fetchPropertyById,
    addProperty,
    fetchPendingProperties
} from '@/services/propertyService.js'

export const useLandlordPropertyStore = defineStore('landlordPropertyStore', () => {
    const properties = ref([])
    const pendingProperties = ref([])
    const selectedProperty = ref(null)
    const loading = ref(false)
    const error = ref(null)

    const loadProperties = async () => {
        loading.value = true
        error.value = null
        try {
            const result = await fetchMyProperties()
            properties.value = Array.isArray(result) ? result : []
        } catch (err) {
            console.error('Error loading properties:', err)
            error.value = err?.message || err?.toString() || 'Failed to load properties'
            properties.value = []
        } finally {
            loading.value = false
        }
    }

    const loadPendingProperties = async () => {
        loading.value = true
        error.value = null
        try {
            const result = await fetchPendingProperties()
            pendingProperties.value = Array.isArray(result) ? result : []
        } catch (err) {
            console.error('Error loading pending properties:', err)
            error.value = err?.message || err?.toString() || 'Failed to load pending properties'
            pendingProperties.value = []
        } finally {
            loading.value = false
        }
    }

    const loadPropertyById = async (id) => {
        loading.value = true
        error.value = null
        try {
            selectedProperty.value = await fetchPropertyById(id)
        } catch (err) {
            console.error('Error loading property by ID:', err)
            error.value = err?.message || err?.toString() || 'Failed to fetch property'
            selectedProperty.value = null
        } finally {
            loading.value = false
        }
    }

    const createProperty = async (propertyDTO, attachments) => {
        loading.value = true
        error.value = null
        try {
            const newProp = await addProperty(propertyDTO, attachments)
            if (newProp) {
                properties.value.unshift(newProp)
            }
            return newProp
        } catch (err) {
            console.error('Error creating property:', err)
            error.value = err?.message || err?.toString() || 'Failed to create property'
            throw err
        } finally {
            loading.value = false
        }
    }

    return {
        properties,
        pendingProperties,
        selectedProperty,
        loading,
        error,
        loadProperties,
        loadPendingProperties,
        loadPropertyById,
        createProperty
    }
})