<template>
  <div class="role-list">
    <div class="page-header">
      <h2>角色管理</h2>
    </div>
    <el-table :data="roles" v-loading="loading" stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="roleName" label="角色名称" />
      <el-table-column prop="roleCode" label="角色编码" />
      <el-table-column prop="description" label="描述" />
      <el-table-column label="状态">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '正常' : '禁用' }}</el-tag>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'

const loading = ref(false)
const roles = ref([])

async function fetchRoles() {
  loading.value = true
  try {
    const res = await request.get('/api/admin/roles')
    roles.value = res.data
  } finally {
    loading.value = false
  }
}

onMounted(fetchRoles)
</script>

<style scoped>
.role-list {
  background: white;
  padding: 20px;
  border-radius: 8px;
}

.page-header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}
</style>
