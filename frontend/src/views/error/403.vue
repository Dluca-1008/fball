<template>
  <div class="forbidden-page">
    <div class="bg-layer">
      <div class="bg-orb orb-1" />
      <div class="bg-orb orb-2" />
    </div>
    <div class="forbidden-card">
      <div class="forbidden-icon">🔒</div>
      <h1 class="forbidden-title">403 无权访问</h1>
      <p class="forbidden-desc">您没有权限访问该页面，请联系管理员获取授权</p>
      <el-button type="primary" size="large" @click="$router.push('/')">
        返回首页
      </el-button>
    </div>
  </div>
</template>

<script setup>
</script>

<style scoped>
.forbidden-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #0f1c2e;
  position: relative;
  overflow: hidden;
}

.bg-layer {
  position: absolute;
  inset: 0;
  overflow: hidden;
  pointer-events: none;
}

.bg-orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.4;
  animation: orbFloat 10s ease-in-out infinite;
}

.orb-1 {
  width: 350px;
  height: 350px;
  background: #e53e3e;
  top: -100px;
  right: 10%;
}

.orb-2 {
  width: 280px;
  height: 280px;
  background: #7c3aed;
  bottom: -60px;
  left: 5%;
  animation-delay: -5s;
}

@keyframes orbFloat {
  0%, 100% { transform: translate(0, 0) scale(1); }
  50% { transform: translate(20px, -30px) scale(1.08); }
}

.forbidden-card {
  position: relative;
  z-index: 1;
  text-align: center;
  color: #fff;
}

.forbidden-icon {
  font-size: 72px;
  margin-bottom: 24px;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.1); }
}

.forbidden-title {
  font-size: 32px;
  font-weight: 800;
  margin-bottom: 12px;
  background: linear-gradient(90deg, #fff, #fca5a5);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.forbidden-desc {
  font-size: 15px;
  color: #94a3b8;
  margin-bottom: 32px;
}

.forbidden-card .el-button {
  min-width: 160px;
  height: 48px;
  border-radius: 12px !important;
  font-size: 15px !important;
  font-weight: 600 !important;
}
</style>
