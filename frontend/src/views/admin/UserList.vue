<template>
  <div class="user-list">
    <div class="page-header">
      <h2>用户管理</h2>
      <el-input v-model="keyword" placeholder="搜索用户" style="width: 200px; margin-right: 12px;" clearable @clear="fetchUsers" @keyup.enter="fetchUsers" />
      <el-button type="primary" @click="fetchUsers">搜索</el-button>
    </div>
    <el-table :data="users" v-loading="loading" stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="username" label="用户名" />
      <el-table-column prop="nickname" label="昵称" />
      <el-table-column prop="email" label="邮箱" />
      <el-table-column label="状态">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '正常' : '禁用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="注册时间" />
      <el-table-column label="操作" width="250">
        <template #default="{ row }">
          <el-button type="primary" link @click="toggleStatus(row)">{{ row.status === 1 ? '禁用' : '启用' }}</el-button>
          <el-button type="warning" link @click="showRoleDialog(row)">分配角色</el-button>
          <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      v-model:current-page="page"
      v-model:page-size="pageSize"
      :page-sizes="[10, 20, 50]"
      :total="total"
      layout="total, sizes, prev, pager, next"
      @size-change="fetchUsers"
      @current-change="fetchUsers"
    />

    <el-dialog v-model="roleDialogVisible" title="分配角色" width="400px">
      <el-checkbox-group v-model="selectedRoles">
        <el-checkbox v-for="role in roles" :key="role.id" :label="role.id">
          {{ role.roleName }}
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
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

onMounted(() => {
  fetchUsers()
  fetchRoles()
})
</script>

<style scoped>
.user-list {
  background: white;
  padding: 20px;
  border-radius: 8px;
}

.page-header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h2 {
  margin-right: 20px;
}

.el-pagination {
  margin-top: 20px;
  justify-content: flex-end;
}
</style>
