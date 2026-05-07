<template>
  <el-card shadow="hover">
    <template #header>
      <div style="display: flex; justify-content: space-between">
        <span>用户管理</span>
        <el-button type="primary" size="small" @click="dialogVisible = true, current = {}">新增用户</el-button>
      </div>
    </template>
    <el-table :data="list" stripe v-loading="loading">
      <el-table-column prop="username" label="用户名" width="120" />
      <el-table-column prop="realName" label="姓名" width="120" />
      <el-table-column prop="email" label="邮箱" />
      <el-table-column prop="phone" label="电话" width="130" />
      <el-table-column prop="status" label="状态" width="80" align="center">
        <template #default="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'" size="small">{{ scope.row.status === 1 ? '启用' : '禁用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="170" />
      <el-table-column label="操作" width="180">
        <template #default="scope">
          <el-button text type="primary" @click="edit(scope.row)">编辑</el-button>
          <el-button text type="warning" @click="handleResetPwd(scope.row.id)">重置密码</el-button>
        </template>
      </el-table-column>
    </el-table>
    <div style="margin-top: 16px; display: flex; justify-content: flex-end">
      <el-pagination v-model:current-page="page" v-model:page-size="pageSize" :total="total" layout="total, prev, pager, next" @change="fetchData" />
    </div>

    <el-dialog v-model="dialogVisible" :title="current.id ? '编辑用户' : '新增用户'" width="500px">
      <el-form :model="current" label-width="100px">
        <el-form-item label="用户名" required>
          <el-input v-model="current.username" :disabled="!!current.id" />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="current.realName" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="current.email" />
        </el-form-item>
        <el-form-item label="电话">
          <el-input v-model="current.phone" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="current.status" :active-value="1" :inactive-value="0" />
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
import { getUserPage, saveUser, updateUser, resetPassword } from '../../api/system'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const list = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(20)
const dialogVisible = ref(false)
const current = ref({})

onMounted(() => fetchData())

async function fetchData() {
  loading.value = true
  try {
    const res = await getUserPage({ page: page.value, pageSize: pageSize.value })
    list.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (e) {} finally { loading.value = false }
}

function edit(row) { current.value = { ...row }; dialogVisible.value = true }

async function save() {
  if (current.value.id) await updateUser(current.value)
  else await saveUser(current.value)
  ElMessage.success('保存成功')
  dialogVisible.value = false
  fetchData()
}

async function handleResetPwd(userId) {
  try {
    await ElMessageBox.confirm('确定重置密码为 123456？')
    await resetPassword(userId)
    ElMessage.success('已重置为 123456')
  } catch (e) {}
}
</script>
