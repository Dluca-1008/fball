<template>
  <div class="page-shell">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <div class="header-icon schedule">📅</div>
        <div>
          <h1 class="header-title">比赛日程</h1>
          <p class="header-desc">查看和管理赛事日程安排</p>
        </div>
      </div>
      <div class="header-actions">
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          size="default"
          @change="fetchSchedule"
          class="date-picker"
        />
      </div>
    </div>

    <!-- 赛程时间线 -->
    <div v-loading="loading" class="schedule-timeline">
      <el-empty v-if="!loading && matches.length === 0" description="该时间段没有比赛" :image-size="80" />

      <div v-for="(group, date) in groupedMatches" :key="date" class="date-group">
        <!-- 日期标题 -->
        <div class="date-header">
          <div class="date-icon">📅</div>
          <div>
            <div class="date-text">{{ formatDate(date) }}</div>
            <div class="date-count">{{ group.length }} 场比赛</div>
          </div>
        </div>

        <!-- 比赛卡片 -->
        <div v-for="match in group" :key="match.id" class="match-card" @click="$router.push(`/app/matches/${match.id}`)">
          <div class="match-card-time">{{ formatTime(match.matchDate) }}</div>
          <div class="match-card-body">
            <div class="team home">
              <div class="team-badge">{{ (match.homeTeamName || '?').charAt(0) }}</div>
              <span class="team-name">{{ match.homeTeamName || '待定' }}</span>
            </div>
            <div class="match-vs">
              <template v-if="match.status === 1 || match.status === 2">
                <span class="score">{{ match.homeScore ?? 0 }}</span>
                <span class="sep">-</span>
                <span class="score">{{ match.awayScore ?? 0 }}</span>
              </template>
              <template v-else>
                <span class="vs-badge">VS</span>
              </template>
            </div>
            <div class="team away">
              <span class="team-name">{{ match.awayTeamName || '待定' }}</span>
              <div class="team-badge">{{ (match.awayTeamName || '?').charAt(0) }}</div>
            </div>
          </div>
          <div class="match-card-footer">
            <el-tag :type="match.matchType === 'cup' ? 'danger' : 'success'" size="small" class="type-tag">
              {{ match.matchType === 'cup' ? '杯赛' : '联赛' }}
            </el-tag>
            <el-tag :type="getStatusType(match.status)" size="small" class="status-tag">
              {{ getStatusText(match.status) }}
            </el-tag>
            <span class="venue">🏟️ {{ match.venue || '-' }}</span>
            <div class="quick-actions" @click.stop>
              <el-button v-if="match.status === 0 && userStore.hasPermission('match:edit')" type="success" size="small" round @click="startMatch(match)">开始</el-button>
              <el-button v-if="match.status === 1 && userStore.hasPermission('match:edit')" type="primary" size="small" round @click="openScoreDialog(match)">比分</el-button>
              <el-button v-if="match.status === 2" type="info" size="small" round @click="$router.push(`/app/matches/${match.id}`)">查看</el-button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 比分弹窗 -->
    <el-dialog v-model="scoreDialogVisible" title="录入比分" width="420px" class="score-dialog">
      <div v-if="currentMatch" class="score-dialog-teams">
        <span class="team-name">{{ currentMatch.homeTeamName }}</span>
        <span class="vs-text">VS</span>
        <span class="team-name">{{ currentMatch.awayTeamName }}</span>
      </div>
      <el-form label-width="70px" style="margin-top: 20px;">
        <el-divider content-position="left">全场比分</el-divider>
        <div class="score-inputs">
          <el-input-number v-model="scoreForm.homeScore" :min="0" :max="99" class="score-input" />
          <span class="score-sep">:</span>
          <el-input-number v-model="scoreForm.awayScore" :min="0" :max="99" class="score-input" />
        </div>
        <el-divider content-position="left">半场比分</el-divider>
        <div class="score-inputs">
          <el-input-number v-model="scoreForm.homeScoreHalf" :min="0" :max="99" class="score-input" />
          <span class="score-sep">:</span>
          <el-input-number v-model="scoreForm.awayScoreHalf" :min="0" :max="99" class="score-input" />
        </div>
      </el-form>
      <template #footer>
        <el-button @click="scoreDialogVisible = false">取消</el-button>
        <el-button type="warning" @click="endMatch">结束比赛</el-button>
        <el-button type="primary" :loading="savingScore" @click="saveScore">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'
import request from '@/utils/request'

const userStore = useUserStore()
const loading = ref(false)
const matches = ref([])
const dateRange = ref(null)
const scoreDialogVisible = ref(false)
const savingScore = ref(false)
const currentMatch = ref(null)

const scoreForm = ref({ homeScore: 0, awayScore: 0, homeScoreHalf: 0, awayScoreHalf: 0 })

const groupedMatches = computed(() => {
  const groups = {}
  matches.value.forEach(match => {
    // 用 ISO 日期安全提取日期部分
    const dateStr = match.matchDate
    const date = dateStr ? dateStr.substring(0, 10) : '未知日期'
    if (!groups[date]) groups[date] = []
    groups[date].push(match)
  })
  return groups
})

async function fetchSchedule() {
  loading.value = true
  try {
    let start, end
    if (dateRange.value && dateRange.value.length === 2) {
      start = formatDateStr(dateRange.value[0]) + ' 00:00:00'
      end = formatDateStr(dateRange.value[1]) + ' 23:59:59'
    } else {
      const now = new Date()
      const monthStart = new Date(now.getFullYear(), now.getMonth(), 1)
      const monthEnd = new Date(now.getFullYear(), now.getMonth() + 1, 0, 23, 59, 59)
      start = formatDateStr(monthStart) + ' 00:00:00'
      end = formatDateStr(monthEnd) + ' 23:59:59'
    }
    const res = await request.get('/api/matches/schedule', { params: { start, end } })
    // 过滤掉赛事容器记录（leagueId=null 且无球队，即 id=39/32 这类），只展示实际比赛
    // 注意：友谊赛也 leagueId=null，但有 homeTeamId/awayTeamId，不会被误过滤
    matches.value = (res.data || []).filter(m => m.leagueId != null || (m.homeTeamId != null && m.awayTeamId != null))
  } finally {
    loading.value = false
  }
}

async function startMatch(match) {
  await ElMessageBox.confirm(`确定开始 ${match.homeTeamName} vs ${match.awayTeamName}？`)
  await request.put(`/api/matches/${match.id}`, { status: 1 })
  ElMessage.success('比赛已开始')
  fetchSchedule()
}

function openScoreDialog(match) {
  currentMatch.value = match
  scoreForm.value = {
    homeScore: match.homeScore || 0,
    awayScore: match.awayScore || 0,
    homeScoreHalf: match.homeScoreHalf || 0,
    awayScoreHalf: match.awayScoreHalf || 0
  }
  scoreDialogVisible.value = true
}

async function saveScore() {
  savingScore.value = true
  try {
    await request.put(`/api/matches/${currentMatch.value.id}`, {
      homeScore: scoreForm.value.homeScore,
      awayScore: scoreForm.value.awayScore,
      homeScoreHalf: scoreForm.value.homeScoreHalf,
      awayScoreHalf: scoreForm.value.awayScoreHalf,
      status: currentMatch.value.status
    })
    ElMessage.success('比分已保存')
    scoreDialogVisible.value = false
    fetchSchedule()
  } finally {
    savingScore.value = false
  }
}

async function endMatch() {
  await ElMessageBox.confirm('确定结束比赛？')
  await request.put(`/api/matches/${currentMatch.value.id}`, {
    homeScore: scoreForm.value.homeScore,
    awayScore: scoreForm.value.awayScore,
    homeScoreHalf: scoreForm.value.homeScoreHalf,
    awayScoreHalf: scoreForm.value.awayScoreHalf,
    status: 2
  })
  ElMessage.success('比赛已结束')
  scoreDialogVisible.value = false
  fetchSchedule()
}

function formatDateStr(date) {
  const y = date.getFullYear()
  const m = String(date.getMonth() + 1).padStart(2, '0')
  const d = String(date.getDate()).padStart(2, '0')
  return `${y}-${m}-${d}`
}

function formatDate(dateStr) {
  if (!dateStr) return ''
  // 直接解析 ISO 字符串，避免重复拼接时间部分导致 NaN
  const d = new Date(dateStr)
  if (isNaN(d.getTime())) return ''
  const weekdays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
  return `${d.getMonth() + 1}月${d.getDate()}日 ${weekdays[d.getDay()]}`
}

function formatTime(dateStr) {
  if (!dateStr) return ''
  // 兼容 ISO 格式（无空格）和数据库格式（有空格）
  const parts = dateStr.split(/[ T]/)
  return parts[1] || parts[0] || ''
}

function getStatusType(s) { return { 0: 'info', 1: 'success', 2: 'warning' }[s] || 'info' }
function getStatusText(s) { return { 0: '未开始', 1: '进行中', 2: '已结束' }[s] || '未知' }

onMounted(fetchSchedule)
</script>

<style scoped>
.page-shell { max-width: 900px; margin: 0 auto; }

.page-header {
  display: flex; align-items: center; justify-content: space-between;
  margin-bottom: 24px; flex-wrap: wrap; gap: 12px;
}
.header-left { display: flex; align-items: center; gap: 14px; }
.header-icon {
  width: 48px; height: 48px;
  background: linear-gradient(135deg, #409eff, #1d4ed8);
  border-radius: 12px; display: flex; align-items: center; justify-content: center;
  font-size: 24px; box-shadow: 0 4px 12px rgba(64,158,255,0.3);
}
.header-title { font-size: 21px; font-weight: 700; color: #1a202c; margin-bottom: 2px; }
.header-desc { font-size: 13px; color: #718096; }
.header-actions { display: flex; gap: 10px; }
.date-picker { border-radius: 10px; }

/* ── 赛程时间线 ── */
.schedule-timeline { display: flex; flex-direction: column; gap: 24px; }

.date-group { }

.date-header {
  display: flex; align-items: center; gap: 12px;
  padding: 10px 0;
  border-bottom: 2px solid #e2e8f0;
  margin-bottom: 14px;
}
.date-icon { font-size: 20px; }
.date-text { font-size: 16px; font-weight: 700; color: #1a202c; }
.date-count { font-size: 12px; color: #a0aec0; }

/* ── 比赛卡片 ── */
.match-card {
  background: #fff;
  border-radius: 14px;
  padding: 16px 20px;
  border: 1px solid rgba(0,0,0,0.05);
  transition: transform 0.2s, box-shadow 0.2s, border-color 0.2s;
  cursor: pointer;
}
.match-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0,0,0,0.08);
  border-color: rgba(64,158,255,0.3);
}

.match-card-time { font-size: 13px; color: #a0aec0; margin-bottom: 10px; }

.match-card-body {
  display: flex; align-items: center; justify-content: space-between;
  padding: 8px 0;
}
.team { display: flex; align-items: center; gap: 10px; flex: 1; min-width: 0; }
.team.home { justify-content: flex-end; text-align: right; }
.team.away { justify-content: flex-start; text-align: left; }
.team-badge {
  width: 36px; height: 36px;
  background: linear-gradient(135deg, #f0f4ff, #dce6ff);
  border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  font-weight: 700; font-size: 15px; color: #409eff; flex-shrink: 0;
}
.team-name { font-size: 14px; font-weight: 600; color: #1a202c; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }

.match-vs { display: flex; align-items: center; gap: 6px; padding: 0 16px; flex-shrink: 0; }
.score { font-size: 22px; font-weight: 800; color: #1d4ed8; }
.sep { font-size: 18px; color: #cbd5e1; }
.vs-badge {
  font-size: 14px; font-weight: 700; color: #a0aec0;
  background: #f7fafc; padding: 4px 12px; border-radius: 6px;
}

.match-card-footer {
  display: flex; align-items: center; gap: 8px;
  margin-top: 12px; padding-top: 10px;
  border-top: 1px solid #f7fafc;
  flex-wrap: wrap;
}
.type-tag, .status-tag { font-weight: 500; }
.venue { font-size: 12px; color: #a0aec0; margin-left: 4px; }
.quick-actions { margin-left: auto; display: flex; gap: 6px; }

/* ── 比分弹窗 ── */
:deep(.score-dialog .el-dialog__header) { padding: 20px 24px 16px; }
:deep(.score-dialog .el-dialog__body) { padding: 20px 24px; }
:deep(.score-dialog .el-dialog__footer) { padding: 12px 24px 20px; }

.score-dialog-teams {
  display: flex; align-items: center; justify-content: center;
  gap: 16px; font-size: 17px; font-weight: 600; color: #1a202c;
}
.vs-text { color: #a0aec0; font-size: 14px; }
.score-inputs {
  display: flex; align-items: center; justify-content: center;
  gap: 12px; padding: 8px 0;
}
.score-input { width: 100px; }
.score-sep { font-size: 24px; font-weight: 700; color: #cbd5e1; }
</style>
