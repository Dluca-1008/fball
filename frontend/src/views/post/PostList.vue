<template>
  <div class="post-list">
    <div class="page-header">
      <h2>社区帖子</h2>
      <el-button type="primary" @click="showCreateDialog">发布帖子</el-button>
    </div>
    <el-table :data="posts" v-loading="loading" stripe>
      <el-table-column prop="title" label="标题" />
      <el-table-column prop="category" label="分类" width="100" />
      <el-table-column prop="viewCount" label="浏览" width="80" />
      <el-table-column prop="likeCount" label="点赞" width="80" />
      <el-table-column prop="createdAt" label="发布时间" />
      <el-table-column label="操作" width="120">
        <template #default="{ row }">
          <el-button type="primary" link @click="$router.push(`/posts/${row.id}`)">查看详情</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      v-model:current-page="page"
      v-model:page-size="pageSize"
      :page-sizes="[10, 20, 50]"
      :total="total"
      layout="total, sizes, prev, pager, next"
      @size-change="fetchPosts"
      @current-change="fetchPosts"
    />

    <el-dialog v-model="dialogVisible" title="发布帖子" width="600px">
      <el-form :model="form" :rules="rules" ref="formRef">
        <el-form-item prop="title" label="标题">
          <el-input v-model="form.title" placeholder="请输入标题" />
        </el-form-item>
        <el-form-item prop="category" label="分类">
          <el-select v-model="form.category" placeholder="请选择分类">
            <el-option label="赛事讨论" value="赛事讨论" />
            <el-option label="球队动态" value="球队动态" />
            <el-option label="球员话题" value="球员话题" />
            <el-option label="球迷交流" value="球迷交流" />
          </el-select>
        </el-form-item>
        <el-form-item prop="content" label="内容">
          <el-input v-model="form.content" type="textarea" :rows="6" placeholder="请输入内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitPost">发布</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import request from '@/utils/request'

const userStore = useUserStore()

const loading = ref(false)
const submitting = ref(false)
const posts = ref([])
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const dialogVisible = ref(false)
const formRef = ref(null)

const form = ref({
  title: '',
  category: '',
  content: ''
})

const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入内容', trigger: 'blur' }]
}

async function fetchPosts() {
  loading.value = true
  try {
    const res = await request.get('/api/posts', {
      params: { page: page.value, size: pageSize.value }
    })
    posts.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

function showCreateDialog() {
  if (!userStore.token) {
    ElMessage.warning('请先登录')
    return
  }
  form.value = { title: '', category: '', content: '' }
  dialogVisible.value = true
}

async function submitPost() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    await request.post('/api/posts', form.value)
    ElMessage.success('发布成功')
    dialogVisible.value = false
    fetchPosts()
  } finally {
    submitting.value = false
  }
}

onMounted(fetchPosts)
</script>

<style scoped>
.post-list {
  background: white;
  padding: 20px;
  border-radius: 8px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.el-pagination {
  margin-top: 20px;
  justify-content: flex-end;
}
</style>
