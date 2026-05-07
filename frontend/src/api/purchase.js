import request from '../utils/request'

export function getPurchasePage(data) {
  return request.post('/purchase/page', data)
}

export function getPurchaseDetail(id) {
  return request.get(`/purchase/${id}`)
}

export function getPurchaseItems(id) {
  return request.get(`/purchase/${id}/items`)
}

export function createPurchase(data) {
  return request.post('/purchase', data)
}

export function submitPurchase(id) {
  return request.put(`/purchase/${id}/submit`)
}

export function approvePurchase(id) {
  return request.put(`/purchase/${id}/approve`)
}

export function warehousePurchase(id) {
  return request.put(`/purchase/${id}/warehouse`)
}
