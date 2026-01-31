<template>
  <el-dialog
    v-model="dialogVisible"
    :title="isEdit ? '编辑广告组' : '创建广告组'"
    width="700px"
    :close-on-click-modal="false"
    @close="handleClose"
  >
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="120px"
      @submit.prevent="handleSubmit"
    >
      <!-- 基本信息 -->
      <el-divider content-position="left">基本信息</el-divider>

      <el-form-item v-if="!isEdit" label="所属活动" prop="campaignId">
        <el-select
          v-model="formData.campaignId"
          placeholder="请选择投放活动"
          filterable
          style="width: 100%"
        >
          <el-option
            v-for="campaign in campaignOptions"
            :key="campaign.id"
            :label="`${campaign.name} (${campaign.campaignObjectiveName})`"
            :value="campaign.id"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="广告组名称" prop="name">
        <el-input
          v-model="formData.name"
          placeholder="请输入广告组名称"
          maxlength="100"
          show-word-limit
        />
      </el-form-item>

      <el-form-item label="广告组描述">
        <el-input
          v-model="formData.description"
          type="textarea"
          :rows="2"
          placeholder="请输入广告组描述（可选）"
          maxlength="500"
          show-word-limit
        />
      </el-form-item>

      <!-- 出价设置 -->
      <el-divider content-position="left">出价设置</el-divider>

      <el-form-item label="出价策略" prop="bidStrategy">
        <el-radio-group v-model="formData.bidStrategy">
          <el-radio :label="1">固定CPM</el-radio>
          <el-radio :label="2" disabled>智能出价</el-radio>
          <el-radio :label="3" disabled>目标CPA</el-radio>
          <el-radio :label="4" disabled>最高赢价</el-radio>
        </el-radio-group>
      </el-form-item>

      <el-form-item label="基础出价" prop="baseBidPrice">
        <el-input-number
          v-model="formData.baseBidPrice"
          :min="0.01"
          :max="100"
          :precision="2"
          :step="0.1"
          controls-position="right"
          style="width: 200px"
        />
        <span style="margin-left: 10px; color: #909399">元/千次</span>
      </el-form-item>

      <el-form-item label="最高出价" prop="maxBid">
        <el-input-number
          v-model="formData.maxBid"
          :min="0.01"
          :max="100"
          :precision="2"
          :step="0.1"
          controls-position="right"
          style="width: 200px"
        />
        <span style="margin-left: 10px; color: #909399">元/千次（出价上限）</span>
      </el-form-item>

      <el-form-item label="竞价底价" prop="bidFloor">
        <el-input-number
          v-model="formData.bidFloor"
          :min="0.01"
          :max="100"
          :precision="2"
          :step="0.1"
          controls-position="right"
          style="width: 200px"
        />
        <span style="margin-left: 10px; color: #909399">元/千次（低于此价格不参与竞价）</span>
      </el-form-item>

      <!-- 预算控制 -->
      <el-divider content-position="left">预算控制</el-divider>

      <el-form-item label="日预算">
        <el-input-number
          v-model="formData.dailyBudget"
          :min="10"
          :max="1000000"
          :precision="2"
          :step="10"
          controls-position="right"
          style="width: 200px"
        />
        <span style="margin-left: 10px; color: #909399">元/天（留空则继承活动预算）</span>
      </el-form-item>

      <el-form-item label="投放速度">
        <el-radio-group v-model="formData.deliveryMode">
          <el-radio :label="2">均匀投放</el-radio>
          <el-radio :label="1">加速投放</el-radio>
        </el-radio-group>
      </el-form-item>

      <!-- 定向设置 -->
      <el-divider content-position="left">定向设置</el-divider>

      <el-form-item label="地域定向" prop="targetingGeo">
        <el-select
          v-model="targetingGeoArray"
          multiple
          filterable
          allow-create
          placeholder="请选择或输入地域代码（如：CN-11）"
          style="width: 100%"
        >
          <el-option label="全国" value="CN" />
          <el-option label="北京" value="CN-11" />
          <el-option label="天津" value="CN-12" />
          <el-option label="上海" value="CN-31" />
          <el-option label="广东" value="CN-44" />
        </el-select>
      </el-form-item>

      <el-form-item label="设备定向" prop="targetingDevice">
        <el-checkbox-group v-model="targetingDeviceArray">
          <el-checkbox :label="1">手机</el-checkbox>
          <el-checkbox :label="2">个人电脑</el-checkbox>
          <el-checkbox :label="3">平板</el-checkbox>
          <el-checkbox :label="4">联网电视</el-checkbox>
          <el-checkbox :label="5">机顶盒</el-checkbox>
        </el-checkbox-group>
      </el-form-item>

      <el-form-item label="操作系统">
        <el-checkbox-group v-model="targetingOsArray">
          <el-checkbox label="iOS">iOS</el-checkbox>
          <el-checkbox label="Android">Android</el-checkbox>
          <el-checkbox label="Windows">Windows</el-checkbox>
          <el-checkbox label="macOS">macOS</el-checkbox>
          <el-checkbox label="Linux">Linux</el-checkbox>
          <el-checkbox label="ROKU">ROKU</el-checkbox>
          <el-checkbox label="Chrome OS">Chrome OS</el-checkbox>
          <el-checkbox label="Tizen">Tizen</el-checkbox>
          <el-checkbox label="WebOS">WebOS</el-checkbox>
        </el-checkbox-group>
      </el-form-item>

      <!-- 时段控制 -->
      <el-divider content-position="left">时段控制</el-divider>

      <el-form-item label="投放时段">
        <el-radio-group v-model="formData.scheduleType">
          <el-radio :label="1">全天投放</el-radio>
          <el-radio :label="2">工作日</el-radio>
          <el-radio :label="3">自定义</el-radio>
        </el-radio-group>
      </el-form-item>

      <!-- 频次控制 -->
      <el-divider content-position="left">频次控制</el-divider>

      <el-form-item label="展示频次">
        <el-input-number
          v-model="formData.frequencyCap"
          :min="1"
          :max="50"
          controls-position="right"
          style="width: 150px"
        />
        <el-select
          v-model="formData.frequencyCapPeriod"
          style="width: 100px; margin-left: 10px"
        >
          <el-option label="次/小时" :value="1" />
          <el-option label="次/天" :value="2" />
          <el-option label="次/周" :value="3" />
          <el-option label="次/月" :value="4" />
        </el-select>
        <span style="margin-left: 10px; color: #909399">（留空则不限制）</span>
      </el-form-item>

      <!-- 品牌安全 -->
      <el-divider content-position="left">品牌安全</el-divider>

      <el-form-item label="安全级别">
        <el-radio-group v-model="formData.brandSafetyLevel">
          <el-radio :label="0">不限制</el-radio>
          <el-radio :label="1">宽松</el-radio>
          <el-radio :label="2">中等</el-radio>
          <el-radio :label="3">严格</el-radio>
        </el-radio-group>
      </el-form-item>
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
import { ref, reactive, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import {
  createAdGroup,
  updateAdGroup,
  getAdGroupById,
  type AdGroupCreateRequest,
  type AdGroupDetail
} from '@/api/adGroup'
import { getCampaignPage, type CampaignListItem } from '@/api/campaign'

interface Props {
  visible: boolean
  adGroupId?: number
}

interface Emits {
  (e: 'update:visible', value: boolean): void
  (e: 'success'): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

const formRef = ref<FormInstance>()
const submitting = ref(false)
const campaignOptions = ref<CampaignListItem[]>([])

const dialogVisible = computed({
  get: () => props.visible,
  set: (val) => emit('update:visible', val)
})

const isEdit = computed(() => !!props.adGroupId)

// 表单数据
const formData = reactive<AdGroupCreateRequest>({
  campaignId: undefined as number | undefined,
  name: '',
  description: '',
  bidStrategy: 1,
  baseBidPrice: undefined as number | undefined,
  maxBid: undefined as number | undefined,
  bidFloor: undefined as number | undefined,
  targetCpa: undefined,
  targetRoas: undefined,
  bidAdjustments: undefined,
  dailyBudget: undefined,
  deliveryMode: 2,
  deliveryPace: 10,
  targetingGeo: undefined,
  targetingGeoExclude: undefined,
  targetingDevice: undefined,
  targetingOs: undefined,
  targetingOsVersion: undefined,
  targetingCarrier: undefined,
  targetingConnectionType: undefined,
  targetingBrowser: undefined,
  targetingKeywords: undefined,
  targetingKeywordsExclude: undefined,
  targetingIabCategories: undefined,
  targetingIabCategoriesExclude: undefined,
  targetingUserSegments: undefined,
  targetingUserSegmentsExclude: undefined,
  targetingAudienceType: undefined,
  scheduleType: 1,
  scheduleConfig: undefined,
  frequencyCap: undefined,
  frequencyCapPeriod: 2,
  brandSafetyLevel: undefined,
  brandSafetyCategoriesExclude: undefined,
  priority: 0
})

// 数组类型的定向（用于组件绑定）
const targetingGeoArray = ref<string[]>([])
const targetingDeviceArray = ref<number[]>([])
const targetingOsArray = ref<string[]>([])

// 监听数组变化，同步到JSON字符串
watch(targetingGeoArray, (val) => {
  formData.targetingGeo = val.length > 0 ? JSON.stringify(val) : undefined
})

watch(targetingDeviceArray, (val) => {
  formData.targetingDevice = val.length > 0 ? JSON.stringify(val) : undefined
})

watch(targetingOsArray, (val) => {
  formData.targetingOs = val.length > 0 ? JSON.stringify(val) : undefined
})

// 表单验证规则
const formRules: FormRules = {
  campaignId: [
    { required: true, message: '请选择投放活动', trigger: 'change' }
  ],
  name: [
    { required: true, message: '请输入广告组名称', trigger: 'blur' },
    { min: 2, max: 100, message: '长度在 2 到 100 个字符', trigger: 'blur' }
  ],
  bidStrategy: [
    { required: true, message: '请选择出价策略', trigger: 'change' }
  ],
  baseBidPrice: [
    { required: true, message: '请输入基础出价', trigger: 'blur' },
    { type: 'number', min: 0.01, message: '出价不能低于 0.01 元', trigger: 'blur' }
  ],
  maxBid: [
    { required: true, message: '请输入最高出价', trigger: 'blur' },
    { type: 'number', min: 0.01, message: '出价不能低于 0.01 元', trigger: 'blur' },
    {
      validator: (_rule, _value, callback) => {
        if (formData.maxBid && formData.maxBid < formData.baseBidPrice) {
          callback(new Error('最高出价不能低于基础出价'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  bidFloor: [
    { required: true, message: '请输入竞价底价', trigger: 'blur' },
    { type: 'number', min: 0.01, message: '出价不能低于 0.01 元', trigger: 'blur' },
    {
      validator: (_rule, _value, callback) => {
        if (formData.bidFloor && formData.bidFloor > formData.baseBidPrice) {
          callback(new Error('竞价底价不能高于基础出价'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  targetingGeo: [
    { required: true, message: '请选择地域定向', trigger: 'change' }
  ],
  targetingDevice: [
    { required: true, message: '请选择设备定向', trigger: 'change' }
  ]
}

// 加载活动选项
const loadCampaignOptions = async () => {
  try {
    const res = await getCampaignPage({ current: 1, size: 1000 })
    campaignOptions.value = res.list.filter(item => item.status === 1 || item.status === 2)
  } catch (error) {
    console.error('加载活动选项失败:', error)
  }
}

// 加载广告组详情
const loadAdGroupDetail = async () => {
  if (!props.adGroupId) return

  try {
    const res = await getAdGroupById(props.adGroupId)

    // 填充表单
    Object.assign(formData, {
      name: res.name,
      description: res.description,
      bidStrategy: res.bidStrategy,
      baseBidPrice: res.baseBidPrice,
      maxBid: res.maxBid,
      bidFloor: res.bidFloor,
      targetCpa: res.targetCpa,
      targetRoas: res.targetRoas,
      bidAdjustments: res.bidAdjustments,
      dailyBudget: res.dailyBudget,
      deliveryMode: res.deliveryMode,
      deliveryPace: res.deliveryPace,
      targetingAudienceType: res.targetingAudienceType,
      scheduleType: res.scheduleType,
      scheduleConfig: res.scheduleConfig,
      frequencyCap: res.frequencyCap,
      frequencyCapPeriod: res.frequencyCapPeriod,
      brandSafetyLevel: res.brandSafetyLevel,
      brandSafetyCategoriesExclude: res.brandSafetyCategoriesExclude,
      priority: res.priority
    })

    // 解析JSON字段到数组
    if (res.targetingGeo) {
      try {
        targetingGeoArray.value = JSON.parse(res.targetingGeo)
      } catch {
        targetingGeoArray.value = []
      }
    } else {
      targetingGeoArray.value = []
    }
    if (res.targetingDevice) {
      try {
        targetingDeviceArray.value = JSON.parse(res.targetingDevice)
      } catch {
        targetingDeviceArray.value = []
      }
    } else {
      targetingDeviceArray.value = []
    }
    if (res.targetingOs) {
      try {
        targetingOsArray.value = JSON.parse(res.targetingOs)
      } catch {
        targetingOsArray.value = []
      }
    } else {
      targetingOsArray.value = []
    }
  } catch (error) {
    console.error('加载广告组详情失败:', error)
  }
}

// 重置表单
const resetForm = () => {
  formRef.value?.resetFields()
  Object.assign(formData, {
    campaignId: undefined,
    name: '',
    description: '',
    bidStrategy: 1,
    baseBidPrice: undefined,
    maxBid: undefined,
    bidFloor: undefined,
    dailyBudget: undefined,
    deliveryMode: 2,
    deliveryPace: 10,
    scheduleType: 1,
    frequencyCap: undefined,
    frequencyCapPeriod: 2,
    brandSafetyLevel: 0,
    priority: 0
  })
  targetingGeoArray.value = []
  targetingDeviceArray.value = []
  targetingOsArray.value = []
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
  } catch {
    return
  }

  submitting.value = true
  try {
    if (isEdit.value) {
      await updateAdGroup(props.adGroupId!, formData)
      ElMessage.success('更新成功')
    } else {
      await createAdGroup(formData)
      ElMessage.success('创建成功')
    }
    emit('success')
    handleClose()
  } catch (error: any) {
    console.error('提交失败:', error)
    ElMessage.error(error.message || '操作失败，请重试')
  } finally {
    submitting.value = false
  }
}

// 关闭对话框
const handleClose = () => {
  resetForm()
  emit('update:visible', false)
}

// 监听对话框打开
watch(() => props.visible, (val) => {
  if (val) {
    loadCampaignOptions()
    if (isEdit.value) {
      loadAdGroupDetail()
    }
  }
})
</script>

<style scoped>
.el-divider {
  margin: 16px 0;
}
</style>
