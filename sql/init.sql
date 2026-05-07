-- ============================================
-- OMS 订单管理系统 数据库初始化脚本
-- MySQL 8.0+
-- ============================================

CREATE DATABASE IF NOT EXISTS oms DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE oms;

-- ---------- 系统管理 ----------
-- 用户表
CREATE TABLE sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    real_name VARCHAR(50),
    email VARCHAR(100),
    phone VARCHAR(20),
    avatar VARCHAR(255),
    status TINYINT DEFAULT 1 COMMENT '1-启用 0-禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username(username),
    INDEX idx_status(status)
) COMMENT '系统用户';

-- 角色表
CREATE TABLE sys_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL,
    role_code VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    status TINYINT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_role_code(role_code)
) COMMENT '角色';

-- 菜单表
CREATE TABLE sys_menu (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    menu_name VARCHAR(100) NOT NULL,
    parent_id BIGINT DEFAULT 0,
    menu_type TINYINT DEFAULT 1 COMMENT '1-目录 2-菜单 3-按钮',
    path VARCHAR(255),
    component VARCHAR(255),
    perms VARCHAR(255) COMMENT '权限标识',
    icon VARCHAR(100),
    sort INT DEFAULT 0,
    status TINYINT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_parent_id(parent_id),
    INDEX idx_status(status)
) COMMENT '菜单';

-- 角色菜单关联
CREATE TABLE sys_role_menu (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_id BIGINT NOT NULL,
    menu_id BIGINT NOT NULL,
    UNIQUE KEY uk_role_menu(role_id, menu_id)
) COMMENT '角色菜单关联';

-- 用户角色关联
CREATE TABLE sys_user_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    UNIQUE KEY uk_user_role(user_id, role_id)
) COMMENT '用户角色关联';

-- 操作日志
CREATE TABLE sys_operation_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    username VARCHAR(50),
    module VARCHAR(50) COMMENT '功能模块',
    operation VARCHAR(100) COMMENT '操作描述',
    request_method VARCHAR(10),
    request_url VARCHAR(255),
    request_params TEXT,
    execution_time INT COMMENT '耗时(ms)',
    ip VARCHAR(50),
    status TINYINT DEFAULT 1 COMMENT '1-成功 0-失败',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id(user_id),
    INDEX idx_module(module),
    INDEX idx_create_time(create_time)
) COMMENT '操作日志';

-- ---------- 客户管理 ----------
-- 客户等级
CREATE TABLE oms_customer_level (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    level_name VARCHAR(50) NOT NULL,
    min_amount DECIMAL(12,2) DEFAULT 0,
    max_amount DECIMAL(12,2) DEFAULT 99999999.99,
    discount_rate DECIMAL(5,2) DEFAULT 100.00 COMMENT '折扣率%',
    description VARCHAR(255),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT '客户等级';

-- 客户表
CREATE TABLE oms_customer (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_no VARCHAR(50) NOT NULL UNIQUE,
    customer_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    email VARCHAR(100),
    gender TINYINT DEFAULT 0 COMMENT '0-未知 1-男 2-女',
    birth_date DATE,
    province VARCHAR(50),
    city VARCHAR(50),
    district VARCHAR(50),
    detail_address VARCHAR(255),
    level_id BIGINT,
    level_name VARCHAR(50),
    total_points BIGINT DEFAULT 0,
    available_points BIGINT DEFAULT 0,
    total_amount DECIMAL(14,2) DEFAULT 0.00,
    order_count INT DEFAULT 0,
    last_order_time DATETIME,
    tag VARCHAR(255) COMMENT '客户标签',
    source VARCHAR(50) COMMENT '来源',
    status TINYINT DEFAULT 1,
    remark VARCHAR(500),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_customer_no(customer_no),
    INDEX idx_phone(phone),
    INDEX idx_level_id(level_id),
    INDEX idx_status(status),
    INDEX idx_create_time(create_time)
) COMMENT '客户';

-- 客户地址
CREATE TABLE oms_customer_address (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    receiver_name VARCHAR(50) NOT NULL,
    receiver_phone VARCHAR(20) NOT NULL,
    province VARCHAR(50),
    city VARCHAR(50),
    district VARCHAR(50),
    detail_address VARCHAR(255) NOT NULL,
    is_default TINYINT DEFAULT 0,
    label VARCHAR(20) COMMENT '标签：家/公司',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_customer_id(customer_id)
) COMMENT '客户地址';

-- ---------- 商品管理 ----------
-- 商品分类
CREATE TABLE oms_product_category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    category_name VARCHAR(100) NOT NULL,
    parent_id BIGINT DEFAULT 0,
    sort INT DEFAULT 0,
    status TINYINT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_parent_id(parent_id),
    INDEX idx_status(status)
) COMMENT '商品分类';

-- 商品SKU
CREATE TABLE oms_product_sku (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sku_code VARCHAR(50) NOT NULL UNIQUE,
    sku_name VARCHAR(200) NOT NULL,
    category_id BIGINT,
    category_name VARCHAR(100),
    brand VARCHAR(100),
    specs JSON COMMENT '规格JSON',
    unit VARCHAR(20) COMMENT '单位',
    price DECIMAL(12,2) NOT NULL COMMENT '销售价',
    cost_price DECIMAL(12,2) DEFAULT 0.00 COMMENT '成本价',
    status TINYINT DEFAULT 1 COMMENT '1-上架 0-下架',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_sku_code(sku_code),
    INDEX idx_category_id(category_id),
    INDEX idx_status(status),
    INDEX idx_create_time(create_time)
) COMMENT '商品SKU';

-- ---------- 库存管理 ----------
CREATE TABLE oms_warehouse (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    warehouse_name VARCHAR(100) NOT NULL,
    warehouse_code VARCHAR(50) NOT NULL UNIQUE,
    contact_person VARCHAR(50),
    contact_phone VARCHAR(20),
    province VARCHAR(50),
    city VARCHAR(50),
    district VARCHAR(50),
    detail_address VARCHAR(255),
    status TINYINT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT '仓库';

-- 实时库存
CREATE TABLE oms_inventory (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sku_id BIGINT NOT NULL,
    sku_code VARCHAR(50) NOT NULL,
    sku_name VARCHAR(200) NOT NULL,
    warehouse_id BIGINT,
    warehouse_name VARCHAR(100),
    quantity INT DEFAULT 0 COMMENT '总库存',
    locked_quantity INT DEFAULT 0 COMMENT '锁定库存',
    available_quantity INT DEFAULT 0 COMMENT '可用库存',
    min_stock INT DEFAULT 0 COMMENT '最低库存预警',
    max_stock INT DEFAULT 999999 COMMENT '最高库存',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_sku_warehouse(sku_id, warehouse_id),
    INDEX idx_sku_code(sku_code),
    INDEX idx_warehouse_id(warehouse_id),
    INDEX idx_available_quantity(available_quantity)
) COMMENT '实时库存';

-- 库存变动日志
CREATE TABLE oms_inventory_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sku_id BIGINT,
    sku_code VARCHAR(50),
    change_type VARCHAR(30) COMMENT 'IN-入库 OUT-出库 ADJUST-调整 RETURN-退货 ORDER_LOCK-下单锁定 ORDER_UNLOCK-解锁',
    quantity INT COMMENT '变动数量(正增负减)',
    before_quantity INT,
    after_quantity INT,
    related_no VARCHAR(100) COMMENT '关联单号',
    operator VARCHAR(50),
    remark VARCHAR(255),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_sku_code(sku_code),
    INDEX idx_related_no(related_no),
    INDEX idx_change_type(change_type),
    INDEX idx_create_time(create_time)
) COMMENT '库存变动日志';

-- 库存盘点
CREATE TABLE oms_stock_take (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    take_no VARCHAR(50) NOT NULL UNIQUE,
    warehouse_id BIGINT,
    warehouse_name VARCHAR(100),
    status TINYINT DEFAULT 0 COMMENT '0-进行中 1-已完成',
    operator VARCHAR(50),
    total_sku INT DEFAULT 0,
    matched_sku INT DEFAULT 0,
    profit_sku INT DEFAULT 0 COMMENT '盘盈SKU数',
    loss_sku INT DEFAULT 0 COMMENT '盘亏SKU数',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    complete_time DATETIME,
    remark VARCHAR(500),
    INDEX idx_take_no(take_no),
    INDEX idx_status(status)
) COMMENT '库存盘点';

-- 盘点明细
CREATE TABLE oms_stock_take_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    take_id BIGINT NOT NULL,
    sku_id BIGINT,
    sku_code VARCHAR(50),
    sku_name VARCHAR(200),
    book_quantity INT COMMENT '账面数量',
    actual_quantity INT COMMENT '实际数量',
    diff_quantity INT COMMENT '差异数量',
    remark VARCHAR(255),
    INDEX idx_take_id(take_id)
) COMMENT '盘点明细';

-- ---------- 订单管理 ----------
CREATE TABLE oms_order (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_no VARCHAR(50) NOT NULL UNIQUE COMMENT '订单号',
    customer_id BIGINT,
    customer_name VARCHAR(100),
    customer_phone VARCHAR(20),
    -- 金额
    total_amount DECIMAL(12,2) NOT NULL DEFAULT 0.00 COMMENT '商品总额',
    discount_amount DECIMAL(12,2) DEFAULT 0.00 COMMENT '折扣金额',
    shipping_amount DECIMAL(12,2) DEFAULT 0.00 COMMENT '运费',
    payment_amount DECIMAL(12,2) DEFAULT 0.00 COMMENT '实付金额',
    paid_amount DECIMAL(12,2) DEFAULT 0.00 COMMENT '已付金额',
    -- 状态
    order_status VARCHAR(30) NOT NULL DEFAULT 'PENDING_AUDIT' COMMENT 'PENDING_AUDIT/PAID/PENDING_DELIVERY/DELIVERED/RECEIVED/COMPLETED/CANCELLED/AFTER_SALE',
    payment_method VARCHAR(30) COMMENT '余额/微信/支付宝/银行转账',
    payment_time DATETIME,
    -- 收货信息
    receiver_name VARCHAR(50),
    receiver_phone VARCHAR(20),
    province VARCHAR(50),
    city VARCHAR(50),
    district VARCHAR(50),
    detail_address VARCHAR(255),
    -- 物流
    express_company VARCHAR(50),
    express_no VARCHAR(50),
    delivery_time DATETIME,
    received_time DATETIME,
    finish_time DATETIME,
    cancel_time DATETIME,
    cancel_reason VARCHAR(500),
    -- 备注
    seller_message VARCHAR(500) COMMENT '卖家备注',
    buyer_message VARCHAR(500) COMMENT '买家留言',
    -- 审计
    create_by VARCHAR(50),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    remark VARCHAR(500),
    INDEX idx_order_no(order_no),
    INDEX idx_customer_id(customer_id),
    INDEX idx_order_status(order_status),
    INDEX idx_create_time(create_time),
    INDEX idx_express_no(express_no),
    INDEX idx_customer_phone(customer_phone),
    INDEX idx_deleted(deleted)
) COMMENT '订单';

-- 订单明细
CREATE TABLE oms_order_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    sku_id BIGINT,
    sku_code VARCHAR(50),
    sku_name VARCHAR(200),
    sku_spec VARCHAR(255) COMMENT '规格描述',
    category_name VARCHAR(100),
    quantity INT NOT NULL,
    unit_price DECIMAL(12,2) NOT NULL,
    total_price DECIMAL(12,2) NOT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_order_id(order_id),
    INDEX idx_sku_id(sku_id)
) COMMENT '订单明细';

-- 订单日志
CREATE TABLE oms_order_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT,
    order_no VARCHAR(50),
    operation_type VARCHAR(50) COMMENT 'CREATE/AUDIT/PAY/DELIVERY/RECEIVE/CANCEL/MERGE/SPLIT',
    before_status VARCHAR(30),
    after_status VARCHAR(30),
    operator VARCHAR(50),
    operation_content VARCHAR(500),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_order_id(order_id),
    INDEX idx_order_no(order_no),
    INDEX idx_create_time(create_time)
) COMMENT '订单日志';

-- 拆合单记录
CREATE TABLE oms_order_merge (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    merge_no VARCHAR(50) NOT NULL UNIQUE COMMENT '合单号',
    source_order_nos JSON COMMENT '源订单号列表',
    target_order_no VARCHAR(50) COMMENT '目标订单号',
    merge_type VARCHAR(20) COMMENT 'MERGE-合单 SPLIT-拆单',
    operator VARCHAR(50),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_merge_no(merge_no)
) COMMENT '拆合单记录';

-- ---------- 物流售后 ----------
-- 快递模板
CREATE TABLE oms_shipping_template (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    template_name VARCHAR(100) NOT NULL,
    delivery_method VARCHAR(30) DEFAULT 'express' COMMENT '快递/EMS/平邮',
    -- 按重量计费
    first_weight DECIMAL(10,2) DEFAULT 0 COMMENT '首重(kg)',
    first_weight_fee DECIMAL(10,2) DEFAULT 0,
   additional_weight DECIMAL(10,2) DEFAULT 0 COMMENT '续重(kg)',
   additional_weight_fee DECIMAL(10,2) DEFAULT 0 COMMENT '续重费用',
    -- 按件计费
    first_piece INT DEFAULT 0,
    first_piece_fee DECIMAL(10,2) DEFAULT 0,
   additional_piece INT DEFAULT 0 COMMENT '续件(件)',
   additional_piece_fee DECIMAL(10,2) DEFAULT 0 COMMENT '续件费用',
    -- 包邮条件
    free_amount DECIMAL(10,2) DEFAULT NULL COMMENT '满额包邮',
    -- 配送区域
    delivery_area VARCHAR(500) COMMENT '适用区域',
    status TINYINT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT '快递模板';

-- 发货单
CREATE TABLE oms_delivery_order (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    delivery_no VARCHAR(50) NOT NULL UNIQUE COMMENT '发货单号',
    order_no VARCHAR(50) NOT NULL,
    order_id BIGINT NOT NULL,
    express_company VARCHAR(50),
    express_no VARCHAR(50),
    -- 发货人
    sender_name VARCHAR(50),
    sender_phone VARCHAR(20),
    sender_address VARCHAR(255),
    -- 收货人
    receiver_name VARCHAR(50),
    receiver_phone VARCHAR(20),
    receiver_address VARCHAR(255),
    -- 商品
    total_quantity INT DEFAULT 0,
    total_weight DECIMAL(10,2) DEFAULT 0,
    -- 状态
    status VARCHAR(30) DEFAULT 'PENDING' COMMENT 'PENDING-待发货 DELIVERED-已发货',
    operator VARCHAR(50),
    delivery_time DATETIME,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_delivery_no(delivery_no),
    INDEX idx_order_no(order_no),
    INDEX idx_express_no(express_no),
    INDEX idx_status(status)
) COMMENT '发货单';

-- 发货明细
CREATE TABLE oms_delivery_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    delivery_id BIGINT NOT NULL,
    sku_id BIGINT,
    sku_code VARCHAR(50),
    sku_name VARCHAR(200),
    quantity INT NOT NULL,
    INDEX idx_delivery_id(delivery_id)
) COMMENT '发货明细';

-- 售后单
CREATE TABLE oms_after_sale (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    after_sale_no VARCHAR(50) NOT NULL UNIQUE,
    order_no VARCHAR(50) NOT NULL,
    order_id BIGINT NOT NULL,
    sku_id BIGINT,
    sku_code VARCHAR(50),
    sku_name VARCHAR(200),
    sku_spec VARCHAR(255),
    quantity INT NOT NULL,
    after_sale_type VARCHAR(30) NOT NULL COMMENT 'RETURN-退货 EXCHANGE-换货 REFUND-仅退款',
    reason VARCHAR(500),
    status VARCHAR(30) DEFAULT 'PENDING' COMMENT 'PENDING-待审核 APPROVED-已通过 REJECTED-已拒绝 COMPLETED-已完成',
    applicant VARCHAR(50),
    approve_time DATETIME,
    approve_remark VARCHAR(255),
    refund_amount DECIMAL(12,2),
    refund_time DATETIME,
    return_express_company VARCHAR(50),
    return_express_no VARCHAR(50),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_after_sale_no(after_sale_no),
    INDEX idx_order_no(order_no),
    INDEX idx_status(status)
) COMMENT '售后单';

-- ---------- 初始化数据 ----------
-- 初始管理员
INSERT INTO sys_user (username, password, real_name, status) VALUES
('admin', '$2a$12$IpVxatM/6sV/mr6/TTXDfudyOXSkhrhZqOxTY2dlbjFiXzqkYsU4G', '系统管理员', 1);
-- 密码默认 admin123

-- 初始角色
INSERT INTO sys_role (role_name, role_code, description) VALUES
('系统管理员', 'ROLE_ADMIN', '最高权限管理员'),
('运营主管', 'ROLE_OP_MANAGER', '运营管理'),
('客服', 'ROLE_CUSTOMER_SERVICE', '客服人员'),
('仓库管理员', 'ROLE_WAREHOUSE', '仓库管理人员'),
('财务', 'ROLE_FINANCE', '财务人员');

-- 分配管理员角色
INSERT INTO sys_user_role (user_id, role_id) VALUES (1, 1);

-- 菜单数据（按模块）
INSERT INTO sys_menu (menu_name, parent_id, menu_type, path, component, perms, icon, sort) VALUES
('仪表盘', 0, 1, '/dashboard', 'dashboard/index', 'dashboard:list', 'Dashboard', 1),
('订单管理', 0, 1, '/order', 'Layout', 'order:list', 'List', 2),
('订单列表', 2, 2, 'list', 'order/index', 'order:list', 'List', 1),
('商品管理', 0, 1, '/product', 'Layout', 'product:list', 'Goods', 3),
('商品SKU', 4, 2, 'sku', 'product/sku', 'product:sku:list', 'Goods', 1),
('商品分类', 4, 2, 'category', 'product/category', 'product:category:list', 'GoodsFilled', 2),
('库存管理', 0, 1, '/inventory', 'Layout', 'inventory:list', 'Box', 4),
('实时库存', 7, 2, 'current', 'inventory/current', 'inventory:current:list', 'Box', 1),
('库存盘点', 7, 2, 'stock-take', 'inventory/stockTake', 'inventory:stockTake:list', 'Edit', 2),
('客户管理', 0, 1, '/customer', 'Layout', 'customer:list', 'User', 5),
('客户列表', 10, 2, 'list', 'customer/index', 'customer:list', 'User', 1),
('客户等级', 10, 2, 'level', 'customer/level', 'customer:level:list', 'UserFilled', 2),
('物流管理', 0, 1, '/logistics', 'Layout', 'logistics:list', 'Van', 6),
('发货管理', 13, 2, 'delivery', 'logistics/delivery', 'logistics:delivery:list', 'Van', 1),
('快递模板', 13, 2, 'template', 'logistics/template', 'logistics:template:list', 'Document', 2),
('售后管理', 13, 2, 'after-sale', 'logistics/afterSale', 'logistics:afterSale:list', 'SoldOut', 3),
('系统管理', 0, 1, '/system', 'Layout', 'system:list', 'Setting', 7),
('用户管理', 18, 2, 'user', 'system/user', 'system:user:list', 'User', 1),
('角色管理', 18, 2, 'role', 'system/role', 'system:role:list', 'Avatar', 2),
('菜单管理', 18, 2, 'menu', 'system/menu', 'system:menu:list', 'Menu', 3),
('操作日志', 18, 2, 'log', 'system/log', 'system:log:list', 'Document', 4),
('数据统计', 0, 1, '/statistics', 'Layout', 'statistics:list', 'DataAnalysis', 8),
('订单统计', 23, 2, 'order', 'statistics/order', 'statistics:order:list', 'DataAnalysis', 1),
('商品统计', 23, 2, 'product', 'statistics/product', 'statistics:product:list', 'DataBoard', 2),
('客户统计', 23, 2, 'customer', 'statistics/customer', 'statistics:customer:list', 'DataLine', 3);
