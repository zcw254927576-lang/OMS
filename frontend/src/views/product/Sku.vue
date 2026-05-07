<template>
  <div>
    <el-card shadow="hover" style="margin-bottom: 20px">
      <el-form :inline="true">
        <el-form-item label="搜索">
          <el-input v-model="keyword" placeholder="商品名称/编码" clearable @change="search" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="search">查询</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="hover">
      <div style="margin-bottom: 16px">
        <el-button type="primary" @click="dialogVisible = true, dialogType = 'add', currentSku = {}">新增SKU</el-button>
      </div>
      <el-table :data="list" stripe v-loading="loading">
        <el-table-column prop="skuCode" label="编码" width="150" />
        <el-table-column prop="skuName" label="商品名称" min-width="200" />
        <el-table-column prop="categoryName" label="分类" width="120" />
        <el-table-column prop="brand" label="品牌" width="120" />
        <el-table-column prop="price" label="售价" width="100" align="right">
          <template #default="scope">¥{{ scope.row.price }}</template>
        </el-table-column>
        <el-table-column prop="costPrice" label="成本价" width="100" align="right">
          <template #default="scope">¥{{ scope.row.costPrice }}</template>
        </el-table-column>
        <el-table-column prop="unit" label="单位" width="60" align="center" />
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'info'" size="small">{{ scope.row.status === 1 ? '上架' : '下架' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="scope">
            <el-button text type="primary" @click="edit(scope.row)">编辑</el-button>
            <el-button text type="warning" @click="toggleStatus(scope.row)">{{ scope.row.status === 1 ? '下架' : '上架' }}</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div style="margin-top: 16px; display: flex; justify-content: flex-end">
        <el-pagination v-model:current-page="page" v-model:page-size="pageSize" :total="total" layout="total, prev, pager, next" @change="fetchData" />
      </div>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogType === 'add' ? '新增SKU' : '编辑SKU'" width="700px">
      <el-form :model="currentSku" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="编码" required>
              <el-input v-model="currentSku.skuCode" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="名称" required>
              <el-input v-model="currentSku.skuName" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="分类">
              <el-select v-model="currentSku.categoryId" filterable style="width: 100%">
                <el-option v-for="c in categories" :key="c.id" :label="c.categoryName" :value="c.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="品牌">
              <el-input v-model="currentSku.brand" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="售价">
              <el-input-number v-model="currentSku.price" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="成本价">
              <el-input-number v-model="currentSku.costPrice" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveSku">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getSkuPage, saveSku, updateSku, toggleSkuStatus, getCategoryList } from '../../api/product'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const list = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(20)
const keyword = ref('')
const dialogVisible = ref(false)
const dialogType = ref('add')
const currentSku = ref({})
const categories = ref([])

onMounted(() => { fetchData(); loadCategories() })

async function fetchData() {
  loading.value = true
  try {
    const res = await getSkuPage({ keyword: keyword.value, page: page.value, pageSize: pageSize.value })
    list.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (e) {} finally { loading.value = false }
}

function search() { page.value = 1; fetchData() }

function edit(row) { currentSku.value = { ...row }; dialogType.value = 'edit'; dialogVisible.value = true }

async function toggleStatus(row) {
  await toggleSkuStatus(row.id)
  ElMessage.success('操作成功')
  fetchData()
}

async function handleSaveSku() {
  if (dialogType.value === 'add') await saveSku(currentSku.value)
  else await updateSku(currentSku.value)
  ElMessage.success('保存成功')
  dialogVisible.value = false
  fetchData()
}

async function loadCategories() {
  try { const res = await getCategoryList(); categories.value = res.data || [] } catch (e) {}
}
</script>
