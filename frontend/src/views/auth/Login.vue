<template>
  <div class="login-page">
    <!-- 背景装饰 -->
    <div class="bg-layer">
      <div class="bg-orb orb-1" />
      <div class="bg-orb orb-2" />
      <div class="bg-orb orb-3" />
    </div>

    <div class="login-wrapper">
      <!-- 左侧装饰 -->
      <div class="login-decoration">
        <div class="deco-ball">⚽</div>
        <h2 class="deco-title">足球社区</h2>
        <p class="deco-sub">让热爱，不止于看球</p>
        <div class="deco-stats">
          <div class="deco-stat">
            <div class="deco-stat-num">1,280</div>
            <div class="deco-stat-label">注册球队</div>
          </div>
          <div class="deco-stat">
            <div class="deco-stat-num">5,640</div>
            <div class="deco-stat-label">活跃球员</div>
          </div>
          <div class="deco-stat">
            <div class="deco-stat-num">320</div>
            <div class="deco-stat-label">进行赛事</div>
          </div>
        </div>
      </div>

      <!-- 右侧登录卡片 -->
      <div class="login-card">
        <div class="card-header">
          <div class="card-logo">⚽</div>
          <h1 class="card-title">欢迎回来</h1>
          <p class="card-desc">登录你的账号，继续精彩</p>
        </div>

        <el-form
          :model="form"
          :rules="rules"
          ref="formRef"
          class="login-form"
          @keyup.enter="handleLogin"
        >
          <el-form-item prop="username">
            <div class="input-wrap">
              <el-icon class="input-icon"><User /></el-icon>
              <el-input
                v-model="form.username"
                placeholder="用户名"
                size="large"
              />
            </div>
          </el-form-item>

          <el-form-item prop="password">
            <div class="input-wrap">
              <el-icon class="input-icon"><Lock /></el-icon>
              <el-input
                v-model="form.password"
                type="password"
                placeholder="密码"
                size="large"
                show-password
              />
            </div>
          </el-form-item>

          <el-button
            type="primary"
            size="large"
            :loading="loading"
            class="login-btn"
            @click="handleLogin"
          >
            <template v-if="!loading">
              <el-icon><Right /></el-icon> 登 录
            </template>
            <template v-else>登录中...</template>
          </el-button>
        </el-form>

        <div class="login-footer">
          <span>还没有账号？</span>
          <router-link to="/register" class="register-link">立即注册 →</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import { User, Lock, Right } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

const formRef = ref(null)
const loading = ref(false)

const form = ref({ username: '', password: '' })

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function handleLogin() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    await userStore.loginAction(form.value.username, form.value.password)
    ElMessage.success('登录成功')
    router.push('/')
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #0f1c2e;
  position: relative;
  overflow: hidden;
  padding: 40px 20px;
}

/* ── 背景动态光球 ── */
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
  width: 400px;
  height: 400px;
  background: #409eff;
  top: -100px;
  left: -80px;
  animation-delay: 0s;
}

.orb-2 {
  width: 300px;
  height: 300px;
  background: #7c3aed;
  bottom: -60px;
  right: 10%;
  animation-delay: -4s;
}

.orb-3 {
  width: 200px;
  height: 200px;
  background: #22c55e;
  top: 40%;
  right: 30%;
  animation-delay: -7s;
}

@keyframes orbFloat {
  0%, 100% { transform: translate(0, 0) scale(1); }
  33% { transform: translate(30px, -20px) scale(1.05); }
  66% { transform: translate(-20px, 20px) scale(0.95); }
}

/* ── 布局 ── */
.login-wrapper {
  display: flex;
  align-items: center;
  gap: 60px;
  position: relative;
  z-index: 1;
  max-width: 900px;
  width: 100%;
}

/* ── 左侧装饰区 ── */
.login-decoration {
  flex: 1;
  color: #fff;
  display: none; /* 默认隐藏，平板以上显示 */
}

@media (min-width: 768px) {
  .login-decoration { display: block; }
}

.deco-ball {
  font-size: 72px;
  animation: spin 6s linear infinite;
  display: block;
  margin-bottom: 24px;
  filter: drop-shadow(0 4px 20px rgba(64, 158, 255, 0.5));
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.deco-title {
  font-size: 32px;
  font-weight: 800;
  margin-bottom: 8px;
  background: linear-gradient(90deg, #fff, #93c5fd);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.deco-sub {
  font-size: 15px;
  color: #94a3b8;
  margin-bottom: 36px;
}

.deco-stats {
  display: flex;
  gap: 24px;
}

.deco-stat {
  text-align: center;
}

.deco-stat-num {
  font-size: 26px;
  font-weight: 800;
  color: #409eff;
}

.deco-stat-label {
  font-size: 12px;
  color: #64748b;
  margin-top: 4px;
}

/* ── 右侧登录卡片 ── */
.login-card {
  flex: 0 0 400px;
  background: rgba(255, 255, 255, 0.06);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.12);
  border-radius: 24px;
  padding: 40px 36px;
  color: #fff;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
}

.card-header {
  text-align: center;
  margin-bottom: 32px;
}

.card-logo {
  font-size: 40px;
  margin-bottom: 12px;
  display: block;
}

.card-title {
  font-size: 26px;
  font-weight: 700;
  margin-bottom: 6px;
  color: #fff;
}

.card-desc {
  font-size: 14px;
  color: #94a3b8;
}

/* ── 表单 ── */
.login-form {
  margin-top: 8px;
}

:deep(.el-form-item) {
  margin-bottom: 20px;
}

:deep(.el-form-item__error) {
  padding-left: 44px;
}

.input-wrap {
  position: relative;
  width: 100%;
}

.input-icon {
  position: absolute;
  left: 14px;
  top: 50%;
  transform: translateY(-50%);
  z-index: 2;
  color: #64748b;
  font-size: 18px;
}

:deep(.el-input) {
  width: 100%;
}
:deep(.el-input__wrapper) {
  padding-left: 44px !important;
  background: transparent !important;
  border: 1px solid rgba(255, 255, 255, 0.15) !important;
  border-radius: 12px !important;
  box-shadow: none !important;
  transition: all 0.25s;
  width: 100%;
}
:deep(.el-input__wrapper:hover) {
  border-color: rgba(64, 158, 255, 0.5) !important;
  background: transparent !important;
  box-shadow: none !important;
}
:deep(.el-input__wrapper.is-focus) {
  border-color: #409eff !important;
  background: transparent !important;
  box-shadow: none !important;
}

:deep(.el-input__inner) {
  color: #fff !important;
}

:deep(.el-input__inner::placeholder) {
  color: #64748b !important;
}

/* ── 登录按钮 ── */
.login-btn {
  width: 100%;
  height: 50px !important;
  border-radius: 12px !important;
  background: linear-gradient(135deg, #409eff, #1d4ed8) !important;
  border: none !important;
  font-size: 16px !important;
  font-weight: 600 !important;
  letter-spacing: 2px;
  margin-top: 8px;
  box-shadow: 0 4px 16px rgba(64, 158, 255, 0.35);
  transition: transform 0.2s, box-shadow 0.2s !important;
}
.login-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 24px rgba(64, 158, 255, 0.5) !important;
}

/* ── 底部链接 ── */
.login-footer {
  text-align: center;
  margin-top: 24px;
  font-size: 14px;
  color: #94a3b8;
}

.register-link {
  color: #409eff;
  text-decoration: none;
  font-weight: 600;
  margin-left: 4px;
  transition: color 0.2s;
}
.register-link:hover {
  color: #93c5fd;
  text-decoration: underline;
}
</style>
