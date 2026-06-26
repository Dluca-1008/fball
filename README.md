# 足球社区平台

一个综合性的足球社区平台，包含球队管理、赛事信息、社区交流和商城购物四大核心模块。

## 技术栈

### 后端
- Spring Boot 2.7+
- Spring Security + JWT
- MyBatis Plus
- MySQL 8.0
- Redis

### 前端
- Vue.js 3
- Element Plus
- Pinia
- Vue Router
- Vite

## 项目结构

```
fball/
├── backend/                    # 后端项目
│   ├── src/main/java/com/football/community/
│   │   ├── config/            # 配置类
│   │   ├── controller/        # 控制器
│   │   ├── dto/               # 数据传输对象
│   │   ├── entity/            # 实体类
│   │   ├── exception/         # 异常处理
│   │   ├── repository/        # 数据访问层
│   │   ├── security/          # 安全相关
│   │   └── service/           # 业务逻辑层
│   └── src/main/resources/
│       ├── application.yml    # 配置文件
│       └── db/                # 数据库脚本
├── frontend/                   # 前端项目
│   └── src/
│       ├── api/               # API接口
│       ├── components/        # 公共组件
│       ├── layouts/           # 布局组件
│       ├── router/            # 路由配置
│       ├── stores/            # Pinia状态管理
│       ├── utils/             # 工具函数
│       └── views/             # 页面组件
└── README.md
```

## 快速开始

### 后端启动

1. 创建数据库并执行初始化脚本：
```sql
source backend/src/main/resources/db/schema.sql
source backend/src/main/resources/db/data.sql
```

2. 修改数据库配置（application.yml）

3. 启动后端服务：
```bash
cd backend
mvn spring-boot:run
```

### 前端启动

1. 安装依赖：
```bash
cd frontend
npm install
```

2. 启动开发服务器：
```bash
npm run dev
```

## 功能模块

### 1. RBAC权限管理
- 5种角色：管理员、普通用户、赛事主办方、商家、球队管理员
- 基于角色的访问控制
- 前端路由和按钮级权限控制

### 2. 球队管理
- 球队信息CRUD
- 球队成员管理
- 申请加入/邀请加入
- 成员角色设置

### 3. 赛事管理
- 赛事信息管理
- 赛程安排
- 比分更新

### 4. 社区功能
- 发帖/回帖
- 点赞/收藏
- 内容分类

### 5. 商城功能
- 商品展示
- 购物车
- 订单管理

## 默认账号

- 管理员：admin / admin123
