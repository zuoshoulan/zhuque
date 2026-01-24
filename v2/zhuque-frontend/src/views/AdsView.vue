<template>
  <div class="ads-view">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>广告</span>
          <el-button type="primary" :icon="Plus" @click="handleAdd">创建广告</el-button>
        </div>
      </template>

      <!-- 搜索栏 -->
      <div class="search-bar">
        <el-select
          v-model="queryParams.campaignId"
          placeholder="选择投放活动"
          clearable
          filterable
          style="width: 200px"
          @change="onCampaignChange"
        >
          <el-option label="全部活动" :value="undefined" />
          <el-option
            v-for="campaign in campaignOptions"
            :key="campaign.id"
            :label="campaign.name"
            :value="campaign.id"
          />
        </el-select>
        <el-select
          v-model="queryParams.adGroupId"
          placeholder="选择广告组"
          clearable
          filterable
          style="width: 200px; margin-left: 10px"
          @change="handleQuery"
        >
          <el-option label="全部广告组" :value="undefined" />
          <el-option
            v-for="adGroup in adGroupOptions"
            :key="adGroup.id"
            :label="adGroup.name"
            :value="adGroup.id"
          />
        </el-select>
        <el-select
          v-model="queryParams.creativeId"
          placeholder="选择创意"
          clearable
          filterable
          style="width: 200px; margin-left: 10px"
          @change="handleQuery"
        >
          <el-option label="全部创意" :value="undefined" />
          <el-option
            v-for="creative in creativeOptions"
            :key="creative.id"
            :label="creative.name"
            :value="creative.id"
          />
        </el-select>
        <el-input
          v-model="queryParams.name"
          placeholder="搜索广告名称"
          clearable
          style="width: 250px; margin-left: 10px"
          @keyup.enter="handleQuery"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-select
          v-model="queryParams.status"
          placeholder="状态"
          clearable
          style="width: 150px; margin-left: 10px"
          @change="handleQuery"
        >
          <el-option label="全部" :value="undefined" />
          <el-option label="草稿" :value="0" />
          <el-option label="进行中" :value="1" />
          <el-option label="暂停" :value="2" />
        </el-select>
        <el-button type="primary" :icon="Search" @click="handleQuery">搜索</el-button>
        <el-button :icon="Refresh" @click="handleReset">重置</el-button>
      </div>

      <!-- 数据表格 -->
      <el-table
        v-loading="loading"
        :data="tableData"
        style="width: 100%; margin-top: 20px"
        border
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="广告名称" width="180" show-overflow-tooltip />
        <el-table-column prop="adGroupName" label="所属广告组" width="150" show-overflow-tooltip />
        <el-table-column prop="creativeName" label="创意" width="150" show-overflow-tooltip />
        <el-table-column prop="landingPageUrl" label="落地页" width="200" show-overflow-tooltip />
        <el-table-column prop="weight" label="权重" width="80" align="center" />
        <el-table-column label="今日数据" width="150">
          <template #default="{ row }">
            <div class="data-info">
              <span>展现: {{ formatNumber(row.todayImpressions) }}</span>
              <span>点击: {{ formatNumber(row.todayClicks) }}</span>
              <span v-if="row.todayCtr > 0">CTR: {{ (row.todayCtr * 100).toFixed(2) }}%</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.displayStatusType as any">
              {{ row.statusName }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="280" align="center" fixed="right">
          <template #default="{ row }">
            <el-button size="small" :icon="View" @click="handleView(row)">查看</el-button>
            <el-button size="small" :icon="Edit" @click="handleEdit(row)">编辑</el-button>
            <el-button
              v-if="row.status === 0 || row.status === 2"
              size="small"
              type="success"
              :icon="VideoPlay"
              @click="handleStart(row)"
            >
              启动
            </el-button>
            <el-button
              v-if="row.status === 1"
              size="small"
              type="warning"
              :icon="VideoPause"
              @click="handlePause(row)"
            >
              暂停
            </el-button>
            <el-button
              size="small"
              type="danger"
              :icon="Delete"
              @click="handleDelete(row)"
              :disabled="row.status !== 0"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="queryParams.current"
        v-model:page-size="queryParams.size"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        style="margin-top: 20px; justify-content: center"
        @size-change="handleQuery"
        @current-change="handleQuery"
      />
    </el-card>

    <!-- 创建/编辑对话框 -->
    <AdFormDialog
      v-model:visible="dialogVisible"
      :ad-id="editingId"
      @success="handleQuery"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Plus, Search, Refresh, Edit, Delete, View, VideoPlay, VideoPause
} from '@element-plus/icons-vue'
import {
  getAdPage,
  deleteAd,
  startAd,
  pauseAd,
  type AdListItem
} from '@/api/ad'
import { getCampaignPage, type CampaignListItem } from '@/api/campaign'
import { getAdGroupsByCampaignId, type AdGroupListItem } from '@/api/adGroup'
import { getCreativePage, type CreativeListItem } from '@/api/creative'
import AdFormDialog from '@/components/AdFormDialog.vue'

const loading = ref(false)
const tableData = ref<AdListItem[]>([])
const total = ref(0)
const campaignOptions = ref<CampaignListItem[]>([])
const adGroupOptions = ref<AdGroupListItem[]>([])
const creativeOptions = ref<CreativeListItem[]>([])

const queryParams = reactive({
  current: 1,
  size: 10,
  campaignId: undefined as number | undefined,
  adGroupId: undefined as number | undefined,
  creativeId: undefined as number | undefined,
  name: undefined as string | undefined,
  status: undefined as number | undefined
})

const dialogVisible = ref(false)
const editingId = ref<number>()

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
const loadAdGroupOptions = async (campaignId?: number) => {
  if (!campaignId) {
    adGroupOptions.value = []
    return
  }
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
    creativeOptions.value = res.list
  } catch (error) {
    console.error('加载创意选项失败:', error)
  }
}

// 活动变化时重新加载广告组
const onCampaignChange = () => {
  queryParams.adGroupId = undefined
  loadAdGroupOptions(queryParams.campaignId)
  handleQuery()
}

// 查询列表
const handleQuery = async () => {
  loading.value = true
  try {
    const res = await getAdPage(queryParams)
    tableData.value = res.list
    total.value = res.total
  } catch (error) {
    console.error('查询失败:', error)
  } finally {
    loading.value = false
  }
}

// 重置搜索
const handleReset = () => {
  queryParams.campaignId = undefined
  queryParams.adGroupId = undefined
  queryParams.creativeId = undefined
  queryParams.name = undefined
  queryParams.status = undefined
  queryParams.current = 1
  adGroupOptions.value = []
  handleQuery()
}

// 新增
const handleAdd = () => {
  editingId.value = undefined
  dialogVisible.value = true
}

// 查看
const handleView = (row: AdListItem) => {
  ElMessage.info('详情页待实现')
  // TODO: 跳转到详情页
}

// 编辑
const handleEdit = (row: AdListItem) => {
  editingId.value = row.id
  dialogVisible.value = true
}

// 启动
const handleStart = async (row: AdListItem) => {
  try {
    await ElMessageBox.confirm(`确认启动广告「${row.name}」吗？`, '启动确认', {
      type: 'warning'
    })
    await startAd(row.id)
    ElMessage.success('启动成功')
    handleQuery()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('启动失败:', error)
    }
  }
}

// 暂停
const handlePause = async (row: AdListItem) => {
  try {
    await ElMessageBox.confirm(`确认暂停广告「${row.name}」吗？`, '暂停确认', {
      type: 'warning'
    })
    await pauseAd(row.id)
    ElMessage.success('暂停成功')
    handleQuery()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('暂停失败:', error)
    }
  }
}

// 删除
const handleDelete = async (row: AdListItem) => {
  try {
    await ElMessageBox.confirm(
      `确认删除广告「${row.name}」吗？删除后无法恢复！`,
      '删除确认',
      { type: 'error', confirmButtonText: '确认删除' }
    )
    await deleteAd(row.id)
    ElMessage.success('删除成功')
    handleQuery()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
    }
  }
}

// 格式化数字
const formatNumber = (value: number) => {
  if (value >= 10000) {
    return `${(value / 10000).toFixed(1)}万`
  }
  return value.toString()
}

onMounted(() => {
  loadCampaignOptions()
  loadCreativeOptions()
  handleQuery()
})
</script>

<style scoped>
.ads-view {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-bar {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 10px;
}

.data-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
  font-size: 12px;
  color: #606266;
}
</style>
