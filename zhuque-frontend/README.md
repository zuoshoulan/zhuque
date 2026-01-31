# 朱雀广告平台 - 前端项目

基于 Vue 3 + TypeScript + Element Plus 的单页应用

## 技术栈

- **Vue 3.5.13** - 渐进式 JavaScript 框架
- **TypeScript** - 类型安全的 JavaScript 超集
- **Vite 7.3** - 下一代前端构建工具
- **Element Plus** - Vue 3 UI 组件库
- **Vue Router 4** - 官方路由管理器
- **Pinia** - Vue 3 状态管理库
- **Axios** - HTTP 请求库

## 快速开始

### 安装依赖
```bash
npm install
```

### 启动开发服务器
```bash
npm run dev
```
访问: http://localhost:3000

### 构建生产版本
```bash
npm run build
```

## 项目结构

```
zhuque-frontend/
├── src/
│   ├── api/              # API 接口定义
│   │   └── auth.ts       # 认证相关接口
│   ├── assets/           # 静态资源
│   ├── router/           # 路由配置
│   │   └── index.ts      # 路由定义和守卫
│   ├── stores/           # Pinia 状态管理
│   │   └── user.ts       # 用户状态管理
│   ├── utils/            # 工具函数
│   │   └── request.ts    # Axios 封装
│   ├── views/            # 页面组件
│   │   ├── LoginView.vue # 登录页面
│   │   └── HomeView.vue  # 首页
│   ├── App.vue           # 根组件
│   └── main.ts           # 应用入口
├── index.html            # HTML 模板
├── vite.config.ts        # Vite 配置
└── package.json          # 项目依赖
```

## 功能特性

### 已实现功能

✅ **用户登录**
- 手机号/密码登录
- 表单验证
- 错误提示
- 自动跳转

✅ **JWT Token 认证**
- Token 自动存储到 localStorage
- 请求拦截器自动添加 Token
- 响应拦截器处理 403 错误
- 自动退出登录

✅ **首页展示**
- 用户信息展示
- Token 信息展示
- 退出登录功能

✅ **路由守卫**
- 未登录自动跳转到登录页
- 已登录自动跳转到首页

## 测试账号

- **手机号**: 13800138001
- **密码**: 123456

## API 配置

### 开发环境
- **前端地址**: http://localhost:3000
- **后端地址**: http://localhost:8080
- **API 代理**: Vite 配置了 `/api` 路径代理到后端

## 浏览器支持

- Chrome >= 87
- Firefox >= 78
- Safari >= 14
- Edge >= 88
