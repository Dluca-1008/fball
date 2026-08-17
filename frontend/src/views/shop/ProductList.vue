<template>
  <div class="product-list-page">
    <div class="page-header">
      <div class="header-left">
        <div class="header-icon shop">🛍️</div>
        <div>
          <h1 class="header-title">商品商城</h1>
          <p class="header-desc">探索精选运动装备</p>
        </div>
      </div>
      <div class="header-actions">
        <el-input v-model="keyword" placeholder="搜索商品" clearable style="width:220px" @clear="fetchProducts" @keyup.enter="fetchProducts" />
        <el-button @click="fetchProducts">搜索</el-button>
      </div>
    </div>

    <div class="product-grid" v-loading="loading">
      <el-empty v-if="products.length === 0" description="暂无商品" />
      <div v-for="product in products" :key="product.id" class="product-card" @click="$router.push(`/app/shop/products/${product.id}`)">
        <div class="product-image-wrap">
          <img v-if="product.images" :src="product.images" :alt="product.name" />
          <div v-else class="no-image">📷</div>
        </div>
        <div class="product-card-body">
          <h3 class="product-name">{{ product.name }}</h3>
          <div class="product-price-row">
            <span class="current-price">¥{{ product.price }}</span>
            <span v-if="product.originalPrice" class="original-price">¥{{ product.originalPrice }}</span>
          </div>
          <div class="product-sales">已售 {{ product.salesCount }} 件</div>
        </div>
      </div>
    </div>

    <div class="pagination-wrap">
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
.product-list-page { }

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
  background: linear-gradient(135deg, #409eff, #1d4ed8);
  border-radius: 12px;
  display: flex; align-items: center; justify-content: center;
  font-size: 24px;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}
.header-title { font-size: 21px; font-weight: 700; color: #1a202c; margin-bottom: 2px; }
.header-desc { font-size: 13px; color: #718096; }
.header-actions { display: flex; gap: 10px; }

/* ── 商品网格 ── */
.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
}

.product-card {
  background: #fff;
  border-radius: 14px;
  overflow: hidden;
  cursor: pointer;
  border: 1px solid rgba(0,0,0,0.05);
  transition: transform 0.2s, box-shadow 0.2s;
}
.product-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0,0,0,0.1);
}

.product-image-wrap {
  height: 180px;
  background: linear-gradient(135deg, #f8fafc, #eef2ff);
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}
.product-image-wrap img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.no-image {
  font-size: 40px;
  opacity: 0.4;
}

.product-card-body { padding: 14px 16px; }
.product-name {
  font-size: 14px;
  font-weight: 600;
  color: #1a202c;
  margin: 0 0 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.product-price-row {
  display: flex;
  align-items: baseline;
  gap: 6px;
  margin-bottom: 6px;
}
.current-price {
  font-size: 18px;
  font-weight: 800;
  color: #e53e3e;
}
.original-price {
  font-size: 12px;
  color: #a0aec0;
  text-decoration: line-through;
}
.product-sales {
  font-size: 12px;
  color: #a0aec0;
}

.pagination-wrap {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}
</style>
