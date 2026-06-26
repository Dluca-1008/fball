<template>
  <div class="my-invitations">
    <div class="page-header">
      <h2>我的邀请</h2>
    </div>
    <el-empty v-if="invitations.length === 0 && !loading" description="暂无邀请" />
    <el-table v-else :data="invitations" v-loading="loading" stripe>
      <el-table-column label="球队" prop="teamName" />
      <el-table-column label="邀请码" prop="inviteCode" width="120" />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="邀请时间" prop="createdAt" />
      <el-table-column label="过期时间" prop="expireTime" />
      <el-table-column label="操作" width="160">
        <template #default="{ row }">
          <template v-if="row.status === 0">
            <el-button type="success" link @click="openAcceptDialog(row)">接受</el-button>
            <el-button type="danger" link @click="handleRespond(row.id, false)">拒绝</el-button>
          </template>
          <span v-else class="text-muted">已处理</span>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="acceptDialogVisible" title="接受邀请" width="500px">
      <el-form :model="acceptForm" label-width="80px">
        <el-form-item label="身份类型">
          <el-radio-group v-model="acceptForm.memberType">
            <el-radio value="player">球员</el-radio>
            <el-radio value="coach">教练</el-radio>
          </el-radio-group>
        </el-form-item>
        <template v-if="acceptForm.memberType === 'player'">
          <el-form-item label="姓名">
            <el-input v-model="acceptForm.playerInfo.name" placeholder="请输入姓名" />
          </el-form-item>
          <el-form-item label="位置">
            <el-select v-model="acceptForm.playerInfo.position" placeholder="请选择位置" style="width: 100%;">
              <el-option label="前锋" value="前锋" />
              <el-option label="中场" value="中场" />
              <el-option label="后卫" value="后卫" />
              <el-option label="守门员" value="守门员" />
            </el-select>
          </el-form-item>
          <el-form-item label="号码">
            <el-input-number v-model="acceptForm.playerInfo.number" :min="1" :max="99" />
          </el-form-item>
          <el-form-item label="国籍">
            <el-input v-model="acceptForm.playerInfo.nationality" placeholder="请输入国籍" />
          </el-form-item>
        </template>
        <template v-if="acceptForm.memberType === 'coach'">
          <el-form-item label="姓名">
            <el-input v-model="acceptForm.coachInfo.name" placeholder="请输入姓名" />
          </el-form-item>
          <el-form-item label="职位">
            <el-select v-model="acceptForm.coachInfo.roleTitle" placeholder="请选择职位" style="width: 100%;">
              <el-option label="主教练" value="主教练" />
              <el-option label="助理教练" value="助理教练" />
              <el-option label="体能教练" value="体能教练" />
              <el-option label="守门员教练" value="守门员教练" />
            </el-select>
          </el-form-item>
          <el-form-item label="国籍">
            <el-input v-model="acceptForm.coachInfo.nationality" placeholder="请输入国籍" />
          </el-form-item>
          <el-form-item label="执教年限">
            <el-input-number v-model="acceptForm.coachInfo.experienceYears" :min="0" :max="50" />
          </el-form-item>
        </template>
      </el-form>
      <template #footer>
        <el-button @click="acceptDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="accepting" @click="confirmAccept">确认接受</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const loading = ref(false)
const invitations = ref([])
const acceptDialogVisible = ref(false)
const accepting = ref(false)
const currentInvitationId = ref(null)

const acceptForm = reactive({
  memberType: 'player',
  playerInfo: { name: '', position: '', number: null, nationality: '' },
  coachInfo: { name: '', roleTitle: '', nationality: '', experienceYears: null }
})

async function fetchInvitations() {
  loading.value = true
  try {
    const res = await request.get('/api/teams/my/invitations')
    invitations.value = res.data
  } finally {
    loading.value = false
  }
}

function openAcceptDialog(row) {
  currentInvitationId.value = row.id
  acceptForm.memberType = 'player'
  acceptForm.playerInfo = { name: '', position: '', number: null, nationality: '' }
  acceptForm.coachInfo = { name: '', roleTitle: '', nationality: '', experienceYears: null }
  acceptDialogVisible.value = true
}

async function confirmAccept() {
  const memberInfo = acceptForm.memberType === 'player' ? acceptForm.playerInfo : acceptForm.coachInfo
  if (!memberInfo.name) {
    ElMessage.error('请输入您的姓名')
    return
  }
  accepting.value = true
  try {
    await request.put(`/api/teams/invitations/${currentInvitationId.value}/respond`, {
      accept: true,
      memberType: acceptForm.memberType,
      memberInfo
    })
    ElMessage.success('已接受邀请')
    acceptDialogVisible.value = false
    fetchInvitations()
  } finally {
    accepting.value = false
  }
}

async function handleRespond(invitationId, accept) {
  const action = accept ? '接受' : '拒绝'
  await ElMessageBox.confirm(`确定${action}该邀请？`, '提示')
  await request.put(`/api/teams/invitations/${invitationId}/respond`, {
    accept,
    memberType: null,
    memberInfo: null
  })
  ElMessage.success(`已${action}`)
  fetchInvitations()
}

function getStatusType(status) {
  const types = { 0: 'warning', 1: 'success', 2: 'danger' }
  return types[status] || 'info'
}

function getStatusText(status) {
  const texts = { 0: '待处理', 1: '已接受', 2: '已拒绝' }
  return texts[status] || '未知'
}

onMounted(fetchInvitations)
</script>

<style scoped>
.my-invitations {
  background: white;
  padding: 20px;
  border-radius: 8px;
}

.page-header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.text-muted {
  color: #999;
}
</style>
