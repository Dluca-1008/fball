<template>
  <div class="player-manage" v-loading="loading">
    <div class="page-header">
      <h2>球员管理</h2>
      <el-button @click="$router.back()">返回</el-button>
    </div>

    <el-card v-if="player">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px" style="max-width: 600px;">
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="所属球队">
          <el-select v-model="form.teamId" placeholder="请选择球队" filterable clearable style="width: 100%;">
            <el-option v-for="t in teams" :key="t.id" :label="t.name" :value="t.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="位置">
          <el-select v-model="form.position" placeholder="请选择位置" style="width: 100%;">
            <el-option label="前锋" value="前锋" />
            <el-option label="中场" value="中场" />
            <el-option label="后卫" value="后卫" />
            <el-option label="守门员" value="守门员" />
          </el-select>
        </el-form-item>
        <el-form-item label="号码">
          <el-input-number v-model="form.number" :min="1" :max="99" />
        </el-form-item>
        <el-form-item label="国籍">
          <el-input v-model="form.nationality" />
        </el-form-item>
        <el-form-item label="出生日期">
          <el-date-picker v-model="form.birthDate" type="date" placeholder="请选择出生日期" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="身高(cm)">
          <el-input-number v-model="form.height" :min="100" :max="250" :precision="1" />
        </el-form-item>
        <el-form-item label="体重(kg)">
          <el-input-number v-model="form.weight" :min="40" :max="150" :precision="1" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
          <el-button type="danger" @click="handleDelete">删除球员</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const route = useRoute()
const router = useRouter()
const playerId = route.params.id

const loading = ref(false)
const saving = ref(false)
const player = ref(null)
const teams = ref([])
const formRef = ref(null)

const form = reactive({
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
  name: [{ required: true, message: '请输入球员姓名', trigger: 'blur' }]
}

async function fetchPlayer() {
  loading.value = true
  try {
    const res = await request.get(`/api/players/${playerId}`)
    player.value = res.data
    Object.assign(form, {
      name: res.data.name,
      teamId: res.data.teamId,
      position: res.data.position,
      number: res.data.number,
      nationality: res.data.nationality,
      birthDate: res.data.birthDate,
      height: res.data.height,
      weight: res.data.weight
    })
  } finally {
    loading.value = false
  }
}

async function fetchTeams() {
  const res = await request.get('/api/teams', { params: { page: 1, size: 200 } })
  teams.value = res.data.records
}

async function handleSave() {
  await formRef.value.validate()
  saving.value = true
  try {
    await request.put(`/api/players/${playerId}`, form)
    ElMessage.success('保存成功')
    fetchPlayer()
  } finally {
    saving.value = false
  }
}

async function handleDelete() {
  await ElMessageBox.confirm('确定删除该球员？此操作不可撤销', '警告', { type: 'warning' })
  await request.delete(`/api/players/${playerId}`)
  ElMessage.success('删除成功')
  router.push('/players')
}

onMounted(() => {
  fetchPlayer()
  fetchTeams()
})
</script>

<style scoped>
.player-manage {
  background: white;
  padding: 20px;
  border-radius: 8px;
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}
</style>
