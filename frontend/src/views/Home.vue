<template>
  <div class="home-container">
    <div class="banner">
      <h1>欢迎来到足球社区</h1>
      <p>关注球队、参与赛事、交流互动</p>
    </div>
    <div class="content-section">
      <el-row :gutter="20">
        <el-col :span="8">
          <el-card class="feature-card" @click="$router.push('/teams')">
            <div class="feature-icon">⚽</div>
            <h3>球队管理</h3>
            <p>查看和管理球队信息</p>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card class="feature-card" @click="$router.push('/matches')">
            <div class="feature-icon">🏆</div>
            <h3>赛事信息</h3>
            <p>关注最新赛事动态</p>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card class="feature-card" @click="$router.push('/posts')">
            <div class="feature-icon">💬</div>
            <h3>社区交流</h3>
            <p>分享足球见解</p>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<style scoped>
.home-container {
  max-width: 1200px;
  margin: 0 auto;
}

.banner {
  background: linear-gradient(135deg, #1a73e8 0%, #4285f4 100%);
  color: white;
  padding: 60px 40px;
  border-radius: 12px;
  text-align: center;
  margin-bottom: 40px;
}

.banner h1 {
  font-size: 36px;
  margin-bottom: 16px;
}

.banner p {
  font-size: 18px;
  opacity: 0.9;
}

.content-section {
  margin-top: 40px;
}

.feature-card {
  text-align: center;
  padding: 30px 20px;
  cursor: pointer;
  transition: transform 0.3s, box-shadow 0.3s;
}

.feature-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

.feature-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.feature-card h3 {
  font-size: 20px;
  margin-bottom: 8px;
  color: #333;
}

.feature-card p {
  color: #666;
  font-size: 14px;
}
</style>
