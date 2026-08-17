<template>
  <div class="page-shell" v-loading="loading">
    <div class="page-header">
      <div class="header-left">
        <div class="header-icon">🤝</div>
        <div>
          <h1 class="header-title">友谊赛邀请</h1>
          <p class="header-desc">邀请其他球队进行友谊赛，管理你的邀请</p>
        </div>
      </div>
      <el-button type="primary" @click="showInviteDialog = true">
        <el-icon><Plus /></el-icon> 发起邀请
      </el-button>
    </div>

    <!-- tabs -->
    <el-tabs v-model="activeTab" class="friend-tabs">
      <el-tab-pane label="已发出的邀请" name="sent">
        <div class="request-list" v-loading="listLoading">
          <el-empty v-if="!listLoading && sentRequests.length === 0" description="暂无已发出的邀请" />
          <div
            v-for="req in sentRequests"
            :key="req.id"
            class="request-item"
          >
            <div class="request-info">
              <span class="request-label">→</span>
              <span class="request-text">邀请球队 #{{ req.receiverTeamId }}</span>
              <span class="request-date">{{ formatDate(req.matchDate) }}</span>
              <span class="request-venue">{{ req.venue || '待定' }}</span>
            </div>
            <div class="request-status">
              <el-tag v-if="req.status === 0" type="warning" size="small">待处理</el-tag>
              <el-tag v-else-if="req.status === 1" type="success" size="small">已接受</el-tag>
              <el-tag v-else type="danger" size="small">已拒绝</el-tag>
            </div>
          </div>
        </div>
      </el-tab-pane>

      <el-tab-pane label="收到的邀请" name="received">
        <div class="request-list" v-loading="listLoading">
          <el-empty v-if="!listLoading && receivedRequests.length === 0" description="暂无收到的邀请" />
          <div
            v-for="req in receivedRequests"
            :key="req.id"
            class="request-item"
          >
            <div class="request-info">
              <span class="request-label">←</span>
              <span class="request-text">来自球队 #{{ req.senderTeamId }}</span>
              <span class="request-date">{{ formatDate(req.matchDate) }}</span>
              <span class="request-venue">{{ req.venue || '待定' }}</span>
            </div>
            <div class="request-status">
              <el-tag v-if="req.status === 0" type="warning" size="small">待处理</el-tag>
              <el-tag v-else-if="req.status === 1" type="success" size="small">已接受</el-tag>
              <el-tag v-else type="danger" size="small">已拒绝</el-tag>
            </div>
            <div v-if="req.status === 0" class="request-actions">
              <el-button size="small" type="success" @click="respondRequest(req.id, true)">接受</el-button>
              <el-button size="small" type="danger" @click="respondRequest(req.id, false)">拒绝</el-button>
            </div>
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>

    <!-- 发起邀请弹窗 -->
    <el-dialog v-model="showInviteDialog" title="发起友谊赛邀请" width="480px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="邀请球队" prop="receiverTeamId">
          <el-select v-model="form.receiverTeamId" placeholder="请选择" style="width:100%">
            <el-option
              v-for="team in otherTeams"
              :key="team.id"
              :label="team.name"
              :value="team.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="比赛时间" prop="matchDate">
          <el-date-picker
            v-model="form.matchDate"
            type="datetime"
            placeholder="选择比赛时间"
            style="width:100%"
            format="YYYY-MM-DD HH:mm"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>
        <el-form-item label="比赛场地" prop="venue">
          <el-input v-model="form.venue" placeholder="请输入比赛场地" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showInviteDialog = false">取消</el-button>
        <el-button type="primary" :loading="sending" @click="sendInvite">发送邀请</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

const loading = ref(false)
const listLoading = ref(false)
const activeTab = ref('sent')
const sentRequests = ref([])
const receivedRequests = ref([])
const showInviteDialog = ref(false)
const sending = ref(false)
const otherTeams = ref([])
const formRef = ref(null)

const form = ref({ receiverTeamId: null, matchDate: null, venue: '' })
const rules = {
  receiverTeamId: [{ required: true, message: '请选择邀请球队', trigger: 'change' }],
  matchDate: [{ required: true, message: '请选择比赛时间', trigger: 'change' }],
  venue: [{ required: true, message: '请输入比赛场地', trigger: 'blur' }]
}

function formatDate(dateStr) {
  if (!dateStr) return '-'
  const d = new Date(dateStr)
  if (isNaN(d.getTime())) return '-'
  return d.toLocaleString('zh-CN', { month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' })
}

async function fetchRequests() {
  listLoading.value = true
  try {
    const [s, r] = await Promise.all([
      request.get('/api/matches/friendly/sent'),
      request.get('/api/matches/friendly/received')
    ])
    sentRequests.value = s.data || []
    receivedRequests.value = r.data || []
  } catch { /* ignored */ } finally {
    listLoading.value = false
  }
}

async function fetchOtherTeams() {
  try {
    const res = await request.get('/api/teams/list')
    otherTeams.value = res.data || []
  } catch { /* ignored */ }
}

async function sendInvite() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  sending.value = true
  try {
    await request.post('/api/matches/friendly/invite', {
      receiverTeamId: form.value.receiverTeamId,
      matchDate: form.value.matchDate,
      venue: form.value.venue
    })
    ElMessage.success('邀请已发送')
    showInviteDialog.value = false
    form.value = { receiverTeamId: null, matchDate: null, venue: '' }
    fetchRequests()
  } catch {
    // 错误已由拦截器统一提示，这里吞掉避免未处理的 Promise 拒绝
  } finally {
    sending.value = false
  }
}

async function respondRequest(requestId, accept) {
  try {
    await request.put(`/api/matches/friendly/requests/${requestId}/respond`, { accept })
    ElMessage.success(accept ? '已接受邀请' : '已拒绝邀请')
    fetchRequests()
  } catch { /* ignored */ }
}

onMounted(async () => {
  loading.value = true
  await Promise.all([fetchRequests(), fetchOtherTeams()])
  loading.value = false
})
</script>

<style scoped>
.page-shell { max-width: 900px; margin: 0 auto; }

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24px;
  gap: 16px;
}
.header-left { display: flex; align-items: center; gap: 16px; }
.header-icon {
  width: 52px; height: 52px;
  background: linear-gradient(135deg, #409eff, #1d4ed8);
  border-radius: 14px;
  display: flex; align-items: center; justify-content: center;
  font-size: 26px;
  box-shadow: 0 4px 14px rgba(64, 158, 255, 0.35);
}
.header-title { font-size: 22px; font-weight: 700; color: #1a202c; margin-bottom: 2px; }
.header-desc { font-size: 13px; color: #718096; }

.friend-tabs { margin-top: 8px; }
:deep(.el-tabs__header) { margin-bottom: 16px; }
:deep(.el-tabs__item) { font-size: 15px; font-weight: 600; }

.request-list { display: flex; flex-direction: column; gap: 10px; }

.request-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 16px;
  background: #fff;
  border-radius: 12px;
  border: 1px solid #e8ecf1;
  transition: box-shadow 0.2s;
}
.request-item:hover { box-shadow: 0 4px 12px rgba(0,0,0,0.06); }

.request-info { display: flex; align-items: center; gap: 10px; flex: 1; min-width: 0; }
.request-label {
  font-size: 18px; color: #409eff; flex-shrink: 0;
}
.request-text {
  font-size: 14px; font-weight: 500; color: #2d3748;
  white-space: nowrap; overflow: hidden; text-overflow: ellipsis;
}
.request-date { font-size: 12px; color: #718096; white-space: nowrap; }
.request-venue { font-size: 12px; color: #a0aec0; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }

.request-status { flex-shrink: 0; }
.request-actions { display: flex; gap: 6px; flex-shrink: 0; }
</style>
