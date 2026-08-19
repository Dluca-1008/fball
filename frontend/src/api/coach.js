import request from '@/utils/request'

// 获取教练列表
export function getCoaches(params) {
  return request.get('/api/coaches', { params })
}

// 获取当前用户的教练信息
export function getMyCoach() {
  return request.get('/api/coaches/my')
}

// 注册为教练
export function registerCoach(data) {
  return request.post('/api/coaches/register', data)
}
