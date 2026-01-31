<template>
  <el-dialog
    v-model="dialogVisible"
    :title="isEdit ? '编辑广告' : '创建广告'"
    width="600px"
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
          v-model="selectedCampaignId"
          placeholder="请先选择投放活动"
          filterable
          style="width: 100%"
          @change="onCampaignChange"
        >
          <el-option
            v-for="campaign in campaignOptions"
            :key="campaign.id"
            :label="campaign.name"
            :value="campaign.id"
          />
        </el-select>
      </el-form-item>

      <el-form-item v-if="!isEdit" label="所属广告组" prop="adGroupId">
        <el-select
          v-model="formData.adGroupId"
          placeholder="请选择广告组"
          filterable
          style="width: 100%"
          @change="onAdGroupChange"
        >
          <el-option
            v-for="adGroup in adGroupOptions"
            :key="adGroup.id"
            :label="adGroup.name"
            :value="adGroup.id"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="创意" prop="creativeId">
        <el-select
          v-model="formData.creativeId"
          placeholder="请选择创意"
          filterable
          style="width: 100%"
        >
          <el-option
            v-for="creative in creativeOptions"
            :key="creative.id"
            :label="creative.name"
            :value="creative.id"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="广告名称" prop="name">
        <el-input
          v-model="formData.name"
          placeholder="请输入广告名称"
          maxlength="100"
          show-word-limit
        />
      </el-form-item>

      <!-- 落地页设置 -->
      <el-divider content-position="left">落地页设置</el-divider>

      <el-form-item label="落地页URL">
        <el-input
          v-model="formData.landingPageUrl"
          placeholder="留空则使用创意中的设置"
          maxlength="1024"
        />
      </el-form-item>

      <el-form-item label="展示URL">
        <el-input
          v-model="formData.displayUrl"
          placeholder="留空则使用创意中的设置"
          maxlength="255"
        />
      </el-form-item>

      <el-form-item label="追踪参数">
        <el-input
          v-model="trackingParamsInput"
          type="textarea"
          :rows="3"
          placeholder='JSON格式，如：{"utm_source":"rtb","campaign_id":"123"}'
        />
      </el-form-item>

      <!-- 其他设置 -->
      <el-divider content-position="left">其他设置</el-divider>

      <el-form-item label="权重">
        <el-input-number
          v-model="formData.weight"
          :min="1"
          :max="1000"
          :step="10"
          controls-position="right"
          style="width: 200px"
        />
        <span style="margin-left: 10px; color: #909399">值越大分配流量越多</span>
      </el-form-item>

      <el-form-item v-if="isEdit" label="状态">
        <el-radio-group v-model="formData.status">
          <el-radio :label="0">草稿</el-radio>
          <el-radio :label="1">进行中</el-radio>
          <el-radio :label="2">暂停</el-radio>
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
  createAd,
  updateAd,
  getAdById,
  type AdCreateRequest,
  type AdDetail
} from '@/api/ad'
import { getCampaignPage, type CampaignListItem } from '@/api/campaign'
import { getAdGroupsByCampaignId, type AdGroupListItem } from '@/api/adGroup'
import { getCreativePage, type CreativeListItem } from '@/api/creative'

interface Props {
  visible: boolean
  adId?: number
}

interface Emits {
  (e: 'update:visible', value: boolean): void
  (e: 'success'): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

const formRef = ref<FormInstance>()
const submitting = ref(false)
const selectedCampaignId = ref<number | undefined>()
const campaignOptions = ref<CampaignListItem[]>([])
const adGroupOptions = ref<AdGroupListItem[]>([])
const creativeOptions = ref<CreativeListItem[]>([])

const dialogVisible = computed({
  get: () => props.visible,
  set: (val) => emit('update:visible', val)
})

const isEdit = computed(() => !!props.adId)

// 表单数据
const formData = reactive<AdCreateRequest>({
  adGroupId: undefined as number | undefined,
  creativeId: undefined as number | undefined,
  name: '',
  landingPageUrl: undefined,
  displayUrl: undefined,
  trackingParams: undefined,
  weight: 100,
  status: 0
})

// 追踪参数输入（用于显示）
const trackingParamsInput = ref('')

// 监听对话框打开
watch(() => props.visible, (val) => {
  if (val) {
    loadCampaignOptions()
    loadCreativeOptions()
    if (isEdit.value) {
      loadAdDetail()
    }
  }
})

// 表单验证规则
const formRules: FormRules = {
  adGroupId: [
    { required: true, message: '请选择广告组', trigger: 'change' }
  ],
  creativeId: [
    { required: true, message: '请选择创意', trigger: 'change' }
  ],
  name: [
    { required: true, message: '请输入广告名称', trigger: 'blur' },
    { min: 2, max: 100, message: '长度在 2 到 100 个字符', trigger: 'blur' }
  ]
}

// 加载活动选项
const loadCampaignOptions = async () => {
  try {
    const res = await getCampaignPage({ current: 1, size: 1000 })
    campaignOptions.value = res.list
  } catch (error) {
    console.error('加载活动选项失败:', error)
  }
}

// 加载广告组选项
const loadAdGroupOptions = async (campaignId: number) => {
  try {
    const res = await getAdGroupsByCampaignId(campaignId)
    adGroupOptions.value = res
  } catch (error) {
    console.error('加载广告组选项失败:', error)
  }
}

// 加载创意选项
const loadCreativeOptions = async () => {
  try {
    const res = await getCreativePage({ page: 1, size: 1000 })
    creativeOptions.value = res.list.filter(item => item.status === 1)
  } catch (error) {
    console.error('加载创意选项失败:', error)
  }
}

// 活动变化
const onCampaignChange = () => {
  formData.adGroupId = undefined
  if (selectedCampaignId.value) {
    loadAdGroupOptions(selectedCampaignId.value)
  }
}

// 广告组变化
const onAdGroupChange = () => {
  // 可以根据广告组获取默认设置
}

// 加载广告详情
const loadAdDetail = async () => {
  if (!props.adId) return

  try {
    const res = await getAdById(props.adId)

    // 填充表单
    Object.assign(formData, {
      creativeId: res.creativeId,
      name: res.name,
      landingPageUrl: res.landingPageUrl,
      displayUrl: res.displayUrl,
      trackingParams: res.trackingParams,
      weight: res.weight,
      status: res.status
    })

    trackingParamsInput.value = res.trackingParams || ''
  } catch (error) {
    console.error('加载广告详情失败:', error)
  }
}

// 重置表单
const resetForm = () => {
  formRef.value?.resetFields()
  selectedCampaignId.value = undefined
  adGroupOptions.value = []
  Object.assign(formData, {
    adGroupId: undefined,
    creativeId: undefined,
    name: '',
    landingPageUrl: undefined,
    displayUrl: undefined,
    trackingParams: undefined,
    weight: 100,
    status: 0
  })
  trackingParamsInput.value = ''
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
  } catch {
    return
  }

  // 同步追踪参数
  if (trackingParamsInput.value && trackingParamsInput.value.trim()) {
    let jsonStr = trackingParamsInput.value.trim()
    // 自动转换中文引号为英文引号
    jsonStr = jsonStr.replace(/"/g, '"').replace(/"/g, '"').replace(/'/g, "'")
    try {
      JSON.parse(jsonStr) // 验证JSON格式
      formData.trackingParams = jsonStr
    } catch {
      ElMessage.error('追踪参数格式不正确，请输入有效的JSON格式')
      return
    }
  } else {
    formData.trackingParams = undefined
  }

  submitting.value = true
  try {
    if (isEdit.value) {
      await updateAd(props.adId!, formData)
      ElMessage.success('更新成功')
    } else {
      await createAd(formData)
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
</script>

<style scoped>
.el-divider {
  margin: 16px 0;
}
</style>
