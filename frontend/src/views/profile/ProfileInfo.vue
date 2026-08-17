<template>
  <div class="profile-info">
    <div class="info-header">
      <h3 class="section-title">基本信息</h3>
      <el-button v-if="!editing" type="primary" plain size="small" @click="startEdit">
        <el-icon><Edit /></el-icon> 编辑资料
      </el-button>
    </div>

    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      label-width="80px"
      class="edit-form"
    >
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="用户名" prop="username">
            <span v-if="!editing" class="info-value">{{ form.username || '-' }}</span>
            <el-input v-else v-model="form.username" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="昵称" prop="nickname">
            <span v-if="!editing" class="info-value">{{ form.nickname || '-' }}</span>
            <el-input v-else v-model="form.nickname" placeholder="请输入昵称" />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="邮箱" prop="email">
            <span v-if="!editing" class="info-value">{{ form.email || '-' }}</span>
            <el-input v-else v-model="form.email" placeholder="请输入邮箱" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="手机号" prop="phone">
            <span v-if="!editing" class="info-value">{{ form.phone || '-' }}</span>
            <el-input v-else v-model="form.phone" placeholder="请输入手机号" />
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item label="性别">
        <span v-if="!editing" class="info-value">{{ genderText }}</span>
        <el-radio-group v-else v-model="form.gender">
          <el-radio :value="1">男</el-radio>
          <el-radio :value="0">女</el-radio>
          <el-radio :value="null">未设置</el-radio>
        </el-radio-group>
      </el-form-item>

      <el-form-item v-if="!editing" label="注册时间">
        <span class="info-value">{{ formatDate(user.createdAt) }}</span>
      </el-form-item>
      <el-form-item v-if="!editing" label="上次登录">
        <span class="info-value">{{ formatDate(user.lastLoginTime) || '暂无' }}</span>
      </el-form-item>

      <el-form-item v-if="roles.length" label="角色">
        <div class="roles-wrap">
          <el-tag
            v-for="role in roles"
            :key="role.roleCode"
            :type="role.roleCode === 'admin' ? 'danger' : 'warning'"
            effect="dark"
            round
          >{{ role.roleName }}</el-tag>
        </div>
      </el-form-item>

      <el-form-item v-if="editing">
        <div class="form-actions">
          <el-button type="primary" :loading="saving" @click="handleSubmit">保存修改</el-button>
          <el-button @click="cancelEdit">取消</el-button>
        </div>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Edit } from '@element-plus/icons-vue'
import { updateUserInfo } from '@/api/auth'
import { useUserStore } from '@/stores/user'

const props = defineProps({
  user: { type: Object, required: true }
})

const userStore = useUserStore()
const editing = ref(false)
const saving = ref(false)
const formRef = ref(null)

function initFormFromUser(u) {
  return {
    username: u?.username || '',
    nickname: u?.nickname || '',
    email: u?.email || '',
    phone: u?.phone || '',
    gender: u?.gender ?? null
  }
}

const form = ref(initFormFromUser(props.user))

// 用户数据加载后同步到表单（编辑中不覆盖，避免打断正在编辑的内容）
watch(
  () => props.user,
  (newUser) => {
    if (!editing.value) {
      form.value = initFormFromUser(newUser)
    }
  },
  { immediate: true }
)

const rules = {
  nickname: [{ max: 20, message: '昵称最多20个字符', trigger: 'blur' }],
  email: [{ type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }]
}

const roles = computed(() => props.user?.roles || [])

// 性别默认未设置，展示时以文本形式呈现
const genderText = computed(() => {
  if (form.value.gender === 1) return '男'
  if (form.value.gender === 0) return '女'
  return '未设置'
})

function formatDate(dateStr) {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleDateString('zh-CN', {
    year: 'numeric', month: '2-digit', day: '2-digit'
  })
}

function startEdit() {
  form.value = initFormFromUser(props.user)
  editing.value = true
}

function cancelEdit() {
  editing.value = false
  formRef.value?.resetFields?.()
}

async function handleSubmit() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  saving.value = true
  try {
    const res = await updateUserInfo({
      nickname: form.value.nickname || null,
      email: form.value.email || null,
      phone: form.value.phone || null,
      gender: form.value.gender
    })
    userStore.userInfo = res.data
    editing.value = false
    ElMessage.success('资料已更新')
  } catch {
    // error handled by interceptor
  } finally {
    saving.value = false
  }
}
</script>

<style scoped>
.section-title {
  font-size: 16px;
  font-weight: 700;
  color: #1a202c;
  margin: 0;
}

.info-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 2px solid #f0f2f5;
}

.edit-form {
  max-width: 560px;
}

:deep(.el-form-item) {
  margin-bottom: 20px;
}

:deep(.el-form-item__label) {
  color: #4a5568;
  font-weight: 500;
}

:deep(.el-input__wrapper) {
  background: #f7fafc !important;
}

.info-value {
  font-size: 14px;
  color: #2d3748;
}

.roles-wrap {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.form-actions {
  display: flex;
  gap: 10px;
  padding-left: 80px;
}
</style>
