<template>
  <div class="campaigns-view">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>投放活动管理</span>
          <el-button type="primary" :icon="Plus" @click="handleAdd">创建活动</el-button>
        </div>
      </template>

      <!-- 搜索栏 -->
      <div class="search-bar">
        <el-input
          v-model="queryParams.name"
          placeholder="搜索活动名称"
          clearable
          style="width: 300px"
          @keyup.enter="handleQuery"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-select
          v-model="queryParams.campaignObjective"
          placeholder="营销目标"
          clearable
          style="width: 150px; margin-left: 10px"
          @change="handleQuery"
        >
          <el-option label="全部" :value="undefined" />
          <el-option label="品牌曝光" :value="1" />
          <el-option label="流量" :value="2" />
          <el-option label="转化" :value="3" />
          <el-option label="ROI" :value="4" />
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
        <el-table-column prop="name" label="活动名称" width="200" show-overflow-tooltip />
        <el-table-column prop="campaignObjectiveName" label="营销目标" width="100" />
        <el-table-column label="预算/消耗" width="180">
          <template #default="{ row }">
            <div class="budget-info">
              <span>{{ formatMoney(row.lifetimeBudget) }}</span>
              <span class="used">已用 {{ formatMoney(row.lifetimeBudgetUsed) }}</span>
            </div>
            <el-progress
              :percentage="row.usedPercent || 0"
              :color="getProgressColor(row.usedPercent)"
              :stroke-width="6"
            />
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.displayStatusType as any">
              {{ row.displayStatusName }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="timeRange" label="投放时间" width="140" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="280" align="center" fixed="right">
          <template #default="{ row }">
            <el-button size="small" :icon="View" @click="handleView(row)">查看</el-button>
            <el-button size="small" :icon="Edit" @click="handleEdit(row)"
              :disabled="row.displayStatusName === '已完成'">编辑</el-button>
            <el-button
              v-if="row.status === 0"
              size="small"
              type="primary"
              :icon="VideoPlay"
              @click="handlePublish(row)"
            >
              发布
            </el-button>
            <el-button
              v-if="row.status === 2"
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
    <CampaignFormDialog
      v-model:visible="dialogVisible"
      :campaign-id="editingId"
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
  getCampaignPage,
  deleteCampaign,
  startCampaign,
  pauseCampaign,
  type CampaignListItem
} from '@/api/campaign'
import CampaignFormDialog from '@/components/CampaignFormDialog.vue'

const loading = ref(false)
const tableData = ref<CampaignListItem[]>([])
const total = ref(0)

const queryParams = reactive({
  current: 1,
  size: 10,
  name: undefined as string | undefined,
  campaignObjective: undefined as number | undefined,
  status: undefined as number | undefined
})

const dialogVisible = ref(false)
const editingId = ref<number>()

// 查询列表
const handleQuery = async () => {
  loading.value = true
  try {
    const res = await getCampaignPage(queryParams)
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
  queryParams.name = undefined
  queryParams.campaignObjective = undefined
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
const handleView = (row: CampaignListItem) => {
  // TODO: 跳转到详情页
  ElMessage.info('详情页待实现')
}

// 编辑
const handleEdit = (row: CampaignListItem) => {
  editingId.value = row.id
  dialogVisible.value = true
}

// 发布
const handlePublish = async (row: CampaignListItem) => {
  try {
    await ElMessageBox.confirm(`确认发布活动「${row.name}」吗？发布后将开始投放。`, '发布确认', {
      type: 'warning'
    })
    await startCampaign(row.id)
    ElMessage.success('发布成功')
    handleQuery()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('发布失败:', error)
    }
  }
}

// 启动
const handleStart = async (row: CampaignListItem) => {
  try {
    await ElMessageBox.confirm(`确认启动活动「${row.name}」吗？`, '启动确认', {
      type: 'warning'
    })
    await startCampaign(row.id)
    ElMessage.success('启动成功')
    handleQuery()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('启动失败:', error)
    }
  }
}

// 暂停
const handlePause = async (row: CampaignListItem) => {
  try {
    await ElMessageBox.confirm(`确认暂停活动「${row.name}」吗？`, '暂停确认', {
      type: 'warning'
    })
    await pauseCampaign(row.id)
    ElMessage.success('暂停成功')
    handleQuery()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('暂停失败:', error)
    }
  }
}

// 删除
const handleDelete = async (row: CampaignListItem) => {
  try {
    await ElMessageBox.confirm(
      `确认删除活动「${row.name}」吗？删除后无法恢复！`,
      '删除确认',
      { type: 'error', confirmButtonText: '确认删除' }
    )
    await deleteCampaign(row.id)
    ElMessage.success('删除成功')
    handleQuery()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
    }
  }
}

// 格式化金额
const formatMoney = (value: number) => {
  return `¥${value.toFixed(2)}`
}

// 获取进度条颜色
const getProgressColor = (percent: number) => {
  if (percent >= 90) return '#f56c6c'
  if (percent >= 70) return '#e6a23c'
  return '#67c23a'
}

onMounted(() => {
  handleQuery()
})
</script>

<style scoped>
.campaigns-view {
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
</style>
