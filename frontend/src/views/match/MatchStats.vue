<template>
  <div class="match-stats">
    <div class="page-header">
      <h2>比赛统计</h2>
    </div>

    <el-row :gutter="20" v-loading="loading">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">{{ overallStats.totalMatches || 0 }}</div>
          <div class="stat-label">总比赛数</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card success">
          <div class="stat-value">{{ overallStats.completedMatches || 0 }}</div>
          <div class="stat-label">已结束</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card warning">
          <div class="stat-value">{{ overallStats.inProgressMatches || 0 }}</div>
          <div class="stat-label">进行中</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card info">
          <div class="stat-value">{{ overallStats.pendingMatches || 0 }}</div>
          <div class="stat-label">未开始</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">{{ overallStats.totalGoals || 0 }}</div>
          <div class="stat-label">总进球数</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">{{ overallStats.avgGoalsPerMatch || 0 }}</div>
          <div class="stat-label">场均进球</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">{{ overallStats.totalTeams || 0 }}</div>
          <div class="stat-label">参赛球队</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>赛事类型分布</span>
          </template>
          <div class="type-distribution">
            <div class="type-item">
              <span class="type-label">联赛</span>
              <el-progress :percentage="getPercentage(overallStats.leagueMatches, overallStats.totalMatches)" status="success" />
              <span class="type-count">{{ overallStats.leagueMatches || 0 }}场</span>
            </div>
            <div class="type-item">
              <span class="type-label">杯赛</span>
              <el-progress :percentage="getPercentage(overallStats.cupMatches, overallStats.totalMatches)" status="danger" />
              <span class="type-count">{{ overallStats.cupMatches || 0 }}场</span>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card>
          <template #header>
            <span>球队进球排行</span>
          </template>
          <el-table :data="topScorers" stripe size="small">
            <el-table-column type="index" label="#" width="50" />
            <el-table-column prop="teamName" label="球队" />
            <el-table-column prop="goals" label="进球数" width="100" />
            <el-table-column label="进度条">
              <template #default="{ row }">
                <el-progress :percentage="getGoalPercentage(row.goals)" :stroke-width="10" />
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <el-card style="margin-top: 20px;" v-if="selectedMatchId">
      <template #header>
        <div class="card-header">
          <span>联赛统计</span>
          <el-select v-model="selectedMatchId" placeholder="选择联赛" size="small" style="width: 200px;">
            <el-option v-for="m in matches" :key="m.id" :label="`${m.homeTeamName || '联赛'} #${m.id}`" :value="m.id" />
          </el-select>
        </div>
      </template>
      <div class="league-stats" v-if="leagueStats">
        <el-row :gutter="16">
          <el-col :span="6">
            <div class="league-stat">
              <div class="league-stat-value">{{ leagueStats.totalMatches }}</div>
              <div class="league-stat-label">总场次</div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="league-stat">
              <div class="league-stat-value">{{ leagueStats.totalTeams }}</div>
              <div class="league-stat-label">参赛球队</div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="league-stat">
              <div class="league-stat-value">{{ leagueStats.groupCount }}</div>
              <div class="league-stat-label">分组数</div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="league-stat">
              <div class="league-stat-value">{{ leagueStats.avgGoalsPerMatch }}</div>
              <div class="league-stat-label">场均进球</div>
            </div>
          </el-col>
        </el-row>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'

const loading = ref(false)
const overallStats = ref({})
const topScorers = ref([])
const leagueStats = ref(null)
const selectedMatchId = ref(null)
const matches = ref([])

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
  const maxGoals = topScorers.value.length > 0 ? topScorers.value[0].goals : 1
  return Math.round(goals / maxGoals * 100)
}

onMounted(() => {
  fetchOverallStats()
  fetchTopScorers()
  fetchMatches()
})

import { watch } from 'vue'
watch(selectedMatchId, fetchLeagueStats)
</script>

<style scoped>
.match-stats {
  background: white;
  padding: 20px;
  border-radius: 8px;
}

.page-header {
  margin-bottom: 20px;
}

.stat-card {
  text-align: center;
  padding: 20px;
}

.stat-card .stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #409eff;
}

.stat-card.success .stat-value { color: #67c23a; }
.stat-card.warning .stat-value { color: #e6a23c; }
.stat-card.info .stat-value { color: #909399; }

.stat-card .stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 8px;
}

.type-distribution {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.type-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.type-label {
  width: 40px;
  font-weight: 500;
}

.type-count {
  width: 60px;
  text-align: right;
  color: #909399;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.league-stats {
  padding: 10px 0;
}

.league-stat {
  text-align: center;
  padding: 12px;
  background: #f5f7fa;
  border-radius: 8px;
}

.league-stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #409eff;
}

.league-stat-label {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}
</style>
