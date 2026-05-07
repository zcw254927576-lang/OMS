<template>
  <el-card shadow="hover">
    <template #header>
      <div style="display: flex; justify-content: space-between">
        <span>客户等级</span>
        <el-button type="primary" size="small" @click="dialogVisible = true, current = {}">新增等级</el-button>
      </div>
    </template>
    <el-table :data="levels" stripe>
      <el-table-column prop="levelName" label="等级名称" />
      <el-table-column prop="minAmount" label="最低消费" align="right">
        <template #default="scope">¥{{ scope.row.minAmount }}</template>
      </el-table-column>
      <el-table-column prop="maxAmount" label="最高消费" align="right">
        <template #default="scope">¥{{ scope.row.maxAmount }}</template>
      </el-table-column>
      <el-table-column prop="discountRate" label="折扣率" align="center">
        <template #default="scope">{{ scope.row.discountRate }}%</template>
      </el-table-column>
      <el-table-column prop="description" label="描述" />
      <el-table-column label="操作" width="120">
        <template #default="scope">
          <el-button text type="primary" @click="edit(scope.row)">编辑</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="current.id ? '编辑等级' : '新增等级'" width="500px">
      <el-form :model="current" label-width="100px">
        <el-form-item label="等级名称" required>
          <el-input v-model="current.levelName" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="最低消费">
              <el-input-number v-model="current.minAmount" :min="0" :precision="2" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="最高消费">
              <el-input-number v-model="current.maxAmount" :min="0" :precision="2" style="width:100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="折扣率(%)">
          <el-input-number v-model="current.discountRate" :min="0" :max="100" :precision="2" style="width:100%" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="current.description" type="textarea" />
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
import { getLevelList, saveLevel } from '../../api/customer'
import { ElMessage } from 'element-plus'

const levels = ref([])
const dialogVisible = ref(false)
const current = ref({})

onMounted(() => fetchData())

async function fetchData() {
  try { const res = await getLevelList(); levels.value = res.data || [] } catch (e) {}
}

function edit(row) { current.value = { ...row }; dialogVisible.value = true }

async function save() {
  await saveLevel(current.value)
  ElMessage.success('保存成功')
  dialogVisible.value = false
  fetchData()
}
</script>
