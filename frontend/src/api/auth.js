import request from '@/utils/request'

export function login(data) {
  return request({
    url: '/api/auth/login',
    method: 'post',
    data
  })
}

export function register(data) {
  return request({
    url: '/api/auth/register',
    method: 'post',
    data
  })
}

export function getUserInfo() {
  return request({
    url: '/api/auth/info',
    method: 'get'
  })
}

export function getPermissions() {
  return request({
    url: '/api/auth/permissions',
    method: 'get'
  })
}

export function changePassword(data) {
  return request({
    url: '/api/auth/change-password',
    method: 'post',
    data
  })
}

export function updateUserInfo(data) {
  return request({
    url: '/api/auth/info',
    method: 'post',
    data
  })
}
