<template>
  <div class="post-detail" v-loading="loading">
    <el-card v-if="post">
      <h2>{{ post.title }}</h2>
      <div class="post-meta">
        <span>分类: {{ post.category || '未分类' }}</span>
        <span>浏览: {{ post.viewCount }}</span>
        <span>点赞: {{ post.likeCount }}</span>
        <span>发布时间: {{ post.createdAt }}</span>
      </div>
      <el-divider />
      <div class="post-content">{{ post.content }}</div>
      <el-divider />
      <div class="post-actions">
        <el-button :type="liked ? 'primary' : ''" @click="likePost">
          👍 点赞 {{ likeCount }}
        </el-button>
        <el-button :type="favorited ? 'warning' : ''" @click="favoritePost">
          ⭐ 收藏 {{ favoriteCount }}
        </el-button>
      </div>
    </el-card>

    <el-card class="comment-section" v-if="post">
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
.post-detail {
  max-width: 800px;
  margin: 0 auto;
}

.post-meta {
  display: flex;
  gap: 20px;
  color: #666;
  font-size: 14px;
  margin-top: 12px;
}

.post-content {
  line-height: 1.8;
  color: #333;
  white-space: pre-wrap;
}

.post-actions {
  display: flex;
  gap: 12px;
}

.comment-section {
  margin-top: 20px;
}
</style>
