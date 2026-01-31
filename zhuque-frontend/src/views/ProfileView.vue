<template>
  <div class="profile-view">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>个人中心</span>
        </div>
      </template>

      <el-tabs v-model="activeTab">
        <!-- 基本信息 -->
        <el-tab-pane label="基本信息" name="info">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="用户名">
              {{ userInfo?.username }}
            </el-descriptions-item>
            <el-descriptions-item label="昵称">
              {{ userInfo?.nickname }}
            </el-descriptions-item>
            <el-descriptions-item label="手机号">
              {{ userInfo?.phone }}
            </el-descriptions-item>
            <el-descriptions-item label="邮箱">
              {{ userInfo?.email || '-' }}
            </el-descriptions-item>
            <el-descriptions-item label="主题偏好">
              <el-tag v-if="userInfo?.themePreference" :type="userInfo.themePreference === 'light' ? 'success' : 'primary'">
                {{ userInfo.themePreference === 'light' ? '亮色' : userInfo.themePreference === 'dark' ? '暗色' : '自动' }}
              </el-tag>
              <span v-else>-</span>
            </el-descriptions-item>
            <el-descriptions-item label="强制修改密码">
              <el-tag :type="userInfo?.forceChangePassword ? 'warning' : 'success'">
                {{ userInfo?.forceChangePassword ? '是' : '否' }}
              </el-tag>
            </el-descriptions-item>
          </el-descriptions>
        </el-tab-pane>

        <!-- 修改密码 -->
        <el-tab-pane label="修改密码" name="password">
          <el-form
            ref="passwordFormRef"
            :model="passwordForm"
            :rules="passwordRules"
            label-width="120px"
            style="max-width: 500px"
          >
            <el-form-item label="原密码" prop="oldPassword">
              <el-input
                v-model="passwordForm.oldPassword"
                type="password"
                placeholder="请输入原密码"
                show-password
                autocomplete="off"
                name="old-password"
              />
            </el-form-item>

            <el-form-item label="新密码" prop="newPassword">
              <el-input
                v-model="passwordForm.newPassword"
                type="password"
                placeholder="请输入新密码（至少8个字符）"
                show-password
                autocomplete="new-password"
                name="new-password"
              />
            </el-form-item>

            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input
                v-model="passwordForm.confirmPassword"
                type="password"
                placeholder="请再次输入新密码"
                show-password
                autocomplete="new-password"
                name="confirm-password"
              />
            </el-form-item>

            <el-form-item>
              <el-button type="primary" :loading="submitLoading" @click="handleChangePassword">
                修改密码
              </el-button>
              <el-button @click="handleResetForm">重置</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { ElMessage, FormInstance, FormRules } from 'element-plus'
import { changePassword, type ChangePasswordRequest } from '@/api/user'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

// 当前激活的标签页
const activeTab = ref('info')

// 用户信息 - 直接从 store 获取
const userInfo = computed(() => userStore.userInfo)

// 密码表单
const passwordFormRef = ref<FormInstance>()
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const submitLoading = ref(false)

// 表单验证规则
const passwordRules: FormRules = {
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 8, message: '新密码长度不能少于8个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 修改密码
const handleChangePassword = async () => {
  if (!passwordFormRef.value) return

  await passwordFormRef.value.validate(async (valid) => {
    if (!valid) return

    if (!userInfo.value) {
      ElMessage.error('用户信息不存在')
      return
    }

    try {
      submitLoading.value = true

      const data: ChangePasswordRequest = {
        oldPassword: passwordForm.oldPassword,
        newPassword: passwordForm.newPassword
      }

      await changePassword(userInfo.value.id, data)
      ElMessage.success('密码修改成功，请重新登录')

      // 清空表单
      handleResetForm()

      // 可以选择是否自动登出
      // setTimeout(() => {
      //   userStore.logout()
      //   window.location.href = '/login'
      // }, 1500)
    } catch (error) {
      // 错误消息已在 request.ts 的响应拦截器中显示
      console.error('修改密码失败:', error)
    } finally {
      submitLoading.value = false
    }
  })
}

// 重置表单
const handleResetForm = () => {
  passwordFormRef.value?.resetFields()
}
</script>

<style scoped>
.profile-view {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
