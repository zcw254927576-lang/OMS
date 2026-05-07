<template>
  <div>
    <el-card shadow="hover" style="margin-bottom: 20px">
      <el-form :inline="true">
        <el-form-item label="搜索">
          <el-input v-model="keyword" placeholder="客户名称/电话/编号" clearable @change="search" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="search">查询</el-button>
          <el-button type="success" @click="dialogVisible = true, current = {}">新增客户</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="hover">
      <el-table :data="list" stripe v-loading="loading">
        <el-table-column prop="customerNo" label="编号" width="150" />
        <el-table-column prop="customerName" label="客户名称" width="140" />
        <el-table-column prop="phone" label="电话" width="130" />
        <el-table-column prop="levelName" label="等级" width="80" align="center">
          <template #default="scope"><el-tag size="small">{{ scope.row.levelName || '-' }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="totalAmount" label="消费总额" width="130" align="right">
          <template #default="scope">¥{{ scope.row.totalAmount || 0 }}</template>
        </el-table-column>
        <el-table-column prop="orderCount" label="订单数" width="80" align="center" />
        <el-table-column prop="lastOrderTime" label="最近下单" width="170" />
        <el-table-column prop="tag" label="标签" width="150" />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="scope">
            <el-button text type="primary" @click="edit(scope.row)">编辑</el-button>
            <el-button text type="primary" @click="showStats(scope.row)">统计</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div style="margin-top: 16px; display: flex; justify-content: flex-end">
        <el-pagination v-model:current-page="page" v-model:page-size="pageSize" :total="total" layout="total, prev, pager, next" @change="fetchData" />
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="current.id ? '编辑客户' : '新增客户'" width="700px">
      <el-form :model="current" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12"><el-form-item label="客户编号"><el-input v-model="current.customerNo" placeholder="留空自动生成" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="名称" required><el-input v-model="current.customerName" /></el-form-item></el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12"><el-form-item label="电话"><el-input v-model="current.phone" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="等级"><el-select v-model="current.levelId" style="width:100%"><el-option v-for="l in levels" :key="l.id" :label="l.levelName" :value="l.id" /></el-select></el-form-item></el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12"><el-form-item label="标签"><el-input v-model="current.tag" /></el-form-item></el-col>
        </el-row>
        <el-form-item label="地址">
          <el-input v-model="current.detailAddress" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="current.remark" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="statsVisible" title="客户统计" width="500px">
      <el-descriptions :column="2" border v-if="customerStats">
        <el-descriptions-item label="总消费">¥{{ customerStats.totalAmount || 0 }}</el-descriptions-item>
        <el-descriptions-item label="订单数">{{ customerStats.orderCount || 0 }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getCustomerPage, saveCustomer, updateCustomer, getCustomerStats, getLevelList } from '../../api/customer'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const list = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(20)
const keyword = ref('')
const dialogVisible = ref(false)
const statsVisible = ref(false)
const current = ref({})
const levels = ref([])
const customerStats = ref(null)

onMounted(() => { fetchData(); loadLevels() })

async function fetchData() {
  loading.value = true
  try {
    const res = await getCustomerPage({ keyword: keyword.value, page: page.value, pageSize: pageSize.value })
    list.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (e) {} finally { loading.value = false }
}

function search() { page.value = 1; fetchData() }
function edit(row) { current.value = { ...row }; dialogVisible.value = true }

async function save() {
  if (current.value.id) await updateCustomer(current.value)
  else await saveCustomer(current.value)
  ElMessage.success('保存成功')
  dialogVisible.value = false
  fetchData()
}

async function showStats(row) {
  try { const res = await getCustomerStats(row.id); customerStats.value = res.data } catch (e) {}
  statsVisible.value = true
}

async function loadLevels() {
  try { const res = await getLevelList(); levels.value = res.data || [] } catch (e) {}
}
</script>
