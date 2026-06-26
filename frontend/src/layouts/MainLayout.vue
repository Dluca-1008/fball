<template>
  <el-container class="main-container">
    <el-header class="header">
      <div class="logo" @click="$router.push('/')">足球社区</div>
      <el-menu
        :default-active="activeMenu"
        mode="horizontal"
        :router="true"
        class="nav-menu"
      >
        <el-menu-item index="/">首页</el-menu-item>
        <el-menu-item index="/teams">球队</el-menu-item>
        <el-menu-item index="/players">球员</el-menu-item>
        <el-menu-item index="/coaches">教练</el-menu-item>
        <el-menu-item index="/matches">赛事</el-menu-item>
        <el-menu-item index="/posts">社区</el-menu-item>
        <el-menu-item index="/products">商城</el-menu-item>
        <el-menu-item index="/orders" v-if="userStore.token">我的订单</el-menu-item>
        <el-menu-item index="/my-invitations" v-if="userStore.token">我的邀请</el-menu-item>
        <el-menu-item index="/chat" v-if="userStore.token">💬 消息</el-menu-item>
      </el-menu>
      <div class="header-right">
        <template v-if="userStore.token">
          <el-badge :value="cartCount" :hidden="cartCount === 0" class="cart-badge">
            <el-button @click="$router.push('/cart')">🛒 购物车</el-button>
          </el-badge>
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-avatar :size="32" :src="userStore.userInfo?.avatar">
                {{ userStore.userInfo?.nickname?.charAt(0) || 'U' }}
              </el-avatar>
              <span class="username">{{ userStore.userInfo?.nickname || userStore.userInfo?.username }}</span>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item command="changePassword">修改密码</el-dropdown-item>
                <el-dropdown-item command="admin" v-if="userStore.hasPermission('user:view')">管理后台</el-dropdown-item>
                <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
        <template v-else>
          <el-button type="primary" @click="$router.push('/login')">登录</el-button>
          <el-button @click="$router.push('/register')">注册</el-button>
        </template>
      </div>
    </el-header>
    <el-main class="main-content">
      <router-view />
    </el-main>
  </el-container>
</template>

<script setup>
import { computed, ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import request from '@/utils/request'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)
const cartCount = ref(0)

async function fetchCartCount() {
  if (!userStore.token) return
  try {
    const res = await request.get('/api/cart')
    cartCount.value = res.data.length
  } catch (error) {
    console.error(error)
  }
}

function handleCommand(command) {
  if (command === 'logout') {
    userStore.logout()
    router.push('/login')
  } else if (command === 'admin') {
    router.push('/admin')
  } else if (command === 'changePassword') {
    router.push('/change-password')
  }
}

onMounted(fetchCartCount)
</script>

<style scoped>
.main-container {
  min-height: 100vh;
}

.header {
  display: flex;
  align-items: center;
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 0 20px;
}

.logo {
  font-size: 20px;
  font-weight: bold;
  color: #409eff;
  cursor: pointer;
  margin-right: 40px;
}

.nav-menu {
  flex: 1;
  border-bottom: none;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.cart-badge {
  margin-right: 8px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.username {
  font-size: 14px;
}

.main-content {
  background: #f5f7fa;
  padding: 20px;
}
</style>
