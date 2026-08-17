<template>
  <div class="profile-page">
    <!-- 用户信息卡片 -->
    <el-card class="user-card">
      <div class="user-card-top">
        <el-avatar :size="80" class="user-avatar">
          {{ user.nickname?.charAt(0) || user.username?.charAt(0) || 'U' }}
        </el-avatar>
        <div class="user-meta">
          <div class="user-name-row">
            <span class="user-name">{{ user.nickname || user.username }}</span>
            <el-tag v-if="user.gender === 1" type="primary" size="small" round>男</el-tag>
            <el-tag v-else-if="user.gender === 0" type="danger" size="small" round>女</el-tag>
          </div>
          <div class="user-username">@{{ user.username }}</div>
          <div class="user-email" v-if="user.email">
            <el-icon><Message /></el-icon> {{ user.email }}
          </div>
        </div>
      </div>

      <el-divider style="margin: 20px 0 16px" />

      <div class="user-details">
        <div class="detail-row">
          <span class="detail-label">注册时间</span>
          <span class="detail-value">{{ formatDate(user.createdAt) }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">上次登录</span>
          <span class="detail-value">{{ formatDate(user.lastLoginTime) || '暂无' }}</span>
        </div>
        <div class="detail-row" v-if="roles.length">
          <span class="detail-label">角色</span>
          <div class="detail-value roles-wrap">
            <el-tag
              v-for="role in roles"
              :key="role.roleCode"
              :type="role.roleCode === 'admin' ? 'danger' : 'warning'"
              size="small"
              effect="dark"
              round
            >{{ role.roleName }}</el-tag>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 功能按钮 -->
    <div class="action-buttons">
      <div
        class="action-btn"
        :class="{ active: activeView === 'info' }"
        @click="activeView = 'info'"
      >
        <el-icon><UserFilled /></el-icon> 基本信息
      </div>
      <div
        class="action-btn"
        :class="{ active: activeView === 'player' }"
        @click="activeView = 'player'"
      >
        <el-icon><User /></el-icon> 球员注册
      </div>
      <div
        class="action-btn"
        :class="{ active: activeView === 'invitations' }"
        @click="activeView = 'invitations'"
      >
        <el-icon><Postcard /></el-icon> 我的邀请
      </div>
      <div
        class="action-btn"
        :class="{ active: activeView === 'password' }"
        @click="activeView = 'password'"
      >
        <el-icon><Lock /></el-icon> 修改密码
      </div>
    </div>

    <!-- 子页面 -->
    <div class="sub-page">
      <ProfileInfo v-if="activeView === 'info'" :user="user" />
      <PlayerRegister v-if="activeView === 'player'" />
      <MyInvitations v-if="activeView === 'invitations'" />
      <ChangePassword v-if="activeView === 'password'" />
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useUserStore } from '@/stores/user'
import { Message, UserFilled, Postcard, Lock, User } from '@element-plus/icons-vue'
import ProfileInfo from './ProfileInfo.vue'
import MyInvitations from '@/views/team/MyInvitations.vue'
import ChangePassword from '@/views/auth/ChangePassword.vue'
import PlayerRegister from './PlayerRegister.vue'

const userStore = useUserStore()

const activeView = ref('info')

const user = computed(() => userStore.userInfo || {})
const roles = computed(() => user.value?.roles || [])

function formatDate(dateStr) {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  return d.toLocaleDateString('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit' })
}
</script>

<style scoped>
.profile-page {
  max-width: 720px;
  margin: 0 auto;
}

/* ── 用户信息卡片 ── */
.user-card {
  margin-bottom: 24px;
}

.user-card-top {
  display: flex;
  align-items: flex-start;
  gap: 20px;
}

.user-avatar {
  width: 80px;
  height: 80px;
  font-size: 32px;
  font-weight: 700;
  flex-shrink: 0;
  background: linear-gradient(135deg, #409eff, #7c3aed);
}

.user-meta {
  flex: 1;
  min-width: 0;
}

.user-name-row {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 4px;
}

.user-name {
  font-size: 22px;
  font-weight: 700;
  color: #1a202c;
}

.user-username {
  font-size: 14px;
  color: #718096;
  margin-bottom: 6px;
}

.user-email {
  font-size: 13px;
  color: #a0aec0;
  display: flex;
  align-items: center;
  gap: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* ── 详情行 ── */
.user-details {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.detail-row {
  display: flex;
  align-items: center;
  gap: 16px;
  font-size: 14px;
}

.detail-label {
  color: #718096;
  width: 70px;
  flex-shrink: 0;
}

.detail-value {
  color: #2d3748;
  flex: 1;
}

.roles-wrap {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

/* ── 功能按钮 ── */
.action-buttons {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
}

.action-btn {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  padding: 12px 0;
  background: #fff;
  border: 2px solid #e2e8f0;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 600;
  color: #4a5568;
  cursor: pointer;
  transition: all 0.2s;
}

.action-btn:hover {
  border-color: #409eff;
  color: #409eff;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.15);
}

.action-btn.active {
  border-color: #409eff;
  background: linear-gradient(135deg, rgba(64, 158, 255, 0.08), rgba(29, 78, 216, 0.05));
  color: #1d4ed8;
}

.action-btn .el-icon {
  font-size: 16px;
}

/* ── 子页面容器 ── */
.sub-page {
  background: #fff;
  border-radius: 14px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.sub-page :deep(.page-header) {
  margin-bottom: 20px;
}
</style>
