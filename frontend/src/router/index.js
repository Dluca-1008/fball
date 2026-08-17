import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/auth/Login.vue')
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
  if ((to.meta.requiresAuth || to.path.startsWith('/app')) && !userStore.token) {
    next({ path: '/login', query: { redirect: to.fullPath } })
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

  // 登录状态下确保用户信息已加载（刷新页面/直达时 userInfo 为 null，个人中心与侧边栏会空白）
  if (userStore.token && !userStore.userInfo) {
    try {
      await userStore.fetchUserInfo()
    } catch {
      // 401 已由拦截器统一处理（登出并跳转登录），这里忽略
    }
  }

  if (to.meta.permission && userStore.token) {
    if (!userStore.permissions || userStore.permissions.length === 0) {
      try {
        await userStore.fetchPermissions()
      } catch {
        next('/login')
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
