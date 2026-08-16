# ============================================================
# 第一阶段：安装依赖并构建 Next.js 生产版本
# ============================================================
FROM node:24-alpine AS builder

# 后续命令在 /app 目录执行
WORKDIR /app

# 先复制依赖清单，尽量复用 Docker 构建缓存
COPY package.json package-lock.json ./

# npm ci 根据 package-lock.json 安装精确版本
# 适合 Docker 和 CI 环境，结果比 npm install 更稳定
RUN npm ci

# 复制 Next.js 源代码和配置
COPY . .

# NEXT_PUBLIC_ 前缀的变量会在构建期被内联到页面里
# 这里用 ARG/ENV 接收 compose 传入的构建参数，未传入时用默认值
ARG NEXT_PUBLIC_API_URL=http://localhost:8080
ENV NEXT_PUBLIC_API_URL=$NEXT_PUBLIC_API_URL

# 构建生产版本
# next.config.mjs 中的 output: 'standalone'
# 会生成 .next/standalone 目录
RUN npm run build


# ============================================================
# 第二阶段：只保留 Next.js 生产运行所需文件
# ============================================================
FROM node:24-alpine AS runner

# 声明生产环境
ENV NODE_ENV=production

# 容器内的工作目录
WORKDIR /app

# 创建非 root 用户，减少容器以 root 运行的安全风险
RUN addgroup -S nextjs && adduser -S nextjs -G nextjs

# 复制 standalone 运行文件
COPY --from=builder --chown=nextjs:nextjs /app/.next/standalone ./

# 复制静态资源
# standalone 目录不会自动包含 .next/static
COPY --from=builder --chown=nextjs:nextjs /app/.next/static ./.next/static

# 如果项目存在 public 目录，复制公开静态资源
COPY --from=builder --chown=nextjs:nextjs /app/public ./public

# 让后续命令使用非 root 用户
USER nextjs

# Next.js 容器监听端口
EXPOSE 3000

# 让 Next.js 监听所有网络接口
# 如果只监听 127.0.0.1，容器外部无法访问
ENV HOSTNAME=0.0.0.0
ENV PORT=3000

# standalone 输出中会生成 server.js
CMD ["node", "server.js"]
