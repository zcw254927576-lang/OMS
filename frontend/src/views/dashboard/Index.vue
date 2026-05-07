<template>
  <div>
    <!-- 统计卡片 -->
    <el-row :gutter="20" style="margin-bottom: 20px">
      <el-col :span="6" v-for="card in statCards" :key="card.title">
        <el-card shadow="hover">
          <div style="display: flex; justify-content: space-between; align-items: center">
            <div>
              <div style="color: #909399; font-size: 14px">{{ card.title }}</div>
              <div style="font-size: 28px; font-weight: bold; margin-top: 8px">{{ card.value }}</div>
            </div>
            <el-icon :size="48" :color="card.color"><component :is="card.icon" /></el-icon>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20">
      <!-- 订单趋势 -->
      <el-col :span="16">
        <el-card shadow="hover">
          <template #header>近30日订单趋势</template>
          <div ref="orderChartRef" style="height: 350px"></div>
        </el-card>
      </el-col>
      <!-- 订单状态分布 -->
      <el-col :span="8">
        <el-card shadow="hover">
          <template #header>订单状态分布</template>
          <div ref="pieChartRef" style="height: 350px"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 商品销量排行 -->
    <el-card shadow="hover" style="margin-top: 20px">
      <template #header>商品销量排行 TOP10</template>
      <el-table :data="productRanking" stripe style="width: 100%">
        <el-table-column type="index" label="排名" width="60" />
        <el-table-column prop="sku_name" label="商品名称" />
        <el-table-column prop="sku_code" label="编码" width="150" />
        <el-table-column prop="totalSold" label="销量" width="100" align="center" />
        <el-table-column prop="totalAmount" label="销售金额" width="150" align="center">
          <template #default="scope">¥{{ scope.row.totalAmount }}</template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 库存预警 -->
    <el-card shadow="hover" style="margin-top: 20px">
      <template #header>库存预警</template>
      <el-alert v-if="lowStockList.length === 0" title="暂无库存预警" type="success" show-icon :closable="false" />
      <el-table v-else :data="lowStockList" stripe>
        <el-table-column prop="skuCode" label="SKU编码" />
        <el-table-column prop="skuName" label="商品名称" />
        <el-table-column prop="availableQuantity" label="可用库存" />
        <el-table-column prop="minStock" label="预警下限" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { getDashboard, getProductRanking } from '../../api/statistics'
import { getLowStockList } from '../../api/inventory'
import * as echarts from 'echarts'

const statCards = ref([
  { title: '今日订单', value: 0, icon: 'ShoppingCart', color: '#409EFF' },
  { title: '今日销售额', value: '¥0', icon: 'Money', color: '#67C23A' },
  { title: '待发货', value: 0, icon: 'Van', color: '#E6A23C' },
  { title: '售后处理', value: 0, icon: 'SoldOut', color: '#F56C6C' }
])

const productRanking = ref([])
const lowStockList = ref([])
const orderChartRef = ref()
const pieChartRef = ref()

onMounted(async () => {
  try {
    const res = await getDashboard()
    const data = res.data || {}
    statCards.value[0].value = data.todayOrderCount || 0
    statCards.value[1].value = '¥' + (data.todayAmount || 0)
    statCards.value[2].value = getStatusCount(data.orderStatusStats, 'PENDING_DELIVERY')
    statCards.value[3].value = getStatusCount(data.orderStatusStats, 'AFTER_SALE')

    nextTick(() => {
      renderOrderChart(data.dailyOrderStats)
      renderPieChart(data.orderStatusStats)
    })
  } catch (e) {}

  try {
    const res = await getProductRanking(10)
    productRanking.value = res.data || []
  } catch (e) {}

  try {
    const res = await getLowStockList()
    lowStockList.value = res.data || []
  } catch (e) {}
})

const statusLabels = {
  'PENDING_AUDIT': '待审核', 'PAID': '已付款', 'PENDING_DELIVERY': '待发货',
  'DELIVERED': '已发货', 'RECEIVED': '已签收', 'COMPLETED': '已完成',
  'CANCELLED': '已取消', 'AFTER_SALE': '售后中'
}

function getStatusCount(list, status) {
  if (!list) return 0
  const item = list.find(s => s.statusKey === status)
  return item ? item.count : 0
}

function renderOrderChart(data) {
  if (!data || !orderChartRef.value) return
  const chart = echarts.init(orderChartRef.value)
  chart.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', data: data.map(d => d.dateKey), axisLabel: { rotate: 45 } },
    yAxis: { type: 'value' },
    series: [
      { name: '订单数', type: 'bar', data: data.map(d => d.orderCount), itemStyle: { color: '#409EFF' } },
      { name: '金额', type: 'line', data: data.map(d => d.amount), itemStyle: { color: '#67C23A' } }
    ]
  })
}

function renderPieChart(data) {
  if (!data || !pieChartRef.value) return
  const chart = echarts.init(pieChartRef.value)
  chart.setOption({
    tooltip: { trigger: 'item' },
    series: [{
      type: 'pie', radius: ['40%', '70%'],
      data: data.map(d => ({ name: statusLabels[d.statusKey] || d.statusKey, value: d.count })),
      label: { show: true, formatter: '{b}: {c}' }
    }]
  })
}
</script>
