import request from '@/utils/request'

// 获取球员列表
export function getPlayers(params) {
  return request.get('/api/players', { params })
}

// 获取当前用户的球员信息
export function getMyPlayer() {
  return request.get('/api/players/my')
}

// 注册为球员
export function registerPlayer(data) {
  return request.post('/api/players/register', data)
}
