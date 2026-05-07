<template>
  <div>
    <el-row :gutter="20">
      <el-col :span="14">
        <el-card shadow="hover">
          <template #header>商品销量排行</template>
          <el-table :data="ranking" stripe v-loading="loading">
            <el-table-column type="index" label="排名" width="60" />
            <el-table-column prop="sku_name" label="商品名称" min-width="200" />
            <el-table-column prop="sku_code" label="编码" width="150" />
            <el-table-column prop="price" label="售价" width="100" align="right" />
            <el-table-column prop="totalSold" label="销量" width="80" align="center" />
            <el-table-column prop="totalAmount" label="销售金额" width="130" align="right">
              <template #default="scope">¥{{ scope.row.totalAmount }}</template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="10">
        <el-card shadow="hover">
          <template #header>库存周转率</template>
          <el-table :data="turnover" stripe v-loading="loading">
            <el-table-column prop="sku_name" label="商品" min-width="160" />
            <el-table-column prop="outQuantity" label="出库量" width="80" align="center" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getProductRanking, getStockTurnover } from '../../api/statistics'

const loading = ref(false)
const ranking = ref([])
const turnover = ref([])

onMounted(async () => {
  loading.value = true
  try {
    const [rankRes, turnRes] = await Promise.all([
      getProductRanking(50), getStockTurnover()
    ])
    ranking.value = rankRes.data || []
    turnover.value = turnRes.data || []
  } catch (e) {} finally { loading.value = false }
})
</script>
