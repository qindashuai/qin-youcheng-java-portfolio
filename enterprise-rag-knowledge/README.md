# 企业级RAG知识库问答系统

## 项目介绍

基于 Spring Boot + LangChain4j + Ollama 构建的企业级 RAG（Retrieval-Augmented Generation）知识库问答系统。支持文档上传、智能分块、向量化存储、意图识别、精准问答、打分反馈等完整功能链路，适用于企业内部制度查询、故障排查、流程问答等场景。

## 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Java | 11 | JDK版本 |
| Spring Boot | 2.7.18 | 基础框架 |
| MyBatis-Plus | 3.5.3 | ORM框架 |
| MySQL | 8.x | 关系型数据库 |
| Redis | 7.x | 缓存中间件 |
| LangChain4j | 0.35.0 | LLM编排框架 |
| Ollama | latest | 本地大模型运行时 |
| DeepSeek/Qwen | - | 对话/Embedding模型 |
| PDFBox | 2.0.30 | PDF文档解析 |
| Apache POI | 5.2.5 | Word文档解析 |
| Docker | - | 容器化部署 |

## 功能列表

### 核心功能
- **文档管理**：支持 PDF、Word(DOCX)、TXT 文档上传，异步解析处理
- **智能分块**：固定大小分块（默认512 tokens，重叠64 tokens）+ 语义分块双策略
- **向量化存储**：基于 Ollama 本地 Embedding 模型，向量存储于 MySQL（可替换 Milvus）
- **RAG问答**：用户提问 → 意图识别 → 向量检索 → Prompt构造 → LLM生成 → 历史记录
- **意图识别**：关键词匹配识别（制度查询/故障排查/流程问答/其他）
- **打分反馈**：1-5星评分 + 文字反馈，持续优化问答质量

### 业务场景
- 内部制度查询：考勤、报销、请假、薪酬等制度快速查询
- 故障排查：系统故障诊断、错误码解析、运维指南
- 流程问答：审批流程、操作指南、业务办理步骤

### 系统特性
- 私有化部署：Ollama本地运行，数据不出企业
- Redis缓存：热门问题缓存、对话上下文缓存
- 统一返回格式：Result<T> 封装，全局异常处理
- RESTful API：统一 /api/v1 前缀
- 操作日志：AOP切面记录关键操作
- Docker部署：一键容器化部署

## RAG架构说明

```
用户提问
   │
   ▼
┌─────────────┐
│  意图识别     │ ← 关键词匹配 + LLM辅助
└──────┬──────┘
       │
       ▼
┌─────────────┐
│  向量检索     │ ← Ollama Embedding + 余弦相似度
└──────┬──────┘
       │
       ▼
┌─────────────┐
│  Prompt构造   │ ← System Prompt + 检索上下文 + 历史对话
└──────┬──────┘
       │
       ▼
┌─────────────┐
│  LLM生成     │ ← Ollama (DeepSeek/Qwen)
└──────┬──────┘
       │
       ▼
┌─────────────┐
│  回答+反馈    │ ← 记录历史 + 用户评分
└─────────────┘
```

### 文档处理流程

```
文档上传 → 文件存储 → 异步解析 → 智能分块 → 向量化 → 存储
  │                      │           │          │
  ▼                      ▼           ▼          ▼
PDF/Word/TXT      PDFBox/POI   策略模式     Ollama
                               FixedSize   Embedding
                               Semantic    Model
```

## 本地启动步骤

### 1. 环境准备

```bash
# 安装 MySQL 8.x 并创建数据库
mysql -u root -p < sql/schema.sql
mysql -u root -p < sql/data.sql

# 安装 Redis
# macOS
brew install redis && brew services start redis
# Ubuntu
sudo apt install redis-server && sudo systemctl start redis
# Windows: 下载 Redis for Windows
```

### 2. 安装 Ollama 并下载模型

```bash
# 安装 Ollama
# macOS/Linux
curl -fsSL https://ollama.com/install.sh | sh
# Windows: 从 https://ollama.com 下载安装包

# 下载对话模型
ollama pull deepseek-r1:7b
# 或使用 Qwen
ollama pull qwen2.5:7b

# 下载 Embedding 模型
ollama pull nomic-embed-text

# 启动 Ollama 服务
ollama serve
```

### 3. 启动应用

```bash
# 编译项目
mvn clean package -DskipTests

# 运行
java -jar target/enterprise-rag-knowledge-1.0.0.jar --spring.profiles.active=dev

# 或使用 Maven 直接运行
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### 4. 验证

```bash
# 健康检查
curl http://localhost:8080/api/v1/knowledge-bases/list

# 上传文档
curl -X POST http://localhost:8080/api/v1/documents/upload \
  -F "file=@test.pdf" \
  -F "knowledgeBaseId=1" \
  -F "chunkStrategy=FIXED_SIZE" \
  -F "chunkSize=512" \
  -F "chunkOverlap=64"

# 问答
curl -X POST http://localhost:8080/api/v1/chat/ask \
  -H "Content-Type: application/json" \
  -d '{"question":"公司请假制度是什么？","knowledgeBaseId":1}'
```

## Docker部署步骤

### 1. 创建 docker-compose.yml

```yaml
version: '3.8'
services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: enterprise_rag
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
      - ./sql/schema.sql:/docker-entrypoint-initdb.d/01-schema.sql
      - ./sql/data.sql:/docker-entrypoint-initdb.d/02-data.sql

  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"

  ollama:
    image: ollama/ollama:latest
    ports:
      - "11434:11434"
    volumes:
      - ollama_data:/root/.ollama

  rag-app:
    build: .
    ports:
      - "8080:8080"
    environment:
      SPRING_PROFILES_ACTIVE: prod
      MYSQL_HOST: mysql
      REDIS_HOST: redis
      OLLAMA_BASE_URL: http://ollama:11434
    depends_on:
      - mysql
      - redis
      - ollama

volumes:
  mysql_data:
  ollama_data:
```

### 2. 启动服务

```bash
# 构建并启动
docker-compose up -d --build

# 在 Ollama 容器中拉取模型
docker-compose exec ollama ollama pull deepseek-r1:7b
docker-compose exec ollama ollama pull nomic-embed-text

# 查看日志
docker-compose logs -f rag-app
```

## 接口文档概览

### 知识库管理 `/api/v1/knowledge-bases`

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | / | 创建知识库 |
| GET | /{id} | 获取知识库详情 |
| GET | /list | 分页查询知识库列表 |
| PUT | / | 更新知识库 |
| DELETE | /{id} | 删除知识库 |

### 文档管理 `/api/v1/documents`

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /upload | 上传文档 |
| GET | /{id} | 获取文档详情 |
| GET | /list | 分页查询文档列表 |
| DELETE | /{id} | 删除文档 |
| POST | /{id}/reprocess | 重新处理文档 |

### 问答对话 `/api/v1/chat`

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /ask | 提问问答 |
| GET | /conversations/{id}/messages | 获取会话消息 |
| GET | /conversations | 获取会话列表 |
| DELETE | /conversations/{id} | 删除会话 |

### 反馈评价 `/api/v1/feedback`

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /submit | 提交反馈 |
| GET | /{id} | 获取反馈详情 |
| GET | /message/{messageId} | 获取消息的反馈列表 |
| GET | /average-score | 获取平均评分 |

## 简历亮点

1. **RAG架构设计**：设计并实现了完整的检索增强生成（RAG）系统，涵盖文档解析、智能分块、向量化、语义检索、Prompt工程全链路
2. **多策略分块引擎**：基于策略模式实现固定大小分块与语义分块双引擎，支持按章节、段落等语义边界智能切分，提升检索精度
3. **意图识别机制**：结合关键词匹配与LLM辅助判断，实现制度查询/故障排查/流程问答等多意图精准识别，针对性构造System Prompt
4. **私有化部署方案**：基于Ollama本地部署大模型，数据不出企业网络，满足金融/政务等行业的合规要求
5. **向量检索优化**：基于余弦相似度的向量检索，支持Top-K与相似度阈值双重过滤，Redis缓存热门问题降低LLM调用成本
6. **异步处理流水线**：文档上传后异步执行解析→分块→向量化→存储全流程，提升用户体验
7. **反馈闭环机制**：1-5星评分+文字反馈，持续优化知识库质量与问答效果
