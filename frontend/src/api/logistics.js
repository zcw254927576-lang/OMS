import request from '../utils/request'

export function getDeliveryPage(params) {
  return request.get('/logistics/delivery/page', { params })
}

export function delivery(data) {
  return request.post('/logistics/delivery', data)
}

export function batchDelivery(list) {
  return request.post('/logistics/delivery/batch', list)
}

export function getAfterSalePage(params) {
  return request.get('/logistics/after-sale/page', { params })
}

export function getAfterSaleDetail(id) {
  return request.get(`/logistics/after-sale/${id}`)
}

export function createAfterSale(data) {
  return request.post('/logistics/after-sale', data)
}

export function auditAfterSale(id, data) {
  return request.put(`/logistics/after-sale/${id}/audit`, data)
}
