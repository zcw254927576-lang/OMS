import request from '../utils/request'

export function getCustomerPage(params) {
  return request.get('/customer/page', { params })
}

export function getCustomerDetail(id) {
  return request.get(`/customer/${id}`)
}

export function saveCustomer(data) {
  return request.post('/customer', data)
}

export function updateCustomer(data) {
  return request.put('/customer', data)
}

export function getCustomerStats(id) {
  return request.get(`/customer/${id}/stats`)
}

export function getCustomerRanking(limit = 10) {
  return request.get('/customer/ranking', { params: { limit } })
}

export function getRepurchaseStats() {
  return request.get('/customer/repurchase')
}

export function getLevelList() {
  return request.get('/customer/level/list')
}

export function saveLevel(data) {
  return request.post('/customer/level', data)
}
