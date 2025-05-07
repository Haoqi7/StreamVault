<template>
	<view class="container">
		<!-- 视频播放区域 -->
		<view class="video-section">
			<video 
				:src="videoInfo.videounrealaddr" 
				class="video-player"
				:controls="true"
				:show-center-play-btn="true"
				:enable-progress-gesture="true"
				:show-fullscreen-btn="true"
				:show-play-btn="true"
				:show-progress="true"
				:autoplay="true"
				:poster="videoInfo.videocover"
				object-fit="contain"
				@error="handleVideoError"
			></video>
		</view>

		<!-- 视频信息区域 -->
		<view class="video-info">
			<view class="info-header">
				<view class="title-section">
					<text class="video-title">{{videoInfo.videoname}}</text>
					<view class="video-stats">
						<text class="upload-time">{{formatTime(videoInfo.createTime)}}</text>
					</view>
				</view>
			</view>

			<!-- 操作栏 -->


			<!-- 操作栏 -->
			 
			<!-- 视频描述 -->
			<view class="video-desc" v-if="videoInfo.videodesc">
				<text class="desc-text">{{videoInfo.videodesc}}</text>
			</view>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				videoInfo: {},
				isShare: false  // 是否是分享进入
			}
		},
		onLoad(options) {
			// 处理分享进入的情况
			if (options.share === 'true') {
				this.isShare = true;
				this.videoInfo = {
					videounrealaddr: decodeURIComponent(options.url || ''),
					videoname: decodeURIComponent(options.title || ''),
					videodesc: decodeURIComponent(options.desc || ''),
					createTime: decodeURIComponent(options.time || '')
				};
			} else if (options.videoInfo) {
				// 正常进入的情况
				try {
					this.videoInfo = JSON.parse(decodeURIComponent(options.videoInfo));
				} catch (e) {
					console.error('解析视频信息失败:', e);
					uni.showToast({
						title: '视频信息加载失败',
						icon: 'none'
					});
				}
			}
		},
		// 分享配置
		onShareAppMessage(res) {
			const shareInfo = {
				title: this.videoInfo.videoname,
				path: `/pages/video/videoPlay?share=true&url=${encodeURIComponent(this.videoInfo.videounrealaddr)}&title=${encodeURIComponent(this.videoInfo.videoname)}&desc=${encodeURIComponent(this.videoInfo.videodesc)}&time=${encodeURIComponent(this.videoInfo.createTime)}`,
				imageUrl: this.videoInfo.videocover
			};
			return shareInfo;
		},
		// 分享到朋友圈
		onShareTimeline() {
			const shareInfo = {
				title: this.videoInfo.videoname,
				query: `share=true&url=${encodeURIComponent(this.videoInfo.videounrealaddr)}&title=${encodeURIComponent(this.videoInfo.videoname)}&desc=${encodeURIComponent(this.videoInfo.videodesc)}&time=${encodeURIComponent(this.videoInfo.createTime)}`,
				imageUrl: this.videoInfo.videocover
			};
			return shareInfo;
		},
		methods: {
			handleVideoError(err) {
				console.error('视频播放错误:', err);
				uni.showToast({
					title: '视频加载失败',
					icon: 'none'
				});
			},
			formatTime(timestamp) {
				if (!timestamp) return '';
				const date = new Date(timestamp);
				return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
			}
		}
	}
</script>

<style>
.container {
	min-height: 100vh;
	background: #f6f7f8;
}

.video-section {
	width: 100%;
	height: 56.25vw; /* 16:9 比例 */
	background: #000;
	position: relative;
}

.video-player {
	width: 100%;
	height: 100%;
}

.video-info {
	background: #fff;
	padding: 24rpx;
}

.info-header {
	margin-bottom: 20rpx;
}

.video-title {
	font-size: 32rpx;
	font-weight: 500;
	color: #333;
	line-height: 1.4;
	margin-bottom: 12rpx;
}

.video-stats {
	display: flex;
	gap: 16rpx;
}

.upload-time {
	font-size: 24rpx;
	color: #999;
}

.action-bar {
	display: flex;
	justify-content: flex-end;
	padding: 16rpx 0;
	border-top: 1px solid #f5f5f5;
	border-bottom: 1px solid #f5f5f5;
}

.share-btn {
	display: flex;
	align-items: center;
	gap: 8rpx;
	background: none;
	border: none;
	padding: 12rpx 24rpx;
	margin: 0;
	line-height: 1;
}

.share-btn::after {
	border: none;
}

.action-text {
	font-size: 24rpx;
	color: #666;
}

.video-desc {
	padding: 20rpx 0;
}

.desc-text {
	font-size: 28rpx;
	color: #666;
	line-height: 1.6;
}
</style>
