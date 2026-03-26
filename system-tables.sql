-- ----------------------------
-- 1. 校区表
-- ----------------------------
DROP TABLE IF EXISTS `campus`;
CREATE TABLE `campus` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL COMMENT '校区名称',
  `address` varchar(255) DEFAULT NULL COMMENT '地址',
  `contact_person` varchar(50) DEFAULT NULL COMMENT '联系人',
  `contact_phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `contact_email` varchar(50) DEFAULT NULL COMMENT '联系邮箱',
  `manager_id` int DEFAULT NULL COMMENT '校区管理员ID (关联用户表)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='校区表';

-- ----------------------------
-- 2. 用户表 (学员、教练、管理员)
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `real_name` varchar(50) DEFAULT NULL COMMENT '真实姓名',
  `gender` int DEFAULT NULL COMMENT '性别: 1-男, 2-女',
  `age` int DEFAULT NULL COMMENT '年龄',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `email` varchar(50) DEFAULT NULL COMMENT '邮箱',
  `campus_id` int DEFAULT NULL COMMENT '所属校区ID',
  `user_role` int DEFAULT NULL COMMENT '用户角色: 1-超级管理员, 2-校区管理员, 3-教练, 4-学员',
  `user_status` int DEFAULT '1' COMMENT '用户状态: 0-待审核, 1-正常, 2-禁用',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像URL (主要用于教练)',
  `achievements` text COMMENT '过往成绩 (教练)',
  `coach_level` int DEFAULT NULL COMMENT '教练级别: 1-高级, 2-中级, 3-初级',
  `balance` decimal(10,2) DEFAULT '0.00' COMMENT '账户余额 (学员)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户表';

-- ----------------------------
-- 3. 球台表
-- ----------------------------
DROP TABLE IF EXISTS `tables`;
CREATE TABLE `tables` (
  `id` int NOT NULL AUTO_INCREMENT,
  `campus_id` int NOT NULL COMMENT '所属校区ID',
  `table_number` varchar(20) NOT NULL COMMENT '球台编号',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='球台表';

-- ----------------------------
-- 4. 师生关系表
-- ----------------------------
DROP TABLE IF EXISTS `coach_student_relations`;
CREATE TABLE `coach_student_relations` (
  `id` int NOT NULL AUTO_INCREMENT,
  `coach_id` int NOT NULL COMMENT '教练ID',
  `student_id` int NOT NULL COMMENT '学员ID',
  `status` int NOT NULL COMMENT '状态: 0-待教练确认, 1-已确认, 2-已解约',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='师生关系表';

-- ----------------------------
-- 5. 课程预约表
-- ----------------------------
DROP TABLE IF EXISTS `appointments`;
CREATE TABLE `appointments` (
  `id` int NOT NULL AUTO_INCREMENT,
  `coach_id` int NOT NULL,
  `student_id` int NOT NULL,
  `table_id` int NOT NULL,
  `start_time` datetime NOT NULL COMMENT '课程开始时间',
  `end_time` datetime NOT NULL COMMENT '课程结束时间',
  `cost` decimal(10,2) NOT NULL COMMENT '课程费用',
  `status` int NOT NULL COMMENT '状态: 0-待教练确认, 1-已预约, 2-已完成, 3-已取消',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='课程预约表';

-- ----------------------------
-- 6. 训练评价表
-- ----------------------------
DROP TABLE IF EXISTS `evaluations`;
CREATE TABLE `evaluations` (
  `id` int NOT NULL AUTO_INCREMENT,
  `appointment_id` int NOT NULL COMMENT '关联的课程预约ID',
  `student_feedback` text COMMENT '学员评价: 收获和教训',
  `coach_feedback` text COMMENT '教练评价: 表现和建议',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='训练评价表';

-- ----------------------------
-- 7. 账户流水表
-- ----------------------------
DROP TABLE IF EXISTS `transactions`;
CREATE TABLE `transactions` (
  `id` int NOT NULL AUTO_INCREMENT,
  `student_id` int NOT NULL,
  `type` int NOT NULL COMMENT '流水类型: 1-充值, 2-课程消费, 3-课程退款, 4-比赛报名费',
  `amount` decimal(10,2) NOT NULL COMMENT '金额 (正数表示增加, 负数表示减少)',
  `notes` varchar(255) DEFAULT NULL COMMENT '备注',
  `transaction_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='账户流水表';

-- ----------------------------
-- 8. 比赛表
-- ----------------------------
DROP TABLE IF EXISTS `competitions`;
CREATE TABLE `competitions` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL COMMENT '比赛名称',
  `competition_date` date NOT NULL COMMENT '比赛日期',
  `status` int DEFAULT '0' COMMENT '状态: 0-报名中, 1-进行中, 2-已结束',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='比赛表';

-- ----------------------------
-- 9. 比赛报名表
-- ----------------------------
DROP TABLE IF EXISTS `competition_participants`;
CREATE TABLE `competition_participants` (
  `id` int NOT NULL AUTO_INCREMENT,
  `competition_id` int NOT NULL,
  `student_id` int NOT NULL,
  `group_level` int NOT NULL COMMENT '组别: 1-甲, 2-乙, 3-丙',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='比赛报名表';

-- ----------------------------
-- 10. 比赛对阵表
-- ----------------------------
DROP TABLE IF EXISTS `matches`;
CREATE TABLE `matches` (
  `id` int NOT NULL AUTO_INCREMENT,
  `competition_id` int NOT NULL,
  `group_level` int NOT NULL COMMENT '组别',
  `round_number` int DEFAULT NULL COMMENT '轮次',
  `player1_id` int NOT NULL,
  `player2_id` int DEFAULT NULL COMMENT '轮空时为空',
  `table_id` int NOT NULL COMMENT '比赛球台',
  `winner_id` int DEFAULT NULL COMMENT '获胜者ID',
  `score` varchar(20) DEFAULT NULL COMMENT '比分',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='比赛对阵表';

-- ----------------------------
-- 11. 系统消息表
-- ----------------------------
DROP TABLE IF EXISTS `system_messages`;
CREATE TABLE `system_messages` (
  `id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(100) NOT NULL COMMENT '消息标题',
  `content` text NOT NULL COMMENT '消息内容',
  `type` int DEFAULT '1' COMMENT '消息类型: 1-系统通知, 2-更新公告, 3-活动消息',
  `status` int DEFAULT '2' COMMENT '状态: 1-草稿, 2-已发布, 3-已归档',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统消息表';

-- ----------------------------
-- 12. 系统日志表
-- ----------------------------
DROP TABLE IF EXISTS `system_logs`;
CREATE TABLE `system_logs` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int DEFAULT NULL COMMENT '操作用户ID',
  `action` varchar(100) NOT NULL COMMENT '操作类型',
  `details` text COMMENT '详情',
  `log_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统日志表';

-- ----------------------------
-- 初始化数据
-- ----------------------------
-- 插入一个超级管理员用户 (密码是明文'123456'，实际项目中应加密存储)
INSERT INTO `users` (`username`, `password`, `real_name`, `user_role`, `user_status`) VALUES ('superadmin', '123456', '超级管理员', 1, 1);

-- 插入基础校区数据
INSERT INTO `campus` (`id`, `name`, `address`) VALUES (1, '默认校区', '默认地址');

-- 插入基础球台数据
INSERT INTO `tables` (`id`, `campus_id`, `table_number`) VALUES 
(1, 1, '1号台'),
(2, 1, '2号台'),
(3, 1, '3号台'),
(4, 1, '4号台');