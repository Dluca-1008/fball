<template>
  <div class="shop-layout">
    <div class="shop-header">
      <el-button type="primary" @click="$router.push('/shop/products')">
        <el-icon><ShoppingBag /></el-icon> 商城
      </el-button>
      <el-button @click="$router.push('/shop/orders')">
        <el-icon><List /></el-icon> 我的订单
      </el-button>
      <div class="shop-header-right">
        <el-badge :value="cartCount" :hidden="cartCount === 0">
          <el-button @click="$router.push('/cart')">🛒 购物车</el-button>
        </el-badge>
      </div>
    </div>
    <router-view />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ShoppingBag, List } from '@element-plus/icons-vue'
import request from '@/utils/request'

const cartCount = ref(0)

async function fetchCartCount() {
  try {
    const res = await request.get('/api/cart')
    cartCount.value = res.data?.length || 0
  } catch {
    // ignore
  }
}

onMounted(fetchCartCount)
</script>

<style scoped>
.shop-layout {
  height: 100%;
}

.shop-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  background: #fff;
  border-radius: 8px;
  margin-bottom: 16px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
}

.shop-header-right {
  margin-left: auto;
}
</style>
