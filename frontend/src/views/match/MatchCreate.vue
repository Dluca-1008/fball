<template>
  <div class="page-shell" v-loading="loading">
    <div class="page-header">
      <div class="header-left">
        <div class="header-icon">🏆</div>
        <div>
          <h1 class="header-title">创建赛事</h1>
          <p class="header-desc">填写赛事信息，系统将自动生成赛程</p>
        </div>
      </div>
      <el-button @click="$router.push('/app/matches')">
        <el-icon><ArrowLeft /></el-icon> 返回
      </el-button>
    </div>

    <el-card class="form-card">
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="120px"
        label-position="right"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="赛事名称" prop="name">
              <el-input v-model="form.name" placeholder="如：2025年春季联赛" maxlength="100" show-word-limit />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="赛事类型" prop="matchType">
              <el-radio-group v-model="form.matchType">
                <el-radio value="league">联赛（主客场双循环）</el-radio>
                <el-radio value="cup">杯赛（分组淘汰）</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="开始日期" prop="matchDate">
              <el-date-picker
                v-model="form.matchDate"
                type="datetime"
                placeholder="选择赛事开始时间"
                style="width: 100%"
                format="YYYY-MM-DD HH:mm"
                value-format="YYYY-MM-DD HH:mm:ss"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="比赛场地" prop="venue">
              <el-input v-model="form.venue" placeholder="如：市体育中心体育场" maxlength="200" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="参赛队伍" prop="teamIds">
          <div class="team-selector">
            <el-select
              v-model="form.teamIds"
              multiple
              filterable
              placeholder="搜索并选择参赛队伍（至少2支）"
              style="width: 100%"
              :loading="teamsLoading"
            >
              <el-option
                v-for="team in teams"
                :key="team.id"
                :label="team.name"
                :value="team.id"
              />
            </el-select>
            <div class="team-hint">已选 {{ form.teamIds.length }} 支队伍</div>
          </div>
        </el-form-item>

        <el-form-item label="间隔天数" prop="intervalDays">
          <el-input-number v-model="form.intervalDays" :min="1" :max="30" placeholder="轮次间隔" />
          <span class="form-hint">每轮比赛间隔天数</span>
        </el-form-item>

        <el-divider />

        <el-form-item>
          <el-button type="primary" :loading="submitting" @click="submitForm" :disabled="form.teamIds.length < 2">
            <el-icon><Check /></el-icon> 创建并生成赛程
          </el-button>
          <el-button @click="$router.push('/app/matches')">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 赛程预览 -->
    <el-card v-if="generatedCount > 0" class="schedule-preview">
      <template #header>
        <div class="card-title-bar">
          <span>✅ 赛程已生成（{{ generatedCount }} 场）</span>
          <el-button size="small" type="primary" @click="$router.push(`/app/matches/${tournamentId}`)">
            查看详情
          </el-button>
        </div>
      </template>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'
import { ArrowLeft, Check } from '@element-plus/icons-vue'

const router = useRouter()
const loading = ref(false)
const submitting = ref(false)
const teamsLoading = ref(false)
const teams = ref([])
const generatedCount = ref(0)
const tournamentId = ref(null)

const formRef = ref(null)
const form = ref({
  name: '',
  matchType: 'league',
  matchDate: '',
  venue: '',
  teamIds: [],
  intervalDays: 7
})

const rules = {
  name: [{ required: true, message: '请输入赛事名称', trigger: 'blur' }],
  matchType: [{ required: true, message: '请选择赛事类型', trigger: 'change' }],
  matchDate: [{ required: true, message: '请选择开始日期', trigger: 'change' }],
  venue: [{ required: true, message: '请输入比赛场地', trigger: 'blur' }],
  teamIds: [{ required: true, message: '请选择参赛队伍（至少2支）', trigger: 'change' }]
}

async function fetchTeams() {
  teamsLoading.value = true
  try {
    const res = await request.get('/api/teams/list')
    teams.value = res.data || []
  } finally {
    teamsLoading.value = false
  }
}

async function submitForm() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  if (form.value.teamIds.length < 2) {
    ElMessage.warning('至少需要选择2支队伍')
    return
  }

  submitting.value = true
  try {
    // 1. 创建赛事
    const createRes = await request.post('/api/matches', {
      name: form.value.name,
      matchType: form.value.matchType,
      matchDate: form.value.matchDate,
      venue: form.value.venue
    })
    tournamentId.value = createRes.data.id
    ElMessage.success('赛事创建成功')

    // 2. 生成赛程
    const generateRes = await request.post(
      `/api/matches/${tournamentId.value}/generate-schedule`,
      {
        teamIds: form.value.teamIds,
        startDate: form.value.matchDate,
        intervalDays: form.value.intervalDays,
        matchType: form.value.matchType
      }
    )
    generatedCount.value = generateRes.data
    ElMessage.success(`已生成 ${generateRes.data} 场比赛`)
    // 生成成功后跳转赛程生成页，携带 matchId
    setTimeout(() => {
      router.push({ path: '/app/matches/generate', query: { matchId: tournamentId.value } })
    }, 1500)
  } finally {
    submitting.value = false
  }
}

onMounted(fetchTeams)
</script>

<style scoped>
.page-shell { max-width: 800px; margin: 0 auto; }

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24px;
  gap: 16px;
}
.header-left { display: flex; align-items: center; gap: 16px; }
.header-icon {
  width: 52px; height: 52px;
  background: linear-gradient(135deg, #e6a23c, #c45d0e);
  border-radius: 14px;
  display: flex; align-items: center; justify-content: center;
  font-size: 26px;
  box-shadow: 0 4px 14px rgba(230, 162, 60, 0.35);
}
.header-title { font-size: 22px; font-weight: 700; color: #1a202c; margin-bottom: 2px; }
.header-desc { font-size: 13px; color: #718096; }

.form-card { overflow: hidden; }
.form-hint { margin-left: 12px; font-size: 12px; color: #a0aec0; }

.team-selector { width: 100%; }
.team-hint { font-size: 12px; color: #718096; margin-top: 6px; }

.schedule-preview { overflow: hidden; margin-top: 16px; }
.card-title-bar {
  display: flex; align-items: center; justify-content: space-between;
  font-size: 15px; font-weight: 600; color: #2d3748;
}
</style>
