<template>
  <div class="page-shell">
    <div class="page-header">
      <div class="header-left">
        <div class="header-icon players">👤</div>
        <div>
          <h1 class="header-title">球员管理</h1>
          <p class="header-desc">管理球员信息与阵容</p>
        </div>
      </div>
      <div class="header-actions">
        <el-input v-model="keyword" placeholder="搜索球员" clearable style="width:200px" @clear="fetchPlayers" @keyup.enter="fetchPlayers" />
        <el-button @click="fetchPlayers">搜索</el-button>
        <el-button type="success" class="btn-create" @click="$router.push('/players/create')">
          <el-icon><Plus /></el-icon> 添加球员
        </el-button>
      </div>
    </div>

    <el-table :data="players" v-loading="loading" stripe class="player-table">
      <el-table-column prop="name" label="姓名" width="140">
        <template #default="{ row }">
          <div class="player-cell">
            <div class="player-avatar">{{ row.name?.charAt(0) || '?' }}</div>
            <span>{{ row.name }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="position" label="位置" width="100">
        <template #default="{ row }">
          <el-tag size="small" class="position-tag">{{ row.position || '-' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="number" label="号码" width="80" align="center">
        <template #default="{ row }">
          <span class="number-badge">#{{ row.number || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="teamName" label="所属球队" width="160" />
      <el-table-column prop="nationality" label="国籍" width="100" />
      <el-table-column label="身体数据" width="140">
        <template #default="{ row }">
          <span class="body-stats">{{ row.height }}cm / {{ row.weight }}kg</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="$router.push(`/players/${row.id}/manage`)">管理</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination-wrap" v-if="total > 0">
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
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'
import { Plus } from '@element-plus/icons-vue'

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
.page-shell { max-width: 1000px; margin: 0 auto; }

.page-header {
  display: flex; align-items: center; justify-content: space-between;
  margin-bottom: 24px; flex-wrap: wrap; gap: 12px;
}
.header-left { display: flex; align-items: center; gap: 14px; }
.header-icon {
  width: 48px; height: 48px;
  background: linear-gradient(135deg, #7c3aed, #553c9a);
  border-radius: 12px; display: flex; align-items: center; justify-content: center;
  font-size: 24px; box-shadow: 0 4px 12px rgba(124, 58, 237, 0.3);
}
.header-title { font-size: 21px; font-weight: 700; color: #1a202c; margin-bottom: 2px; }
.header-desc { font-size: 13px; color: #718096; }
.header-actions { display: flex; gap: 10px; flex-wrap: wrap; }

.btn-create {
  background: linear-gradient(135deg, #67c23a, #2f891e) !important;
  border: none !important; border-radius: 10px !important;
  font-weight: 600 !important;
  box-shadow: 0 4px 12px rgba(103, 194, 58, 0.3);
  transition: transform 0.2s, box-shadow 0.2s;
}
.btn-create:hover { transform: translateY(-2px); box-shadow: 0 6px 20px rgba(103, 194, 58, 0.4); }

/* ── 球员表格 ── */
.player-table { background: #fff; border-radius: 14px; overflow: hidden; }
.player-cell { display: flex; align-items: center; gap: 10px; }
.player-avatar {
  width: 34px; height: 34px;
  background: linear-gradient(135deg, #7c3aed, #553c9a);
  border-radius: 50%; display: flex; align-items: center; justify-content: center;
  font-size: 14px; font-weight: 700; color: #fff; flex-shrink: 0;
}
.position-tag { background: rgba(124, 58, 237, 0.08); color: #7c3aed; border: none; font-weight: 500; }
.number-badge { font-weight: 700; color: #7c3aed; font-size: 15px; }
.body-stats { font-size: 12px; color: #718096; }

.pagination-wrap { display: flex; justify-content: flex-end; margin-top: 24px; }
</style>
