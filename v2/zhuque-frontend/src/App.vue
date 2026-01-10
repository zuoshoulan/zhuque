<script setup lang="ts">
import { onMounted } from 'vue'
import { RouterView } from 'vue-router'
import { useTheme } from '@/composables/useTheme'

const { initTheme } = useTheme()

// 初始化主题
onMounted(() => {
  initTheme()
})
</script>

<template>
  <RouterView />
</template>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB',
    'Microsoft YaHei', '微软雅黑', Arial, sans-serif;
}

#app {
  width: 100%;
  min-height: 100vh;
}

/* 暗色主题样式 */
html.dark body {
  background-color: #1a1a1a;
  color: #e0e0e0;
}

html.dark .el-card {
  background-color: #2a2a2a;
  border-color: #3a3a3a;
}

html.dark .el-table {
  background-color: #2a2a2a;
  color: #e0e0e0;
}

html.dark .el-table th,
html.dark .el-table tr {
  background-color: #2a2a2a;
}

html.dark .el-table td,
html.dark .el-table th.is-leaf {
  border-color: #3a3a3a;
}

html.dark .el-table--enable-row-hover .el-table__body tr:hover > td {
  background-color: #3a3a3a !important;
}

/* 描述列表(个人信息表格) */
html.dark .el-descriptions {
  --el-descriptions-table-border-color: #3a3a3a;
}

html.dark .el-descriptions__label {
  color: #e0e0e0;
}

html.dark .el-descriptions__content {
  color: #e0e0e0;
}

html.dark .el-descriptions__body {
  background-color: #2a2a2a;
  color: #e0e0e0;
}

html.dark .el-descriptions__cell {
  border-color: #3a3a3a;
}

html.dark .el-descriptions-item__label {
  color: #e0e0e0;
}

html.dark .el-input__wrapper {
  background-color: #2a2a2a;
  box-shadow: 0 0 0 1px #3a3a3a inset;
}

html.dark .el-input__wrapper:hover {
  box-shadow: 0 0 0 1px #4a4a4a inset;
}

html.dark .el-input__wrapper.is-focus {
  box-shadow: 0 0 0 1px #409eff inset;
}

html.dark .el-textarea__inner {
  background-color: #2a2a2a;
  border-color: #3a3a3a;
  color: #e0e0e0;
}

html.dark .el-dialog {
  background-color: #2a2a2a;
}

html.dark .el-drawer {
  background-color: #2a2a2a;
}

html.dark .layout-container .sidebar {
  background-color: #1a1a1a;
}

html.dark .layout-container .header {
  background-color: #2a2a2a;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.3);
}

html.dark .main-content {
  background: #0f0f0f;
}
</style>
