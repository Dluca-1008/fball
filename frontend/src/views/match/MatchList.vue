<template>
  <div class="match-list">
    <div class="page-header">
      <h2>赛事列表</h2>
      <div class="header-actions">
        <el-button @click="$router.push('/matches/schedule')">日程安排</el-button>
        <el-button @click="$router.push('/matches/stats')">比赛统计</el-button>
        <el-button type="warning" @click="$router.push('/matches/generate')">自动生成赛程</el-button>
        <el-button type="success" @click="$router.push('/matches/create')">创建比赛</el-button>
      </div>
    </div>
    <el-table :data="matches" v-loading="loading" stripe>
      <el-table-column label="比赛类型" width="100">
        <template #default="{ row }">
          <el-tag :type="row.matchType === 'cup' ? 'danger' : 'success'">{{ row.matchType === 'cup' ? '杯赛' : '联赛' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="主队" prop="homeTeamName" />
      <el-table-column label="比分" width="100" align="center">
        <template #default="{ row }">
          <span class="score">{{ row.homeScore }} - {{ row.awayScore }}</span>
        </template>
      </el-table-column>
      <el-table-column label="客队" prop="awayTeamName" />
      <el-table-column prop="matchDate" label="比赛时间" />
      <el-table-column prop="venue" label="比赛场地" />
      <el-table-column label="状态">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180">
        <template #default="{ row }">
          <el-button type="primary" link @click="$router.push(`/matches/${row.id}`)">详情</el-button>
          <el-button v-if="userStore.hasPermission('match:edit')" type="warning" link @click="$router.push(`/matches/${row.id}/manage`)">管理</el-button>
        </template>
      </el-table-column>
    </el-table>
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
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import request from '@/utils/request'

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
  const types = { 0: 'info', 1: 'success', 2: 'warning' }
  return types[status] || 'info'
}

function getStatusText(status) {
  const texts = { 0: '未开始', 1: '进行中', 2: '已结束' }
  return texts[status] || '未知'
}

onMounted(fetchMatches)
</script>

<style scoped>
.match-list {
  background: white;
  padding: 20px;
  border-radius: 8px;
}

.page-header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.score {
  font-weight: bold;
  color: #409eff;
}

.el-pagination {
  margin-top: 20px;
  justify-content: flex-end;
}
</style>
