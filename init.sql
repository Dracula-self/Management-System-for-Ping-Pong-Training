/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80300
 Source Host           : localhost:3306
 Source Schema         : pingpang

 Target Server Type    : MySQL
 Target Server Version : 80300
 File Encoding         : 65001

 Date: 17/09/2025 22:57:54
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for appointments
-- ----------------------------
DROP TABLE IF EXISTS `appointments`;
CREATE TABLE `appointments`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `coach_id` int(0) NOT NULL,
  `student_id` int(0) NOT NULL,
  `table_id` int(0) NOT NULL,
  `start_time` datetime(0) NOT NULL COMMENT '课程开始时间',
  `end_time` datetime(0) NOT NULL COMMENT '课程结束时间',
  `cost` decimal(10, 2) NOT NULL COMMENT '课程费用',
  `status` int(0) NOT NULL COMMENT '状态: 0-待教练确认, 1-已预约, 2-已完成, 3-已取消',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '课程预约表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of appointments
-- ----------------------------
INSERT INTO `appointments` VALUES (4, 4, 6, 1, '2025-09-18 00:00:00', '2025-09-18 01:00:00', 80.00, 2, '2025-09-17 22:46:23');

-- ----------------------------
-- Table structure for campus
-- ----------------------------
DROP TABLE IF EXISTS `campus`;
CREATE TABLE `campus`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '校区名称',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '地址',
  `contact_person` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系人',
  `contact_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系电话',
  `contact_email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系邮箱',
  `manager_id` int(0) NULL DEFAULT NULL COMMENT '校区管理员ID (关联用户表)',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '校区表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of campus
-- ----------------------------
INSERT INTO `campus` VALUES (1, '校区A', 'xxxx', '张三', '18811112222', 'mail@qq.com', 2, '2025-09-13 16:39:15');
INSERT INTO `campus` VALUES (2, '校区B', 'xxx', '张三', '18822223333', '', 3, '2025-09-17 21:44:33');

-- ----------------------------
-- Table structure for coach_student_relations
-- ----------------------------
DROP TABLE IF EXISTS `coach_student_relations`;
CREATE TABLE `coach_student_relations`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `coach_id` int(0) NOT NULL COMMENT '教练ID',
  `student_id` int(0) NOT NULL COMMENT '学员ID',
  `status` int(0) NOT NULL COMMENT '状态: 0-待教练确认, 1-已确认, 2-已解约',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '师生关系表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of coach_student_relations
-- ----------------------------
INSERT INTO `coach_student_relations` VALUES (1, 4, 6, 1, '2025-09-17 22:38:00');

-- ----------------------------
-- Table structure for competition_participants
-- ----------------------------
DROP TABLE IF EXISTS `competition_participants`;
CREATE TABLE `competition_participants`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `competition_id` int(0) NOT NULL,
  `student_id` int(0) NOT NULL,
  `group_level` int(0) NOT NULL COMMENT '组别: 1-甲, 2-乙, 3-丙',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '比赛报名表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of competition_participants
-- ----------------------------
INSERT INTO `competition_participants` VALUES (1, 1, 6, 3);
INSERT INTO `competition_participants` VALUES (2, 1, 10, 3);
INSERT INTO `competition_participants` VALUES (3, 1, 9, 3);
INSERT INTO `competition_participants` VALUES (4, 1, 8, 3);
INSERT INTO `competition_participants` VALUES (5, 1, 7, 3);
INSERT INTO `competition_participants` VALUES (6, 1, 11, 3);

-- ----------------------------
-- Table structure for competitions
-- ----------------------------
DROP TABLE IF EXISTS `competitions`;
CREATE TABLE `competitions`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '比赛名称',
  `competition_date` date NOT NULL COMMENT '比赛日期',
  `status` int(0) NULL DEFAULT 0 COMMENT '状态: 0-报名中, 1-进行中, 2-已结束',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '比赛表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of competitions
-- ----------------------------
INSERT INTO `competitions` VALUES (1, 'test', '2025-09-18', 3);

-- ----------------------------
-- Table structure for evaluations
-- ----------------------------
DROP TABLE IF EXISTS `evaluations`;
CREATE TABLE `evaluations`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `appointment_id` int(0) NOT NULL COMMENT '关联的课程预约ID',
  `student_feedback` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '学员评价: 收获和教训',
  `coach_feedback` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '教练评价: 表现和建议',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '训练评价表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of evaluations
-- ----------------------------
INSERT INTO `evaluations` VALUES (1, 4, '123123123', '123123123', '2025-09-17 22:46:55');

-- ----------------------------
-- Table structure for matches
-- ----------------------------
DROP TABLE IF EXISTS `matches`;
CREATE TABLE `matches`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `competition_id` int(0) NOT NULL,
  `group_level` int(0) NOT NULL COMMENT '组别',
  `round_number` int(0) NULL DEFAULT NULL COMMENT '轮次',
  `player1_id` int(0) NOT NULL,
  `player2_id` int(0) NULL DEFAULT NULL COMMENT '轮空时为空',
  `table_id` int(0) NOT NULL COMMENT '比赛球台',
  `winner_id` int(0) NULL DEFAULT NULL COMMENT '获胜者ID',
  `score` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '比分',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '比赛对阵表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of matches
-- ----------------------------
INSERT INTO `matches` VALUES (1, 1, 3, 1, 7, 9, 1, 7, '3:1');
INSERT INTO `matches` VALUES (2, 1, 3, 1, 8, 11, 2, 8, '3:1');
INSERT INTO `matches` VALUES (3, 1, 3, 1, 10, 6, 3, 10, '3:1');
INSERT INTO `matches` VALUES (4, 1, 3, 2, 7, NULL, 1, 7, '轮空');
INSERT INTO `matches` VALUES (5, 1, 3, 2, 8, 10, 2, 8, '3:1');
INSERT INTO `matches` VALUES (6, 1, 3, 3, 7, 8, 1, 7, '3:1');

-- ----------------------------
-- Table structure for system_logs
-- ----------------------------
DROP TABLE IF EXISTS `system_logs`;
CREATE TABLE `system_logs`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `user_id` int(0) NULL DEFAULT NULL COMMENT '操作用户ID',
  `action` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '操作类型',
  `details` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '详情',
  `log_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of system_logs
-- ----------------------------
INSERT INTO `system_logs` VALUES (1, 1, '用户登录', '用户【admin】成功登录系统', '2025-09-17 21:39:28');
INSERT INTO `system_logs` VALUES (2, 1, '更新用户', '更新用户【xiaoqu2】信息', '2025-09-17 21:45:38');
INSERT INTO `system_logs` VALUES (3, 1, '更新用户', '更新用户【xiaoqu1】信息', '2025-09-17 21:45:46');
INSERT INTO `system_logs` VALUES (4, 1, '更新用户', '更新用户【liaolian1】信息', '2025-09-17 21:45:56');
INSERT INTO `system_logs` VALUES (5, 1, '用户登录', '用户【admin】成功登录系统', '2025-09-17 22:30:17');
INSERT INTO `system_logs` VALUES (6, 2, '用户登录', '用户【xiaoqu1】成功登录系统', '2025-09-17 22:30:48');
INSERT INTO `system_logs` VALUES (7, 4, '用户登录', '用户【jiaolian1】成功登录系统', '2025-09-17 22:31:32');
INSERT INTO `system_logs` VALUES (8, 6, '用户登录', '用户【xuesheng1】成功登录系统', '2025-09-17 22:31:44');
INSERT INTO `system_logs` VALUES (9, 1, '创建系统消息', '创建系统消息【111】，类型：1', '2025-09-17 22:45:28');
INSERT INTO `system_logs` VALUES (10, 7, '用户登录', '用户【xuesheng2】成功登录系统', '2025-09-17 22:49:24');
INSERT INTO `system_logs` VALUES (11, 8, '用户登录', '用户【xuesheng3】成功登录系统', '2025-09-17 22:49:32');
INSERT INTO `system_logs` VALUES (12, 9, '用户登录', '用户【xuesheng4】成功登录系统', '2025-09-17 22:50:05');
INSERT INTO `system_logs` VALUES (13, 10, '用户登录', '用户【xuesheng5】成功登录系统', '2025-09-17 22:50:45');
INSERT INTO `system_logs` VALUES (14, 1, '创建用户', '创建用户【xuesheng6】，角色：4', '2025-09-17 22:51:50');
INSERT INTO `system_logs` VALUES (15, 11, '用户登录', '用户【xuesheng6】成功登录系统', '2025-09-17 22:52:06');

-- ----------------------------
-- Table structure for system_messages
-- ----------------------------
DROP TABLE IF EXISTS `system_messages`;
CREATE TABLE `system_messages`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '消息标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '消息内容',
  `type` int(0) NULL DEFAULT 1 COMMENT '消息类型: 1-系统通知, 2-更新公告, 3-活动消息',
  `status` int(0) NULL DEFAULT 2 COMMENT '状态: 1-草稿, 2-已发布, 3-已归档',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统消息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of system_messages
-- ----------------------------
INSERT INTO `system_messages` VALUES (1, '111', '123123123', 1, 2, '2025-09-17 22:45:28');
INSERT INTO `system_messages` VALUES (2, '新的预约申请', '学员【学生1】申请预约课程，时间：2025-09-18T00:00 - 2025-09-18T01:00，请及时处理。', 1, 2, '2025-09-17 22:46:23');
INSERT INTO `system_messages` VALUES (3, '预约确认通知', '教练【教练1】已确认学员【学生1】的预约，时间：2025-09-18T00:00 - 2025-09-18T01:00，请准时上课。', 1, 2, '2025-09-17 22:46:34');

-- ----------------------------
-- Table structure for tables
-- ----------------------------
DROP TABLE IF EXISTS `tables`;
CREATE TABLE `tables`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `campus_id` int(0) NOT NULL COMMENT '所属校区ID',
  `table_number` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '球台编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '球台表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tables
-- ----------------------------
INSERT INTO `tables` VALUES (1, 1, '001');
INSERT INTO `tables` VALUES (2, 1, '002');
INSERT INTO `tables` VALUES (3, 1, '003');
INSERT INTO `tables` VALUES (4, 1, '004');
INSERT INTO `tables` VALUES (5, 1, '005');
INSERT INTO `tables` VALUES (6, 1, '006');

-- ----------------------------
-- Table structure for transactions
-- ----------------------------
DROP TABLE IF EXISTS `transactions`;
CREATE TABLE `transactions`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `student_id` int(0) NOT NULL,
  `type` int(0) NOT NULL COMMENT '流水类型: 1-充值, 2-课程消费, 3-课程退款, 4-比赛报名费',
  `amount` decimal(10, 2) NOT NULL COMMENT '金额 (正数表示增加, 负数表示减少)',
  `notes` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `transaction_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '账户流水表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of transactions
-- ----------------------------
INSERT INTO `transactions` VALUES (1, 6, 2, -80.00, '课程费用支付 - 预约ID: 4', '2025-09-17 22:46:34');
INSERT INTO `transactions` VALUES (2, 6, 1, 100.00, '管理员手工充值 - ', '2025-09-17 22:47:51');
INSERT INTO `transactions` VALUES (3, 6, 4, -30.00, '比赛报名费 - test', '2025-09-17 22:49:04');
INSERT INTO `transactions` VALUES (4, 10, 4, -30.00, '比赛报名费 - test', '2025-09-17 22:51:01');
INSERT INTO `transactions` VALUES (5, 9, 4, -30.00, '比赛报名费 - test', '2025-09-17 22:51:07');
INSERT INTO `transactions` VALUES (6, 8, 4, -30.00, '比赛报名费 - test', '2025-09-17 22:51:13');
INSERT INTO `transactions` VALUES (7, 7, 4, -30.00, '比赛报名费 - test', '2025-09-17 22:51:20');
INSERT INTO `transactions` VALUES (8, 11, 4, -30.00, '比赛报名费 - test', '2025-09-17 22:52:12');

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '真实姓名',
  `gender` int(0) NULL DEFAULT NULL COMMENT '性别: 1-男, 2-女',
  `age` int(0) NULL DEFAULT NULL COMMENT '年龄',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `campus_id` int(0) NULL DEFAULT NULL COMMENT '所属校区ID',
  `user_role` int(0) NULL DEFAULT NULL COMMENT '用户角色: 1-超级管理员, 2-校区管理员, 3-教练, 4-学员',
  `user_status` int(0) NULL DEFAULT 1 COMMENT '用户状态: 0-待审核, 1-正常, 2-禁用',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像URL (主要用于教练)',
  `achievements` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '过往成绩 (教练)',
  `coach_level` int(0) NULL DEFAULT NULL COMMENT '教练级别: 1-高级, 2-中级, 3-初级',
  `balance` decimal(10, 2) NULL COMMENT '账户余额 (学员)',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username`(`username`) USING BTREE,
  UNIQUE INDEX `uk_phone`(`phone`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES (1, 'admin', '123456', '超级管理员', NULL, NULL, NULL, NULL, NULL, 1, 1, NULL, NULL, NULL, 0.00, '2025-09-13 15:44:10', '2025-09-13 16:14:31');
INSERT INTO `users` VALUES (2, 'xiaoqu1', '123456', '校区1管理员', 1, 20, '18811112222', '', 1, 2, 1, '/uploads/images/2025/09/13/e22aedf8584a4b05be4d16c10abda911.png', '', NULL, 0.00, '2025-09-13 16:37:36', '2025-09-17 21:46:54');
INSERT INTO `users` VALUES (3, 'xiaoqu2', '123456', '校区2管理员', 1, 22, '18811113333', '', 2, 2, 1, '', '', NULL, 0.00, '2025-09-17 21:43:48', '2025-09-17 21:46:56');
INSERT INTO `users` VALUES (4, 'jiaolian1', '123456', '教练1', 1, 22, '18833335555', '', 1, 3, 1, '', '', 3, 0.00, '2025-09-17 21:44:59', '2025-09-17 22:31:24');
INSERT INTO `users` VALUES (5, 'jiaolian2', '123456', '教练2', 1, 33, '18866665555', NULL, 2, 3, 1, NULL, NULL, NULL, 0.00, '2025-09-17 21:46:34', '2025-09-17 21:47:01');
INSERT INTO `users` VALUES (6, 'xuesheng1', '123456', '学生1', 1, 22, '18800000001', NULL, 1, 4, 1, NULL, NULL, NULL, 970.00, '2025-09-17 21:47:32', '2025-09-17 22:48:58');
INSERT INTO `users` VALUES (7, 'xuesheng2', '123456', '学生2', 1, 33, '18800000002', NULL, 1, 4, 1, NULL, NULL, NULL, 9970.00, '2025-09-17 21:47:54', '2025-09-17 22:51:20');
INSERT INTO `users` VALUES (8, 'xuesheng3', '123456', '学生3', 1, 22, '18800000003', NULL, 1, 4, 1, NULL, NULL, NULL, 4970.00, '2025-09-17 21:48:13', '2025-09-17 22:51:13');
INSERT INTO `users` VALUES (9, 'xuesheng4', '123456', '学生4', 1, 22, '18800000004', NULL, 1, 4, 1, NULL, NULL, NULL, 470.00, '2025-09-17 21:48:32', '2025-09-17 22:50:00');
INSERT INTO `users` VALUES (10, 'xuesheng5', '123456', '学生5', 1, 33, '18800000005', NULL, 1, 4, 1, NULL, NULL, NULL, 770.00, '2025-09-17 21:48:57', '2025-09-17 22:50:38');
INSERT INTO `users` VALUES (11, 'xuesheng6', '123456', '学生6', 1, NULL, '18811111119', '', 1, 4, 1, '', '', NULL, 470.00, '2025-09-17 22:51:51', '2025-09-17 22:52:12');

SET FOREIGN_KEY_CHECKS = 1;
