<template>
  <div class="order-list">
    <div class="page-header">
      <h2>我的订单</h2>
    </div>
    <el-table :data="orders" v-loading="loading" stripe>
      <el-table-column prop="orderNo" label="订单号" width="200" />
      <el-table-column prop="productName" label="商品名称" />
      <el-table-column prop="quantity" label="数量" width="80" />
      <el-table-column prop="totalAmount" label="金额">
        <template #default="{ row }">
          <span class="amount">¥{{ row.totalAmount }}</span>
        </template>
      </el-table-column>
      <el-table-column label="状态">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="下单时间" />
      <el-table-column label="操作" width="120">
        <template #default="{ row }">
          <el-button v-if="row.status === 0" type="primary" link @click="payOrder(row.id)">去支付</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      v-model:current-page="page"
      v-model:page-size="pageSize"
      :page-sizes="[10, 20, 50]"
      :total="total"
      layout="total, sizes, prev, pager, next"
      @size-change="fetchOrders"
      @current-change="fetchOrders"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const loading = ref(false)
const orders = ref([])
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)

async function fetchOrders() {
  loading.value = true
  try {
    const res = await request.get('/api/orders', {
      params: { page: page.value, size: pageSize.value }
    })
    orders.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

function getStatusType(status) {
  const types = { 0: 'warning', 1: 'success', 2: 'info' }
  return types[status] || 'info'
}

function getStatusText(status) {
  const texts = { 0: '待支付', 1: '已支付', 2: '已完成' }
  return texts[status] || '未知'
}

async function payOrder(id) {
  try {
    await request.post(`/api/orders/${id}/pay`)
    ElMessage.success('支付成功')
    fetchOrders()
  } catch (error) {
    console.error(error)
  }
}

onMounted(fetchOrders)
</script>

<style scoped>
.order-list {
  background: white;
  padding: 20px;
  border-radius: 8px;
}

.page-header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.amount {
  color: #f56c6c;
  font-weight: bold;
}

.el-pagination {
  margin-top: 20px;
  justify-content: flex-end;
}
</style>
