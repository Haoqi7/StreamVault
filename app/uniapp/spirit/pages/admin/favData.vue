<template>
	<view class="container">
		<view class="header">
			<text class="title">收藏任务列表</text>
		</view>
		<view class="fav-list">
			<view class="fav-card" v-for="item in list" :key="item.id">
				<view class="fav-info">
					<view class="fav-row">
						<text class="fav-label">平台：</text>
						<text class="fav-value">{{item.platform}}</text>
					</view>
					<view class="fav-row">
						<text class="fav-label">任务名：</text>
						<text class="fav-value text-ellipsis" :title="item.taskname">{{item.taskname}}</text>
					</view>
					<view class="fav-row">
						<text class="fav-label">状态：</text>
						<text class="fav-value">{{item.taskstatus}}</text>
					</view>
					<view class="fav-row">
						<text class="fav-label">创建：</text>
						<text class="fav-value">{{item.createtime}}</text>
					</view>
					<view class="fav-row">
						<text class="fav-label">完成：</text>
						<text class="fav-value">{{item.endtime}}</text>
					</view>
					<view class="fav-row">
						<text class="fav-label">数量：</text>
						<text class="fav-value">{{item.count}}</text>
						<text class="fav-label">已执行：</text>
						<text class="fav-value">{{item.carriedout}}</text>
					</view>
				</view>
				<view class="fav-actions">
					<button class="delete-btn" @tap="deleteFav(item)">删除</button>
				</view>
			</view>
			<view v-if="list.length === 0" class="empty">暂无收藏任务</view>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				list: [],
				pageNo: 1,
				loading: false,
				finished: false
			}
		},
		onLoad() {
			this.getList(1);
		},
		onPullDownRefresh() {
			this.pageNo = 1;
			this.finished = false;
			this.getList(1);
		},
		onReachBottom() {
			if (!this.finished && !this.loading) {
				this.getList(this.pageNo);
			}
		},
		methods: {
			getList(page) {
				if (this.loading) return;
				this.loading = true;
				const serveraddr = uni.getStorageSync('serveraddr');
				const serverport = uni.getStorageSync('serverport');
				const adminCookie = uni.getStorageSync('adminCookie');
				uni.request({
					url: `${serveraddr}:${serverport}/admin/api/findCollectDataList`,
					method: 'POST',
					header: {
						'content-type': 'application/x-www-form-urlencoded',
						'Cookie': adminCookie
					},
					data: {
						pageNo: page
					},
					success: (res) => {
						if (res.data.resCode === '000001') {
							const content = res.data.record.content || [];
							if (page === 1) {
								this.list = content;
							} else {
								this.list = this.list.concat(content);
							}
							this.pageNo++;
							this.finished = res.data.record.last;
						} else {
							uni.showToast({title: res.data.message || '获取失败', icon: 'none'});
							// 清除登录信息并跳转
							uni.removeStorageSync('adminCookie');
							uni.removeStorageSync('adminCookieExpire');
							setTimeout(() => {
								uni.redirectTo({
									url: '/pages/admin/login'
								});
							}, 1500);
						}
					},
					fail: () => {
						uni.showToast({title: '网络错误', icon: 'none'});
						// 清除登录信息并跳转
						uni.removeStorageSync('adminCookie');
						uni.removeStorageSync('adminCookieExpire');
						setTimeout(() => {
							uni.redirectTo({
								url: '/pages/admin/login'
							});
						}, 1500);
					},
					complete: () => {
						this.loading = false;
						uni.stopPullDownRefresh();
					}
				});
			},
			deleteFav(item) {
				uni.showModal({
					title: '确认删除',
					content: '确定要删除该收藏任务吗？',
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
				uni.showLoading({title: '删除中...'});
				uni.request({
					url: `${serveraddr}:${serverport}/admin/api/deleteCollectData?id=${item.id}`,
					method: 'GET',
					header: {
						'content-type': 'application/x-www-form-urlencoded',
						'Cookie': adminCookie
					},
					success: (res) => {
						if (res.data.resCode === '000001') {
							uni.showToast({title: '删除成功', icon: 'success'});
							const idx = this.list.findIndex(v => v.id === item.id);
							if (idx > -1) this.list.splice(idx, 1);
						} else {
							uni.showToast({title: res.data.message || '删除失败', icon: 'none'});
						}
					},
					fail: () => {
						uni.showToast({title: '网络错误', icon: 'none'});
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
	padding: 24rpx;
}
.header {
	margin-bottom: 24rpx;
}
.title {
	font-size: 32rpx;
	font-weight: 600;
	color: #333;
}
.fav-list {
	display: flex;
	flex-direction: column;
	gap: 18rpx;
}
.fav-card {
	background: #fff;
	border-radius: 12rpx;
	padding: 20rpx;
	display: flex;
	align-items: flex-start;
	justify-content: space-between;
	box-shadow: 0 2rpx 8rpx rgba(0,0,0,0.05);
}
.fav-info {
	flex: 1;
	min-width: 0;
}
.fav-row {
	display: flex;
	align-items: center;
	gap: 8rpx;
	margin-bottom: 6rpx;
}
.fav-label {
	font-size: 22rpx;
	color: #888;
}
.fav-value {
	font-size: 24rpx;
	color: #333;
	max-width: 320rpx;
}
.text-ellipsis {
	white-space: nowrap;
	overflow: hidden;
	text-overflow: ellipsis;
}
.fav-actions {
	display: flex;
	flex-direction: column;
	gap: 12rpx;
	margin-left: 18rpx;
}
.delete-btn {
	background: #ff4d4f;
	color: #fff;
	border-radius: 20rpx;
	font-size: 24rpx;
	padding: 0 32rpx;
	height: 48rpx;
	line-height: 48rpx;
	border: none;
}
.empty {
	text-align: center;
	color: #999;
	font-size: 26rpx;
	margin-top: 80rpx;
}
</style>
