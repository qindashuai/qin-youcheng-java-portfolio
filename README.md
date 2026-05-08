# 秦友成｜Java 后端技术作品集

> 10 年 Java 后端开发｜专注供应链 / 物流 / 仓储系统｜微服务 / 高并发 / 性能优化｜熟悉企业级 AI 落地

## 个人简介

- **姓名**：秦友成
- **经验**：10 年 Java 后端开发
- **领域**：供应链管理、物流仓储、园区调度系统
- **技术方向**：微服务架构、高并发优化、企业级 AI 应用落地
- **核心能力**：系统架构设计、性能调优、技术团队管理

## 技术栈

| 类别 | 技术 |
|------|------|
| 语言 | Java 11 |
| 框架 | Spring Boot 2.7.x、Spring Cloud Alibaba |
| 数据库 | MySQL 8、Redis |
| 消息队列 | RabbitMQ |
| ORM | MyBatis-Plus |
| AI | Ollama、DeepSeek/Qwen、LangChain |
| 容器化 | Docker、Docker Compose |
| 构建 | Maven |
| 版本控制 | Git |

## 项目清单

| # | 项目 | 说明 | 核心技术 |
|---|------|------|----------|
| 1 | [供应链供应商预约管理系统](./supply-booking-system) | 核心主项目，园区供应商预约全流程管理 | Spring Boot、MySQL、Redis、RabbitMQ、MyBatis-Plus |
| 2 | [企业 RAG 知识库问答系统](./enterprise-rag-knowledge) | AI 加分项，私有化部署的企业知识库 | Spring Boot、Ollama、DeepSeek/Qwen、LangChain |
| 3 | [微服务权限中台](./micro-service-auth-center) | 架构能力证明，统一认证授权平台 | Spring Cloud Alibaba、Nacos、Sentinel、JWT |
| 4 | [Java 通用工具库](./java-common-toolkit) | 底层功底，企业级通用组件封装 | Java 11、Spring Boot Starter |

## 简历亮点

### 供应链供应商预约管理系统
- 支撑日 800+ 供应商预约，信息准确率 100%
- 园区拥堵率下降 70%，入库延误率从 12% 降至 0.5%
- 全流程线上化，每月减少人工 60 小时

### 企业 RAG 知识库问答系统
- 企业级私有化部署，数据安全合规
- 回答准确率 90%+，降低重复咨询 50%
- 可直接集成到现有业务系统使用

### 微服务权限中台
- 统一认证授权，支撑 5+ 业务系统接入
- RBAC + 数据权限，精细化权限管控
- 限流熔断降级，系统可用性 99.9%

## 快速开始

### 本地一键启动

```bash
# 克隆仓库
git clone https://github.com/qinyoucheng/qin-youcheng-java-portfolio.git
cd qin-youcheng-java-portfolio

# 启动基础设施（MySQL、Redis、RabbitMQ）
docker-compose up -d

# 启动各项目（按需）
cd supply-booking-system && mvn spring-boot:run
cd enterprise-rag-knowledge && mvn spring-boot:run
cd micro-service-auth-center && mvn spring-boot:run
```

### Docker Compose 一键部署

```bash
docker-compose --profile all up -d
```

## 文档

- [架构设计文档](./docs/architecture.md)
- [部署运维文档](./docs/deployment.md)
- [接口文档](./docs/api-docs.md)
- [压测报告](./docs/stress-test-report.md)

## 联系方式

- **邮箱**：qinyoucheng@example.com
- **GitHub**：https://github.com/qinyoucheng

---

> 本作品集所有项目均为企业级标准实现，包含完整业务逻辑、异常处理、日志监控、接口限流等生产级特性。
