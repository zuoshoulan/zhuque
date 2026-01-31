#!/bin/bash
# 竞价接口测试脚本

BASE_URL="http://localhost:8081"
ENDPOINT="/openrtb/bid"
JSON_FILE="$(dirname "$0")/bid_request_full.json"

echo "=========================================="
echo "  朱雀竞价接口测试"
echo "=========================================="
echo "URL: ${BASE_URL}${ENDPOINT}"
echo "请求文件: ${JSON_FILE}"
echo ""

if [ ! -f "$JSON_FILE" ]; then
    echo "错误: 找不到文件 ${JSON_FILE}"
    exit 1
fi

echo "发送请求..."
curl -s -X POST "${BASE_URL}${ENDPOINT}" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d @"$JSON_FILE" | jq .

echo ""
echo "=========================================="
