<template>
  <el-card shadow="hover">
    <template #header>创建订单</template>
    <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">

      <el-divider>客户信息</el-divider>
      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item label="客户" prop="customerId">
            <el-select v-model="form.customerId" filterable remote :remote-method="searchCustomer" placeholder="搜索客户" style="width: 100%">
              <el-option v-for="c in customerOptions" :key="c.id" :label="c.customerName + ' (' + c.phone + ')'" :value="c.id" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="客户名称">
            <el-input v-model="form.customerName" />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="客户电话">
            <el-input v-model="form.customerPhone" />
          </el-form-item>
        </el-col>
      </el-row>

      <el-divider>收货信息</el-divider>
      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item label="收货人" prop="receiverName">
            <el-input v-model="form.receiverName" />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="电话" prop="receiverPhone">
            <el-input v-model="form.receiverPhone" />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="运费">
            <el-input-number v-model="form.shippingAmount" :min="0" :precision="2" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="6"><el-form-item label="省" prop="province"><el-input v-model="form.province" /></el-form-item></el-col>
        <el-col :span="6"><el-form-item label="市" prop="city"><el-input v-model="form.city" /></el-form-item></el-col>
        <el-col :span="6"><el-form-item label="区" prop="district"><el-input v-model="form.district" /></el-form-item></el-col>
      </el-row>
      <el-form-item label="详细地址" prop="detailAddress">
        <el-input v-model="form.detailAddress" type="textarea" :rows="2" />
      </el-form-item>

      <el-divider>订单商品</el-divider>
      <el-table :data="form.items" stripe>
        <el-table-column label="商品" width="300">
          <template #default="scope">
            <el-select v-model="scope.row.skuId" filterable remote :remote-method="searchSku" placeholder="搜索商品" style="width: 100%" @change="val => onSkuChange(scope.$index, val)">
              <el-option v-for="s in skuOptions" :key="s.id" :label="s.skuName + ' (' + s.skuCode + ')'" :value="s.id" />
            </el-select>
          </template>
        </el-table-column>
        <el-table-column label="单价">
          <template #default="scope">
            <el-input-number v-model="scope.row.unitPrice" :min="0" :precision="2" />
          </template>
        </el-table-column>
        <el-table-column label="数量" width="120">
          <template #default="scope">
            <el-input-number v-model="scope.row.quantity" :min="1" :max="9999" />
          </template>
        </el-table-column>
        <el-table-column label="小计" width="120">
          <template #default="scope">¥{{ (scope.row.unitPrice * scope.row.quantity).toFixed(2) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="80">
          <template #default="scope">
            <el-button text type="danger" @click="form.items.splice(scope.$index, 1)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div style="margin: 10px 0">
        <el-button @click="addItem">+ 添加商品</el-button>
      </div>

      <div style="text-align: right; font-size: 18px; padding: 10px">
        合计：¥{{ totalAmount }}
      </div>

      <el-divider />
      <el-form-item label="买家留言">
        <el-input v-model="form.buyerMessage" type="textarea" :rows="2" />
      </el-form-item>

      <div style="text-align: center; margin-top: 20px">
        <el-button type="primary" size="large" @click="handleSubmit" :loading="submitting">提交订单</el-button>
        <el-button size="large" @click="$router.back()">返回</el-button>
      </div>
    </el-form>
  </el-card>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { createOrder } from '../../api/order'
import { getCustomerPage } from '../../api/customer'
import { getSkuPage } from '../../api/product'
import { ElMessage } from 'element-plus'

const router = useRouter()
const formRef = ref()
const submitting = ref(false)
const customerOptions = ref([])
const skuOptions = ref([])

const form = reactive({
  customerId: null, customerName: '', customerPhone: '',
  receiverName: '', receiverPhone: '', province: '', city: '', district: '', detailAddress: '',
  shippingAmount: 0, buyerMessage: '',
  items: [{ skuId: null, skuCode: '', skuName: '', quantity: 1, unitPrice: 0 }]
})

const rules = {
  customerId: [{ required: true, message: '请选择客户' }],
  receiverName: [{ required: true, message: '请输入收货人' }],
  receiverPhone: [{ required: true, message: '请输入电话' }],
  detailAddress: [{ required: true, message: '请输入地址' }]
}

const totalAmount = computed(() => {
  return form.items.reduce((sum, item) => sum + (item.unitPrice || 0) * (item.quantity || 0), 0).toFixed(2)
})

function addItem() {
  form.items.push({ skuId: null, skuCode: '', skuName: '', quantity: 1, unitPrice: 0 })
}

async function searchCustomer(query) {
  if (!query) return
  const res = await getCustomerPage({ keyword: query, page: 1, pageSize: 20 })
  customerOptions.value = res.data.records || []
}

async function searchSku(query) {
  if (!query) return
  const res = await getSkuPage({ keyword: query, page: 1, pageSize: 20 })
  skuOptions.value = res.data.records || []
}

function onSkuChange(index, skuId) {
  const sku = skuOptions.value.find(s => s.id === skuId)
  if (sku) {
    form.items[index].skuCode = sku.skuCode
    form.items[index].skuName = sku.skuName
    form.items[index].unitPrice = sku.price
  }
}

async function handleSubmit() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  if (form.items.length === 0 || form.items.some(i => !i.skuId)) {
    ElMessage.warning('请添加完整商品信息')
    return
  }
  submitting.value = true
  try {
    await createOrder(form)
    ElMessage.success('创建成功')
    router.push('/order/list')
  } catch (e) {} finally {
    submitting.value = false
  }
}
</script>
