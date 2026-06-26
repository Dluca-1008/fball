import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login, register, getUserInfo, getPermissions } from '@/api/auth'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(null)
  const permissions = ref([])

  async function loginAction(username, password) {
    const res = await login({ username, password })
    token.value = res.data.token
    userInfo.value = res.data.user
    localStorage.setItem('token', res.data.token)
    await fetchPermissions()
    return res
  }

  async function registerAction(data) {
    const res = await register(data)
    return res
  }

  async function fetchUserInfo() {
    const res = await getUserInfo()
    userInfo.value = res.data
    return res
  }

  async function fetchPermissions() {
    const res = await getPermissions()
    permissions.value = res.data
    return res
  }

  function hasPermission(code) {
    return permissions.value.includes(code)
  }

  function logout() {
    token.value = ''
    userInfo.value = null
    permissions.value = []
    localStorage.removeItem('token')
  }

  return {
    token,
    userInfo,
    permissions,
    loginAction,
    registerAction,
    fetchUserInfo,
    fetchPermissions,
    hasPermission,
    logout
  }
})
