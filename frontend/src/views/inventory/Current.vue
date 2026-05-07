<template>
  <div>
    <el-card shadow="hover" style="margin-bottom: 20px">
      <el-form :inline="true">
        <el-form-item label="SKU编码">
          <el-input v-model="skuCode" placeholder="编码搜索" clearable @change="search" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="search">查询</el-button>
        </el-form-item>
        <el-form-item>
          <el-button type="warning" @click="showLowStock">库存预警</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="hover">
      <el-table :data="list" stripe v-loading="loading">
        <el-table-column prop="skuCode" label="SKU编码" width="150" />
        <el-table-column prop="skuName" label="商品名称" min-width="200" />
        <el-table-column prop="warehouseName" label="仓库" width="120" />
        <el-table-column prop="quantity" label="总库存" width="100" align="center" />
        <el-table-column prop="lockedQuantity" label="锁定库存" width="100" align="center">
          <template #default="scope">
            <span style="color: #E6A23C">{{ scope.row.lockedQuantity }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="availableQuantity" label="可用库存" width="100" align="center">
          <template #default="scope">
            <span :style="{ color: scope.row.availableQuantity < scope.row.minStock ? '#F56C6C' : '#67C23A', fontWeight: 'bold' }">
              {{ scope.row.availableQuantity }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="minStock" label="预警下限" width="100" align="center" />
        <el-table-column label="操作" width="120">
          <template #default="scope">
            <el-button text type="primary" @click="showAdjust(scope.row)">调整</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div style="margin-top: 16px; display: flex; justify-content: flex-end">
        <el-pagination v-model:current-page="page" v-model:page-size="pageSize" :total="total" layout="total, prev, pager, next" @change="fetchData" />
      </div>
    </el-card>

    <!-- 库存调整 -->
    <el-dialog v-model="adjustVisible" title="库存调整" width="400px">
      <el-form :model="adjustForm" label-width="80px">
        <el-form-item label="商品">{{ currentSku?.skuName }}</el-form-item>
        <el-form-item label="当前库存">{{ currentSku?.quantity }}</el-form-item>
        <el-form-item label="调整数量">
          <el-input-number v-model="adjustForm.quantity" :min="-99999" :max="99999" />
          <div style="color: #909399; font-size: 12px">正数=入库，负数=出库</div>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="adjustForm.remark" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="adjustVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAdjust">确认调整</el-button>
      </template>
    </el-dialog>

    <!-- 库存预警列表 -->
    <el-dialog v-model="lowStockVisible" title="库存预警" width="700px">
      <el-table :data="lowStockList" stripe>
        <el-table-column prop="skuCode" label="SKU编码" width="150" />
        <el-table-column prop="skuName" label="商品名称" />
        <el-table-column prop="availableQuantity" label="可用库存" width="100" align="center">
          <template #default="scope">
            <span style="color: #F56C6C; font-weight: bold">{{ scope.row.availableQuantity }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="minStock" label="预警下限" width="100" align="center" />
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getInventoryPage, getLowStockList, adjustStock } from '../../api/inventory'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const list = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(20)
const skuCode = ref('')
const adjustVisible = ref(false)
const lowStockVisible = ref(false)
const currentSku = ref(null)
const lowStockList = ref([])
const adjustForm = ref({ quantity: 0, remark: '' })

onMounted(() => fetchData())

async function fetchData() {
  loading.value = true
  try {
    const res = await getInventoryPage({ skuCode: skuCode.value, page: page.value, pageSize: pageSize.value })
    list.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (e) {} finally { loading.value = false }
}

function search() { page.value = 1; fetchData() }

function showAdjust(row) {
  currentSku.value = row
  adjustForm.value = { quantity: 0, remark: '' }
  adjustVisible.value = true
}

async function handleAdjust() {
  await adjustStock({ skuId: currentSku.value.skuId, quantity: adjustForm.value.quantity, remark: adjustForm.value.remark })
  ElMessage.success('调整成功')
  adjustVisible.value = false
  fetchData()
}

async function showLowStock() {
  try { const res = await getLowStockList(); lowStockList.value = res.data || [] } catch (e) {}
  lowStockVisible.value = true
}
</script>
