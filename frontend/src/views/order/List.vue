<template>
  <div>
    <!-- 搜索区 -->
    <el-card shadow="hover" style="margin-bottom: 20px">
      <el-form :inline="true" :model="queryForm" size="default">
        <el-form-item label="订单号">
          <el-input v-model="queryForm.orderNo" placeholder="订单号" clearable />
        </el-form-item>
        <el-form-item label="客户">
          <el-input v-model="queryForm.customerName" placeholder="客户名称" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.orderStatus" placeholder="全部" clearable style="width: 130px">
            <el-option v-for="(label, key) in statusMap" :key="key" :label="label" :value="key" />
          </el-select>
        </el-form-item>
        <el-form-item label="时间">
          <el-date-picker v-model="queryForm.createTimeStart" type="datetime" placeholder="开始时间" />
          <span style="margin: 0 8px">-</span>
          <el-date-picker v-model="queryForm.createTimeEnd" type="datetime" placeholder="结束时间" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 操作栏 -->
    <el-card shadow="hover">
      <div style="margin-bottom: 16px; display: flex; justify-content: space-between">
        <div>
          <el-button type="primary" @click="$router.push('/order/add')">创建订单</el-button>
          <el-button @click="handleExport">导出Excel</el-button>
          <el-button v-if="selectedIds.length > 0" @click="handleMerge">合单</el-button>
        </div>
        <div>
          共 <strong>{{ total }}</strong> 条
        </div>
      </div>

      <!-- 表格 -->
      <el-table :data="list" stripe v-loading="loading" @selection-change="ids => selectedIds = ids.map(i => i.id)">
        <el-table-column type="selection" width="45" />
        <el-table-column prop="orderNo" label="订单号" width="180" fixed />
        <el-table-column prop="customerName" label="客户" width="120" />
        <el-table-column prop="paymentAmount" label="金额" width="120" align="right">
          <template #default="scope">¥{{ scope.row.paymentAmount }}</template>
        </el-table-column>
        <el-table-column prop="orderStatus" label="状态" width="100" align="center">
          <template #default="scope">
            <el-tag :type="statusType(scope.row.orderStatus)" size="small">
              {{ statusMap[scope.row.orderStatus] || scope.row.orderStatus }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="receiverName" label="收货人" width="100" />
        <el-table-column prop="receiverPhone" label="电话" width="130" />
        <el-table-column prop="createTime" label="下单时间" width="170" />
        <el-table-column prop="expressNo" label="快递单号" width="150" />
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="scope">
            <el-button text type="primary" @click="$router.push('/order/detail/' + scope.row.id)">详情</el-button>
            <el-button v-if="scope.row.orderStatus === 'PENDING_AUDIT'" text type="primary" @click="handleAudit(scope.row)">审核通过</el-button>
            <el-button v-if="scope.row.orderStatus === 'PENDING_AUDIT'" text type="warning" @click="handleCancel(scope.row)">取消</el-button>
            <el-button v-if="scope.row.orderStatus === 'PAID'" text type="primary" @click="handleSplit(scope.row)">拆单</el-button>
            <el-button text type="danger" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div style="margin-top: 16px; display: flex; justify-content: flex-end">
        <el-pagination
          v-model:current-page="queryForm.page"
          v-model:page-size="queryForm.pageSize"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next"
          @change="fetchData"
        />
      </div>
    </el-card>

    <!-- 拆单对话框 -->
    <el-dialog v-model="splitVisible" title="拆单" width="600px">
      <p>请选择拆单方式（功能预留）</p>
      <span slot="footer">
        <el-button @click="splitVisible = false">取消</el-button>
        <el-button type="primary">确认拆单</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getOrderPage, cancelOrder, deleteOrder, mergeOrders, exportOrder } from '../../api/order'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const list = ref([])
const total = ref(0)
const selectedIds = ref([])
const splitVisible = ref(false)

const statusMap = {
  PENDING_AUDIT: '待审核', PAID: '已付款', PENDING_DELIVERY: '待发货',
  DELIVERED: '已发货', RECEIVED: '已签收', COMPLETED: '已完成',
  CANCELLED: '已取消', AFTER_SALE: '售后中'
}

const queryForm = reactive({
  orderNo: '', customerName: '', orderStatus: '',
  createTimeStart: null, createTimeEnd: null,
  page: 1, pageSize: 20
})

onMounted(() => fetchData())

async function fetchData() {
  loading.value = true
  try {
    const res = await getOrderPage(queryForm)
    const page = res.data
    list.value = page.records || []
    total.value = page.total || 0
  } catch (e) {} finally {
    loading.value = false
  }
}

function handleSearch() {
  queryForm.page = 1
  fetchData()
}

function handleReset() {
  queryForm.orderNo = ''
  queryForm.customerName = ''
  queryForm.orderStatus = ''
  queryForm.createTimeStart = null
  queryForm.createTimeEnd = null
  handleSearch()
}

function statusType(status) {
  const types = { PENDING_AUDIT: 'warning', PAID: 'info', PENDING_DELIVERY: 'primary', DELIVERED: '', RECEIVED: 'success', COMPLETED: 'success', CANCELLED: 'danger', AFTER_SALE: 'danger' }
  return types[status] || 'info'
}

async function handleAudit(row) {
  await updateOrderStatus(row.id, { status: 'PAID', content: '审核通过' })
  ElMessage.success('审核通过')
  fetchData()
}

async function handleCancel(row) {
  try {
    await ElMessageBox.prompt('请输入取消原因', '取消订单')
    await cancelOrder(row.id, { reason: '手动取消' })
    ElMessage.success('已取消')
    fetchData()
  } catch (e) {}
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm('确定删除订单 ' + row.orderNo + '？')
    await deleteOrder(row.id)
    ElMessage.success('已删除')
    fetchData()
  } catch (e) {}
}

async function handleMerge() {
  if (selectedIds.value.length < 2) {
    ElMessage.warning('请选择至少2个订单')
    return
  }
  try {
    await ElMessageBox.confirm('确定合并所选订单？')
    await mergeOrders(selectedIds.value)
    ElMessage.success('合单成功')
    fetchData()
  } catch (e) {}
}

function handleSplit(row) {
  splitVisible.value = true
}

async function handleExport() {
  try {
    await exportOrder(queryForm)
    ElMessage.success('导出成功')
  } catch (e) {
    ElMessage.error('导出失败')
  }
}

import { updateOrderStatus } from '../../api/order'
</script>
