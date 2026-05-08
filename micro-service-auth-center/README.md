# 微服务权限中台 (Micro Service Auth Center)

企业级统一认证授权平台，基于 Spring Cloud Alibaba 构建，提供完整的 RBAC 权限管理、JWT 鉴权、SSO 单点登录、接口限流、操作审计等功能。

## 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Java | 11 | JDK 版本 |
| Spring Boot | 2.7.18 | 基础框架 |
| Spring Cloud | 2021.0.8 | 微服务框架 |
| Spring Cloud Alibaba | 2021.0.5.0 | 微服务组件 |
| Nacos | 2.2.x | 服务注册与发现、配置中心 |
| Sentinel | 1.8.6 | 流量控制、熔断降级 |
| MySQL | 8.0 | 关系型数据库 |
| Redis | 7.x | 缓存、会话存储 |
| MyBatis-Plus | 3.5.3 | ORM 框架 |
| JWT (jjwt) | 0.11.5 | Token 鉴权 |
| Druid | 1.2.16 | 数据库连接池 |
| Docker | - | 容器化部署 |

## 功能列表

### 核心功能
- **RBAC 权限模型**：用户 → 角色 → 菜单（权限），支持数据权限（按部门/组织隔离数据）
- **统一登录认证**：用户名密码登录，BCrypt 密码加密
- **JWT 鉴权**：access_token（2小时）+ refresh_token（7天），Redis 存储会话信息
- **SSO 单点登录**：基于 Redis 共享 Token，一处登录处处可用，一处登出处处失效
- **多系统接入**：通过 app_key/app_secret 标识不同系统，Token 中携带系统标识

### 安全防护
- **网关鉴权过滤**：AuthGlobalFilter 拦截所有请求，校验 JWT Token，白名单路径放行
- **IP 黑名单**：网关层 IP 黑名单过滤，Redis 实时同步
- **Token 黑名单**：登出/踢出时 Token 加入黑名单
- **登录防暴力破解**：5次失败后锁定30分钟
- **Sentinel 限流**：网关层 QPS 限流 + IP 级别限流，认证服务接口级别限流
- **降级熔断**：Sentinel 熔断降级保护

### 运维审计
- **操作日志**：AOP 切面自动记录操作人、操作类型、请求参数、响应结果、IP、耗时
- **审计追踪**：全链路 traceId 追踪
- **统一返回**：Result<T> 统一返回格式
- **全局异常处理**：BusinessException + 全局异常处理器

## 架构说明

```
                    ┌─────────────┐
                    │   客户端     │
                    └──────┬──────┘
                           │
                    ┌──────▼──────┐
                    │  Nginx/LB   │
                    └──────┬──────┘
                           │
              ┌────────────▼────────────┐
              │     auth-gateway        │
              │  ┌─────────────────┐    │
              │  │ BlackListFilter │    │  ← IP黑名单过滤
              │  ├─────────────────┤    │
              │  │ AuthGlobalFilter│    │  ← JWT鉴权过滤
              │  ├─────────────────┤    │
              │  │ Sentinel限流    │    │  ← 流量控制
              │  └─────────────────┘    │
              └────────────┬────────────┘
                           │
              ┌────────────▼────────────┐
              │     auth-system         │
              │  ┌─────────────────┐    │
              │  │ AuthController  │    │  ← 登录/登出/SSO
              │  ├─────────────────┤    │
              │  │ UserController  │    │  ← 用户CRUD
              │  ├─────────────────┤    │
              │  │ RoleController  │    │  ← 角色CRUD
              │  ├─────────────────┤    │
              │  │ MenuController  │    │  ← 菜单CRUD
              │  ├─────────────────┤    │
              │  │ DataPermission  │    │  ← 数据权限
              │  ├─────────────────┤    │
              │  │ BlackListCtrl   │    │  ← 黑名单管理
              │  ├─────────────────┤    │
              │  │ OperationLog    │    │  ← 操作日志
              │  └─────────────────┘    │
              └────────────┬────────────┘
                    ┌──────┴──────┐
              ┌─────▼───┐  ┌─────▼───┐
              │  MySQL  │  │  Redis  │
              └─────────┘  └─────────┘
```

## 项目结构

```
micro-service-auth-center/
├── pom.xml                    # 父POM（依赖管理）
├── docker-compose.yml         # Docker编排
├── sql/                       # 数据库脚本
│   ├── schema.sql             # 建表语句
│   └── data.sql               # 初始化数据
├── auth-common/               # 公共模块
│   └── src/.../common/
│       ├── Result.java        # 统一返回
│       ├── ResultCode.java    # 状态码枚举
│       ├── GlobalExceptionHandler.java  # 全局异常
│       ├── BusinessException.java       # 业务异常
│       ├── PageRequest.java   # 分页请求
│       ├── PageResult.java    # 分页结果
│       ├── JwtUtil.java       # JWT工具类
│       ├── RedisUtil.java     # Redis工具类
│       ├── UserContext.java   # 用户上下文
│       └── UserContextHolder.java  # 上下文持有者
├── auth-gateway/              # 网关服务 (端口:8080)
│   └── src/.../gateway/
│       ├── filter/            # 过滤器
│       │   ├── AuthGlobalFilter.java   # JWT鉴权
│       │   └── BlackListFilter.java    # 黑名单
│       ├── handler/           # 异常处理
│       └── config/            # 配置
│           ├── RouteConfig.java        # 路由配置
│           └── SentinelGatewayConfig.java  # 限流配置
└── auth-system/               # 认证服务 (端口:8081)
    └── src/.../system/
        ├── entity/            # 实体类
        ├── mapper/            # 数据访问层
        ├── service/           # 业务逻辑层
        ├── controller/        # 控制器层
        ├── dto/               # 数据传输对象
        ├── vo/                # 视图对象
        ├── aspect/            # 切面
        └── config/            # 配置
```

## 本地启动步骤

### 前置条件
- JDK 11+
- Maven 3.6+
- MySQL 8.0
- Redis 7.x
- Nacos 2.2.x

### 1. 启动基础设施

```bash
# 启动 Nacos (单机模式)
sh startup.sh -m standalone

# 启动 Redis
redis-server

# 启动 Sentinel Dashboard
java -jar sentinel-dashboard-1.8.6.jar
```

### 2. 初始化数据库

```bash
# 执行建表脚本
mysql -u root -p < sql/schema.sql

# 执行初始化数据
mysql -u root -p < sql/data.sql
```

### 3. 编译项目

```bash
cd micro-service-auth-center
mvn clean install -DskipTests
```

### 4. 启动服务

```bash
# 启动认证服务
cd auth-system
mvn spring-boot:run

# 启动网关服务
cd auth-gateway
mvn spring-boot:run
```

### 5. 验证

```bash
# 登录获取Token
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123","appKey":"default_system"}'

# 使用Token访问接口
curl http://localhost:8080/api/v1/users \
  -H "Authorization: Bearer <access_token>"
```

## Docker 部署步骤

```bash
# 构建并启动所有服务
docker-compose up -d

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f auth-gateway
docker-compose logs -f auth-system

# 停止所有服务
docker-compose down

# 停止并清除数据卷
docker-compose down -v
```

## 接口文档概览

### 认证接口 `/api/v1/auth`

| 方法 | 路径 | 说明 | 是否鉴权 |
|------|------|------|----------|
| POST | /login | 用户登录 | 否 |
| POST | /logout | 用户登出 | 是 |
| POST | /refresh | 刷新Token | 否 |
| GET | /sso/ticket | 生成SSO Ticket | 是 |
| POST | /sso/callback | SSO回调登录 | 否 |

### 用户接口 `/api/v1/users`

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | / | 用户列表 | system:user:list |
| GET | /{id} | 用户详情 | system:user:list |
| POST | / | 创建用户 | system:user:add |
| PUT | /{id} | 更新用户 | system:user:edit |
| DELETE | /{id} | 删除用户 | system:user:delete |
| PUT | /{id}/reset-password | 重置密码 | system:user:resetPwd |
| PUT | /{id}/roles | 分配角色 | system:user:edit |

### 角色接口 `/api/v1/roles`

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | / | 角色列表 | system:role:list |
| GET | /all | 所有角色 | system:role:list |
| GET | /{id} | 角色详情 | system:role:list |
| POST | / | 创建角色 | system:role:add |
| PUT | /{id} | 更新角色 | system:role:edit |
| DELETE | /{id} | 删除角色 | system:role:delete |
| PUT | /{id}/menus | 分配菜单 | system:role:edit |
| GET | /user/{userId} | 用户角色 | system:role:list |

### 菜单接口 `/api/v1/menus`

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | /tree | 菜单树 | system:menu:list |
| GET | /{id} | 菜单详情 | system:menu:list |
| POST | / | 创建菜单 | system:menu:add |
| PUT | /{id} | 更新菜单 | system:menu:edit |
| DELETE | /{id} | 删除菜单 | system:menu:delete |
| GET | /role/{roleId} | 角色菜单 | system:menu:list |
| GET | /user/{userId} | 用户菜单 | system:menu:list |

### 数据权限接口 `/api/v1/data-permissions`

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | / | 数据权限列表 |
| GET | /{id} | 数据权限详情 |
| POST | / | 创建数据权限 |
| PUT | /{id} | 更新数据权限 |
| DELETE | /{id} | 删除数据权限 |
| GET | /role/{roleId} | 角色数据权限 |

### 黑名单接口 `/api/v1/blacklist`

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | / | 黑名单列表 |
| POST | / | 添加黑名单 |
| DELETE | /{id} | 移除黑名单 |
| GET | /check/ip | 检查IP |
| GET | /check/token | 检查Token |

### 操作日志接口 `/api/v1/logs`

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | / | 日志列表 |

## 简历亮点

1. **微服务架构设计**：基于 Spring Cloud Alibaba 构建企业级权限中台，采用网关 + 认证服务的微服务架构，实现服务注册发现（Nacos）、流量控制（Sentinel）、统一路由鉴权
2. **JWT + Redis 双Token机制**：设计 access_token/refresh_token 双Token方案，Redis 存储会话信息实现Token主动失效和SSO单点登录，解决JWT无法主动失效的痛点
3. **SSO单点登录**：基于 Redis 共享Token实现跨系统单点登录，通过 Ticket 机制实现安全回调，一处登录处处可用，一处登出处处失效
4. **RBAC + 数据权限**：实现用户→角色→菜单（权限）的RBAC模型，并扩展数据权限层，支持按部门/组织隔离数据，满足企业级多租户数据安全需求
5. **网关统一鉴权**：自研 AuthGlobalFilter + BlackListFilter 双层过滤，实现JWT鉴权、IP黑名单、Token黑名单三重安全防护，白名单路径自动放行
6. **Sentinel 流量防护**：网关层配置QPS限流 + IP级别限流，认证服务接口级别限流，实现多级流量防护和熔断降级
7. **AOP操作审计**：基于自定义注解 + AOP切面实现无侵入式操作日志记录，自动采集操作人、请求参数、响应结果、IP、耗时等审计信息
8. **多系统接入**：通过 app_key/app_secret 标识不同接入系统，Token中携带系统标识，实现一套权限体系支撑多个业务系统
