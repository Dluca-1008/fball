<template>
  <div class="role-list-page">
    <div class="page-header">
      <div class="header-left">
        <div class="header-icon roles">🔑</div>
        <div>
          <h1 class="header-title">角色管理</h1>
          <p class="header-desc">管理系统角色权限</p>
        </div>
      </div>
    </div>

    <el-card class="table-card">
      <el-table :data="roles" v-loading="loading" stripe class="role-table">
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="roleName" label="角色名称" width="160">
          <template #default="{ row }">
            <div class="role-name-cell">
              <el-avatar :size="32" class="role-avatar">{{ row.roleName?.charAt(0) }}</el-avatar>
              <span>{{ row.roleName }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="roleCode" label="角色编码" width="160">
          <template #default="{ row }">
            <code class="role-code-badge">{{ row.roleCode }}</code>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" />
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <span :class="['status-dot', row.status === 1 ? 'status-on' : 'status-off']"></span>
            <span :class="['status-text', row.status === 1 ? 'status-on' : 'status-off']">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </span>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
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
.role-list-page { }

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}
.header-left { display: flex; align-items: center; gap: 14px; }
.header-icon {
  width: 48px; height: 48px;
  background: linear-gradient(135deg, #e6a23c, #c45d0e);
  border-radius: 12px;
  display: flex; align-items: center; justify-content: center;
  font-size: 24px;
  box-shadow: 0 4px 12px rgba(230, 162, 60, 0.3);
}
.header-title { font-size: 21px; font-weight: 700; color: #1a202c; margin-bottom: 2px; }
.header-desc { font-size: 13px; color: #718096; }

.table-card { background: #fff; border-radius: 16px; overflow: hidden; }
.role-table { background: transparent; }

.role-name-cell { display: flex; align-items: center; gap: 10px; }
.role-avatar {
  background: linear-gradient(135deg, #e6a23c, #c45d0e) !important;
  font-size: 14px !important;
  font-weight: 700;
}
.role-code-badge {
  background: rgba(230, 162, 60, 0.08);
  color: #c45d0e;
  padding: 3px 10px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 600;
  font-family: 'Courier New', monospace;
}

.status-dot {
  display: inline-block;
  width: 8px; height: 8px;
  border-radius: 50%;
  margin-right: 6px;
  vertical-align: middle;
}
.status-on { background: #67c23a; }
.status-off { background: #f56c6c; }
.status-text { font-size: 13px; font-weight: 500; }
</style>
