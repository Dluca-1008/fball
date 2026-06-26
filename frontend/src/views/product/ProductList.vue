<template>
  <div class="product-list">
    <div class="page-header">
      <h2>商品商城</h2>
      <el-input v-model="keyword" placeholder="搜索商品" style="width: 200px; margin-right: 12px;" clearable @clear="fetchProducts" @keyup.enter="fetchProducts" />
      <el-button type="primary" @click="fetchProducts">搜索</el-button>
    </div>
    <el-row :gutter="20">
      <el-col :span="6" v-for="product in products" :key="product.id">
        <el-card class="product-card" @click="$router.push(`/products/${product.id}`)">
          <div class="product-image">
            <img v-if="product.images" :src="product.images" alt="商品图片" />
            <div v-else class="no-image">暂无图片</div>
          </div>
          <div class="product-info">
            <h4 class="product-name">{{ product.name }}</h4>
            <div class="product-price">
              <span class="current-price">¥{{ product.price }}</span>
              <span v-if="product.originalPrice" class="original-price">¥{{ product.originalPrice }}</span>
            </div>
            <div class="product-sales">已售 {{ product.salesCount }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    <el-empty v-if="products.length === 0 && !loading" description="暂无商品" />
    <el-pagination
      v-model:current-page="page"
      v-model:page-size="pageSize"
      :page-sizes="[12, 24, 48]"
      :total="total"
      layout="total, sizes, prev, pager, next"
      @size-change="fetchProducts"
      @current-change="fetchProducts"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'

const loading = ref(false)
const products = ref([])
const keyword = ref('')
const page = ref(1)
const pageSize = ref(12)
const total = ref(0)

async function fetchProducts() {
  loading.value = true
  try {
    const res = await request.get('/api/products', {
      params: { page: page.value, size: pageSize.value, keyword: keyword.value }
    })
    products.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

onMounted(fetchProducts)
</script>

<style scoped>
.product-list {
  background: white;
  padding: 20px;
  border-radius: 8px;
}

.page-header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h2 {
  margin-right: 20px;
}

.product-card {
  cursor: pointer;
  margin-bottom: 20px;
  transition: transform 0.3s;
}

.product-card:hover {
  transform: translateY(-5px);
}

.product-image {
  height: 150px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f5f5;
  margin-bottom: 12px;
}

.product-image img {
  max-width: 100%;
  max-height: 100%;
}

.no-image {
  color: #999;
}

.product-name {
  font-size: 14px;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-price {
  margin-bottom: 4px;
}

.current-price {
  color: #f56c6c;
  font-size: 18px;
  font-weight: bold;
}

.original-price {
  color: #999;
  font-size: 12px;
  text-decoration: line-through;
  margin-left: 8px;
}

.product-sales {
  color: #999;
  font-size: 12px;
}

.el-pagination {
  margin-top: 20px;
  justify-content: flex-end;
}
</style>
