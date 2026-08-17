<template>
  <div class="match-manage" v-loading="loading">
    <div class="page-header">
      <h2>比赛管理</h2>
      <el-button @click="$router.back()">返回</el-button>
    </div>

    <el-card v-if="match">
      <el-tabs v-model="activeTab">
        <el-tab-pane label="比赛信息" name="info">
          <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" style="max-width: 600px;">
            <el-form-item label="比赛类型">
              <el-tag :type="match.matchType === 'cup' ? 'danger' : 'success'" size="large">{{ match.matchType === 'cup' ? '杯赛' : '联赛' }}</el-tag>
            </el-form-item>
            <el-form-item label="比赛时间" prop="matchDate">
              <el-date-picker v-model="form.matchDate" type="datetime" placeholder="请选择比赛时间" style="width: 100%;" />
            </el-form-item>
            <el-form-item label="比赛场地" prop="venue">
              <el-input v-model="form.venue" />
            </el-form-item>
            <el-form-item label="状态" prop="status">
              <el-select v-model="form.status" style="width: 100%;">
                <el-option label="未开始" :value="0" />
                <el-option label="进行中" :value="1" />
                <el-option label="已结束" :value="2" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
              <el-button type="danger" @click="handleDelete">删除比赛</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="比分录入" name="score" v-if="match.homeTeamId">
          <div class="score-section">
            <div class="score-display">
              <div class="team-score">
                <span class="team-label">{{ match.homeTeamName || '主队' }}</span>
                <el-input-number v-model="scoreForm.homeScore" :min="0" :max="99" size="large" />
              </div>
              <span class="score-divider">:</span>
              <div class="team-score">
                <span class="team-label">{{ match.awayTeamName || '客队' }}</span>
                <el-input-number v-model="scoreForm.awayScore" :min="0" :max="99" size="large" />
              </div>
            </div>
            <el-divider />
            <div class="score-actions">
              <el-button type="primary" :loading="savingScore" @click="handleUpdateScore">保存比分</el-button>
              <el-button type="success" @click="handleStartMatch" :disabled="match.status !== 0 || !match.homeTeamId">开始比赛</el-button>
              <el-button type="warning" @click="handleEndMatch" :disabled="match.status !== 1">结束比赛</el-button>
            </div>
          </div>
        </el-tab-pane>

        <el-tab-pane label="红黄牌" name="cards" v-if="match.homeTeamId">
          <div class="cards-section">
            <div class="cards-header">
              <span>红黄牌记录</span>
              <el-button type="primary" size="small" @click="cardDialogVisible = true">添加红黄牌</el-button>
            </div>

            <el-table :data="cards" stripe v-loading="cardsLoading">
              <el-table-column prop="minute" label="时间" width="70" align="center" />
              <el-table-column label="球队" width="120">
                <template #default="{ row }">
                  <span>{{ row.teamName || '球队 #' + row.teamId }}</span>
                </template>
              </el-table-column>
              <el-table-column label="球员" width="120">
                <template #default="{ row }">
                  <span>{{ row.playerName || '-' }}</span>
                </template>
              </el-table-column>
              <el-table-column label="类型" width="100" align="center">
                <template #default="{ row }">
                  <el-tag :type="row.cardType === 1 ? 'warning' : 'danger'" size="small">
                    {{ row.cardType === 1 ? '🟨 黄牌' : '🟥 红牌' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="createdAt" label="记录时间" />
            </el-table>

            <el-empty v-if="!cardsLoading && cards.length === 0" description="暂无红黄牌记录" />
          </div>
        </el-tab-pane>

        <el-tab-pane v-if="match.matchType === 'cup'" label="分组赛" name="groupStage">
          <div class="section-header">
            <span>分组赛管理</span>
            <el-button type="primary" size="small" @click="groupDialogVisible = true">初始化分组</el-button>
          </div>
          <div v-for="(teams, groupName) in groupStandings" :key="groupName" style="margin-bottom: 24px;">
            <h4>{{ groupName }}组</h4>
            <el-table :data="teams" stripe border size="small">
              <el-table-column type="index" label="#" width="50" />
              <el-table-column prop="teamName" label="球队" />
              <el-table-column prop="played" label="场次" width="70" />
              <el-table-column prop="won" label="胜" width="60" />
              <el-table-column prop="drawn" label="平" width="60" />
              <el-table-column prop="lost" label="负" width="60" />
              <el-table-column prop="points" label="积分" width="70" />
            </el-table>
          </div>
        </el-tab-pane>

        <el-tab-pane v-if="match.matchType === 'cup'" label="淘汰赛" name="knockout">
          <div class="section-header">
            <span>淘汰赛对阵</span>
            <el-button type="primary" size="small" @click="initKnockoutFromGroups">从分组赛生成淘汰赛</el-button>
          </div>
          <div class="bracket-container">
            <div v-for="(roundMatches, round) in knockoutRounds" :key="round" class="bracket-round">
              <div class="round-title">{{ round }}</div>
              <div class="round-matches">
                <div v-for="match in roundMatches" :key="match.id" class="bracket-match">
                  <div :class="['bracket-team', match.winnerTeamId === match.homeTeamId && 'winner']">
                    <span>{{ match.homeTeamName || '待定' }}</span>
                    <el-input-number v-if="match.status !== 2" v-model="match.homeScore" :min="0" :max="99" size="small" @change="updateKnockoutScore(match)" />
                    <span v-else class="score">{{ match.homeScore }}</span>
                  </div>
                  <div :class="['bracket-team', match.winnerTeamId === match.awayTeamId && 'winner']">
                    <span>{{ match.awayTeamName || '待定' }}</span>
                    <el-input-number v-if="match.status !== 2" v-model="match.awayScore" :min="0" :max="99" size="small" @change="updateKnockoutScore(match)" />
                    <span v-else class="score">{{ match.awayScore }}</span>
                  </div>
                  <el-button v-if="match.homeScore != null && match.awayScore != null && match.status !== 2 && match.homeTeamId && match.awayTeamId"
                    type="success" size="small" @click="advanceKnockout(match)">确认晋级</el-button>
                </div>
              </div>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 添加红黄牌弹窗 -->
    <el-dialog v-model="cardDialogVisible" title="添加红黄牌" width="400px">
      <el-form :model="cardForm" label-width="80px">
        <el-form-item label="球队">
          <el-select v-model="cardForm.teamId" placeholder="选择球队" style="width:100%">
            <el-option v-for="t in teams" :key="t.id" :label="t.name" :value="t.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="球员">
          <el-select v-model="cardForm.playerId" placeholder="选择球员（可选）" filterable style="width:100%">
            <el-option v-for="p in players" :key="p.id" :label="p.name" :value="p.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="类型">
          <el-radio-group v-model="cardForm.cardType">
            <el-radio :label="1">🟨 黄牌</el-radio>
            <el-radio :label="2">🟥 红牌</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="时间">
          <el-input-number v-model="cardForm.minute" :min="1" :max="120" placeholder="分钟（可选）" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="cardDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="savingCard" @click="handleAddCard">确认</el-button>
      </template>
    </el-dialog>

    <!-- 初始化分组弹窗 -->
    <el-dialog v-model="groupDialogVisible" title="初始化分组" width="600px">
      <el-form label-width="80px">
        <el-form-item v-for="(group, index) in groupForm" :key="index" :label="'第' + (index + 1) + '组'">
          <el-select v-model="groupForm[index].teamIds" multiple placeholder="选择球队" filterable style="width: 100%;">
            <el-option v-for="t in teams" :key="t.id" :label="t.name" :value="t.id" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" link @click="addGroup">添加分组</el-button>
          <el-button type="danger" link @click="removeGroup" :disabled="groupForm.length <= 1">删除分组</el-button>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="groupDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleInitGroups">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const route = useRoute()
const router = useRouter()
const matchId = route.params.id

const activeTab = ref('info')
const loading = ref(false)
const saving = ref(false)
const savingScore = ref(false)
const savingCard = ref(false)
const cardsLoading = ref(false)
const match = ref(null)
const teams = ref([])
const players = ref([])
const groupStandings = ref({})
const knockouts = ref([])
const cards = ref([])
const formRef = ref(null)
const groupDialogVisible = ref(false)
const cardDialogVisible = ref(false)

const groupForm = ref([{ teamIds: [] }])
const cardForm = ref({ teamId: null, playerId: null, cardType: 1, minute: null })

const form = reactive({ matchDate: '', venue: '', status: 0 })
const scoreForm = reactive({ homeScore: 0, awayScore: 0 })
const rules = {
  matchDate: [{ required: true, message: '请选择比赛时间', trigger: 'change' }],
  venue: [{ required: true, message: '请输入比赛场地', trigger: 'blur' }]
}

const knockoutRounds = computed(() => {
  const rounds = {}
  knockouts.value.forEach(k => {
    if (!rounds[k.round]) rounds[k.round] = []
    rounds[k.round].push(k)
  })
  return rounds
})

async function fetchMatch() {
  loading.value = true
  try {
    const res = await request.get(`/api/matches/${matchId}`)
    match.value = res.data
    Object.assign(form, { matchDate: res.data.matchDate, venue: res.data.venue, status: res.data.status })
    scoreForm.homeScore = res.data.homeScore || 0
    scoreForm.awayScore = res.data.awayScore || 0
  } finally { loading.value = false }
}

async function fetchTeams() {
  try {
    const res = await request.get('/api/teams/list')
    teams.value = res.data || []
  } catch { /* ignore */ }
}

async function fetchPlayers() {
  try {
    const homeId = match.value?.homeTeamId
    const awayId = match.value?.awayTeamId
    const teamIds = [homeId, awayId].filter(Boolean)
    if (teamIds.length === 0) return
    const res = await request.get('/api/players', { params: { teamIds: teamIds.join(','), page: 1, size: 200 } })
    players.value = (res.data.records) || []
  } catch { /* ignore */ }
}

async function fetchCards() {
  cardsLoading.value = true
  try {
    const res = await request.get(`/api/matches/${matchId}/cards`)
    cards.value = res.data || []
  } catch { /* ignore */ } finally {
    cardsLoading.value = false
  }
}

async function fetchGroupStandings() {
  try {
    const res = await request.get(`/api/matches/${matchId}/groups/standings`)
    groupStandings.value = res.data || {}
  } catch (e) { /* ignore */ }
}

async function fetchKnockouts() {
  try {
    const res = await request.get(`/api/matches/${matchId}/knockouts`)
    knockouts.value = res.data || []
  } catch (e) { /* ignore */ }
}

async function handleInitGroups() {
  const groupMap = {}
  groupForm.value.forEach((g, i) => {
    if (g.teamIds.length > 0) {
      groupMap[String.fromCharCode(65 + i)] = g.teamIds
    }
  })
  if (Object.keys(groupMap).length === 0) { ElMessage.warning('请至少添加一个分组'); return }
  await request.post(`/api/matches/${matchId}/groups/init`, groupMap)
  ElMessage.success('分组初始化成功')
  groupDialogVisible.value = false
  fetchGroupStandings()
}

async function initKnockoutFromGroups() {
  if (Object.keys(groupStandings.value).length > 0) {
    let topTeams = []
    Object.keys(groupStandings.value).forEach(groupName => {
      const teams = groupStandings.value[groupName]
      if (teams.length >= 2) {
        topTeams.push(teams[0].teamId)
        topTeams.push(teams[1].teamId)
      }
    })
    if (topTeams.length < 2) { ElMessage.warning('分组赛数据不足'); return }
    const powerOf2 = Math.pow(2, Math.ceil(Math.log2(topTeams.length)))
    while (topTeams.length < powerOf2) topTeams.push(null)
    await request.post(`/api/matches/${matchId}/knockouts/init`, { teamIds: topTeams })
  } else {
    ElMessage.warning('请先初始化分组赛')
    return
  }
  ElMessage.success('淘汰赛对阵已生成')
  fetchKnockouts()
}

async function updateKnockoutScore(knockout) {
  if (knockout.homeScore == null || knockout.awayScore == null) return
  try {
    await request.put(`/api/matches/knockouts/${knockout.id}/score`, {
      homeScore: knockout.homeScore, awayScore: knockout.awayScore
    })
  } catch { /* silent - will re-fetch on next tab switch */ }
}

async function advanceKnockout(knockout) {
  await request.put(`/api/matches/knockouts/${knockout.id}/advance`)
  ElMessage.success('晋级成功')
  fetchKnockouts()
}

async function handleSave() {
  await formRef.value.validate()
  saving.value = true
  try { await request.put(`/api/matches/${matchId}`, { ...form, name: match.value?.name }); ElMessage.success('保存成功'); fetchMatch() }
  finally { saving.value = false }
}

async function handleUpdateScore() {
  savingScore.value = true
  try {
    await request.put(`/api/matches/${matchId}`, { homeScore: scoreForm.homeScore, awayScore: scoreForm.awayScore, status: match.value.status })
    ElMessage.success('比分更新成功'); fetchMatch()
  } finally { savingScore.value = false }
}

async function handleStartMatch() { await ElMessageBox.confirm('确定开始比赛？'); await request.put(`/api/matches/${matchId}`, { status: 1 }); ElMessage.success('比赛已开始'); fetchMatch() }
async function handleEndMatch() { await ElMessageBox.confirm('确定结束比赛？'); await request.put(`/api/matches/${matchId}`, { homeScore: scoreForm.homeScore, awayScore: scoreForm.awayScore, status: 2 }); ElMessage.success('比赛已结束'); fetchMatch() }
async function handleDelete() { await ElMessageBox.confirm('确定删除该比赛？', '警告', { type: 'warning' }); await request.delete(`/api/matches/${matchId}`); ElMessage.success('删除成功'); router.push('/app/matches') }

async function handleAddCard() {
  if (!cardForm.value.teamId) { ElMessage.warning('请选择球队'); return }
  savingCard.value = true
  try {
    await request.post(`/api/matches/${matchId}/cards`, {
      teamId: cardForm.value.teamId,
      playerId: cardForm.value.playerId,
      cardType: cardForm.value.cardType,
      minute: cardForm.value.minute
    })
    ElMessage.success('添加成功')
    cardDialogVisible.value = false
    cardForm.value = { teamId: null, playerId: null, cardType: 1, minute: null }
    fetchCards()
  } finally { savingCard.value = false }
}

function addGroup() { groupForm.value.push({ teamIds: [] }) }
function removeGroup() { if (groupForm.value.length > 1) groupForm.value.pop() }

onMounted(() => {
  fetchMatch()
  fetchTeams()
  fetchPlayers()
  fetchCards()
  fetchGroupStandings()
  fetchKnockouts()
})
</script>

<style scoped>
.match-manage { background: white; padding: 20px; border-radius: 8px; }
.page-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 20px; }
.section-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 12px; }
.score-section { max-width: 600px; }
.score-display { display: flex; align-items: center; justify-content: center; gap: 24px; padding: 20px 0; }
.team-score { display: flex; flex-direction: column; align-items: center; gap: 8px; }
.team-label { font-size: 14px; color: #606266; font-weight: 500; }
.score-divider { font-size: 32px; font-weight: bold; color: #c0c4cc; }
.score-actions { display: flex; gap: 12px; justify-content: center; }

.cards-section { max-width: 800px; }
.cards-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 12px; }

.bracket-container { display: flex; gap: 40px; overflow-x: auto; padding: 20px 0; }
.bracket-round { display: flex; flex-direction: column; gap: 20px; min-width: 200px; }
.round-title { text-align: center; font-weight: bold; color: #409eff; padding: 8px; background: #ecf5ff; border-radius: 4px; }
.round-matches { display: flex; flex-direction: column; gap: 20px; justify-content: center; flex: 1; }
.bracket-match { border: 1px solid #dcdfe6; border-radius: 6px; padding: 12px; background: #fafafa; }
.bracket-team { display: flex; align-items: center; justify-content: space-between; padding: 6px 0; }
.bracket-team.winner { color: #67c23a; font-weight: bold; }
.bracket-team .score { font-weight: bold; min-width: 30px; text-align: center; }
</style>
