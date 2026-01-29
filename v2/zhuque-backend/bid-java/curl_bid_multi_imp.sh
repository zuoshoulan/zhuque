#!/bin/bash

# 多广告位竞价测试脚本
# 用于测试单个请求包含多个广告位的情况

curl 'http://localhost:8081/openrtb/bid' \
  -H 'Content-Type: application/json' \
  --data-raw '{
  "id": "req-20250129-multi-001",
  "imp": [
    {
      "id": "imp-001",
      "banner": {
        "w": 728,
        "h": 90,
        "pos": 1
      },
      "bidfloor": 0.5
    },
    {
      "id": "imp-002",
      "banner": {
        "w": 300,
        "h": 250,
        "pos": 1
      },
      "bidfloor": 0.3
    },
    {
      "id": "imp-003",
      "banner": {
        "w": 320,
        "h": 50,
        "pos": 1
      },
      "bidfloor": 0.2
    }
  ],
  "device": {
    "ua": "Mozilla/5.0 (Linux; Android 12) AppleWebKit/537.36",
    "ip": "210.73.204.1",
    "geo": {
      "country": "CN",
      "region": "Beijing",
      "city": "Beijing"
    },
    "devicetype": 1,
    "os": "Android"
  },
  "user": {
    "id": "user-001"
  },
  "site": {
    "id": "site-001",
    "domain": "example.com",
    "page": "http://example.com/page.html"
  },
  "test": 0,
  "at": 1,
  "tmax": 100
}'
