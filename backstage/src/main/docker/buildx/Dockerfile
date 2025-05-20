FROM eclipse-temurin:17-jre

ENV TZ=Asia/Shanghai
ARG BUILD_VERSION=2025.05.19.053522
ENV YT_DLP_VERSION=$BUILD_VERSION
ENV DEBIAN_FRONTEND=noninteractive
ENV YT_DLP_PATH=/usr/local/bin/yt-dlp

# 安装依赖
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
        tzdata \
        ffmpeg \
        python3 \
        python3-pip \
        python3-venv \
        python3-dev \
        build-essential \
        libffi-dev \
        wget \
    && ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \
    && echo "Asia/Shanghai" > /etc/timezone \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

# 创建虚拟环境并安装 f2
RUN python3 -m venv /opt/venv && \
    /opt/venv/bin/pip install --no-cache-dir f2

# 下载 yt-dlp
RUN wget -O /usr/local/bin/yt-dlp https://github.com/lemon8866/yt-dlp/releases/download/${YT_DLP_VERSION}/yt-dlp && \
    chmod a+rx /usr/local/bin/yt-dlp

# 应用数据目录和 JAR 包
VOLUME ["/tmp","/app"]
COPY db /home/app/db/
COPY script /home/app/script/
RUN mkdir -p /app/resources
ADD spirit-0.0.1-SNAPSHOT.jar /app.jar

# 启动入口
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app.jar", "--spring.profiles.active=docker"]
