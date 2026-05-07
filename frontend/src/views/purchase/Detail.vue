<template>
  <div>
    <el-card shadow="hover" v-loading="loading">
      <template #header>
        <div style="display: flex; align-items: center; justify-content: space-between">
          <span style="font-weight: bold">采购单详情</span>
          <div>
            <el-button v-if="order.status === 0" type="primary" @click="handleSubmit">提交</el-button>
            <el-button v-if="order.status === 1" type="warning" @click="handleApprove">审核</el-button>
            <el-button v-if="order.status === 2" type="success" @click="handleWarehouse">入库</el-button>
            <el-button @click="$router.push('/purchase/list')">返回</el-button>
          </div>
        </div>
      </template>

      <el-descriptions :column="3" border>
        <el-descriptions-item label="采购单号">{{ order.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="供应商">{{ order.supplierName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="statusTag(order.status)" size="small">{{ statusText(order.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="总金额">¥{{ order.totalAmount || 0 }}</el-descriptions-item>
        <el-descriptions-item label="创建人">{{ order.createBy || '-' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ order.createTime }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="3">{{ order.remark || '-' }}</el-descriptions-item>
      </el-descriptions>

      <el-divider>采购商品明细</el-divider>

      <el-table :data="items" border>
        <el-table-column prop="skuName" label="商品名称" />
        <el-table-column prop="quantity" label="数量" width="100" align="center" />
        <el-table-column prop="unitPrice" label="单价" width="120" align="right">
          <template #default="{ row }">¥{{ row.unitPrice || 0 }}</template>
        </el-table-column>
        <el-table-column prop="totalPrice" label="小计" width="120" align="right">
          <template #default="{ row }">¥{{ row.totalPrice || 0 }}</template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getPurchaseDetail, getPurchaseItems, submitPurchase, approvePurchase, warehousePurchase } from '../../api/purchase'
import { ElMessage, ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const order = ref({})
const items = ref([])

onMounted(async () => {
  const id = route.params.id
  if (id) { await fetchDetail(id) }
})

async function fetchDetail(id) {
  loading.value = true
  try {
    const [res1, res2] = await Promise.all([getPurchaseDetail(id), getPurchaseItems(id)])
    order.value = res1.data || {}
    items.value = res2.data || []
  } catch (e) {} finally { loading.value = false }
}

function statusText(status) {
  return { 0: '草稿', 1: '已提交', 2: '已审核', 3: '已入库' }[status] || '未知'
}
function statusTag(status) {
  return { 0: 'info', 1: 'warning', 2: 'primary', 3: 'success' }[status] || ''
}

async function handleSubmit() {
  await ElMessageBox.confirm('确认提交此采购单吗？')
  await submitPurchase(order.value.id)
  ElMessage.success('已提交')
  fetchDetail(order.value.id)
}
async function handleApprove() {
  await ElMessageBox.confirm('确认审核通过此采购单吗？')
  await approvePurchase(order.value.id)
  ElMessage.success('已审核')
  fetchDetail(order.value.id)
}
async function handleWarehouse() {
  await ElMessageBox.confirm('确认入库吗？入库后将自动更新库存。')
  await warehousePurchase(order.value.id)
  ElMessage.success('已入库，库存已更新')
  fetchDetail(order.value.id)
}
</script>
