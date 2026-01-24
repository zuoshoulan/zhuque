<template>
  <div class="ad-groups-view">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>广告组管理</span>
          <el-button type="primary" :icon="Plus" @click="handleAdd">创建广告组</el-button>
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
          @change="handleQuery"
        >
          <el-option label="全部活动" :value="undefined" />
          <el-option
            v-for="campaign in campaignOptions"
            :key="campaign.id"
            :label="campaign.name"
            :value="campaign.id"
          />
        </el-select>
        <el-input
          v-model="queryParams.name"
          placeholder="搜索广告组名称"
          clearable
          style="width: 250px; margin-left: 10px"
          @keyup.enter="handleQuery"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-select
          v-model="queryParams.bidStrategy"
          placeholder="出价策略"
          clearable
          style="width: 150px; margin-left: 10px"
          @change="handleQuery"
        >
          <el-option label="全部" :value="undefined" />
          <el-option label="固定CPM" :value="1" />
          <el-option label="智能出价" :value="2" />
          <el-option label="目标CPA" :value="3" />
          <el-option label="最高赢价" :value="4" />
        </el-select>
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
        <el-table-column prop="name" label="广告组名称" width="180" show-overflow-tooltip />
        <el-table-column prop="campaignName" label="所属活动" width="150" show-overflow-tooltip />
        <el-table-column prop="bidStrategyName" label="出价策略" width="100" />
        <el-table-column label="基础出价" width="120" align="right">
          <template #default="{ row }">
            ¥{{ row.baseBidPrice.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column label="日预算/消耗" width="180">
          <template #default="{ row }">
            <div v-if="row.dailyBudget" class="budget-info">
              <span>¥{{ row.dailyBudget.toFixed(2) }}</span>
              <span class="used">已用 ¥{{ row.dailyBudgetUsed.toFixed(2) }}</span>
            </div>
            <div v-else class="budget-info">
              <span class="inherit">继承活动预算</span>
              <span class="used">¥{{ row.dailyBudgetUsed.toFixed(2) }}</span>
            </div>
            <el-progress
              v-if="row.dailyBudget"
              :percentage="row.dailyUsedPercent || 0"
              :color="getProgressColor(row.dailyUsedPercent)"
              :stroke-width="6"
            />
          </template>
        </el-table-column>
        <el-table-column prop="adCount" label="广告数" width="80" align="center" />
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
    <AdGroupFormDialog
      v-model:visible="dialogVisible"
      :ad-group-id="editingId"
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
  getAdGroupPage,
  deleteAdGroup,
  startAdGroup,
  pauseAdGroup,
  type AdGroupListItem
} from '@/api/adGroup'
import { getCampaignPage, type CampaignListItem } from '@/api/campaign'
import AdGroupFormDialog from '@/components/AdGroupFormDialog.vue'

const loading = ref(false)
const tableData = ref<AdGroupListItem[]>([])
const total = ref(0)
const campaignOptions = ref<CampaignListItem[]>([])

const queryParams = reactive({
  current: 1,
  size: 10,
  campaignId: undefined as number | undefined,
  name: undefined as string | undefined,
  bidStrategy: undefined as number | undefined,
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

// 查询列表
const handleQuery = async () => {
  loading.value = true
  try {
    const res = await getAdGroupPage(queryParams)
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
  queryParams.name = undefined
  queryParams.bidStrategy = undefined
  queryParams.status = undefined
  queryParams.current = 1
  handleQuery()
}

// 新增
const handleAdd = () => {
  editingId.value = undefined
  dialogVisible.value = true
}

// 查看
const handleView = (row: AdGroupListItem) => {
  ElMessage.info('详情页待实现')
  // TODO: 跳转到详情页
}

// 编辑
const handleEdit = (row: AdGroupListItem) => {
  editingId.value = row.id
  dialogVisible.value = true
}

// 启动
const handleStart = async (row: AdGroupListItem) => {
  try {
    await ElMessageBox.confirm(`确认启动广告组「${row.name}」吗？`, '启动确认', {
      type: 'warning'
    })
    await startAdGroup(row.id)
    ElMessage.success('启动成功')
    handleQuery()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('启动失败:', error)
    }
  }
}

// 暂停
const handlePause = async (row: AdGroupListItem) => {
  try {
    await ElMessageBox.confirm(`确认暂停广告组「${row.name}」吗？`, '暂停确认', {
      type: 'warning'
    })
    await pauseAdGroup(row.id)
    ElMessage.success('暂停成功')
    handleQuery()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('暂停失败:', error)
    }
  }
}

// 删除
const handleDelete = async (row: AdGroupListItem) => {
  try {
    await ElMessageBox.confirm(
      `确认删除广告组「${row.name}」吗？删除后无法恢复！`,
      '删除确认',
      { type: 'error', confirmButtonText: '确认删除' }
    )
    await deleteAdGroup(row.id)
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

// 获取进度条颜色
const getProgressColor = (percent: number) => {
  if (percent >= 90) return '#f56c6c'
  if (percent >= 70) return '#e6a23c'
  return '#67c23a'
}

onMounted(() => {
  loadCampaignOptions()
  handleQuery()
})
</script>

<style scoped>
.ad-groups-view {
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

.budget-info {
  display: flex;
  justify-content: space-between;
  margin-bottom: 4px;
  font-size: 12px;
}

.budget-info .used {
  color: #909399;
}

.budget-info .inherit {
  color: #67c23a;
}

.data-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
  font-size: 12px;
  color: #606266;
}
</style>
