<template>
  <div class="player-list">
    <div class="page-header">
      <h2>球员管理</h2>
      <el-input v-model="keyword" placeholder="搜索球员" style="width: 200px; margin-right: 12px;" clearable @clear="fetchPlayers" @keyup.enter="fetchPlayers" />
      <el-button type="primary" @click="fetchPlayers">搜索</el-button>
      <el-button type="success" style="margin-left: auto;" @click="$router.push('/players/create')">添加球员</el-button>
    </div>
    <el-table :data="players" v-loading="loading" stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="name" label="姓名" />
      <el-table-column prop="position" label="位置" width="80" />
      <el-table-column prop="number" label="号码" width="60" />
      <el-table-column prop="nationality" label="国籍" />
      <el-table-column prop="teamName" label="所属球队" />
      <el-table-column prop="height" label="身高(cm)" width="90" />
      <el-table-column prop="weight" label="体重(kg)" width="90" />
      <el-table-column label="操作" width="120">
        <template #default="{ row }">
          <el-button type="primary" link @click="$router.push(`/players/${row.id}/manage`)">管理</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      v-model:current-page="page"
      v-model:page-size="pageSize"
      :page-sizes="[10, 20, 50]"
      :total="total"
      layout="total, sizes, prev, pager, next"
      @size-change="fetchPlayers"
      @current-change="fetchPlayers"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'

const loading = ref(false)
const players = ref([])
const keyword = ref('')
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)

async function fetchPlayers() {
  loading.value = true
  try {
    const res = await request.get('/api/players', {
      params: { page: page.value, size: pageSize.value, keyword: keyword.value || undefined }
    })
    players.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

onMounted(fetchPlayers)
</script>

<style scoped>
.player-list {
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
