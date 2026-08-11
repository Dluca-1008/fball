<template>
  <div class="post-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <div class="header-icon community">💬</div>
        <div>
          <h1 class="header-title">社区论坛</h1>
          <p class="header-desc">分享赛事见解，与球迷交流互动</p>
        </div>
      </div>
      <el-button type="primary" class="btn-publish" @click="showCreateDialog">
        <el-icon><Plus /></el-icon> 发布帖子
      </el-button>
    </div>

    <!-- 帖子卡片 -->
    <div class="post-cards" v-loading="loading">
      <el-empty v-if="!loading && posts.length === 0" description="暂无帖子" :image-size="100" />
      <div
        v-for="post in posts"
        :key="post.id"
        class="post-card"
        @click="$router.push(`/posts/${post.id}`)"
      >
        <div class="post-card-top">
          <el-tag size="small" class="category-tag">{{ post.category || '未分类' }}</el-tag>
          <span class="post-time">{{ formatTime(post.createdAt) }}</span>
        </div>
        <h3 class="post-title">{{ post.title }}</h3>
        <div class="post-card-bottom">
          <span class="stat"><el-icon><View /></el-icon> {{ post.viewCount || 0 }}</span>
          <span class="stat"><el-icon><Star /></el-icon> {{ post.likeCount || 0 }}</span>
          <span class="stat"><el-icon><ChatDotRound /></el-icon> {{ post.commentCount || 0 }} 评论</span>
        </div>
      </div>
    </div>

    <!-- 分页 -->
    <div class="pagination-wrap" v-if="total > 0">
      <el-pagination
        v-model:current-page="page"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50]"
        :total="total"
        layout="total, sizes, prev, pager, next"
        @size-change="fetchPosts"
        @current-change="fetchPosts"
      />
    </div>

    <!-- 发布对话框 -->
    <el-dialog v-model="dialogVisible" title="发布帖子" width="580px" class="publish-dialog">
      <el-form :model="form" :rules="rules" ref="formRef" label-position="top">
        <el-form-item prop="title" label="标题">
          <el-input v-model="form.title" placeholder="请输入标题" maxlength="100" show-word-limit />
        </el-form-item>
        <el-form-item prop="category" label="分类">
          <el-select v-model="form.category" placeholder="请选择分类" style="width:100%">
            <el-option label="赛事讨论" value="赛事讨论" />
            <el-option label="球队动态" value="球队动态" />
            <el-option label="球员话题" value="球员话题" />
            <el-option label="球迷交流" value="球迷交流" />
          </el-select>
        </el-form-item>
        <el-form-item prop="content" label="内容">
          <el-input v-model="form.content" type="textarea" :rows="6" placeholder="请输入内容" maxlength="2000" show-word-limit />
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
import { Plus, View, Star, ChatDotRound } from '@element-plus/icons-vue'

const userStore = useUserStore()
const loading = ref(false)
const submitting = ref(false)
const posts = ref([])
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const dialogVisible = ref(false)
const formRef = ref(null)

const form = ref({ title: '', category: '', content: '' })
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

function formatTime(time) {
  if (!time) return ''
  const d = new Date(time)
  const now = new Date()
  const diff = now - d
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return Math.floor(diff / 60000) + '分钟前'
  if (diff < 86400000) return Math.floor(diff / 3600000) + '小时前'
  return d.toLocaleDateString('zh-CN')
}

onMounted(fetchPosts)
</script>

<style scoped>
.post-page {
  max-width: 900px;
  margin: 0 auto;
}

/* ── 页面头部 ── */
.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24px;
  flex-wrap: wrap;
  gap: 12px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 14px;
}

.header-icon {
  width: 48px;
  height: 48px;
  background: linear-gradient(135deg, #67c23a, #2f891e);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  box-shadow: 0 4px 12px rgba(103, 194, 58, 0.3);
}

.header-title {
  font-size: 21px;
  font-weight: 700;
  color: #1a202c;
  margin-bottom: 2px;
}
.header-desc {
  font-size: 13px;
  color: #718096;
}

.btn-publish {
  background: linear-gradient(135deg, #409eff, #1d4ed8);
  border: none !important;
  border-radius: 10px;
  padding: 0 20px !important;
  font-weight: 600;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
  transition: transform 0.2s, box-shadow 0.2s;
}
.btn-publish:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(64, 158, 255, 0.4);
}

/* ── 帖子卡片 ── */
.post-cards {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.post-card {
  background: #fff;
  border-radius: 14px;
  padding: 18px 22px;
  cursor: pointer;
  border: 1px solid rgba(0,0,0,0.05);
  transition: transform 0.2s, box-shadow 0.2s, border-color 0.2s;
}
.post-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 24px rgba(0,0,0,0.1);
  border-color: rgba(64, 158, 255, 0.25);
}

.post-card-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 10px;
}

.category-tag {
  background: rgba(64, 158, 255, 0.08);
  color: #409eff;
  border: none;
  font-weight: 500;
}

.post-time {
  font-size: 12px;
  color: #a0aec0;
}

.post-title {
  font-size: 16px;
  font-weight: 600;
  color: #1a202c;
  margin-bottom: 12px;
  line-height: 1.5;
}

.post-card-bottom {
  display: flex;
  gap: 20px;
}

.stat {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #a0aec0;
}

/* ── 分页 ── */
.pagination-wrap {
  display: flex;
  justify-content: flex-end;
  margin-top: 24px;
}

/* ── 发布对话框 ── */
:deep(.publish-dialog .el-dialog__header) {
  padding: 20px 24px 16px;
}
:deep(.publish-dialog .el-dialog__body) {
  padding: 20px 24px;
}
:deep(.publish-dialog .el-dialog__footer) {
  padding: 12px 24px 20px;
}
:deep(.el-form-item__label) {
  font-weight: 500;
  color: #4a5568;
}
</style>
