<template>
  <div class="coach-manage" v-loading="loading">
    <div class="page-header">
      <h2>教练管理</h2>
      <el-button @click="$router.back()">返回</el-button>
    </div>
    <el-card v-if="coach">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" style="max-width: 600px;">
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="所属球队">
          <el-select v-model="form.teamId" placeholder="请选择球队" filterable clearable style="width: 100%;">
            <el-option v-for="t in teams" :key="t.id" :label="t.name" :value="t.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="职位">
          <el-select v-model="form.roleTitle" placeholder="请选择职位" style="width: 100%;">
            <el-option label="主教练" value="主教练" />
            <el-option label="助理教练" value="助理教练" />
            <el-option label="体能教练" value="体能教练" />
            <el-option label="守门员教练" value="守门员教练" />
          </el-select>
        </el-form-item>
        <el-form-item label="国籍">
          <el-input v-model="form.nationality" />
        </el-form-item>
        <el-form-item label="执教年限">
          <el-input-number v-model="form.experienceYears" :min="0" :max="50" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
          <el-button type="danger" @click="handleDelete">删除教练</el-button>
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
const coachId = route.params.id

const loading = ref(false)
const saving = ref(false)
const coach = ref(null)
const teams = ref([])
const formRef = ref(null)

const form = reactive({
  name: '',
  teamId: null,
  roleTitle: '',
  nationality: '',
  experienceYears: null
})

const rules = {
  name: [{ required: true, message: '请输入教练姓名', trigger: 'blur' }]
}

async function fetchCoach() {
  loading.value = true
  try {
    const res = await request.get(`/api/coaches/${coachId}`)
    coach.value = res.data
    Object.assign(form, {
      name: res.data.name,
      teamId: res.data.teamId,
      roleTitle: res.data.roleTitle,
      nationality: res.data.nationality,
      experienceYears: res.data.experienceYears
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
    await request.put(`/api/coaches/${coachId}`, form)
    ElMessage.success('保存成功')
    fetchCoach()
  } finally {
    saving.value = false
  }
}

async function handleDelete() {
  await ElMessageBox.confirm('确定删除该教练？此操作不可撤销', '警告', { type: 'warning' })
  await request.delete(`/api/coaches/${coachId}`)
  ElMessage.success('删除成功')
  router.push('/app/coaches')
}

onMounted(() => {
  fetchCoach()
  fetchTeams()
})
</script>

<style scoped>
.coach-manage {
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
