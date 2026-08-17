<template>
  <div class="player-register">
    <!-- 已注册状态 -->
    <div v-if="player" class="registered-state">
      <div class="registered-header">
        <el-icon class="registered-icon"><CircleCheckFilled /></el-icon>
        <span class="registered-title">您已注册为球员</span>
        <el-button type="primary" size="small" @click="editing = true">编辑信息</el-button>
      </div>
      <el-descriptions :column="2" border v-if="!editing">
        <el-descriptions-item label="姓名">{{ player.name }}</el-descriptions-item>
        <el-descriptions-item label="号码">#{{ player.number || '-' }}</el-descriptions-item>
        <el-descriptions-item label="位置">{{ player.position || '-' }}</el-descriptions-item>
        <el-descriptions-item label="国籍">{{ player.nationality || '-' }}</el-descriptions-item>
        <el-descriptions-item label="所属球队">{{ player.teamName || '未绑定' }}</el-descriptions-item>
        <el-descriptions-item label="出生日期">{{ player.birthDate || '-' }}</el-descriptions-item>
        <el-descriptions-item label="身高">{{ player.height ? player.height + ' cm' : '-' }}</el-descriptions-item>
        <el-descriptions-item label="体重">{{ player.weight ? player.weight + ' kg' : '-' }}</el-descriptions-item>
      </el-descriptions>
    </div>

    <!-- 未注册状态 / 编辑状态 -->
    <el-form
      v-else-if="!loading"
      ref="formRef"
      :model="form"
      :rules="rules"
      label-width="90px"
      class="register-form"
    >
      <div class="form-title">注册成为球员</div>
      <div class="form-desc">填写您的球员信息，完成注册后即可参与球队活动</div>

      <el-form-item label="姓名" prop="name">
        <el-input v-model="form.name" placeholder="请输入您的姓名" maxlength="50" />
      </el-form-item>

      <el-form-item label="所属球队">
        <el-select v-model="form.teamId" placeholder="请选择球队（可选）" filterable clearable style="width: 100%;">
          <el-option v-for="t in teams" :key="t.id" :label="t.name" :value="t.id" />
        </el-select>
      </el-form-item>

      <el-form-item label="位置" prop="position">
        <el-select v-model="form.position" placeholder="请选择位置" style="width: 100%;">
          <el-option label="前锋" value="前锋" />
          <el-option label="中场" value="中场" />
          <el-option label="后卫" value="后卫" />
          <el-option label="守门员" value="守门员" />
        </el-select>
      </el-form-item>

      <el-form-item label="号码">
        <el-input-number v-model="form.number" :min="1" :max="99" placeholder="球衣号码" style="width: 120px;" />
      </el-form-item>

      <el-form-item label="国籍">
        <el-input v-model="form.nationality" placeholder="如：中国" maxlength="50" />
      </el-form-item>

      <el-form-item label="出生日期">
        <el-date-picker
          v-model="form.birthDate"
          type="date"
          placeholder="请选择出生日期"
          style="width: 200px;"
          format="YYYY-MM-DD"
          value-format="YYYY-MM-DD"
        />
      </el-form-item>

      <el-form-item label="身高(cm)">
        <el-input-number v-model="form.height" :min="100" :max="250" :precision="1" style="width: 120px;" />
      </el-form-item>

      <el-form-item label="体重(kg)">
        <el-input-number v-model="form.weight" :min="40" :max="150" :precision="1" style="width: 120px;" />
      </el-form-item>

      <el-form-item>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">
          {{ editing ? '保存修改' : '确认注册' }}
        </el-button>
        <el-button v-if="editing" @click="cancelEdit">取消</el-button>
      </el-form-item>
    </el-form>

    <div v-else class="loading-state">
      <el-icon class="is-loading"><Loading /></el-icon> 加载中...
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { CircleCheckFilled, Loading } from '@element-plus/icons-vue'
import { getMyPlayer, registerPlayer } from '@/api/player'
import request from '@/utils/request'

const loading = ref(true)
const player = ref(null)
const teams = ref([])
const submitting = ref(false)
const editing = ref(false)
const formRef = ref(null)

const form = ref({
  name: '',
  teamId: null,
  position: '',
  number: null,
  nationality: '',
  birthDate: '',
  height: null,
  weight: null
})

const rules = {
  name: [{ required: true, message: '请输入您的姓名', trigger: 'blur' }],
  position: [{ required: true, message: '请选择您的位置', trigger: 'change' }]
}

async function fetchPlayer() {
  loading.value = true
  try {
    const res = await getMyPlayer()
    if (res.data) {
      player.value = res.data
    }
  } catch {
    // 未注册或出错，player.value 保持 null
  } finally {
    loading.value = false
  }
}

async function fetchTeams() {
  try {
    const res = await request.get('/api/teams/list')
    teams.value = res.data || []
  } catch {
    // ignore
  }
}

async function handleSubmit() {
  if (editing.value) {
    // 编辑模式：使用 PUT 更新
    if (!player.value) return
    submitting.value = true
    try {
      await request.put(`/api/players/${player.value.id}`, form.value)
      ElMessage.success('信息已更新')
      editing.value = false
      fetchPlayer()
    } finally {
      submitting.value = false
    }
    return
  }

  // 注册模式
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    await registerPlayer(form.value)
    ElMessage.success('注册成功！')
    fetchPlayer()
  } catch (e) {
    const msg = e?.response?.data?.message || e?.message || '注册失败，请重试'
    ElMessage.error(msg)
  } finally {
    submitting.value = false
  }
}

function cancelEdit() {
  editing.value = false
  formRef.value?.resetFields?.()
}

onMounted(() => {
  fetchPlayer()
  fetchTeams()
})
</script>

<style scoped>
.player-register {
  max-width: 600px;
}

.registered-state {
  padding: 8px 0;
}

.registered-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
  padding: 16px;
  background: linear-gradient(135deg, rgba(103, 194, 58, 0.08), rgba(67, 162, 80, 0.04));
  border-radius: 12px;
  border: 1px solid rgba(103, 194, 58, 0.2);
}

.registered-icon {
  font-size: 24px;
  color: #67c23a;
}

.registered-title {
  font-size: 16px;
  font-weight: 600;
  color: #2d3748;
  flex: 1;
}

.register-form {
  padding: 4px 0;
}

.form-title {
  font-size: 18px;
  font-weight: 700;
  color: #1a202c;
  margin-bottom: 4px;
}

.form-desc {
  font-size: 13px;
  color: #718096;
  margin-bottom: 24px;
}

.loading-state {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 40px 0;
  color: #a0aec0;
  font-size: 14px;
}
</style>
