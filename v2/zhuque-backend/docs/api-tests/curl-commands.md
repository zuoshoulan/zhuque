# Curl 命令速查表

## 认证接口

### 1. 用户登录
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "account": "13800138001",
    "password": "admin123"
  }' | jq .
```

### 2. 用户登出
```bash
curl -X POST http://localhost:8080/api/auth/logout \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <TOKEN>" \
  | jq .
```

## 用户接口

### 1. 根据ID查询用户
```bash
curl -X GET http://localhost:8080/api/user/3 | jq .
```

### 2. 根据手机号查询用户(待实现)
```bash
curl -X GET "http://localhost:8080/api/user/phone/13800138001" | jq .
```

## 健康检查

### 1. 应用健康状态
```bash
curl -X GET http://localhost:8080/actuator/health | jq .
```

### 2. 应用信息
```bash
curl -X GET http://localhost:8080/actuator/info | jq .
```

## 测试数据

### 数据库连接信息
- **Host**: 124.221.166.184
- **Port**: 3306
- **Database**: zhuque_v2
- **Username**: wake
- **Password**: wake12345

### 测试账号
- **手机号**: 13800138001
- **密码**: admin123
- **用户ID**: 3
- **用户名**: admin

## Tips

1. **格式化JSON输出**: 使用 `jq .` 格式化
2. **查看HTTP状态码**: 添加 `-w "\nHTTP Status: %{http_code}\n"`
3. **查看请求头**: 添加 `-v` 参数
4. **只看响应头**: 使用 `-I` 参数
5. **保存响应到文件**: 使用 `-o response.json`

## 示例: 完整的登录并保存Token
```bash
# 登录并保存响应
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"account": "13800138001", "password": "admin123"}' \
  -o login_response.json

# 提取Token
TOKEN=$(cat login_response.json | jq -r '.data.accessToken')

# 使用Token访问其他接口
curl -X GET http://localhost:8080/api/user/3 \
  -H "Authorization: Bearer $TOKEN" \
  | jq .
```
