<template>
  <div class="home-container">
    <el-container>
      <el-header class="header">
        <div class="header-left">
          <h2>朱雀广告平台</h2>
        </div>
        <div class="header-right">
          <el-dropdown>
            <span class="user-info">
              <el-avatar :size="32" :src="userStore.userInfo?.avatar || undefined">
                {{ userStore.userInfo?.nickname?.charAt(0) }}
              </el-avatar>
              <span class="username">{{ userStore.userInfo?.nickname }}</span>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item>个人中心</el-dropdown-item>
                <el-dropdown-item divided @click="handleLogout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-main class="main">
        <el-card class="welcome-card">
          <template #header>
            <div class="card-header">
              <span>欢迎使用朱雀广告平台</span>
            </div>
          </template>

          <div class="user-info-list">
            <el-descriptions :column="2" border>
              <el-descriptions-item label="用户ID">
                {{ userStore.userInfo?.id }}
              </el-descriptions-item>
              <el-descriptions-item label="用户名">
                {{ userStore.userInfo?.username }}
              </el-descriptions-item>
              <el-descriptions-item label="昵称">
                {{ userStore.userInfo?.nickname }}
              </el-descriptions-item>
              <el-descriptions-item label="手机号">
                {{ userStore.userInfo?.phone }}
              </el-descriptions-item>
              <el-descriptions-item label="邮箱" :span="2">
                {{ userStore.userInfo?.email }}
              </el-descriptions-item>
            </el-descriptions>
          </div>

          <div class="token-info">
            <el-alert
              title="Token信息"
              type="info"
              :closable="false"
              show-icon
            >
              <template #default>
                <p style="margin: 5px 0; word-break: break-all;">
                  {{ userStore.token?.substring(0, 50) }}...
                </p>
              </template>
            </el-alert>
          </div>
        </el-card>

        <el-row :gutter="20" class="stats-row">
          <el-col :span="8">
            <el-card class="stat-card">
              <div class="stat-item">
                <div class="stat-value">0</div>
                <div class="stat-label">广告数量</div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="8">
            <el-card class="stat-card">
              <div class="stat-item">
                <div class="stat-value">0</div>
                <div class="stat-label">活动数量</div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="8">
            <el-card class="stat-card">
              <div class="stat-item">
                <div class="stat-value">0</div>
                <div class="stat-label">数据报告</div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </el-main>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

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
  } catch (error) {
    // 用户取消
  }
}
</script>

<style scoped>
.home-container {
  min-height: 100vh;
  background: #f5f5f5;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: white;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  padding: 0 20px;
}

.header-left h2 {
  margin: 0;
  color: #333;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
}

.username {
  color: #333;
  font-size: 14px;
}

.main {
  padding: 20px;
}

.welcome-card {
  margin-bottom: 20px;
}

.card-header {
  font-size: 18px;
  font-weight: bold;
  color: #333;
}

.user-info-list {
  margin-bottom: 20px;
}

.token-info {
  margin-top: 20px;
}

.stats-row {
  margin-top: 20px;
}

.stat-card {
  text-align: center;
}

.stat-item {
  padding: 20px;
}

.stat-value {
  font-size: 36px;
  font-weight: bold;
  color: #409eff;
  margin-bottom: 10px;
}

.stat-label {
  font-size: 14px;
  color: #666;
}
</style>
