<script setup>
import { ref } from 'vue'

defineProps({
  requests: Array,
  sortField: String,
  sortDirection: String
})

const emit = defineEmits(['sort', 'view-images'])

const showDetails = ref(null)

const columns = [
  { label: 'Request ID', key: 'requestId' },
  { label: 'Lease ID', key: 'leaseId' },
  { label: 'Description', key: 'description' },
  { label: 'Status', key: 'status' },
  { label: 'Is Fixed', key: 'isFixed' },
  { label: 'Cost', key: 'cost' },
  { label: 'Created', key: 'createdAt' }
]

const statusColor = (status) => {
  switch (status?.toLowerCase()) {
    case 'pending':
      return 'bg-yellow-100 text-yellow-800'
    case 'in_progress':
    case 'in progress':
      return 'bg-blue-100 text-blue-800'
    case 'completed':
      return 'bg-green-100 text-green-800'
    case 'cancelled':
    case 'rejected':
      return 'bg-red-100 text-red-800'
    default:
      return 'bg-gray-100 text-gray-800'
  }
}

const statusDotColor = (status) => {
  switch (status?.toLowerCase()) {
    case 'pending':
      return 'bg-yellow-400'
    case 'in_progress':
    case 'in progress':
      return 'bg-blue-400'
    case 'completed':
      return 'bg-green-400'
    case 'cancelled':
    case 'rejected':
      return 'bg-red-400'
    default:
      return 'bg-gray-400'
  }
}

const isFixedColor = (isFixed) => {
  if (isFixed === null || isFixed === undefined) {
    return 'bg-gray-100 text-gray-600'
  } else if (isFixed === true) {
    return 'bg-green-100 text-green-800'
  } else {
    return 'bg-orange-100 text-orange-800'
  }
}

const isFixedDotColor = (isFixed) => {
  if (isFixed === null || isFixed === undefined) {
    return 'bg-gray-400'
  } else if (isFixed === true) {
    return 'bg-green-500'
  } else {
    return 'bg-orange-500'
  }
}

const isFixedLabel = (isFixed) => {
  if (isFixed === null || isFixed === undefined) {
    return 'No Response'
  } else if (isFixed === true) {
    return 'Fixed'
  } else {
    return 'Not Fixed'
  }
}

const formatCurrency = (amount) => {
  return amount != null ? `$${amount.toFixed(2)}` : '—'
}
</script>

<template>
  <div class="overflow-x-auto">
    <table class="min-w-full table-auto">
      <thead>
      <tr class="bg-gray-50 border-b border-gray-200">
        <th
            v-for="col in columns"
            :key="col.key"
            @click="emit('sort', col.key)"
            class="px-6 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider cursor-pointer hover:bg-gray-100 transition-colors"
        >
          <div class="flex items-center gap-1">
            {{ col.label }}
            <span v-if="sortField === col.key" class="text-indigo-500">
                {{ sortDirection === 'asc' ? '↑' : '↓' }}
              </span>
          </div>
        </th>
        <th class="px-6 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">Images</th>
      </tr>
      </thead>
      <tbody class="bg-white divide-y divide-gray-100">
      <tr
          v-for="request in requests"
          :key="request.requestId"
          class="hover:bg-gray-50 transition-colors cursor-pointer"
          @click="showDetails === request.requestId ? showDetails = null : showDetails = request.requestId"
      >
        <td class="px-6 py-4 whitespace-nowrap">
          <div class="text-sm font-medium text-gray-900">#{{ request.requestId }}</div>
        </td>
        <td class="px-6 py-4 whitespace-nowrap">
          <div class="text-sm text-gray-900">{{ request.leaseId }}</div>
        </td>
        <td class="px-6 py-4">
          <div class="text-sm text-gray-900 max-w-xs truncate" :title="request.description">
            {{ request.description }}
          </div>
        </td>
        <td class="px-6 py-4 whitespace-nowrap">
            <span
                class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium"
                :class="statusColor(request.status)"
            >
              <span :class="statusDotColor(request.status)" class="w-1.5 h-1.5 mr-1.5 rounded-full"></span>
              {{ request.status }}
            </span>
        </td>
        <td class="px-6 py-4 whitespace-nowrap">
          <span
              class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium"
              :class="isFixedColor(request.isFixed)"
          >
            <span :class="isFixedDotColor(request.isFixed)" class="w-1.5 h-1.5 mr-1.5 rounded-full"></span>
            {{ isFixedLabel(request.isFixed) }}
          </span>
        </td>
        <td class="px-6 py-4 whitespace-nowrap">
          <div class="text-sm font-medium text-gray-900">
            {{ formatCurrency(request.cost) }}
          </div>
        </td>
        <td class="px-6 py-4 whitespace-nowrap">
          <div class="text-sm text-gray-900">{{ new Date(request.createdAt).toLocaleDateString() }}</div>
          <div class="text-xs text-gray-500">{{ new Date(request.createdAt).toLocaleTimeString() }}</div>
        </td>
        <td class="px-6 py-4 whitespace-nowrap">
          <button
              v-if="request.imageUrls?.length"
              @click.stop="emit('view-images', request.imageUrls)"
              class="inline-flex items-center px-3 py-1 border border-transparent text-xs font-medium rounded-md text-indigo-600 bg-indigo-100 hover:bg-indigo-200 transition-colors"
          >
            <svg class="w-3 h-3 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z"/>
            </svg>
            View ({{ request.imageUrls.length }})
          </button>
          <span v-else class="text-xs text-gray-400">No images</span>
        </td>
      </tr>

      <!-- Expandable detail row -->
      <tr
          v-for="request in requests"
          :key="`detail-${request.requestId}`"
          v-show="showDetails === request.requestId"
      >
        <td colspan="8" class="px-6 py-4 bg-gray-50">
          <div class="space-y-4">
            <div>
              <h4 class="text-xs font-semibold text-gray-500 uppercase mb-1">Full Description</h4>
              <p class="text-sm text-gray-900">{{ request.description }}</p>
            </div>

            <div v-if="request.status === 'COMPLETED'">
              <h4 class="text-xs font-semibold text-gray-500 uppercase mb-1">Tenant Feedback</h4>
              <div class="bg-white p-3 rounded border">
                <div class="flex items-center gap-2 mb-2">
                  <span class="text-sm font-medium">Issue Resolution Status:</span>
                  <span
                      class="inline-flex items-center px-2 py-1 rounded-full text-xs font-medium"
                      :class="isFixedColor(request.isFixed)"
                  >
                    <span :class="isFixedDotColor(request.isFixed)" class="w-1.5 h-1.5 mr-1.5 rounded-full"></span>
                    {{ isFixedLabel(request.isFixed) }}
                  </span>
                </div>
                <div v-if="request.tenantFeedbackDate" class="text-xs text-gray-500 mb-1">
                  Feedback submitted: {{ new Date(request.tenantFeedbackDate).toLocaleDateString() }}
                </div>
                <div v-if="request.tenantFeedback" class="text-sm text-gray-700 italic">
                  "{{ request.tenantFeedback }}"
                </div>
                <div v-else-if="request.isFixed === false" class="text-sm text-gray-500 italic">
                  Tenant marked as not fixed - no additional feedback provided
                </div>
                <div v-else-if="request.isFixed === null" class="text-sm text-gray-500 italic">
                  Awaiting tenant feedback
                </div>
              </div>
            </div>
          </div>
        </td>
      </tr>
      </tbody>
    </table>
  </div>
</template>