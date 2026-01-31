<template>
  <div class="layout-container">
    <el-container class="layout">
      <!-- 侧边栏 -->
      <el-aside width="200px" class="sidebar">
        <div class="logo">
          <h2>朱雀广告平台</h2>
        </div>

        <el-menu
          :default-active="activeMenu"
          class="sidebar-menu"
          router
          background-color="#304156"
          text-color="#bfcbd9"
          active-text-color="#409eff"
        >
          <el-menu-item index="/">
            <el-icon><HomeFilled /></el-icon>
            <span>首页</span>
          </el-menu-item>

          <el-sub-menu index="campaign">
            <template #title>
              <el-icon><Promotion /></el-icon>
              <span>投放管理</span>
            </template>
            <el-menu-item index="/campaigns">投放活动</el-menu-item>
            <el-menu-item index="/ad-groups">广告组</el-menu-item>
            <el-menu-item index="/ads">广告</el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="creative">
            <template #title>
              <el-icon><Picture /></el-icon>
              <span>创意中心</span>
            </template>
            <el-menu-item index="/creatives">创意管理</el-menu-item>
            <el-menu-item index="/materials">素材库</el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="user">
            <template #title>
              <el-icon><User /></el-icon>
              <span>用户管理</span>
            </template>
            <el-menu-item index="/users">用户列表</el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="system">
            <template #title>
              <el-icon><Setting /></el-icon>
              <span>系统设置</span>
            </template>
            <el-menu-item index="/roles">角色管理</el-menu-item>
            <el-menu-item index="/permissions">权限管理</el-menu-item>
          </el-sub-menu>
        </el-menu>
      </el-aside>

      <!-- 主体内容 -->
      <el-container>
        <!-- 顶部导航 -->
        <el-header class="header">
          <div class="header-left">
            <el-breadcrumb separator="/">
              <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
              <el-breadcrumb-item v-if="currentBreadcrumb">{{ currentBreadcrumb }}</el-breadcrumb-item>
            </el-breadcrumb>
          </div>

          <div class="header-right">
            <!-- 主题切换按钮 -->
            <el-dropdown @command="handleThemeChange" style="margin-right: 20px">
              <span class="theme-switcher">
                <el-icon><Sunny /></el-icon>
                <span style="margin-left: 5px">主题</span>
                <el-icon class="el-icon--right"><ArrowDown /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="light">
                    <el-icon><Sunny /></el-icon>
                    亮色模式
                    <el-icon v-if="themeMode === 'light'" style="margin-left: 10px; color: #409eff">
                      <Check />
                    </el-icon>
                  </el-dropdown-item>
                  <el-dropdown-item command="dark">
                    <el-icon><Moon /></el-icon>
                    暗色模式
                    <el-icon v-if="themeMode === 'dark'" style="margin-left: 10px; color: #409eff">
                      <Check />
                    </el-icon>
                  </el-dropdown-item>
                  <el-dropdown-item command="auto">
                    <el-icon><Clock /></el-icon>
                    自动切换
                    <el-icon v-if="themeMode === 'auto'" style="margin-left: 10px; color: #409eff">
                      <Check />
                    </el-icon>
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>

            <el-dropdown>
              <span class="user-info">
                <el-avatar :size="32" :src="userStore.userInfo?.avatar || undefined">
                  {{ userStore.userInfo?.nickname?.charAt(0) }}
                </el-avatar>
                <span class="username">{{ userStore.userInfo?.nickname }}</span>
                <el-icon class="el-icon--right"><ArrowDown /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="handleProfile">
                    <el-icon><User /></el-icon>
                    个人中心
                  </el-dropdown-item>
                  <el-dropdown-item divided @click="handleLogout">
                    <el-icon><SwitchButton /></el-icon>
                    退出登录
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </el-header>

        <!-- 主内容区 -->
        <el-main class="main-content">
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Sunny, Moon, Clock, Check, ArrowDown, User, SwitchButton, HomeFilled, Promotion, Picture, Setting } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { useTheme } from '@/composables/useTheme'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const { themeMode, setTheme } = useTheme()

// 当前激活的菜单
const activeMenu = computed(() => route.path)

// 当前面包屑
const currentBreadcrumb = computed(() => {
  const path = route.path
  const breadcrumbMap: Record<string, string> = {
    '/users': '用户列表',
    '/campaigns': '投放活动',
    '/ad-groups': '广告组',
    '/ads': '广告',
    '/creatives': '创意管理',
    '/materials': '素材库',
    '/roles': '角色管理',
    '/permissions': '权限管理',
    '/profile': '个人中心'
  }
  return breadcrumbMap[path] || ''
})

// 主题切换
const handleThemeChange = async (theme: 'light' | 'dark' | 'auto') => {
  try {
    await setTheme(theme)
    ElMessage.success(`已切换到${theme === 'light' ? '亮色' : theme === 'dark' ? '暗色' : '自动'}模式`)
  } catch (error: any) {
    ElMessage.error(error?.message || '主题切换失败')
  }
}

// 个人中心
const handleProfile = () => {
  router.push('/profile')
}

// 退出登录
const handleLogout = async () => {
  try {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    userStore.logout()
    ElMessage.success('退出成功')
    router.push('/login')
  } catch {
    // 用户取消
  }
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

.layout {
  height: 100%;
}

/* 侧边栏样式 */
.sidebar {
  background-color: #304156;
  overflow-x: hidden;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #2b3a4a;
}

.logo h2 {
  margin: 0;
  font-size: 18px;
  color: #fff;
  font-weight: 500;
}

.sidebar-menu {
  border-right: none;
  height: calc(100vh - 60px);
}

/* 顶部导航样式 */
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  padding: 0 20px;
}

.header-left {
  flex: 1;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 0 12px;
  transition: background-color 0.3s;
}

.user-info:hover {
  background-color: #f5f5f5;
  border-radius: 4px;
}

.username {
  color: #333;
  font-size: 14px;
}

.theme-switcher {
  display: flex;
  align-items: center;
  gap: 5px;
  cursor: pointer;
  padding: 8px 12px;
  transition: background-color 0.3s;
  border-radius: 4px;
}

.theme-switcher:hover {
  background-color: #f5f5f5;
}

/* 主内容区样式 */
.main-content {
  background: #f5f5f5;
  padding: 20px;
  overflow-y: auto;
}
</style>
