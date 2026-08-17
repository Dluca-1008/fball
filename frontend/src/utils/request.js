import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import router from '@/router'

const service = axios.create({
  baseURL: '',
  timeout: 15000
})

service.interceptors.request.use(
  config => {
    const userStore = useUserStore()
    if (userStore.token) {
      config.headers['Authorization'] = `Bearer ${userStore.token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

service.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code !== 200) {
      const silent = response.config?.silent
      if (!silent) {
        ElMessage.error(res.message || '请求失败')
      }
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    return res
  },
  error => {
    const silent = error?.config?.silent
    if (error.response) {
      const { status } = error.response
      if (status === 401) {
        const userStore = useUserStore()
        userStore.logout()
        router.push('/login')
        ElMessage.error('登录已过期，请重新登录')
      } else if (!silent) {
        if (status === 403) {
          ElMessage.error('没有权限访问')
        } else {
          ElMessage.error(error.response.data?.message || '请求失败')
        }
      }
    } else if (!silent) {
      ElMessage.error('网络错误')
    }
    return Promise.reject(error)
  }
)

export default service
