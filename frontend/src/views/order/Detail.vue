<template>
  <div>
    <el-card shadow="hover" style="margin-bottom: 20px">
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center">
          <span>订单详情 - {{ order?.orderNo }}</span>
          <el-button @click="$router.back()">返回</el-button>
        </div>
      </template>

      <el-descriptions :column="3" border v-if="order">
        <el-descriptions-item label="订单号" :span="2">{{ order.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="statusType(order.orderStatus)">{{ statusMap[order.orderStatus] }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="客户">{{ order.customerName }}</el-descriptions-item>
        <el-descriptions-item label="电话">{{ order.customerPhone }}</el-descriptions-item>
        <el-descriptions-item label="下单时间">{{ order.createTime }}</el-descriptions-item>
        <el-descriptions-item label="商品总额">¥{{ order.totalAmount }}</el-descriptions-item>
        <el-descriptions-item label="运费">¥{{ order.shippingAmount }}</el-descriptions-item>
        <el-descriptions-item label="实付金额">¥{{ order.paymentAmount }}</el-descriptions-item>
        <el-descriptions-item label="收货人">{{ order.receiverName }}</el-descriptions-item>
        <el-descriptions-item label="电话">{{ order.receiverPhone }}</el-descriptions-item>
        <el-descriptions-item label="地址">{{ order.province }}{{ order.city }}{{ order.district }}{{ order.detailAddress }}</el-descriptions-item>
        <el-descriptions-item label="快递公司">{{ order.expressCompany || '-' }}</el-descriptions-item>
        <el-descriptions-item label="快递单号">{{ order.expressNo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="卖家备注">{{ order.sellerMessage || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <!-- 商品明细 -->
    <el-card shadow="hover" style="margin-bottom: 20px">
      <template #header>商品明细</template>
      <el-table :data="items" stripe>
        <el-table-column prop="skuCode" label="编码" width="150" />
        <el-table-column prop="skuName" label="商品名称" />
        <el-table-column prop="skuSpec" label="规格" />
        <el-table-column prop="quantity" label="数量" width="80" align="center" />
        <el-table-column prop="unitPrice" label="单价" width="100" align="right">
          <template #default="scope">¥{{ scope.row.unitPrice }}</template>
        </el-table-column>
        <el-table-column prop="totalPrice" label="小计" width="120" align="right">
          <template #default="scope">¥{{ scope.row.totalPrice }}</template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 操作日志 -->
    <el-card shadow="hover">
      <template #header>操作日志</template>
      <el-timeline>
        <el-timeline-item v-for="log in logs" :key="log.id" :timestamp="log.createTime">
          <p><strong>{{ log.operator }}</strong> {{ log.operationContent }}</p>
          <p style="color: #909399; font-size: 13px">
            {{ log.beforeStatus ? statusMap[log.beforeStatus] + ' → ' : '' }}{{ log.afterStatus ? statusMap[log.afterStatus] : '' }}
          </p>
        </el-timeline-item>
      </el-timeline>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getOrderDetail, getOrderItems, getOrderLogs } from '../../api/order'

const route = useRoute()
const order = ref(null)
const items = ref([])
const logs = ref([])

const statusMap = {
  PENDING_AUDIT: '待审核', PAID: '已付款', PENDING_DELIVERY: '待发货',
  DELIVERED: '已发货', RECEIVED: '已签收', COMPLETED: '已完成',
  CANCELLED: '已取消', AFTER_SALE: '售后中'
}

onMounted(async () => {
  const id = route.params.id
  try {
    const [orderRes, itemsRes, logsRes] = await Promise.all([
      getOrderDetail(id), getOrderItems(id), getOrderLogs(id)
    ])
    order.value = orderRes.data
    items.value = itemsRes.data || []
    logs.value = logsRes.data || []
  } catch (e) {}
})

function statusType(status) {
  const types = { PENDING_AUDIT: 'warning', PAID: 'info', PENDING_DELIVERY: 'primary', DELIVERED: '', RECEIVED: 'success', COMPLETED: 'success', CANCELLED: 'danger', AFTER_SALE: 'danger' }
  return types[status] || 'info'
}
</script>
