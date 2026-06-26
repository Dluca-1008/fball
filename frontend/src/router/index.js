import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/auth/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/auth/Register.vue')
  },
  {
    path: '/',
    component: () => import('@/layouts/MainLayout.vue'),
    children: [
      {
        path: '',
        name: 'Home',
        component: () => import('@/views/Home.vue')
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
        meta: { requiresAuth: true }
      },
      {
        path: 'my-invitations',
        name: 'MyInvitations',
        component: () => import('@/views/team/MyInvitations.vue'),
        meta: { requiresAuth: true }
      },
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
        path: 'matches/create',
        name: 'MatchCreate',
        component: () => import('@/views/match/MatchCreate.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: 'matches/:id',
        name: 'MatchDetail',
        component: () => import('@/views/match/MatchDetail.vue')
      },
      {
        path: 'matches/:id/manage',
        name: 'MatchManage',
        component: () => import('@/views/match/MatchManage.vue'),
        meta: { requiresAuth: true }
      },
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
        path: 'players',
        name: 'Players',
        component: () => import('@/views/player/PlayerList.vue')
      },
      {
        path: 'players/create',
        name: 'PlayerCreate',
        component: () => import('@/views/player/PlayerCreate.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: 'players/:id/manage',
        name: 'PlayerManage',
        component: () => import('@/views/player/PlayerManage.vue'),
        meta: { requiresAuth: true }
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
        meta: { requiresAuth: true }
      },
      {
        path: 'products',
        name: 'Products',
        component: () => import('@/views/product/ProductList.vue')
      },
      {
        path: 'products/:id',
        name: 'ProductDetail',
        component: () => import('@/views/product/ProductDetail.vue')
      },
      {
        path: 'orders',
        name: 'Orders',
        component: () => import('@/views/order/OrderList.vue')
      },
      {
        path: 'cart',
        name: 'Cart',
        component: () => import('@/views/cart/CartList.vue')
      },
      {
        path: 'chat',
        name: 'Chat',
        component: () => import('@/views/chat/ChatList.vue')
      },
      {
        path: 'change-password',
        name: 'ChangePassword',
        component: () => import('@/views/auth/ChangePassword.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: 'admin',
        name: 'Admin',
        component: () => import('@/views/admin/AdminLayout.vue'),
        meta: { requiresAuth: true, permission: 'user:view' },
        children: [
          {
            path: 'users',
            name: 'AdminUsers',
            component: () => import('@/views/admin/UserList.vue')
          },
          {
            path: 'roles',
            name: 'AdminRoles',
            component: () => import('@/views/admin/RoleList.vue')
          }
        ]
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()

  if (to.meta.requiresAuth && !userStore.token) {
    next('/login')
  } else {
    next()
  }
})

export default router
