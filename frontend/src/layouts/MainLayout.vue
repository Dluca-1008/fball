<!--
  MainLayout — 已废弃，由 SidebarLayout.vue 替代。
  保留仅用于向后兼容未迁移的路由（如 /admin 等独立 layout）。
-->
<template>
  <div class="main-container">
    <el-main class="main-content">
      <router-view />
    </el-main>
  </div>
</template>

<script setup>
</script>

<style scoped>
.main-container {
  min-height: 100vh;
  background: #f0f2f5;
}
.main-content {
  padding: 20px;
}
</style>
