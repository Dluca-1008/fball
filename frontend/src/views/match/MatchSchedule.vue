<template>
  <div class="match-schedule">
    <div class="page-header">
      <h2>比赛日程</h2>
      <div class="header-actions">
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          @change="fetchSchedule"
        />
      </div>
    </div>

    <div v-loading="loading">
      <el-empty v-if="matches.length === 0" description="该时间段没有比赛" />

      <div v-else class="schedule-timeline">
        <div v-for="(group, date) in groupedMatches" :key="date" class="date-group">
          <div class="date-header">
            <el-icon><Calendar /></el-icon>
            <span>{{ formatDate(date) }}</span>
            <el-tag size="small">{{ group.length }}场比赛</el-tag>
          </div>

          <div v-for="match in group" :key="match.id" class="match-card">
            <div class="match-time">{{ formatTime(match.matchDate) }}</div>
            <div class="match-content" @click="$router.push(`/matches/${match.id}`)">
              <div class="team home">
                <span class="team-name">{{ match.homeTeamName || '待定' }}</span>
              </div>
              <div class="score">
                <template v-if="match.status === 1 || match.status === 2">
                  <span class="score-num">{{ match.homeScore }}</span>
                  <span class="score-sep">-</span>
                  <span class="score-num">{{ match.awayScore }}</span>
                </template>
                <template v-else>
                  <span class="vs">VS</span>
                </template>
              </div>
              <div class="team away">
                <span class="team-name">{{ match.awayTeamName || '待定' }}</span>
              </div>
            </div>
            <div class="match-meta">
              <el-tag :type="match.matchType === 'cup' ? 'danger' : 'success'" size="small">
                {{ match.matchType === 'cup' ? '杯赛' : '联赛' }}
              </el-tag>
              <el-tag :type="getStatusType(match.status)" size="small">{{ getStatusText(match.status) }}</el-tag>
              <span class="venue">{{ match.venue }}</span>
              <div class="quick-actions" v-if="userStore.hasPermission('match:edit')">
                <el-button v-if="match.status === 0" type="success" size="small" @click.stop="startMatch(match)">开始</el-button>
                <el-button v-if="match.status === 1" type="primary" size="small" @click.stop="openScoreDialog(match)">录入比分</el-button>
                <el-button v-if="match.status === 2" type="info" size="small" @click.stop="viewMatch(match)">查看</el-button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <el-dialog v-model="scoreDialogVisible" title="录入比分" width="400px">
      <div v-if="currentMatch" class="score-dialog">
        <div class="match-teams">
          <span class="team-name">{{ currentMatch.homeTeamName }}</span>
          <span class="vs">VS</span>
          <span class="team-name">{{ currentMatch.awayTeamName }}</span>
        </div>

        <el-form label-width="60px" style="margin-top: 20px;">
          <el-divider content-position="left">全场比分</el-divider>
          <div class="score-inputs">
            <el-input-number v-model="scoreForm.homeScore" :min="0" :max="99" />
            <span class="score-sep">:</span>
            <el-input-number v-model="scoreForm.awayScore" :min="0" :max="99" />
          </div>

          <el-divider content-position="left">半场比分</el-divider>
          <div class="score-inputs">
            <el-input-number v-model="scoreForm.homeScoreHalf" :min="0" :max="99" />
            <span class="score-sep">:</span>
            <el-input-number v-model="scoreForm.awayScoreHalf" :min="0" :max="99" />
          </div>
        </el-form>
      </div>
      <template #footer>
        <el-button @click="scoreDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="savingScore" @click="saveScore">保存</el-button>
        <el-button type="warning" @click="endMatch">结束比赛</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { Calendar } from '@element-plus/icons-vue'
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

const scoreForm = ref({
  homeScore: 0,
  awayScore: 0,
  homeScoreHalf: 0,
  awayScoreHalf: 0
})

const groupedMatches = computed(() => {
  const groups = {}
  matches.value.forEach(match => {
    const date = match.matchDate ? match.matchDate.split(' ')[0] : '未知日期'
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
    matches.value = res.data
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

function viewMatch(match) {
  window.open(`/matches/${match.id}`, '_blank')
}

function formatDateStr(date) {
  const y = date.getFullYear()
  const m = String(date.getMonth() + 1).padStart(2, '0')
  const d = String(date.getDate()).padStart(2, '0')
  return `${y}-${m}-${d}`
}

function formatDate(dateStr) {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  const weekdays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
  return `${dateStr} ${weekdays[d.getDay()]}`
}

function formatTime(dateStr) {
  if (!dateStr) return ''
  return dateStr.split(' ')[1] || ''
}

function getStatusType(status) {
  const types = { 0: 'info', 1: 'success', 2: 'warning' }
  return types[status] || 'info'
}

function getStatusText(status) {
  const texts = { 0: '未开始', 1: '进行中', 2: '已结束' }
  return texts[status] || '未知'
}

onMounted(fetchSchedule)
</script>

<style scoped>
.match-schedule {
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

.header-actions {
  display: flex;
  gap: 12px;
}

.date-group {
  margin-bottom: 24px;
}

.date-header {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 0;
  border-bottom: 2px solid #409eff;
  margin-bottom: 12px;
  font-size: 16px;
  font-weight: 500;
}

.match-card {
  padding: 16px;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  margin-bottom: 12px;
  transition: all 0.3s;
}

.match-card:hover {
  border-color: #409eff;
  box-shadow: 0 2px 12px rgba(64, 158, 255, 0.1);
}

.match-time {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
}

.match-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 0;
  cursor: pointer;
}

.team {
  flex: 1;
}

.team.home {
  text-align: right;
  padding-right: 20px;
}

.team.away {
  text-align: left;
  padding-left: 20px;
}

.team-name {
  font-size: 18px;
  font-weight: 500;
}

.score {
  display: flex;
  align-items: center;
  gap: 8px;
  min-width: 100px;
  justify-content: center;
}

.score-num {
  font-size: 24px;
  font-weight: bold;
  color: #409eff;
}

.score-sep {
  font-size: 20px;
  color: #c0c4cc;
}

.vs {
  font-size: 18px;
  font-weight: bold;
  color: #c0c4cc;
}

.match-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 8px;
}

.venue {
  font-size: 13px;
  color: #909399;
}

.quick-actions {
  margin-left: auto;
}

.score-dialog .match-teams {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 20px;
  font-size: 18px;
  font-weight: 500;
}

.score-dialog .score-inputs {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16px;
  padding: 12px 0;
}

.score-dialog .score-sep {
  font-size: 24px;
  font-weight: bold;
  color: #c0c4cc;
}
</style>
