# StreamVault 🎬

<div align="center">

> 🚀 一站式视频资源管理与下载解决方案

[![Docker Pulls](https://img.shields.io/docker/pulls/qingfeng2336/stream-vault)](https://hub.docker.com/r/qingfeng2336/stream-vault)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Java](https://img.shields.io/badge/Java-1.8+-red.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.7.x-brightgreen.svg)](https://spring.io/projects/spring-boot)

</div>

> ⚠️ **注意事项**: 
> 1. 如Docker Hub镜像版本不包含环境变量需要使用的参数（例如：代理、掩码、时区等），建议自行修改Dockerfile并编译。编译文件位于`backstage/src/main/docker/buildx`，已包含所有必要文件，仅需执行Docker编译即可。每次修复bug后都会提交更新后的jar文件，无需单独编译jar。
> 2. 更多详细部署方式和配置说明，请先查看[旧版文档](doc/README.md)和[更新日志](doc/updaterecords.md)，部署方式等可以参考旧版方式。
> 3. 更多详细细节待补充，敬请期待。

## 🌟 项目简介

StreamVault（原名：spirit）是一个视频资源管理与下载平台，支持多平台视频解析和下载，提供便捷的资源管理功能。

## ✨ 主要特性

### 🎯 平台支持

状态说明：

- ✅ 支持
- ❌ 不支持
- 🤔 考虑中
- 🔨 开发中
- 🚀 未来会做

| 平台      | 单链接 | 收藏/作品/主页 | 下载类型     | 备注                                               |
| --------- | ------ | -------------- | ------------ | -------------------------------------------------- |
| 抖音      | ✅     | ✅             | HTTP/Aria2   |                                                    |
| 哔哩哔哩  | ✅     | ✅             | HTTP/Aria2   | 主页和投稿不支持 将来会                            |
| YouTube   | ✅     | 🤔             | 仅支持yt-dlp | docker版自带 避免产生过多ts文件 还需要合并 麻烦    |
| Twitter   | ✅     | 🚀             | 仅支持yt-dlp | 未来可支持其他 避免产生过多ts文件 还需要合并 麻烦 |
| Instagram | 🔨     | 🤔             |              |                                                    |
| TikTok    | 🔨     | 🤔             |              |                                                    |
| 快手      | 🤔     | 🤔             |              |                                                    |
| 微博      | 🤔     | 🤔             |              |                                                    |
| 红薯      | 🤔     | 🤔             |              |                                                    |

### 🚀 核心功能

- 🎥 视频链接解析与直链获取
- ⬇️ 多种下载方式支持（HTTP、Aria2）
- 📚 哔哩哔哩收藏夹下载与监控
- ❤️ 抖音作品与喜欢列表下载与监控
- 📋 NFO元数据生成
- 💾 视频资源缓存管理

### 💻 技术栈

- 🛠️ 后端：Spring Boot 2.7.x + JPA + SQLite
- 📱 前端：UniApp（支持小程序、APP等多端）
- 🐳 容器化：Docker多架构支持（AMD64/ARM64）

## 🔧 部署指南

### 🐳 Docker部署（推荐）

```bash
# 拉取镜像
docker pull qingfeng2336/stream-vault

# 运行容器
docker run --name stream-vault -d -p 28083:28081 \
  -v d:/home/spirit/log:/app/log \
  -v d:/home/spirit/resources:/app/resources \
  -v d:/home/spirit/db:/app/db \
  -v d:/home/spirit/tmp:/tmp \
  qingfeng2336/stream-vault
```

[Docker Hub](https://hub.docker.com/r/qingfeng2336/stream-vault) | [使用文档](https://github.com/lemon8866/StreamVault/wiki)

### 🚀 快速开始

1. 🔗 访问 http://your-ip:28081/admin/login
2. 🔑 使用默认账号密码登录
3. ⚙️ 在设置中删除admin并重新新建账号
4. 🎉 开始使用

### 📦 手动部署

- 要求：Java 1.8+
- 详细部署文档待完善

## 📸 功能展示

[待补充项目截图]

## 📝 更新日志

查看 [更新日志](doc/updaterecords.md) 了解详细更新内容。

## 📱 客户端使用

### 🔗 访问方式

- 🌐 Web后台：http://your-ip:28081/admin/login
- 👤 默认账号：admin
- 🔑 默认密码：123456

### 📱 移动端支持

- 🤖 Android APP
- 💬 微信小程序（开发者模式）
- 🌍 其他UniApp支持的平台

## 🔌 API接口

### 📤 推送接口

```http
POST http://ip:port/api/processingVideos
参数：
- token: 后台设置的token
- video: 链接或分享口令
```

### 📋 获取视频列表

```http
POST http://ip:port/api/findVideos
参数：
- token: 后台设置的token（必填）
- pageNo: 页数（必填）
- pageSize: 每页数量（必填）
- videodesc: 视频描述（选填）
- videoname: 视频名称（选填）
- videoplatform: 视频平台（选填）
```

### 📝 书签提交方式

```javascript
javascript:(function(){
    var token = "你的token";  
    var url = window.location.href; 
    fetch("http://ip:port/api/processingVideos", {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
        body: "token=" + encodeURIComponent(token) + "&video=" + encodeURIComponent(url)
    }).then(response => response.json())
      .then(data => alert("请求成功: " + JSON.stringify(data)))
      .catch(error => alert("请求失败: " + error));
})();
```

> ⚠️ **注意**: 通过接口获取的视频播放链接或缩略图，访问时需追加 `?apptoken=xxxx` 参数，否则无法访问。

## 🙏 致谢

项目参考及使用了以下优秀的开源项目：

- [bilibili-API-collect](https://github.com/SocialSisterYi/bilibili-API-collect)
- [parsing-tiktok-video](https://toscode.gitee.com/zong_zh/parsing-tiktok-video)
- [f2](https://github.com/Johnserf-Seed/f2)
- [Light-Year-Admin-Template](https://gitee.com/yinqi/Light-Year-Admin-Template)
- [yt-dlp](https://github.com/yt-dlp/yt-dlp)

## 📄 许可证

MIT License

<div align="center">

### 🌟 欢迎 Star & Fork

</div>
