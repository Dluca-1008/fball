<template>
  <div class="sidebar-layout">
    <!-- 左侧边栏 -->
    <div class="sidebar">
      <!-- Logo -->
      <div class="sidebar-logo" @click="handleLogoClick">
        <div class="logo-orb">⚽</div>
        <span class="logo-text">足球社区</span>
      </div>

      <!-- 导航菜单 -->
      <el-menu
        :default-active="activeMenu"
        :router="true"
        class="sidebar-menu"
        background-color="#0f1c2e"
        text-color="#8899aa"
        active-text-color="#ffffff"
      >
        <!-- 首页 -->
        <el-menu-item index="/app">
          <el-icon :class="{ 'is-active': activeMenu === '/' }"><HomeFilled /></el-icon>
          <span>首页</span>
        </el-menu-item>

        <!-- 社区 -->
        <el-sub-menu index="community">
          <template #title>
            <el-icon><ChatDotRound /></el-icon>
            <span>社区</span>
          </template>
          <el-menu-item index="/app/posts">
            <el-icon><Document /></el-icon>
            <span>论坛动态</span>
          </el-menu-item>
          <el-menu-item index="/app/chat">
            <el-icon><Message /></el-icon>
            <span>消息中心</span>
          </el-menu-item>
        </el-sub-menu>

        <!-- 商城 -->
        <el-sub-menu index="shop">
          <template #title>
            <el-icon><ShoppingBag /></el-icon>
            <span>商城</span>
          </template>
          <el-menu-item index="/app/shop/products">
            <el-icon><Goods /></el-icon>
            <span>商品列表</span>
          </el-menu-item>
          <el-menu-item index="/app/shop/orders">
            <el-icon><List /></el-icon>
            <span>我的订单</span>
          </el-menu-item>
          <el-menu-item index="/app/cart">
            <el-icon><ShoppingCart /></el-icon>
            <span>购物车</span>
          </el-menu-item>
        </el-sub-menu>

        <!-- 赛事 -->
        <el-sub-menu index="match">
          <template #title>
            <el-icon><Trophy /></el-icon>
            <span>赛事</span>
          </template>
          <el-menu-item index="/app/matches">
            <el-icon><Calendar /></el-icon>
            <span>赛事列表</span>
          </el-menu-item>
          <el-menu-item index="/app/teams">
            <el-icon><UserFilled /></el-icon>
            <span>球队列表</span>
          </el-menu-item>
          <el-menu-item index="/app/my-teams">
            <el-icon><HomeFilled /></el-icon>
            <span>我的球队</span>
          </el-menu-item>
          <el-menu-item index="/app/players">
            <el-icon><Avatar /></el-icon>
            <span>球员管理</span>
          </el-menu-item>
          <el-menu-item index="/app/coaches">
            <el-icon><School /></el-icon>
            <span>教练员管理</span>
          </el-menu-item>
          <el-menu-item index="/app/matches/create" v-if="userStore.hasRole('organizer')">
            <el-icon><Plus /></el-icon>
            <span>创建赛事</span>
          </el-menu-item>
          <el-menu-item index="/app/matches/friendly/sent" v-if="userStore.hasRole('team_admin')">
            <el-icon><ChatDotRound /></el-icon>
            <span>友谊赛邀请</span>
          </el-menu-item>
        </el-sub-menu>

        <!-- 管理后台 -->
        <el-sub-menu index="admin" v-if="userStore.hasRole('admin')">
          <template #title>
            <el-icon><Setting /></el-icon>
            <span>管理后台</span>
          </template>
          <el-menu-item index="/app/admin/users">
            <el-icon><UserFilled /></el-icon>
            <span>用户管理</span>
          </el-menu-item>
          <el-menu-item index="/app/admin/roles">
            <el-icon><Key /></el-icon>
            <span>角色管理</span>
          </el-menu-item>
        </el-sub-menu>

      </el-menu>

      <!-- 用户信息区 -->
      <div class="sidebar-footer" v-if="userStore.token">
        <div class="user-card" @click="$router.push('/app/profile')">
          <el-avatar :size="38" class="user-avatar">
            {{ userStore.userInfo?.nickname?.charAt(0) || userStore.userInfo?.username?.charAt(0) || 'U' }}
          </el-avatar>
          <div class="user-info">
            <div class="user-name">{{ userStore.userInfo?.nickname || userStore.userInfo?.username }}</div>
            <div class="user-status">
              <span class="status-dot" />在线
            </div>
          </div>
        </div>
        <el-button type="danger" size="small" class="logout-btn" @click="handleLogout">
          <el-icon><SwitchButton /></el-icon> 退出登录
        </el-button>
      </div>
    </div>

    <!-- 主内容区 -->
    <div class="main-area">
      <router-view v-slot="{ Component, route: r }">
        <transition name="page-fade" mode="out-in">
          <component :is="Component" :key="r.fullPath" />
        </transition>
      </router-view>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import {
  HomeFilled, ChatDotRound, ShoppingBag, Trophy, User,
  Document, Message, Goods, List, ShoppingCart,
  Calendar, UserFilled, Avatar, SwitchButton,
  Setting, Key, Plus, School
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const activeMenu = computed(() => resolveActiveMenu(route.path))

function handleLogoClick() {
  router.push(userStore.token ? '/app' : '/')
}

function handleLogout() {
  userStore.logout()
  router.push('/login')
}

const table = [
  { index: '/app',        match: '/app' },
  { index: '/app/posts', match: '/app/posts' },
  { index: '/app/chat',  match: '/app/chat' },
  { index: '/app/shop/products', match: '/app/shop/products' },
  { index: '/app/shop/orders',   match: '/app/shop/orders' },
  { index: '/app/cart',  match: '/app/cart' },
  { index: '/app/matches', match: '/app/matches' },
  { index: '/app/teams', match: '/app/teams' },
  { index: '/app/my-teams', match: '/app/my-teams' },
  { index: '/app/players', match: '/app/players' },
  { index: '/app/coaches', match: '/app/coaches' },
  { index: '/app/profile', match: '/app/profile' },
  { index: '/app/admin/users', match: '/app/admin/users' },
  { index: '/app/admin/roles', match: '/app/admin/roles' },
]

/**
 * 根据当前路由 path，计算侧边栏应高亮的菜单 index。
 * 精确匹配优先，其次按路径前缀（从长到短）查找。
 */
function resolveActiveMenu(path) {
  if (!path) return '/app'
  // 精确匹配
  const exact = table.find(({ match }) => match === path)
  if (exact) return exact.index
  // 前缀匹配（从长到短）
  const sorted = [...table].sort((a, b) => b.match.length - a.match.length)
  for (const { index, match } of sorted) {
    if (path.startsWith(match)) return index
  }
  return '/app'
}
</script>

<style scoped>
.sidebar-layout {
  display: flex;
  height: 100vh;
  overflow: hidden;
}

/* ═══════════════════════════════════════
   左侧边栏
═══════════════════════════════════════ */
.sidebar {
  width: 240px;
  min-width: 240px;
  background: #0f1c2e;
  display: flex;
  flex-direction: column;
  overflow-y: auto;
  overflow-x: hidden;
  position: relative;
  z-index: 10;
}

.sidebar::-webkit-scrollbar {
  width: 4px;
}
.sidebar::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.08);
  border-radius: 2px;
}

/* 渐变左侧条装饰 */
.sidebar::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 3px;
  height: 100%;
  background: linear-gradient(180deg, #409eff 0%, #1d4ed8 50%, #0f1c2e 100%);
  opacity: 0.8;
}

/* Logo */
.sidebar-logo {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 22px 20px;
  cursor: pointer;
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
  transition: background 0.2s;
}
.sidebar-logo:hover {
  background: rgba(255, 255, 255, 0.04);
}

.logo-orb {
  width: 38px;
  height: 38px;
  background: linear-gradient(135deg, #409eff, #1d4ed8);
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.35);
  transition: transform 0.3s;
  flex-shrink: 0;
}
.sidebar-logo:hover .logo-orb {
  transform: rotate(-12deg) scale(1.08);
}

.logo-text {
  font-size: 17px;
  font-weight: 700;
  color: #fff;
  letter-spacing: 1.5px;
}

/* 菜单 */
.sidebar-menu {
  flex: 1;
  padding: 8px 0;
  background: transparent;
  border: none;
}

.sidebar-menu:not(:deep(.el-menu--collapse)) {
  width: 240px;
}

:deep(.el-menu-item),
:deep(.el-sub-menu__title) {
  border-radius: 8px !important;
  margin: 3px 10px !important;
  height: 48px !important;
  line-height: 48px !important;
  padding-left: 16px !important;
  transition: all 0.2s ease !important;
}

:deep(.el-menu-item:hover),
:deep(.el-sub-menu__title:hover) {
  background: rgba(64, 158, 255, 0.15) !important;
  color: #93c5fd !important;
}

:deep(.el-menu-item.is-active) {
  background: linear-gradient(135deg, rgba(64, 158, 255, 0.35), rgba(29, 78, 216, 0.25)) !important;
  color: #fff !important;
  box-shadow: 0 2px 12px rgba(64, 158, 255, 0.2);
}

:deep(.el-sub-menu__title .el-icon),
:deep(.el-menu-item .el-icon) {
  font-size: 18px;
  margin-right: 8px;
}

/* 子菜单展开图标 */
:deep(.el-sub-menu__icon-arrow) {
  color: #8899aa;
}

/* 底栏用户区 */
.sidebar-footer {
  padding: 16px 14px;
  border-top: 1px solid rgba(255, 255, 255, 0.06);
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.user-card {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px 10px;
  border-radius: 10px;
  cursor: pointer;
  transition: background 0.2s;
}
.user-card:hover {
  background: rgba(255, 255, 255, 0.07);
}

.user-avatar {
  background: linear-gradient(135deg, #409eff, #7c3aed);
  font-weight: 600;
  font-size: 15px;
  flex-shrink: 0;
}

.user-info {
  display: flex;
  flex-direction: column;
  overflow: hidden;
  flex: 1;
}

.user-name {
  color: #e2e8f0;
  font-size: 13px;
  font-weight: 500;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.user-status {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 11px;
  color: #64748b;
  margin-top: 2px;
}

.status-dot {
  width: 6px;
  height: 6px;
  background: #22c55e;
  border-radius: 50%;
  box-shadow: 0 0 6px rgba(34, 197, 94, 0.6);
  animation: pulse-dot 2s infinite;
}

@keyframes pulse-dot {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

.logout-btn {
  width: 100%;
  color: #f87171 !important;
  font-size: 13px;
  padding: 8px 12px !important;
  border-radius: 6px;
  border: 1px solid rgba(248, 113, 113, 0.3) !important;
  background: rgba(248, 113, 113, 0.08) !important;
  transition: all 0.2s;
  font-weight: 500;
}
.logout-btn:hover {
  background: rgba(248, 113, 113, 0.2) !important;
  border-color: rgba(248, 113, 113, 0.5) !important;
  color: #fca5a5 !important;
}

/* ═══════════════════════════════════════
   主内容区
═══════════════════════════════════════ */
.main-area {
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
  background: #f0f2f5;
  padding: 24px;
}

/* ═══════════════════════════════════════
   页面过渡动画
═══════════════════════════════════════ */
.page-fade-enter-active {
  transition: opacity 0.25s ease, transform 0.25s ease;
}
.page-fade-leave-active {
  transition: opacity 0.15s ease, transform 0.15s ease;
}
.page-fade-enter-from {
  opacity: 0;
  transform: translateY(12px);
}
.page-fade-leave-to {
  opacity: 0;
  transform: translateY(-8px);
}
</style>
