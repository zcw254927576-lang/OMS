import request from '../utils/request'

export function getSkuPage(params) {
  return request.get('/product/sku/page', { params })
}

export function getSkuDetail(id) {
  return request.get(`/product/sku/${id}`)
}

export function saveSku(data) {
  return request.post('/product/sku', data)
}

export function updateSku(data) {
  return request.put('/product/sku', data)
}

export function toggleSkuStatus(id) {
  return request.put(`/product/sku/${id}/toggle-status`)
}

export function getCategoryList() {
  return request.get('/product/category')
}

export function saveCategory(data) {
  return request.post('/product/category', data)
}

export function updateCategory(data) {
  return request.put('/product/category', data)
}

export function deleteCategory(id) {
  return request.delete(`/product/category/${id}`)
}
