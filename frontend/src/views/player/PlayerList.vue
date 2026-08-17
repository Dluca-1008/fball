<template>
  <div class="page-shell">
    <div class="page-header">
      <div class="header-left">
        <div class="header-icon players">👤</div>
        <div>
          <h1 class="header-title">球员管理</h1>
          <p class="header-desc">管理球员信息与阵容</p>
        </div>
      </div>
      <div class="header-actions">
        <el-input v-model="keyword" placeholder="搜索球员" clearable style="width:200px" @clear="fetchPlayers" @keyup.enter="fetchPlayers" />
        <el-button @click="fetchPlayers">搜索</el-button>
        <!-- 管理员：添加球员 -->
        <el-button type="success" class="btn-create" @click="router.push('/app/players/create')" v-if="userStore.hasPermission('team:edit')">
          <el-icon><Plus /></el-icon> 添加球员
        </el-button>
        <!-- 普通用户：注册/管理自己的球员信息 -->
        <el-button v-if="!userStore.hasPermission('team:edit')" type="primary" class="btn-register" @click="openRegisterDialog">
          <el-icon><User /></el-icon> {{ myPlayer ? '管理我的球员信息' : '注册球员' }}
        </el-button>
      </div>
    </div>

    <el-table :data="players" v-loading="loading" stripe class="player-table">
      <el-table-column prop="name" label="姓名" width="140">
        <template #default="{ row }">
          <div class="player-cell">
            <div class="player-avatar">{{ row.name?.charAt(0) || '?' }}</div>
            <span>{{ row.name }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="position" label="位置" width="100">
        <template #default="{ row }">
          <el-tag size="small" class="position-tag">{{ row.position || '-' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="number" label="号码" width="80" align="center">
        <template #default="{ row }">
          <span class="number-badge">#{{ row.number || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="teamName" label="所属球队" width="160" />
      <el-table-column prop="nationality" label="国籍" width="100" />
      <el-table-column label="身体数据" width="140">
        <template #default="{ row }">
          <span class="body-stats">{{ row.height }}cm / {{ row.weight }}kg</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120" fixed="right" v-if="userStore.hasPermission('team:edit')">
        <template #default="{ row }">
          <el-button type="primary" link @click="router.push(`/app/players/${row.id}/manage`)">管理</el-button>
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
        @size-change="fetchPlayers"
        @current-change="fetchPlayers"
      />
    </div>

    <!-- 注册/编辑球员弹窗 -->
    <el-dialog v-model="dialogVisible" :title="myPlayer ? '编辑球员信息' : '注册球员'" width="520px" class="register-dialog">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" :placeholder="myPlayer ? '修改姓名' : '请输入您的姓名'" maxlength="50" />
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
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">
          {{ myPlayer ? '保存修改' : '确认注册' }}
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
const players = ref([])
const keyword = ref('')
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const teams = ref([])

// 注册弹窗
const dialogVisible = ref(false)
const submitting = ref(false)
const formRef = ref(null)
const myPlayer = ref(null)

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

async function fetchPlayers() {
  loading.value = true
  try {
    const res = await request.get('/api/players', {
      params: { page: page.value, size: pageSize.value, keyword: keyword.value || undefined }
    })
    players.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

async function fetchMyPlayer() {
  try {
    const res = await request.get('/api/players/my')
    myPlayer.value = res.data
    if (res.data) {
      Object.assign(form.value, {
        name: res.data.name,
        teamId: res.data.teamId,
        position: res.data.position,
        number: res.data.number,
        nationality: res.data.nationality,
        birthDate: res.data.birthDate,
        height: res.data.height,
        weight: res.data.weight
      })
    }
  } catch {
    myPlayer.value = null
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
  if (!myPlayer.value) {
    // 注册
    const valid = await formRef.value.validate().catch(() => false)
    if (!valid) return
    submitting.value = true
    try {
      await request.post('/api/players/register', form.value)
      ElMessage.success('注册成功！')
      dialogVisible.value = false
      fetchMyPlayer()
      fetchPlayers()
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
      await request.put(`/api/players/${myPlayer.value.id}`, form.value)
      ElMessage.success('信息已更新')
      dialogVisible.value = false
      fetchMyPlayer()
      fetchPlayers()
    } catch (e) {
      ElMessage.error('更新失败，请重试')
    } finally {
      submitting.value = false
    }
  }
}

onMounted(() => {
  fetchPlayers()
  fetchMyPlayer()
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
  background: linear-gradient(135deg, #7c3aed, #553c9a);
  border-radius: 12px; display: flex; align-items: center; justify-content: center;
  font-size: 24px; box-shadow: 0 4px 12px rgba(124, 58, 237, 0.3);
}
.header-title { font-size: 21px; font-weight: 700; color: #1a202c; margin-bottom: 2px; }
.header-desc { font-size: 13px; color: #718096; }
.header-actions { display: flex; gap: 10px; flex-wrap: wrap; }

.btn-create {
  background: linear-gradient(135deg, #67c23a, #2f891e) !important;
  border: none !important; border-radius: 10px !important;
  font-weight: 600 !important;
  box-shadow: 0 4px 12px rgba(103, 194, 58, 0.3);
  transition: transform 0.2s, box-shadow 0.2s;
}
.btn-create:hover { transform: translateY(-2px); box-shadow: 0 6px 20px rgba(103, 194, 58, 0.4); }

.btn-register {
  background: linear-gradient(135deg, #409eff, #1d4ed8) !important;
  border: none !important; border-radius: 10px !important;
  font-weight: 600 !important;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
  transition: transform 0.2s, box-shadow 0.2s;
}
.btn-register:hover { transform: translateY(-2px); box-shadow: 0 6px 20px rgba(64, 158, 255, 0.4); }

/* ── 球员表格 ── */
.player-table { background: #fff; border-radius: 14px; overflow: hidden; }
.player-cell { display: flex; align-items: center; gap: 10px; }
.player-avatar {
  width: 34px; height: 34px;
  background: linear-gradient(135deg, #7c3aed, #553c9a);
  border-radius: 50%; display: flex; align-items: center; justify-content: center;
  font-size: 14px; font-weight: 700; color: #fff; flex-shrink: 0;
}
.position-tag { background: rgba(124, 58, 237, 0.08); color: #7c3aed; border: none; font-weight: 500; }
.number-badge { font-weight: 700; color: #7c3aed; font-size: 15px; }
.body-stats { font-size: 12px; color: #718096; }

.pagination-wrap { display: flex; justify-content: flex-end; margin-top: 24px; }

/* ── 注册弹窗 ── */
:deep(.register-dialog .el-dialog__header) { padding: 20px 24px 16px; }
:deep(.register-dialog .el-dialog__body) { padding: 20px 24px; }
:deep(.register-dialog .el-dialog__footer) { padding: 12px 24px 20px; }
:deep(.el-form-item) { margin-bottom: 18px; }
</style>
