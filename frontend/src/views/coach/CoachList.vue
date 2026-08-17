<template>
  <div class="page-shell">
    <div class="page-header">
      <div class="header-left">
        <div class="header-icon coach">🎩</div>
        <div>
          <h1 class="header-title">教练管理</h1>
          <p class="header-desc">管理球队教练阵容</p>
        </div>
      </div>
      <div class="header-actions">
        <el-input v-model="keyword" placeholder="搜索教练" clearable style="width:200px" @clear="fetchCoaches" @keyup.enter="fetchCoaches" />
        <el-button @click="fetchCoaches">搜索</el-button>
        <el-button type="success" class="btn-create" @click="$router.push('/app/coaches/create')">
          <el-icon><Plus /></el-icon> 添加教练
        </el-button>
      </div>
    </div>

    <el-table :data="coaches" v-loading="loading" stripe class="coach-table">
      <el-table-column prop="name" label="姓名" width="140">
        <template #default="{ row }">
          <div class="coach-cell">
            <div class="coach-avatar">{{ row.name?.charAt(0) || '?' }}</div>
            <span>{{ row.name }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="roleTitle" label="职位" width="130">
        <template #default="{ row }">
          <el-tag size="small" class="role-tag">{{ row.roleTitle || '-' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="teamName" label="所属球队" width="160" />
      <el-table-column prop="nationality" label="国籍" width="100" />
      <el-table-column prop="experienceYears" label="执教年限" width="100" align="center">
        <template #default="{ row }">
          <span class="exp-badge">{{ row.experienceYears }}年</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="$router.push(`/app/coaches/${row.id}/manage`)">管理</el-button>
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
        @size-change="fetchCoaches"
        @current-change="fetchCoaches"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'
import { Plus } from '@element-plus/icons-vue'

const loading = ref(false)
const coaches = ref([])
const keyword = ref('')
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)

async function fetchCoaches() {
  loading.value = true
  try {
    const res = await request.get('/api/coaches', {
      params: { page: page.value, size: pageSize.value, keyword: keyword.value || undefined }
    })
    coaches.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

onMounted(fetchCoaches)
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
  background: linear-gradient(135deg, #e6a23c, #c45d0e);
  border-radius: 12px; display: flex; align-items: center; justify-content: center;
  font-size: 24px; box-shadow: 0 4px 12px rgba(230, 162, 60, 0.3);
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

.coach-table { background: #fff; border-radius: 14px; overflow: hidden; }
.coach-cell { display: flex; align-items: center; gap: 10px; }
.coach-avatar {
  width: 34px; height: 34px;
  background: linear-gradient(135deg, #e6a23c, #c45d0e);
  border-radius: 50%; display: flex; align-items: center; justify-content: center;
  font-size: 14px; font-weight: 700; color: #fff; flex-shrink: 0;
}
.role-tag { background: rgba(230, 162, 60, 0.08); color: #c45d0e; border: none; font-weight: 500; }
.exp-badge { font-weight: 700; color: #c45d0e; }

.pagination-wrap { display: flex; justify-content: flex-end; margin-top: 24px; }
</style>
