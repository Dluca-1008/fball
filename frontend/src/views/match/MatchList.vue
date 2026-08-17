<template>
  <div class="page-shell">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <div class="header-icon trophy">🏆</div>
        <div>
          <h1 class="header-title">赛事中心</h1>
          <p class="header-desc">正在举办的赛事，点击查看比赛详情</p>
        </div>
      </div>
      <div class="header-actions">
        <el-button @click="$router.push('/app/matches/schedule')">
          <el-icon><Calendar /></el-icon> 日程
        </el-button>
        <el-button @click="$router.push('/app/matches/stats')">
          <el-icon><DataAnalysis /></el-icon> 统计
        </el-button>
        <el-button
          v-if="userStore.hasRole('organizer')"
          type="primary"
          @click="$router.push('/app/matches/create')"
        >
          <el-icon><Plus /></el-icon> 创建赛事
        </el-button>
      </div>
    </div>

    <!-- 赛事卡片列表 -->
    <div class="tournament-list" v-loading="loading">
      <el-empty v-if="!loading && matches.length === 0" description="暂无正在举办的赛事" :image-size="100" />
      <div
        v-for="match in matches"
        :key="match.id"
        class="tournament-card"
        @click="goToMatch(match)"
      >
        <div class="tournament-header">
          <span class="tournament-name">{{ match.name || '未命名赛事' }}</span>
          <el-tag type="success" size="small" round>
            {{ match.matchDate ? formatDate(match.matchDate) : '待定日期' }}
          </el-tag>
        </div>
        <div class="tournament-meta">
          <span class="meta-item"><el-icon><Location /></el-icon> {{ match.venue || '待定场地' }}</span>
          <span class="meta-item"><el-icon><UserFilled /></el-icon> 赛事组织者</span>
        </div>
        <div class="tournament-footer">
          <span class="footer-hint">点击查看详情与赛程 →</span>
          <el-button size="small" type="primary" plain @click.stop="$router.push(`/app/matches/${match.id}`)">
            进入赛事
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import request from '@/utils/request'
import { Calendar, DataAnalysis, Plus, Location, UserFilled } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const matches = ref([])

function formatDate(dateStr) {
  if (!dateStr) return '-'
  const d = new Date(dateStr)
  return d.toLocaleDateString('zh-CN', { month: '2-digit', day: '2-digit' })
}

function goToMatch(match) {
  router.push(`/app/matches/${match.id}`)
}

async function fetchMatches() {
  loading.value = true
  try {
    const res = await request.get('/api/matches', { params: { page: 1, size: 20 } })
    matches.value = res.data.records || res.data
  } finally {
    loading.value = false
  }
}

onMounted(fetchMatches)
</script>

<style scoped>
.page-shell { max-width: 1000px; margin: 0 auto; }

/* ── 页面头部 ── */
.page-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  margin-bottom: 28px;
  gap: 16px;
  flex-wrap: wrap;
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

.header-actions { display: flex; gap: 10px; flex-wrap: wrap; }

/* ── 赛事卡片 ── */
.tournament-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.tournament-card {
  background: #fff;
  border-radius: 14px;
  padding: 20px 24px;
  cursor: pointer;
  border: 1px solid rgba(0,0,0,0.05);
  transition: transform 0.2s, box-shadow 0.2s, border-color 0.2s;
}
.tournament-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
  border-color: rgba(230, 162, 60, 0.3);
}

.tournament-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 10px;
}

.tournament-name {
  font-size: 17px;
  font-weight: 700;
  color: #1a202c;
}

.tournament-meta {
  display: flex;
  gap: 16px;
  margin-bottom: 14px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #718096;
}

.tournament-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-top: 12px;
  border-top: 1px solid #f0f2f5;
}

.footer-hint { font-size: 13px; color: #a0aec0; }
</style>
