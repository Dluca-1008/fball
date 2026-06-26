<template>
  <div class="coach-create">
    <div class="page-header">
      <h2>添加教练</h2>
      <el-button @click="$router.back()">返回</el-button>
    </div>
    <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" style="max-width: 600px;">
      <el-form-item label="姓名" prop="name">
        <el-input v-model="form.name" placeholder="请输入教练姓名" />
      </el-form-item>
      <el-form-item label="所属球队" prop="teamId">
        <el-select v-model="form.teamId" placeholder="请选择球队" filterable clearable style="width: 100%;">
          <el-option v-for="t in teams" :key="t.id" :label="t.name" :value="t.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="职位" prop="roleTitle">
        <el-select v-model="form.roleTitle" placeholder="请选择职位" style="width: 100%;">
          <el-option label="主教练" value="主教练" />
          <el-option label="助理教练" value="助理教练" />
          <el-option label="体能教练" value="体能教练" />
          <el-option label="守门员教练" value="守门员教练" />
        </el-select>
      </el-form-item>
      <el-form-item label="国籍">
        <el-input v-model="form.nationality" placeholder="请输入国籍" />
      </el-form-item>
      <el-form-item label="执教年限">
        <el-input-number v-model="form.experienceYears" :min="0" :max="50" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :loading="loading" @click="handleSubmit">添加</el-button>
        <el-button @click="$router.back()">取消</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)
const teams = ref([])

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

async function fetchTeams() {
  const res = await request.get('/api/teams', { params: { page: 1, size: 200 } })
  teams.value = res.data.records
}

async function handleSubmit() {
  await formRef.value.validate()
  loading.value = true
  try {
    await request.post('/api/coaches', form)
    ElMessage.success('教练添加成功')
    router.push('/coaches')
  } finally {
    loading.value = false
  }
}

onMounted(fetchTeams)
</script>

<style scoped>
.coach-create {
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
