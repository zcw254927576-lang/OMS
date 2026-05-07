<template>
  <div>
    <el-card shadow="hover" style="margin-bottom: 20px">
      <el-form :inline="true">
        <el-form-item label="搜索">
          <el-input v-model="keyword" placeholder="采购单号/供应商" clearable @change="search" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="search">查询</el-button>
          <el-button type="success" @click="$router.push('/purchase/add')">新增采购单</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="hover">
      <el-table :data="list" stripe v-loading="loading">
        <el-table-column prop="orderNo" label="采购单号" width="200" />
        <el-table-column prop="supplierName" label="供应商" width="150" />
        <el-table-column prop="totalAmount" label="总金额" width="130" align="right">
          <template #default="scope">¥{{ scope.row.totalAmount || 0 }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="scope">
            <el-tag :type="statusTag(scope.row.status)" size="small">{{ statusText(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createBy" label="创建人" width="100" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="scope">
            <el-button text type="primary" @click="$router.push('/purchase/detail/' + scope.row.id)">详情</el-button>
            <el-button v-if="scope.row.status === 0" text type="primary" @click="handleSubmit(scope.row)">提交</el-button>
            <el-button v-if="scope.row.status === 1" text type="warning" @click="handleApprove(scope.row)">审核</el-button>
            <el-button v-if="scope.row.status === 2" text type="success" @click="handleWarehouse(scope.row)">入库</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div style="margin-top: 16px; display: flex; justify-content: flex-end">
        <el-pagination v-model:current-page="page" v-model:page-size="pageSize" :total="total" layout="total, prev, pager, next" @change="fetchData" />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getPurchasePage, submitPurchase, approvePurchase, warehousePurchase } from '../../api/purchase'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const list = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(20)
const keyword = ref('')

onMounted(() => fetchData())

async function fetchData() {
  loading.value = true
  try {
    const res = await getPurchasePage({ keyword: keyword.value, page: page.value, pageSize: pageSize.value })
    list.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (e) {} finally { loading.value = false }
}

function search() { page.value = 1; fetchData() }

function statusText(status) {
  const map = { 0: '草稿', 1: '已提交', 2: '已审核', 3: '已入库' }
  return map[status] || '未知'
}

function statusTag(status) {
  const map = { 0: 'info', 1: 'warning', 2: 'primary', 3: 'success' }
  return map[status] || ''
}

async function handleSubmit(row) {
  await ElMessageBox.confirm('确认提交采购单 ' + row.orderNo + ' 吗？')
  await submitPurchase(row.id)
  ElMessage.success('已提交')
  fetchData()
}

async function handleApprove(row) {
  await ElMessageBox.confirm('确认审核通过采购单 ' + row.orderNo + ' 吗？')
  await approvePurchase(row.id)
  ElMessage.success('已审核')
  fetchData()
}

async function handleWarehouse(row) {
  await ElMessageBox.confirm('确认入库采购单 ' + row.orderNo + ' 吗？入库后将自动更新库存。')
  await warehousePurchase(row.id)
  ElMessage.success('已入库，库存已更新')
  fetchData()
}
</script>
