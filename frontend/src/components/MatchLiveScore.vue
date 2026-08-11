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
import { useWebSocket } from '@/composables/useWebSocket'

const props = defineProps({
  matchId: { type: [Number, String], required: true }
})

const emit = defineEmits(['update'])

const match = ref(null)

function getMatchWebSocketUrl() {
  const token = localStorage.getItem('token')
  const base = import.meta.env.VITE_WS_URL || 'ws://localhost:8080'
  return `${base}/ws/match/${props.matchId}?token=${token}`
}

const { connect, send, onMessage, close } = useWebSocket(getMatchWebSocketUrl, { reconnectDelay: 3000, autoReconnect: true })

onMessage((data) => {
  match.value = data
  emit('update', data)
})

watch(() => props.matchId, () => {
  connect()
})

onMounted(connect)

onUnmounted(close)
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
