# 使用官方的OpenJDK镜像作为基础镜像
FROM openjdk:21

# 设置工作目录
WORKDIR /app

# 复制Maven构建结果到镜像中
COPY target/ca-plus.jar /app/ca-plus.jar

# 暴露应用端口
EXPOSE 14523

# 定义启动命令，使用环境变量指定Spring配置文件
ENTRYPOINT ["java", "-jar", "ca-plus.jar"]