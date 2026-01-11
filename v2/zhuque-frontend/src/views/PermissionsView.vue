<template>
  <div class="permissions-view">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>权限管理</span>
          <el-button type="primary" :icon="Plus" @click="handleAdd">添加权限</el-button>
        </div>
      </template>

      <!-- 工具栏 -->
      <div class="toolbar">
        <el-select
          v-model="queryParams.permissionType"
          placeholder="权限类型"
          clearable
          style="width: 150px"
          @change="handleQuery"
        >
          <el-option label="全部" :value="undefined" />
          <el-option label="路由" :value="1" />
          <el-option label="按钮" :value="2" />
          <el-option label="接口" :value="3" />
        </el-select>
        <el-input
          v-model="queryParams.keyword"
          placeholder="搜索权限名称或编码"
          clearable
          style="width: 300px; margin-left: 10px"
          @keyup.enter="handleQuery"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-button type="primary" :icon="Search" @click="handleQuery">搜索</el-button>
        <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        <el-button :icon="RefreshRight" @click="handleRefresh">刷新</el-button>
      </div>

      <!-- 数据表格 -->
      <el-table
        v-loading="loading"
        :data="tableData"
        style="width: 100%; margin-top: 20px"
        row-key="id"
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
        border
        default-expand-all
      >
        <el-table-column prop="permissionName" label="权限名称" min-width="200" />
        <el-table-column prop="permissionCode" label="权限编码" min-width="200" />
        <el-table-column prop="permissionType" label="权限类型" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="PermissionTypeTagMap[row.permissionType]">
              {{ PermissionTypeMap[row.permissionType] }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="path" label="路径" min-width="200" show-overflow-tooltip />
        <el-table-column prop="method" label="方法" width="80" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.method" type="info">{{ row.method }}</el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="sortOrder" label="排序" width="80" align="center" />
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
        <el-table-column label="操作" width="250" align="center" fixed="right">
          <template #default="{ row }">
            <el-button size="small" :icon="Edit" @click="handleEdit(row)">编辑</el-button>
            <el-button
              v-if="!row.children || row.children.length === 0"
              size="small"
              type="danger"
              :icon="Delete"
              @click="handleDelete(row)"
            >
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
        label-width="120px"
      >
        <el-form-item label="上级权限" prop="parentId">
          <el-tree-select
            v-model="formData.parentId"
            :data="permissionTreeOptions"
            :props="{ label: 'permissionName', value: 'id' }"
            placeholder="选择上级权限（不选则为顶级）"
            clearable
            check-strictly
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="权限名称" prop="permissionName">
          <el-input v-model="formData.permissionName" placeholder="请输入权限名称" />
        </el-form-item>

        <el-form-item label="权限编码" prop="permissionCode">
          <el-input
            v-model="formData.permissionCode"
            placeholder="如：advertiser:create"
            :disabled="isEdit"
          />
        </el-form-item>

        <el-form-item label="权限类型" prop="permissionType">
          <el-radio-group v-model="formData.permissionType">
            <el-radio :label="1">路由</el-radio>
            <el-radio :label="2">按钮</el-radio>
            <el-radio :label="3">接口</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="请求路径" prop="path">
          <el-input v-model="formData.path" placeholder="如：/api/advertisers 或 /advertisers" />
        </el-form-item>

        <el-form-item label="请求方法" prop="method">
          <el-select v-model="formData.method" placeholder="请选择请求方法" clearable>
            <el-option label="GET" value="GET" />
            <el-option label="POST" value="POST" />
            <el-option label="PUT" value="PUT" />
            <el-option label="DELETE" value="DELETE" />
            <el-option label="PATCH" value="PATCH" />
          </el-select>
        </el-form-item>

        <el-form-item label="图标" prop="icon">
          <el-input v-model="formData.icon" placeholder="图标名称（可选）" />
        </el-form-item>

        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="formData.sortOrder" :min="0" :max="9999" style="width: 100%" />
        </el-form-item>

        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox, FormInstance, FormRules } from 'element-plus'
import { Plus, Edit, Delete, Search, Refresh, RefreshRight } from '@element-plus/icons-vue'
import {
  getPermissionTree,
  getPermissionList,
  createPermission,
  updatePermission,
  deletePermission,
  updatePermissionStatus,
  type PermissionInfo,
  type CreatePermissionRequest,
  PermissionTypeMap,
  PermissionTypeTagMap
} from '@/api/permission'

// 查询参数
const queryParams = reactive({
  keyword: '',
  permissionType: undefined as number | undefined
})

// 表格数据
const tableData = ref<PermissionInfo[]>([])
const loading = ref(false)

// 对话框相关
const dialogVisible = ref(false)
const dialogTitle = computed(() => (isEdit.value ? '编辑权限' : '添加权限'))
const submitLoading = ref(false)
const isEdit = ref(false)
const currentId = ref<number>()

// 表单数据
const formData = reactive<CreatePermissionRequest>({
  parentId: 0,
  permissionCode: '',
  permissionName: '',
  permissionType: 1,
  path: '',
  method: undefined,
  icon: '',
  sortOrder: 0,
  status: 1
})

const formRef = ref<FormInstance>()

// 表单验证规则
const formRules: FormRules = {
  permissionName: [{ required: true, message: '请输入权限名称', trigger: 'blur' }],
  permissionCode: [{ required: true, message: '请输入权限编码', trigger: 'blur' }],
  permissionType: [{ required: true, message: '请选择权限类型', trigger: 'change' }]
}

// 权限树选项（用于上级权限选择）
const permissionTreeOptions = ref<PermissionInfo[]>([])

// 查询权限树
const handleQuery = async () => {
  try {
    loading.value = true
    const data = await getPermissionTree()

    // 前端过滤
    if (queryParams.keyword || queryParams.permissionType !== undefined) {
      tableData.value = filterPermissions(data)
    } else {
      tableData.value = data
    }
  } catch (error) {
    console.error('查询权限失败:', error)
  } finally {
    loading.value = false
  }
}

// 前端过滤权限
const filterPermissions = (permissions: PermissionInfo[]): PermissionInfo[] => {
  return permissions
    .filter((permission) => {
      // 关键词过滤
      if (queryParams.keyword) {
        const keyword = queryParams.keyword.toLowerCase()
        const matchName = permission.permissionName.toLowerCase().includes(keyword)
        const matchCode = permission.permissionCode.toLowerCase().includes(keyword)
        if (!matchName && !matchCode) return false
      }

      // 类型过滤
      if (queryParams.permissionType !== undefined && permission.permissionType !== queryParams.permissionType) {
        return false
      }

      return true
    })
    .map((permission) => {
      // 递归处理子权限
      if (permission.children && permission.children.length > 0) {
        const filteredChildren = filterPermissions(permission.children)
        return { ...permission, children: filteredChildren }
      }
      return permission
    })
    .filter((permission) => {
      // 如果没有子节点，或者过滤后有子节点，则保留
      return !permission.children || permission.children.length === 0 || permission.children.length > 0
    })
}

// 重置查询
const handleReset = () => {
  queryParams.keyword = ''
  queryParams.permissionType = undefined
  handleQuery()
}

// 刷新
const handleRefresh = () => {
  handleQuery()
}

// 添加权限
const handleAdd = () => {
  isEdit.value = false
  currentId.value = undefined
  Object.assign(formData, {
    parentId: 0,
    permissionCode: '',
    permissionName: '',
    permissionType: 1,
    path: '',
    method: undefined,
    icon: '',
    sortOrder: 0,
    status: 1
  })
  dialogVisible.value = true
}

// 编辑权限
const handleEdit = (row: PermissionInfo) => {
  isEdit.value = true
  currentId.value = row.id
  Object.assign(formData, {
    parentId: row.parentId,
    permissionCode: row.permissionCode,
    permissionName: row.permissionName,
    permissionType: row.permissionType,
    path: row.path || '',
    method: row.method,
    icon: row.icon || '',
    sortOrder: row.sortOrder || 0,
    status: row.status
  })
  dialogVisible.value = true
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (!valid) return

    try {
      submitLoading.value = true

      if (isEdit.value) {
        await updatePermission(currentId.value!, formData)
        ElMessage.success('更新成功')
      } else {
        await createPermission(formData)
        ElMessage.success('创建成功')
      }

      dialogVisible.value = false
      handleQuery()
    } catch (error) {
      // 错误消息已在 request.ts 的响应拦截器中显示
      console.error('提交失败:', error)
    } finally {
      submitLoading.value = false
    }
  })
}

// 删除权限
const handleDelete = async (row: PermissionInfo) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除权限 "${row.permissionName}" 吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    await deletePermission(row.id)
    ElMessage.success('删除成功')
    handleQuery()
  } catch (error) {
    // 用户取消或请求失败
  }
}

// 修改状态
const handleStatusChange = async (row: PermissionInfo) => {
  try {
    await updatePermissionStatus(row.id, row.status)
    ElMessage.success('状态更新成功')
  } catch (error) {
    // 更新失败，恢复原状态
    row.status = row.status === 1 ? 0 : 1
    console.error('状态更新失败:', error)
  }
}

// 加载权限树选项
const loadPermissionTreeOptions = async () => {
  try {
    const data = await getPermissionList()
    // 添加根节点选项
    permissionTreeOptions.value = [
      {
        id: 0,
        parentId: 0,
        permissionName: '顶级权限',
        permissionCode: '',
        permissionType: 1,
        status: 1,
        createTime: '',
        children: data
      }
    ]
  } catch (error) {
    console.error('加载权限树失败:', error)
  }
}

// 页面加载时查询数据
onMounted(() => {
  handleQuery()
  loadPermissionTreeOptions()
})
</script>

<style scoped>
.permissions-view {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.toolbar {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}
</style>
