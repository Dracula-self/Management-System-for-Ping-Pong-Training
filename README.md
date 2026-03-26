# 乒乓球培训管理系统

## 项目介绍

本项目是一个面向培训场景的 **乒乓球培训管理系统**（Table-tennis Training Management System，TTTMS）。

系统旨在帮助乒乓球培训机构完成**学员报名、教练入职、双向选择、课程预约与取消、学费收退费、训练记录与评价、月赛管理**等全流程数字化管理，替代传统手工记录方式，提升管理效率与用户体验，支持多校区运营。

系统采用 **B/S 架构**，后端基于 **Spring Boot 3 + MyBatis + MySQL**，前端基于 **Vue 3 + Element Plus** 构建，是一套完整的 Web 管理应用。

---

## 用户角色

| 角色 | 描述 | 核心目标 |
|------|------|----------|
| 超级管理员 | 系统最高权限者，通常是机构总负责人 | 管理所有校区，监控系统运行，需每年付费获取使用密钥 |
| 校区管理员 | 单个校区的运营管理者 | 高效处理本校区日常事务（用户审核、课程、财务、日志查询） |
| 教练 | 提供培训服务的雇员，分高/中/初级 | 审核学员双选申请，确认课程预约，填写学员评价 |
| 学员 | 接受培训的消费者 | 查询选择教练，预约课程，支付费用，参与评价和月赛 |

---

## 功能模块

### 1. 用户与权限管理
- 学员在线注册（密码需 8-16 位，含字母、数字、特殊字符）
- 教练提交注册申请（含照片、获奖描述），由校区管理员审核并指定等级后生效
- 用户个人信息维护（密码、头像等）
- 管理员对学员/教练的查询、修改、删除（逻辑删除）

### 2. 教练查询与双选管理
- 学员按姓名、性别、年龄等条件查询教练，查看详情（含照片、获奖信息）
- 学员选择教练并提交双选申请（每名学员最多关联 2 位教练）
- 教练审核申请（每位教练最多指导 20 名学员）
- 学员可发起更换教练请求

### 3. 课程预约与取消管理
- 学员查看已建立双选关系的教练未来一周课程安排
- 学员选择空闲时段和球台预约课程（支持指定球台或系统自动分配）
- 教练对预约请求进行确认或拒绝
- 开课前 24 小时以上，双方均可发起取消（需另一方确认），每月限取消 3 次

### 4. 财务管理
- 学员通过线上（模拟微信/支付宝）或线下（管理员录入）方式充值（单次上限 10,000 元）
- 教练确认预约后系统按等级自动扣款：
  - 高级教练：150 元/小时
  - 中级教练：120 元/小时
  - 初级教练：80 元/小时
- 课程取消后自动退款至学员账户
- 月赛报名费 30 元，月赛取消后自动退还
- 余额不足时系统提醒用户

### 5. 月赛管理
- 学员在指定时间内选择组别（甲/乙/丙）报名参赛，自动扣除报名费
- 系统根据报名人数自动排赛：≤6 人采用全循环赛制；>6 人先循环赛后交叉淘汰赛
- 学员可查询自己的比赛时间、对手和球台安排

### 6. 训练评价系统
- 课程结束后，学员填写"收获与教训"，教练填写"表现与建议"
- 双方可查询、浏览历史评价记录

### 7. 系统通知与日志
- 站内信通知：上课前 1 小时提醒、教练更改通知、余额不足提醒、各类操作结果
- 操作日志记录（登录、注册、预约、支付、信息修改等），供管理员查询
- 系统许可密钥验证（超级管理员需每年支付 500 元服务费获取密钥）

---

## 技术栈

| 层次 | 技术/工具 | 版本 | 主要职责 |
|------|----------|------|----------|
| 后端框架 | Spring Boot | 3.3.13 | 快速构建后端应用，提供 RESTful API |
| ORM | MyBatis | 3.0.3 | 数据库操作与 SQL 映射 |
| 分页 | PageHelper | 6.0+ | 分页查询插件 |
| 数据库 | MySQL | 8.0+ | 关系型数据库 |
| 连接池 | HikariCP | 4.0+ | 高性能 JDBC 连接池 |
| 安全认证 | JWT（jjwt） | 0.11.5 | 无状态身份认证 |
| 验证码 | Kaptcha | 2.3.2 | 图形验证码生成与验证（AES 加密） |
| 日志 | Logback | 1.2+ | 日志记录，按日期分割 |
| 构建工具 | Maven | 3.6+ | 依赖管理与项目构建 |
| 开发环境 | JDK | 17 | Java 编译与运行环境 |
| 前端框架 | Vue 3 + Element Plus | - | 单页应用，动态页面与用户交互 |
| 前端工具 | Tailwind CSS、Iconify | - | 样式与图标 |
| 序列化 | Jackson | 2.15.2 | JSON 序列化/反序列化 |

---

## 系统架构

系统采用 **前后端分离的 B/S 架构**，各层职责如下：

```
前端（Vue 3 + Element Plus）
    ↕ HTTP/HTTPS（JSON）
后端（Spring Boot + MyBatis）
    ↕ SQL
数据库（MySQL 8.0）
```

- **前端**：构建单页应用（SPA），通过 API 调用获取 JSON 数据渲染页面，响应用户操作。
- **后端**：处理业务逻辑（用户管理、双选关系、预约、月赛、支付、评价、消息通知等），读写数据库。
- **数据库**：持久化存储所有业务数据（用户、校区、课程、预约、财务、评价、比赛等）。

---

## 文件架构

```text
pingpong/
├── .idea/                          # IDE 配置目录
├── logs/                           # 日志目录
├── src/
│   └── main/
│       ├── java/com/quan/project/
│       │   ├── common/             # 公共工具类（R、UserContext）
│       │   ├── config/             # 配置类（Kaptcha、WebConfig）
│       │   ├── controller/         # 控制器层（13 个模块）
│       │   ├── dto/                # 数据传输对象
│       │   ├── entity/             # 实体类（12 个）
│       │   ├── exception/          # 全局异常处理
│       │   ├── interceptor/        # JWT 拦截器
│       │   ├── mapper/             # MyBatis Mapper 接口
│       │   ├── service/            # 业务逻辑层
│       │   └── utils/              # 工具类（JWT、文件上传）
│       └── resources/
│           ├── mapper/             # MyBatis XML 映射文件
│           ├── static/             # 前端静态资源（Vue、Element Plus 等）
│           ├── application.yml     # 应用配置
│           └── logback-spring.xml  # 日志配置
├── target/                         # Maven 构建输出目录
├── init.sql                        # 业务初始化脚本
├── system-tables.sql               # 系统表结构脚本
└── pom.xml                         # Maven 构建与依赖配置
```

---

## 数据库设计

系统共 12 张核心业务表：

| 表名 | 说明 |
|------|------|
| `users` | 用户表（管理员、教练、学员，含余额、头像、教练等级等字段） |
| `campus` | 校区表（校区信息、联系人、管理员关联） |
| `coach_student_relations` | 师生关系表（双选申请与确认状态） |
| `tables` | 球台表（各校区乒乓球台信息） |
| `appointments` | 课程预约表（预约状态、时间、费用） |
| `evaluations` | 训练评价表（学员评价 + 教练评价） |
| `transactions` | 账户流水表（充值、课程消费、退款、报名费） |
| `competitions` | 比赛表（月赛信息、状态） |
| `competition_participants` | 比赛报名表（学员报名记录与组别） |
| `matches` | 比赛对阵表（对阵安排、赛制、结果） |
| `system_logs` | 系统日志表（用户操作记录） |
| `system_messages` | 系统消息表（通知、公告） |

---

## API 接口概览

系统共 13 个控制器模块，接口路径统一以 `/api` 开头，认证方式为 JWT（`Authorization: Bearer {token}`）：

| 模块 | 控制器 | 主要接口路径 |
|------|--------|-------------|
| 用户管理 | `UserController` | `/api/users` |
| 验证码 | `CaptchaController` | `/api/captcha` |
| 校区管理 | `CampusController` | `/api/campus` |
| 师生关系 | `CoachStudentRelationController` | `/api/coach-student-relations` |
| 课程预约 | `AppointmentController` | `/api/appointments` |
| 交易流水 | `TransactionController` | `/api/transactions` |
| 训练评价 | `EvaluationController` | `/api/evaluations` |
| 月赛管理 | `CompetitionController` | `/api/competitions` |
| 对阵管理 | `MatchController` | `/api/matches` |
| 球台管理 | `TableController` | `/api/tables` |
| 系统消息 | `SystemMessageController` | `/api/system-messages` |
| 系统日志 | `SystemLogController` | `/api/system-logs` |
| 文件上传 | `FileUploadController` | `/api/file` |

---

## 如何运行项目

### 1. 环境准备
请先安装：
- JDK 17
- Maven 3.8+
- MySQL 8.x

### 2. 克隆项目
```bash
git clone https://github.com/Dracula-self/pingpong.git
cd pingpong
```

### 3. 初始化数据库
在 MySQL 中新建数据库（例如：`pingpong`），执行仓库中的 SQL：

```sql
source system-tables.sql;
source init.sql;
```

### 4. 配置数据库连接
修改 `src/main/resources/application.yml`（或 `application.properties`）中的数据源配置：

```yaml
spring:
  datasource:
    url: jdbc:mysql://127.0.0.1:3306/pingpong?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai
    username: root
    password: your_password
```

### 5. 启动项目
在项目根目录执行：

```bash
mvn clean package
mvn spring-boot:run
```

或运行打包产物：

```bash
java -jar target/project-0.0.1-SNAPSHOT.jar
```

> `pom.xml` 中配置的启动类为：`com.quan.project.ProjectApplication`。

### 6. 访问系统
默认访问地址：

```text
http://localhost:8080
```

## 测试

- **后端单元测试**：JUnit 5 + Mockito，覆盖核心 Service 与工具类（UserContext、JwtUtil、AppointmentService、CompetitionService 等），方法覆盖率约 90%。
- **前端单元测试**：Jest + Testing Library，覆盖工具函数（日期格式化）及核心 Vue 组件（课表、月赛报名等）。
- **接口测试**：Apifox，验证全部 RESTful 接口的响应格式、业务逻辑与状态码。
- **端到端测试（可选）**：Selenium，用于核心流程自动化回归测试。
- **缺陷管理**：TAPD。
- **测试浏览器**：Chrome、Edge。

---

## 常见问题

### 启动失败：数据库连接错误
- 检查 MySQL 是否启动
- 检查 URL、用户名、密码是否正确
- 检查数据库是否已创建并导入 SQL

### 启动失败：表不存在
- 确认已执行 `system-tables.sql` 和 `init.sql`
- 确认执行 SQL 的数据库与项目连接配置一致

### 端口被占用
- 修改 `application.yml` 中 `server.port`
- 或释放占用端口后重启
