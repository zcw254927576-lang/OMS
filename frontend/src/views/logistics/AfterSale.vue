<template>
  <div>
    <el-card shadow="hover" style="margin-bottom: 20px">
      <el-form :inline="true">
        <el-form-item label="状态">
          <el-select v-model="status" placeholder="全部" clearable style="width: 130px">
            <el-option label="待审核" value="PENDING" />
            <el-option label="已通过" value="APPROVED" />
            <el-option label="已拒绝" value="REJECTED" />
            <el-option label="已完成" value="COMPLETED" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="search">查询</el-button>
          <el-button type="success" @click="showCreate = true">新建售后</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="hover">
      <el-table :data="list" stripe v-loading="loading">
        <el-table-column prop="afterSaleNo" label="售后单号" width="170" />
        <el-table-column prop="orderNo" label="订单号" width="170" />
        <el-table-column prop="skuName" label="商品" min-width="180" />
        <el-table-column prop="quantity" label="数量" width="60" align="center" />
        <el-table-column prop="afterSaleType" label="类型" width="80" align="center">
          <template #default="scope">
            <el-tag size="small">{{ typeMap[scope.row.afterSaleType] }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="reason" label="原因" width="200" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.status === 'PENDING' ? 'warning' : scope.row.status === 'APPROVED' ? 'success' : 'danger'" size="small">
              {{ statusMap[scope.row.status] }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="申请时间" width="170" />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="scope">
            <el-button v-if="scope.row.status === 'PENDING'" text type="success" @click="handleAudit(scope.row, true)">通过</el-button>
            <el-button v-if="scope.row.status === 'PENDING'" text type="danger" @click="handleAudit(scope.row, false)">拒绝</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div style="margin-top: 16px; display: flex; justify-content: flex-end">
        <el-pagination v-model:current-page="page" v-model:page-size="pageSize" :total="total" layout="total, prev, pager, next" @change="fetchData" />
      </div>
    </el-card>

    <el-dialog v-model="showCreate" title="新建售后单" width="500px">
      <el-form :model="afterSaleForm" label-width="100px">
        <el-form-item label="订单号" required><el-input v-model="afterSaleForm.orderNo" /></el-form-item>
        <el-form-item label="商品SKU" required><el-input v-model="afterSaleForm.skuId" /></el-form-item>
        <el-form-item label="数量" required><el-input-number v-model="afterSaleForm.quantity" :min="1" /></el-form-item>
        <el-form-item label="类型" required>
          <el-select v-model="afterSaleForm.afterSaleType" style="width:100%">
            <el-option label="退货" value="RETURN" />
            <el-option label="换货" value="EXCHANGE" />
            <el-option label="仅退款" value="REFUND" />
          </el-select>
        </el-form-item>
        <el-form-item label="原因"><el-input v-model="afterSaleForm.reason" type="textarea" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreate = false">取消</el-button>
        <el-button type="primary" @click="handleCreate">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { getAfterSalePage, createAfterSale, auditAfterSale } from '../../api/logistics'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const list = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(20)
const status = ref('')
const showCreate = ref(false)
const afterSaleForm = reactive({ orderNo: '', skuId: null, quantity: 1, afterSaleType: 'RETURN', reason: '' })

const typeMap = { RETURN: '退货', EXCHANGE: '换货', REFUND: '退款' }
const statusMap = { PENDING: '待审核', APPROVED: '已通过', REJECTED: '已拒绝', COMPLETED: '已完成' }

onMounted(() => fetchData())

async function fetchData() {
  loading.value = true
  try {
    const res = await getAfterSalePage({ status: status.value, page: page.value, pageSize: pageSize.value })
    list.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (e) {} finally { loading.value = false }
}

function search() { page.value = 1; fetchData() }

async function handleCreate() {
  await createAfterSale(afterSaleForm)
  ElMessage.success('提交成功')
  showCreate.value = false
  fetchData()
}

async function handleAudit(row, approved) {
  await auditAfterSale(row.id, { approved, remark: approved ? '审核通过' : '审核拒绝' })
  ElMessage.success(approved ? '已通过' : '已拒绝')
  fetchData()
}
</script>
