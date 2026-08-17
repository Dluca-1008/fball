<template>
  <div class="page-shell" v-loading="loading">
    <!-- 球队头部 -->
    <el-card class="team-hero" v-if="team">
      <div class="team-hero-inner">
        <div class="team-avatar-wrap">
          <el-avatar :size="80" class="team-avatar">
            {{ team.name?.charAt(0) || 'T' }}
          </el-avatar>
        </div>
        <div class="team-hero-info">
          <h1 class="team-name">{{ team.name }}</h1>
          <p class="team-location">
            <el-icon><Location /></el-icon> {{ team.city }}, {{ team.country }}
          </p>
          <p v-if="team.stadium" class="team-stadium">
            <el-icon><OfficeBuilding /></el-icon> 主场：{{ team.stadium }}
          </p>
        </div>
        <el-button
          v-if="isTeamAdmin"
          type="primary"
          class="btn-manage"
          @click="$router.push(`/app/teams/${team.id}/manage`)"
        >管理球队</el-button>
      </div>

      <div v-if="team.description" class="team-bio">
        <h3>球队简介</h3>
        <p>{{ team.description }}</p>
      </div>
    </el-card>

    <!-- 成员列表 -->
    <el-card class="members-card" v-if="team">
      <template #header>
        <div class="card-header-row">
          <span class="card-title"><span class="card-icon">👥</span>球队成员</span>
          <el-button type="primary" size="small" round @click="applyDialogVisible = true" v-if="!isMember">
            <el-icon><Plus /></el-icon> 申请加入
          </el-button>
        </div>
      </template>
      <el-table :data="members" stripe size="small">
        <el-table-column label="用户名" width="120">
          <template #default="{ row }">
            <span>{{ row.username || row.nickname || row.userId }}</span>
          </template>
        </el-table-column>
        <el-table-column label="角色" width="100">
          <template #default="{ row }">
            <el-tag :type="row.role === 'admin' ? 'danger' : ''" size="small" class="role-tag">
              {{ row.role === 'admin' ? '👑 管理员' : '🧑 成员' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="身份" width="100">
          <template #default="{ row }">
            <el-tag :type="row.memberType === 'coach' ? 'warning' : 'success'" size="small">
              {{ row.memberType === 'coach' ? '🎩 教练' : '⚽ 球员' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="参赛状态" width="110">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
              {{ row.status === 1 ? '✅ 可参赛' : '❌ 不可参赛' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="joinTime" label="加入时间" />
      </el-table>
    </el-card>

    <!-- 申请加入弹窗 -->
    <el-dialog v-model="applyDialogVisible" title="申请加入球队" width="520px" class="apply-dialog">
      <el-form :model="applyForm" label-width="80px" label-position="top">
        <el-form-item label="身份类型">
          <el-radio-group v-model="applyForm.memberType" class="type-radio-group">
            <el-radio-button value="player">⚽ 球员</el-radio-button>
            <el-radio-button value="coach">🎩 教练</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="申请理由">
          <el-input v-model="applyForm.reason" type="textarea" :rows="2" placeholder="选填" />
        </el-form-item>
        <template v-if="applyForm.memberType === 'player'">
          <el-form-item label="姓名"><el-input v-model="applyForm.playerInfo.name" placeholder="请输入姓名" /></el-form-item>
          <el-form-item label="位置">
            <el-select v-model="applyForm.playerInfo.position" placeholder="请选择" style="width:100%">
              <el-option label="前锋" value="前锋" />
              <el-option label="中场" value="中场" />
              <el-option label="后卫" value="后卫" />
              <el-option label="守门员" value="守门员" />
            </el-select>
          </el-form-item>
          <el-form-item label="号码">
            <el-input-number v-model="applyForm.playerInfo.number" :min="1" :max="99" style="width:100%" />
          </el-form-item>
          <el-form-item label="国籍"><el-input v-model="applyForm.playerInfo.nationality" placeholder="请输入国籍" /></el-form-item>
        </template>
        <template v-if="applyForm.memberType === 'coach'">
          <el-form-item label="姓名"><el-input v-model="applyForm.coachInfo.name" placeholder="请输入姓名" /></el-form-item>
          <el-form-item label="职位">
            <el-select v-model="applyForm.coachInfo.roleTitle" placeholder="请选择" style="width:100%">
              <el-option label="主教练" value="主教练" />
              <el-option label="助理教练" value="助理教练" />
              <el-option label="体能教练" value="体能教练" />
              <el-option label="守门员教练" value="守门员教练" />
            </el-select>
          </el-form-item>
          <el-form-item label="国籍"><el-input v-model="applyForm.coachInfo.nationality" placeholder="请输入国籍" /></el-form-item>
          <el-form-item label="执教年限">
            <el-input-number v-model="applyForm.coachInfo.experienceYears" :min="0" :max="50" style="width:100%" />
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
import { Location, OfficeBuilding, Plus } from '@element-plus/icons-vue'

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

const isMember = computed(() => members.value.some(m => m.userId === userStore.userInfo?.id))
const isTeamAdmin = computed(() => members.value.some(m => m.userId === userStore.userInfo?.id && m.role === 'admin'))

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
  if (!memberInfo.name) { ElMessage.error('请输入您的姓名'); return }
  applying.value = true
  try {
    await request.post(`/api/teams/${route.params.id}/members/apply`, {
      reason: applyForm.reason, memberType: applyForm.memberType, memberInfo
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
.page-shell { max-width: 900px; margin: 0 auto; display: flex; flex-direction: column; gap: 16px; }

/* ── 球队 Hero ── */
.team-hero { overflow: hidden; }
.team-hero-inner { display: flex; align-items: flex-start; gap: 24px; flex-wrap: wrap; }
.team-avatar-wrap { flex-shrink: 0; }
.team-avatar {
  background: linear-gradient(135deg, #409eff, #1d4ed8) !important;
  font-size: 32px !important;
  font-weight: 700;
  box-shadow: 0 4px 16px rgba(64, 158, 255, 0.35);
}
.team-hero-info { flex: 1; min-width: 0; }
.team-name { font-size: 26px; font-weight: 800; color: #1a202c; margin-bottom: 8px; }
.team-location, .team-stadium { font-size: 14px; color: #718096; display: flex; align-items: center; gap: 4px; margin-bottom: 4px; }

.btn-manage { border-radius: 10px; flex-shrink: 0; padding: 0 20px; }

.team-bio { margin-top: 20px; padding-top: 20px; border-top: 1px solid #f0f0f0; }
.team-bio h3 { font-size: 15px; font-weight: 600; color: #2d3748; margin-bottom: 8px; }
.team-bio p { font-size: 14px; color: #4a5568; line-height: 1.7; }

/* ── 成员卡片 ── */
.members-card { overflow: hidden; }
.card-header-row { display: flex; align-items: center; gap: 8px; }
.card-title { display: flex; align-items: center; gap: 6px; font-size: 15px; font-weight: 600; color: #2d3748; }
.card-icon { font-size: 18px; }

.role-tag { font-weight: 500; }

/* ── 申请弹窗 ── */
:deep(.apply-dialog .el-dialog__header) { padding: 20px 24px 16px; }
:deep(.apply-dialog .el-dialog__body) { padding: 20px 24px; }
:deep(.apply-dialog .el-dialog__footer) { padding: 12px 24px 20px; }
.type-radio-group { display: flex; gap: 10px; }
:deep(.el-radio-button__inner) { border-radius: 8px !important; padding: 8px 20px !important; }
</style>
