<template>
  <div class="team-manage">
    <div class="page-header">
      <h2>球队管理</h2>
      <div>
        <el-button type="danger" @click="handleDissolve">解散球队</el-button>
        <el-button @click="$router.back()">返回</el-button>
      </div>
    </div>

    <el-tabs v-model="activeTab">
      <el-tab-pane label="球队信息" name="info">
        <el-form ref="formRef" :model="form" :rules="rules" label-width="80px" style="max-width: 600px;">
          <el-form-item label="球队名称" prop="name">
            <el-input v-model="form.name" />
          </el-form-item>
          <el-form-item label="城市" prop="city">
            <el-input v-model="form.city" />
          </el-form-item>
          <el-form-item label="国家" prop="country">
            <el-input v-model="form.country" />
          </el-form-item>
          <el-form-item label="主场" prop="stadium">
            <el-input v-model="form.stadium" />
          </el-form-item>
          <el-form-item label="简介" prop="description">
            <el-input v-model="form.description" type="textarea" :rows="4" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
          </el-form-item>
        </el-form>
      </el-tab-pane>

      <el-tab-pane label="成员管理" name="members">
        <div class="section-header">
          <span>当前成员</span>
          <el-button type="primary" size="small" @click="inviteDialogVisible = true">邀请成员</el-button>
        </div>
        <el-table :data="members" stripe>
          <el-table-column prop="userId" label="用户ID" width="80" />
          <el-table-column label="角色" width="120">
            <template #default="{ row }">
              <el-tag :type="row.role === 'admin' ? 'danger' : 'info'">{{ row.role === 'admin' ? '管理员' : '成员' }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="身份" width="80">
            <template #default="{ row }">
              <el-tag :type="row.memberType === 'coach' ? 'warning' : 'success'">{{ row.memberType === 'coach' ? '教练' : '球员' }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="参赛状态" width="100">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '可参赛' : '不可参赛' }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="joinTime" label="加入时间" />
          <el-table-column label="操作" width="260">
            <template #default="{ row }">
              <template v-if="row.userId !== currentUserId">
                <el-button v-if="row.role !== 'admin'" type="warning" link @click="handleSetAdmin(row)">设为管理员</el-button>
                <el-button v-if="row.role === 'admin'" type="info" link @click="handleCancelAdmin(row)">取消管理员</el-button>
                <el-button type="primary" link @click="handleToggleStatus(row)">{{ row.status === 1 ? '设为不可参赛' : '设为可参赛' }}</el-button>
                <el-button type="danger" link @click="handleRemoveMember(row)">移除</el-button>
              </template>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="申请审核" name="applications">
        <el-table :data="applications" stripe>
          <el-table-column prop="userId" label="申请人ID" width="100" />
          <el-table-column label="申请身份" width="80">
            <template #default="{ row }">
              <el-tag :type="row.memberType === 'coach' ? 'warning' : 'success'">{{ row.memberType === 'coach' ? '教练' : '球员' }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="reason" label="申请理由" />
          <el-table-column prop="createdAt" label="申请时间" />
          <el-table-column label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="row.status === 0 ? 'warning' : row.status === 1 ? 'success' : 'danger'">
                {{ row.status === 0 ? '待审核' : row.status === 1 ? '已通过' : '已拒绝' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="160">
            <template #default="{ row }">
              <template v-if="row.status === 0">
                <el-button type="success" link @click="handleApprove(row)">通过</el-button>
                <el-button type="danger" link @click="handleReject(row)">拒绝</el-button>
              </template>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <el-dialog v-model="inviteDialogVisible" title="邀请成员" width="400px">
      <el-form :model="inviteForm" label-width="80px">
        <el-form-item label="用户ID">
          <el-input-number v-model="inviteForm.userId" :min="1" style="width: 100%;" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="inviteDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="inviting" @click="handleInvite">邀请</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const teamId = route.params.id

const activeTab = ref('info')
const formRef = ref(null)
const saving = ref(false)
const members = ref([])
const applications = ref([])
const inviteDialogVisible = ref(false)
const inviting = ref(false)
const currentUserId = ref(userStore.userInfo?.id)

const form = reactive({
  name: '',
  city: '',
  country: '',
  stadium: '',
  description: ''
})

const inviteForm = reactive({
  userId: null
})

const rules = {
  name: [{ required: true, message: '请输入球队名称', trigger: 'blur' }],
  city: [{ required: true, message: '请输入城市', trigger: 'blur' }],
  country: [{ required: true, message: '请输入国家', trigger: 'blur' }]
}

async function fetchTeam() {
  const res = await request.get(`/api/teams/${teamId}`)
  Object.assign(form, res.data)
}

async function fetchMembers() {
  const res = await request.get(`/api/teams/${teamId}/members`)
  members.value = res.data
}

async function fetchApplications() {
  const res = await request.get(`/api/teams/${teamId}/applications`)
  applications.value = res.data
}

async function handleSave() {
  await formRef.value.validate()
  saving.value = true
  try {
    await request.put(`/api/teams/${teamId}`, form)
    ElMessage.success('保存成功')
  } finally {
    saving.value = false
  }
}

async function handleSetAdmin(row) {
  await ElMessageBox.confirm('确定设为管理员？', '提示')
  await request.put(`/api/teams/${teamId}/members/${row.userId}/role`, null, { params: { role: 'admin' } })
  ElMessage.success('操作成功')
  fetchMembers()
}

async function handleCancelAdmin(row) {
  await ElMessageBox.confirm('确定取消管理员？', '提示')
  await request.put(`/api/teams/${teamId}/members/${row.userId}/role`, null, { params: { role: 'member' } })
  ElMessage.success('操作成功')
  fetchMembers()
}

async function handleRemoveMember(row) {
  await ElMessageBox.confirm('确定移除该成员？', '提示')
  await request.put(`/api/teams/${teamId}/members/${row.userId}/remove`)
  ElMessage.success('操作成功')
  fetchMembers()
}

async function handleToggleStatus(row) {
  const newStatus = row.status === 1 ? 0 : 1
  const action = newStatus === 1 ? '设为可参赛' : '设为不可参赛'
  await ElMessageBox.confirm(`确定将该成员${action}？`, '提示')
  await request.put(`/api/teams/${teamId}/members/${row.userId}/status`, { status: newStatus })
  ElMessage.success('操作成功')
  fetchMembers()
}

async function handleApprove(row) {
  await request.put(`/api/teams/${teamId}/members/${row.userId}/approve`, {
    status: 1,
    memberType: row.memberType || 'player',
    memberInfo: null
  })
  ElMessage.success('已通过')
  fetchApplications()
  fetchMembers()
}

async function handleReject(row) {
  await request.put(`/api/teams/${teamId}/members/${row.userId}/approve`, {
    status: 2,
    memberType: null,
    memberInfo: null
  })
  ElMessage.success('已拒绝')
  fetchApplications()
}

async function handleInvite() {
  if (!inviteForm.userId) {
    ElMessage.warning('请输入用户ID')
    return
  }
  inviting.value = true
  try {
    await request.post(`/api/teams/${teamId}/members/invite`, null, { params: { userId: inviteForm.userId } })
    ElMessage.success('邀请已发送')
    inviteDialogVisible.value = false
    inviteForm.userId = null
  } finally {
    inviting.value = false
  }
}

async function handleDissolve() {
  try {
    await ElMessageBox.confirm('确定要解散该球队吗？此操作不可撤销，将删除球队所有相关数据！', '解散球队', {
      confirmButtonText: '确定解散',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await request.put(`/api/teams/${teamId}/dissolve`)
    ElMessage.success('球队已解散')
    router.push('/teams')
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

onMounted(() => {
  fetchTeam()
  fetchMembers()
  fetchApplications()
})
</script>

<style scoped>
.team-manage {
  background: white;
  padding: 20px;
  border-radius: 8px;
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}
</style>
