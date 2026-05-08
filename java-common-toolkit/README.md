# Java Common Toolkit

企业级 Java 通用组件封装库，基于 Spring Boot 2.7.x，提供开箱即用的通用功能组件。

## 组件列表

| 组件 | 说明 | 使用方式 |
|------|------|----------|
| 统一返回封装 | Result、ResultCode、PageResult | 直接使用 |
| 全局异常处理 | GlobalExceptionHandler、BusinessException | 自动配置 |
| JWT工具 | JwtUtil、Token生成/验证/刷新 | 注入JwtUtil |
| Redis工具 | RedisUtil、分布式锁 | 注入RedisUtil |
| Excel导出 | ExcelExportUtil、注解式导出 | 注入ExcelExportUtil |
| 加密工具 | CryptoUtil、AES/RSA/MD5/SHA256 | 静态方法调用 |
| 限流 | @RateLimiter、令牌桶算法 | 注解式 |
| 日志追踪 | TraceIdFilter、MDC链路追踪 | 自动配置 |
| 幂等性 | @Idempotent、基于Redis | 注解式 |
| 分布式锁 | @DistributedLock、基于Redis | 注解式 |

## 快速开始

### 引入依赖

```xml
<dependency>
    <groupId>com.qinyoucheng</groupId>
    <artifactId>java-common-toolkit</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 配置文件

```yaml
toolkit:
  jwt:
    secret: yourSecretKeyMustBe256BitsLongForHmacSha256
    issuer: your-app
    access-token-expiration: 7200000
    refresh-token-expiration: 604800000
    algorithm: HMAC
  redis:
    key-prefix: "myapp:"
    default-expiration: 3600
```

## 使用示例

### 1. 统一返回封装

```java
@GetMapping("/user/{id}")
public Result<User> getUser(@PathVariable Long id) {
    User user = userService.getById(id);
    return Result.success(user);
}

@GetMapping("/users")
public Result<PageResult<User>> listUsers(PageRequest pageRequest) {
    PageResult<User> page = userService.page(pageRequest);
    return Result.success(page);
}
```

### 2. 全局异常处理

```java
@GetMapping("/order/{id}")
public Result<Order> getOrder(@PathVariable Long id) {
    Order order = orderService.getById(id);
    if (order == null) {
        throw new BusinessException("订单不存在");
    }
    return Result.success(order);
}
```

### 3. JWT工具

```java
@Autowired
private JwtUtil jwtUtil;

public String login(String username, String password) {
    User user = userService.authenticate(username, password);
    Map<String, Object> claims = new HashMap<>();
    claims.put("role", user.getRole());
    String accessToken = jwtUtil.generateAccessToken(user.getId().toString(), claims);
    String refreshToken = jwtUtil.generateRefreshToken(user.getId().toString());
    return accessToken;
}

public Claims verifyToken(String token) {
    return jwtUtil.parseToken(token);
}
```

### 4. Redis工具

```java
@Autowired
private RedisUtil redisUtil;

public void cacheUser(User user) {
    redisUtil.set("user:" + user.getId(), user, 3600);
}

public User getCachedUser(Long userId) {
    return redisUtil.get("user:" + userId);
}
```

### 5. 分布式锁

```java
@DistributedLock(key = "#orderId", leaseTime = 5000, waitTime = 1000)
public Order processOrder(String orderId) {
    // 业务逻辑，同一时刻只有一个线程能执行
    return orderService.process(orderId);
}
```

### 6. Excel导出

```java
@Data
public class UserExportDTO {
    @ExcelColumn(name = "用户ID", order = 1, width = 15)
    private Long id;

    @ExcelColumn(name = "用户名", order = 2, width = 20)
    private String username;

    @ExcelColumn(name = "创建时间", order = 3, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
}

@Autowired
private ExcelExportUtil excelExportUtil;

@GetMapping("/export")
public void export(HttpServletResponse response) {
    List<UserExportDTO> list = userService.listForExport();
    excelExportUtil.export(response, "用户列表", "用户", list, UserExportDTO.class);
}
```

### 7. 加密工具

```java
// AES对称加密
String key = CryptoUtil.aesGenerateKey();
String encrypted = CryptoUtil.aesEncrypt("敏感数据", key);
String decrypted = CryptoUtil.aesDecrypt(encrypted, key);

// RSA非对称加密
RsaUtil.KeyPairInfo keyPair = CryptoUtil.rsaGenerateKeyPair();
String encrypted = CryptoUtil.rsaEncryptByPublicKey("数据", keyPair.getPublicKey());
String decrypted = CryptoUtil.rsaDecryptByPrivateKey(encrypted, keyPair.getPrivateKey());

// 哈希
String md5Hash = CryptoUtil.md5("数据");
String sha256Hash = CryptoUtil.sha256("数据");
String hmacHash = CryptoUtil.hmacSha256("数据", "密钥");
```

### 8. 限流

```java
@RateLimiter(permits = 10, period = 1, limitType = RateLimiter.LimitType.IP)
@GetMapping("/api/data")
public Result<List<Data>> getData() {
    return Result.success(dataService.list());
}

@RateLimiter(permits = 100, period = 1, limitType = RateLimiter.LimitType.USER,
             key = "#userId", message = "操作过于频繁")
@GetMapping("/api/user/{userId}/action")
public Result<Void> action(@PathVariable String userId) {
    return Result.success();
}
```

### 9. 日志追踪

自动配置，无需额外代码。日志输出格式中包含 `[traceId]` 占位符：

```xml
<!-- logback-spring.xml -->
<pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] [%X{traceId}] %-5level %logger{50} - %msg%n</pattern>
```

### 10. 幂等性

```java
@Idempotent(key = "#orderId", expireTime = 5, message = "请勿重复提交")
@PostMapping("/order/create")
public Result<Void> createOrder(@RequestBody OrderRequest request) {
    orderService.create(request);
    return Result.success();
}
```

## 设计理念

### 约定优于配置

所有组件通过 Spring Boot 自动配置机制注册，引入依赖即可使用，零配置启动。通过 `spring.factories` 注册自动配置类，每个组件都有对应的 `AutoConfiguration` 和 `Properties` 类。

### 注解驱动

核心功能通过注解驱动使用，降低接入成本：
- `@DistributedLock` - 声明式分布式锁
- `@RateLimiter` - 声明式限流
- `@Idempotent` - 声明式幂等性保证
- `@ExcelColumn` - 声明式Excel列映射

### 关注点分离

每个模块职责单一，互不耦合。Redis 相关组件设为 `optional`，不引入 Redis 时不影响其他组件使用。

### 防御性编程

- 严格参数校验，快速失败
- 分布式锁自动释放，防止死锁
- 限流脚本执行失败时放行请求，保证可用性
- 幂等性组件方法异常时自动清除Key

### 企业级特性

- JWT 支持 HMAC 和 RSA 双算法
- Redis 分布式锁支持 SpEL 表达式动态Key
- 限流支持 IP/用户/接口/自定义维度
- Excel 导出基于 SXSSFWorkbook 支持大数据量
- AES 使用 GCM 模式提供认证加密
- 日志追踪通过 MDC 实现全链路 TraceId 传递
