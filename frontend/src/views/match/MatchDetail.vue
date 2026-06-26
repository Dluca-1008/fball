<template>
  <div class="match-detail" v-loading="loading">
    <el-card v-if="match">
      <div class="match-header">
        <div>
          <h2>{{ match.homeTeamName || '待定' }} vs {{ match.awayTeamName || '待定' }}</h2>
          <el-tag :type="match.matchType === 'cup' ? 'danger' : 'success'" style="margin-top: 8px;">{{ match.matchType === 'cup' ? '杯赛' : '联赛' }}</el-tag>
        </div>
        <el-button v-if="userStore.hasPermission('match:edit')" type="primary" @click="$router.push(`/matches/${match.id}/manage`)">管理比赛</el-button>
      </div>
      <MatchLiveScore :matchId="matchId" @update="onScoreUpdate" />

      <el-divider />

      <div class="match-info">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="比赛类型">{{ match.matchType === 'cup' ? '杯赛制' : '积分制联赛' }}</el-descriptions-item>
          <el-descriptions-item label="比赛状态">
            <el-tag :type="getStatusType(match.status)">{{ getStatusText(match.status) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="比赛时间">{{ match.matchDate }}</el-descriptions-item>
          <el-descriptions-item label="比赛场地">{{ match.venue }}</el-descriptions-item>
          <el-descriptions-item label="主队">{{ match.homeTeamName || '待定' }}</el-descriptions-item>
          <el-descriptions-item label="客队">{{ match.awayTeamName || '待定' }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-card>

    <el-card class="registrations-section" v-if="registrations.length > 0">
      <template #header>
        <span>参赛球队</span>
      </template>
      <el-table :data="registrations" stripe>
        <el-table-column prop="teamName" label="球队名称" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : row.status === 2 ? 'danger' : 'warning'">
              {{ row.status === 1 ? '已接受' : row.status === 2 ? '已拒绝' : '待处理' }}
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

function getStatusType(status) {
  const types = { 0: 'info', 1: 'success', 2: 'warning' }
  return types[status] || 'info'
}

function getStatusText(status) {
  const texts = { 0: '未开始', 1: '进行中', 2: '已结束' }
  return texts[status] || '未知'
}

onMounted(fetchMatch)
</script>

<style scoped>
.match-detail {
  max-width: 800px;
  margin: 0 auto;
}

.match-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
}

.match-info {
  margin-top: 20px;
}

.registrations-section {
  margin-top: 20px;
}
</style>
