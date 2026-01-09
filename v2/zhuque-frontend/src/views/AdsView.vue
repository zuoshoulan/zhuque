<template>
  <div class="ads-view">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>广告管理</span>
          <el-button type="primary">创建广告</el-button>
        </div>
      </template>

      <el-table :data="[]" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="广告名称" />
        <el-table-column prop="type" label="类型" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default>
            <el-tag type="success">启用</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" />
        <el-table-column label="操作" width="200">
          <template #default>
            <el-button size="small">编辑</el-button>
            <el-button size="small" type="danger">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-empty description="暂无数据" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
// 广告管理页面
</script>

<style scoped>
.ads-view {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
