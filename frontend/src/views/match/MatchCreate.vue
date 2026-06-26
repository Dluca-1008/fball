<template>
  <div class="match-create">
    <div class="page-header">
      <h2>创建比赛</h2>
      <el-button @click="$router.back()">返回</el-button>
    </div>
    <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" style="max-width: 600px;">
      <el-form-item label="比赛类型" prop="matchType">
        <el-radio-group v-model="form.matchType">
          <el-radio value="league">积分制联赛</el-radio>
          <el-radio value="cup">杯赛制</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="比赛时间" prop="matchDate">
        <el-date-picker v-model="form.matchDate" type="datetime" placeholder="请选择比赛时间" style="width: 100%;" />
      </el-form-item>
      <el-form-item label="比赛场地" prop="venue">
        <el-input v-model="form.venue" placeholder="请输入比赛场地" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :loading="loading" @click="handleSubmit">创建</el-button>
        <el-button @click="$router.back()">取消</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  matchType: 'league',
  matchDate: '',
  venue: ''
})

const rules = {
  matchType: [{ required: true, message: '请选择比赛类型', trigger: 'change' }],
  matchDate: [{ required: true, message: '请选择比赛时间', trigger: 'change' }],
  venue: [{ required: true, message: '请输入比赛场地', trigger: 'blur' }]
}

async function handleSubmit() {
  await formRef.value.validate()
  loading.value = true
  try {
    await request.post('/api/matches', form)
    ElMessage.success('比赛创建成功')
    router.push('/matches')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.match-create {
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
