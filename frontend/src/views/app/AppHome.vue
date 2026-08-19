<template>
  <div class="dashboard">
    <!-- 欢迎横幅 -->
    <div class="hero">
      <div class="hero-bg-orbs">
        <div class="orb orb-1" />
        <div class="orb orb-2" />
        <div class="orb orb-3" />
      </div>
      <div class="hero-content">
        <div class="hero-badge">⚽ 欢迎来到足球社区</div>
        <h1 class="hero-title">Hi, {{ userStore.userInfo?.nickname || userStore.userInfo?.username }}!</h1>
        <p class="hero-subtitle">探索球队、参与赛事、交流互动</p>
      </div>
    </div>

    <!-- 快捷功能入口 -->
    <div class="section">
      <div class="section-header">
        <h2>快捷功能</h2>
        <p>常用操作，一键直达</p>
      </div>
      <el-row :gutter="16">
        <el-col :span="6" v-for="item in quickActions" :key="item.path">
          <div class="quick-card" @click="$router.push(item.path)">
            <span class="quick-icon">{{ item.icon }}</span>
            <span class="quick-label">{{ item.label }}</span>
            <span class="quick-desc">{{ item.desc }}</span>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 核心模块入口 -->
    <div class="section">
      <div class="section-header">
        <h2>核心模块</h2>
        <p>探索社区各个功能</p>
      </div>
      <el-row :gutter="20">
        <el-col :span="6">
          <div class="module-card" @click="$router.push('/app/teams')">
            <div class="module-icon" style="background: linear-gradient(135deg, #409eff, #1d4ed8);">
              <el-icon size="28"><UserFilled /></el-icon>
            </div>
            <h3>球队管理</h3>
            <p>创建、加入和管理球队</p>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="module-card" @click="$router.push('/app/matches')">
            <div class="module-icon" style="background: linear-gradient(135deg, #e6a23c, #c45d0e);">
              <el-icon size="28"><Trophy /></el-icon>
            </div>
            <h3>赛事中心</h3>
            <p>查看赛事列表和赛程</p>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="module-card" @click="$router.push('/app/posts')">
            <div class="module-icon" style="background: linear-gradient(135deg, #67c23a, #2f891e);">
              <el-icon size="28"><ChatDotRound /></el-icon>
            </div>
            <h3>社区论坛</h3>
            <p>参与讨论，分享见解</p>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="module-card" @click="$router.push('/app/shop/products')">
            <div class="module-icon" style="background: linear-gradient(135deg, #909399, #555);">
              <el-icon size="28"><ShoppingBag /></el-icon>
            </div>
            <h3>足球商城</h3>
            <p>购买球衣装备</p>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 个人信息 -->
    <div class="section">
      <div class="section-header">
        <h2>我的信息</h2>
      </div>
      <el-card class="profile-card">
        <div class="profile-info">
          <el-avatar :size="64" class="profile-avatar">
            {{ userStore.userInfo?.nickname?.charAt(0) || userStore.userInfo?.username?.charAt(0) || 'U' }}
          </el-avatar>
          <div class="profile-details">
            <div class="profile-name">{{ userStore.userInfo?.nickname || userStore.userInfo?.username }}</div>
            <div class="profile-username">@{{ userStore.userInfo?.username }}</div>
            <div class="profile-roles" v-if="userStore.userInfo?.roles?.length">
              <el-tag
                v-for="role in userStore.userInfo.roles"
                :key="role.roleCode"
                :type="role.roleCode === 'admin' ? 'danger' : 'warning'"
                size="small"
              >{{ role.roleName }}</el-tag>
            </div>
          </div>
          <el-button type="primary" text @click="$router.push('/app/profile')">
            查看详情 →
          </el-button>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useUserStore } from '@/stores/user'
import { Trophy, ChatDotRound, ShoppingBag, UserFilled } from '@element-plus/icons-vue'

const userStore = useUserStore()

const quickActions = ref([
  { label: '赛事列表', path: '/app/matches', icon: '📅', desc: '查看所有赛事' },
  { label: '我的球队', path: '/app/my-teams', icon: '🏟️', desc: '管理球队信息' },
  { label: '社区动态', path: '/app/posts', icon: '💬', desc: '浏览最新帖子' },
  { label: '消息中心', path: '/app/chat', icon: '📨', desc: '查看聊天记录' },
])
</script>

<style scoped>
.dashboard {
  max-width: 1100px;
  margin: 0 auto;
}

/* ── Hero Banner ── */
.hero {
  position: relative;
  border-radius: 20px;
  overflow: hidden;
  background: linear-gradient(135deg, #0f1c2e 0%, #1a3a5c 50%, #1d4ed8 100%);
  padding: 48px 56px;
  color: #fff;
  margin-bottom: 32px;
  box-shadow: 0 8px 32px rgba(29, 78, 216, 0.25);
}

.hero-bg-orbs {
  position: absolute;
  inset: 0;
  overflow: hidden;
  pointer-events: none;
}

.orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(60px);
  opacity: 0.35;
  animation: float 8s ease-in-out infinite;
}

.orb-1 {
  width: 260px;
  height: 260px;
  background: #409eff;
  top: -60px;
  right: 10%;
  animation-delay: 0s;
}

.orb-2 {
  width: 180px;
  height: 180px;
  background: #7c3aed;
  bottom: -40px;
  left: 20%;
  animation-delay: -3s;
}

.orb-3 {
  width: 120px;
  height: 120px;
  background: #22c55e;
  top: 30%;
  right: 30%;
  animation-delay: -5s;
}

@keyframes float {
  0%, 100% { transform: translateY(0) scale(1); }
  50% { transform: translateY(-20px) scale(1.06); }
}

.hero-content {
  position: relative;
  z-index: 1;
}

.hero-badge {
  display: inline-block;
  background: rgba(255, 255, 255, 0.12);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 20px;
  padding: 5px 16px;
  font-size: 13px;
  color: #bfdbfe;
  margin-bottom: 16px;
  backdrop-filter: blur(4px);
}

.hero-title {
  font-size: 32px;
  font-weight: 800;
  margin-bottom: 10px;
  letter-spacing: -0.5px;
  background: linear-gradient(90deg, #fff, #bfdbfe);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.hero-subtitle {
  font-size: 15px;
  color: #94a3b8;
}

/* ── Section ── */
.section {
  margin-bottom: 32px;
}

.section-header {
  margin-bottom: 20px;
}

.section-header h2 {
  font-size: 20px;
  font-weight: 700;
  color: #1a202c;
  margin-bottom: 4px;
}

.section-header p {
  font-size: 13px;
  color: #718096;
}

/* ── Quick Actions ── */
.quick-card {
  background: #fff;
  border-radius: 14px;
  padding: 24px 20px;
  cursor: pointer;
  transition: all 0.25s;
  border: 1px solid rgba(0,0,0,0.05);
  text-align: center;
}

.quick-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
  border-color: rgba(64, 158, 255, 0.2);
}

.quick-icon {
  font-size: 32px;
  display: block;
  margin-bottom: 10px;
}

.quick-label {
  font-size: 15px;
  font-weight: 600;
  color: #1a202c;
  display: block;
  margin-bottom: 4px;
}

.quick-desc {
  font-size: 12px;
  color: #718096;
  display: block;
}

/* ── Module Cards ── */
.module-card {
  background: #fff;
  border-radius: 16px;
  padding: 28px 22px;
  cursor: pointer;
  transition: all 0.25s;
  border: 1px solid rgba(0,0,0,0.05);
  text-align: center;
}

.module-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.12);
}

.module-icon {
  width: 56px;
  height: 56px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  margin: 0 auto 16px;
  box-shadow: 0 4px 14px rgba(0,0,0,0.18);
}

.module-card h3 {
  font-size: 16px;
  font-weight: 700;
  color: #1a202c;
  margin-bottom: 6px;
}

.module-card p {
  font-size: 13px;
  color: #718096;
  line-height: 1.5;
}

/* ── Profile Card ── */
.profile-card {
  border-radius: 14px;
}

.profile-info {
  display: flex;
  align-items: center;
  gap: 20px;
}

.profile-avatar {
  background: linear-gradient(135deg, #409eff, #7c3aed);
  font-weight: 600;
  font-size: 24px;
  flex-shrink: 0;
}

.profile-details {
  flex: 1;
}

.profile-name {
  font-size: 18px;
  font-weight: 700;
  color: #1a202c;
  margin-bottom: 4px;
}

.profile-username {
  font-size: 14px;
  color: #718096;
  margin-bottom: 8px;
}

.profile-roles {
  display: flex;
  gap: 6px;
}
</style>
