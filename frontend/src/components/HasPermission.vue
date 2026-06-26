<template>
  <div v-if="hasPermission">
    <slot />
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useUserStore } from '@/stores/user'

const props = defineProps({
  code: { type: String, required: true }
})

const userStore = useUserStore()
const hasPermission = computed(() => userStore.hasPermission(props.code))
</script>
