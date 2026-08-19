<template>
  <div class="team-create">
    <div class="page-header">
      <h2>创建球队</h2>
    </div>
    <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" style="max-width: 600px;">
      <el-divider content-position="left">球队信息</el-divider>
      <el-form-item label="球队名称" prop="name">
        <el-input v-model="form.name" placeholder="请输入球队名称" />
      </el-form-item>
      <el-form-item label="城市" prop="city">
        <el-input v-model="form.city" placeholder="请输入城市" />
      </el-form-item>
      <el-form-item label="国家" prop="country">
        <el-input v-model="form.country" placeholder="请输入国家" />
      </el-form-item>
      <el-form-item label="主场" prop="stadium">
        <el-input v-model="form.stadium" placeholder="请输入主场名称" />
      </el-form-item>
      <el-form-item label="简介" prop="description">
        <el-input v-model="form.description" type="textarea" :rows="4" placeholder="请输入球队简介" />
      </el-form-item>

      <el-divider content-position="left">您的身份</el-divider>
      <el-form-item label="身份类型" prop="memberType">
        <el-radio-group v-model="form.memberType">
          <el-radio value="player">球员</el-radio>
          <el-radio value="coach">教练</el-radio>
        </el-radio-group>
      </el-form-item>

      <template v-if="form.memberType === 'player'">
        <el-form-item label="姓名" prop="playerInfo.name">
          <el-input v-model="form.playerInfo.name" placeholder="请输入您的姓名" />
        </el-form-item>
        <el-form-item label="位置" prop="playerInfo.position">
          <el-select v-model="form.playerInfo.position" placeholder="请选择位置" style="width: 100%;">
            <el-option label="前锋" value="前锋" />
            <el-option label="中场" value="中场" />
            <el-option label="后卫" value="后卫" />
            <el-option label="守门员" value="守门员" />
          </el-select>
        </el-form-item>
        <el-form-item label="号码">
          <el-input-number v-model="form.playerInfo.number" :min="1" :max="99" />
        </el-form-item>
        <el-form-item label="国籍">
          <el-input v-model="form.playerInfo.nationality" placeholder="请输入国籍" />
        </el-form-item>
      </template>

      <template v-if="form.memberType === 'coach'">
        <el-form-item label="姓名" prop="coachInfo.name">
          <el-input v-model="form.coachInfo.name" placeholder="请输入您的姓名" />
        </el-form-item>
        <el-form-item label="职位" prop="coachInfo.roleTitle">
          <el-select v-model="form.coachInfo.roleTitle" placeholder="请选择职位" style="width: 100%;">
            <el-option label="主教练" value="主教练" />
            <el-option label="助理教练" value="助理教练" />
            <el-option label="体能教练" value="体能教练" />
            <el-option label="守门员教练" value="守门员教练" />
          </el-select>
        </el-form-item>
        <el-form-item label="国籍">
          <el-input v-model="form.coachInfo.nationality" placeholder="请输入国籍" />
        </el-form-item>
        <el-form-item label="执教年限">
          <el-input-number v-model="form.coachInfo.experienceYears" :min="0" :max="50" />
        </el-form-item>
      </template>

      <el-form-item>
        <el-button type="primary" :loading="loading" @click="handleSubmit">创建</el-button>
        <el-button @click="$router.back()">取消</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref(null)
const loading = ref(false)

const nickname = computed(() => userStore.userInfo?.nickname || userStore.userInfo?.username || '')

const form = reactive({
  name: '',
  city: '',
  country: '',
  stadium: '',
  description: '',
  memberType: 'player',
  playerInfo: { name: nickname.value, position: '', number: null, nationality: '' },
  coachInfo: { name: nickname.value, roleTitle: '', nationality: '', experienceYears: null }
})

const rules = {
  name: [{ required: true, message: '请输入球队名称', trigger: 'blur' }],
  city: [{ required: true, message: '请输入城市', trigger: 'blur' }],
  country: [{ required: true, message: '请输入国家', trigger: 'blur' }],
  memberType: [{ required: true, message: '请选择身份类型', trigger: 'change' }]
}

async function handleSubmit() {
  await formRef.value.validate()
  const memberInfo = form.memberType === 'player' ? form.playerInfo : form.coachInfo
  if (!memberInfo.name) {
    ElMessage.error('请输入您的姓名')
    return
  }
  loading.value = true
  try {
    const res = await request.post('/api/teams', {
      name: form.name,
      city: form.city,
      country: form.country,
      stadium: form.stadium,
      description: form.description,
      memberType: form.memberType,
      memberInfo
    })
    ElMessage.success('球队创建成功')
    await userStore.fetchPermissions()
    router.push(`/app/teams/${res.data.id}`)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.team-create {
  background: white;
  padding: 20px;
  border-radius: 8px;
}

.page-header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}
</style>
