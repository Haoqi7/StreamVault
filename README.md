# StreamVault

> 🎬 一站式视频资源管理与下载解决方案

## 项目简介

StreamVault（原名：spirit）是一个视频资源管理与下载平台，支持多平台视频解析和下载，提供便捷的资源管理功能。

## 主要特性

### 🎯 平台支持
- 抖音
- 哔哩哔哩
- 更多平台持续迁移中...

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
- 容器化：Docker多架构支持（AMD64/ARM）

## 🔧 部署指南

### Docker部署（推荐）

```bash
先参考spirit吧 都一样
```

### 手动部署
- 要求：Java 1.8+
- 详细部署文档待完善

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
项目参考了以下优秀的开源项目：
- [bilibili-API-collect](https://github.com/SocialSisterYi/bilibili-API-collect)
- [parsing-tiktok-video](https://toscode.gitee.com/zong_zh/parsing-tiktok-video)
- [f2](https://github.com/Johnserf-Seed/f2)
- [Light-Year-Admin-Template](https://gitee.com/yinqi/Light-Year-Admin-Template)

## 📄 许可证
MIT License
