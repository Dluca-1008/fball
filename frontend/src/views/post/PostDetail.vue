<template>
  <div class="page-shell" v-loading="loading">
    <el-card class="post-hero" v-if="post">
      <!-- 帖子头部 -->
      <div class="post-header">
        <el-tag class="category-tag">{{ post.category || '未分类' }}</el-tag>
        <h1 class="post-title">{{ post.title }}</h1>
      </div>

      <!-- 元信息 -->
      <div class="post-meta">
        <span class="meta-item"><el-icon><View /></el-icon> {{ post.viewCount || 0 }} 浏览</span>
        <span class="meta-dot">·</span>
        <span class="meta-item"><el-icon><Calendar /></el-icon> {{ post.createdAt?.split(' ')[0] || '-' }}</span>
      </div>

      <el-divider class="post-divider" />

      <!-- 正文 -->
      <div class="post-content">{{ post.content }}</div>

      <el-divider class="post-divider" />

      <!-- 互动按钮 -->
      <div class="post-actions">
        <el-button
          :type="liked ? 'primary' : ''"
          class="action-btn"
          :class="{ active: liked }"
          @click="likePost"
        >
          <el-icon><Star /></el-icon>
          {{ liked ? '已点赞' : '点赞' }} · {{ likeCount }}
        </el-button>
        <el-button
          :type="favorited ? 'warning' : ''"
          class="action-btn"
          :class="{ active: favorited }"
          @click="favoritePost"
        >
          <el-icon><Star /></el-icon>
          {{ favorited ? '已收藏' : '收藏' }} · {{ favoriteCount }}
        </el-button>
      </div>
    </el-card>

    <!-- 评论区 -->
    <el-card class="comment-card" v-if="post">
      <template #header>
        <div class="card-title-bar">
          <span class="card-icon">💬</span>
          <span>评论区</span>
          <span class="card-count">{{ post.commentCount || 0 }} 条评论</span>
        </div>
      </template>
      <CommentList :postId="route.params.id" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'
import CommentList from '@/components/CommentList.vue'
import { View, Calendar, Star } from '@element-plus/icons-vue'

const route = useRoute()

const loading = ref(false)
const post = ref(null)
const liked = ref(false)
const favorited = ref(false)
const likeCount = ref(0)
const favoriteCount = ref(0)

async function fetchPost() {
  loading.value = true
  try {
    const res = await request.get(`/api/posts/${route.params.id}`)
    post.value = res.data.post
    liked.value = res.data.liked || false
    favorited.value = res.data.favorited || false
    likeCount.value = post.value.likeCount || 0
  } finally {
    loading.value = false
  }
}

async function likePost() {
  try {
    const res = await request.post(`/api/posts/${route.params.id}/like`)
    liked.value = res.data.liked
    likeCount.value = res.data.count
  } catch (error) {
    console.error(error)
  }
}

async function favoritePost() {
  try {
    const res = await request.post(`/api/posts/${route.params.id}/favorite`)
    favorited.value = res.data.favorited
    favoriteCount.value = res.data.count
  } catch (error) {
    console.error(error)
  }
}

onMounted(fetchPost)
</script>

<style scoped>
.page-shell { max-width: 800px; margin: 0 auto; display: flex; flex-direction: column; gap: 16px; }

.post-hero { overflow: hidden; }

.post-header { display: flex; align-items: flex-start; gap: 12px; margin-bottom: 12px; }
.category-tag {
  background: rgba(64, 158, 255, 0.08);
  color: #409eff;
  border: none;
  font-weight: 500;
  flex-shrink: 0;
}
.post-title {
  font-size: 24px;
  font-weight: 800;
  color: #1a202c;
  line-height: 1.4;
  margin: 0;
}

.post-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: #a0aec0;
}
.meta-item { display: flex; align-items: center; gap: 4px; }
.meta-dot { color: #e2e8f0; }

.post-divider { margin: 20px 0; }

.post-content {
  font-size: 15px;
  line-height: 1.9;
  color: #2d3748;
  white-space: pre-wrap;
}

.post-actions { display: flex; gap: 12px; }
.action-btn {
  border-radius: 10px !important;
  padding: 8px 20px !important;
  font-weight: 500 !important;
  border: 2px solid #e2e8f0 !important;
  background: #fff !important;
  color: #4a5568 !important;
  transition: all 0.2s;
}
.action-btn:hover { border-color: #409eff !important; color: #409eff !important; }
.action-btn.active {
  background: rgba(64, 158, 255, 0.08) !important;
  border-color: #409eff !important;
  color: #409eff !important;
}

.comment-card { overflow: hidden; }
.card-title-bar { display: flex; align-items: center; gap: 8px; font-size: 15px; font-weight: 600; color: #2d3748; }
.card-icon { font-size: 18px; }
.card-count { margin-left: auto; font-size: 12px; color: #a0aec0; font-weight: 400; }
</style>
