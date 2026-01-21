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
        <el-select
          v-model="queryParams.format"
          placeholder="素材格式"
          clearable
          style="width: 150px"
          @change="handleQuery"
        >
          <el-option label="全部" :value="undefined" />
          <el-option label="Banner" :value="1" />
          <el-option label="Video" :value="2" />
          <el-option label="Audio" :value="3" />
          <el-option label="Native" :value="4" />
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
        <el-table-column prop="name" label="素材名称" width="200" />
        <el-table-column prop="creativeName" label="所属创意" width="200" />
        <el-table-column prop="formatName" label="格式" width="120" />
        <el-table-column label="尺寸" width="150">
          <template #default="{ row }">
            {{ row.width }} × {{ row.height }}
          </template>
        </el-table-column>
        <el-table-column prop="fileUrl" label="预览" width="100">
          <template #default="{ row }">
            <el-image
              v-if="row.format === 1"
              style="width: 50px; height: 50px"
              :src="row.fileUrl"
              :preview-src-list="[row.fileUrl]"
              fit="cover"
            />
            <el-tag v-else type="info">视频/音频</el-tag>
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
        <el-form-item label="素材名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入素材名称" />
        </el-form-item>
        <el-form-item label="所属创意" prop="creativeId">
          <el-select v-model="formData.creativeId" placeholder="请选择创意" style="width: 100%">
            <el-option
              v-for="creative in creativeOptions"
              :key="creative.id"
              :label="creative.name"
              :value="creative.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="素材格式" prop="format">
          <el-select v-model="formData.format" placeholder="请选择素材格式" style="width: 100%">
            <el-option label="Banner" :value="1" />
            <el-option label="Video" :value="2" />
            <el-option label="Audio" :value="3" />
            <el-option label="Native" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="宽度" prop="width">
          <el-input-number v-model="formData.width" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="高度" prop="height">
          <el-input-number v-model="formData.height" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="文件" prop="fileId">
          <el-upload
            class="upload-demo"
            action="#"
            :auto-upload="false"
            :on-change="handleFileChange"
            :limit="1"
          >
            <el-button type="primary">选择文件</el-button>
            <template #tip>
              <div class="el-upload__tip">支持图片、视频、音频文件</div>
            </template>
          </el-upload>
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
  getMaterialPage,
  createMaterial,
  updateMaterial,
  deleteMaterial
} from '@/api/material'
import { getCreativePage } from '@/api/creative'
import type { MaterialListItem, MaterialCreateRequest } from '@/api/material'
import type { CreativeListItem } from '@/api/creative'

// 查询参数
const queryParams = reactive({
  current: 1,
  size: 10,
  format: undefined as number | undefined
})

// 表格数据
const tableData = ref<MaterialListItem[]>([])
const total = ref(0)
const loading = ref(false)

// 创意选项
const creativeOptions = ref<CreativeListItem[]>([])

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('')
const submitLoading = ref(false)

// 表单数据
const formData = reactive<Partial<MaterialCreateRequest> & { id?: number }>({
  id: undefined,
  name: '',
  creativeId: undefined,
  format: 1,
  width: undefined,
  height: undefined,
  fileId: ''
})

// 表单引用
const formRef = ref<FormInstance>()

// 表单验证规则
const formRules: FormRules = {
  name: [
    { required: true, message: '请输入素材名称', trigger: 'blur' }
  ],
  creativeId: [
    { required: true, message: '请选择创意', trigger: 'change' }
  ],
  format: [
    { required: true, message: '请选择素材格式', trigger: 'change' }
  ],
  width: [
    { required: true, message: '请输入宽度', trigger: 'blur' }
  ],
  height: [
    { required: true, message: '请输入高度', trigger: 'blur' }
  ],
  fileId: [
    { required: true, message: '请选择文件', trigger: 'change' }
  ]
}

// 查询素材列表
const handleQuery = async () => {
  loading.value = true
  try {
    const data = await getMaterialPage(queryParams)
    console.log('素材列表数据:', data)
    tableData.value = data.list || []
    total.value = data.total || 0
  } catch (error) {
    console.error('查询素材列表失败:', error)
    tableData.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

// 重置查询
const handleReset = () => {
  queryParams.format = undefined
  queryParams.current = 1
  handleQuery()
}

// 加载创意列表
const loadCreativeOptions = async () => {
  try {
    const data = await getCreativePage({ page: 1, size: 100 })
    creativeOptions.value = data.list || []
  } catch (error) {
    console.error('加载创意列表失败:', error)
  }
}

// 文件选择变化
const handleFileChange = (_file: any) => {
  // TODO: 实际项目中需要上传文件到服务器
  // 这里暂时用文件名作为fileId
  formData.fileId = 'file_' + Date.now()
  ElMessage.success('文件已选择')
}

// 添加素材
const handleAdd = () => {
  dialogTitle.value = '添加素材'
  dialogVisible.value = true
  Object.assign(formData, {
    id: undefined,
    name: '',
    creativeId: undefined,
    format: 1,
    width: undefined,
    height: undefined,
    fileId: ''
  })
  formRef.value?.clearValidate()
}

// 编辑素材
const handleEdit = (row: MaterialListItem) => {
  dialogTitle.value = '编辑素材'
  dialogVisible.value = true
  Object.assign(formData, {
    id: row.id,
    name: row.name,
    creativeId: row.creativeId,
    format: row.format,
    width: row.width,
    height: row.height,
    fileId: row.fileUrl
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
      await updateMaterial(formData.id, { name: formData.name })
      ElMessage.success('更新成功')
    } else {
      const createData: MaterialCreateRequest = {
        creativeId: formData.creativeId!,
        name: formData.name!,
        format: formData.format!,
        width: formData.width!,
        height: formData.height!,
        fileId: formData.fileId!
      }
      await createMaterial(createData)
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
    await ElMessageBox.confirm(`确定要删除素材 "${row.name}" 吗？`, '提示', {
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

// 页面加载时查询数据
onMounted(() => {
  handleQuery()
  loadCreativeOptions()
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
