-- =============================================
-- 智慧旅游管理系统 - 数据库初始化脚本
-- =============================================

CREATE DATABASE IF NOT EXISTS travel_system DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE travel_system;

-- ----------------------------
-- 1. 用户表
-- ----------------------------
CREATE TABLE users (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username        VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password        VARCHAR(255) NOT NULL COMMENT '密码(Bcrypt加密)',
    nickname        VARCHAR(100) COMMENT '昵称',
    avatar          VARCHAR(500) COMMENT '头像URL',
    phone           VARCHAR(20) COMMENT '手机号',
    email           VARCHAR(100) COMMENT '邮箱',
    gender          TINYINT DEFAULT 0 COMMENT '0未知 1男 2女',
    birthday        DATE COMMENT '生日',
    role            TINYINT DEFAULT 1 COMMENT '1普通用户 2管理员',
    status          TINYINT DEFAULT 1 COMMENT '1正常 0封禁',
    last_login      DATETIME COMMENT '最后登录时间',
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted         TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_username (username),
    INDEX idx_phone (phone),
    INDEX idx_role (role),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ----------------------------
-- 2. 景点表
-- ----------------------------
CREATE TABLE spots (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '景点ID',
    name            VARCHAR(200) NOT NULL COMMENT '景点名称',
    cover_image     VARCHAR(500) COMMENT '封面图',
    city            VARCHAR(50) NOT NULL COMMENT '所在城市',
    address         VARCHAR(500) COMMENT '详细地址',
    longitude       DECIMAL(10,6) COMMENT '经度',
    latitude        DECIMAL(10,6) COMMENT '纬度',
    description     TEXT COMMENT '景点描述',
    ticket_price    DECIMAL(10,2) DEFAULT 0 COMMENT '门票价格',
    open_time       VARCHAR(200) COMMENT '开放时间',
    suggested_time   INT COMMENT '建议游览时长(小时)',
    level           VARCHAR(50) COMMENT '景区等级(5A/4A等)',
    tags            VARCHAR(500) COMMENT '标签,逗号分隔',
    hot_score       INT DEFAULT 0 COMMENT '热度分数',
    view_count      INT DEFAULT 0 COMMENT '浏览次数',
    status          TINYINT DEFAULT 1 COMMENT '1上架 0下架',
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted         TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_city (city),
    INDEX idx_status (status),
    INDEX idx_hot (hot_score DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='景点表';

-- ----------------------------
-- 3. 酒店表
-- ----------------------------
CREATE TABLE hotels (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '酒店ID',
    name            VARCHAR(200) NOT NULL COMMENT '酒店名称',
    cover_image     VARCHAR(500) COMMENT '封面图',
    city            VARCHAR(50) NOT NULL COMMENT '所在城市',
    address         VARCHAR(500) COMMENT '详细地址',
    longitude       DECIMAL(10,6) COMMENT '经度',
    latitude        DECIMAL(10,6) COMMENT '纬度',
    description     TEXT COMMENT '酒店描述',
    star_level      TINYINT COMMENT '星级(3-5)',
    facilities      VARCHAR(500) COMMENT '设施,逗号分隔',
    hot_score       INT DEFAULT 0 COMMENT '热度分数',
    status          TINYINT DEFAULT 1 COMMENT '1上架 0下架',
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted         TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_city (city),
    INDEX idx_star (star_level),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='酒店表';

-- ----------------------------
-- 4. 房型表
-- ----------------------------
CREATE TABLE room_types (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '房型ID',
    hotel_id        BIGINT NOT NULL COMMENT '所属酒店',
    name            VARCHAR(100) NOT NULL COMMENT '房型名称',
    price           DECIMAL(10,2) NOT NULL COMMENT '单价/晚',
    bed_type        VARCHAR(50) COMMENT '床型(大床/双床)',
    max_guest       INT DEFAULT 2 COMMENT '最大入住人数',
    total_rooms     INT DEFAULT 10 COMMENT '房间总数',
    amenities       VARCHAR(500) COMMENT '设施服务',
    images          VARCHAR(1000) COMMENT '房型图片,逗号分隔',
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted         TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_hotel (hotel_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='房型表';

-- ----------------------------
-- 5. 订单表
-- ----------------------------
CREATE TABLE orders (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '订单ID',
    order_no        VARCHAR(50) NOT NULL UNIQUE COMMENT '订单号',
    user_id         BIGINT NOT NULL COMMENT '用户ID',
    order_type      TINYINT NOT NULL COMMENT '1景点订单 2酒店订单',
    target_id       BIGINT NOT NULL COMMENT '景点ID或酒店ID',
    target_name     VARCHAR(200) COMMENT '景点/酒店名称',
    total_amount    DECIMAL(10,2) NOT NULL COMMENT '总金额',
    pay_amount      DECIMAL(10,2) NOT NULL COMMENT '实付金额',
    status          TINYINT DEFAULT 1 COMMENT '1待支付 2已支付 3已取消 4已退款',
    pay_time        DATETIME COMMENT '支付时间',
    pay_channel     VARCHAR(50) COMMENT '支付渠道',
    contact_name    VARCHAR(50) COMMENT '联系人',
    contact_phone   VARCHAR(20) COMMENT '联系电话',
    quantity        INT DEFAULT 1 COMMENT '数量',
    visit_date      DATE COMMENT '游览/入住日期',
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted         TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_user (user_id),
    INDEX idx_order_no (order_no),
    INDEX idx_status (status),
    INDEX idx_create (created_at DESC),
    INDEX idx_order_type (order_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- ----------------------------
-- 6. 游记表
-- ----------------------------
CREATE TABLE articles (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '游记ID',
    user_id         BIGINT NOT NULL COMMENT '作者ID',
    title           VARCHAR(200) NOT NULL COMMENT '标题',
    cover_image     VARCHAR(500) COMMENT '封面图',
    content         TEXT NOT NULL COMMENT '正文(Markdown)',
    spot_ids        VARCHAR(500) COMMENT '关联景点ID,逗号分隔',
    tags            VARCHAR(200) COMMENT '标签',
    view_count      INT DEFAULT 0 COMMENT '浏览次数',
    like_count      INT DEFAULT 0 COMMENT '点赞数',
    collect_count   INT DEFAULT 0 COMMENT '收藏数',
    comment_count   INT DEFAULT 0 COMMENT '评论数',
    status          TINYINT DEFAULT 0 COMMENT '0待审核 1已发布 2已下架',
    is_top          TINYINT DEFAULT 0 COMMENT '是否置顶',
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted         TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_user (user_id),
    INDEX idx_status (status),
    INDEX idx_create (created_at DESC),
    FULLTEXT idx_title (title)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='游记表';

-- ----------------------------
-- 7. 评论表
-- ----------------------------
CREATE TABLE comments (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '评论ID',
    user_id         BIGINT NOT NULL COMMENT '评论者ID',
    target_type     TINYINT NOT NULL COMMENT '1景点 2酒店 3游记 4订单',
    target_id       BIGINT NOT NULL COMMENT '目标ID',
    parent_id       BIGINT DEFAULT 0 COMMENT '父评论ID(0为顶级)',
    content         VARCHAR(1000) NOT NULL COMMENT '评论内容',
    rating          TINYINT COMMENT '评分(1-5)',
    like_count      INT DEFAULT 0 COMMENT '点赞数',
    status          TINYINT DEFAULT 1 COMMENT '1正常 0删除',
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted         TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_target (target_type, target_id),
    INDEX idx_user (user_id),
    INDEX idx_parent (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论表';

-- ----------------------------
-- 8. 收藏表
-- ----------------------------
CREATE TABLE favorites (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '收藏ID',
    user_id         BIGINT NOT NULL COMMENT '用户ID',
    target_type     TINYINT NOT NULL COMMENT '1景点 2酒店',
    target_id       BIGINT NOT NULL COMMENT '目标ID',
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    deleted         TINYINT DEFAULT 0 COMMENT '逻辑删除',
    UNIQUE KEY uk_user_target (user_id, target_type, target_id),
    INDEX idx_user (user_id),
    INDEX idx_target (target_type, target_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收藏表';

-- =============================================
-- 9. 用户地址表
-- =============================================
CREATE TABLE user_addresses (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '地址ID',
    user_id         BIGINT NOT NULL COMMENT '用户ID',
    contact_name    VARCHAR(50) NOT NULL COMMENT '联系人姓名',
    contact_phone   VARCHAR(20) NOT NULL COMMENT '联系人电话',
    province        VARCHAR(50) COMMENT '省份',
    city            VARCHAR(50) COMMENT '城市',
    district        VARCHAR(50) COMMENT '区县',
    detail_address  VARCHAR(500) NOT NULL COMMENT '详细地址',
    is_default      TINYINT DEFAULT 0 COMMENT '是否默认地址 0否 1是',
    tag             VARCHAR(50) COMMENT '地址标签(家/公司/其他)',
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted         TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_user (user_id),
    INDEX idx_default (user_id, is_default)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户地址表';

-- ----------------------------
-- 10. 景点门票类型表
-- ----------------------------
CREATE TABLE spot_tickets (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '门票类型ID',
    spot_id         BIGINT NOT NULL COMMENT '景点ID',
    name            VARCHAR(100) NOT NULL COMMENT '门票名称',
    description     VARCHAR(500) COMMENT '门票描述',
    price           DECIMAL(10,2) NOT NULL COMMENT '价格',
    stock           INT DEFAULT -1 COMMENT '库存(-1不限)',
    valid_days      INT DEFAULT 1 COMMENT '有效天数',
    refundable      TINYINT DEFAULT 1 COMMENT '是否可退 0否 1是',
    status          TINYINT DEFAULT 1 COMMENT '1上架 0下架',
    sort_order      INT DEFAULT 0 COMMENT '排序',
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted         TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_spot (spot_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='景点门票类型表';

-- ----------------------------
-- 11. 景点开放时间表
-- ----------------------------
CREATE TABLE spot_schedules (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '开放时间ID',
    spot_id         BIGINT NOT NULL COMMENT '景点ID',
    day_of_week     TINYINT COMMENT '星期几(1-7, null表示全部)',
    start_time      VARCHAR(10) NOT NULL COMMENT '开始时间 HH:mm',
    end_time        VARCHAR(10) NOT NULL COMMENT '结束时间 HH:mm',
    is_closed       TINYINT DEFAULT 0 COMMENT '是否闭园 0开园 1闭园',
    description     VARCHAR(200) COMMENT '特殊说明',
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted         TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_spot (spot_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='景点开放时间表';

-- ----------------------------
-- 12. 房间库存表
-- ----------------------------
CREATE TABLE room_inventory (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '库存ID',
    room_type_id    BIGINT NOT NULL COMMENT '房型ID',
    inventory_date  DATE NOT NULL COMMENT '库存日期',
    available_rooms INT NOT NULL COMMENT '可用房间数',
    price           DECIMAL(10,2) COMMENT '当日价格(为空则用房型原价)',
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted         TINYINT DEFAULT 0 COMMENT '逻辑删除',
    UNIQUE KEY uk_room_date (room_type_id, inventory_date),
    INDEX idx_date (inventory_date),
    INDEX idx_room (room_type_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='房间库存表';

-- ----------------------------
-- 13. 订单明细表
-- ----------------------------
CREATE TABLE order_items (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '订单明细ID',
    order_id        BIGINT NOT NULL COMMENT '订单ID',
    item_type       TINYINT NOT NULL COMMENT '1景点门票 2酒店房间',
    item_id         BIGINT NOT NULL COMMENT '门票ID或房型ID',
    item_name       VARCHAR(200) NOT NULL COMMENT '商品名称',
    quantity        INT NOT NULL DEFAULT 1 COMMENT '数量',
    unit_price      DECIMAL(10,2) NOT NULL COMMENT '单价',
    subtotal        DECIMAL(10,2) NOT NULL COMMENT '小计',
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    deleted         TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_order (order_id),
    INDEX idx_item (item_type, item_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单明细表';

-- ----------------------------
-- 14. 支付记录表
-- ----------------------------
CREATE TABLE payments (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '支付记录ID',
    payment_no      VARCHAR(50) NOT NULL UNIQUE COMMENT '支付流水号',
    order_no        VARCHAR(50) NOT NULL COMMENT '关联订单号',
    user_id         BIGINT NOT NULL COMMENT '用户ID',
    amount          DECIMAL(10,2) NOT NULL COMMENT '支付金额',
    pay_channel     VARCHAR(20) NOT NULL COMMENT '支付渠道(wechat/alipay/card)',
    pay_status      TINYINT NOT NULL DEFAULT 0 COMMENT '0待支付 1支付成功 2支付失败 3已退款',
    pay_time        DATETIME COMMENT '支付时间',
    transaction_id  VARCHAR(100) COMMENT '第三方交易号',
    extra_data      TEXT COMMENT '扩展数据(JSON)',
    refund_amount    DECIMAL(10,2) DEFAULT 0 COMMENT '退款金额',
    refund_time     DATETIME COMMENT '退款时间',
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted         TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_order (order_no),
    INDEX idx_user (user_id),
    INDEX idx_pay_status (pay_status),
    INDEX idx_pay_time (pay_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付记录表';

-- ----------------------------
-- 15. 点赞表
-- ----------------------------
CREATE TABLE likes (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '点赞ID',
    user_id         BIGINT NOT NULL COMMENT '用户ID',
    target_type     TINYINT NOT NULL COMMENT '1景点 2酒店 3游记',
    target_id       BIGINT NOT NULL COMMENT '目标ID',
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    deleted         TINYINT DEFAULT 0 COMMENT '逻辑删除',
    UNIQUE KEY uk_user_target (user_id, target_type, target_id),
    INDEX idx_target (target_type, target_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='点赞表';

-- ----------------------------
-- 16. 消息通知表
-- ----------------------------
CREATE TABLE notifications (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '通知ID',
    user_id         BIGINT NOT NULL COMMENT '接收用户ID',
    type            VARCHAR(30) NOT NULL COMMENT '通知类型(order_pay/order_cancel/refund/article_comment/article_like/system)',
    title           VARCHAR(200) NOT NULL COMMENT '通知标题',
    content         TEXT COMMENT '通知内容',
    related_id      BIGINT COMMENT '关联ID(订单/游记等)',
    is_read         TINYINT DEFAULT 0 COMMENT '是否已读 0未读 1已读',
    read_time       DATETIME COMMENT '阅读时间',
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    deleted         TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_user (user_id),
    INDEX idx_type (type),
    INDEX idx_read (user_id, is_read),
    INDEX idx_create (created_at DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息通知表';

-- ----------------------------
-- 17. 系统通知表
-- ----------------------------
CREATE TABLE system_notifications (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '系统通知ID',
    title           VARCHAR(200) NOT NULL COMMENT '通知标题',
    content         TEXT NOT NULL COMMENT '通知内容',
    type            VARCHAR(30) DEFAULT 'system' COMMENT '通知类型',
    target_users    VARCHAR(50) DEFAULT 'all' COMMENT '推送目标(all/users/admins)',
    cover_image     VARCHAR(500) COMMENT '封面图',
    link_url        VARCHAR(500) COMMENT '跳转链接',
    is_published    TINYINT DEFAULT 0 COMMENT '是否发布 0草稿 1已发布',
    published_at    DATETIME COMMENT '发布时间',
    start_time      DATETIME COMMENT '展示开始时间',
    end_time        DATETIME COMMENT '展示结束时间',
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted         TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_published (is_published),
    INDEX idx_time (start_time, end_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统通知表';

-- ----------------------------
-- 18. 文件表
-- ----------------------------
CREATE TABLE files (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '文件ID',
    file_name       VARCHAR(255) NOT NULL COMMENT '原始文件名',
    file_path       VARCHAR(500) NOT NULL COMMENT '存储路径',
    file_url        VARCHAR(500) NOT NULL COMMENT '访问URL',
    file_size       BIGINT COMMENT '文件大小(字节)',
    file_type       VARCHAR(50) COMMENT '文件MIME类型',
    file_ext        VARCHAR(20) COMMENT '文件扩展名',
    storage_type    VARCHAR(20) DEFAULT 'local' COMMENT '存储类型(local/oss/minio)',
    user_id         BIGINT COMMENT '上传用户ID',
    bucket_name     VARCHAR(100) COMMENT 'OSS Bucket',
    biz_type        VARCHAR(50) COMMENT '业务类型(avatar/article/spot/hotel)',
    biz_id          BIGINT COMMENT '关联业务ID',
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    deleted         TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_user (user_id),
    INDEX idx_biz (biz_type, biz_id),
    INDEX idx_create (created_at DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文件表';

-- ----------------------------
-- 19. 优惠券表
-- ----------------------------
CREATE TABLE coupons (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '优惠券ID',
    name            VARCHAR(100) NOT NULL COMMENT '优惠券名称',
    description     VARCHAR(500) COMMENT '使用说明',
    type            TINYINT NOT NULL COMMENT '1满减券 2折扣券 3兑换券',
    discount_value  DECIMAL(10,2) NOT NULL COMMENT '优惠值(满减金额或折扣率)',
    min_amount      DECIMAL(10,2) DEFAULT 0 COMMENT '最低消费金额',
    max_discount    DECIMAL(10,2) COMMENT '最高优惠金额(折扣券)',
    total_count     INT NOT NULL COMMENT '发放总数量',
    remain_count    INT NOT NULL COMMENT '剩余数量',
    per_user_limit  INT DEFAULT 1 COMMENT '每人限领数量',
    applicable_type TINYINT DEFAULT 1 COMMENT '适用类型 1全场 2景点 3酒店',
    applicable_ids  VARCHAR(500) COMMENT '适用的景点/酒店ID列表',
    valid_start     DATETIME NOT NULL COMMENT '有效期开始',
    valid_end       DATETIME NOT NULL COMMENT '有效期结束',
    status          TINYINT DEFAULT 1 COMMENT '1有效 0无效',
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted         TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_status (status),
    INDEX idx_valid (valid_start, valid_end)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='优惠券表';

-- ----------------------------
-- 20. 用户优惠券表
-- ----------------------------
CREATE TABLE user_coupons (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户优惠券ID',
    user_id         BIGINT NOT NULL COMMENT '用户ID',
    coupon_id       BIGINT NOT NULL COMMENT '优惠券ID',
    coupon_name     VARCHAR(100) COMMENT '优惠券名称快照',
    coupon_type     TINYINT COMMENT '优惠券类型快照',
    discount_value  DECIMAL(10,2) COMMENT '优惠值快照',
    min_amount      DECIMAL(10,2) COMMENT '最低消费快照',
    max_discount    DECIMAL(10,2) COMMENT '最高优惠快照',
    order_no        VARCHAR(50) COMMENT '关联订单号',
    status          TINYINT DEFAULT 0 COMMENT '0未使用 1已使用 2已过期',
    receive_time    DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '领取时间',
    use_time        DATETIME COMMENT '使用时间',
    expire_time     DATETIME COMMENT '过期时间',
    deleted         TINYINT DEFAULT 0 COMMENT '逻辑删除',
    UNIQUE KEY uk_user_coupon (user_id, coupon_id),
    INDEX idx_user (user_id),
    INDEX idx_status (status),
    INDEX idx_expire (expire_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户优惠券表';

-- ----------------------------
-- 21. 操作日志表
-- ----------------------------
CREATE TABLE operation_logs (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '日志ID',
    user_id         BIGINT COMMENT '操作用户ID',
    username        VARCHAR(50) COMMENT '操作用户名',
    module          VARCHAR(50) NOT NULL COMMENT '模块',
    action          VARCHAR(50) NOT NULL COMMENT '操作类型',
    description     VARCHAR(500) COMMENT '操作描述',
    request_method  VARCHAR(10) COMMENT '请求方法',
    request_url     VARCHAR(500) COMMENT '请求URL',
    request_params  TEXT COMMENT '请求参数',
    response_data   TEXT COMMENT '响应数据',
    ip_address      VARCHAR(50) COMMENT 'IP地址',
    user_agent      VARCHAR(500) COMMENT '浏览器标识',
    status          TINYINT DEFAULT 1 COMMENT '1成功 0失败',
    error_message   VARCHAR(500) COMMENT '错误信息',
    duration_ms     BIGINT COMMENT '耗时(毫秒)',
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    deleted         TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_user (user_id),
    INDEX idx_module (module),
    INDEX idx_create (created_at DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- =============================================
-- 初始化测试数据
-- =============================================

-- 插入管理员账号 (密码: admin123)
INSERT INTO users (username, password, nickname, role, status, created_at) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '系统管理员', 2, 1, NOW());

-- 插入普通用户账号 (密码: user123)
INSERT INTO users (username, password, nickname, phone, role, status, created_at) VALUES
('testuser', '$2a$10$rDkPvvAFV8kRQAJb5hX3/.V8pVRVpVRVpVRVpVRVpVRVpVRVpVRV', '测试用户', '13800138000', 1, 1, NOW());

-- 插入景点数据
INSERT INTO spots (name, cover_image, city, address, longitude, latitude, description, ticket_price, open_time, suggested_time, level, tags, hot_score, view_count, status) VALUES
('故宫博物院', 'https://picsum.photos/800/600?random=1', '北京', '北京市东城区景山前街4号', 116.397128, 39.916527, '世界上现存规模最大、保存最为完整的木质结构古建筑之一，是中国古代宫廷建筑的精华', 60.00, '08:30-17:00', 4, '5A', '历史,文化,博物馆', 9800, 125600, 1),
('杭州西湖', 'https://picsum.photos/800/600?random=2', '杭州', '浙江省杭州市西湖区西湖风景名胜区', 120.155070, 30.246359, '世界文化遗产，中国著名的风景游览胜地，以秀丽的湖光山色和众多名胜古迹闻名', 0.00, '全天开放', 6, '5A', '自然,湖泊,休闲', 9500, 98000, 1),
('黄山风景区', 'https://picsum.photos/800/600?random=3', '黄山', '安徽省黄山市黄山区黄山风景区', 118.157677, 30.128417, '中国著名的山岳风景区，以奇松、怪石、云海、温泉著称于世', 230.00, '06:30-17:30', 1, '5A', '山岳,自然,摄影', 9200, 87000, 1),
('厦门鼓浪屿', 'https://picsum.photos/800/600?random=4', '厦门', '福建省厦门市思明区鼓浪屿', 118.061722, 24.446483, '世界文化遗产，国家5A级旅游景区，享有海上花园的美誉', 0.00, '全天开放', 8, '5A', '海岛,文艺,历史', 8900, 78000, 1),
('成都大熊猫繁育研究基地', 'https://picsum.photos/800/600?random=5', '成都', '四川省成都市成华区外北熊猫大道1375号', 104.143534, 30.739060, '世界著名的大熊猫迁地保护基地、科研繁育基地和公众教育基地', 58.00, '07:30-18:00', 4, '4A', '动物,亲子,科普', 8600, 72000, 1),
('张家界国家森林公园', 'https://picsum.photos/800/600?random=6', '张家界', '湖南省张家界市武陵源区', 110.449902, 29.117040, '中国第一个国家森林公园，以独特的石英砂岩峰林地貌著称', 248.00, '07:00-18:00', 2, '5A', '自然,山岳,探险', 8400, 69000, 1),
('丽江古城', 'https://picsum.photos/800/600?random=7', '丽江', '云南省丽江市古城区', 100.235532, 26.875022, '世界文化遗产，是中国历史文化名城中保存最为完整的古城之一', 0.00, '全天开放', 2, '5A', '古城,民族,夜生活', 8100, 65000, 1),
('西安秦始皇兵马俑', 'https://picsum.photos/800/600?random=8', '西安', '陕西省西安市临潼区秦始皇帝陵博物院', 109.278372, 34.384414, '世界第八大奇迹，是世界上规模最大、结构最奇特的古代军事博物馆', 150.00, '08:30-18:30', 3, '5A', '历史,文化,博物馆', 8800, 81000, 1);

-- 插入酒店数据
INSERT INTO hotels (name, cover_image, city, address, longitude, latitude, description, star_level, facilities, hot_score, status) VALUES
('北京华尔道夫酒店', 'https://picsum.photos/800/600?random=11', '北京', '北京市东城区金宝街8号', 116.415150, 39.918234, '位于繁华的王府井商圈，距离故宫仅10分钟步行路程，奢华五星级酒店', 5, 'WiFi,游泳池,健身房,餐厅,酒吧,管家服务', 9200, 1),
('上海外滩悦榕庄', 'https://picsum.photos/800/600?random=12', '上海', '上海市虹口区海平路19号', 121.491900, 31.245680, '坐拥黄浦江迷人景致，顶级都市度假酒店，房间配备全景落地窗', 5, 'WiFi,江景房,SPA,健身房,餐厅', 9000, 1),
('杭州西子湖四季酒店', 'https://picsum.photos/800/600?random=13', '杭州', '浙江省杭州市西湖区北山街道曙光路120号', 120.146890, 30.267120, '紧邻西湖，园林式度假酒店，私密性极佳，中式古典风格', 5, 'WiFi,湖景房,游泳池,SPA,茶室', 8800, 1),
('成都瑞吉酒店', 'https://picsum.photos/800/600?random=14', '成都', '四川省成都市锦江区太升南路88号', 104.078920, 30.675410, '位于春熙路核心商圈，精致奢华五星级酒店，服务一流', 5, 'WiFi,健身房,游泳池,餐厅,酒吧', 8500, 1),
('厦门海悦山庄', 'https://picsum.photos/800/600?random=15', '厦门', '福建省厦门市思明区环岛南路3999号', 118.142320, 24.468260, '临海度假酒店，花园式环境，拥有私人沙滩，距离鼓浪屿码头仅15分钟', 5, 'WiFi,海景房,私人沙滩,游泳池,SPA', 8700, 1),
('张家界京武铂尔曼酒店', 'https://picsum.photos/800/600?random=16', '张家界', '湖南省张家界市永定区古庸路87号', 110.473520, 29.128230, '距离天门山景区仅10分钟车程，山景度假酒店，视野开阔', 4, 'WiFi,山景房,健身房,餐厅', 8200, 1);

-- 插入房型数据
INSERT INTO room_types (hotel_id, name, price, bed_type, max_guest, total_rooms, amenities, images) VALUES
(1, '豪华大床房', 1888.00, '大床1.8m', 2, 15, '免费WiFi,含双早,迷你吧', 'https://picsum.photos/400/300?random=21'),
(1, '行政双床房', 2288.00, '双床1.2m', 2, 10, '免费WiFi,含双早,行政酒廊', 'https://picsum.photos/400/300?random=22'),
(1, '总统套房', 9888.00, '大床2.0m', 4, 2, '免费WiFi,含双早,管家服务,全景露台', 'https://picsum.photos/400/300?random=23'),
(2, '江景大床房', 2688.00, '大床1.8m', 2, 20, '免费WiFi,含双早,江景浴缸', 'https://picsum.photos/400/300?random=24'),
(2, '江景双床房', 2988.00, '双床1.3m', 2, 15, '免费WiFi,含双早,行政酒廊', 'https://picsum.photos/400/300?random=25'),
(3, '园林大床房', 1588.00, '大床1.8m', 2, 12, '免费WiFi,含双早,庭院景观', 'https://picsum.photos/400/300?random=26'),
(3, '湖景套房', 3588.00, '大床2.0m', 3, 5, '免费WiFi,含双早,阳台,管家服务', 'https://picsum.photos/400/300?random=27'),
(4, '豪华大床房', 1288.00, '大床1.8m', 2, 18, '免费WiFi,含双早', 'https://picsum.photos/400/300?random=28'),
(4, '豪华双床房', 1388.00, '双床1.2m', 2, 15, '免费WiFi,含双早', 'https://picsum.photos/400/300?random=29'),
(5, '海景大床房', 1888.00, '大床1.8m', 2, 16, '免费WiFi,含双早,海景阳台', 'https://picsum.photos/400/300?random=30'),
(5, '海景套房', 3888.00, '大床2.0m', 3, 4, '免费WiFi,含双早,管家服务,按摩浴缸', 'https://picsum.photos/400/300?random=31');

-- 插入游记数据
INSERT INTO articles (user_id, title, cover_image, content, tags, view_count, like_count, comment_count, status, is_top) VALUES
(1, '北京三日深度游攻略｜故宫颐和园长城一次玩遍', 'https://picsum.photos/800/600?random=41', '这次北京之旅真的太棒了！从故宫的红墙金瓦到长城的巍峨壮丽，每一处都让人震撼...', '北京,故宫,长城,攻略', 8560, 324, 56, 1, 1),
(1, '杭州西湖边的慢生活｜三天两夜治愈之旅', 'https://picsum.photos/800/600?random=42', '杭州是一座让人来了就不想走的城市，西湖边的清晨太美好了...', '杭州,西湖,慢生活,美食', 6230, 256, 38, 1, 0),
(1, '张家界天门山探险｜玻璃栈道真的腿软了', 'https://picsum.photos/800/600?random=43', '天门山的玻璃栈道真的不是一般的刺激！整理了一份详细的攻略分享给大家...', '张家界,天门山,探险,玻璃栈道', 4920, 189, 27, 1, 0);

-- 插入订单数据
INSERT INTO orders (order_no, user_id, order_type, target_id, target_name, total_amount, pay_amount, status, pay_time, contact_name, contact_phone, quantity, visit_date) VALUES
('ORD202604010001', 1, 1, 1, '故宫博物院', 120.00, 120.00, 2, '2026-04-01 10:30:00', '测试用户', '13800138000', 2, '2026-04-15'),
('ORD202604010002', 1, 1, 2, '杭州西湖', 0.00, 0.00, 2, '2026-04-01 14:22:00', '测试用户', '13800138000', 1, '2026-04-20'),
('ORD202604020001', 1, 2, 1, '北京华尔道夫酒店', 3776.00, 3776.00, 2, '2026-04-02 09:15:00', '测试用户', '13800138000', 2, '2026-04-15');

-- 插入景点门票数据
INSERT INTO spot_tickets (spot_id, name, description, price, stock, valid_days, refundable, status, sort_order) VALUES
(1, '成人票', '成人普通门票', 60.00, -1, 1, 1, 1, 1),
(1, '学生票', '在校学生凭有效证件', 30.00, -1, 1, 1, 1, 2),
(1, '老人票', '60周岁以上老人', 30.00, -1, 1, 1, 1, 3),
(2, '船票', '西湖游船票', 55.00, -1, 1, 1, 1, 1),
(3, '成人票', '黄山景区大门票', 230.00, -1, 1, 1, 1, 1),
(4, '上岛船票', '往返鼓浪屿船票', 35.00, -1, 1, 1, 1, 1);

-- 插入景点开放时间
INSERT INTO spot_schedules (spot_id, day_of_week, start_time, end_time, is_closed, description) VALUES
(1, 1, '08:30', '17:00', 0, '周一开放'),
(1, 2, '08:30', '17:00', 0, '周二开放'),
(1, 3, '08:30', '17:00', 0, '周三开放'),
(1, 4, '08:30', '17:00', 0, '周四开放'),
(1, 5, '08:30', '17:00', 0, '周五开放'),
(1, 6, '08:30', '17:00', 0, '周六开放'),
(1, 7, '08:30', '17:00', 0, '周日开放'),
(2, NULL, '00:00', '23:59', 0, '全天开放');

-- 插入房间库存数据
INSERT INTO room_inventory (room_type_id, inventory_date, available_rooms, price) VALUES
(1, '2026-04-15', 10, 1888.00),
(1, '2026-04-16', 8, 1888.00),
(1, '2026-04-17', 12, 1788.00),
(2, '2026-04-15', 5, 2288.00),
(2, '2026-04-16', 6, 2288.00),
(8, '2026-04-15', 10, 1288.00),
(8, '2026-04-16', 8, 1388.00);

-- 插入优惠券数据
INSERT INTO coupons (name, description, type, discount_value, min_amount, max_discount, total_count, remain_count, per_user_limit, applicable_type, valid_start, valid_end, status) VALUES
('新人专享券', '新用户首单满100减20', 1, 20.00, 100.00, NULL, 1000, 850, 1, 1, '2026-01-01 00:00:00', '2026-12-31 23:59:59', 1),
('景点专属券', '景点门票满50减10', 1, 10.00, 50.00, NULL, 500, 400, 2, 2, '2026-01-01 00:00:00', '2026-12-31 23:59:59', 1),
('酒店折扣券', '酒店预订8折优惠', 2, 0.80, 200.00, 200.00, 300, 250, 1, 3, '2026-01-01 00:00:00', '2026-12-31 23:59:59', 1),
('满200减30', '全场通用满减券', 1, 30.00, 200.00, NULL, 1000, 900, 1, 1, '2026-04-01 00:00:00', '2026-06-30 23:59:59', 1);

-- 插入用户优惠券
INSERT INTO user_coupons (user_id, coupon_id, coupon_name, coupon_type, discount_value, min_amount, status, expire_time) VALUES
(1, 1, '新人专享券', 1, 20.00, 100.00, 0, '2026-12-31 23:59:59'),
(1, 2, '景点专属券', 1, 10.00, 50.00, 0, '2026-12-31 23:59:59');

-- 插入系统通知
INSERT INTO system_notifications (title, content, type, target_users, is_published, published_at) VALUES
('欢迎使用智慧旅游管理系统', '感谢您的注册，祝您旅途愉快！', 'system', 'all', 1, NOW()),
('五一假期优惠活动', '五一假期期间，所有景点门票8折优惠，快来抢购吧！', 'activity', 'all', 1, NOW());
