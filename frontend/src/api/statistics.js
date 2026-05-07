import request from '../utils/request'

export function getDashboard() {
  return request.get('/statistics/dashboard')
}

export function getOrderStatusStats() {
  return request.get('/statistics/order-status')
}

export function getDailyOrderStats() {
  return request.get('/statistics/daily-order')
}

export function getMonthlySales() {
  return request.get('/statistics/monthly-sales')
}

export function getProductRanking(limit = 20) {
  return request.get('/statistics/product-ranking', { params: { limit } })
}

export function getStockTurnover() {
  return request.get('/statistics/stock-turnover')
}
