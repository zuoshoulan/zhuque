# 前端项目启动指南

## 项目信息

- **项目名称**: 朱雀广告平台前端
- **技术栈**: Vue 3 + TypeScript + Element Plus + Vite
- **项目路径**: `/home/wake/code/zhuque/v2/zhuque-frontend`

## 前置要求

- Node.js >= 18.0.0
- npm >= 9.0.0

## 启动步骤

### 1. 进入前端项目目录

```bash
cd /home/wake/code/zhuque/v2/zhuque-frontend
```

### 2. 安装依赖（首次运行）

```bash
npm install
```

### 3. 启动开发服务器

```bash
npm run dev
```

启动成功后，终端会显示：

```
VITE v7.3.1  ready in 345 ms

➜  Local:   http://localhost:3000/
➜  Network: use --host to expose
```

### 4. 访问应用

在浏览器中打开：http://localhost:3000

## 常用命令

### 开发环境

```bash
# 启动开发服务器（热重载）
npm run dev

# 指定端口启动
npm run dev -- --port 3000

# 启动并暴露到网络
npm run dev -- --host
```

### 生产构建

```bash
# 构建生产版本
npm run build

# 预览生产构建
npm run preview
```

### 代码检查

```bash
# TypeScript 类型检查
npm run type-check

# ESLint 检查（如果已配置）
npm run lint
```

## 服务端口

- **开发服务器**: http://localhost:3000
- **后端 API**: http://localhost:8080

## API 代理配置

开发环境已配置 API 代理，`/api` 路径会自动转发到后端：

```typescript
// vite.config.ts
server: {
  port: 3000,
  proxy: {
    '/api': {
      target: 'http://localhost:8080',
      changeOrigin: true
    }
  }
}
```

## 停止服务

在终端中按 `Ctrl + C` 停止开发服务器

## 故障排查

### 端口被占用

如果 3000 端口被占用，可以：

```bash
# 方案1: 使用其他端口
npm run dev -- --port 3001

# 方案2: 查找并关闭占用进程
lsof -ti:3000 | xargs kill -9
```

### 依赖安装失败

```bash
# 清除缓存重新安装
rm -rf node_modules package-lock.json
npm install
```

### 热更新不生效

重启开发服务器：

```bash
# 按 Ctrl + C 停止
# 然后重新启动
npm run dev
```

## VS Code 插件推荐

开发此项目需要安装以下 VS Code 插件：

### 必装插件

1. **Vue - Official** (原名 Volar)
   - Vue 3 官方插件
   - 提供 TypeScript 支持
   - ⚠️ 如果已安装 Vetur，需要禁用

2. **TypeScript Vue Plugin (Volar)**
   - Vue 3 + TypeScript 类型检查

### 推荐插件

3. **ESLint** - 代码检查
4. **Prettier** - 代码格式化
5. **Auto Close Tag** - 自动闭合标签
6. **Auto Rename Tag** - 自动重命名标签
7. **Path Intellisense** - 路径智能提示
8. **GitLens** - Git 增强

## 开发注意事项

1. **路径别名**: 使用 `@/` 代替 `src/`
   ```typescript
   import { useUserStore } from '@/stores/user'
   ```

2. **组件注册**: Element Plus 组件已全局注册，可直接使用
   ```vue
   <el-button>按钮</el-button>
   ```

3. **API 请求**: 使用封装的 request 工具
   ```typescript
   import request from '@/utils/request'
   ```

4. **状态管理**: 使用 Pinia stores
   ```typescript
   import { useUserStore } from '@/stores/user'
   ```

## 测试账号

- **手机号**: 13800138001
- **密码**: 123456

## 浏览器支持

- Chrome >= 87
- Firefox >= 78
- Safari >= 14
- Edge >= 88
