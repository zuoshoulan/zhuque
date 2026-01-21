<template>
  <div class="materials-view">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>素材管理</span>
          <el-button type="primary" :icon="Plus" @click="handleAdd">添加素材</el-button>
        </div>
      </template>

      <!-- 搜索栏 -->
      <div class="search-bar">
        <el-input
          v-model="queryParams.keyword"
          placeholder="搜索素材名称"
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
          placeholder="素材状态"
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
        <el-table-column prop="materialName" label="素材名称" width="200" />
        <el-table-column prop="materialType" label="素材类型" width="120" />
        <el-table-column prop="width" label="宽度" width="100" />
        <el-table-column prop="height" label="高度" width="100" />
        <el-table-column prop="fileSize" label="文件大小" width="120">
          <template #default="{ row }">
            {{ formatFileSize(row.fileSize) }}
          </template>
        </el-table-column>
        <el-table-column prop="fileUrl" label="文件URL" min-width="200" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100" align="center">
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
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
        @submit.prevent="handleSubmit"
      >
        <el-form-item label="素材名称" prop="materialName">
          <el-input v-model="formData.materialName" placeholder="请输入素材名称" />
        </el-form-item>
        <el-form-item label="素材类型" prop="materialType">
          <el-select v-model="formData.materialType" placeholder="请选择素材类型" style="width: 100%">
            <el-option label="图片" value="image" />
            <el-option label="视频" value="video" />
            <el-option label="HTML" value="html" />
          </el-select>
        </el-form-item>
        <el-form-item label="文件URL" prop="fileUrl">
          <el-input v-model="formData.fileUrl" placeholder="请输入文件URL" />
        </el-form-item>
        <el-form-item label="宽度" prop="width">
          <el-input-number v-model="formData.width" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="高度" prop="height">
          <el-input-number v-model="formData.height" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item v-if="!formData.id" label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
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
import { ElMessage, ElMessageBox, FormInstance, FormRules } from 'element-plus'
import { Plus, Search, Refresh, Edit, Delete } from '@element-plus/icons-vue'
import {
  getMaterialPage,
  createMaterial,
  updateMaterial,
  deleteMaterial,
  updateMaterialStatus
} from '@/api/material'
import type { MaterialListItem, MaterialCreateRequest, MaterialUpdateRequest } from '@/api/material'

// 查询参数
const queryParams = reactive({
  page: 1,
  size: 10,
  keyword: '',
  status: undefined as number | undefined
})

// 表格数据
const tableData = ref<MaterialListItem[]>([])
const total = ref(0)
const loading = ref(false)

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('')
const submitLoading = ref(false)

// 表单数据
const formData = reactive<Partial<MaterialCreateRequest> & { id?: number; status?: number }>({
  id: undefined,
  materialName: '',
  materialType: '',
  fileUrl: '',
  width: undefined,
  height: undefined,
  status: 1
})

// 表单引用
const formRef = ref<FormInstance>()

// 表单验证规则
const formRules: FormRules = {
  materialName: [
    { required: true, message: '请输入素材名称', trigger: 'blur' }
  ],
  materialType: [
    { required: true, message: '请选择素材类型', trigger: 'change' }
  ],
  fileUrl: [
    { required: true, message: '请输入文件URL', trigger: 'blur' }
  ]
}

// 格式化文件大小
const formatFileSize = (bytes: number): string => {
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return Math.round((bytes / Math.pow(k, i)) * 100) / 100 + ' ' + sizes[i]
}

// 查询素材列表
const handleQuery = async () => {
  loading.value = true
  try {
    const data = await getMaterialPage(queryParams)
    tableData.value = data.list
    total.value = data.total
  } catch (error) {
    console.error('查询素材列表失败:', error)
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

// 添加素材
const handleAdd = () => {
  dialogTitle.value = '添加素材'
  dialogVisible.value = true
  Object.assign(formData, {
    id: undefined,
    materialName: '',
    materialType: '',
    fileUrl: '',
    width: undefined,
    height: undefined,
    status: 1
  })
  formRef.value?.clearValidate()
}

// 编辑素材
const handleEdit = (row: MaterialListItem) => {
  dialogTitle.value = '编辑素材'
  dialogVisible.value = true
  Object.assign(formData, {
    id: row.id,
    materialName: row.materialName,
    materialType: row.materialType,
    fileUrl: row.fileUrl,
    width: row.width,
    height: row.height,
    status: row.status
  })
  formRef.value?.clearValidate()
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    submitLoading.value = true

    if (formData.id) {
      await updateMaterial(formData.id, formData)
      ElMessage.success('更新成功')
    } else {
      await createMaterial(formData)
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

// 删除素材
const handleDelete = async (row: MaterialListItem) => {
  try {
    await ElMessageBox.confirm(`确定要删除素材 "${row.materialName}" 吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    await deleteMaterial(row.id)
    ElMessage.success('删除成功')
    handleQuery()
  } catch (error) {
    // 用户取消
  }
}

// 修改素材状态
const handleStatusChange = async (row: MaterialListItem) => {
  try {
    await updateMaterialStatus(row.id, row.status)
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
.materials-view {
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
