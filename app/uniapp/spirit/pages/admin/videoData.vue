<template>
	<view class="container">
		<!-- 搜索栏 -->
		<view class="search-header">
			<view class="search-box">
				<text class="icon">🔍</text>
				<input 
					class="search-input" 
					v-model="searchKey"
					placeholder="搜索视频" 
					placeholder-class="placeholder"
					confirm-type="search"
					@confirm="handleSearch"
				/>
				<text 
					v-if="searchKey" 
					class="clear-icon"
					@tap="clearSearch"
				>✕</text>
			</view>
		</view>

		<!-- 视频列表 -->
		<view class="content-wrap">
			<view class="video-list">
				<view 
					class="video-card" 
					v-for="(item, index) in list" 
					:key="index"
				>
					<view class="video-info">
						<image class="video-cover" :src="item.videocover" mode="aspectFill"></image>
						<view class="info-action-wrap">
							<view class="info-content">
								<text class="video-title text-ellipsis" :title="item.videoname">{{item.videoname}}</text>
								<text class="video-desc text-ellipsis" :title="item.videodesc">{{item.videodesc}}</text>
								<text class="video-time">{{item.createtime}}</text>
							</view>
							<view class="action-buttons-vertical">
								<button class="action-btn play" @tap="playVideo(item)"><text class="btn-text">播放</text></button>
								<button class="action-btn delete" @tap="deleteVideo(item)"><text class="btn-text">删除</text></button>
							</view>
						</view>
					</view>
				</view>
			</view>

			<!-- 加载更多 -->
			<view class="loading-more" v-if="list.length > 0">
				<text class="loading-text">加载中...</text>
			</view>

			<!-- 空状态 -->
			<view class="empty-state" v-if="list.length === 0">
				<text class="empty-text">暂无视频内容</text>
			</view>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				list: [],
				fetchPageNum: 1,
				searchKey: '',
				isSearching: false
			}
		},
		onLoad() {
			this.checkLogin();
		},
		onPullDownRefresh() {
			if (this.isSearching) {
				this.handleSearch();
			} else {
				this.fetchPageNum = 1;
				this.getData(this.fetchPageNum);
			}
		},
		onReachBottom() {
			if (!this.isSearching) {
				this.getData(this.fetchPageNum);
			}
		},
		methods: {
			checkLogin() {
				const adminCookie = uni.getStorageSync('adminCookie');
				if (!adminCookie) {
					uni.showToast({
						title: '请先登录',
						icon: 'none',
						duration: 1500
					});
					setTimeout(() => {
						uni.redirectTo({
							url: '/pages/admin/login'
						});
					}, 1500);
				} else {
					this.getData(1);
				}
			},
			clearSearch() {
				this.searchKey = '';
				this.isSearching = false;
				this.fetchPageNum = 1;
				this.getData(this.fetchPageNum);
			},
			handleSearch() {
				if (!this.searchKey.trim()) {
					return;
				}
				this.isSearching = true;
				this.fetchPageNum = 1;
				this.getData(this.fetchPageNum);
			},
			getData(page) {
				const serveraddr = uni.getStorageSync('serveraddr');
				const serverport = uni.getStorageSync('serverport');
				const adminCookie = uni.getStorageSync('adminCookie');
				const servertoken = uni.getStorageSync('servertoken');
				
				if (!serveraddr || !serverport || !adminCookie) {
					return;
				}
				
				uni.showLoading({
					title: '加载中...'
				});
				
				uni.request({
					url: `${serveraddr}:${serverport}/admin/api/findVideoDataList`,
					method: 'POST',
					header: {
						'content-type': 'application/x-www-form-urlencoded',
						'Cookie': adminCookie
					},
					data: {
						pageNo: page,
						videodesc: this.searchKey,
						videoname: this.searchKey
					},
					success: (res) => {
						if (res.data.resCode === '000001') {
							if (page === 1) {
								this.list = [];
							}
							const content = res.data.record.content;
							for (let i = 0; i < content.length; i++) {
								content[i].videounrealaddr = `${serveraddr}:${serverport}${content[i].videounrealaddr}?apptoken=${servertoken}`;
								content[i].videocover = `${serveraddr}:${serverport}${content[i].videocover}?apptoken=${servertoken}`;
							}
							this.list = this.list.concat(content);
							this.fetchPageNum++;
						} else {
							uni.showToast({
								title: res.data.message || '获取数据失败',
								icon: 'none'
							});
						}
					},
					fail: () => {
						uni.showToast({
							title: '网络错误',
							icon: 'none'
						});
					},
					complete: () => {
						uni.hideLoading();
						uni.stopPullDownRefresh();
					}
				});
			},
			playVideo(item) {
				uni.navigateTo({
					url: `/pages/video/videoPlay?videoInfo=${encodeURIComponent(JSON.stringify(item))}`
				});
			},
			deleteVideo(item) {
				uni.showModal({
					title: '确认删除',
					content: '确定要删除这个视频吗？',
					success: (res) => {
						if (res.confirm) {
							this.confirmDelete(item);
						}
					}
				});
			},
			confirmDelete(item) {
				const serveraddr = uni.getStorageSync('serveraddr');
				const serverport = uni.getStorageSync('serverport');
				const adminCookie = uni.getStorageSync('adminCookie');
				
				uni.showLoading({
					title: '删除中...'
				});
				
				uni.request({
					url: `${serveraddr}:${serverport}/admin/api/deleteVideoData?id=${item.id}`,
					method: 'GET',
					header: {
						'content-type': 'application/x-www-form-urlencoded',
						'Cookie': adminCookie
					},
					success: (res) => {
						if (res.data.resCode === '000001') {
							uni.showToast({
								title: '删除成功',
								icon: 'success'
							});
							// 从列表中移除
							const index = this.list.findIndex(v => v.id === item.id);
							if (index > -1) {
								this.list.splice(index, 1);
							}
						} else {
							uni.showToast({
								title: res.data.message || '删除失败',
								icon: 'none'
							});
						}
					},
					fail: () => {
						uni.showToast({
							title: '网络错误',
							icon: 'none'
						});
					},
					complete: () => {
						uni.hideLoading();
					}
				});
			}
		}
	}
</script>

<style>
.container {
	min-height: 100vh;
	background: #f6f7f8;
}

.search-header {
	position: fixed;
	top: 0;
	left: 0;
	right: 0;
	z-index: 100;
	background: #fff;
	padding: 16rpx 24rpx;
	box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.05);
}

.search-box {
	display: flex;
	align-items: center;
	background: #f5f5f5;
	border-radius: 36rpx;
	padding: 12rpx 24rpx;
	gap: 12rpx;
}

.icon {
	font-size: 32rpx;
	color: #999;
}

.search-input {
	flex: 1;
	height: 48rpx;
	font-size: 28rpx;
	color: #333;
}

.placeholder {
	color: #999;
}

.clear-icon {
	font-size: 24rpx;
	color: #999;
	padding: 8rpx;
}

.content-wrap {
	padding-top: 108rpx;
}

.video-list {
	padding: 24rpx;
}

.video-card {
	background: #fff;
	border-radius: 8rpx;
	padding: 18rpx;
	margin-bottom: 14rpx;
	box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.05);
}

.video-info {
	display: flex;
	gap: 18rpx;
	margin-bottom: 0;
	align-items: center;
}

.video-cover {
	width: 130rpx;
	height: 80rpx;
	border-radius: 6rpx;
	background: #f5f5f5;
	flex-shrink: 0;
}

.info-action-wrap {
	display: flex;
	flex: 1;
	align-items: center;
	gap: 18rpx;
	min-width: 0;
}

.info-content {
	flex: 1;
	display: flex;
	flex-direction: column;
	gap: 4rpx;
	min-width: 0;
}

.video-title {
	font-size: 24rpx;
	font-weight: 500;
	color: #333;
	line-height: 1.2;
	width: 100%;
}

.video-desc {
	font-size: 20rpx;
	color: #666;
	line-height: 1.2;
	width: 100%;
}

.video-time {
	font-size: 18rpx;
	color: #999;
	width: 100%;
}

.text-ellipsis {
	white-space: nowrap;
	overflow: hidden;
	text-overflow: ellipsis;
	width: 100%;
}

.action-buttons-vertical {
	display: flex;
	flex-direction: column;
	gap: 8rpx;
	margin-left: 8rpx;
}

.action-btn {
	height: 36rpx;
	padding: 0 16rpx;
	border-radius: 18rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	font-size: 20rpx;
	min-width: 60rpx;
}

.action-btn.play {
	background: #0284da;
	color: #fff;
}

.action-btn.delete {
	background: #ff4d4f;
	color: #fff;
}

.loading-more {
	text-align: center;
	padding: 30rpx 0;
}

.loading-text {
	font-size: 26rpx;
	color: #999;
}

.empty-state {
	padding: 120rpx 0;
	text-align: center;
}

.empty-text {
	font-size: 28rpx;
	color: #999;
}
</style>
