<template>
	<view class="container">
		<!-- 搜索栏 -->
		<view class="search-header">
			<view class="search-box">
				<uni-icons type="search" size="16" color="#666"></uni-icons>
				<input 
					class="search-input" 
					v-model="searchKey"
					placeholder="搜索视频" 
					placeholder-class="placeholder"
					confirm-type="search"
					@confirm="handleSearch"
				/>
				<uni-icons 
					v-if="searchKey" 
					type="clear" 
					size="14" 
					color="#999" 
					@click="clearSearch"
				></uni-icons>
			</view>
		</view>

		<!-- 视频列表 -->
		<view class="content-wrap">
			<view class="video-list">
				<view 
					class="video-card" 
					v-for="(item, index) in list" 
					:key="index"
					@tap="palyVideo(item.videounrealaddr)"
				>
					<view class="video-card-inner">
						<view class="video-cover">
							<image class="cover-image" :src="item.videocover" mode="aspectFill"></image>
						</view>
						<view class="video-info">
							<text class="video-title">{{item.videoname}}</text>
							<text class="video-desc">{{item.videodesc}}</text>
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
			this.getData(this.fetchPageNum);
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
				var that = this;
				var serveraddr = uni.getStorageSync('serveraddr');
				var serverport = uni.getStorageSync('serverport');
				var servertoken = uni.getStorageSync('servertoken');
				var api = `${serveraddr}:${serverport}/api/findVideos?token=${servertoken}`;
				
				uni.showLoading({
					title: '搜索中...'
				});
				
				uni.request({
					url: api,
					method: "POST",
					header: {
						'content-type': 'application/x-www-form-urlencoded'
					},
					data: {
						videodesc: that.searchKey,
						videoname: that.searchKey
					},
					success(res) {
						if(res.data.resCode == "000001"){
							that.list = [];
							var content = res.data.record.content;
							for(var i = 0; i < content.length; i++){
								content[i].videounrealaddr = `${serveraddr}:${serverport}${content[i].videounrealaddr}?apptoken=${servertoken}`;
								content[i].videocover = `${serveraddr}:${serverport}${content[i].videocover}?apptoken=${servertoken}`;
							}
							that.list = content;
						}
					},
					complete() {
						uni.hideLoading();
					}
				});
			},
			getData(page) {
				var that= this;
				var option ={
					pageNo:that.fetchPageNum
				}
				var serveraddr = uni.getStorageSync('serveraddr')
				var serverport = uni.getStorageSync('serverport')
				var servertoken = uni.getStorageSync('servertoken')
				var api =""+serveraddr+":"+serverport+"/api/findVideos?token="+servertoken;
				uni.request({
					url: api,
					method: "POST",
					header: {
						'content-type': 'application/x-www-form-urlencoded' // 默认值
					},
					data:option,
					success(res) {
						console.log(res);
						if(res.data.resCode =="000001"){
							var temp = that.list
							that.list = [];
							uni.showToast({
							    icon: 'none',
							    title: '刷新成功'
							})
							var content = res.data.record.content;
							for(var  i = 0;i<content.length;i++){
								content[i].videounrealaddr=""+serveraddr+":"+serverport+content[i].videounrealaddr+"?apptoken="+servertoken
								content[i].videocover=""+serveraddr+":"+serverport+content[i].videocover+"?apptoken="+servertoken
							}
							that.list =temp.concat(content);
							that.fetchPageNum=that.fetchPageNum+1;
							console.log(that.list);
						}
					}
				})
			},
			palyVideo(url) {
				// 找到当前视频的完整信息
				const currentVideo = this.list.find(item => item.videounrealaddr === url);
				if (currentVideo) {
					uni.navigateTo({
						url: `/pages/video/videoPlay?videoInfo=${encodeURIComponent(JSON.stringify(currentVideo))}`
					});
				}
			},
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

.search-input {
	flex: 1;
	height: 48rpx;
	font-size: 28rpx;
	color: #333;
}

.placeholder {
	color: #999;
}

.content-wrap {
	padding-top: 108rpx;
}

.video-list {
	display: flex;
	flex-wrap: wrap;
	padding: 0 12rpx;
}

.video-card {
	width: 50%;
	padding: 6rpx;
	box-sizing: border-box;
}

.video-card-inner {
	background: #fff;
	border-radius: 12rpx;
	overflow: hidden;
	box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.05);
	height: 320rpx;
	display: flex;
	flex-direction: column;
}

.video-cover {
	position: relative;
	width: 100%;
	padding-top: 56.25%; /* 16:9 比例 */
	flex-shrink: 0;
}

.cover-image {
	position: absolute;
	top: 0;
	left: 0;
	width: 100%;
	height: 100%;
	object-fit: cover;
}

.video-info {
	padding: 8rpx 12rpx;
	flex: 1;
	display: flex;
	flex-direction: column;
	justify-content: flex-start;
}

.video-title {
	font-size: 28rpx;
	font-weight: 500;
	color: #333;
	line-height: 1.3;
	margin-bottom: 4rpx;
	display: -webkit-box;
	-webkit-line-clamp: 2;
	-webkit-box-orient: vertical;
	overflow: hidden;
	text-overflow: ellipsis;
	height: 64rpx;
}

.video-desc {
	font-size: 24rpx;
	color: #999;
	line-height: 1.3;
	display: -webkit-box;
	-webkit-line-clamp: 1;
	-webkit-box-orient: vertical;
	overflow: hidden;
	text-overflow: ellipsis;
	height: 30rpx;
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

.video-player {
	width: 100vw;
	height: 56.25vw; /* 16:9 比例 */
	background: #000;
	position: relative;
}

.player {
	width: 100%;
	height: 100%;
}

/* 下拉刷新样式 */
.uni-refresh {
	background: #f6f7f8 !important;
}

.uni-refresh-icon {
	color: #fb7299 !important;
}
</style>
