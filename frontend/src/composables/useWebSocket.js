import { ref, watch } from 'vue'

export function useWebSocket(urlGetter, options = {}) {
  const { reconnectDelay = 3000, reconnectMaxRetries = 5, autoReconnect = true, heartbeatInterval = 30000 } = options
  const ws = ref(null)
  const isConnected = ref(false)
  const reconnectAttempts = ref(0)
  let messageListeners = []
  let heartbeatTimer = null
  let reconnectTimer = null

  function onMessage(fn) {
    messageListeners.push(fn)
    return () => { const i = messageListeners.indexOf(fn); if (i > -1) messageListeners.splice(i, 1) }
  }
  function send(data) {
    if (ws.value && ws.value.readyState === WebSocket.OPEN) {
      ws.value.send(typeof data === 'string' ? data : JSON.stringify(data))
    }
  }
  function close() {
    clearTimeout(heartbeatTimer); clearTimeout(reconnectTimer); reconnectAttempts.value = 0
    try { ws.value?.close() } catch {}
    ws.value = null; isConnected.value = false
  }
  function sendHeartbeat() {
    if (ws.value && ws.value.readyState === WebSocket.OPEN) ws.value.send(JSON.stringify({ type: 'ping' }))
  }
  function connect() {
    close()
    const url = typeof urlGetter === 'function' ? urlGetter() : urlGetter
    const socket = new WebSocket(url)
    ws.value = socket
    socket.onopen = () => { isConnected.value = true; reconnectAttempts.value = 0; if (heartbeatInterval > 0) { sendHeartbeat(); heartbeatTimer = setInterval(sendHeartbeat, heartbeatInterval) } }
    socket.onmessage = (event) => { let data; try { data = JSON.parse(event.data) } catch { data = event.data } messageListeners.forEach(fn => fn(data)) }
    socket.onclose = (event) => {
      isConnected.value = false; clearTimeout(heartbeatTimer); ws.value = null
      if (!event.wasClean && autoReconnect && reconnectMaxRetries !== 0) {
        if (reconnectMaxRetries < 0 || reconnectAttempts.value < reconnectMaxRetries) { reconnectAttempts.value++; reconnectTimer = setTimeout(connect, reconnectDelay) }
      }
    }
    socket.onerror = () => {}
  }
  if (typeof urlGetter === 'function') {
    watch(urlGetter, () => { if (isConnected.value) connect() }, { deep: true })
  }
  return { connect, send, onMessage, close, isConnected }
}
