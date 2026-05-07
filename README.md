# 企业订单管理系统 (OMS)

基于 Spring Boot 3 + MyBatis-Plus + MySQL 8.0 + Vue 3 + Element Plus 的企业级订单管理系统。

## 功能模块

| 模块 | 功能 |
|------|------|
| **订单管理** | 创建/编辑/查询/删除订单、订单状态流转、拆单合单、Excel导入导出、操作日志 |
| **商品管理** | SKU维护、商品分类、上下架管理 |
| **库存管理** | 实时库存、库存预警、库存调整、库存盘点、变动日志 |
| **客户管理** | 客户信息维护、等级管理、消费统计、客户排行 |
| **物流管理** | 发货管理、快递模板、售后/退货管理 |
| **系统管理** | 用户/角色/菜单权限管理、操作日志审计 |
| **数据统计** | 订单统计、商品销量排行、客户复购率分析、报表导出 |

## 技术栈

**后端：**
- Java 17 + Spring Boot 3.2
- MyBatis-Plus 3.5.5
- MySQL 8.0 + HikariCP 连接池
- Spring Security + JWT 认证
- EasyExcel 导入导出
- Lombok / FastJSON

**前端：**
- Vue 3 + Composition API
- Vite 5
- Element Plus 2.5
- Pinia 状态管理
- Vue Router 4
- ECharts 图表
- Axios HTTP 客户端

## 快速启动

### 方式一：本地运行

#### 1. 数据库初始化
```sql
-- 执行 SQL 初始化脚本
mysql -u root -p < sql/init.sql
```

#### 2. 启动后端
```bash
cd backend
mvn clean package -DskipTests
java -jar target/oms-backend-1.0.0.jar
```
后端启动在 http://localhost:8080

#### 3. 启动前端
```bash
cd frontend
npm install
npm run dev
```
前端启动在 http://localhost:3000

### 方式二：Docker 部署
```bash
cd docker
docker-compose up -d
```
- 前端: http://localhost:3000
- 后端: http://localhost:8080
- 数据库: localhost:3306

## 默认账号

| 用户名 | 密码 | 角色 |
|--------|------|------|
| admin | admin123 | 系统管理员 |

## 项目结构

```
oms/
├── sql/init.sql                  # 数据库初始化脚本
├── docker/docker-compose.yml     # Docker 部署配置
├── backend/                      # 后端 Spring Boot 项目
│   ├── src/main/java/com/oms/
│   │   ├── OmsApplication.java       # 启动类
│   │   ├── config/                    # 配置类（安全、跨域、JWT、MyBatis-Plus）
│   │   ├── common/                    # 公共类（响应封装、分页、枚举）
│   │   ├── exception/                 # 全局异常处理
│   │   ├── entity/                    # 实体类
│   │   ├── mapper/                    # MyBatis Mapper
│   │   ├── service/                   # 服务接口与实现
│   │   ├── controller/                # REST 控制器
│   │   └── dto/                       # 数据传输对象
│   └── src/main/resources/
│       ├── application.yml            # 应用配置
│       └── mapper/                    # XML Mapper
├── frontend/                      # 前端 Vue 3 项目
│   └── src/
│       ├── api/                        # API 接口模块
│       ├── views/                      # 页面组件
│       ├── router/                     # 路由配置
│       ├── store/                      # Pinia 状态管理
│       ├── components/                 # 公共组件
│       └── utils/                      # 工具函数
└── README.md
```

## API 接口

### 认证
- `POST /api/auth/login` - 登录
- `GET /api/auth/user-info` - 获取用户信息与菜单

### 订单管理
- `POST /api/order/page` - 分页查询订单
- `GET /api/order/{id}` - 订单详情
- `GET /api/order/{id}/items` - 订单明细
- `GET /api/order/{id}/logs` - 操作日志
- `POST /api/order` - 创建订单
- `PUT /api/order/{id}/status` - 更新状态
- `PUT /api/order/{id}/cancel` - 取消订单
- `DELETE /api/order/{id}` - 删除订单
- `POST /api/order/{id}/split` - 拆单
- `POST /api/order/merge` - 合单
- `POST /api/order/export` - 导出Excel

### 商品管理
- `GET /api/product/sku/page` - SKU分页
- `POST /api/product/sku` - 新增SKU
- `PUT /api/product/sku/{id}/toggle-status` - 上下架

### 库存管理
- `GET /api/inventory/page` - 库存查询
- `GET /api/inventory/low-stock` - 库存预警
- `POST /api/inventory/adjust` - 库存调整

### 客户管理
- `GET /api/customer/page` - 客户分页
- `POST /api/customer` - 新增客户
- `GET /api/customer/{id}/stats` - 客户统计

### 物流管理
- `POST /api/logistics/delivery` - 发货
- `POST /api/logistics/after-sale` - 创建售后
- `PUT /api/logistics/after-sale/{id}/audit` - 审核售后

### 统计报表
- `GET /api/statistics/dashboard` - 仪表盘
- `GET /api/statistics/order-status` - 状态分布
- `GET /api/statistics/daily-order` - 每日订单
- `GET /api/statistics/monthly-sales` - 月度销售
- `GET /api/statistics/product-ranking` - 商品排行

## 关键设计

### 库存一致性
订单创建时通过 `SELECT ... FOR UPDATE` 锁定库存行，使用乐观锁机制保证并发安全，防止超卖。

### 事务控制
订单创建、库存扣减、状态变更等关键操作使用 `@Transactional` 保证原子性，任一环节失败自动回滚。

### 数据权限
基于 Spring Security + JWT 实现认证鉴权，支持按钮级权限控制。

### 扩展接口
预留了 ERP、WMS、第三方物流系统的对接接口，方便后续集成。
