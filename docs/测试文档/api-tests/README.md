# API 接口测试文档

## 目录说明

本目录包含朱雀广告平台的API接口测试脚本和curl命令示例。

## 文件列表

### 1. auth-api.sh
认证相关接口测试脚本,包括:
- 用户登录
- 查询用户信息
- 健康检查

### 2. curl-commands.md
常用curl命令速查表

## 使用方法

### 方式1: 使用交互式脚本
```bash
cd /home/wake/code/zhuque/v2/zhuque-backend/docs/api-tests
./auth-api.sh
```

### 方式2: 直接执行curl命令
参见 [curl-commands.md](./curl-commands.md)

## 测试账号

- **手机号**: 13800138001
- **密码**: admin123
- **用户ID**: 3
- **用户名**: admin
- **昵称**: 管理员

## 注意事项

1. 确保应用已启动在 http://localhost:8080
2. 需要安装 jq 工具用于格式化JSON输出: `sudo apt install jq`
3. 如需修改BASE_URL,编辑脚本中的变量即可
