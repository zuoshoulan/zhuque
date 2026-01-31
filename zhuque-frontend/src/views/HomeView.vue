<template>
  <div class="home-view">
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
    </el-card>

    <el-row :gutter="20" class="stats-row">
      <el-col :span="8">
        <el-card class="stat-card">
          <div class="stat-item">
            <el-icon class="stat-icon" color="#409eff" :size="40">
              <Promotion />
            </el-icon>
            <div class="stat-content">
              <div class="stat-value">0</div>
              <div class="stat-label">广告数量</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="stat-card">
          <div class="stat-item">
            <el-icon class="stat-icon" color="#67c23a" :size="40">
              <TrendCharts />
            </el-icon>
            <div class="stat-content">
              <div class="stat-value">0</div>
              <div class="stat-label">活动数量</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="stat-card">
          <div class="stat-item">
            <el-icon class="stat-icon" color="#e6a23c" :size="40">
              <Document />
            </el-icon>
            <div class="stat-content">
              <div class="stat-value">0</div>
              <div class="stat-label">数据报告</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="quick-actions">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>快速操作</span>
            </div>
          </template>
          <div class="action-buttons">
            <el-button type="primary" :icon="Promotion" @click="$router.push('/ads')">
              创建广告
            </el-button>
            <el-button type="success" :icon="TrendCharts" @click="$router.push('/campaigns')">
              创建活动
            </el-button>
            <el-button type="warning" :icon="User" @click="$router.push('/users')">
              添加用户
            </el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { useUserStore } from '@/stores/user'
import { Promotion, TrendCharts, Document, User } from '@element-plus/icons-vue'

const userStore = useUserStore()
</script>

<style scoped>
.home-view {
  padding: 0;
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
  margin-bottom: 0;
}

.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  cursor: pointer;
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.stat-item {
  display: flex;
  align-items: center;
  padding: 10px;
}

.stat-icon {
  margin-right: 20px;
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #333;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: #666;
}

.quick-actions {
  margin-top: 20px;
}

.action-buttons {
  display: flex;
  gap: 15px;
}
</style>
