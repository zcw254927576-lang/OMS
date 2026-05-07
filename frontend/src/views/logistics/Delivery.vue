<template>
  <div>
    <el-card shadow="hover" style="margin-bottom: 20px">
      <el-form :inline="true">
        <el-form-item label="订单号">
          <el-input v-model="orderNo" placeholder="订单号" clearable @change="search" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="search">查询</el-button>
          <el-button type="success" @click="showDelivery = true">发货</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="hover">
      <el-table :data="list" stripe v-loading="loading">
        <el-table-column prop="deliveryNo" label="发货单号" width="170" />
        <el-table-column prop="orderNo" label="订单号" width="170" />
        <el-table-column prop="expressCompany" label="快递公司" width="120" />
        <el-table-column prop="expressNo" label="快递单号" width="150" />
        <el-table-column prop="receiverName" label="收货人" width="100" />
        <el-table-column prop="receiverPhone" label="电话" width="130" />
        <el-table-column prop="totalQuantity" label="总数" width="60" align="center" />
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.status === 'DELIVERED' ? 'success' : 'warning'">{{ scope.row.status === 'DELIVERED' ? '已发货' : '待发货' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="deliveryTime" label="发货时间" width="170" />
        <el-table-column prop="operator" label="操作人" width="100" />
      </el-table>
      <div style="margin-top: 16px; display: flex; justify-content: flex-end">
        <el-pagination v-model:current-page="page" v-model:page-size="pageSize" :total="total" layout="total, prev, pager, next" @change="fetchData" />
      </div>
    </el-card>

    <!-- 发货对话框 -->
    <el-dialog v-model="showDelivery" title="发货" width="500px">
      <el-form :model="deliveryForm" label-width="100px">
        <el-form-item label="订单ID" required>
          <el-input v-model="deliveryForm.orderId" placeholder="输入订单ID" />
        </el-form-item>
        <el-form-item label="快递公司" required>
          <el-select v-model="deliveryForm.expressCompany" style="width:100%">
            <el-option label="顺丰速运" value="顺丰速运" />
            <el-option label="中通快递" value="中通快递" />
            <el-option label="圆通速递" value="圆通速递" />
            <el-option label="韵达快递" value="韵达快递" />
            <el-option label="申通快递" value="申通快递" />
            <el-option label="京东物流" value="京东物流" />
            <el-option label="EMS" value="EMS" />
          </el-select>
        </el-form-item>
        <el-form-item label="快递单号" required>
          <el-input v-model="deliveryForm.expressNo" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDelivery = false">取消</el-button>
        <el-button type="primary" @click="handleDelivery">确认发货</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { getDeliveryPage, delivery } from '../../api/logistics'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const list = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(20)
const orderNo = ref('')
const showDelivery = ref(false)
const deliveryForm = reactive({ orderId: '', expressCompany: '', expressNo: '' })

onMounted(() => fetchData())

async function fetchData() {
  loading.value = true
  try {
    const res = await getDeliveryPage({ orderNo: orderNo.value, page: page.value, pageSize: pageSize.value })
    list.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (e) {} finally { loading.value = false }
}

function search() { page.value = 1; fetchData() }

async function handleDelivery() {
  if (!deliveryForm.orderId || !deliveryForm.expressCompany || !deliveryForm.expressNo) {
    ElMessage.warning('请填写完整信息')
    return
  }
  await delivery({ ...deliveryForm })
  ElMessage.success('发货成功')
  showDelivery.value = false
  deliveryForm.orderId = ''
  deliveryForm.expressNo = ''
  fetchData()
}
</script>
