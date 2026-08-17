<template>
  <div class="page-shell" v-loading="loading">
    <div class="page-header">
      <div class="header-left">
        <div class="header-icon">📊</div>
        <div>
          <h1 class="header-title">比赛统计</h1>
          <p class="header-desc">{{ match?.name ? match.name + ' · ' : '' }}第 {{ match ? matchIndex + 1 : '' }} 场</p>
        </div>
      </div>
      <el-button @click="$router.back()">返回</el-button>
    </div>

    <!-- 对阵双方比分 -->
    <template v-if="match">
    <el-row :gutter="16">
      <el-col :span="24">
        <el-card class="score-card">
          <div class="score-display">
            <div class="team-block home">
              <div class="team-name">{{ homeTeam || '主队' }}</div>
              <div class="score-number">{{ match.homeScore ?? 0 }}</div>
            </div>
            <div class="score-divider">:</div>
            <div class="team-block away">
              <div class="team-name">{{ awayTeam || '客队' }}</div>
              <div class="score-number">{{ match.awayScore ?? 0 }}</div>
            </div>
          </div>
          <div class="match-meta">
            <span><el-icon><Clock /></el-icon> {{ formatTime(match.matchDate) }}</span>
            <span><el-icon><Location /></el-icon> {{ match.venue || '-' }}</span>
            <el-tag :type="getStatusType(match.status)">{{ getStatusText(match.status) }}</el-tag>
          </div>
        </el-card>
      </el-col>
    </el-row>
    </template>
    <el-empty v-else-if="!loading" description="比赛数据加载失败" :image-size="80" />

    <!-- 红黄牌 -->
    <el-row :gutter="16" style="margin-top:16px">
      <el-col :span="24">
        <el-card class="card">
          <template #header>
            <div class="card-title-bar">
              <span class="card-icon">🟨</span>
              <span>红黄牌记录</span>
            </div>
          </template>
          <div v-loading="cardsLoading" class="cards-list">
            <div v-if="cards.length === 0" class="empty-hint">暂无红黄牌记录</div>
            <div v-for="card in cards" :key="card.id" class="card-row">
              <span class="card-minute">{{ card.minute ? card.minute + "'" : '-' }}</span>
              <span class="card-team">{{ card.teamName || ('球队 #' + card.teamId) }}</span>
              <span class="card-player">{{ card.playerName || '-' }}</span>
              <el-tag :type="card.cardType === 1 ? 'warning' : 'danger'" size="small">
                {{ card.cardType === 1 ? '🟨 黄牌' : '🟥 红牌' }}
              </el-tag>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'
import { Clock, Location } from '@element-plus/icons-vue'

const route = useRoute()
const matchId = route.params.id

const loading = ref(false)
const loadError = ref(false)
const cardsLoading = ref(false)
const match = ref(null)
const homeTeam = ref('')
const awayTeam = ref('')
const cards = ref([])
const matchIndex = ref(0)

function formatTime(dateStr) {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleString('zh-CN', { month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' })
}

function getStatusType(s) { return { 0: 'info', 1: 'success', 2: 'warning' }[s] || 'info' }
function getStatusText(s) { return { 0: '未开始', 1: '进行中', 2: '已结束' }[s] || '未知' }

async function fetchMatch() {
  loading.value = true
  loadError.value = false
  try {
    const res = await request.get(`/api/matches/${matchId}`)
    match.value = res.data
    homeTeam.value = res.data.homeTeamName || ''
    awayTeam.value = res.data.awayTeamName || ''
  } catch {
    loadError.value = true
    ElMessage.error('比赛数据加载失败')
  } finally {
    loading.value = false
  }
}

async function fetchMatchIndex() {
  try {
    const res = await request.get('/api/matches/tournaments/ongoing')
    const list = res.data || []
    const idx = list.findIndex(m => m.id === parseInt(matchId))
    matchIndex.value = idx >= 0 ? idx : 0
  } catch { /* ignore */ }
}

async function fetchCards() {
  cardsLoading.value = true
  try {
    const res = await request.get(`/api/matches/${matchId}/cards`)
    cards.value = res.data || []
  } catch { /* ignore */ } finally {
    cardsLoading.value = false
  }
}

onMounted(() => {
  fetchMatch()
  fetchCards()
  fetchMatchIndex()
})
</script>

<style scoped>
.page-shell { max-width: 800px; margin: 0 auto; }
.page-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 20px; }
.header-left { display: flex; align-items: center; gap: 14px; }
.header-icon {
  width: 48px; height: 48px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border-radius: 12px; display: flex; align-items: center; justify-content: center;
  font-size: 24px;
}
.header-title { font-size: 20px; font-weight: 700; color: #1a202c; margin: 0; }
.header-desc { font-size: 13px; color: #718096; margin: 2px 0 0; }

.score-card { overflow: hidden; }
.score-display {
  display: flex; align-items: center; justify-content: center; gap: 24px; padding: 20px 0 12px;
}
.team-block { text-align: center; min-width: 120px; }
.team-name {
  font-size: 14px; color: #4a5568; font-weight: 600;
  white-space: nowrap; overflow: hidden; text-overflow: ellipsis;
}
.score-number { font-size: 42px; font-weight: 800; color: #1d4ed8; line-height: 1; }
.score-divider { font-size: 32px; color: #cbd5e1; font-weight: 700; }
.match-meta {
  display: flex; align-items: center; gap: 16px;
  font-size: 13px; color: #718096; padding-top: 12px;
  border-top: 1px solid #f0f2f5;
}
.match-meta > span { display: flex; align-items: center; gap: 4px; }

.card { overflow: hidden; }
.card-title-bar { display: flex; align-items: center; gap: 8px; font-size: 15px; font-weight: 600; color: #2d3748; }
.card-icon { font-size: 18px; }

.cards-list { display: flex; flex-direction: column; gap: 10px; }
.empty-hint { color: #a0aec0; font-size: 14px; text-align: center; padding: 20px 0; }
.card-row {
  display: flex; align-items: center; gap: 12px;
  padding: 8px 12px; background: #f8fafc; border-radius: 8px; font-size: 13px;
}
.card-minute { color: #718096; width: 36px; flex-shrink: 0; }
.card-team { flex: 1; color: #2d3748; font-weight: 500; min-width: 0; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.card-player { width: 80px; color: #718096; text-align: center; flex-shrink: 0; }
</style>
