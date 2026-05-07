import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '',
    component: () => import('../components/Layout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('../views/dashboard/Index.vue'),
        meta: { title: '仪表盘' }
      },
      // 订单管理
      {
        path: 'order/list',
        name: 'OrderList',
        component: () => import('../views/order/List.vue'),
        meta: { title: '订单列表' }
      },
      {
        path: 'order/add',
        name: 'OrderAdd',
        component: () => import('../views/order/Add.vue'),
        meta: { title: '创建订单' }
      },
      {
        path: 'order/detail/:id',
        name: 'OrderDetail',
        component: () => import('../views/order/Detail.vue'),
        meta: { title: '订单详情' }
      },
      // 商品管理
      {
        path: 'product/sku',
        name: 'ProductSku',
        component: () => import('../views/product/Sku.vue'),
        meta: { title: '商品SKU' }
      },
      {
        path: 'product/category',
        name: 'ProductCategory',
        component: () => import('../views/product/Category.vue'),
        meta: { title: '商品分类' }
      },
      // 库存管理
      {
        path: 'inventory/current',
        name: 'InventoryCurrent',
        component: () => import('../views/inventory/Current.vue'),
        meta: { title: '实时库存' }
      },
      // 客户管理
      {
        path: 'customer/list',
        name: 'CustomerList',
        component: () => import('../views/customer/List.vue'),
        meta: { title: '客户列表' }
      },
      {
        path: 'customer/level',
        name: 'CustomerLevel',
        component: () => import('../views/customer/Level.vue'),
        meta: { title: '客户等级' }
      },
      // 物流管理
      {
        path: 'logistics/delivery',
        name: 'DeliveryList',
        component: () => import('../views/logistics/Delivery.vue'),
        meta: { title: '发货管理' }
      },
      {
        path: 'logistics/after-sale',
        name: 'AfterSale',
        component: () => import('../views/logistics/AfterSale.vue'),
        meta: { title: '售后管理' }
      },
      // 采购管理
      {
        path: 'purchase/list',
        name: 'PurchaseList',
        component: () => import('../views/purchase/List.vue'),
        meta: { title: '采购订单' }
      },
      {
        path: 'purchase/add',
        name: 'PurchaseAdd',
        component: () => import('../views/purchase/Add.vue'),
        meta: { title: '新增采购单' }
      },
      {
        path: 'purchase/detail/:id',
        name: 'PurchaseDetail',
        component: () => import('../views/purchase/Detail.vue'),
        meta: { title: '采购单详情' }
      },
      // 系统管理
      {
        path: 'system/user',
        name: 'SysUser',
        component: () => import('../views/system/User.vue'),
        meta: { title: '用户管理' }
      },
      {
        path: 'system/role',
        name: 'SysRole',
        component: () => import('../views/system/Role.vue'),
        meta: { title: '角色管理' }
      },
      {
        path: 'system/log',
        name: 'SysLog',
        component: () => import('../views/system/Log.vue'),
        meta: { title: '操作日志' }
      },
      // 数据统计
      {
        path: 'statistics/order',
        name: 'StatsOrder',
        component: () => import('../views/statistics/Order.vue'),
        meta: { title: '订单统计' }
      },
      {
        path: 'statistics/product',
        name: 'StatsProduct',
        component: () => import('../views/statistics/Product.vue'),
        meta: { title: '商品统计' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
export { router, routes }
