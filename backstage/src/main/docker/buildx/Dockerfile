FROM eclipse-temurin:17-jre-alpine
ENV TZ=Asia/Shanghai
ARG BUILD_VERSION=2025.05.19.053522
ENV YT_DLP_VERSION=$BUILD_VERSION
RUN apk upgrade --update-cache && \
    apk add ffmpeg && \
    apk add python3 py3-pip py3-virtualenv build-base python3-dev libffi-dev&& \
    rm -rf /tmp/* /var/cache/apk/*
	
RUN python3 -m venv /opt/venv && \
    . /opt/venv/bin/activate && \
    pip install --no-cache-dir f2
	
RUN wget -O /usr/local/bin/yt-dlp https://github.com/lemon8866/yt-dlp/releases/download/${YT_DLP_VERSION}/yt-dlp && chmod a+rx /usr/local/bin/yt-dlp
ENV YT_DLP_PATH=/usr/local/bin/yt-dlp
VOLUME ["/tmp","/app"]
COPY db /home/app/db/
COPY script /home/app/script/
RUN mkdir -p /app/resources
ADD spirit-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar","--spring.profiles.active=docker"]