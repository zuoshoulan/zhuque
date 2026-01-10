<template>
  <div class="permissions-view">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>权限管理</span>
          <el-button type="primary" :icon="Plus" @click="handleAdd">添加权限</el-button>
        </div>
      </template>

      <!-- 搜索栏 -->
      <div class="search-bar">
        <el-input
          v-model="queryParams.keyword"
          placeholder="搜索权限名称、编码"
          clearable
          style="width: 300px"
          @keyup.enter="handleQuery"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-button type="primary" :icon="Search" @click="handleQuery">搜索</el-button>
        <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        <el-button :icon="RefreshRight" @click="loadData">刷新</el-button>
      </div>

      <!-- 数据表格 -->
      <el-table
        v-loading="loading"
        :data="tableData"
        style="width: 100%; margin-top: 20px"
        border
        row-key="id"
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
        :default-expand-all="false"
      >
        <el-table-column prop="permissionName" label="权限名称" min-width="200" />
        <el-table-column prop="permissionCode" label="权限编码" min-width="200" />
        <el-table-column prop="permissionType" label="类型" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="PermissionTypeTagMap[row.permissionType]">
              {{ PermissionTypeMap[row.permissionType] }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="path" label="路径/方法" min-width="200">
          <template #default="{ row }">
            <span v-if="row.path">{{ row.path }}</span>
            <span v-if="row.method" class="method-tag">
              <el-tag size="small" type="info">{{ row.method }}</el-tag>
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="sortOrder" label="排序" width="80" align="center" />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="250" align="center" fixed="right">
          <template #default="{ row }">
            <el-button size="small" :icon="Edit" @click="handleEdit(row)">编辑</el-button>
            <el-button
              v-if="row.permissionType === PermissionType.ROUTE"
              size="small"
              type="primary"
              :icon="Plus"
              @click="handleAddChild(row)"
            >
              添加子权限
            </el-button>
            <el-button size="small" type="danger" :icon="Delete" @click="handleDelete(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
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
        <el-form-item label="父级权限" prop="parentId">
          <el-tree-select
            v-model="formData.parentId"
            :data="permissionTreeOptions"
            :props="{ label: 'permissionName', value: 'id' }"
            placeholder="选择父级权限（不选则为根权限）"
            clearable
            check-strictly
            :render-after-expand="false"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="权限名称" prop="permissionName">
          <el-input v-model="formData.permissionName" placeholder="请输入权限名称" />
        </el-form-item>
        <el-form-item label="权限编码" prop="permissionCode">
          <el-input
            v-model="formData.permissionCode"
            placeholder="如: advertiser:create"
            :disabled="isEdit"
          />
        </el-form-item>
        <el-form-item label="权限类型" prop="permissionType">
          <el-select v-model="formData.permissionType" placeholder="选择权限类型" style="width: 100%">
            <el-option :value="PermissionType.ROUTE" label="路由" />
            <el-option :value="PermissionType.BUTTON" label="按钮" />
            <el-option :value="PermissionType.API" label="接口" />
          </el-select>
        </el-form-item>
        <el-form-item label="路由路径" prop="path" v-if="formData.permissionType === PermissionType.ROUTE">
          <el-input v-model="formData.path" placeholder="如: /advertisers" />
        </el-form-item>
        <el-form-item
          label="接口路径"
          prop="path"
          v-if="formData.permissionType === PermissionType.API"
        >
          <el-input v-model="formData.path" placeholder="如: /api/advertisers" />
        </el-form-item>
        <el-form-item label="HTTP方法" prop="method" v-if="formData.permissionType === PermissionType.API">
          <el-select v-model="formData.method" placeholder="选择HTTP方法" style="width: 100%">
            <el-option value="GET" label="GET" />
            <el-option value="POST" label="POST" />
            <el-option value="PUT" label="PUT" />
            <el-option value="DELETE" label="DELETE" />
            <el-option value="PATCH" label="PATCH" />
          </el-select>
        </el-form-item>
        <el-form-item label="图标" prop="icon" v-if="formData.permissionType === PermissionType.ROUTE">
          <el-input v-model="formData.icon" placeholder="如: User" />
        </el-form-item>
        <el-form-item label="排序号" prop="sortOrder">
          <el-input-number v-model="formData.sortOrder" :min="0" :max="9999" />
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
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import {
  Plus,
  Search,
  Refresh,
  Edit,
  Delete,
  RefreshRight
} from '@element-plus/icons-vue'
import {
  getPermissionTree,
  createPermission,
  updatePermission,
  deletePermission,
  type PermissionInfo,
  type CreatePermissionRequest,
  PermissionType,
  PermissionTypeMap,
  PermissionTypeTagMap
} from '@/api/permission'

// 查询参数
const queryParams = reactive({
  keyword: ''
})

// 表格数据
const tableData = ref<PermissionInfo[]>([])
const loading = ref(false)

// 权限树选项（用于父级权限选择）
const permissionTreeOptions = ref<PermissionInfo[]>([])

// 对话框
const dialogVisible = ref(false)
const dialogTitle = computed(() => (isEdit.value ? '编辑权限' : '添加权限'))
const isEdit = ref(false)
const submitLoading = ref(false)

// 表单
const formRef = ref<FormInstance>()
const formData = reactive<CreatePermissionRequest>({
  parentId: 0,
  permissionCode: '',
  permissionName: '',
  permissionType: PermissionType.BUTTON,
  path: '',
  method: undefined,
  icon: '',
  sortOrder: 0
})

// 表单验证规则
const formRules: FormRules<CreatePermissionRequest> = {
  permissionName: [{ required: true, message: '请输入权限名称', trigger: 'blur' }],
  permissionCode: [
    { required: true, message: '请输入权限编码', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9:_-]+$/, message: '权限编码只能包含字母、数字、冒号、下划线和横线', trigger: 'blur' }
  ],
  permissionType: [{ required: true, message: '请选择权限类型', trigger: 'change' }]
}

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    const data = await getPermissionTree()
    tableData.value = data
    // 构建父级权限选项树
    permissionTreeOptions.value = buildParentOptions(data)
  } catch (error: any) {
    ElMessage.error(error.message || '加载数据失败')
  } finally {
    loading.value = false
  }
}

// 构建父级权限选项树
const buildParentOptions = (data: PermissionInfo[]): PermissionInfo[] => {
  return data.map((item) => ({
    ...item,
    children: item.children ? buildParentOptions(item.children) : undefined
  }))
}

// 搜索
const handleQuery = () => {
  if (!queryParams.keyword) {
    loadData()
    return
  }

  const keyword = queryParams.keyword.toLowerCase()
  const filterTree = (data: PermissionInfo[]): PermissionInfo[] => {
    return data
      .filter((item) => {
        const matchSelf =
          item.permissionName.toLowerCase().includes(keyword) ||
          item.permissionCode.toLowerCase().includes(keyword)
        const matchChildren = item.children ? filterTree(item.children) : []
        return matchSelf || matchChildren.length > 0
      })
      .map((item) => ({
        ...item,
        children: item.children ? filterTree(item.children) : undefined
      }))
  }

  const filtered = filterTree(tableData.value)
  tableData.value = filtered
}

// 重置
const handleReset = () => {
  queryParams.keyword = ''
  loadData()
}

// 添加
const handleAdd = () => {
  isEdit.value = false
  currentEditId.value = 0
  Object.assign(formData, {
    parentId: 0,
    permissionCode: '',
    permissionName: '',
    permissionType: PermissionType.BUTTON,
    path: '',
    method: undefined,
    icon: '',
    sortOrder: 0
  })
  dialogVisible.value = true
  formRef.value?.clearValidate()
}

// 添加子权限
const handleAddChild = (row: PermissionInfo) => {
  isEdit.value = false
  currentEditId.value = 0
  Object.assign(formData, {
    parentId: row.id,
    permissionCode: '',
    permissionName: '',
    permissionType: PermissionType.BUTTON,
    path: '',
    method: undefined,
    icon: '',
    sortOrder: 0
  })
  dialogVisible.value = true
  formRef.value?.clearValidate()
}

// 编辑
const handleEdit = (row: PermissionInfo) => {
  isEdit.value = true
  currentEditId.value = row.id
  Object.assign(formData, {
    parentId: row.parentId,
    permissionCode: row.permissionCode,
    permissionName: row.permissionName,
    permissionType: row.permissionType,
    path: row.path || '',
    method: row.method,
    icon: row.icon || '',
    sortOrder: row.sortOrder || 0
  })
  dialogVisible.value = true
  formRef.value?.clearValidate()
}

// 删除
const handleDelete = (row: PermissionInfo) => {
  // 检查是否有子权限
  if (row.children && row.children.length > 0) {
    ElMessage.warning('该权限下还有子权限，无法删除')
    return
  }

  ElMessageBox.confirm(`确定要删除权限"${row.permissionName}"吗？`, '提示', {
    type: 'warning'
  })
    .then(async () => {
      try {
        await deletePermission(row.id)
        ElMessage.success('删除成功')
        loadData()
      } catch (error: any) {
        ElMessage.error(error.message || '删除失败')
      }
    })
    .catch(() => {
      // 用户取消
    })
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (!valid) return

    submitLoading.value = true
    try {
      if (isEdit.value) {
        await updatePermission(currentEditId.value, formData)
        ElMessage.success('更新成功')
      } else {
        await createPermission(formData)
        ElMessage.success('创建成功')
      }
      dialogVisible.value = false
      loadData()
    } catch (error: any) {
      ElMessage.error(error.message || '操作失败')
    } finally {
      submitLoading.value = false
    }
  })
}

// 当前编辑的ID
const currentEditId = ref<number>(0)

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.permissions-view {
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

.method-tag {
  margin-left: 8px;
}
</style>
