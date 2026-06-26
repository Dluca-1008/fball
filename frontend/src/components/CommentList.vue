<template>
  <div class="comment-list">
    <div class="comment-header">
      <h4>评论 ({{ total }})</h4>
    </div>
    <div class="comment-input">
      <el-input v-model="newComment" type="textarea" :rows="3" placeholder="写下你的评论..." />
      <el-button type="primary" @click="submitComment" :loading="submitting" style="margin-top: 8px;">发布评论</el-button>
    </div>
    <div class="comments">
      <div v-for="comment in comments" :key="comment.id" class="comment-item">
        <div class="comment-avatar">
          <el-avatar :size="40" :src="comment.authorAvatar">{{ comment.authorName?.charAt(0) }}</el-avatar>
        </div>
        <div class="comment-content">
          <div class="comment-author">{{ comment.authorName }}</div>
          <div class="comment-text">{{ comment.content }}</div>
          <div class="comment-meta">
            <span>{{ comment.createdAt }}</span>
            <el-button type="primary" link @click="likeComment(comment)">
              👍 {{ comment.likeCount || 0 }}
            </el-button>
          </div>
        </div>
      </div>
      <el-empty v-if="comments.length === 0" description="暂无评论" />
    </div>
    <el-pagination
      v-model:current-page="page"
      v-model:page-size="pageSize"
      :page-sizes="[10, 20, 50]"
      :total="total"
      layout="total, prev, pager, next"
      @current-change="fetchComments"
    />
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import request from '@/utils/request'

const props = defineProps({
  postId: { type: [Number, String], required: true }
})

const userStore = useUserStore()

const comments = ref([])
const newComment = ref('')
const submitting = ref(false)
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)

async function fetchComments() {
  try {
    const res = await request.get('/api/comments', {
      params: { postId: props.postId, page: page.value, size: pageSize.value }
    })
    comments.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    console.error(error)
  }
}

async function submitComment() {
  if (!userStore.token) {
    ElMessage.warning('请先登录')
    return
  }

  if (!newComment.value.trim()) {
    ElMessage.warning('请输入评论内容')
    return
  }

  submitting.value = true
  try {
    await request.post('/api/comments', {
      postId: props.postId,
      content: newComment.value
    })
    ElMessage.success('评论成功')
    newComment.value = ''
    fetchComments()
  } finally {
    submitting.value = false
  }
}

async function likeComment(comment) {
  try {
    await request.post(`/api/comments/${comment.id}/like`)
    comment.likeCount = (comment.likeCount || 0) + 1
  } catch (error) {
    console.error(error)
  }
}

watch(() => props.postId, fetchComments)

onMounted(fetchComments)
</script>

<style scoped>
.comment-list {
  margin-top: 20px;
}

.comment-header {
  margin-bottom: 16px;
}

.comment-input {
  margin-bottom: 20px;
}

.comment-item {
  display: flex;
  gap: 12px;
  padding: 16px 0;
  border-bottom: 1px solid #eee;
}

.comment-content {
  flex: 1;
}

.comment-author {
  font-weight: bold;
  margin-bottom: 4px;
}

.comment-text {
  color: #333;
  line-height: 1.6;
  margin-bottom: 8px;
}

.comment-meta {
  display: flex;
  justify-content: space-between;
  color: #999;
  font-size: 12px;
}
</style>
