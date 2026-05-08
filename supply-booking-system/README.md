# 供应链供应商预约管理系统

## 项目介绍

供应链供应商预约管理系统是一套面向供应链园区/物流中心的企业级管理平台，实现从供应商入驻、资质审核、分时段预约、入园门禁到收台结算的全流程数字化管理。系统采用 Spring Boot 微服务架构，通过 RabbitMQ 消息驱动实现预约→门禁→收台的数据自动联动，利用 Redis 缓存和分布式锁保障高并发场景下的数据一致性。

## 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Java | 11 | JDK |
| Spring Boot | 2.7.18 | 基础框架 |
| MySQL | 8.0 | 关系型数据库 |
| Redis | 6.x+ | 缓存/分布式锁 |
| RabbitMQ | 3.x | 消息队列 |
| MyBatis-Plus | 3.5.3 | ORM框架 |
| Apache POI | 5.2.3 | Excel导出 |
| Hutool | 5.8.18 | 工具类库 |
| Docker | - | 容器化部署 |
| Maven | 3.6+ | 项目构建 |

## 功能列表

### 1. 供应商信息管理
- 供应商CRUD操作（创建、查询、修改、删除）
- 供应商状态管理（启用/禁用）
- Redis缓存供应商信息，缓存Key设计：`supply:supplier:{id}`

### 2. 资质管理与过期预警
- 供应商资质信息管理
- 资质状态自动更新（有效/即将过期/失效）
- 定时任务每天8:00检查资质过期情况，提前3天发送预警
- 通过RabbitMQ `qualification.warning` 队列发送预警消息

### 3. 分时段预约
- 时间段管理（容量、已预约数、状态）
- 预约冲突检测（同一供应商同一时段不可重复预约）
- 并发控制：Redis分布式锁 + MyBatis-Plus乐观锁双重保障
- 预约状态流转：待确认→已确认→已入园→已完成/已取消

### 4. 入园登记与冷链车辆管理
- 入园登记（待入园→已入园→已离园）
- 冷链车辆信息管理（温控范围、容量、检验到期等）
- 入园确认自动更新预约状态

### 5. 预约→门禁→收台数据自动联动
- 预约确认后通过RabbitMQ `booking.linkage` 队列发送联动消息
- 消费者自动创建入园登记记录和收台记录
- 消息手动ACK机制确保可靠性

### 6. 后台管理与数据统计
- 预约订单统计（总量、状态分布、趋势）
- 供应商统计（总数、活跃数、资质预警数）
- 货物类型统计、供应商排名

### 7. 报表导出
- 预约订单报表导出（Excel）
- 供应商信息报表导出
- 资质信息报表导出

### 8. 高并发优化与异常处理
- Redis缓存热点数据
- 基于Redis令牌桶算法的接口限流
- 全局异常处理（业务异常、参数校验异常、系统异常）
- AOP操作日志记录

## 简历亮点

1. **高并发预约系统设计**：采用 Redis 分布式锁 + MyBatis-Plus 乐观锁双重并发控制，解决分时段预约的库存超卖问题，保证数据一致性
2. **消息驱动架构**：基于 RabbitMQ 实现预约→门禁→收台的数据自动联动，解耦业务模块，提升系统扩展性
3. **资质过期预警机制**：Spring Scheduled 定时任务 + RabbitMQ 异步消息，实现提前3天资质过期预警通知
4. **接口限流**：基于 Redis 令牌桶算法实现接口限流，防止恶意请求和流量突增
5. **多级缓存策略**：Redis 缓存供应商热点数据，减少数据库压力，缓存Key设计规范统一
6. **统一异常处理**：GlobalExceptionHandler 全局异常拦截，BusinessException 业务异常封装，Result 统一返回格式
7. **AOP操作日志**：自定义注解 + 切面编程，实现声明式操作日志记录

## 本地启动步骤

### 环境准备
- JDK 11+
- MySQL 8.0+
- Redis 6.x+
- RabbitMQ 3.x+
- Maven 3.6+

### 1. 初始化数据库
```bash
mysql -u root -p < sql/schema.sql
mysql -u root -p < sql/data.sql
```

### 2. 修改配置
编辑 `src/main/resources/application-dev.yml`，修改数据库、Redis、RabbitMQ连接信息

### 3. 编译打包
```bash
mvn clean package -DskipTests
```

### 4. 启动服务
```bash
java -jar target/supply-booking-system-1.0.0.jar --spring.profiles.active=dev
```

或直接通过IDE运行 `SupplyBookingApplication.main()`

## Docker部署步骤

### 1. 构建镜像
```bash
mvn clean package -DskipTests
docker build -t supply-booking-system:1.0.0 .
```

### 2. 使用 Docker Compose 部署
```yaml
version: '3.8'
services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: root123
      MYSQL_DATABASE: supply_booking
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
      - ./sql:/docker-entrypoint-initdb.d

  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"

  rabbitmq:
    image: rabbitmq:3-management
    ports:
      - "5672:5672"
      - "15672:15672"

  app:
    image: supply-booking-system:1.0.0
    ports:
      - "8080:8080"
    environment:
      SPRING_PROFILES_ACTIVE: prod
      MYSQL_HOST: mysql
      MYSQL_USERNAME: root
      MYSQL_PASSWORD: root123
      REDIS_HOST: redis
      RABBITMQ_HOST: rabbitmq
    depends_on:
      - mysql
      - redis
      - rabbitmq

volumes:
  mysql_data:
```

```bash
docker-compose up -d
```

## 接口文档概览

| 模块 | 接口 | 方法 | 说明 |
|------|------|------|------|
| 供应商 | /api/v1/supplier | POST | 创建供应商 |
| 供应商 | /api/v1/supplier | PUT | 更新供应商 |
| 供应商 | /api/v1/supplier/{id} | DELETE | 删除供应商 |
| 供应商 | /api/v1/supplier/{id} | GET | 查询供应商详情 |
| 供应商 | /api/v1/supplier/page | GET | 分页查询供应商 |
| 供应商 | /api/v1/supplier/{id}/status | PUT | 更新供应商状态 |
| 供应商 | /api/v1/supplier/active | GET | 查询所有启用供应商 |
| 资质 | /api/v1/qualification | POST | 新增资质 |
| 资质 | /api/v1/qualification | PUT | 更新资质 |
| 资质 | /api/v1/qualification/{id} | DELETE | 删除资质 |
| 资质 | /api/v1/qualification/page | GET | 分页查询资质 |
| 资质 | /api/v1/qualification/expiring | GET | 查询即将过期资质 |
| 预约 | /api/v1/booking | POST | 创建预约 |
| 预约 | /api/v1/booking/{id}/confirm | PUT | 确认预约 |
| 预约 | /api/v1/booking/{id}/cancel | PUT | 取消预约 |
| 预约 | /api/v1/booking/{id} | GET | 查询预约详情 |
| 预约 | /api/v1/booking/page | GET | 分页查询预约 |
| 预约 | /api/v1/booking/time-slots | GET | 查询可用时间段 |
| 入园 | /api/v1/park-entry | POST | 创建入园登记 |
| 入园 | /api/v1/park-entry/{id}/entry | PUT | 确认入园 |
| 入园 | /api/v1/park-entry/{id}/exit | PUT | 确认离园 |
| 入园 | /api/v1/park-entry/page | GET | 分页查询入园记录 |
| 车辆 | /api/v1/vehicle | POST | 新增冷链车辆 |
| 车辆 | /api/v1/vehicle | PUT | 更新冷链车辆 |
| 车辆 | /api/v1/vehicle/{id} | DELETE | 删除冷链车辆 |
| 车辆 | /api/v1/vehicle/{id} | GET | 查询车辆详情 |
| 车辆 | /api/v1/vehicle/page | GET | 分页查询车辆 |
| 收台 | /api/v1/receiving | POST | 创建收台记录 |
| 收台 | /api/v1/receiving | PUT | 更新收台记录 |
| 收台 | /api/v1/receiving/{id} | GET | 查询收台详情 |
| 收台 | /api/v1/receiving/page | GET | 分页查询收台记录 |
| 统计 | /api/v1/statistics/overview | GET | 总览统计 |
| 统计 | /api/v1/statistics/booking | GET | 预约统计 |
| 统计 | /api/v1/statistics/supplier | GET | 供应商统计 |
| 报表 | /api/v1/report/booking | GET | 导出预约报表 |
| 报表 | /api/v1/report/supplier | GET | 导出供应商报表 |
| 报表 | /api/v1/report/qualification | GET | 导出资质报表 |
