<template>
  <el-card shadow="hover">
    <template #header>
      <div style="display: flex; justify-content: space-between">
        <span>商品分类</span>
        <el-button type="primary" size="small" @click="dialogVisible = true, current = {}">新增分类</el-button>
      </div>
    </template>
    <el-table :data="categories" stripe v-loading="loading">
      <el-table-column prop="categoryName" label="分类名称" />
      <el-table-column prop="sort" label="排序" width="80" align="center" />
      <el-table-column prop="status" label="状态" width="80" align="center">
        <template #default="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'info'" size="small">{{ scope.row.status === 1 ? '启用' : '禁用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="160">
        <template #default="scope">
          <el-button text type="primary" @click="edit(scope.row)">编辑</el-button>
          <el-button text type="danger" @click="handleDelete(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="current.id ? '编辑分类' : '新增分类'" width="500px">
      <el-form :model="current" label-width="100px">
        <el-form-item label="名称" required>
          <el-input v-model="current.categoryName" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="current.sort" :min="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getCategoryList, saveCategory, updateCategory, deleteCategory } from '../../api/product'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const categories = ref([])
const dialogVisible = ref(false)
const current = ref({})

onMounted(() => fetchData())

async function fetchData() {
  loading.value = true
  try { const res = await getCategoryList(); categories.value = res.data || [] }
  catch (e) {} finally { loading.value = false }
}

function edit(row) { current.value = { ...row }; dialogVisible.value = true }

async function save() {
  if (current.value.id) await updateCategory(current.value)
  else await saveCategory(current.value)
  ElMessage.success('保存成功')
  dialogVisible.value = false
  fetchData()
}

async function handleDelete(id) {
  try {
    await ElMessageBox.confirm('确定删除？')
    await deleteCategory(id)
    ElMessage.success('已删除')
    fetchData()
  } catch (e) {}
}
</script>
