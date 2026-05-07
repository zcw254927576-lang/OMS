<template>
  <div>
    <!-- 月度趋势 -->
    <el-card shadow="hover" style="margin-bottom: 20px">
      <template #header>月度销售额趋势</template>
      <div ref="monthlyChart" style="height: 400px"></div>
    </el-card>

    <!-- 订单状态分布 -->
    <el-row :gutter="20">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>订单状态分布</template>
          <div ref="statusPie" style="height: 350px"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>近30日订单量</template>
          <div ref="dailyBar" style="height: 350px"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { getMonthlySales, getOrderStatusStats, getDailyOrderStats } from '../../api/statistics'
import * as echarts from 'echarts'

const monthlyChart = ref()
const statusPie = ref()
const dailyBar = ref()

onMounted(async () => {
  const [monthlyRes, statusRes, dailyRes] = await Promise.all([
    getMonthlySales(), getOrderStatusStats(), getDailyOrderStats()
  ]).catch(() => [{ data: [] }, { data: [] }, { data: [] }])

  const monthlyData = monthlyRes.data || []
  const statusData = statusRes.data || []
  const dailyData = dailyRes.data || []

  nextTick(() => {
    // 月度趋势
    const chart1 = echarts.init(monthlyChart.value)
    chart1.setOption({
      tooltip: { trigger: 'axis' },
      grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
      xAxis: { type: 'category', data: monthlyData.map(d => d.monthKey) },
      yAxis: { type: 'value' },
      series: [
        { name: '订单数', type: 'bar', data: monthlyData.map(d => d.orderCount), itemStyle: { color: '#409EFF' } },
        { name: '销售额', type: 'line', data: monthlyData.map(d => d.amount), itemStyle: { color: '#67C23A' } }
      ]
    })

    // 状态分布
    const chart2 = echarts.init(statusPie.value)
    const statusLabels = { PENDING_AUDIT: '待审核', PAID: '已付款', PENDING_DELIVERY: '待发货', DELIVERED: '已发货', RECEIVED: '已签收', COMPLETED: '已完成', CANCELLED: '已取消', AFTER_SALE: '售后中' }
    chart2.setOption({
      tooltip: { trigger: 'item' },
      series: [{
        type: 'pie', radius: ['30%', '60%'],
        data: statusData.map(d => ({ name: statusLabels[d.statusKey] || d.statusKey, value: d.count })),
        label: { formatter: '{b}: {c}' }
      }]
    })

    // 每日订单
    const chart3 = echarts.init(dailyBar.value)
    chart3.setOption({
      tooltip: { trigger: 'axis' },
      grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
      xAxis: { type: 'category', data: dailyData.map(d => d.dateKey), axisLabel: { rotate: 45 } },
      yAxis: { type: 'value' },
      series: [{ type: 'bar', data: dailyData.map(d => d.orderCount), itemStyle: { color: '#409EFF' } }]
    })
  })
})
</script>
