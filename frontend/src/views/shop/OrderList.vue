<template>
  <div class="order-list-page">
    <div class="page-header">
      <el-button class="btn-back" @click="$router.push('/shop/products')">
        <el-icon><ArrowLeft /></el-icon> 返回商城
      </el-button>
      <div class="header-left">
        <div class="header-icon orders">📋</div>
        <div>
          <h1 class="header-title">我的订单</h1>
          <p class="header-desc">查看和管理您的订单</p>
        </div>
      </div>
    </div>

    <div class="order-cards" v-loading="loading">
      <el-empty v-if="!loading && orders.length === 0" description="暂无订单" />

      <div v-for="order in orders" :key="order.id" class="order-card">
        <div class="order-card-top">
          <span class="order-no">订单号：{{ order.orderNo }}</span>
          <el-tag :type="getStatusType(order.status)" size="small" class="status-tag">
            {{ getStatusText(order.status) }}
          </el-tag>
        </div>
        <div class="order-card-body">
          <div class="order-product">
            <span class="product-name">{{ order.productName }}</span>
            <span class="product-qty">×{{ order.quantity }}</span>
          </div>
          <span class="order-amount">¥{{ order.totalAmount }}</span>
        </div>
        <div class="order-card-footer">
          <span class="order-time">{{ order.createdAt }}</span>
          <el-button
            v-if="order.status === 0"
            type="primary"
            size="small"
            round
            @click="payOrder(order.id)"
          >去支付</el-button>
        </div>
      </div>
    </div>

    <div class="pagination-wrap" v-if="total > 0">
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
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'
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
.order-list-page { }

.page-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 24px;
  flex-wrap: wrap;
}
.btn-back {
  border-radius: 10px !important;
  border: 2px solid #e2e8f0 !important;
  font-weight: 500 !important;
  transition: all 0.2s;
}
.btn-back:hover { border-color: #409eff !important; color: #409eff !important; }

.header-left { display: flex; align-items: center; gap: 14px; }
.header-icon {
  width: 48px; height: 48px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border-radius: 12px;
  display: flex; align-items: center; justify-content: center;
  font-size: 24px;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.35);
}
.header-title { font-size: 21px; font-weight: 700; color: #1a202c; margin-bottom: 2px; }
.header-desc { font-size: 13px; color: #718096; }

/* ── 订单卡片 ── */
.order-cards { display: flex; flex-direction: column; gap: 12px; }

.order-card {
  background: #fff;
  border-radius: 14px;
  padding: 16px 20px;
  border: 1px solid rgba(0,0,0,0.05);
  transition: box-shadow 0.2s, border-color 0.2s;
}
.order-card:hover {
  box-shadow: 0 4px 16px rgba(0,0,0,0.07);
  border-color: rgba(102, 126, 234, 0.3);
}

.order-card-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 10px;
}
.order-no { font-size: 12px; color: #a0aec0; }
.status-tag { font-weight: 500; }

.order-card-body {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px 0;
}
.order-product { display: flex; align-items: center; gap: 8px; }
.product-name { font-size: 15px; font-weight: 600; color: #1a202c; }
.product-qty { font-size: 13px; color: #718096; }
.order-amount { font-size: 18px; font-weight: 800; color: #e53e3e; }

.order-card-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 10px;
  padding-top: 10px;
  border-top: 1px solid #f7fafc;
}
.order-time { font-size: 12px; color: #a0aec0; }

.pagination-wrap { display: flex; justify-content: flex-end; margin-top: 24px; }
</style>
