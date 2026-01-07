# 朱雀广告平台

开发源码的一站式广告平台，包含 SSP/ADX/DSP/DMP 模块

## 📚 项目文档

项目正在进行重写（v2版本），所有设计文档已迁移至 [docs/](./docs/) 目录。

### 快速导航

- **[快速启动](./QUICKSTART.md)** 🚀 - 5分钟快速搭建开发环境
- **[文档中心](./docs/overview/README.md)** - 文档导航和索引
- **[项目状态追踪](./docs/overview/PROJECT_STATUS.md)** - 当前进度和待办事项
- **[开发策略](./docs/overview/DEVELOPMENT_STRATEGY.md)** - 渐进式开发路线图
- **[技术方案](./docs/design/REWRITE_PLAN.md)** - 完整重写技术方案
- **[架构设计](./docs/design/ARCHITECTURE.md)** - 系统架构设计
- **[配置管理](./docs/design/CONFIGURATION.md)** - 配置文件和环境变量
- **[RBAC权限设计](./docs/design/RBAC_DESIGN.md)** - 权限系统详细设计

### 项目结构

```
zhuque/
├── docs/                    # 📄 项目文档
│   ├── overview/            # 项目概览
│   ├── design/              # 设计文档
│   └── modules/             # 各模块详细设计
│
├── zhuque-*/                # 🔧 原有项目代码（v1）
│
└── v2/                      # 🚀 新项目代码（v2，待创建）
    ├── zhuque-backend/      # 后端
    └── zhuque-frontend/     # 前端
```

### 项目状态

- **当前版本**: v1（旧版本）
- **重写版本**: v2（规划中）
- **技术栈**: Spring Boot 3.2.x + Vue 3 + TypeScript
- **架构**: 单体应用 + 模块化分层

### 文档分类

#### 📋 项目概览
- [项目状态追踪](./docs/overview/PROJECT_STATUS.md) - 当前进度、待办事项、工作日志
- [开发策略](./docs/overview/DEVELOPMENT_STRATEGY.md) - 渐进式开发路线图、里程碑

#### 🏗️ 设计文档
- [技术方案](./docs/design/REWRITE_PLAN.md) - 完整重写技术方案（12章节）
- [架构设计](./docs/design/ARCHITECTURE.md) - 系统架构、模块划分
- [RBAC权限设计](./docs/design/RBAC_DESIGN.md) - 权限系统详细设计

#### 📦 模块设计
- [认证授权模块](./docs/modules/auth-module.md) - 用户认证、权限管理

## 开发指南

### 快速恢复工作

如果你是开发者，需要恢复之前的工作进度：

1. 阅读 [项目状态追踪](./docs/overview/PROJECT_STATUS.md)
2. 查看 [开发策略](./docs/overview/DEVELOPMENT_STRATEGY.md)
3. 了解当前模块的详细设计

### 新人上手

如果你是新加入的开发者：

1. 先看 [技术方案](./docs/design/REWRITE_PLAN.md)
2. 理解 [架构设计](./docs/design/ARCHITECTURE.md)
3. 阅读 [开发策略](./docs/overview/DEVELOPMENT_STRATEGY.md)

## 技术栈

### 后端
- Spring Boot 3.2.x
- JDK 17/21
- MyBatis-Plus 3.5.x
- MySQL 8.0
- Redis 7.x
- Spring Security + JWT

### 前端
- Vue 3.4.x
- TypeScript 5.x
- Vite 5.x
- Element Plus 2.5.x
- Pinia 2.x

## 联系方式

- 项目地址: [GitHub](https://github.com/your-org/zhuque)
- 问题反馈: [Issues](https://github.com/your-org/zhuque/issues)

---

**文档最后更新**: 2025-01-07
