<template>
  <el-container class="admin-container">
    <el-aside width="220px" class="admin-aside">
      <div class="aside-header">
        <div class="aside-logo">⚙️</div>
        <span class="aside-title">后台管理</span>
      </div>
      <el-menu :default-active="activeMenu" router class="admin-menu" :ellipsis="false">
        <el-menu-item index="/admin/users">
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/roles">
          <el-icon><Key /></el-icon>
          <span>角色管理</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-main class="admin-main">
      <router-view />
    </el-main>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { User, Key } from '@element-plus/icons-vue'

const route = useRoute()
const activeMenu = computed(() => route.path)
</script>

<style scoped>
.admin-container {
  min-height: calc(100vh - 60px);
  background: #f0f2f5;
}

.admin-aside {
  background: linear-gradient(180deg, #0f1c2e, #1a2744);
  box-shadow: 2px 0 8px rgba(0,0,0,0.15);
  overflow: hidden;
}

.aside-header {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 20px 18px;
  border-bottom: 1px solid rgba(255,255,255,0.08);
}
.aside-logo { font-size: 22px; }
.aside-title { font-size: 15px; font-weight: 700; color: #fff; letter-spacing: 0.5px; }

.admin-menu {
  background: transparent;
  border: none;
  padding: 8px 0;
}
:deep(.admin-menu .el-menu-item) {
  color: rgba(255,255,255,0.7);
  border-radius: 8px;
  margin: 4px 10px;
  transition: all 0.2s;
}
:deep(.admin-menu .el-menu-item:hover) {
  background: rgba(64, 158, 255, 0.15);
  color: #fff;
}
:deep(.admin-menu .el-menu-item.is-active) {
  background: linear-gradient(135deg, rgba(64, 158, 255, 0.35), rgba(29, 78, 216, 0.25));
  color: #fff;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.2);
}

.admin-main {
  padding: 24px;
  background: #f0f2f5;
}
</style>
