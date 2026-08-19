<template>
  <div class="player-create">
    <div class="page-header">
      <h2>添加球员</h2>
      <el-button @click="$router.back()">返回</el-button>
    </div>
    <el-form ref="formRef" :model="form" :rules="rules" label-width="80px" style="max-width: 600px;">
      <el-form-item label="姓名" prop="name">
        <el-input v-model="form.name" placeholder="请输入球员姓名" />
      </el-form-item>
      <el-form-item label="位置" prop="position">
        <el-select v-model="form.position" placeholder="请选择位置" style="width: 100%;">
          <el-option label="前锋" value="前锋" />
          <el-option label="中场" value="中场" />
          <el-option label="后卫" value="后卫" />
          <el-option label="守门员" value="守门员" />
        </el-select>
      </el-form-item>
      <el-form-item label="号码" prop="number">
        <el-input-number v-model="form.number" :min="1" :max="99" />
      </el-form-item>
      <el-form-item label="国籍" prop="nationality">
        <el-input v-model="form.nationality" placeholder="请输入国籍" />
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

async function fetchTeams() {
  const res = await request.get('/api/teams', { params: { page: 1, size: 200 } })
  teams.value = res.data.records
}

async function handleSubmit() {
  await formRef.value.validate()
  loading.value = true
  try {
    await request.post('/api/players', form)
    ElMessage.success('球员添加成功')
    router.push('/app/players')
  } finally {
    loading.value = false
  }
}

onMounted(fetchTeams)
</script>

<style scoped>
.player-create {
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
