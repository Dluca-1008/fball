<template>
  <div class="product-detail" v-loading="loading">
    <el-card v-if="product">
      <el-row :gutter="40">
        <el-col :span="12">
          <div class="product-image">
            <img v-if="product.images" :src="product.images" alt="商品图片" />
            <div v-else class="no-image">暂无图片</div>
          </div>
        </el-col>
        <el-col :span="12">
          <h2 class="product-name">{{ product.name }}</h2>
          <div class="product-price">
            <span class="current-price">¥{{ product.price }}</span>
            <span v-if="product.originalPrice" class="original-price">¥{{ product.originalPrice }}</span>
          </div>
          <div class="product-stock">库存: {{ product.stock }}</div>
          <div class="product-sales">已售: {{ product.salesCount }}</div>
          <el-divider />
          <div class="product-desc">
            <h4>商品描述</h4>
            <p>{{ product.description || '暂无描述' }}</p>
          </div>
          <el-divider />
          <div class="product-actions">
            <el-input-number v-model="quantity" :min="1" :max="product.stock" />
            <el-button @click="addToCart" :disabled="product.stock === 0">
              加入购物车
            </el-button>
            <el-button type="primary" size="large" @click="buyNow" :disabled="product.stock === 0">
              {{ product.stock === 0 ? '已售罄' : '立即购买' }}
            </el-button>
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
  if (!userStore.token) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }

  try {
    await ElMessageBox.confirm('确认购买该商品？', '确认订单', {
      type: 'info'
    })

    const res = await request.post('/api/orders', null, {
      params: { productId: product.value.id, quantity: quantity.value }
    })

    ElMessage.success('下单成功')
    router.push('/orders')
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

async function addToCart() {
  if (!userStore.token) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }

  try {
    await request.post('/api/cart', null, {
      params: { productId: product.value.id, quantity: quantity.value }
    })
    ElMessage.success('已加入购物车')
  } catch (error) {
    console.error(error)
  }
}

onMounted(fetchProduct)
</script>

<style scoped>
.product-detail {
  max-width: 1000px;
  margin: 0 auto;
}

.product-image {
  height: 400px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f5f5;
  border-radius: 8px;
}

.product-image img {
  max-width: 100%;
  max-height: 100%;
}

.no-image {
  color: #999;
  font-size: 16px;
}

.product-name {
  font-size: 24px;
  margin-bottom: 16px;
}

.product-price {
  margin-bottom: 12px;
}

.current-price {
  color: #f56c6c;
  font-size: 28px;
  font-weight: bold;
}

.original-price {
  color: #999;
  font-size: 16px;
  text-decoration: line-through;
  margin-left: 12px;
}

.product-stock,
.product-sales {
  color: #666;
  margin-bottom: 8px;
}

.product-desc h4 {
  margin-bottom: 8px;
}

.product-desc p {
  color: #666;
  line-height: 1.6;
}

.product-actions {
  display: flex;
  gap: 16px;
  align-items: center;
}
</style>
