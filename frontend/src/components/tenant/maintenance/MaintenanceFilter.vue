<script setup>
const props = defineProps({
  modelValue: {
    type: String,
    required: true
  }
})

const emit = defineEmits(['update:modelValue'])

const statusOptions = [
  { value: 'PENDING', label: 'Pending', color: 'bg-yellow-100 text-yellow-800' },
  { value: 'IN_PROGRESS', label: 'In Progress', color: 'bg-blue-100 text-blue-800' },
  { value: 'COMPLETED', label: 'Completed', color: 'bg-green-100 text-green-800' },
  { value: 'ALL', label: 'All Requests', color: 'bg-gray-100 text-gray-800' }
]

const getStatusColor = (value) => {
  const option = statusOptions.find(opt => opt.value === value);
  return option ? option.color : 'bg-gray-100 text-gray-800';
}
</script>

<template>
  <div class="relative inline-block text-left">

    <div
        class="origin-top-right absolute right-0 mt-2 w-48 rounded-md shadow-lg bg-white ring-1 ring-black ring-opacity-5 divide-y divide-gray-100 focus:outline-none z-10"
        role="menu"
        aria-orientation="vertical"
        aria-labelledby="menu-button"
        tabindex="-1"
        v-show="false"
    >
      <div class="py-1" role="none">
        <a
            v-for="option in statusOptions"
            :key="option.value"
            href="#"
            class="flex items-center px-4 py-2 text-sm hover:bg-gray-100"
            :class="modelValue === option.value ? 'bg-gray-100' : ''"
            role="menuitem"
            tabindex="-1"
            @click.prevent="$emit('update:modelValue', option.value)"
        >
          <span
              class="inline-flex items-center justify-center h-6 w-6 rounded-full mr-2"
              :class="option.color.replace('text-', 'bg-').replace('800', '500')"
          >
            <span class="text-white text-xs">{{ option.label.charAt(0) }}</span>
          </span>
          <span>{{ option.label }}</span>
        </a>
      </div>
    </div>
  </div>

  <select
      :value="modelValue"
      @change="$emit('update:modelValue', $event.target.value)"
      class="block w-48 pl-3 pr-10 py-2 text-base border border-gray-300 focus:outline-none focus:ring-teal-500 focus:border-teal-500 sm:text-sm rounded-md appearance-none"
      style="background-image: url('data:image/svg+xml;charset=utf-8,%3Csvg xmlns=\'http://www.w3.org/2000/svg\' viewBox=\'0 0 20 20\' fill=\'%236b7280\'%3E%3Cpath fill-rule=\'evenodd\' d=\'M5.293 7.293a1 1 0 011.414 0L10 10.586l3.293-3.293a1 1 0 111.414 1.414l-4 4a1 1 0 01-1.414 0l-4-4a1 1 0 010-1.414z\' clip-rule=\'evenodd\'/%3E%3C/svg%3E'); background-position: right 0.5rem center; background-size: 1.5em 1.5em; background-repeat: no-repeat;"
  >
    <option
        v-for="option in statusOptions"
        :key="option.value"
        :value="option.value"
    >
      {{ option.label }}
    </option>
  </select>
</template>