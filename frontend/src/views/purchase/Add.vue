<template>
  <div>
    <el-card shadow="hover">
      <template #header>
        <span style="font-weight: bold">新增采购单</span>
      </template>
      <el-form :model="form" ref="formRef" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="供应商">
              <el-input v-model="form.supplierName" placeholder="供应商名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="备注">
              <el-input v-model="form.remark" placeholder="备注" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider>采购商品</el-divider>

        <el-table :data="form.items" border style="margin-bottom: 16px">
          <el-table-column label="商品名称" width="250">
            <template #default="{ row, $index }">
              <el-input v-model="row.skuName" placeholder="商品名称" />
            </template>
          </el-table-column>
          <el-table-column label="数量" width="150">
            <template #default="{ row, $index }">
              <el-input-number v-model="row.quantity" :min="1" :max="99999" style="width: 120px" />
            </template>
          </el-table-column>
          <el-table-column label="单价" width="150">
            <template #default="{ row, $index }">
              <el-input-number v-model="row.unitPrice" :min="0" :precision="2" style="width: 120px" />
            </template>
          </el-table-column>
          <el-table-column label="小计" width="120" align="right">
            <template #default="{ row }">¥{{ ((row.quantity||0) * (row.unitPrice||0)).toFixed(2) }}</template>
          </el-table-column>
          <el-table-column label="操作" width="80">
            <template #default="{ $index }">
              <el-button text type="danger" @click="form.items.splice($index, 1)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <el-button @click="addItem" type="primary" plain>+ 添加商品</el-button>

        <div style="text-align: right; font-size: 18px; margin-top: 16px; padding-right: 40px">
          合计：<strong style="color: #f56c6c">¥{{ totalAmount.toFixed(2) }}</strong>
        </div>

        <div style="margin-top: 24px; text-align: center">
          <el-button @click="$router.push('/purchase/list')">取消</el-button>
          <el-button type="primary" @click="handleSave">保存</el-button>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { createPurchase } from '../../api/purchase'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'

const router = useRouter()
const formRef = ref(null)

const form = reactive({
  supplierName: '',
  remark: '',
  items: []
})

function addItem() {
  form.items.push({ skuName: '', quantity: 1, unitPrice: 0 })
}

const totalAmount = computed(() => {
  return form.items.reduce((sum, item) => sum + (item.quantity || 0) * (item.unitPrice || 0), 0)
})

async function handleSave() {
  if (form.items.length === 0) { ElMessage.warning('请至少添加一个商品'); return }
  for (const item of form.items) {
    if (!item.skuName) { ElMessage.warning('请填写商品名称'); return }
  }
  try {
    await createPurchase(form)
    ElMessage.success('创建成功')
    router.push('/purchase/list')
  } catch (e) {}
}
</script>
