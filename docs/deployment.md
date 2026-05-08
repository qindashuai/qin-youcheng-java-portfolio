# 部署运维文档

## 1. 环境要求

### 1.1 基础环境

| 组件 | 版本 | 说明 |
|------|------|------|
| JDK | 11+ | 推荐OpenJDK 11 |
| Maven | 3.8+ | 构建工具 |
| Docker | 20.10+ | 容器化部署 |
| Docker Compose | 2.0+ | 编排工具 |
| MySQL | 8.0+ | 数据库 |
| Redis | 6.0+ | 缓存 |
| RabbitMQ | 3.10+ | 消息队列 |
| Nacos | 2.2+ | 注册中心/配置中心 |
| Ollama | latest | 本地大模型运行 |

### 1.2 硬件要求

| 环境 | CPU | 内存 | 磁盘 |
|------|-----|------|------|
| 开发 | 4核 | 8GB | 50GB |
| 测试 | 8核 | 16GB | 100GB |
| 生产 | 16核 | 32GB | 500GB |

## 2. 本地开发部署

### 2.1 启动基础设施

```bash
# 进入项目根目录
cd qin-dashuai-java-portfolio

# 启动MySQL、Redis、RabbitMQ
docker-compose up -d mysql redis rabbitmq

# 等待服务就绪（约30秒）
docker-compose ps
```

### 2.2 启动供应链预约系统

```bash
cd supply-booking-system

# 初始化数据库
mysql -h127.0.0.1 -uroot -proot < sql/schema.sql
mysql -h127.0.0.1 -uroot -proot < sql/data.sql

# 启动服务
mvn clean spring-boot:run -Dspring-boot.run.profiles=dev
```

服务地址：http://localhost:8080

### 2.3 启动RAG知识库系统

```bash
# 安装Ollama
curl -fsSL https://ollama.com/install.sh | sh

# 拉取模型
ollama pull qwen2:7b
ollama pull nomic-embed-text

cd enterprise-rag-knowledge

# 初始化数据库
mysql -h127.0.0.1 -uroot -proot < sql/schema.sql
mysql -h127.0.0.1 -uroot -proot < sql/data.sql

# 启动服务
mvn clean spring-boot:run -Dspring-boot.run.profiles=dev
```

服务地址：http://localhost:8081

### 2.4 启动微服务权限中台

```bash
# 启动Nacos
docker-compose up -d nacos

cd micro-service-auth-center

# 初始化数据库
mysql -h127.0.0.1 -uroot -proot < sql/schema.sql
mysql -h127.0.0.1 -uroot -proot < sql/data.sql

# 构建所有模块
mvn clean package -DskipTests

# 启动网关
java -jar auth-gateway/target/auth-gateway-1.0.0.jar --spring.profiles.active=dev

# 启动认证服务
java -jar auth-system/target/auth-system-1.0.0.jar --spring.profiles.active=dev
```

网关地址：http://localhost:9000
认证服务地址：http://localhost:8082

## 3. Docker Compose 一键部署

### 3.1 全量部署

```bash
# 构建所有镜像
docker-compose build

# 启动所有服务
docker-compose --profile all up -d

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f supply-booking-system
```

### 3.2 按项目部署

```bash
# 仅启动供应链预约系统
docker-compose --profile supply up -d

# 仅启动RAG知识库系统
docker-compose --profile rag up -d

# 仅启动权限中台
docker-compose --profile auth up -d
```

### 3.3 服务端口映射

| 服务 | 端口 | 说明 |
|------|------|------|
| MySQL | 3306 | 数据库 |
| Redis | 6379 | 缓存 |
| RabbitMQ | 5672/15672 | 消息队列/管理界面 |
| Nacos | 8848 | 注册/配置中心 |
| Ollama | 11434 | 本地大模型 |
| supply-booking-system | 8080 | 供应链预约系统 |
| enterprise-rag-knowledge | 8081 | RAG知识库系统 |
| auth-gateway | 9000 | 权限中台网关 |
| auth-system | 8082 | 权限中台认证服务 |

## 4. 生产环境部署

### 4.1 JVM参数

```bash
# 供应链预约系统（高并发）
java -Xms1g -Xmx2g -XX:+UseG1GC -XX:MaxGCPauseMillis=200 \
     -jar supply-booking-system.jar --spring.profiles.active=prod

# RAG知识库系统（AI推理）
java -Xms2g -Xmx4g -XX:+UseG1GC \
     -jar enterprise-rag-knowledge.jar --spring.profiles.active=prod

# 权限中台网关
java -Xms512m -Xmx1g -XX:+UseG1GC \
     -jar auth-gateway.jar --spring.profiles.active=prod

# 权限中台认证服务
java -Xms512m -Xmx1g -XX:+UseG1GC \
     -jar auth-system.jar --spring.profiles.active=prod
```

### 4.2 MySQL配置优化

```ini
[mysqld]
innodb_buffer_pool_size = 2G
innodb_log_file_size = 512M
innodb_flush_method = O_DIRECT
max_connections = 500
slow_query_log = 1
long_query_time = 2
```

### 4.3 Redis配置优化

```conf
maxmemory 1gb
maxmemory-policy allkeys-lru
save 900 1
save 300 10
save 60 10000
```

### 4.4 Nginx反向代理

```nginx
upstream supply_booking {
    server 127.0.0.1:8080;
}

upstream rag_knowledge {
    server 127.0.0.1:8081;
}

upstream auth_gateway {
    server 127.0.0.1:9000;
}

server {
    listen 80;
    server_name api.example.com;

    location /api/v1/supply/ {
        proxy_pass http://supply_booking;
    }

    location /api/v1/rag/ {
        proxy_pass http://rag_knowledge;
    }

    location /api/v1/auth/ {
        proxy_pass http://auth_gateway;
    }
}
```

## 5. 监控告警

### 5.1 健康检查

```bash
# 供应链预约系统
curl http://localhost:8080/actuator/health

# RAG知识库系统
curl http://localhost:8081/actuator/health

# 权限中台网关
curl http://localhost:9000/actuator/health

# 权限中台认证服务
curl http://localhost:8082/actuator/health
```

### 5.2 关键指标

| 指标 | 阈值 | 告警级别 |
|------|------|----------|
| CPU使用率 | > 80% | Warning |
| 内存使用率 | > 85% | Warning |
| 接口响应时间 | > 3s | Warning |
| 接口错误率 | > 1% | Critical |
| MySQL慢查询 | > 5s | Warning |
| Redis内存 | > 80% | Warning |
| RabbitMQ队列积压 | > 1000 | Warning |

## 6. 常见问题

### Q1: MySQL连接失败
```bash
# 检查MySQL状态
docker-compose ps mysql
# 查看MySQL日志
docker-compose logs mysql
# 重启MySQL
docker-compose restart mysql
```

### Q2: Redis连接超时
```bash
# 检查Redis状态
docker-compose exec redis redis-cli ping
# 清理Redis缓存
docker-compose exec redis redis-cli FLUSHALL
```

### Q3: Ollama模型加载慢
```bash
# 预加载模型
ollama pull qwen2:7b
# 检查GPU状态
nvidia-smi
```

### Q4: Nacos注册失败
```bash
# 检查Nacos状态
curl http://localhost:8848/nacos/v1/ns/service/list?pageNo=1&pageSize=10
# 重启Nacos
docker-compose restart nacos
```
