# 接口文档

## 1. 通用规范

### 1.1 请求规范

- 协议：HTTP/HTTPS
- 方法：RESTful（GET/POST/PUT/DELETE）
- 前缀：`/api/v1`
- 编码：UTF-8
- 格式：JSON

### 1.2 统一返回格式

```json
{
    "code": 200,
    "message": "操作成功",
    "data": {}
}
```

### 1.3 返回码定义

| Code | 说明 |
|------|------|
| 200 | 成功 |
| 400 | 参数错误 |
| 401 | 未认证 |
| 403 | 无权限 |
| 404 | 资源不存在 |
| 429 | 请求过于频繁 |
| 500 | 服务器内部错误 |

### 1.4 分页请求

```json
{
    "pageNum": 1,
    "pageSize": 10,
    "orderBy": "create_time",
    "orderDir": "desc"
}
```

### 1.5 分页返回

```json
{
    "code": 200,
    "message": "操作成功",
    "data": {
        "records": [],
        "total": 100,
        "pageNum": 1,
        "pageSize": 10,
        "pages": 10
    }
}
```

---

## 2. 供应链供应商预约管理系统

### 2.1 供应商管理

#### 创建供应商
- **POST** `/api/v1/supplier`
- **Body**:
```json
{
    "name": "XX物流有限公司",
    "code": "SUP-2024-001",
    "contactPerson": "张三",
    "contactPhone": "13800138000",
    "businessScope": "冷链运输",
    "address": "上海市XX区XX路XX号"
}
```

#### 查询供应商列表
- **GET** `/api/v1/supplier/page?pageNum=1&pageSize=10&name=&status=`

#### 查询供应商详情
- **GET** `/api/v1/supplier/{id}`

#### 更新供应商
- **PUT** `/api/v1/supplier/{id}`

#### 删除供应商
- **DELETE** `/api/v1/supplier/{id}`

#### 启用/禁用供应商
- **PUT** `/api/v1/supplier/{id}/status?status=1`

### 2.2 资质管理

#### 上传资质
- **POST** `/api/v1/qualification`
- **Body**:
```json
{
    "supplierId": 1,
    "type": "BUSINESS_LICENSE",
    "name": "营业执照",
    "certNo": "91310000XXXXXXXXXX",
    "expireDate": "2025-12-31",
    "filePath": "/upload/qual/2024/xxx.pdf"
}
```

#### 查询供应商资质列表
- **GET** `/api/v1/qualification/supplier/{supplierId}`

#### 资质预警列表
- **GET** `/api/v1/qualification/warning?pageNum=1&pageSize=10`

### 2.3 预约管理

#### 创建预约
- **POST** `/api/v1/booking`
- **Body**:
```json
{
    "supplierId": 1,
    "timeSlotId": 1,
    "bookingDate": "2024-12-15",
    "vehicleNo": "沪A12345",
    "driverName": "李四",
    "driverPhone": "13900139000",
    "goodsType": "冷冻食品",
    "estimatedWeight": 5000.00,
    "remark": "冷链运输"
}
```

#### 查询预约列表
- **GET** `/api/v1/booking/page?pageNum=1&pageSize=10&supplierId=&status=&bookingDate=`

#### 确认预约
- **PUT** `/api/v1/booking/{id}/confirm`

#### 取消预约
- **PUT** `/api/v1/booking/{id}/cancel`

#### 查询时段可用数量
- **GET** `/api/v1/booking/available?date=2024-12-15`

### 2.4 入园登记

#### 创建入园登记
- **POST** `/api/v1/park-entry`
- **Body**:
```json
{
    "bookingId": 1,
    "actualArrivalTime": "2024-12-15 09:30:00",
    "vehicleNo": "沪A12345",
    "gateNo": "G01"
}
```

#### 查询入园记录
- **GET** `/api/v1/park-entry/page?pageNum=1&pageSize=10`

### 2.5 冷链车辆管理

#### 登记冷链车辆
- **POST** `/api/v1/vehicle`
- **Body**:
```json
{
    "supplierId": 1,
    "vehicleNo": "沪A12345",
    "vehicleType": "冷藏车",
    "temperatureRange": "-18℃~-22℃",
    "capacity": 20.00,
    "inspectionDate": "2024-11-01",
    "inspectionExpireDate": "2025-11-01"
}
```

#### 查询车辆列表
- **GET** `/api/v1/vehicle/page?pageNum=1&pageSize=10&supplierId=`

### 2.6 收台管理

#### 创建收台记录
- **POST** `/api/v1/receiving`
- **Body**:
```json
{
    "bookingId": 1,
    "actualWeight": 4800.00,
    "receiver": "王五",
    "remark": "验收合格"
}
```

#### 查询收台记录
- **GET** `/api/v1/receiving/page?pageNum=1&pageSize=10`

### 2.7 数据统计

#### 预约统计
- **GET** `/api/v1/statistics/booking?startDate=2024-12-01&endDate=2024-12-15`

#### 供应商统计
- **GET** `/api/v1/statistics/supplier`

#### 入园统计
- **GET** `/api/v1/statistics/park-entry?startDate=2024-12-01&endDate=2024-12-15`

### 2.8 报表导出

#### 导出预约报表
- **GET** `/api/v1/report/booking/export?startDate=2024-12-01&endDate=2024-12-15`
- **Response**: Excel文件流

#### 导出供应商报表
- **GET** `/api/v1/report/supplier/export`

---

## 3. 企业RAG知识库问答系统

### 3.1 知识库管理

#### 创建知识库
- **POST** `/api/v1/knowledge-base`
- **Body**:
```json
{
    "name": "企业制度知识库",
    "description": "包含公司各项规章制度",
    "type": "POLICY"
}
```

#### 查询知识库列表
- **GET** `/api/v1/knowledge-base/page?pageNum=1&pageSize=10`

### 3.2 文档管理

#### 上传文档
- **POST** `/api/v1/document/upload`
- **Content-Type**: `multipart/form-data`
- **Params**: `file`（PDF/Word/TXT）、`knowledgeBaseId`、`chunkStrategy`（FIXED_SIZE/SEMANTIC）

#### 查询文档列表
- **GET** `/api/v1/document/page?pageNum=1&pageSize=10&knowledgeBaseId=`

#### 删除文档
- **DELETE** `/api/v1/document/{id}`

#### 重新处理文档
- **POST** `/api/v1/document/{id}/reprocess`

### 3.3 问答接口

#### 发起问答
- **POST** `/api/v1/chat/ask`
- **Body**:
```json
{
    "question": "年假怎么申请？",
    "conversationId": null,
    "knowledgeBaseId": 1
}
```
- **Response**:
```json
{
    "code": 200,
    "message": "操作成功",
    "data": {
        "conversationId": "conv-uuid-001",
        "messageId": "msg-uuid-001",
        "answer": "根据公司制度，年假申请流程如下...",
        "sources": [
            {
                "documentName": "员工手册v3.0.pdf",
                "chunkContent": "年假申请需提前3个工作日...",
                "score": 0.92
            }
        ],
        "intent": "POLICY_QUERY"
    }
}
```

#### 查询对话历史
- **GET** `/api/v1/chat/history/{conversationId}?pageNum=1&pageSize=20`

#### 删除对话
- **DELETE** `/api/v1/chat/conversation/{conversationId}`

### 3.4 反馈接口

#### 提交反馈
- **POST** `/api/v1/feedback`
- **Body**:
```json
{
    "messageId": "msg-uuid-001",
    "score": 5,
    "comment": "回答很准确"
}
```

#### 查询反馈统计
- **GET** `/api/v1/feedback/statistics?knowledgeBaseId=1`

---

## 4. 微服务权限中台

### 4.1 认证接口

#### 登录
- **POST** `/api/v1/auth/login`
- **Body**:
```json
{
    "username": "admin",
    "password": "encrypted_password",
    "appKey": "system-a"
}
```
- **Response**:
```json
{
    "code": 200,
    "message": "操作成功",
    "data": {
        "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
        "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
        "tokenType": "Bearer",
        "expiresIn": 7200,
        "userInfo": {
            "userId": 1,
            "username": "admin",
            "realName": "管理员",
            "roles": ["ADMIN"]
        }
    }
}
```

#### 登出
- **POST** `/api/v1/auth/logout`
- **Header**: `Authorization: Bearer {accessToken}`

#### 刷新Token
- **POST** `/api/v1/auth/refresh`
- **Body**:
```json
{
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
}
```

#### SSO登录
- **POST** `/api/v1/auth/sso/login`
- **Body**:
```json
{
    "ticket": "sso-ticket-uuid",
    "appKey": "system-b"
}
```

### 4.2 用户管理

#### 创建用户
- **POST** `/api/v1/user`
- **Body**:
```json
{
    "username": "zhangsan",
    "password": "encrypted_password",
    "realName": "张三",
    "email": "zhangsan@example.com",
    "phone": "13800138000",
    "deptId": 1,
    "roleIds": [2, 3]
}
```

#### 查询用户列表
- **GET** `/api/v1/user/page?pageNum=1&pageSize=10&username=&status=`

#### 更新用户
- **PUT** `/api/v1/user/{id}`

#### 删除用户
- **DELETE** `/api/v1/user/{id}`

#### 分配角色
- **POST** `/api/v1/user/{id}/roles`
- **Body**: `{"roleIds": [1, 2, 3]}`

### 4.3 角色管理

#### 创建角色
- **POST** `/api/v1/role`
- **Body**:
```json
{
    "name": "仓库管理员",
    "code": "WAREHOUSE_ADMIN",
    "description": "仓库管理相关权限",
    "menuIds": [1, 2, 3, 5, 8]
}
```

#### 查询角色列表
- **GET** `/api/v1/role/page?pageNum=1&pageSize=10`

#### 分配菜单权限
- **POST** `/api/v1/role/{id}/menus`
- **Body**: `{"menuIds": [1, 2, 3]}`

### 4.4 菜单管理

#### 创建菜单
- **POST** `/api/v1/menu`
- **Body**:
```json
{
    "parentId": 0,
    "name": "供应商管理",
    "path": "/supplier",
    "component": "supplier/index",
    "permission": "supplier:list",
    "type": 1,
    "sort": 1
}
```

#### 查询菜单树
- **GET** `/api/v1/menu/tree`

### 4.5 数据权限

#### 设置数据权限
- **POST** `/api/v1/data-permission`
- **Body**:
```json
{
    "roleId": 2,
    "type": "DEPT_AND_SUB",
    "deptIds": [1, 2, 3]
}
```

### 4.6 黑名单管理

#### 添加IP黑名单
- **POST** `/api/v1/blacklist`
- **Body**:
```json
{
    "type": "IP",
    "value": "192.168.1.100",
    "reason": "恶意请求"
}
```

#### 查询黑名单列表
- **GET** `/api/v1/blacklist/page?pageNum=1&pageSize=10`

### 4.7 操作日志

#### 查询操作日志
- **GET** `/api/v1/operation-log/page?pageNum=1&pageSize=10&username=&operationType=`
