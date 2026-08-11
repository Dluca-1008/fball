<template>
  <div class="page-shell" v-loading="loading">
    <!-- 赛事详情卡片 -->
    <el-card class="match-hero" v-if="match">
      <div class="match-hero-top">
        <el-tag :type="match.matchType === 'cup' ? 'danger' : 'success'" class="type-tag">
          {{ match.matchType === 'cup' ? '🏅 杯赛' : '⚽ 联赛' }}
        </el-tag>
        <el-tag :type="getStatusType(match.status)" class="status-tag">
          {{ getStatusText(match.status) }}
        </el-tag>
        <el-button
          v-if="userStore.hasPermission('match:edit')"
          type="primary"
          size="small"
          class="btn-manage"
          @click="$router.push(`/matches/${match.id}/manage`)"
        >管理比赛</el-button>
      </div>

      <!-- 对阵横幅 -->
      <div class="match-vs-banner">
        <div class="team-block home">
          <div class="team-badge">{{ match.homeTeamName?.charAt(0) || '?' }}</div>
          <span class="team-name">{{ match.homeTeamName || '待定' }}</span>
        </div>
        <div class="score-display">
          <span class="score home-score">{{ match.homeScore ?? 0 }}</span>
          <span class="score-sep">:</span>
          <span class="score away-score">{{ match.awayScore ?? 0 }}</span>
        </div>
        <div class="team-block away">
          <span class="team-name">{{ match.awayTeamName || '待定' }}</span>
          <div class="team-badge">{{ match.awayTeamName?.charAt(0) || '?' }}</div>
        </div>
      </div>

      <!-- 实时比分 -->
      <MatchLiveScore :matchId="matchId" @update="onScoreUpdate" />

      <!-- 赛事信息 -->
      <div class="match-info-grid">
        <div class="info-item">
          <div class="info-icon">📅</div>
          <div class="info-label">比赛时间</div>
          <div class="info-value">{{ match.matchDate || '-' }}</div>
        </div>
        <div class="info-item">
          <div class="info-icon">🏟️</div>
          <div class="info-label">比赛场地</div>
          <div class="info-value">{{ match.venue || '-' }}</div>
        </div>
        <div class="info-item">
          <div class="info-icon">⚽</div>
          <div class="info-label">比赛类型</div>
          <div class="info-value">{{ match.matchType === 'cup' ? '杯赛制' : '积分制联赛' }}</div>
        </div>
        <div class="info-item">
          <div class="info-icon">📊</div>
          <div class="info-label">比赛状态</div>
          <div class="info-value">
            <el-tag :type="getStatusType(match.status)" size="small">
              {{ getStatusText(match.status) }}
            </el-tag>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 参赛球队 -->
    <el-card class="registrations-card" v-if="registrations.length > 0">
      <template #header>
        <div class="card-title-bar">
          <span class="card-title-icon">👥</span>
          <span>参赛球队</span>
          <span class="card-count">{{ registrations.length }} 支</span>
        </div>
      </template>
      <el-table :data="registrations" stripe size="small">
        <el-table-column prop="teamName" label="球队名称" />
        <el-table-column label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : row.status === 2 ? 'danger' : 'warning'" size="small">
              {{ row.status === 1 ? '✅ 已接受' : row.status === 2 ? '❌ 已拒绝' : '⏳ 待处理' }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import request from '@/utils/request'
import MatchLiveScore from '@/components/MatchLiveScore.vue'

const route = useRoute()
const userStore = useUserStore()

const loading = ref(false)
const match = ref(null)
const registrations = ref([])
const matchId = computed(() => route.params.id)

async function fetchMatch() {
  loading.value = true
  try {
    const res = await request.get(`/api/matches/${matchId.value}`)
    match.value = res.data
    await fetchRegistrations()
  } finally {
    loading.value = false
  }
}

async function fetchRegistrations() {
  const res = await request.get(`/api/matches/${matchId.value}/registrations`)
  registrations.value = res.data
}

function onScoreUpdate(data) {
  if (match.value) {
    match.value.homeScore = data.homeScore
    match.value.awayScore = data.awayScore
    match.value.status = data.status
  }
}

function getStatusType(s) { return { 0: 'info', 1: 'success', 2: 'warning' }[s] || 'info' }
function getStatusText(s) { return { 0: '未开始', 1: '进行中', 2: '已结束' }[s] || '未知' }

onMounted(fetchMatch)
</script>

<style scoped>
.page-shell { max-width: 900px; margin: 0 auto; display: flex; flex-direction: column; gap: 16px; }

.match-hero { overflow: hidden; }

.match-hero-top {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 20px;
}
.type-tag, .status-tag { font-weight: 500; }

.btn-manage { margin-left: auto; border-radius: 8px; }

/* ── 对阵横幅 ── */
.match-vs-banner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 28px 20px;
  background: linear-gradient(135deg, #0f1c2e, #1a3a5c);
  border-radius: 14px;
  margin-bottom: 20px;
  color: #fff;
}

.team-block {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  flex: 1;
}
.team-block.home { text-align: left; align-items: flex-start; }
.team-block.away { text-align: right; align-items: flex-end; }

.team-badge {
  width: 52px; height: 52px;
  background: rgba(255, 255, 255, 0.15);
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  font-size: 22px; font-weight: 700;
  backdrop-filter: blur(4px);
}

.team-name {
  font-size: 15px;
  font-weight: 600;
  max-width: 140px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.score-display {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 0 20px;
}

.score {
  font-size: 36px;
  font-weight: 800;
  color: #409eff;
  line-height: 1;
}
.score-sep { font-size: 22px; color: #4a5568; }

/* ── 信息网格 ── */
.match-info-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
  margin-top: 16px;
}

.info-item {
  text-align: center;
  padding: 16px 8px;
  background: #f8fafc;
  border-radius: 10px;
  transition: background 0.2s;
}
.info-item:hover { background: #f0f4ff; }
.info-icon { font-size: 22px; margin-bottom: 6px; }
.info-label { font-size: 12px; color: #a0aec0; margin-bottom: 4px; }
.info-value { font-size: 13px; font-weight: 600; color: #2d3748; }

/* ── 参赛球队 ── */
.registrations-card { overflow: hidden; }
.card-title-bar {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 15px;
  font-weight: 600;
  color: #2d3748;
}
.card-title-icon { font-size: 18px; }
.card-count {
  margin-left: auto;
  font-size: 12px;
  color: #a0aec0;
  font-weight: 400;
}
</style>
