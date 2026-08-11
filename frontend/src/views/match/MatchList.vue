<template>
  <div class="page-shell">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <div class="header-icon trophy">🏆</div>
        <div>
          <h1 class="header-title">赛事列表</h1>
          <p class="header-desc">管理所有赛事，追踪比赛动态</p>
        </div>
      </div>
      <div class="header-actions">
        <el-button @click="$router.push('/matches/schedule')">
          <el-icon><Calendar /></el-icon> 日程
        </el-button>
        <el-button @click="$router.push('/matches/stats')">
          <el-icon><DataAnalysis /></el-icon> 统计
        </el-button>
        <el-button type="warning" @click="$router.push('/matches/generate')">
          <el-icon><Operation /></el-icon> 自动赛程
        </el-button>
        <el-button type="primary" @click="$router.push('/matches/create')">
          <el-icon><Plus /></el-icon> 创建比赛
        </el-button>
      </div>
    </div>

    <!-- 赛事卡片 -->
    <div class="match-cards" v-loading="loading">
      <el-empty v-if="!loading && matches.length === 0" description="暂无赛事" :image-size="100" />
      <div v-for="match in matches" :key="match.id" class="match-card" @click="$router.push(`/matches/${match.id}`)">
        <!-- 类型标签 -->
        <div class="match-card-type">
          <el-tag :type="match.matchType === 'cup' ? 'danger' : 'success'" size="small">
            {{ match.matchType === 'cup' ? '🏅 杯赛' : '⚽ 联赛' }}
          </el-tag>
          <el-tag :type="getStatusType(match.status)" size="small">
            {{ getStatusText(match.status) }}
          </el-tag>
        </div>

        <!-- 对阵 -->
        <div class="match-card-vs">
          <div class="team home">
            <div class="team-badge">{{ (match.homeTeamName || '?').charAt(0) }}</div>
            <span class="team-name">{{ match.homeTeamName || '待定' }}</span>
          </div>
          <div class="vs-score">
            <template v-if="match.status === 1 || match.status === 2">
              <span class="score-num">{{ match.homeScore ?? 0 }}</span>
              <span class="score-sep">:</span>
              <span class="score-num">{{ match.awayScore ?? 0 }}</span>
            </template>
            <template v-else>
              <span class="vs-text">VS</span>
            </template>
          </div>
          <div class="team away">
            <span class="team-name">{{ match.awayTeamName || '待定' }}</span>
            <div class="team-badge">{{ (match.awayTeamName || '?').charAt(0) }}</div>
          </div>
        </div>

        <!-- 底部信息 -->
        <div class="match-card-footer">
          <span class="meta-item"><el-icon><Clock /></el-icon> {{ match.matchDate?.split(' ')[0] || '-' }}</span>
          <span class="meta-item"><el-icon><Location /></el-icon> {{ match.venue || '-' }}</span>
          <div class="card-actions" @click.stop>
            <el-button type="primary" link size="small" @click="$router.push(`/matches/${match.id}`)">详情</el-button>
            <el-button v-if="userStore.hasPermission('match:edit')" type="warning" link size="small" @click="$router.push(`/matches/${match.id}/manage`)">管理</el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 分页 -->
    <div class="pagination-wrap" v-if="total > 0">
      <el-pagination
        v-model:current-page="page"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50]"
        :total="total"
        layout="total, sizes, prev, pager, next"
        @size-change="fetchMatches"
        @current-change="fetchMatches"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import request from '@/utils/request'
import { Calendar, DataAnalysis, Operation, Plus, Clock, Location } from '@element-plus/icons-vue'

const userStore = useUserStore()
const loading = ref(false)
const matches = ref([])
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)

async function fetchMatches() {
  loading.value = true
  try {
    const res = await request.get('/api/matches', {
      params: { page: page.value, size: pageSize.value }
    })
    matches.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

function getStatusType(status) {
  return { 0: 'info', 1: 'success', 2: 'warning' }[status] || 'info'
}
function getStatusText(status) {
  return { 0: '未开始', 1: '进行中', 2: '已结束' }[status] || '未知'
}

onMounted(fetchMatches)
</script>

<style scoped>
.page-shell {
  max-width: 1000px;
  margin: 0 auto;
}

/* ── 页面头部 ── */
.page-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  margin-bottom: 28px;
  gap: 16px;
  flex-wrap: wrap;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.header-icon {
  width: 52px;
  height: 52px;
  background: linear-gradient(135deg, #e6a23c, #c45d0e);
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 26px;
  box-shadow: 0 4px 14px rgba(230, 162, 60, 0.35);
}

.header-title {
  font-size: 22px;
  font-weight: 700;
  color: #1a202c;
  margin-bottom: 2px;
}

.header-desc {
  font-size: 13px;
  color: #718096;
}

.header-actions {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

/* ── 赛事卡片 ── */
.match-cards {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.match-card {
  background: #fff;
  border-radius: 14px;
  padding: 18px 22px;
  cursor: pointer;
  border: 1px solid rgba(0,0,0,0.05);
  transition: transform 0.2s, box-shadow 0.2s, border-color 0.2s;
}
.match-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
  border-color: rgba(64, 158, 255, 0.3);
}

.match-card-type {
  display: flex;
  gap: 8px;
  margin-bottom: 14px;
}

.match-card-vs {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px 0;
}

.team {
  display: flex;
  align-items: center;
  gap: 10px;
  flex: 1;
  min-width: 0;
}
.team.home { justify-content: flex-end; text-align: right; }
.team.away { justify-content: flex-start; text-align: left; }

.team-badge {
  width: 36px;
  height: 36px;
  background: linear-gradient(135deg, #f0f4ff, #dce6ff);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 16px;
  color: #409eff;
  flex-shrink: 0;
}

.team-name {
  font-size: 15px;
  font-weight: 600;
  color: #1a202c;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.vs-score {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 0 16px;
  flex-shrink: 0;
}

.score-num {
  font-size: 26px;
  font-weight: 800;
  color: #1d4ed8;
  line-height: 1;
}
.score-sep {
  font-size: 18px;
  color: #cbd5e1;
  font-weight: 300;
}
.vs-text {
  font-size: 15px;
  font-weight: 700;
  color: #a0aec0;
  letter-spacing: 2px;
}

.match-card-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #f7fafc;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #a0aec0;
}

.card-actions {
  display: flex;
  gap: 4px;
}

/* ── 分页 ── */
.pagination-wrap {
  display: flex;
  justify-content: flex-end;
  margin-top: 24px;
}
</style>
