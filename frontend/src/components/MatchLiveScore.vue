<template>
  <div class="live-score">
    <div class="score-card" v-if="match">
      <div class="team home">
        <div class="team-name">{{ match.homeTeamName }}</div>
        <div class="score">{{ match.homeScore }}</div>
      </div>
      <div class="vs">
        <div class="status">{{ getStatusText(match.status) }}</div>
        <div class="time" v-if="match.status === 1">进行中</div>
      </div>
      <div class="team away">
        <div class="score">{{ match.awayScore }}</div>
        <div class="team-name">{{ match.awayTeamName }}</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch } from 'vue'

const props = defineProps({
  matchId: { type: [Number, String], required: true }
})

const emit = defineEmits(['update'])

const match = ref(null)
let websocket = null

function connectWebSocket() {
  const wsUrl = `ws://localhost:8080/ws/match/${props.matchId}`
  websocket = new WebSocket(wsUrl)

  websocket.onmessage = (event) => {
    const data = JSON.parse(event.data)
    match.value = data
    emit('update', data)
  }

  websocket.onclose = () => {
    console.log('WebSocket连接关闭')
    setTimeout(connectWebSocket, 3000)
  }

  websocket.onerror = (error) => {
    console.error('WebSocket错误:', error)
  }
}

function getStatusText(status) {
  const texts = { 0: '未开始', 1: '进行中', 2: '已结束' }
  return texts[status] || '未知'
}

watch(() => props.matchId, () => {
  if (websocket) {
    websocket.close()
  }
  connectWebSocket()
})

onMounted(connectWebSocket)

onUnmounted(() => {
  if (websocket) {
    websocket.close()
  }
})
</script>

<style scoped>
.live-score {
  margin: 16px 0;
}

.score-card {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 40px;
  padding: 24px;
  background: linear-gradient(135deg, #1a73e8 0%, #4285f4 100%);
  border-radius: 12px;
  color: white;
}

.team {
  display: flex;
  align-items: center;
  gap: 16px;
}

.team-name {
  font-size: 18px;
  font-weight: bold;
}

.score {
  font-size: 48px;
  font-weight: bold;
}

.vs {
  text-align: center;
}

.status {
  font-size: 14px;
  opacity: 0.9;
}

.time {
  font-size: 12px;
  opacity: 0.7;
}
</style>
