import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'
import Layout from '@/components/Layout.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      name: 'Login',
      component: () => import('@/views/LoginView.vue'),
      meta: { title: '登录 - 朱雀广告平台' }
    },
    {
      path: '/',
      component: Layout,
      meta: { requiresAuth: true },
      children: [
        {
          path: '',
          name: 'Home',
          component: () => import('@/views/HomeView.vue'),
          meta: { title: '首页 - 朱雀广告平台' }
        },
        {
          path: 'users',
          name: 'Users',
          component: () => import('@/views/UsersView.vue'),
          meta: { title: '用户管理 - 朱雀广告平台' }
        },
        {
          path: 'ads',
          name: 'Ads',
          component: () => import('@/views/AdsView.vue'),
          meta: { title: '广告管理 - 朱雀广告平台' }
        },
        {
          path: 'campaigns',
          name: 'Campaigns',
          component: () => import('@/views/CampaignsView.vue'),
          meta: { title: '活动管理 - 朱雀广告平台' }
        },
        {
          path: 'roles',
          name: 'Roles',
          component: () => import('@/views/RolesView.vue'),
          meta: { title: '角色管理 - 朱雀广告平台' }
        },
        {
          path: 'permissions',
          name: 'Permissions',
          component: () => import('@/views/PermissionsView.vue'),
          meta: { title: '权限管理 - 朱雀广告平台' }
        },
        {
          path: 'creatives',
          name: 'Creatives',
          component: () => import('@/views/CreativesView.vue'),
          meta: { title: '创意管理 - 朱雀广告平台' }
        },
        {
          path: 'materials',
          name: 'Materials',
          component: () => import('@/views/MaterialsView.vue'),
          meta: { title: '素材管理 - 朱雀广告平台' }
        },
        {
          path: 'profile',
          name: 'Profile',
          component: () => import('@/views/ProfileView.vue'),
          meta: { title: '个人中心 - 朱雀广告平台' }
        }
      ]
    }
  ]
})

// 路由守卫
router.beforeEach((to, _from, next) => {
  const userStore = useUserStore()

  // 设置页面标题
  document.title = to.meta.title as string || '朱雀广告平台'

  // 检查是否需要登录
  if (to.meta.requiresAuth && !userStore.isLoggedIn()) {
    next('/login')
  } else if (to.path === '/login' && userStore.isLoggedIn()) {
    next('/')
  } else {
    next()
  }
})

export default router
