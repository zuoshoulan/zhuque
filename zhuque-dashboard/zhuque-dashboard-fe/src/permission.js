import router from './router'
// import store from './store'
import NProgress from 'nprogress' // Progress 进度条
import 'nprogress/nprogress.css'// Progress 进度条样式
// import { Message } from 'element-ui'
// import { getToken } from '@/utils/auth' // 验权

// const whiteList = ['/login'] // 不重定向白名单
router.beforeEach((to, from, next) => {
  NProgress.start()
  // 暂时禁用登录验证
  next()

  // 原始验证逻辑（已禁用）
  // if (getToken()) {
  //   if (to.path === '/login') {
  //     next({ path: '/' })
  //     NProgress.done()
  //   } else {
  //     if (store.getters.roles.length === 0) {
  //       store.dispatch('GetInfo').then(res => {
  //         next()
  //       }).catch((err) => {
  //         store.dispatch('FedLogOut').then(() => {
  //           Message.error(err || 'Verification failed, please login again')
  //           next({ path: '/' })
  //         })
  //       })
  //     } else {
  //       next()
  //     }
  //   }
  // } else {
  //   if (whiteList.indexOf(to.path) !== -1) {
  //     next()
  //   } else {
  //     next(`/login?redirect=${to.path}`)
  //     NProgress.done()
  //   }
  // }
})

router.afterEach(() => {
  NProgress.done() // 结束Progress
})
