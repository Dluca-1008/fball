<template>
  <div class="home-page">
    <!-- Hero Banner -->
    <div class="hero">
      <div class="hero-bg-orbs">
        <div class="orb orb-1" />
        <div class="orb orb-2" />
        <div class="orb orb-3" />
      </div>
      <div class="hero-content">
        <div class="hero-badge">⚽ 足球爱好者的聚集地</div>
        <h1 class="hero-title">欢迎来到足球社区</h1>
        <p class="hero-subtitle">关注球队 · 参与赛事 · 交流互动 · 发现精彩</p>
        <div class="hero-actions">
          <el-button size="large" class="hero-btn-primary" @click="$router.push('/matches')">
            <el-icon><Trophy /></el-icon> 查看赛事
          </el-button>
          <el-button size="large" class="hero-btn-secondary" @click="$router.push('/posts')">
            <el-icon><ChatDotRound /></el-icon> 进入社区
          </el-button>
        </div>
      </div>
    </div>

    <!-- 功能入口 -->
    <div class="section">
      <div class="section-header">
        <h2>核心功能</h2>
        <p>探索足球世界的每一个角落</p>
      </div>
      <el-row :gutter="20">
        <el-col :span="6">
          <div class="feature-card" @click="$router.push('/teams')">
            <div class="feature-icon" style="background: linear-gradient(135deg, #409eff, #1d4ed8);">
              <el-icon size="28"><UserFilled /></el-icon>
            </div>
            <h3>球队管理</h3>
            <p>创建、加入和管理你的球队，组建最强阵容</p>
            <div class="feature-tag">球队</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="feature-card" @click="$router.push('/matches')">
            <div class="feature-icon" style="background: linear-gradient(135deg, #e6a23c, #c45d0e);">
              <el-icon size="28"><Trophy /></el-icon>
            </div>
            <h3>赛事中心</h3>
            <p>关注联赛杯赛动态，实时追踪比分与排行</p>
            <div class="feature-tag">赛事</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="feature-card" @click="$router.push('/posts')">
            <div class="feature-icon" style="background: linear-gradient(135deg, #67c23a, #2f891e);">
              <el-icon size="28"><ChatDotRound /></el-icon>
            </div>
            <h3>社区论坛</h3>
            <p>分享赛事见解，与球迷交流互动心得</p>
            <div class="feature-tag">社区</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="feature-card" @click="$router.push('/shop/products')">
            <div class="feature-icon" style="background: linear-gradient(135deg, #909399, #555);">
              <el-icon size="28"><ShoppingBag /></el-icon>
            </div>
            <h3>足球商城</h3>
            <p>购买球衣装备，支持你的主队</p>
            <div class="feature-tag">商城</div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 快捷入口 -->
    <div class="section section-last">
      <div class="section-header">
        <h2>快捷入口</h2>
      </div>
      <el-row :gutter="16">
        <el-col :span="6" v-for="item in quickItems" :key="item.path">
          <div class="quick-card" @click="$router.push(item.path)">
            <span class="quick-icon">{{ item.icon }}</span>
            <span class="quick-label">{{ item.label }}</span>
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import {
  Trophy, ChatDotRound, ShoppingBag, UserFilled
} from '@element-plus/icons-vue'

const quickItems = ref([
  { label: '我的邀请', path: '/my-invitations', icon: '📨' },
  { label: '消息中心', path: '/chat', icon: '💬' },
  { label: '商品商城', path: '/shop/products', icon: '🛒' },
  { label: '赛事日程', path: '/matches/schedule', icon: '📅' },
])
</script>

<style scoped>
.home-page {
  max-width: 1100px;
  margin: 0 auto;
}

/* ── Hero Banner ── */
.hero {
  position: relative;
  border-radius: 20px;
  overflow: hidden;
  background: linear-gradient(135deg, #0f1c2e 0%, #1a3a5c 50%, #1d4ed8 100%);
  padding: 64px 56px;
  color: #fff;
  margin-bottom: 40px;
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
  margin-bottom: 20px;
  backdrop-filter: blur(4px);
}

.hero-title {
  font-size: 40px;
  font-weight: 800;
  margin-bottom: 14px;
  letter-spacing: -0.5px;
  background: linear-gradient(90deg, #fff, #bfdbfe);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.hero-subtitle {
  font-size: 16px;
  color: #94a3b8;
  margin-bottom: 32px;
  letter-spacing: 0.3px;
}

.hero-actions {
  display: flex;
  gap: 14px;
}

.hero-btn-primary {
  background: linear-gradient(135deg, #409eff, #1d4ed8) !important;
  border: none !important;
  color: #fff !important;
  padding: 11px 28px !important;
  border-radius: 10px !important;
  font-weight: 600 !important;
  box-shadow: 0 4px 16px rgba(64, 158, 255, 0.4);
  transition: transform 0.2s, box-shadow 0.2s !important;
}
.hero-btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 24px rgba(64, 158, 255, 0.5) !important;
}

.hero-btn-secondary {
  background: rgba(255, 255, 255, 0.1) !important;
  border: 1px solid rgba(255, 255, 255, 0.25) !important;
  color: #fff !important;
  padding: 11px 28px !important;
  border-radius: 10px !important;
  font-weight: 600 !important;
  backdrop-filter: blur(4px);
  transition: background 0.2s, transform 0.2s !important;
}
.hero-btn-secondary:hover {
  background: rgba(255, 255, 255, 0.18) !important;
  transform: translateY(-2px);
}

/* ── Section ── */
.section {
  margin-bottom: 40px;
}
.section-last {
  margin-bottom: 0;
}

.section-header {
  margin-bottom: 22px;
}
.section-header h2 {
  font-size: 22px;
  font-weight: 700;
  color: #1a202c;
  margin-bottom: 4px;
}
.section-header p {
  font-size: 14px;
  color: #718096;
}

/* ── Feature Cards ── */
.feature-card {
  background: #fff;
  border-radius: 16px;
  padding: 28px 22px;
  cursor: pointer;
  transition: transform 0.25s, box-shadow 0.25s;
  position: relative;
  overflow: hidden;
  border: 1px solid rgba(0,0,0,0.05);
}
.feature-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.12);
}
.feature-card::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(90deg, var(--accent, #409eff), transparent);
  opacity: 0;
  transition: opacity 0.25s;
}
.feature-card:hover::after {
  opacity: 1;
}
.feature-card:nth-child(1) { --accent: #409eff; }
.feature-card:nth-child(2) { --accent: #e6a23c; }
.feature-card:nth-child(3) { --accent: #67c23a; }
.feature-card:nth-child(4) { --accent: #909399; }

.feature-icon {
  width: 56px;
  height: 56px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  margin-bottom: 18px;
  box-shadow: 0 4px 14px rgba(0,0,0,0.18);
}

.feature-card h3 {
  font-size: 17px;
  font-weight: 700;
  color: #1a202c;
  margin-bottom: 8px;
}
.feature-card p {
  font-size: 13px;
  color: #718096;
  line-height: 1.6;
  margin-bottom: 16px;
}

.feature-tag {
  display: inline-block;
  font-size: 11px;
  font-weight: 600;
  color: #409eff;
  background: rgba(64, 158, 255, 0.08);
  padding: 3px 10px;
  border-radius: 20px;
}

/* ── Quick Entry ── */
.quick-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
  border: 1px solid rgba(0,0,0,0.05);
  height: 100%;
}
.quick-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
}
.quick-icon {
  font-size: 32px;
}
.quick-label {
  font-size: 14px;
  font-weight: 600;
  color: #4a5568;
}
</style>
