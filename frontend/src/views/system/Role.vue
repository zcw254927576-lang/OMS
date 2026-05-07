<template>
  <el-card shadow="hover">
    <template #header>
      <div style="display: flex; justify-content: space-between">
        <span>角色管理</span>
        <el-button type="primary" size="small" @click="dialogVisible = true, current = {}">新增角色</el-button>
      </div>
    </template>
    <el-table :data="roles" stripe>
      <el-table-column prop="roleName" label="角色名称" width="150" />
      <el-table-column prop="roleCode" label="角色编码" width="150" />
      <el-table-column prop="description" label="描述" />
      <el-table-column prop="status" label="状态" width="80" align="center">
        <template #default="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'" size="small">{{ scope.row.status === 1 ? '启用' : '禁用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120">
        <template #default="scope">
          <el-button text type="primary" @click="edit(scope.row)">编辑</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="current.id ? '编辑角色' : '新增角色'" width="500px">
      <el-form :model="current" label-width="100px">
        <el-form-item label="角色名称" required><el-input v-model="current.roleName" /></el-form-item>
        <el-form-item label="角色编码" required><el-input v-model="current.roleCode" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="current.description" type="textarea" /></el-form-item>
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
import { getRoleList, saveRole } from '../../api/system'
import { ElMessage } from 'element-plus'

const roles = ref([])
const dialogVisible = ref(false)
const current = ref({})

onMounted(() => fetchData())

async function fetchData() {
  try { const res = await getRoleList(); roles.value = res.data || [] } catch (e) {}
}

function edit(row) { current.value = { ...row }; dialogVisible.value = true }

async function save() {
  await saveRole(current.value)
  ElMessage.success('保存成功')
  dialogVisible.value = false
  fetchData()
}
</script>
