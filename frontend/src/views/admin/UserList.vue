<template>
  <div class="user-list-page">
    <div class="page-header">
      <div class="header-left">
        <div class="header-icon users">👥</div>
        <div>
          <h1 class="header-title">用户管理</h1>
          <p class="header-desc">管理系统用户</p>
        </div>
      </div>
      <div class="header-actions">
        <el-input v-model="keyword" placeholder="搜索用户" clearable style="width:200px" @clear="fetchUsers" @keyup.enter="fetchUsers" />
        <el-button @click="fetchUsers">搜索</el-button>
      </div>
    </div>

    <el-card class="table-card">
      <el-table :data="users" v-loading="loading" stripe class="user-table">
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="username" label="用户名" width="140">
          <template #default="{ row }">
            <div class="user-cell">
              <el-avatar :size="32" class="user-avatar">{{ row.username?.charAt(0) }}</el-avatar>
              <span>{{ row.username }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="nickname" label="昵称" width="140" />
        <el-table-column prop="email" label="邮箱" />
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <span :class="['status-dot', row.status === 1 ? 'status-on' : 'status-off']"></span>
            <span :class="['status-text', row.status === 1 ? 'status-on' : 'status-off']">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="注册时间" width="170" />
        <el-table-column label="操作" width="260" align="center">
          <template #default="{ row }">
            <el-button
              :type="row.status === 1 ? 'warning' : 'success'"
              link
              size="small"
              @click="toggleStatus(row)"
            >{{ row.status === 1 ? '禁用' : '启用' }}</el-button>
            <el-button type="primary" link size="small" @click="showRoleDialog(row)">分配角色</el-button>
            <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <div class="pagination-wrap" v-if="total > 0">
      <el-pagination
        v-model:current-page="page"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50]"
        :total="total"
        layout="total, sizes, prev, pager, next"
        @size-change="fetchUsers"
        @current-change="fetchUsers"
      />
    </div>

    <el-dialog v-model="roleDialogVisible" title="分配角色" width="420px" class="role-dialog">
      <el-checkbox-group v-model="selectedRoles">
        <el-checkbox v-for="role in roles" :key="role.id" :label="role.id" class="role-checkbox">
          <div class="role-info">
            <span class="role-name">{{ role.roleName }}</span>
            <span class="role-code">{{ role.roleCode }}</span>
          </div>
        </el-checkbox>
      </el-checkbox-group>
      <template #footer>
        <el-button @click="roleDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="assignRoles">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const loading = ref(false)
const users = ref([])
const keyword = ref('')
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)

const roleDialogVisible = ref(false)
const currentUser = ref(null)
const selectedRoles = ref([])
const roles = ref([])

async function fetchUsers() {
  loading.value = true
  try {
    const res = await request.get('/api/admin/users', {
      params: { page: page.value, size: pageSize.value, keyword: keyword.value }
    })
    users.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

async function fetchRoles() {
  try {
    const res = await request.get('/api/admin/roles')
    roles.value = res.data
  } catch (error) {
    console.error('获取角色列表失败', error)
  }
}

async function toggleStatus(user) {
  const newStatus = user.status === 1 ? 0 : 1
  try {
    await request.put(`/api/admin/users/${user.id}/status`, null, {
      params: { status: newStatus }
    })
    ElMessage.success('操作成功')
    fetchUsers()
  } catch (error) {
    console.error(error)
  }
}

async function showRoleDialog(user) {
  currentUser.value = user
  selectedRoles.value = []
  try {
    const res = await request.get(`/api/admin/users/${user.id}`)
    const userData = res.data
    if (userData.roles) {
      selectedRoles.value = userData.roles.map(r => r.id)
    }
  } catch (error) {
    console.error('获取用户角色失败', error)
  }
  roleDialogVisible.value = true
}

async function assignRoles() {
  try {
    await request.post(`/api/admin/users/${currentUser.value.id}/roles`, selectedRoles.value)
    ElMessage.success('分配成功')
    roleDialogVisible.value = false
  } catch (error) {
    console.error(error)
  }
}

async function handleDelete(user) {
  try {
    await ElMessageBox.confirm(`确定要删除用户"${user.username}"吗？`, '确认删除', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await request.delete(`/api/admin/users/${user.id}`)
    ElMessage.success('删除成功')
    fetchUsers()
  } catch (error) {
    if (error !== 'cancel') console.error(error)
  }
}

onMounted(() => { fetchUsers(); fetchRoles() })
</script>

<style scoped>
.user-list-page { }

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
  flex-wrap: wrap;
  gap: 12px;
}
.header-left { display: flex; align-items: center; gap: 14px; }
.header-icon {
  width: 48px; height: 48px;
  background: linear-gradient(135deg, #409eff, #1d4ed8);
  border-radius: 12px;
  display: flex; align-items: center; justify-content: center;
  font-size: 24px;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}
.header-title { font-size: 21px; font-weight: 700; color: #1a202c; margin-bottom: 2px; }
.header-desc { font-size: 13px; color: #718096; }
.header-actions { display: flex; gap: 10px; }

.table-card { background: #fff; border-radius: 16px; overflow: hidden; }
.user-table { background: transparent; }

.user-cell { display: flex; align-items: center; gap: 10px; }
.user-avatar {
  background: linear-gradient(135deg, #409eff, #1d4ed8) !important;
  font-size: 14px !important;
  font-weight: 700;
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

.pagination-wrap { display: flex; justify-content: flex-end; margin-top: 20px; }

/* ── 角色弹窗 ── */
:deep(.role-dialog .el-dialog__header) { padding: 20px 24px 16px; }
:deep(.role-dialog .el-dialog__body) { padding: 20px 24px; }
:deep(.role-dialog .el-dialog__footer) { padding: 12px 24px 20px; }
.role-checkbox {
  display: flex;
  align-items: center;
  width: 100%;
  margin-bottom: 8px;
}
.role-info { display: flex; flex-direction: column; }
.role-name { font-weight: 600; color: #2d3748; }
.role-code { font-size: 12px; color: #a0aec0; margin-top: 2px; }
</style>
