<template>
  <div class="cart-list">
    <div class="page-header">
      <h2>购物车</h2>
      <el-button type="danger" @click="clearCart" v-if="cartItems.length > 0">清空购物车</el-button>
    </div>
    <el-table :data="cartItems" v-loading="loading" stripe>
      <el-table-column label="商品信息" min-width="300">
        <template #default="{ row }">
          <div class="product-info">
            <img v-if="row.productImage" :src="row.productImage" class="product-image" />
            <div v-else class="no-image">暂无图片</div>
            <div class="product-name">{{ row.productName }}</div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="单价" width="100">
        <template #default="{ row }">
          <span class="price">¥{{ row.productPrice }}</span>
        </template>
      </el-table-column>
      <el-table-column label="数量" width="150">
        <template #default="{ row }">
          <el-input-number v-model="row.quantity" :min="1" :max="99" @change="updateQuantity(row)" />
        </template>
      </el-table-column>
      <el-table-column label="小计" width="100">
        <template #default="{ row }">
          <span class="price">¥{{ (row.productPrice * row.quantity).toFixed(2) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="100">
        <template #default="{ row }">
          <el-button type="danger" link @click="removeFromCart(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-empty v-if="cartItems.length === 0 && !loading" description="购物车为空" />

    <div class="cart-footer" v-if="cartItems.length > 0">
      <div class="total-info">
        <span>共 {{ totalQuantity }} 件商品</span>
        <span class="total-price">合计: ¥{{ totalPrice }}</span>
      </div>
      <el-button type="primary" size="large" @click="checkout">去结算</el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const router = useRouter()

const loading = ref(false)
const cartItems = ref([])

const totalQuantity = computed(() => {
  return cartItems.value.reduce((sum, item) => sum + item.quantity, 0)
})

const totalPrice = computed(() => {
  return cartItems.value.reduce((sum, item) => sum + item.productPrice * item.quantity, 0).toFixed(2)
})

async function fetchCart() {
  loading.value = true
  try {
    const res = await request.get('/api/cart')
    cartItems.value = res.data
  } finally {
    loading.value = false
  }
}

async function updateQuantity(item) {
  try {
    await request.put(`/api/cart/${item.id}`, null, {
      params: { quantity: item.quantity }
    })
  } catch (error) {
    console.error(error)
    fetchCart()
  }
}

async function removeFromCart(id) {
  try {
    await request.delete(`/api/cart/${id}`)
    ElMessage.success('已移除')
    fetchCart()
  } catch (error) {
    console.error(error)
  }
}

async function clearCart() {
  try {
    await ElMessageBox.confirm('确定清空购物车？', '提示', { type: 'warning' })
    await request.delete('/api/cart/clear')
    ElMessage.success('购物车已清空')
    fetchCart()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

async function checkout() {
  ElMessage.success('功能开发中...')
}

onMounted(fetchCart)
</script>

<style scoped>
.cart-list {
  background: white;
  padding: 20px;
  border-radius: 8px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.product-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.product-image {
  width: 60px;
  height: 60px;
  object-fit: cover;
  border-radius: 4px;
}

.no-image {
  width: 60px;
  height: 60px;
  background: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #999;
  font-size: 12px;
  border-radius: 4px;
}

.product-name {
  flex: 1;
}

.price {
  color: #f56c6c;
  font-weight: bold;
}

.cart-footer {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 20px;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.total-info {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 4px;
}

.total-price {
  font-size: 18px;
  color: #f56c6c;
  font-weight: bold;
}
</style>
