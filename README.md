# 乒乓球课程管理系统

## 项目介绍
本项目是一个面向培训场景的 **乒乓球课程管理系统**。  
用于管理课程、学员、教练及相关业务数据，帮助实现课程信息维护、数据查询与管理流程规范化。

项目后端采用 **Spring Boot 3 + MyBatis + MySQL** 技术栈构建，具备典型管理系统所需的基础能力，包括 Web 接口、数据库持久化、分页、鉴权与验证码支持。

---

## 技术栈
- Java 17
- Spring Boot 3.3.13
- MyBatis（mybatis-spring-boot-starter 3.0.3）
- MySQL 8（mysql-connector-j 8.0.33）
- PageHelper 分页插件
- JWT（jjwt）
- Kaptcha 验证码
- Maven

---

## 文件架构
根据当前仓库可确认的结构如下：

```text
pingpong/
├── .idea/                  # IDE 配置目录
├── logs/                   # 日志目录
├── src/                    # 项目源代码目录
│   └── main/               # 主代码目录（Java / resources 等）
├── uploads/                # 上传文件目录
├── target/                 # Maven 构建输出目录
├── init.sql                # 业务初始化脚本
├── system-tables.sql       # 系统表结构脚本
└── pom.xml                 # Maven 构建与依赖配置
```

> 说明：`src/main` 下通常包含 `java` 与 `resources` 等目录，实际以仓库代码为准。

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

> 建议先执行 `system-tables.sql`，再执行 `init.sql`。

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

如修改了端口，请以配置文件为准。

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

---

## 说明
当前 README 聚焦三个核心点：**项目介绍、文件架构、运行方式**。  
后续如需，我可以再帮你补充：接口文档、模块说明、部署说明（Docker/Nginx）和账号初始化说明。