# ============================================================
# 第一阶段：使用 Maven 编译 Spring Boot 项目
# ============================================================
FROM maven:3.9-eclipse-temurin-17 AS builder

# 后续命令都在 /app 目录中执行
WORKDIR /app

# 先复制 pom.xml，便于 Docker 缓存 Maven 依赖
COPY pom.xml .

# 下载依赖
RUN mvn dependency:go-offline -B

# 再复制源代码
COPY src ./src

# 打包应用，跳过测试
# 测试会在宿主机单独执行，镜像构建阶段先关注打包
RUN mvn clean package -DskipTests


# ============================================================
# 第二阶段：只保留 Java 运行环境和最终 jar
# ============================================================
FROM eclipse-temurin:17-jre

# 容器内的工作目录
WORKDIR /app

# 从 builder 阶段复制 Spring Boot 打包结果
COPY --from=builder /app/target/*.jar app.jar

# 声明应用监听的端口
EXPOSE 8080

# 启动 Spring Boot 应用
ENTRYPOINT ["java", "-jar", "app.jar"]
