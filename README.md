# StreamVault

> 一站式视频资源管理与下载解决方案

[![Docker Pulls](https://img.shields.io/docker/pulls/qingfeng2336/stream-vault)](https://hub.docker.com/r/qingfeng2336/stream-vault)

## 项目简介

StreamVault（原名：spirit）是一个视频资源管理与下载平台，支持多平台视频解析和下载，提供便捷的资源管理功能。

## 主要特性

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
| Twitter   | ✅     | 🚀             | 仅支持yt-dlp | 未来可支持其他 避免产生过多ts文件 还需要合并 麻烦 |
| Instagram | 🔨     | 🤔             |              |                                                    |
| TikTok    | 🔨     | 🤔             |              |                                                    |
| 快手      | 🤔     | 🤔             |              |                                                    |
| 微博      | 🤔     | 🤔             |              |                                                    |
| 红薯      | 🤔     | 🤔             |              |                                                    |

### 🚀 核心功能

- 视频链接解析与直链获取
- 多种下载方式支持（HTTP、Aria2）
- 哔哩哔哩收藏夹下载与监控
- 抖音作品与喜欢列表下载与监控
- NFO元数据生成
- 视频资源缓存管理

### 💻 技术栈

- 后端：Spring Boot 2.7.x + JPA + SQLite
- 前端：UniApp（支持小程序、APP等多端）
- 容器化：Docker多架构支持（AMD64/ARM64）

## 🔧 部署指南

### Docker部署（推荐）

```bash
# 拉取镜像
docker pull qingfeng2336/stream-vault

# 运行容器
docker run --name stream-vault -d -p 28083:28081 -v d:/home/spirit/log:/app/log -v d:/home/spirit/resources:/app/resources -v d:/home/spirit/db:/app/db -v d:/home/spirit/tmp:/tmp qingfeng2336/stream-vault
```

[Docker Hub](https://hub.docker.com/r/qingfeng2336/stream-vault) | [使用文档](https://github.com/qingfeng2336/stream-vault/wiki)

### 快速开始

1. 访问 http://your-ip:28081/admin/login
2. 使用默认账号密码登录
3. 在设置中删除admin  并重新新建账号
4. 开始使用

### 手动部署

- 要求：Java 1.8+
- 详细部署文档待完善

## 📸 功能展示

[待补充项目截图]

## 📝 更新日志

查看 [CHANGELOG.md](CHANGELOG.md) 了解详细更新内容。

## 📱 客户端使用

### 访问方式

- Web后台：http://your-ip:28081/admin/login
- 默认账号：admin
- 默认密码：123456

### 移动端支持

- Android APP
- 微信小程序（开发者模式）
- 其他UniApp支持的平台

## 🙏 致谢

项目参考及使用了以下优秀的开源项目：

- [bilibili-API-collect](https://github.com/SocialSisterYi/bilibili-API-collect)
- [parsing-tiktok-video](https://toscode.gitee.com/zong_zh/parsing-tiktok-video)
- [f2](https://github.com/Johnserf-Seed/f2)
- [Light-Year-Admin-Template](https://gitee.com/yinqi/Light-Year-Admin-Template)
- [yt-dlp](https://github.com/yt-dlp/yt-dlp)

## ⚖️ 协议声明

* **请严格遵守爬虫规范，不要使用此项目进行任何违法行为。**
* **不出售、共享、加密、上传、研究和传播任何个人信息。**
* **项目及其相关代码仅供学习与研究使用，不构成任何明示或暗示的保证。**
* **使用者因使用此项目及其代码可能造成的任何形式的损失，使用者应当自行承担一切风险。**
* **如果使用者使用此项目及其代码，即代表使用者同意遵守上述规定。**

## �� 许可证

MIT License
