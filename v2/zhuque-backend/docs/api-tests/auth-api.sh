#!/bin/bash

# ========================================
# 朱雀广告平台 - 认证相关接口测试
# ========================================

BASE_URL="http://localhost:8080"

# 颜色输出
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

echo_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

echo_title() {
    echo -e "\n${YELLOW}========================================${NC}"
    echo -e "${YELLOW}$1${NC}"
    echo -e "${YELLOW}========================================${NC}\n"
}

# 1. 用户登录
test_login() {
    echo_title "测试用户登录"
    
    curl -X POST "${BASE_URL}/api/auth/login" \
        -H "Content-Type: application/json" \
        -d '{
            "account": "13800138001",
            "password": "admin123"
        }' \
        | jq .
    
    echo_info "登录成功后,会返回 accessToken 和 userInfo"
}

# 2. 查询用户信息
test_get_user() {
    echo_title "测试查询用户信息"
    
    curl -X GET "${BASE_URL}/api/user/3" \
        | jq .
    
    echo_info "查询用户ID=3的信息(密码字段已清除)"
}

# 3. 健康检查
test_health() {
    echo_title "测试健康检查"
    
    curl -X GET "${BASE_URL}/actuator/health" \
        | jq .
    
    echo_info "应用健康状态检查"
}

# 主菜单
main() {
    echo -e "\n${YELLOW}朱雀广告平台 - API 测试脚本${NC}\n"
    echo "1. 用户登录"
    echo "2. 查询用户信息"
    echo "3. 健康检查"
    echo "4. 运行所有测试"
    echo "0. 退出"
    
    read -p "请选择操作 [0-4]: " choice
    
    case $choice in
        1) test_login ;;
        2) test_get_user ;;
        3) test_health ;;
        4) 
            test_login
            test_get_user
            test_health
            ;;
        0) echo_info "退出"; exit 0 ;;
        *) echo_error "无效选择" && exit 1 ;;
    esac
}

# 执行主函数
main
