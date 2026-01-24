<template>
  <el-dialog
    :model-value="visible"
    :title="isEdit ? '编辑投放活动' : '新建投放活动'"
    width="700px"
    :close-on-click-modal="false"
    @update:model-value="handleClose"
  >
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="140px"
      @submit.prevent="handleSubmit"
    >
      <el-divider content-position="left">基本信息</el-divider>

      <el-form-item label="活动名称" prop="name">
        <el-input
          v-model="formData.name"
          placeholder="请输入活动名称（2-100字符）"
          maxlength="100"
          show-word-limit
        />
      </el-form-item>

      <el-form-item label="活动描述" prop="description">
        <el-input
          v-model="formData.description"
          type="textarea"
          placeholder="请输入活动描述"
          maxlength="500"
          show-word-limit
          :rows="3"
        />
      </el-form-item>

      <el-form-item label="营销目标" prop="campaignObjective">
        <el-radio-group v-model="formData.campaignObjective">
          <el-radio :value="1">品牌曝光</el-radio>
          <el-radio :value="2">流量</el-radio>
          <el-radio :value="3">转化</el-radio>
          <el-radio :value="4">ROI</el-radio>
        </el-radio-group>
      </el-form-item>

      <el-divider content-position="left">目标设置</el-divider>

      <el-form-item label="目标类型">
        <el-select
          v-model="formData.campaignGoalType"
          placeholder="请选择目标类型"
          style="width: 200px"
        >
          <el-option label="展示量" :value="1" />
          <el-option label="点击量" :value="2" />
          <el-option label="转化量" :value="3" />
        </el-select>
      </el-form-item>

      <el-form-item label="目标值">
        <el-input-number
          v-model="formData.campaignGoalValue"
          :min="0"
          :max="999999999"
          placeholder="请输入目标值"
        />
      </el-form-item>

      <el-divider content-position="left">预算设置</el-divider>

      <el-form-item label="总预算（元）" prop="lifetimeBudget">
        <el-input-number
          v-model="formData.lifetimeBudget"
          :min="100"
          :max="999999999"
          :precision="2"
          :step="100"
          style="width: 200px"
        />
        <span class="form-tip">最低 100 元</span>
      </el-form-item>

      <el-divider content-position="left">时间设置</el-divider>

      <el-form-item label="投放时间" prop="timeRange">
        <el-date-picker
          v-model="timeRange"
          type="datetimerange"
          range-separator="至"
          start-placeholder="开始时间"
          end-placeholder="结束时间"
          format="YYYY-MM-DD HH:mm"
          value-format="YYYY-MM-DDTHH:mm:ss"
          :disabled-date="disabledDate"
        />
      </el-form-item>

      <el-alert
        v-if="durationDays > 0"
        :title="`共 ${durationDays} 天`"
        type="info"
        :closable="false"
        style="margin-left: 140px; width: 400px"
      />
    </el-form>

    <template #footer>
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="handleSubmit">
        {{ isEdit ? '保存' : '创建' }}
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, watch, computed } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import {
  getCampaignById,
  createCampaign,
  updateCampaign,
  type CampaignDetail,
  type CampaignCreateRequest,
  type CampaignUpdateRequest
} from '@/api/campaign'

interface Props {
  visible: boolean
  campaignId?: number
}

interface Emits {
  (e: 'update:visible', value: boolean): void
  (e: 'success'): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

const formRef = ref<FormInstance>()
const submitting = ref(false)
const detailData = ref<CampaignDetail>()

const isEdit = computed(() => !!props.campaignId)

const formData = reactive({
  name: '',
  description: '',
  campaignObjective: 1,
  campaignGoalType: undefined as number | undefined,
  campaignGoalValue: undefined as number | undefined,
  lifetimeBudget: 1000
})

const timeRange = ref<string[]>([])

const durationDays = computed(() => {
  if (timeRange.value?.length === 2) {
    const start = new Date(timeRange.value[0])
    const end = new Date(timeRange.value[1])
    return Math.ceil((end.getTime() - start.getTime()) / (1000 * 60 * 60 * 24))
  }
  return 0
})

const formRules: FormRules = {
  name: [
    { required: true, message: '请输入活动名称', trigger: 'blur' },
    { min: 2, max: 100, message: '长度在 2 到 100 个字符', trigger: 'blur' }
  ],
  campaignObjective: [
    { required: true, message: '请选择营销目标', trigger: 'change' }
  ],
  lifetimeBudget: [
    { required: true, message: '请输入总预算', trigger: 'blur' },
    { type: 'number', min: 100, message: '预算不能少于 100 元', trigger: 'blur' }
  ],
  timeRange: [
    { required: true, message: '请选择投放时间', trigger: 'change' },
    {
      validator: (_rule: any, value: any, callback: any) => {
        if (timeRange.value?.length === 2) {
          const start = new Date(timeRange.value[0])
          const end = new Date(timeRange.value[1])
          const days = Math.ceil((end.getTime() - start.getTime()) / (1000 * 60 * 60 * 24))
          if (days > 90) {
            callback(new Error('活动时长不能超过 90 天'))
          } else {
            callback()
          }
        } else {
          callback()
        }
      },
      trigger: 'change'
    }
  ]
}

// 禁用今天之前的日期
const disabledDate = (time: Date) => {
  return time.getTime() < Date.now() - 24 * 60 * 60 * 1000
}

// 重置表单
const resetForm = () => {
  formData.name = ''
  formData.description = ''
  formData.campaignObjective = 1
  formData.campaignGoalType = undefined
  formData.campaignGoalValue = undefined
  formData.lifetimeBudget = 1000
  timeRange.value = []
  formRef.value?.clearValidate()
  detailData.value = undefined
}

// 加载详情
const loadDetail = async () => {
  if (!props.campaignId) return

  try {
    const data = await getCampaignById(props.campaignId)
    detailData.value = data
    formData.name = data.name
    formData.description = data.description || ''
    formData.campaignObjective = data.campaignObjective
    formData.campaignGoalType = data.campaignGoalType
    formData.campaignGoalValue = data.campaignGoalValue
    formData.lifetimeBudget = data.lifetimeBudget
    timeRange.value = [data.startTime, data.endTime]
  } catch (error) {
    console.error('加载详情失败:', error)
  }
}

// 关闭对话框
const handleClose = () => {
  emit('update:visible', false)
  setTimeout(() => resetForm(), 300)
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (!valid) return

    if (!timeRange.value || timeRange.value.length !== 2) {
      ElMessage.warning('请选择投放时间')
      return
    }

    submitting.value = true
    try {
      const data: CampaignCreateRequest | CampaignUpdateRequest = {
        name: formData.name,
        description: formData.description,
        campaignObjective: formData.campaignObjective,
        campaignGoalType: formData.campaignGoalType,
        campaignGoalValue: formData.campaignGoalValue,
        lifetimeBudget: formData.lifetimeBudget,
        startTime: timeRange.value[0],
        endTime: timeRange.value[1]
      }

      if (isEdit.value) {
        await updateCampaign(props.campaignId!, data)
        ElMessage.success('更新成功')
      } else {
        await createCampaign(data as CampaignCreateRequest)
        ElMessage.success('创建成功')
      }

      emit('update:visible', false)
      emit('success')
    } catch (error: any) {
      ElMessage.error(error.message || '操作失败')
    } finally {
      submitting.value = false
    }
  })
}

watch(() => props.visible, (val) => {
  if (val) {
    if (isEdit.value) {
      loadDetail()
    }
  }
})
</script>

<style scoped>
.form-tip {
  margin-left: 10px;
  color: #909399;
  font-size: 12px;
}
</style>
