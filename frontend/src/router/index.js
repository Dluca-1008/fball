import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

let isHandling401 = false

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/auth/Login.vue'),
    meta: { public: true }
  },
  {
    path: '/403',
    name: 'Forbidden',
    component: () => import('@/views/error/403.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/auth/Register.vue')
  },
  // ── 首页（公开，无需登录）
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/Home.vue')
  },
  // ── 需要登录的主布局路由
  {
    path: '/app',
    component: () => import('@/layouts/SidebarLayout.vue'),
    meta: { requiresAuth: true },
    children: [
      // ── 首页（已登录用户的应用主页）
      {
        path: '',
        name: 'AppHome',
        component: () => import('@/views/app/AppHome.vue')
      },
      // ── 社区 ──
      {
        path: 'posts',
        name: 'Posts',
        component: () => import('@/views/post/PostList.vue')
      },
      {
        path: 'posts/:id',
        name: 'PostDetail',
        component: () => import('@/views/post/PostDetail.vue')
      },
      {
        path: 'chat',
        name: 'Chat',
        component: () => import('@/views/chat/ChatList.vue'),
        meta: { requiresAuth: true }
      },

      // ── 商城 ──
      {
        path: 'shop',
        component: () => import('@/layouts/ShopLayout.vue'),
        children: [
          {
            path: 'products',
            name: 'ShopProducts',
            component: () => import('@/views/shop/ProductList.vue')
          },
          {
            path: 'products/:id',
            name: 'ProductDetail',
            component: () => import('@/views/product/ProductDetail.vue')
          },
          {
            path: 'orders',
            name: 'ShopOrders',
            component: () => import('@/views/shop/OrderList.vue')
          }
        ]
      },
      {
        path: 'cart',
        name: 'Cart',
        component: () => import('@/views/cart/CartList.vue'),
        meta: { requiresAuth: true }
      },

      // ── 赛事 ──
      {
        path: 'matches',
        name: 'Matches',
        component: () => import('@/views/match/MatchList.vue')
      },
      {
        path: 'matches/schedule',
        name: 'MatchSchedule',
        component: () => import('@/views/match/MatchSchedule.vue')
      },
      {
        path: 'matches/generate',
        name: 'ScheduleGenerator',
        component: () => import('@/views/match/ScheduleGenerator.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: 'matches/stats',
        name: 'MatchStats',
        component: () => import('@/views/match/MatchStats.vue')
      },
      {
        path: 'matches/:id/stats',
        name: 'MatchDetailStats',
        component: () => import('@/views/match/MatchDetailStats.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: 'matches/create',
        name: 'MatchCreate',
        component: () => import('@/views/match/MatchCreate.vue'),
        meta: { requiresAuth: true, permission: 'match:add' }
      },
      {
        path: 'matches/:id/manage',
        name: 'MatchManage',
        component: () => import('@/views/match/MatchManage.vue'),
        meta: { requiresAuth: true, permission: 'match:edit' }
      },
      {
        path: 'matches/:id',
        name: 'MatchDetail',
        component: () => import('@/views/match/MatchDetail.vue')
      },
      {
        path: 'matches/friendly/sent',
        name: 'FriendlySent',
        component: () => import('@/views/match/MatchFriendly.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: 'matches/friendly/received',
        name: 'FriendlyReceived',
        component: () => import('@/views/match/MatchFriendly.vue'),
        meta: { requiresAuth: true }
      },

      {
        path: 'teams',
        name: 'Teams',
        component: () => import('@/views/team/TeamList.vue')
      },
      {
        path: 'my-teams',
        name: 'MyTeams',
        component: () => import('@/views/team/MyTeams.vue')
      },
      {
        path: 'teams/create',
        name: 'TeamCreate',
        component: () => import('@/views/team/TeamCreate.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: 'teams/:id',
        name: 'TeamDetail',
        component: () => import('@/views/team/TeamDetail.vue')
      },
      {
        path: 'teams/:id/manage',
        name: 'TeamManage',
        component: () => import('@/views/team/TeamManage.vue'),
        meta: { requiresAuth: true, permission: 'team:edit' }
      },

      {
        path: 'players',
        name: 'Players',
        component: () => import('@/views/player/PlayerList.vue')
      },
      {
        path: 'players/create',
        name: 'PlayerCreate',
        component: () => import('@/views/player/PlayerCreate.vue'),
        meta: { requiresAuth: true, permission: 'team:edit' }
      },
      {
        path: 'players/:id/manage',
        name: 'PlayerManage',
        component: () => import('@/views/player/PlayerManage.vue'),
        meta: { requiresAuth: true, permission: 'team:edit' }
      },

      {
        path: 'coaches',
        name: 'Coaches',
        component: () => import('@/views/coach/CoachList.vue')
      },
      {
        path: 'coaches/create',
        name: 'CoachCreate',
        component: () => import('@/views/coach/CoachCreate.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: 'coaches/:id/manage',
        name: 'CoachManage',
        component: () => import('@/views/coach/CoachManage.vue'),
        meta: { requiresAuth: true, permission: 'coach:edit' }
      },

      // ── 个人中心 ──
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/profile/ProfileView.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: 'profile/invitations',
        name: 'MyInvitations',
        component: () => import('@/views/team/MyInvitations.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: 'profile/password',
        name: 'ChangePassword',
        component: () => import('@/views/auth/ChangePassword.vue'),
        meta: { requiresAuth: true }
      },

      // ── 管理后台 ──
      {
        path: 'admin/users',
        name: 'AdminUsers',
        component: () => import('@/views/admin/UserList.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: 'admin/roles',
        name: 'AdminRoles',
        component: () => import('@/views/admin/RoleList.vue'),
        meta: { requiresAuth: true }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach(async (to, from, next) => {
  const userStore = useUserStore()

  // 需要登录的页面（包括 /app 及其子路由），未登录则跳转登录页
  const needAuth = to.meta.requiresAuth || to.path.startsWith('/app')

  if (needAuth && !userStore.token) {
    // request.js 已标记过期，把 expired 带上让登录页弹提示
    const expired = localStorage.getItem('__fball_expired__')
    if (expired) localStorage.removeItem('__fball_expired__')
    next({ path: '/login', query: { expired: expired ? '1' : null, redirect: to.fullPath } })
    return
  }

  // 已登录用户访问 / → 直接进入应用（侧边栏布局）
  if (userStore.token && to.path === '/') {
    next('/app')
    return
  }

  // 已登录用户访问登录/注册页 → 跳转应用首页
  if (userStore.token && (to.path === '/login' || to.path === '/register')) {
    next('/app')
    return
  }

  // token 存在但 userInfo 缺失时，先验证 token 是否仍然有效
  // 过期 token 会在此处触发 401，同步清除后跳转到登录页
  if (userStore.token && !userStore.userInfo && needAuth && !isHandling401) {
    try {
      isHandling401 = true
      await userStore.fetchUserInfo()
    } catch {
      localStorage.removeItem('token')
      userStore.token = ''
      userStore.userInfo = null
      userStore.permissions = []
      // 提示由 request.js 统一处理，守卫只负责跳转
      next({ path: '/login', query: { expired: '1', redirect: to.fullPath } })
      setTimeout(() => { isHandling401 = false }, 3000)
      return
    }
  }

  if (to.meta.permission && userStore.token) {
    if (!userStore.permissions || userStore.permissions.length === 0) {
      try {
        await userStore.fetchPermissions()
      } catch {
        localStorage.removeItem('token')
        userStore.token = ''
        userStore.userInfo = null
        userStore.permissions = []
        // 提示由 request.js 统一处理
        next({ path: '/login', query: { expired: '1' } })
        return
      }
    }
    if (!userStore.hasPermission(to.meta.permission)) {
      next('/403')
      return
    }
  }

  next()
})

// 导航完成后滚动到顶部
router.afterEach(() => {
  window.scrollTo(0, 0)
})

export default router
