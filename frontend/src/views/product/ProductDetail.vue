<template>
  <div class="page-shell" v-loading="loading">
    <el-card class="product-hero" v-if="product">
      <el-row :gutter="40">
        <!-- 左侧图片 -->
        <el-col :span="10">
          <div class="product-img-wrap">
            <img v-if="product.images" :src="product.images" :alt="product.name" />
            <div v-else class="no-img">暂无图片</div>
          </div>
        </el-col>

        <!-- 右侧信息 -->
        <el-col :span="14">
          <div class="product-info-wrap">
            <div class="product-category">
              <el-tag size="small" class="cat-tag">⚽ 运动装备</el-tag>
            </div>
            <h1 class="product-name">{{ product.name }}</h1>
            <div class="product-price-row">
              <span class="current-price">¥{{ product.price }}</span>
              <span v-if="product.originalPrice" class="original-price">¥{{ product.originalPrice }}</span>
              <span v-if="product.originalPrice" class="discount-badge">
                省¥{{ (product.originalPrice - product.price).toFixed(2) }}
              </span>
            </div>

            <div class="stock-row">
              <span class="stock-label">
                <span :class="product.stock > 0 ? 'stock-ok' : 'stock-out'">
                  {{ product.stock > 0 ? '● 库存充足' : '● 已售罄' }}
                </span>
              </span>
              <span class="sales-info">已售 {{ product.salesCount }} 件</span>
            </div>

            <el-divider class="info-divider" />

            <div class="product-desc">
              <h4>商品描述</h4>
              <p>{{ product.description || '暂无描述' }}</p>
            </div>

            <el-divider class="info-divider" />

            <div class="product-actions">
              <div class="qty-wrap">
                <span class="qty-label">数量</span>
                <el-input-number v-model="quantity" :min="1" :max="product.stock" size="large" class="qty-input" />
              </div>
              <el-button
                size="large"
                class="btn-cart"
                :disabled="product.stock === 0"
                @click="addToCart"
              >
                <el-icon><ShoppingCart /></el-icon> 加入购物车
              </el-button>
              <el-button
                type="primary"
                size="large"
                class="btn-buy"
                :disabled="product.stock === 0"
                @click="buyNow"
              >
                {{ product.stock === 0 ? '已售罄' : '🛒 立即购买' }}
              </el-button>
            </div>
          </div>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'
import { ShoppingCart } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const product = ref(null)
const quantity = ref(1)

async function fetchProduct() {
  loading.value = true
  try {
    const res = await request.get(`/api/products/${route.params.id}`)
    product.value = res.data
  } finally {
    loading.value = false
  }
}

async function buyNow() {
  if (!userStore.token) { ElMessage.warning('请先登录'); router.push('/login'); return }
  try {
    await ElMessageBox.confirm('确认购买该商品？', '确认订单', { type: 'info' })
    await request.post('/api/orders', null, { params: { productId: product.value.id, quantity: quantity.value } })
    ElMessage.success('下单成功')
    router.push('/app/shop/orders')
  } catch (error) {
    if (error !== 'cancel') console.error(error)
  }
}

async function addToCart() {
  if (!userStore.token) { ElMessage.warning('请先登录'); router.push('/login'); return }
  try {
    await request.post('/api/cart', null, { params: { productId: product.value.id, quantity: quantity.value } })
    ElMessage.success('已加入购物车')
  } catch (error) {
    console.error(error)
  }
}

onMounted(fetchProduct)
</script>

<style scoped>
.page-shell { max-width: 1000px; margin: 0 auto; }

.product-hero { overflow: hidden; }

.product-img-wrap {
  height: 400px;
  background: linear-gradient(135deg, #f8fafc, #eef2ff);
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}
.product-img-wrap img { max-width: 100%; max-height: 100%; object-fit: cover; }
.no-img { color: #a0aec0; font-size: 16px; }

.product-info-wrap { padding: 4px; }
.product-category { margin-bottom: 10px; }
.cat-tag { background: rgba(64, 158, 255, 0.08); color: #409eff; border: none; font-weight: 500; }

.product-name {
  font-size: 24px;
  font-weight: 800;
  color: #1a202c;
  margin-bottom: 14px;
  line-height: 1.3;
}

.product-price-row {
  display: flex;
  align-items: baseline;
  gap: 10px;
  margin-bottom: 14px;
}
.current-price { font-size: 32px; font-weight: 800; color: #e53e3e; }
.original-price { font-size: 16px; color: #a0aec0; text-decoration: line-through; }
.discount-badge {
  background: rgba(229, 62, 62, 0.1);
  color: #e53e3e;
  font-size: 12px;
  font-weight: 600;
  padding: 2px 8px;
  border-radius: 4px;
}

.stock-row {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 12px;
  font-size: 13px;
}
.stock-ok { color: #38a169; }
.stock-out { color: #e53e3e; }
.sales-info { color: #a0aec0; }

.info-divider { margin: 16px 0; }

.product-desc h4 { font-size: 14px; font-weight: 600; color: #2d3748; margin-bottom: 8px; }
.product-desc p { font-size: 14px; color: #4a5568; line-height: 1.7; }

.product-actions {
  display: flex;
  align-items: center;
  gap: 14px;
  flex-wrap: wrap;
}
.qty-wrap { display: flex; align-items: center; gap: 10px; }
.qty-label { font-size: 14px; color: #4a5568; font-weight: 500; }
.qty-input { width: 120px; }

.btn-cart {
  border-radius: 10px !important;
  padding: 0 24px !important;
  background: #fff !important;
  border: 2px solid #e2e8f0 !important;
  color: #4a5568 !important;
  font-weight: 600 !important;
  transition: all 0.2s;
}
.btn-cart:hover { border-color: #409eff !important; color: #409eff !important; }

.btn-buy {
  border-radius: 10px !important;
  padding: 0 28px !important;
  background: linear-gradient(135deg, #409eff, #1d4ed8) !important;
  border: none !important;
  font-weight: 600 !important;
  box-shadow: 0 4px 14px rgba(64, 158, 255, 0.35);
  transition: transform 0.2s, box-shadow 0.2s !important;
}
.btn-buy:hover:not(:disabled) { transform: translateY(-2px); box-shadow: 0 6px 20px rgba(64, 158, 255, 0.45); }
</style>
