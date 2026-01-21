<template>
  <div class="creatives-view">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>创意管理</span>
          <el-button type="primary" :icon="Plus" @click="handleAdd">添加创意</el-button>
        </div>
      </template>

      <!-- 搜索栏 -->
      <div class="search-bar">
        <el-input
          v-model="queryParams.keyword"
          placeholder="搜索创意名称"
          clearable
          style="width: 300px"
          @keyup.enter="handleQuery"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-select
          v-model="queryParams.status"
          placeholder="创意状态"
          clearable
          style="width: 150px; margin-left: 10px"
          @change="handleQuery"
        >
          <el-option label="全部" :value="undefined" />
          <el-option label="启用" :value="1" />
          <el-option label="禁用" :value="0" />
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
        <el-table-column prop="name" label="创意名称" width="200" />
        <el-table-column prop="materialCount" label="素材数量" width="100" />
        <el-table-column prop="statusName" label="状态" width="100" />
        <el-table-column prop="status" label="启用/禁用" width="120" align="center">
          <template #default="{ row }">
            <el-switch
              v-model="row.status"
              :active-value="1"
              :inactive-value="0"
              @change="handleStatusChange(row)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template #default="{ row }">
            <el-button size="small" :icon="Edit" @click="handleEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" :icon="Delete" @click="handleDelete(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="queryParams.page"
        v-model:page-size="queryParams.size"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        style="margin-top: 20px; justify-content: center"
        @size-change="handleQuery"
        @current-change="handleQuery"
      />
    </el-card>

    <!-- 添加/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="700px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="120px"
        @submit.prevent="handleSubmit"
      >
        <el-form-item label="创意名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入创意名称" />
        </el-form-item>
        <el-form-item label="创意描述" prop="description">
          <el-input v-model="formData.description" type="textarea" :rows="3" placeholder="请输入创意描述" />
        </el-form-item>
        <el-form-item label="落地页URL" prop="landingPageUrl">
          <el-input v-model="formData.landingPageUrl" placeholder="请输入落地页URL" />
        </el-form-item>
        <el-form-item label="展示URL" prop="displayUrl">
          <el-input v-model="formData.displayUrl" placeholder="请输入展示URL" />
        </el-form-item>
        <el-form-item label="广告主域名" prop="advertiserDomain">
          <el-input v-model="formData.advertiserDomain" placeholder="请输入广告主域名，例如: example.com" />
        </el-form-item>
        <el-form-item label="IAB类别" prop="cat">
          <el-select
            v-model="formData.cat"
            multiple
            placeholder="请选择IAB内容类别"
            style="width: 100%"
          >
            <el-option label="IAB24-1 (Automotive)" value="IAB24-1" />
            <el-option label="IAB24-6 (Business and Finance)" value="IAB24-6" />
            <el-option label="IAB1 (Education)" value="IAB1" />
            <el-option label="IAB9 (Hobbies & Interests)" value="IAB9" />
          </el-select>
        </el-form-item>
        <el-form-item label="语言" prop="language">
          <el-select v-model="formData.language" placeholder="请选择语言" style="width: 100%">
            <el-option label="中文" value="zh-CN" />
            <el-option label="English" value="en" />
            <el-option label="Japanese" value="ja" />
            <el-option label="Korean" value="ko" />
          </el-select>
        </el-form-item>
        <el-form-item label="生效时间" prop="startTime">
          <el-date-picker
            v-model="formData.startTime"
            type="datetime"
            placeholder="选择生效时间"
            style="width: 100%"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>
        <el-form-item label="失效时间" prop="endTime">
          <el-date-picker
            v-model="formData.endTime"
            type="datetime"
            placeholder="选择失效时间"
            style="width: 100%"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Refresh, Edit, Delete } from '@element-plus/icons-vue'
import {
  getCreativePage,
  getCreativeById,
  createCreative,
  updateCreative,
  deleteCreative,
  updateCreativeStatus
} from '@/api/creative'
import type { CreativeListItem, CreativeCreateRequest, CreativeUpdateRequest } from '@/api/creative'

// 查询参数
const queryParams = reactive({
  page: 1,
  size: 10,
  keyword: '',
  status: undefined as number | undefined
})

// 表格数据
const tableData = ref<CreativeListItem[]>([])
const total = ref(0)
const loading = ref(false)

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('')
const submitLoading = ref(false)

// 表单数据
const formData = reactive<Partial<CreativeCreateRequest> & { id?: number }>({
  id: undefined,
  advertiserId: undefined,
  name: '',
  description: '',
  landingPageUrl: '',
  displayUrl: '',
  advertiserDomain: '',
  cat: [],
  attr: [],
  language: undefined,
  startTime: undefined,
  endTime: undefined
})

// 表单引用
const formRef = ref<FormInstance>()

// 表单验证规则
const formRules: FormRules = {
  name: [
    { required: true, message: '请输入创意名称', trigger: 'blur' }
  ],
  landingPageUrl: [
    { required: true, message: '请输入落地页URL', trigger: 'blur' }
  ],
  displayUrl: [
    { required: true, message: '请输入展示URL', trigger: 'blur' }
  ]
}

// 查询创意列表
const handleQuery = async () => {
  loading.value = true
  try {
    const data = await getCreativePage(queryParams)
    console.log('创意列表数据:', data)
    tableData.value = data.list || []
    total.value = data.total || 0
  } catch (error) {
    console.error('查询创意列表失败:', error)
    tableData.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

// 重置查询
const handleReset = () => {
  queryParams.keyword = ''
  queryParams.status = undefined
  queryParams.page = 1
  handleQuery()
}

// 添加创意
const handleAdd = () => {
  dialogTitle.value = '添加创意'
  dialogVisible.value = true
  Object.assign(formData, {
    id: undefined,
    advertiserId: undefined,
    name: '',
    description: '',
    landingPageUrl: '',
    displayUrl: '',
    advertiserDomain: '',
    cat: [],
    attr: [],
    language: undefined,
    startTime: undefined,
    endTime: undefined
  })
  formRef.value?.clearValidate()
}

// 编辑创意
const handleEdit = async (row: CreativeListItem) => {
  try {
    // 获取完整的创意详情
    const detail = await getCreativeById(row.id)
    dialogTitle.value = '编辑创意'
    dialogVisible.value = true
    Object.assign(formData, {
      id: detail.id,
      advertiserId: detail.advertiserId,
      name: detail.name,
      description: detail.description,
      landingPageUrl: detail.landingPageUrl,
      displayUrl: detail.displayUrl,
      advertiserDomain: detail.advertiserDomain,
      cat: detail.cat || [],
      attr: detail.attr || [],
      language: detail.language,
      startTime: detail.startTime,
      endTime: detail.endTime
    })
    formRef.value?.clearValidate()
  } catch (error) {
    console.error('获取创意详情失败:', error)
    ElMessage.error('获取创意详情失败')
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    submitLoading.value = true

    if (formData.id) {
      // 更新创意
      const updateData: CreativeUpdateRequest = {
        name: formData.name,
        description: formData.description,
        landingPageUrl: formData.landingPageUrl,
        displayUrl: formData.displayUrl,
        advertiserDomain: formData.advertiserDomain,
        cat: formData.cat,
        attr: formData.attr,
        language: formData.language,
        startTime: formData.startTime,
        endTime: formData.endTime
      }
      await updateCreative(formData.id, updateData)
      ElMessage.success('更新成功')
    } else {
      // 创建创意
      const createData: CreativeCreateRequest = {
        name: formData.name!,
        landingPageUrl: formData.landingPageUrl!,
        displayUrl: formData.displayUrl!,
        description: formData.description,
        advertiserDomain: formData.advertiserDomain,
        cat: formData.cat,
        attr: formData.attr,
        language: formData.language,
        startTime: formData.startTime,
        endTime: formData.endTime
      }
      await createCreative(createData)
      ElMessage.success('创建成功')
    }

    dialogVisible.value = false
    handleQuery()
  } catch (error: any) {
    if (error?.message) {
      ElMessage.error(error.message)
    }
  } finally {
    submitLoading.value = false
  }
}

// 删除创意
const handleDelete = async (row: CreativeListItem) => {
  try {
    await ElMessageBox.confirm(`确定要删除创意 "${row.name}" 吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    await deleteCreative(row.id)
    ElMessage.success('删除成功')
    handleQuery()
  } catch (error) {
    // 用户取消
  }
}

// 修改创意状态
const handleStatusChange = async (row: CreativeListItem) => {
  try {
    await updateCreativeStatus(row.id, row.status)
    ElMessage.success(row.status === 1 ? '已启用' : '已禁用')
    handleQuery()
  } catch (error) {
    row.status = row.status === 1 ? 0 : 1 // 恢复原状态
    ElMessage.error('状态修改失败')
  }
}

// 页面加载时查询数据
onMounted(() => {
  handleQuery()
})
</script>

<style scoped>
.creatives-view {
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
  gap: 10px;
}
</style>
