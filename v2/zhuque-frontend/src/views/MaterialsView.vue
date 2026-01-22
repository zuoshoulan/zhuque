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
        <!-- 基础字段 -->
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
        <el-form-item label="宽度(像素)" prop="width">
          <el-input-number v-model="formData.width" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="高度(像素)" prop="height">
          <el-input-number v-model="formData.height" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="文件" prop="fileId" required>
          <el-upload
            class="upload-demo"
            action="#"
            :auto-upload="false"
            :on-change="handleFileChange"
            :limit="1"
            :file-list="fileList"
          >
            <el-button type="primary" :loading="uploading">选择文件</el-button>
            <template #tip>
              <div class="el-upload__tip">
                <span v-if="formData.format === 1">{{ getBannerTypeTip() }}</span>
                <span v-else>支持图片、视频、音频文件</span>
              </div>
            </template>
          </el-upload>

          <!-- 文件预览 -->
          <div v-if="formData.fileId && formData.fileId > 0" class="file-preview">
            <div v-if="formData.format === 1" class="image-preview">
              <el-image
                :src="getFilePreviewUrl()"
                :preview-src-list="[getFilePreviewUrl()]"
                fit="contain"
                style="max-width: 200px; max-height: 200px"
              />
            </div>
            <div v-else-if="formData.format === 2" class="video-preview">
              <video
                :src="getFilePreviewUrl()"
                controls
                style="max-width: 200px; max-height: 200px"
              />
            </div>
            <div v-else class="file-info">
              <el-tag type="info">{{ formData.format === 3 ? '音频文件' : 'Native广告' }}</el-tag>
            </div>
          </div>
        </el-form-item>

        <!-- Banner扩展字段 -->
        <template v-if="formData.format === 1">
          <el-divider content-position="left">Banner扩展属性</el-divider>
          <el-form-item label="广告位置">
            <el-radio-group v-model="formData.bannerExt!.pos">
              <el-radio :value="1">首屏</el-radio>
              <el-radio :value="2">次屏</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="横幅类型">
            <el-radio-group v-model="formData.bannerExt!.btype">
              <el-radio :value="2">静态图片</el-radio>
              <el-radio :value="7">含视频的Banner</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="窗口模式">
            <el-radio-group v-model="formData.bannerExt!.wmode">
              <el-radio :value="1">正常</el-radio>
              <el-radio :value="2">全屏</el-radio>
            </el-radio-group>
          </el-form-item>
        </template>

        <!-- Video扩展字段 -->
        <template v-if="formData.format === 2">
          <el-divider content-position="left">Video扩展属性</el-divider>
          <el-form-item label="播放方式">
            <el-radio-group v-model="formData.videoExt!.linearity">
              <el-radio :value="1">线性播放</el-radio>
              <el-radio :value="2">非线性播放</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="开始延迟">
            <el-select v-model="formData.videoExt!.startdelay" placeholder="请选择">
              <el-option label="前贴片" :value="0" />
              <el-option label="中贴片" :value="-1" />
              <el-option label="后贴片5秒" :value="5" />
              <el-option label="后贴片10秒" :value="10" />
            </el-select>
          </el-form-item>
          <el-form-item label="跳过按钮">
            <el-switch
              v-model="videoSkipEnabled"
              active-text="可跳过"
              inactive-text="不可跳过"
            />
          </el-form-item>
          <el-form-item v-if="videoSkipEnabled" label="跳过等待时间">
            <el-input-number v-model="formData.videoExt!.skipmin" :min="0" placeholder="最少播放秒数" />
            <span style="margin: 0 10px">秒后可跳过</span>
          </el-form-item>
        </template>

        <!-- Audio扩展字段 -->
        <template v-if="formData.format === 3">
          <el-divider content-position="left">Audio扩展属性</el-divider>
          <el-form-item label="音频时长">
            <el-input-number v-model="formData.audioExt!.minDuration" :min="0" placeholder="最小时长(秒)" />
            <span style="margin: 0 10px">-</span>
            <el-input-number v-model="formData.audioExt!.maxDuration" :min="0" placeholder="最大时长(秒)" />
          </el-form-item>
        </template>

        <!-- Native扩展字段 -->
        <template v-if="formData.format === 4">
          <el-divider content-position="left">Native扩展属性</el-divider>
          <el-form-item label="请求JSON">
            <el-input
              v-model="formData.nativeExt!.requestJson"
              type="textarea"
              :rows="4"
              placeholder="请输入原生广告请求JSON"
            />
          </el-form-item>
          <el-form-item label="API版本">
            <el-input v-model="formData.nativeExt!.ver" placeholder="请输入API版本" />
          </el-form-item>
        </template>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, watch } from 'vue'
import type { FormInstance, FormRules, UploadUserFile, UploadFile } from 'element-plus'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Refresh, Edit, Delete } from '@element-plus/icons-vue'
import {
  getMaterialPage,
  getMaterialById,
  createMaterial,
  updateMaterial,
  deleteMaterial
} from '@/api/material'
import { uploadFile, type FileUploadResponse } from '@/api/file'
import { getCreativePage } from '@/api/creative'
import type {
  MaterialListItem,
  MaterialCreateRequest,
  MaterialUpdateRequest,
  BannerExt,
  VideoExt,
  AudioExt,
  NativeExt
} from '@/api/material'
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

// 文件列表
const fileList = ref<UploadUserFile[]>([])
const uploading = ref(false)

// Video跳过按钮开关
const videoSkipEnabled = ref(false)

// 表单数据
const formData = reactive<Partial<MaterialCreateRequest> & { id?: number }>({
  id: undefined,
  name: '',
  creativeId: undefined,
  format: 1,
  width: undefined,
  height: undefined,
  fileId: 0,
  mimes: undefined,
  dur: undefined,
  bannerExt: {} as BannerExt,
  videoExt: {} as VideoExt,
  audioExt: {} as AudioExt,
  nativeExt: {} as NativeExt
})

// 监听format变化，重置扩展字段
watch(() => formData.format, () => {
  formData.bannerExt = {} as BannerExt
  formData.videoExt = {} as VideoExt
  formData.audioExt = {} as AudioExt
  formData.nativeExt = {} as NativeExt
  videoSkipEnabled.value = false
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
const handleFileChange = async (file: UploadFile) => {
  fileList.value = [file]

  try {
    uploading.value = true
    ElMessage.info('文件上传中...')

    // 调用文件上传接口
    const fileData = await uploadFile(file.raw as File)

    // 响应拦截器已经返回了 data，fileData 直接就是 FileUploadResponse
    if (fileData && fileData.fileId) {
      formData.fileId = fileData.fileId

      // 如果是图片，自动填充宽度和高度
      if (fileData.width && fileData.height) {
        formData.width = fileData.width
        formData.height = fileData.height
        ElMessage.success(`文件上传成功，自动识别尺寸: ${fileData.width}x${fileData.height}`)
      } else {
        ElMessage.success('文件上传成功')
      }
    } else {
      ElMessage.error('文件上传失败')
    }
  } catch (error: any) {
    console.error('文件上传失败:', error)
    ElMessage.error(error.message || '文件上传失败')
  } finally {
    uploading.value = false
  }
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
    fileId: 0,
    mimes: undefined,
    dur: undefined,
    bannerExt: {} as BannerExt,
    videoExt: {} as VideoExt,
    audioExt: {} as AudioExt,
    nativeExt: {} as NativeExt
  })
  fileList.value = []
  videoSkipEnabled.value = false
  formRef.value?.clearValidate()
}

// 编辑素材
const handleEdit = async (row: MaterialListItem) => {
  try {
    // 调用详情接口获取完整数据
    const detail = await getMaterialById(row.id)

    dialogTitle.value = '编辑素材'
    dialogVisible.value = true

    // 基础字段
    Object.assign(formData, {
      id: detail.id,
      name: detail.name,
      creativeId: detail.creativeId,
      format: detail.format,
      width: detail.width,
      height: detail.height,
      fileId: detail.fileUrl ? Number(detail.fileUrl.split('/').pop()) : 0,
      mimes: detail.mimes,
      dur: detail.dur,
      bannerExt: {} as BannerExt,
      videoExt: {} as VideoExt,
      audioExt: {} as AudioExt,
      nativeExt: {} as NativeExt
    })

    // 扩展字段
    if (detail.format === 1 && detail.bannerExt) {
      formData.bannerExt = {
        pos: detail.bannerExt.pos as number,
        btype: detail.bannerExt.btype as number || 2,
        wmode: detail.bannerExt.wmode as number,
        ext: detail.bannerExt.ext as string
      }
    } else if (detail.format === 2 && detail.videoExt) {
      formData.videoExt = {
        linearity: detail.videoExt.linearity as number,
        sequence: detail.videoExt.sequence as number,
        minDuration: detail.videoExt.minDuration as number,
        maxDuration: detail.videoExt.maxDuration as number,
        startdelay: detail.videoExt.startdelay as number,
        skip: detail.videoExt.skip as number,
        skipmin: detail.videoExt.skipmin as number,
        skipafter: detail.videoExt.skipafter as number,
        placement: detail.videoExt.placement as number,
        playbackend: detail.videoExt.playbackend as number,
        playableafter: detail.videoExt.playableafter as number,
        podid: detail.videoExt.podid as string,
        podsize: detail.videoExt.podsize as number,
        podseq: detail.videoExt.podseq as number,
        mincpmpersec: detail.videoExt.mincpmpersec as number,
        maxseq: detail.videoExt.maxseq as number,
        render: detail.videoExt.render as number,
        api: detail.videoExt.api as number[] || [],
        ext: detail.videoExt.ext as string
      }
      // 设置skip开关
      videoSkipEnabled.value = detail.videoExt.skip === 1
    } else if (detail.format === 3 && detail.audioExt) {
      formData.audioExt = {
        sequence: detail.audioExt.sequence as number,
        minDuration: detail.audioExt.minDuration as number,
        maxDuration: detail.audioExt.maxDuration as number,
        startdelay: detail.audioExt.startdelay as number,
        api: detail.audioExt.api as number[] || [],
        ext: detail.audioExt.ext as string
      }
    } else if (detail.format === 4 && detail.nativeExt) {
      formData.nativeExt = {
        requestJson: detail.nativeExt.requestJson as string,
        ver: detail.nativeExt.ver as string,
        ext: detail.nativeExt.ext as string
      }
    }

    fileList.value = []
    formRef.value?.clearValidate()
  } catch (error) {
    console.error('获取素材详情失败:', error)
    ElMessage.error('获取素材详情失败')
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    submitLoading.value = true

    // 构建创建/更新数据
    const submitData: MaterialCreateRequest | MaterialUpdateRequest = {
      creativeId: formData.creativeId!,
      name: formData.name!,
      format: formData.format!,
      width: formData.width!,
      height: formData.height!,
      fileId: formData.fileId!,
      mimes: formData.mimes,
      dur: formData.dur
    }

    // 根据format添加对应的扩展字段
    if (formData.format === 1 && formData.bannerExt) {
      submitData.bannerExt = { ...formData.bannerExt }
    } else if (formData.format === 2 && formData.videoExt) {
      submitData.videoExt = { ...formData.videoExt }
      if (!videoSkipEnabled.value) {
        submitData.videoExt.skip = 0
      }
    } else if (formData.format === 3 && formData.audioExt) {
      submitData.audioExt = { ...formData.audioExt }
    } else if (formData.format === 4 && formData.nativeExt) {
      submitData.nativeExt = { ...formData.nativeExt }
    }

    if (formData.id) {
      // 更新
      const updateData: MaterialUpdateRequest = {
        id: formData.id,
        ...submitData
      }
      await updateMaterial(formData.id, updateData)
      ElMessage.success('更新成功')
    } else {
      // 创建
      await createMaterial(submitData as MaterialCreateRequest)
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

// 获取文件预览URL
const getFilePreviewUrl = () => {
  if (formData.fileId && formData.fileId > 0) {
    return `/api/file/by-id/${formData.fileId}`
  }
  return ''
}

// 获取Banner类型提示
const getBannerTypeTip = () => {
  const btype = formData.bannerExt?.btype
  switch (btype) {
    case 2:
      return '建议上传：JPG、PNG、GIF 等图片文件'
    case 7:
      return '建议上传：MP4、HTML5 视频文件或含视频的Banner'
    default:
      return '请先选择横幅类型'
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

.upload-demo {
  width: 100%;
}

.file-preview {
  margin-top: 10px;
  padding: 10px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  background-color: #fafafa;
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 50px;
}

.image-preview,
.video-preview {
  display: flex;
  justify-content: center;
  align-items: center;
}

.file-info {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
}
</style>
