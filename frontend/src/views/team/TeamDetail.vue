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
        <el-table-column label="成员" width="120">
          <template #default="{ row }">
            <span>{{ row.memberName}}</span>
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
    <el-dialog v-model="applyDialogVisible" title="申请加入球队" width="480px" class="apply-dialog">
      <el-form :model="applyForm" label-width="80px" label-position="top">
        <el-form-item label="申请理由">
          <el-input v-model="applyForm.reason" type="textarea" :rows="3" placeholder="请简要说明申请理由（选填）" />
        </el-form-item>
        <div class="apply-hint">提交后将自动关联您的球员/教练注册信息</div>
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
  memberType: 'player'
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
  applying.value = true
  try {
    await request.post(`/api/teams/${route.params.id}/members/apply`, {
      reason: applyForm.reason || null,
      memberType: applyForm.memberType,
      memberInfo: null
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
.apply-hint { font-size: 12px; color: #909399; margin-top: 4px; }
</style>
