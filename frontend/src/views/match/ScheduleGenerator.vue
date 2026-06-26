<template>
  <div class="schedule-generator">
    <div class="page-header">
      <h2>比赛日程生成</h2>
      <el-button @click="$router.back()">返回</el-button>
    </div>

    <el-card>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="120px" style="max-width: 600px;">
        <el-form-item label="赛事名称" prop="matchType">
          <el-radio-group v-model="form.matchType" @change="onTypeChange">
            <el-radio value="league">积分制联赛</el-radio>
            <el-radio value="cup">杯赛制</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="选择球队" prop="selectedTeams">
          <el-select v-model="form.selectedTeams" multiple placeholder="请选择参赛球队" filterable style="width: 100%;">
            <el-option v-for="t in teams" :key="t.id" :label="t.name" :value="t.id" />
          </el-select>
          <div class="team-count">已选 {{ form.selectedTeams.length }} 支球队</div>
        </el-form-item>

        <template v-if="form.matchType === 'cup'">
          <el-divider content-position="left">分组设置</el-divider>
          <div v-for="(group, index) in form.groups" :key="index" class="group-item">
            <el-form-item :label="'第' + (index + 1) + '组'">
              <div class="group-row">
                <el-select v-model="form.groups[index].teamIds" multiple placeholder="选择球队" filterable style="flex: 1;">
                  <el-option v-for="t in availableTeams" :key="t.id" :label="t.name" :value="t.id" />
                </el-select>
                <el-button type="danger" link @click="removeGroup(index)" :disabled="form.groups.length <= 1">删除</el-button>
              </div>
            </el-form-item>
          </div>
          <el-form-item>
            <el-button type="primary" link @click="addGroup">添加分组</el-button>
            <el-button type="success" link @click="autoDistribute">自动分组</el-button>
          </el-form-item>
        </template>

        <el-divider content-position="left">排期设置</el-divider>
        <el-form-item label="首场比赛时间" prop="startDate">
          <el-date-picker v-model="form.startDate" type="datetime" placeholder="请选择开始时间" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="比赛间隔(天)" prop="intervalDays">
          <el-input-number v-model="form.intervalDays" :min="1" :max="30" />
          <span class="hint">每轮比赛之间的间隔天数</span>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" :loading="generating" @click="handleGenerate">生成赛程</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card v-if="generatedCount > 0" style="margin-top: 20px;">
      <el-result icon="success" title="赛程生成成功" :sub-title="`共生成 ${generatedCount} 场比赛`">
        <template #extra>
          <el-button type="primary" @click="$router.push('/matches/schedule')">查看日程</el-button>
          <el-button @click="$router.push('/matches')">返回赛事列表</el-button>
        </template>
      </el-result>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const router = useRouter()
const formRef = ref(null)
const generating = ref(false)
const teams = ref([])
const generatedCount = ref(0)

const form = reactive({
  matchType: 'league',
  selectedTeams: [],
  startDate: '',
  intervalDays: 7,
  groups: [{ teamIds: [] }]
})

const rules = {
  matchType: [{ required: true, message: '请选择赛事类型', trigger: 'change' }],
  selectedTeams: [{ required: true, type: 'array', min: 2, message: '至少选择2支球队', trigger: 'change' }],
  startDate: [{ required: true, message: '请选择开始时间', trigger: 'change' }]
}

const availableTeams = computed(() => {
  const selectedIds = form.selectedTeams
  return teams.value.filter(t => selectedIds.includes(t.id))
})

function onTypeChange() {
  form.groups = [{ teamIds: [] }]
}

function addGroup() {
  form.groups.push({ teamIds: [] })
}

function removeGroup(index) {
  form.groups.splice(index, 1)
}

function autoDistribute() {
  const teamList = [...form.selectedTeams]
  const groupCount = form.groups.length || Math.ceil(teamList.length / 4)
  form.groups = []
  for (let i = 0; i < groupCount; i++) {
    form.groups.push({ teamIds: [] })
  }

  let idx = 0
  for (let round = 0; round < Math.ceil(teamList.length / groupCount); round++) {
    for (let g = 0; g < groupCount && idx < teamList.length; g++) {
      form.groups[g].teamIds.push(teamList[idx])
      idx++
    }
  }
}

async function fetchTeams() {
  const res = await request.get('/api/teams', { params: { page: 1, size: 200 } })
  teams.value = res.data.records
}

async function handleGenerate() {
  await formRef.value.validate()

  if (form.matchType === 'cup') {
    const validGroups = form.groups.filter(g => g.teamIds.length >= 2)
    if (validGroups.length === 0) {
      ElMessage.error('杯赛至少需要一个分组且每组至少2支球队')
      return
    }
  }

  generating.value = true
  try {
    const body = {
      matchType: form.matchType,
      teamIds: form.selectedTeams,
      startDate: formatDateStr(form.startDate),
      intervalDays: form.intervalDays
    }

    if (form.matchType === 'cup') {
      const groupTeams = {}
      form.groups.forEach((g, i) => {
        if (g.teamIds.length >= 2) {
          groupTeams[String.fromCharCode(65 + i)] = g.teamIds
        }
      })
      body.groupTeams = groupTeams
    }

    const res = await request.post('/api/matches/1/generate-schedule', body)
    generatedCount.value = res.data
    ElMessage.success(`成功生成 ${res.data} 场比赛`)
  } catch (e) {
    ElMessage.error('生成失败：' + (e.response?.data?.message || e.message))
  } finally {
    generating.value = false
  }
}

function formatDateStr(date) {
  if (!date) return ''
  const d = new Date(date)
  const pad = n => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}`
}

onMounted(fetchTeams)
</script>

<style scoped>
.schedule-generator {
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

.team-count {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}

.group-item {
  margin-bottom: 12px;
}

.group-row {
  display: flex;
  gap: 12px;
  align-items: center;
}

.hint {
  font-size: 12px;
  color: #909399;
  margin-left: 12px;
}
</style>
