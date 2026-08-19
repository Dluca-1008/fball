<template>
  <div class="page-shell">
    <div class="page-header">
      <div class="header-left">
        <div class="header-icon coach">🎩</div>
        <div>
          <h1 class="header-title">教练管理</h1>
          <p class="header-desc">管理球队教练阵容</p>
        </div>
      </div>
      <div class="header-actions">
        <el-input v-model="keyword" placeholder="搜索教练" clearable style="width:200px" @clear="fetchCoaches" @keyup.enter="fetchCoaches" />
        <el-button @click="fetchCoaches">搜索</el-button>

        <!-- 注册/管理自己的教练员信息 -->
        <el-button type="primary" class="btn-register" @click="openRegisterDialog">
          <el-icon><User /></el-icon> {{ myCoach ? '管理我的教练员信息' : '注册教练员' }}
        </el-button>
      </div>
    </div>

    <el-table :data="coaches" v-loading="loading" stripe class="coach-table">
      <el-table-column prop="name" label="姓名" width="140">
        <template #default="{ row }">
          <div class="coach-cell">
            <div class="coach-avatar">{{ row.name?.charAt(0) || '?' }}</div>
            <span>{{ row.name }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="roleTitle" label="职位" width="130">
        <template #default="{ row }">
          <el-tag size="small" class="role-tag">{{ row.roleTitle || '-' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="teamName" label="所属球队" width="160" />
      <el-table-column prop="nationality" label="国籍" width="100" />
      <el-table-column prop="experienceYears" label="执教年限" width="100" align="center">
        <template #default="{ row }">
          <span class="exp-badge">{{ row.experienceYears }}年</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120" fixed="right" v-if="userStore.hasPermission('team:edit')">
        <template #default="{ row }">
          <el-button type="primary" link @click="$router.push(`/app/coaches/${row.id}/manage`)">管理</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination-wrap" v-if="total > 0">
      <el-pagination
        v-model:current-page="page"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50]"
        :total="total"
        layout="total, sizes, prev, pager, next"
        @size-change="fetchCoaches"
        @current-change="fetchCoaches"
      />
    </div>

    <!-- 注册/编辑教练弹窗 -->
    <el-dialog v-model="dialogVisible" :title="myCoach ? '编辑教练员信息' : '注册教练员'" width="520px" class="register-dialog">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" :placeholder="myCoach ? '修改姓名' : '请输入您的姓名'" maxlength="50" />
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
          <el-input v-model="form.nationality" placeholder="如：中国" maxlength="50" />
        </el-form-item>
        <el-form-item label="执教年限">
          <el-input-number v-model="form.experienceYears" :min="0" :max="50" style="width: 120px;" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">
          {{ myCoach ? '保存修改' : '确认注册' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'
import { Plus, User } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const coaches = ref([])
const keyword = ref('')
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const teams = ref([])

// 注册弹窗
const dialogVisible = ref(false)
const submitting = ref(false)
const formRef = ref(null)
const myCoach = ref(null)

const form = ref({
  name: '',
  roleTitle: '',
  nationality: '',
  experienceYears: null
})

const rules = {
  name: [{ required: true, message: '请输入您的姓名', trigger: 'blur' }],
  roleTitle: [{ required: true, message: '请选择您的职位', trigger: 'change' }]
}

async function fetchCoaches() {
  loading.value = true
  try {
    const res = await request.get('/api/coaches', {
      params: { page: page.value, size: pageSize.value, keyword: keyword.value || undefined }
    })
    coaches.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

async function fetchMyCoach() {
  try {
    const res = await request.get('/api/coaches/my')
    myCoach.value = res.data
    if (res.data) {
      Object.assign(form.value, {
        name: res.data.name,
        roleTitle: res.data.roleTitle,
        nationality: res.data.nationality,
        experienceYears: res.data.experienceYears
      })
    }
  } catch {
    myCoach.value = null
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

function openRegisterDialog() {
  dialogVisible.value = true
}

async function handleSubmit() {
  if (!myCoach.value) {
    // 注册
    const valid = await formRef.value.validate().catch(() => false)
    if (!valid) return
    submitting.value = true
    try {
      await request.post('/api/coaches/register', form.value)
      ElMessage.success('注册成功！')
      dialogVisible.value = false
      fetchMyCoach()
      fetchCoaches()
    } catch (e) {
      const msg = e?.response?.data?.message || e?.message || '注册失败，请重试'
      ElMessage.error(msg)
    } finally {
      submitting.value = false
    }
  } else {
    // 编辑
    submitting.value = true
    try {
      await request.put(`/api/coaches/${myCoach.value.id}`, form.value)
      ElMessage.success('信息已更新')
      dialogVisible.value = false
      fetchMyCoach()
      fetchCoaches()
    } catch (e) {
      ElMessage.error('更新失败，请重试')
    } finally {
      submitting.value = false
    }
  }
}

onMounted(() => {
  fetchCoaches()
  fetchMyCoach()
  fetchTeams()
})
</script>

<style scoped>
.page-shell { max-width: 1000px; margin: 0 auto; }

.page-header {
  display: flex; align-items: center; justify-content: space-between;
  margin-bottom: 24px; flex-wrap: wrap; gap: 12px;
}
.header-left { display: flex; align-items: center; gap: 14px; }
.header-icon {
  width: 48px; height: 48px;
  background: linear-gradient(135deg, #e6a23c, #c45d0e);
  border-radius: 12px; display: flex; align-items: center; justify-content: center;
  font-size: 24px; box-shadow: 0 4px 12px rgba(230, 162, 60, 0.3);
}
.header-title { font-size: 21px; font-weight: 700; color: #1a202c; margin-bottom: 2px; }
.header-desc { font-size: 13px; color: #718096; }
.header-actions { display: flex; gap: 10px; flex-wrap: wrap; }

.btn-register {
  background: linear-gradient(135deg, #409eff, #1d4ed8) !important;
  border: none !important; border-radius: 10px !important;
  font-weight: 600 !important;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
  transition: transform 0.2s, box-shadow 0.2s;
}
.btn-register:hover { transform: translateY(-2px); box-shadow: 0 6px 20px rgba(64, 158, 255, 0.4); }

.coach-table { background: #fff; border-radius: 14px; overflow: hidden; }
.coach-cell { display: flex; align-items: center; gap: 10px; }
.coach-avatar {
  width: 34px; height: 34px;
  background: linear-gradient(135deg, #e6a23c, #c45d0e);
  border-radius: 50%; display: flex; align-items: center; justify-content: center;
  font-size: 14px; font-weight: 700; color: #fff; flex-shrink: 0;
}
.role-tag { background: rgba(230, 162, 60, 0.08); color: #c45d0e; border: none; font-weight: 500; }
.exp-badge { font-weight: 700; color: #c45d0e; }

.pagination-wrap { display: flex; justify-content: flex-end; margin-top: 24px; }

/* ── 注册弹窗 ── */
:deep(.register-dialog .el-dialog__header) { padding: 20px 24px 16px; }
:deep(.register-dialog .el-dialog__body) { padding: 20px 24px; }
:deep(.register-dialog .el-dialog__footer) { padding: 12px 24px 20px; }
:deep(.el-form-item) { margin-bottom: 18px; }
</style>

