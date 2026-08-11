<template>
  <div class="register-page">
    <div class="bg-layer">
      <div class="bg-orb orb-1" />
      <div class="bg-orb orb-2" />
      <div class="bg-orb orb-3" />
    </div>

    <div class="register-wrapper">
      <!-- 左侧装饰 -->
      <div class="register-decoration">
        <div class="deco-ball">⚽</div>
        <h2 class="deco-title">加入足球社区</h2>
        <p class="deco-sub">创建账号，开启你的足球之旅</p>
        <div class="deco-features">
          <div class="deco-feature">
            <span class="feature-check">✓</span>
            <span>创建并管理球队</span>
          </div>
          <div class="deco-feature">
            <span class="feature-check">✓</span>
            <span>参与各类赛事</span>
          </div>
          <div class="deco-feature">
            <span class="feature-check">✓</span>
            <span>与球迷交流互动</span>
          </div>
          <div class="deco-feature">
            <span class="feature-check">✓</span>
            <span>购买球衣装备</span>
          </div>
        </div>
      </div>

      <!-- 右侧注册卡片 -->
      <div class="register-card">
        <div class="card-header">
          <div class="card-logo">⚽</div>
          <h1 class="card-title">创建账号</h1>
          <p class="card-desc">填写信息，注册成为社区一员</p>
        </div>

        <el-form
          :model="form"
          :rules="rules"
          ref="formRef"
          class="register-form"
          @keyup.enter="handleRegister"
        >
          <el-form-item prop="username">
            <div class="input-wrap">
              <el-icon class="input-icon"><User /></el-icon>
              <el-input v-model="form.username" placeholder="用户名（3-20位）" size="large" />
            </div>
          </el-form-item>

          <el-form-item prop="password">
            <div class="input-wrap">
              <el-icon class="input-icon"><Lock /></el-icon>
              <el-input v-model="form.password" type="password" placeholder="密码（6-20位）" size="large" show-password />
            </div>
          </el-form-item>

          <el-form-item prop="confirmPassword">
            <div class="input-wrap">
              <el-icon class="input-icon"><Lock /></el-icon>
              <el-input v-model="form.confirmPassword" type="password" placeholder="确认密码" size="large" show-password />
            </div>
          </el-form-item>

          <el-form-item prop="nickname">
            <div class="input-wrap">
              <el-icon class="input-icon"><Avatar /></el-icon>
              <el-input v-model="form.nickname" placeholder="昵称" size="large" />
            </div>
          </el-form-item>

          <el-form-item prop="email">
            <div class="input-wrap">
              <el-icon class="input-icon"><Message /></el-icon>
              <el-input v-model="form.email" placeholder="邮箱（选填）" size="large" />
            </div>
          </el-form-item>

          <el-button
            type="primary"
            size="large"
            :loading="loading"
            class="register-btn"
            @click="handleRegister"
          >
            <template v-if="!loading">
              <el-icon><CircleCheck /></el-icon> 注 册
            </template>
            <template v-else>注册中...</template>
          </el-button>
        </el-form>

        <div class="register-footer">
          <span>已有账号？</span>
          <router-link to="/login" class="login-link">立即登录 →</router-link>
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
import { User, Lock, Avatar, Message, CircleCheck } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

const formRef = ref(null)
const loading = ref(false)

const form = ref({
  username: '',
  password: '',
  confirmPassword: '',
  nickname: '',
  email: ''
})

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== form.value.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度为3-20个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度为6-20个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ],
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }]
}

async function handleRegister() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    await userStore.registerAction({
      username: form.value.username,
      password: form.value.password,
      nickname: form.value.nickname,
      email: form.value.email
    })
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #0f1c2e;
  position: relative;
  overflow: hidden;
  padding: 40px 20px;
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
  width: 350px; height: 350px;
  background: #7c3aed;
  top: -80px; right: 10%;
  animation-delay: -2s;
}
.orb-2 {
  width: 280px; height: 280px;
  background: #409eff;
  bottom: -60px; left: 5%;
  animation-delay: -5s;
}
.orb-3 {
  width: 160px; height: 160px;
  background: #22c55e;
  top: 50%; left: 40%;
  animation-delay: -8s;
}

@keyframes orbFloat {
  0%, 100% { transform: translate(0, 0) scale(1); }
  50% { transform: translate(20px, -30px) scale(1.08); }
}

.register-wrapper {
  display: flex;
  align-items: center;
  gap: 60px;
  position: relative;
  z-index: 1;
  max-width: 900px;
  width: 100%;
}

/* ── 左侧装饰 ── */
.register-decoration {
  flex: 1;
  color: #fff;
  display: none;
}
@media (min-width: 768px) {
  .register-decoration { display: block; }
}

.deco-ball {
  font-size: 64px;
  animation: spin 8s linear infinite;
  display: block;
  margin-bottom: 20px;
  filter: drop-shadow(0 4px 20px rgba(124, 58, 237, 0.5));
}
@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.deco-title {
  font-size: 28px;
  font-weight: 800;
  margin-bottom: 8px;
  background: linear-gradient(90deg, #fff, #c4b5fd);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.deco-sub {
  font-size: 14px;
  color: #94a3b8;
  margin-bottom: 32px;
}

.deco-features {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.deco-feature {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 14px;
  color: #cbd5e1;
}

.feature-check {
  width: 22px;
  height: 22px;
  background: linear-gradient(135deg, #409eff, #7c3aed);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  color: #fff;
  flex-shrink: 0;
}

/* ── 右侧注册卡片 ── */
.register-card {
  flex: 0 0 420px;
  background: rgba(255, 255, 255, 0.06);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.12);
  border-radius: 24px;
  padding: 40px 32px;
  color: #fff;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
}

.card-header {
  text-align: center;
  margin-bottom: 28px;
}

.card-logo { font-size: 36px; display: block; margin-bottom: 10px; }
.card-title { font-size: 24px; font-weight: 700; color: #fff; margin-bottom: 6px; }
.card-desc { font-size: 13px; color: #94a3b8; }

.register-form { margin-top: 4px; }
:deep(.el-form-item) { margin-bottom: 18px; }
:deep(.el-form-item__error) { padding-left: 44px; }

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
  border-color: rgba(124, 58, 237, 0.5) !important;
  background: transparent !important;
  box-shadow: none !important;
}
:deep(.el-input__wrapper.is-focus) {
  border-color: #7c3aed !important;
  background: transparent !important;
  box-shadow: none !important;
}
:deep(.el-input__inner) { color: #fff !important; }
:deep(.el-input__inner::placeholder) { color: #64748b !important; }

.register-btn {
  width: 100%;
  height: 50px !important;
  border-radius: 12px !important;
  background: linear-gradient(135deg, #7c3aed, #409eff) !important;
  border: none !important;
  font-size: 16px !important;
  font-weight: 600 !important;
  letter-spacing: 2px;
  margin-top: 8px;
  box-shadow: 0 4px 16px rgba(124, 58, 237, 0.35);
  transition: transform 0.2s, box-shadow 0.2s !important;
}
.register-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 24px rgba(124, 58, 237, 0.5) !important;
}

.register-footer {
  text-align: center;
  margin-top: 22px;
  font-size: 14px;
  color: #94a3b8;
}

.login-link {
  color: #7c3aed;
  text-decoration: none;
  font-weight: 600;
  margin-left: 4px;
  transition: color 0.2s;
}
.login-link:hover { color: #c4b5fd; }
</style>
