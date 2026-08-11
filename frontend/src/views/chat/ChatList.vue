<template>
  <div class="chat-page">
    <div class="chat-container">
      <!-- 侧边栏 -->
      <div class="chat-sidebar">
        <div class="sidebar-header">
          <h3>💬 消息</h3>
        </div>
        <div class="chat-list" v-loading="loading">
          <div
            v-for="chat in chatList"
            :key="chat.userId"
            class="chat-item"
            :class="{ active: currentChat?.userId === chat.userId }"
            @click="selectChat(chat)"
          >
            <el-avatar :size="44" class="chat-avatar">
              {{ chat.lastMessage?.senderName?.charAt(0) || 'U' }}
            </el-avatar>
            <div class="chat-info">
              <div class="chat-name-row">
                <span class="chat-name">{{ chat.lastMessage?.senderName }}</span>
                <el-badge :value="chat.unreadCount" :hidden="chat.unreadCount === 0" class="unread-badge" />
              </div>
              <div class="chat-preview">{{ chat.lastMessage?.content || '暂无消息' }}</div>
            </div>
          </div>
          <el-empty v-if="!loading && chatList.length === 0" description="暂无聊天记录" :image-size="60" />
        </div>
      </div>

      <!-- 聊天主区域 -->
      <div class="chat-main" v-if="currentChat">
        <div class="chat-header">
          <el-avatar :size="40">{{ currentChat.lastMessage?.senderName?.charAt(0) }}</el-avatar>
          <div class="chat-header-info">
            <div class="chat-header-name">{{ currentChat.lastMessage?.senderName }}</div>
            <div class="chat-header-status">在线</div>
          </div>
        </div>

        <div class="message-list" ref="messageListRef">
          <div
            v-for="message in messages"
            :key="message.id"
            class="message-item"
            :class="{ 'is-self': message.senderId === currentUserId }"
          >
            <el-avatar :size="36" class="msg-avatar">
              {{ message.senderName?.charAt(0) }}
            </el-avatar>
            <div class="message-bubble">
              <div class="message-text">{{ message.content }}</div>
              <div class="message-time">{{ formatTime(message.createdAt) }}</div>
            </div>
          </div>
        </div>

        <div class="chat-input-area">
          <el-input
            v-model="newMessage"
            type="textarea"
            :rows="3"
            placeholder="输入消息，按 Enter 发送"
            :disabled="!currentChat"
            class="input-textarea"
            @keyup.enter.exact="sendMessage"
          />
          <el-button
            type="primary"
            class="btn-send"
            :disabled="!newMessage.trim() || !currentChat"
            @click="sendMessage"
          >
            <el-icon><Promotion /></el-icon> 发送
          </el-button>
        </div>
      </div>

      <!-- 空状态 -->
      <div class="chat-empty" v-else>
        <div class="empty-content">
          <div class="empty-icon">💬</div>
          <p class="empty-title">选择一个聊天</p>
          <p class="empty-desc">开始一段对话</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick, computed } from 'vue'
import { useUserStore } from '@/stores/user'
import request from '@/utils/request'
import { useWebSocket } from '@/composables/useWebSocket'
import { Promotion } from '@element-plus/icons-vue'

const userStore = useUserStore()
const currentUserId = computed(() => userStore.userInfo?.id)

const loading = ref(false)
const chatList = ref([])
const currentChat = ref(null)
const messages = ref([])
const newMessage = ref('')
const messageListRef = ref(null)

async function fetchChatList() {
  loading.value = true
  try {
    const res = await request.get('/api/chat/list')
    chatList.value = res.data
  } finally {
    loading.value = false
  }
}

function getChatWebSocketUrl() {
  const token = localStorage.getItem('token')
  const base = import.meta.env.VITE_WS_URL || 'ws://localhost:8080'
  return `${base}/ws/chat?token=${token}`
}

const { connect: connectWs, send: sendWs, onMessage, close: closeWs } = useWebSocket(getChatWebSocketUrl, { reconnectDelay: 3000, autoReconnect: true })

async function selectChat(chat) {
  currentChat.value = chat
  await fetchMessages(chat.userId)
  connectWs()
}

async function fetchMessages(otherUserId) {
  try {
    const res = await request.get(`/api/chat/history/${otherUserId}`)
    messages.value = res.data.records.reverse()
    await nextTick()
    scrollToBottom()
  } catch (error) {
    console.error(error)
  }
}

onMessage((message) => {
  if (message.senderId === currentChat.value?.userId || message.receiverId === currentChat.value?.userId) {
    messages.value.push(message)
    scrollToBottom()
  }
  fetchChatList()
})

function sendMessage() {
  if (!newMessage.value.trim() || !currentChat.value) return
  const message = {
    receiverId: currentChat.value.userId,
    content: newMessage.value
  }
  sendWs(JSON.stringify(message))
  newMessage.value = ''
}

function scrollToBottom() {
  if (messageListRef.value) {
    messageListRef.value.scrollTop = messageListRef.value.scrollHeight
  }
}

function formatTime(time) {
  if (!time) return ''
  const date = new Date(time)
  return date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
}

onMounted(() => { fetchChatList(); connectWs() })
onUnmounted(closeWs)
</script>

<style scoped>
.chat-page { height: 100%; }

.chat-container {
  display: flex;
  height: calc(100vh - 120px);
  background: #fff;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
}

/* ── 侧边栏 ── */
.chat-sidebar {
  width: 300px;
  border-right: 1px solid #f0f0f0;
  display: flex;
  flex-direction: column;
  background: #fafbfc;
}

.sidebar-header {
  padding: 18px 20px;
  border-bottom: 1px solid #f0f0f0;
}
.sidebar-header h3 {
  font-size: 16px;
  font-weight: 700;
  color: #1a202c;
  margin: 0;
}

.chat-list {
  flex: 1;
  overflow-y: auto;
  padding: 8px 0;
}

.chat-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  cursor: pointer;
  transition: background 0.2s;
  border-left: 3px solid transparent;
}
.chat-item:hover { background: #f0f4ff; }
.chat-item.active {
  background: #f0f4ff;
  border-left-color: #409eff;
}

.chat-avatar {
  background: linear-gradient(135deg, #409eff, #1d4ed8) !important;
  font-size: 16px !important;
  font-weight: 700;
  flex-shrink: 0;
}

.chat-info { flex: 1; min-width: 0; }
.chat-name-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 2px;
}
.chat-name { font-weight: 600; font-size: 14px; color: #1a202c; }
.unread-badge { flex-shrink: 0; }
.chat-preview { font-size: 12px; color: #a0aec0; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }

/* ── 聊天主区域 ── */
.chat-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: #fff;
}

.chat-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px 20px;
  border-bottom: 1px solid #f0f0f0;
  background: #fff;
}
.chat-header-info { display: flex; flex-direction: column; }
.chat-header-name { font-size: 15px; font-weight: 600; color: #1a202c; }
.chat-header-status { font-size: 12px; color: #67c23a; }

.message-list {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.message-item {
  display: flex;
  gap: 10px;
  align-items: flex-end;
}
.message-item.is-self {
  flex-direction: row-reverse;
}

.msg-avatar {
  background: linear-gradient(135deg, #667eea, #764ba2) !important;
  font-size: 14px !important;
  font-weight: 700;
  flex-shrink: 0;
}

.message-bubble {
  max-width: 70%;
}
.message-item.is-self .message-bubble {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}

.message-text {
  background: #f0f4ff;
  color: #2d3748;
  padding: 10px 14px;
  border-radius: 12px;
  border-top-left-radius: 4px;
  font-size: 14px;
  line-height: 1.5;
  word-break: break-word;
}
.message-item.is-self .message-text {
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: #fff;
  border-radius: 12px;
  border-top-right-radius: 4px;
}

.message-time {
  font-size: 11px;
  color: #a0aec0;
  margin-top: 4px;
  padding: 0 4px;
}

.chat-input-area {
  padding: 16px 20px;
  border-top: 1px solid #f0f0f0;
  display: flex;
  gap: 12px;
  align-items: flex-end;
  background: #fff;
}
.input-textarea { flex: 1; }
.btn-send {
  border-radius: 10px !important;
  padding: 0 24px !important;
  background: linear-gradient(135deg, #667eea, #764ba2) !important;
  border: none !important;
  font-weight: 600 !important;
  height: auto !important;
  align-self: flex-end;
}
.btn-send:hover:not(:disabled) { box-shadow: 0 4px 14px rgba(102, 126, 234, 0.4); }

/* ── 空状态 ── */
.chat-empty {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fafbfc;
}
.empty-content { text-align: center; }
.empty-icon { font-size: 56px; margin-bottom: 16px; }
.empty-title { font-size: 18px; font-weight: 600; color: #1a202c; margin-bottom: 6px; }
.empty-desc { font-size: 14px; color: #a0aec0; }
</style>
