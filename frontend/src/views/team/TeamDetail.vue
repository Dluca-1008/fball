<template>
  <div class="team-detail" v-loading="loading">
    <el-card v-if="team">
      <div class="team-header">
        <el-avatar :size="80" :src="team.logo">{{ team.name?.charAt(0) }}</el-avatar>
        <div class="team-info">
          <h2>{{ team.name }}</h2>
          <p>{{ team.city }}, {{ team.country }}</p>
          <p v-if="team.stadium">主场: {{ team.stadium }}</p>
        </div>
        <el-button v-if="isTeamAdmin" type="primary" @click="$router.push(`/teams/${team.id}/manage`)" style="margin-left: auto;">管理球队</el-button>
      </div>
      <el-divider />
      <div class="team-desc">
        <h3>球队简介</h3>
        <p>{{ team.description || '暂无简介' }}</p>
      </div>
    </el-card>

    <el-card class="members-section" v-if="team">
      <template #header>
        <div class="card-header">
          <span>球队成员</span>
          <el-button type="primary" size="small" @click="applyDialogVisible = true" v-if="!isMember">申请加入</el-button>
        </div>
      </template>
      <el-table :data="members" stripe>
        <el-table-column prop="userId" label="用户ID" width="80" />
        <el-table-column prop="role" label="角色">
          <template #default="{ row }">
            <el-tag :type="row.role === 'admin' ? 'danger' : ''">{{ row.role === 'admin' ? '管理员' : '成员' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="memberType" label="身份">
          <template #default="{ row }">
            <el-tag :type="row.memberType === 'coach' ? 'warning' : 'success'">{{ row.memberType === 'coach' ? '教练' : '球员' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="参赛状态">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '可参赛' : '不可参赛' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="joinTime" label="加入时间" />
      </el-table>
    </el-card>

    <el-dialog v-model="applyDialogVisible" title="申请加入球队" width="500px">
      <el-form :model="applyForm" label-width="80px">
        <el-form-item label="申请理由">
          <el-input v-model="applyForm.reason" type="textarea" :rows="3" placeholder="选填" />
        </el-form-item>
        <el-form-item label="身份类型">
          <el-radio-group v-model="applyForm.memberType">
            <el-radio value="player">球员</el-radio>
            <el-radio value="coach">教练</el-radio>
          </el-radio-group>
        </el-form-item>
        <template v-if="applyForm.memberType === 'player'">
          <el-form-item label="姓名">
            <el-input v-model="applyForm.playerInfo.name" placeholder="请输入姓名" />
          </el-form-item>
          <el-form-item label="位置">
            <el-select v-model="applyForm.playerInfo.position" placeholder="请选择位置" style="width: 100%;">
              <el-option label="前锋" value="前锋" />
              <el-option label="中场" value="中场" />
              <el-option label="后卫" value="后卫" />
              <el-option label="守门员" value="守门员" />
            </el-select>
          </el-form-item>
          <el-form-item label="号码">
            <el-input-number v-model="applyForm.playerInfo.number" :min="1" :max="99" />
          </el-form-item>
          <el-form-item label="国籍">
            <el-input v-model="applyForm.playerInfo.nationality" placeholder="请输入国籍" />
          </el-form-item>
        </template>
        <template v-if="applyForm.memberType === 'coach'">
          <el-form-item label="姓名">
            <el-input v-model="applyForm.coachInfo.name" placeholder="请输入姓名" />
          </el-form-item>
          <el-form-item label="职位">
            <el-select v-model="applyForm.coachInfo.roleTitle" placeholder="请选择职位" style="width: 100%;">
              <el-option label="主教练" value="主教练" />
              <el-option label="助理教练" value="助理教练" />
              <el-option label="体能教练" value="体能教练" />
              <el-option label="守门员教练" value="守门员教练" />
            </el-select>
          </el-form-item>
          <el-form-item label="国籍">
            <el-input v-model="applyForm.coachInfo.nationality" placeholder="请输入国籍" />
          </el-form-item>
          <el-form-item label="执教年限">
            <el-input-number v-model="applyForm.coachInfo.experienceYears" :min="0" :max="50" />
          </el-form-item>
        </template>
      </el-form>
      <template #footer>
        <el-button @click="applyDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="applying" @click="submitApply">提交申请</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const route = useRoute()
const userStore = useUserStore()

const loading = ref(false)
const team = ref(null)
const members = ref([])
const applyDialogVisible = ref(false)
const applying = ref(false)

const applyForm = reactive({
  reason: '',
  memberType: 'player',
  playerInfo: { name: '', position: '', number: null, nationality: '' },
  coachInfo: { name: '', roleTitle: '', nationality: '', experienceYears: null }
})

const isMember = computed(() => {
  return members.value.some(m => m.userId === userStore.userInfo?.id)
})

const isTeamAdmin = computed(() => {
  return members.value.some(m => m.userId === userStore.userInfo?.id && m.role === 'admin')
})

async function fetchTeam() {
  loading.value = true
  try {
    const res = await request.get(`/api/teams/${route.params.id}`)
    team.value = res.data
    await fetchMembers()
  } finally {
    loading.value = false
  }
}

async function fetchMembers() {
  const res = await request.get(`/api/teams/${route.params.id}/members`)
  members.value = res.data
}

async function submitApply() {
  const memberInfo = applyForm.memberType === 'player' ? applyForm.playerInfo : applyForm.coachInfo
  if (!memberInfo.name) {
    ElMessage.error('请输入您的姓名')
    return
  }
  applying.value = true
  try {
    await request.post(`/api/teams/${route.params.id}/members/apply`, {
      reason: applyForm.reason,
      memberType: applyForm.memberType,
      memberInfo
    })
    ElMessage.success('申请已提交，请等待审核')
    applyDialogVisible.value = false
  } finally {
    applying.value = false
  }
}

onMounted(fetchTeam)
</script>

<style scoped>
.team-detail {
  max-width: 800px;
  margin: 0 auto;
}

.team-header {
  display: flex;
  align-items: center;
  gap: 20px;
}

.team-info h2 {
  margin-bottom: 8px;
}

.team-info p {
  color: #666;
  margin: 4px 0;
}

.team-desc h3 {
  margin-bottom: 12px;
}

.team-desc p {
  color: #666;
  line-height: 1.6;
}

.members-section {
  margin-top: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
