<template>
  <div class="page-shell" v-loading="loading">
    <!-- 赛事信息卡片 -->
    <el-card class="tournament-card" v-if="match">
      <div class="tournament-top">
        <div class="tournament-info">
          <div class="tournament-badge">{{ match.matchType === 'cup' ? '🏅 杯赛' : '⚽ 联赛' }}</div>
          <h1 class="tournament-name">{{ match.name || '未命名赛事' }}</h1>
          <div class="tournament-meta-row">
            <span class="meta-item"><el-icon><Clock /></el-icon> {{ formatDate(match.matchDate) }}</span>
            <span class="meta-item"><el-icon><Location /></el-icon> {{ match.venue || '待定' }}</span>
            <el-tag :type="getStatusType(match.status)" size="small">
              {{ getStatusText(match.status) }}
            </el-tag>
          </div>
        </div>
        <el-button
          v-if="userStore.hasPermission('match:edit') || userStore.hasRole('organizer')"
          type="primary"
          @click="$router.push(`/app/matches/${match.id}/manage`)"
        >
          <el-icon><Setting /></el-icon> 管理
        </el-button>
      </div>
    </el-card>

    <!-- 赛事比赛列表 -->
    <div class="section-title" v-if="matches.length > 0">
      <span>比赛列表（{{ matches.length }} 场）</span>
    </div>
    <div class="match-list" v-loading="matchesLoading">
      <el-empty v-if="!matchesLoading && matches.length === 0" description="暂无比赛" />
      <div
        v-for="m in matches"
        :key="m.id"
        class="match-item"
        @click="$router.push(`/app/matches/${m.id}`)"
      >
        <div class="match-item-status">
          <el-tag :type="getStatusType(m.status)" size="small">
            {{ getStatusText(m.status) }}
          </el-tag>
        </div>
        <div class="match-item-teams">
          <div class="team home">
            <span class="team-name">{{ m.homeTeamName || '待定' }}</span>
          </div>
          <div class="match-score">
            <template v-if="m.status === 1 || m.status === 2">
              <span class="score">{{ m.homeScore ?? 0 }}</span>
              <span class="sep">:</span>
              <span class="score">{{ m.awayScore ?? 0 }}</span>
            </template>
            <template v-else>
              <span class="vs">VS</span>
            </template>
          </div>
          <div class="team away">
            <span class="team-name">{{ m.awayTeamName || '待定' }}</span>
          </div>
        </div>
        <div class="match-item-info">
          <span class="info-item"><el-icon><Clock /></el-icon> {{ formatTime(m.matchDate) }}</span>
          <span class="info-item"><el-icon><Location /></el-icon> {{ m.venue || '-' }}</span>
          <el-button
            size="small"
            type="primary"
            link
            @click.stop="goMatchStats(m.id)"
          >
            查看统计
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'
import { Clock, Location, Setting } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const matchesLoading = ref(false)
const match = ref(null)
const matches = ref([])

function formatDate(dateStr) {
  if (!dateStr) return '-'
  const d = new Date(dateStr)
  return d.toLocaleDateString('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit' })
}

function formatTime(dateStr) {
  if (!dateStr) return '-'
  const d = new Date(dateStr)
  return d.toLocaleString('zh-CN', { month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' })
}

function getStatusType(s) { return { 0: 'info', 1: 'success', 2: 'warning' }[s] || 'info' }
function getStatusText(s) { return { 0: '未开始', 1: '进行中', 2: '已结束' }[s] || '未知' }

function goMatchStats(matchId) {
  router.push(`/app/matches/${matchId}/stats`).catch(err => console.error('[MatchDetail] goMatchStats failed:', err))
}

async function fetchMatch() {
  loading.value = true
  try {
    const res = await request.get(`/api/matches/${route.params.id}`)
    match.value = res.data
  } catch (e) {
    ElMessage.error('赛事加载失败')
    console.error('fetchMatch error:', e)
  } finally {
    loading.value = false
  }
}

async function fetchMatches() {
  matchesLoading.value = true
  try {
    // 获取同一赛事(leagueId)下的所有比赛
    const res = await request.get('/api/matches', { params: { page: 1, size: 1000 } })
    const allMatches = res.data.records || res.data
    if (match.value?.leagueId) {
      matches.value = allMatches.filter(m => m.leagueId === match.value.leagueId)
    } else {
      matches.value = allMatches
    }
  } catch (e) {
    console.error('fetchMatches error:', e)
  } finally {
    matchesLoading.value = false
  }
}

onMounted(async () => {
  await fetchMatch()
  await fetchMatches()
})
</script>

<style scoped>
.page-shell { max-width: 900px; margin: 0 auto; display: flex; flex-direction: column; gap: 16px; }

.tournament-card { overflow: hidden; }
.tournament-top {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
}
.tournament-info { flex: 1; }
.tournament-badge {
  display: inline-block;
  font-size: 12px;
  color: #e6a23c;
  font-weight: 600;
  margin-bottom: 6px;
}
.tournament-name {
  font-size: 22px;
  font-weight: 700;
  color: #1a202c;
  margin: 0 0 10px;
}
.tournament-meta-row {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
}
.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #718096;
}

/* ── 比赛列表 ── */
.section-title {
  font-size: 15px;
  font-weight: 700;
  color: #1a202c;
  margin: 8px 0;
}

.match-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
  background: #fff;
  border-radius: 14px;
  padding: 16px;
}

.match-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 14px;
  border-radius: 10px;
  cursor: pointer;
  transition: background 0.2s;
}
.match-item:hover { background: #f8fafc; }

.match-item-status { flex-shrink: 0; }
.match-item-teams {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 10px;
  min-width: 0;
}
.team { flex: 1; min-width: 0; }
.team.home { text-align: right; }
.team.away { text-align: left; }
.team-name {
  font-size: 14px;
  font-weight: 600;
  color: #1a202c;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.match-score {
  display: flex;
  align-items: center;
  gap: 6px;
  flex-shrink: 0;
  padding: 0 8px;
}
.score { font-size: 20px; font-weight: 800; color: #1d4ed8; }
.sep { font-size: 16px; color: #cbd5e1; }
.vs { font-size: 13px; font-weight: 600; color: #a0aec0; letter-spacing: 1px; }

.match-item-info {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-shrink: 0;
}
.info-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #a0aec0;
  white-space: nowrap;
}
</style>
