import request from '../utils/request'

export function getInventoryPage(params) {
  return request.get('/inventory/page', { params })
}

export function getLowStockList() {
  return request.get('/inventory/low-stock')
}

export function adjustStock(data) {
  return request.post('/inventory/adjust', data)
}

export function getInventoryLog(params) {
  return request.get('/inventory/log', { params })
}
