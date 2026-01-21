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
        <el-form-item label="创意名称" prop="creativeName">
          <el-input v-model="formData.creativeName" placeholder="请输入创意名称" />
        </el-form-item>
        <el-form-item label="创意类型" prop="creativeType">
          <el-select v-model="formData.creativeType" placeholder="请选择创意类型" style="width: 100%">
            <el-option label="图片" value="image" />
            <el-option label="视频" value="video" />
            <el-option label="HTML" value="html" />
          </el-select>
        </el-form-item>
        <el-form-item label="素材ID" prop="materialId">
          <el-input-number v-model="formData.materialId" :min="1" style="width: 100%" />
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
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Refresh, Edit, Delete } from '@element-plus/icons-vue'
import {
  getCreativePage,
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
const formData = reactive<Partial<CreativeCreateRequest> & { id?: number; status?: number }>({
  id: undefined,
  creativeName: '',
  creativeType: '',
  materialId: undefined,
  status: 1
})

// 表单引用
const formRef = ref<FormInstance>()

// 表单验证规则
const formRules: FormRules = {
  creativeName: [
    { required: true, message: '请输入创意名称', trigger: 'blur' }
  ],
  creativeType: [
    { required: true, message: '请选择创意类型', trigger: 'change' }
  ],
  materialId: [
    { required: true, message: '请输入素材ID', trigger: 'blur' }
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
    creativeName: '',
    creativeType: '',
    materialId: undefined,
    status: 1
  })
  formRef.value?.clearValidate()
}

// 编辑创意
const handleEdit = (row: CreativeListItem) => {
  dialogTitle.value = '编辑创意'
  dialogVisible.value = true
  Object.assign(formData, {
    id: row.id,
    creativeName: row.name,
    creativeType: '',
    materialId: row.materialCount,
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
      await updateCreative(formData.id, formData)
      ElMessage.success('更新成功')
    } else {
      await createCreative(formData)
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
