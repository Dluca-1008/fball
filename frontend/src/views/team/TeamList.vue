<template>
  <div class="team-list">
    <div class="page-header">
      <h2>球队列表</h2>
      <el-input v-model="keyword" placeholder="搜索球队" style="width: 200px; margin-right: 12px;" clearable @clear="fetchTeams" @keyup.enter="fetchTeams" />
      <el-button type="primary" @click="fetchTeams">搜索</el-button>
      <el-button type="success" style="margin-left: auto;" @click="$router.push('/teams/create')">创建球队</el-button>
    </div>
    <el-table :data="teams" v-loading="loading" stripe>
      <el-table-column prop="name" label="球队名称" />
      <el-table-column prop="city" label="城市" />
      <el-table-column prop="country" label="国家" />
      <el-table-column prop="stadium" label="主场" />
      <el-table-column label="操作" width="120">
        <template #default="{ row }">
          <el-button type="primary" link @click="$router.push(`/teams/${row.id}`)">查看详情</el-button>
        </template>
      </el-table-column>
    </el-table>
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
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'

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
.team-list {
  background: white;
  padding: 20px;
  border-radius: 8px;
}

.page-header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h2 {
  margin-right: 20px;
}

.el-pagination {
  margin-top: 20px;
  justify-content: flex-end;
}
</style>
