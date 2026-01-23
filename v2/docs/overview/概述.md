# 朱雀广告平台重写 - 文档中心

> **项目状态**: 🟡 规划阶段完成，准备开始实施
> **最后更新**: 2025-01-07

---

## 📚 文档导航

### 快速开始
如果你是新加入的开发者，或者需要快速恢复工作，请按以下顺序阅读：

1. **[项目状态追踪](./PROJECT_STATUS.md)** - 了解当前进度和待办事项
2. **[开发策略](./DEVELOPMENT_STRATEGY.md)** - 理解渐进式开发方法
3. **[技术方案](../design/REWRITE_PLAN.md)** - 掌握整体技术架构
4. **具体模块设计** - 查看各模块详细设计文档

---

## 📂 文档分类

### 1️⃣ 项目概览 (`overview/`)

| 文档 | 说明 | 适用对象 |
|-----|------|---------|
| **[项目状态追踪](./PROJECT_STATUS.md)** | 当前进度、待办事项、工作日志 | 所有人 |
| **[开发策略](./DEVELOPMENT_STRATEGY.md)** | 渐进式开发路线图、里程碑 | 开发者 |
| **README.md** | 本文档，文档导航入口 | 所有人 |

### 2️⃣ 设计文档 (`design/`)

| 文档 | 说明 | 适用对象 |
|-----|------|---------|
| **[技术方案](../design/REWRITE_PLAN.md)** | 完整重写技术方案（12章节） | 架构师、技术负责人 |
| **[RBAC权限设计](../design/RBAC_DESIGN.md)** | 权限系统详细设计 | 后端开发者 |
| **[渠道权限控制](../design/CHANNEL_PERMISSION.md)** | 渠道权限控制（简化版） | 后端开发者 |
| **[数据库选型](../design/DATABASE_SELECTION.md)** | MySQL/PostgreSQL/MongoDB对比 | 架构师、技术负责人 |
| **[前端界面原型](../design/FRONTEND_PROTOTYPE.md)** | 页面布局、交互流程 | 前端开发者 |
| **[架构设计](../design/ARCHITECTURE.md)** | 系统架构、模块划分、依赖关系 | 架构师、后端开发者 |
| **[配置管理](../design/CONFIGURATION.md)** | 配置文件、环境变量、Docker配置 | 运维、开发者 |
| **[API设计规范](../design/API_DESIGN.md)** | RESTful API设计规范 | 前后端开发者 |
| **[数据库设计](../design/DATABASE_DESIGN.md)** | 数据表设计、索引优化 | 后端开发者、DBA |

### 3️⃣ 模块设计 (`modules/`)

| 模块 | 文档 | 状态 |
|-----|------|------|
| **认证授权** | [auth-module.md](../modules/auth-module.md) | ✅ 已完成 |
| **广告主管理** | [advertiser-module.md](../modules/advertiser-module.md) | 📝 待编写 |
| **推广活动** | [campaign-module.md](../modules/campaign-module.md) | 📝 待编写 |
| **广告组管理** | [adgroup-module.md](../modules/adgroup-module.md) | 📝 待编写 |
| **创意管理** | [creative-module.md](../modules/creative-module.md) | 📝 待编写 |
| **RTB竞价** | [rtb-module.md](../modules/rtb-module.md) | 📝 待编写 |
| **监测服务** | [pixel-module.md](../modules/pixel-module.md) | 📝 待编写 |

---

## 🎯 模块设计文档模板

每个模块的设计文档应包含以下内容：

```markdown
# [模块名称] 设计文档

## 一、模块概述
- 功能描述
- 业务目标
- 依赖关系

## 二、数据模型
- 数据库表设计
- 实体关系图
- 索引策略

## 三、接口设计
- RESTful API列表
- 请求/响应示例
- 权限要求

## 四、业务逻辑
- 核心流程
- 业务规则
- 异常处理

## 五、技术实现
- 关键代码示例
- 性能优化
- 缓存策略

## 六、测试要点
- 单元测试覆盖
- 集成测试场景
- 边界条件

## 七、开发进度
- 待办事项
- 完成状态
- 遇到的问题
```

---

## 📝 文档维护规范

### 创建新文档
1. 确定文档类型和位置（overview/design/modules）
2. 使用统一的文档模板
3. 在本文档中添加索引链接

### 更新现有文档
1. 修改文档时，更新文档末尾的"修改日期"
2. 重大变更需在文档顶部添加变更记录
3. 同步更新 [PROJECT_STATUS.md](./PROJECT_STATUS.md)

### 文档命名规范
- 使用英文文件名，用连字符分隔：`auth-module.md`
- 标题使用中文，清晰描述内容
- 避免使用特殊字符和空格

---

## 🔄 文档与代码同步

### 开发前
- 阅读相关模块设计文档
- 理解业务逻辑和技术方案
- 确认接口设计和数据模型

### 开发中
- 按照设计文档实施
- 如有变更，先更新设计文档
- 记录遇到的问题和解决方案

### 开发后
- 更新 [PROJECT_STATUS.md](./PROJECT_STATUS.md)
- 标记模块设计文档的完成状态
- 补充实际的实现细节到设计文档

---

## 💡 快速查找

### 按角色查找

**架构师/技术负责人**
- [技术方案](../design/REWRITE_PLAN.md)
- [架构设计](../design/ARCHITECTURE.md)

**后端开发者**
- [技术方案](../design/REWRITE_PLAN.md)
- [RBAC权限设计](../design/RBAC_DESIGN.md)
- [配置管理](../design/CONFIGURATION.md)
- [API设计规范](../design/API_DESIGN.md)
- [数据库设计](../design/DATABASE_DESIGN.md)
- 各模块设计文档（modules/）

**运维工程师**
- [配置管理](../design/CONFIGURATION.md)
- [技术方案 - 部署架构](../design/REWRITE_PLAN.md#九部署架构)
- Docker配置文件
- 环境变量配置

**前端开发者**
- [API设计规范](../design/API_DESIGN.md)
- [开发策略](../design/DEVELOPMENT_STRATEGY.md)
- 各模块设计文档（关注接口部分）

**测试工程师**
- [开发策略](../design/DEVELOPMENT_STRATEGY.md)
- 各模块设计文档（测试要点部分）

### 按场景查找

**新手上路**
1. [项目状态追踪](./PROJECT_STATUS.md) - 了解进度
2. [技术方案](../design/REWRITE_PLAN.md) - 理解架构
3. [开发策略](../design/DEVELOPMENT_STRATEGY.md) - 掌握方法

**继续开发**
1. [项目状态追踪](./PROJECT_STATUS.md) - 查看待办
2. [模块设计文档](../modules/) - 查看具体模块
3. [API设计规范](../design/API_DESIGN.md) - 接口对接

**代码审查**
1. [技术方案](../design/REWRITE_PLAN.md) - 架构规范
2. [API设计规范](../design/API_DESIGN.md) - 接口规范
3. 各模块设计文档 - 业务逻辑

---

## 📞 文档反馈

如果发现文档有问题，或者需要补充内容：

1. 直接修改文档（如果有权限）
2. 在开发群里提出
3. 记录到 [PROJECT_STATUS.md](./PROJECT_STATUS.md) 的"待决策"部分

---

**文档版本**: v1.0
**创建日期**: 2025-01-07
**维护者**: 开发团队
