<template>
  <div class="coach-list">
    <div class="page-header">
      <h2>教练管理</h2>
      <el-input v-model="keyword" placeholder="搜索教练" style="width: 200px; margin-right: 12px;" clearable @clear="fetchCoaches" @keyup.enter="fetchCoaches" />
      <el-button type="primary" @click="fetchCoaches">搜索</el-button>
      <el-button type="success" style="margin-left: auto;" @click="$router.push('/coaches/create')">添加教练</el-button>
    </div>
    <el-table :data="coaches" v-loading="loading" stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="name" label="姓名" />
      <el-table-column prop="roleTitle" label="职位" />
      <el-table-column prop="nationality" label="国籍" />
      <el-table-column prop="teamName" label="所属球队" />
      <el-table-column prop="experienceYears" label="执教年限" width="100" />
      <el-table-column label="操作" width="120">
        <template #default="{ row }">
          <el-button type="primary" link @click="$router.push(`/coaches/${row.id}/manage`)">管理</el-button>
        </template>
      </el-table-column>
    </el-table>
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
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'

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
.coach-list {
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
