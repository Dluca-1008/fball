<template>
  <div class="cart-page">
    <div class="page-header">
      <div class="header-left">
        <div class="header-icon cart">🛒</div>
        <div>
          <h1 class="header-title">购物车</h1>
          <p class="header-desc">{{ cartItems.length }} 件商品</p>
        </div>
      </div>
      <el-button type="danger" plain round @click="clearCart" v-if="cartItems.length > 0">
        <el-icon><Delete /></el-icon> 清空购物车
      </el-button>
    </div>

    <div class="cart-content" v-loading="loading">
      <el-empty v-if="!loading && cartItems.length === 0" description="购物车是空的，去逛逛吧" :image-size="120">
        <el-button type="primary" @click="$router.push('/app/shop/products')">去商城</el-button>
      </el-empty>

      <div v-if="cartItems.length > 0" class="cart-items">
        <div v-for="item in cartItems" :key="item.id" class="cart-item">
          <!-- 商品图片 -->
          <div class="item-img-wrap">
            <img v-if="item.productImage" :src="item.productImage" :alt="item.productName" />
            <div v-else class="no-img">暂无图片</div>
          </div>

          <!-- 商品信息 -->
          <div class="item-info">
            <h3 class="item-name">{{ item.productName }}</h3>
            <span class="item-unit">¥{{ item.productPrice }} / 件</span>
          </div>

          <!-- 数量 -->
          <div class="item-qty">
            <el-input-number v-model="item.quantity" :min="1" :max="99" size="small" @change="updateQuantity(item)" class="qty-control" />
          </div>

          <!-- 小计 -->
          <div class="item-subtotal">
            <span class="subtotal-price">¥{{ (item.productPrice * item.quantity).toFixed(2) }}</span>
          </div>

          <!-- 删除 -->
          <div class="item-action">
            <el-button type="danger" link size="small" @click="removeFromCart(item.id)">
              <el-icon><Delete /></el-icon>
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 结算栏 -->
    <div class="cart-footer" v-if="cartItems.length > 0">
      <div class="footer-left">
        <span class="total-count">共 {{ totalQuantity }} 件商品</span>
      </div>
      <div class="footer-right">
        <div class="total-price-wrap">
          <span class="total-label">合计</span>
          <span class="total-price">¥{{ totalPrice }}</span>
        </div>
        <el-button type="primary" size="large" round class="btn-checkout" @click="checkout">
          <el-icon><Promotion /></el-icon> 去结算
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'
import { Delete, Promotion } from '@element-plus/icons-vue'

const router = useRouter()
const loading = ref(false)
const cartItems = ref([])

const totalQuantity = computed(() => cartItems.value.reduce((sum, item) => sum + item.quantity, 0))
const totalPrice = computed(() => cartItems.value.reduce((sum, item) => sum + item.productPrice * item.quantity, 0).toFixed(2))

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
    await request.put(`/api/cart/${item.id}`, null, { params: { quantity: item.quantity } })
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
    if (error !== 'cancel') console.error(error)
  }
}

function checkout() {
  ElMessage.success('功能开发中...')
}

onMounted(fetchCart)
</script>

<style scoped>
.cart-page { max-width: 960px; margin: 0 auto; }

/* ── 页面头部 ── */
.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24px;
  flex-wrap: wrap;
  gap: 12px;
}
.header-left { display: flex; align-items: center; gap: 14px; }
.header-icon {
  width: 48px; height: 48px;
  background: linear-gradient(135deg, #e53e3e, #c53030);
  border-radius: 12px; display: flex; align-items: center; justify-content: center;
  font-size: 24px; box-shadow: 0 4px 12px rgba(229, 62, 62, 0.3);
}
.header-title { font-size: 21px; font-weight: 700; color: #1a202c; margin-bottom: 2px; }
.header-desc { font-size: 13px; color: #718096; }

/* ── 购物车内容 ── */
.cart-content {
  background: #fff;
  border-radius: 16px;
  padding: 20px;
  min-height: 200px;
}

.cart-items { display: flex; flex-direction: column; gap: 12px; }

.cart-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  background: #f8fafc;
  border-radius: 12px;
  border: 1px solid transparent;
  transition: border-color 0.2s, box-shadow 0.2s;
}
.cart-item:hover { border-color: rgba(64, 158, 255, 0.2); box-shadow: 0 2px 12px rgba(0,0,0,0.06); }

.item-img-wrap {
  width: 80px; height: 80px;
  background: #e2e8f0;
  border-radius: 10px;
  overflow: hidden;
  flex-shrink: 0;
  display: flex; align-items: center; justify-content: center;
}
.item-img-wrap img { width: 100%; height: 100%; object-fit: cover; }
.no-img { font-size: 11px; color: #a0aec0; text-align: center; padding: 4px; }

.item-info { flex: 1; min-width: 0; }
.item-name { font-size: 15px; font-weight: 600; color: #1a202c; margin-bottom: 4px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.item-unit { font-size: 12px; color: #a0aec0; }

.item-qty { flex-shrink: 0; }
.qty-control { width: 100px; }

.item-subtotal { flex-shrink: 0; text-align: right; min-width: 80px; }
.subtotal-price { font-size: 16px; font-weight: 700; color: #e53e3e; }

.item-action { flex-shrink: 0; }

/* ── 结算栏 ── */
.cart-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 20px;
  padding: 20px 24px;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 -2px 12px rgba(0,0,0,0.04);
  flex-wrap: wrap;
  gap: 12px;
}
.footer-left { display: flex; align-items: center; gap: 20px; }
.total-count { font-size: 14px; color: #718096; }
.footer-right { display: flex; align-items: center; gap: 24px; }
.total-price-wrap { display: flex; flex-direction: column; align-items: flex-end; }
.total-label { font-size: 12px; color: #a0aec0; }
.total-price { font-size: 24px; font-weight: 800; color: #e53e3e; }
.btn-checkout {
  background: linear-gradient(135deg, #e53e3e, #c53030) !important;
  border: none !important;
  border-radius: 12px !important;
  padding: 0 32px !important;
  font-weight: 600 !important;
  box-shadow: 0 4px 14px rgba(229, 62, 62, 0.35);
  transition: transform 0.2s, box-shadow 0.2s !important;
}
.btn-checkout:hover { transform: translateY(-2px); box-shadow: 0 6px 20px rgba(229, 62, 62, 0.45); }
</style>
