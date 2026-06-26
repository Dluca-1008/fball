<template>
  <div class="chat-container">
    <div class="chat-sidebar">
      <div class="sidebar-header">
        <h3>消息列表</h3>
      </div>
      <div class="chat-list">
        <div
          v-for="chat in chatList"
          :key="chat.userId"
          class="chat-item"
          :class="{ active: currentChat?.userId === chat.userId }"
          @click="selectChat(chat)"
        >
          <el-avatar :size="40">{{ chat.lastMessage?.senderName?.charAt(0) || 'U' }}</el-avatar>
          <div class="chat-info">
            <div class="chat-name">{{ chat.lastMessage?.senderName }}</div>
            <div class="chat-preview">{{ chat.lastMessage?.content }}</div>
          </div>
          <el-badge :value="chat.unreadCount" :hidden="chat.unreadCount === 0" class="unread-badge" />
        </div>
        <el-empty v-if="chatList.length === 0" description="暂无聊天记录" />
      </div>
    </div>

    <div class="chat-main" v-if="currentChat">
      <div class="chat-header">
        <h3>{{ currentChat.lastMessage?.senderName }}</h3>
      </div>
      <div class="message-list" ref="messageListRef">
        <div
          v-for="message in messages"
          :key="message.id"
          class="message-item"
          :class="{ 'is-self': message.senderId === currentUserId }"
        >
          <el-avatar :size="36">{{ message.senderName?.charAt(0) }}</el-avatar>
          <div class="message-content">
            <div class="message-text">{{ message.content }}</div>
            <div class="message-time">{{ formatTime(message.createdAt) }}</div>
          </div>
        </div>
      </div>
      <div class="chat-input">
        <el-input
          v-model="newMessage"
          type="textarea"
          :rows="2"
          placeholder="输入消息..."
          @keyup.enter="sendMessage"
        />
        <el-button type="primary" @click="sendMessage" :disabled="!newMessage.trim()">发送</el-button>
      </div>
    </div>

    <div class="chat-empty" v-else>
      <el-empty description="选择一个聊天" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick, computed } from 'vue'
import { useUserStore } from '@/stores/user'
import request from '@/utils/request'

const userStore = useUserStore()
const currentUserId = computed(() => userStore.userInfo?.id)

const chatList = ref([])
const currentChat = ref(null)
const messages = ref([])
const newMessage = ref('')
const messageListRef = ref(null)

let websocket = null

async function fetchChatList() {
  try {
    const res = await request.get('/api/chat/list')
    chatList.value = res.data
  } catch (error) {
    console.error(error)
  }
}

async function selectChat(chat) {
  currentChat.value = chat
  await fetchMessages(chat.userId)
  connectWebSocket()
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

function connectWebSocket() {
  if (websocket) {
    websocket.close()
  }

  const wsUrl = `ws://localhost:8080/ws/chat?userId=${currentUserId.value}`
  websocket = new WebSocket(wsUrl)

  websocket.onmessage = (event) => {
    const message = JSON.parse(event.data)
    if (message.senderId === currentChat.value?.userId || message.receiverId === currentChat.value?.userId) {
      messages.value.push(message)
      scrollToBottom()
    }
    fetchChatList()
  }

  websocket.onclose = () => {
    console.log('WebSocket连接关闭')
  }
}

function sendMessage() {
  if (!newMessage.value.trim() || !websocket) return

  const message = {
    receiverId: currentChat.value.userId,
    content: newMessage.value
  }

  websocket.send(JSON.stringify(message))
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

onMounted(fetchChatList)

onUnmounted(() => {
  if (websocket) {
    websocket.close()
  }
})
</script>

<style scoped>
.chat-container {
  display: flex;
  height: calc(100vh - 120px);
  background: white;
  border-radius: 8px;
  overflow: hidden;
}

.chat-sidebar {
  width: 300px;
  border-right: 1px solid #eee;
  display: flex;
  flex-direction: column;
}

.sidebar-header {
  padding: 16px;
  border-bottom: 1px solid #eee;
}

.chat-list {
  flex: 1;
  overflow-y: auto;
}

.chat-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  cursor: pointer;
  transition: background 0.2s;
}

.chat-item:hover {
  background: #f5f5f5;
}

.chat-item.active {
  background: #ecf5ff;
}

.chat-info {
  flex: 1;
  overflow: hidden;
}

.chat-name {
  font-weight: bold;
  margin-bottom: 4px;
}

.chat-preview {
  color: #999;
  font-size: 12px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.chat-main {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.chat-header {
  padding: 16px;
  border-bottom: 1px solid #eee;
}

.message-list {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
}

.message-item {
  display: flex;
  gap: 8px;
  margin-bottom: 16px;
}

.message-item.is-self {
  flex-direction: row-reverse;
}

.message-content {
  max-width: 70%;
}

.message-item.is-self .message-content {
  text-align: right;
}

.message-text {
  background: #f5f5f5;
  padding: 8px 12px;
  border-radius: 8px;
  display: inline-block;
  text-align: left;
}

.message-item.is-self .message-text {
  background: #409eff;
  color: white;
}

.message-time {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}

.chat-input {
  padding: 16px;
  border-top: 1px solid #eee;
  display: flex;
  gap: 12px;
}

.chat-input .el-textarea {
  flex: 1;
}

.chat-empty {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>
