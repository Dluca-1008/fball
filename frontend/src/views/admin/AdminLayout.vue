<template>
  <el-container class="admin-container">
    <el-aside width="200px" class="admin-aside">
      <el-menu :default-active="activeMenu" router>
        <el-menu-item index="/admin/users">
          <span>用户管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/roles">
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

const route = useRoute()
const activeMenu = computed(() => route.path)
</script>

<style scoped>
.admin-container {
  min-height: calc(100vh - 60px);
}

.admin-aside {
  background: #fff;
  border-right: 1px solid #e6e6e6;
}

.admin-main {
  background: #f5f7fa;
  padding: 20px;
}
</style>
