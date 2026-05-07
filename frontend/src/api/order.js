import request from '../utils/request'

export function getOrderPage(data) {
  return request.post('/order/page', data)
}

export function getOrderDetail(id) {
  return request.get(`/order/${id}`)
}

export function getOrderItems(id) {
  return request.get(`/order/${id}/items`)
}

export function getOrderLogs(id) {
  return request.get(`/order/${id}/logs`)
}

export function createOrder(data) {
  return request.post('/order', data)
}

export function updateOrderStatus(id, data) {
  return request.put(`/order/${id}/status`, data)
}

export function cancelOrder(id, data) {
  return request.put(`/order/${id}/cancel`, data)
}

export function deleteOrder(id) {
  return request.delete(`/order/${id}`)
}

export function splitOrder(id, groups) {
  return request.post(`/order/${id}/split`, groups)
}

export function mergeOrders(orderIds) {
  return request.post('/order/merge', orderIds)
}

export function exportOrder(data) {
  return request.post('/order/export', data, { responseType: 'blob' })
}
