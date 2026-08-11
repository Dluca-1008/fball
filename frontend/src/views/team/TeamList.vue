<template>
  <div class="page-shell">
    <div class="page-header">
      <div class="header-left">
        <div class="header-icon teams">🏟️</div>
        <div>
          <h1 class="header-title">球队管理</h1>
          <p class="header-desc">创建并管理球队信息</p>
        </div>
      </div>
      <div class="header-actions">
        <el-input v-model="keyword" placeholder="搜索球队" clearable style="width:200px" @clear="fetchTeams" @keyup.enter="fetchTeams" />
        <el-button @click="fetchTeams">搜索</el-button>
        <el-button type="success" class="btn-create" @click="$router.push('/teams/create')">
          <el-icon><Plus /></el-icon> 创建球队
        </el-button>
      </div>
    </div>

    <div class="team-cards" v-loading="loading">
      <el-empty v-if="!loading && teams.length === 0" description="暂无球队" />
      <div v-for="team in teams" :key="team.id" class="team-card" @click="$router.push(`/teams/${team.id}`)">
        <div class="team-card-header">
          <div class="team-avatar">{{ (team.name || '?').charAt(0) }}</div>
          <div class="team-main-info">
            <h3 class="team-name">{{ team.name }}</h3>
            <p class="team-location">
              <el-icon><Location /></el-icon> {{ team.city }} · {{ team.country }}
            </p>
          </div>
          <el-button type="primary" link size="small" class="detail-link">查看详情 →</el-button>
        </div>
        <div class="team-card-footer">
          <span class="stadium"><el-icon><OfficeBuilding /></el-icon> {{ team.stadium || '主场未设置' }}</span>
        </div>
      </div>
    </div>

    <div class="pagination-wrap" v-if="total > 0">
      <el-pagination
        v-model:current-page="page"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50]"
        :total="total"
        layout="total, sizes, prev, pager, next"
        @size-change="fetchTeams"
        @current-change="fetchTeams"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'
import { Plus, Location, OfficeBuilding } from '@element-plus/icons-vue'

const loading = ref(false)
const teams = ref([])
const keyword = ref('')
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)

async function fetchTeams() {
  loading.value = true
  try {
    const res = await request.get('/api/teams', {
      params: { page: page.value, size: pageSize.value, keyword: keyword.value }
    })
    teams.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

onMounted(fetchTeams)
</script>

<style scoped>
.page-shell { max-width: 960px; margin: 0 auto; }

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24px;
  flex-wrap: wrap;
  gap: 12px;
}
.header-left { display: flex; align-items: center; gap: 14px; }
.header-icon {
  width: 48px; height: 48px;
  background: linear-gradient(135deg, #409eff, #1d4ed8);
  border-radius: 12px;
  display: flex; align-items: center; justify-content: center;
  font-size: 24px;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}
.header-title { font-size: 21px; font-weight: 700; color: #1a202c; margin-bottom: 2px; }
.header-desc { font-size: 13px; color: #718096; }

.header-actions { display: flex; gap: 10px; align-items: center; flex-wrap: wrap; }

.btn-create {
  background: linear-gradient(135deg, #67c23a, #2f891e) !important;
  border: none !important;
  border-radius: 10px !important;
  font-weight: 600 !important;
  box-shadow: 0 4px 12px rgba(103, 194, 58, 0.3);
  transition: transform 0.2s, box-shadow 0.2s;
}
.btn-create:hover { transform: translateY(-2px); box-shadow: 0 6px 20px rgba(103, 194, 58, 0.4); }

/* ── 球队卡片 ── */
.team-cards { display: flex; flex-direction: column; gap: 12px; }

.team-card {
  background: #fff;
  border-radius: 14px;
  padding: 18px 22px;
  cursor: pointer;
  border: 1px solid rgba(0,0,0,0.05);
  transition: transform 0.2s, box-shadow 0.2s, border-color 0.2s;
}
.team-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 24px rgba(0,0,0,0.1);
  border-color: rgba(64, 158, 255, 0.25);
}

.team-card-header { display: flex; align-items: center; gap: 14px; }

.team-avatar {
  width: 48px; height: 48px;
  background: linear-gradient(135deg, #409eff, #1d4ed8);
  border-radius: 12px;
  display: flex; align-items: center; justify-content: center;
  font-size: 22px; font-weight: 700; color: #fff;
  flex-shrink: 0;
}

.team-main-info { flex: 1; min-width: 0; }
.team-name { font-size: 16px; font-weight: 600; color: #1a202c; margin-bottom: 4px; }
.team-location { font-size: 13px; color: #718096; display: flex; align-items: center; gap: 4px; }

.detail-link { flex-shrink: 0; }

.team-card-footer { margin-top: 12px; padding-top: 12px; border-top: 1px solid #f7fafc; }
.stadium { font-size: 12px; color: #a0aec0; display: flex; align-items: center; gap: 4px; }

.pagination-wrap { display: flex; justify-content: flex-end; margin-top: 24px; }
</style>
