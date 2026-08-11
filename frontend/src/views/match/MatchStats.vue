<template>
  <div class="stats-page">
    <div class="page-header">
      <div class="header-left">
        <div class="header-icon stats">📊</div>
        <div>
          <h1 class="header-title">赛事统计</h1>
          <p class="header-desc">数据分析，洞察赛场表现</p>
        </div>
      </div>
    </div>

    <!-- 概览数据卡片 -->
    <el-row :gutter="16" v-loading="loading">
      <el-col :span="6" v-for="(card, i) in overviewCards" :key="i">
        <div class="stat-hero-card" :style="{ '--card-color': card.color }">
          <div class="stat-card-icon">{{ card.icon }}</div>
          <div class="stat-card-value">{{ card.value }}</div>
          <div class="stat-card-label">{{ card.label }}</div>
        </div>
      </el-col>
    </el-row>

    <!-- 进球排行 + 类型分布 -->
    <el-row :gutter="16" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card class="rank-card" shadow="hover">
          <template #header>
            <div class="card-title-bar">
              <span class="card-icon">⚽</span>
              <span>球队进球排行</span>
              <span class="card-count">Top 10</span>
            </div>
          </template>
          <el-table :data="topScorers" size="small" class="rank-table">
            <el-table-column type="index" label="#" width="40" align="center">
              <template #default="{ $index }">
                <span :class="['rank-num', { 'rank-top3': $index < 3 }]">{{ $index + 1 }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="teamName" label="球队" />
            <el-table-column prop="goals" label="进球" width="80" align="center">
              <template #default="{ row }">
                <span class="goals-badge">{{ row.goals }}</span>
              </template>
            </el-table-column>
            <el-table-column label="占比">
              <template #default="{ row }">
                <el-progress :percentage="getGoalPercentage(row.goals)" :stroke-width="8" :color="row.goals === topScorers[0]?.goals ? '#e6a23c' : '#409eff'" />
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-if="topScorers.length === 0" description="暂无数据" :image-size="60" />
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card class="dist-card" shadow="hover">
          <template #header>
            <div class="card-title-bar">
              <span class="card-icon">📈</span>
              <span>赛事类型分布</span>
            </div>
          </template>
          <div class="dist-content">
            <div class="dist-item">
              <div class="dist-label-row">
                <span class="dist-label">⚽ 联赛</span>
                <span class="dist-value">{{ overallStats.leagueMatches || 0 }} 场</span>
              </div>
              <el-progress :percentage="getPercentage(overallStats.leagueMatches, overallStats.totalMatches)" :color="'#67c23a'" :stroke-width="12" />
            </div>
            <div class="dist-item">
              <div class="dist-label-row">
                <span class="dist-label">🏅 杯赛</span>
                <span class="dist-value">{{ overallStats.cupMatches || 0 }} 场</span>
              </div>
              <el-progress :percentage="getPercentage(overallStats.cupMatches, overallStats.totalMatches)" :color="'#e6a23c'" :stroke-width="12" />
            </div>
          </div>

          <!-- 场均进球 -->
          <div class="avg-goals-box">
            <div class="avg-goals-label">场均进球</div>
            <div class="avg-goals-value">{{ overallStats.avgGoalsPerMatch || 0 }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 联赛选择 + 联赛统计 -->
    <el-card class="league-card" shadow="hover" v-if="matches.length > 0">
      <template #header>
        <div class="card-title-bar">
          <span class="card-icon">🏟️</span>
          <span>联赛统计</span>
          <el-select v-model="selectedMatchId" placeholder="选择联赛" size="small" style="width: 220px;" class="league-select">
            <el-option v-for="m in matches" :key="m.id" :label="`${m.homeTeamName || '联赛'} #${m.id}`" :value="m.id" />
          </el-select>
        </div>
      </template>
      <el-row :gutter="12" v-if="leagueStats">
        <el-col :span="6" v-for="(v, k) in leagueStatCards" :key="k">
          <div class="league-stat-item">
            <div class="league-stat-value">{{ v.value }}</div>
            <div class="league-stat-label">{{ v.label }}</div>
          </div>
        </el-col>
      </el-row>
      <el-empty v-else description="请选择一个联赛" :image-size="60" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import request from '@/utils/request'

const loading = ref(false)
const overallStats = ref({})
const topScorers = ref([])
const leagueStats = ref(null)
const selectedMatchId = ref(null)
const matches = ref([])

const overviewCards = computed(() => [
  { icon: '🏆', label: '总比赛数', value: overallStats.value.totalMatches || 0, color: '#409eff' },
  { icon: '✅', label: '已结束', value: overallStats.value.completedMatches || 0, color: '#67c23a' },
  { icon: '🔴', label: '进行中', value: overallStats.value.inProgressMatches || 0, color: '#e6a23c' },
  { icon: '⏳', label: '未开始', value: overallStats.value.pendingMatches || 0, color: '#909399' },
])

const leagueStatCards = computed(() => {
  if (!leagueStats.value) return []
  return [
    { label: '总场次', value: leagueStats.value.totalMatches },
    { label: '参赛球队', value: leagueStats.value.totalTeams },
    { label: '分组数', value: leagueStats.value.groupCount },
    { label: '场均进球', value: leagueStats.value.avgGoalsPerMatch },
  ]
})

async function fetchOverallStats() {
  loading.value = true
  try {
    const res = await request.get('/api/match-stats/overall')
    overallStats.value = res.data
  } finally {
    loading.value = false
  }
}

async function fetchTopScorers() {
  const res = await request.get('/api/match-stats/top-scorers', { params: { limit: 10 } })
  topScorers.value = res.data
}

async function fetchLeagueStats() {
  if (!selectedMatchId.value) return
  const res = await request.get(`/api/match-stats/league/${selectedMatchId.value}`)
  leagueStats.value = res.data
}

async function fetchMatches() {
  const res = await request.get('/api/matches', { params: { page: 1, size: 50 } })
  matches.value = res.data.records
}

function getPercentage(value, total) {
  if (!total) return 0
  return Math.round((value || 0) / total * 100)
}

function getGoalPercentage(goals) {
  const max = topScorers.value.length > 0 ? topScorers.value[0].goals : 1
  return Math.round(goals / max * 100)
}

watch(selectedMatchId, fetchLeagueStats)

onMounted(() => {
  fetchOverallStats()
  fetchTopScorers()
  fetchMatches()
})
</script>

<style scoped>
.stats-page { max-width: 1000px; margin: 0 auto; }

.page-header { margin-bottom: 24px; }
.header-left { display: flex; align-items: center; gap: 14px; }
.header-icon {
  width: 48px; height: 48px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border-radius: 12px; display: flex; align-items: center; justify-content: center;
  font-size: 24px; box-shadow: 0 4px 12px rgba(102, 126, 234, 0.35);
}
.header-title { font-size: 21px; font-weight: 700; color: #1a202c; margin-bottom: 2px; }
.header-desc { font-size: 13px; color: #718096; }

/* ── 概览卡片 ── */
.stat-hero-card {
  background: #fff;
  border-radius: 16px;
  padding: 24px 20px;
  text-align: center;
  border: 1px solid rgba(0,0,0,0.05);
  transition: transform 0.2s, box-shadow 0.2s;
  position: relative;
  overflow: hidden;
}
.stat-hero-card::before {
  content: '';
  position: absolute;
  top: 0; left: 0; right: 0;
  height: 4px;
  background: var(--card-color);
}
.stat-hero-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0,0,0,0.1);
}
.stat-card-icon { font-size: 28px; margin-bottom: 10px; }
.stat-card-value {
  font-size: 36px;
  font-weight: 800;
  color: var(--card-color);
  line-height: 1;
}
.stat-card-label { font-size: 13px; color: #a0aec0; margin-top: 8px; }

/* ── 排行卡片 ── */
.rank-card, .dist-card, .league-card {
  border-radius: 16px;
  overflow: hidden;
}
.card-title-bar {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 15px;
  font-weight: 600;
  color: #2d3748;
}
.card-icon { font-size: 18px; }
.card-count { margin-left: auto; font-size: 12px; color: #a0aec0; font-weight: 400; }
.league-select { margin-left: auto; }

.rank-table { margin-top: 4px; }
.rank-num { font-weight: 700; font-size: 14px; }
.rank-num.rank-top3 { color: #e6a23c; }
.goals-badge {
  background: rgba(64, 158, 255, 0.08);
  color: #409eff;
  font-weight: 700;
  padding: 2px 10px;
  border-radius: 10px;
  font-size: 13px;
}

.dist-content { display: flex; flex-direction: column; gap: 16px; }
.dist-item {}
.dist-label-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 6px;
  font-size: 13px;
  font-weight: 500;
  color: #4a5568;
}
.dist-value { font-weight: 700; color: #2d3748; }

.avg-goals-box {
  margin-top: 20px;
  padding: 16px;
  background: linear-gradient(135deg, #667eea10, #764ba210);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: space-around;
}
.avg-goals-label { font-size: 13px; color: #718096; }
.avg-goals-value { font-size: 28px; font-weight: 800; color: #667eea; }

.league-stat-item {
  text-align: center;
  padding: 16px 8px;
  background: #f8fafc;
  border-radius: 10px;
}
.league-stat-value { font-size: 26px; font-weight: 800; color: #667eea; }
.league-stat-label { font-size: 12px; color: #a0aec0; margin-top: 4px; }
</style>
