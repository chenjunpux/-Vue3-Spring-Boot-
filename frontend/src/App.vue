<template>
  <router-view />
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

onMounted(() => {
  // 初始化主题
  const isDark = localStorage.getItem('theme') === 'dark'
  if (isDark) {
    document.documentElement.classList.add('dark')
  }
})
</script>
