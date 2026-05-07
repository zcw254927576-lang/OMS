<template>
  <el-card shadow="hover">
    <template #header>操作日志</template>
    <el-table :data="list" stripe v-loading="loading">
      <el-table-column prop="username" label="操作人" width="100" />
      <el-table-column prop="module" label="模块" width="100" />
      <el-table-column prop="operation" label="操作" width="200" show-overflow-tooltip />
      <el-table-column prop="requestUrl" label="请求URL" width="200" show-overflow-tooltip />
      <el-table-column prop="executionTime" label="耗时(ms)" width="90" align="center" />
      <el-table-column prop="ip" label="IP" width="140" />
      <el-table-column prop="status" label="状态" width="70" align="center">
        <template #default="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'" size="small">{{ scope.row.status === 1 ? '成功' : '失败' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="时间" width="170" />
    </el-table>
    <div style="margin-top: 16px; display: flex; justify-content: flex-end">
      <el-pagination v-model:current-page="page" v-model:page-size="pageSize" :total="total" layout="total, prev, pager, next" @change="fetchData" />
    </div>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getLogPage } from '../../api/system'

const loading = ref(false)
const list = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(20)

onMounted(() => fetchData())

async function fetchData() {
  loading.value = true
  try {
    const res = await getLogPage({ page: page.value, pageSize: pageSize.value })
    list.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (e) {} finally { loading.value = false }
}
</script>
