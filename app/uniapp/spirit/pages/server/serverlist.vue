<template>
	<view class="container">
		<view class="header">
			<text class="title">服务器列表</text>
		</view>
		
		<view class="server-list">
			<view class="server-card" v-for="(item,index) in serverlist" :key="index">
				<view class="server-info">
					<view class="name">
						<text class="label">{{item.servername}}</text>
						<text class="tag" v-if="item.default === 'y'">默认</text>
					</view>
					<text class="address">{{item.server}}:{{item.port}}</text>
				</view>
				
				<view class="actions">
					<button 
						class="action-btn switch" 
						:class="{'default': item.default === 'y'}"
						@click="swichServer(index)"
					>
						{{item.default === 'y' ? '当前默认' : '设为默认'}}
					</button>
					<button class="action-btn delete" @click="deleteServer(index)">
						<uni-icons type="trash" size="16" color="#666"></uni-icons>
					</button>
				</view>
			</view>
		</view>
		
		<view class="empty-state" v-if="serverlist.length === 0">
			<text class="empty-text">暂无服务器，请添加</text>
		</view>
		
		<button class="add-btn" @click="pageAddServer()">
			<text class="btn-text">添加新服务器</text>
		</button>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				serverlist:[]
			}
		},
		onShow() {
			var serverlist = uni.getStorageSync('serverlist')
			this.serverlist = serverlist;
		},
		methods: {
			swichServer:function(index){
				console.log("swicth"+index+this.serverlist);
				var temp = this.serverlist;
				console.log(temp)
				for(var i = 0;i<temp.length;i++){
					if(i==index){
						temp[i]['default'] ='y'
					}else{
						temp[i]['default'] ='n'
					}
				}
				console.log(temp)
				this.serverlist =temp;
				uni.setStorageSync('serverlist',this.serverlist)
			},
			deleteServer:function(index){
				console.log(index);
				this.serverlist.splice(index, 1);
				uni.setStorageSync('serverlist',this.serverlist)
			},
			pageAddServer:function(){
				console.log("1")
				uni.navigateTo({
					url:"/pages/server/addserver"
				})
			}
		}
	}
</script>

<style>
.container {
	min-height: 100vh;
	padding: 20rpx;
	background-color: #f5f5f5;
}

.header {
	padding: 20rpx 10rpx;
	margin-bottom: 20rpx;
}

.title {
	font-size: 32rpx;
	font-weight: 600;
	color: #333;
}

.server-list {
	display: flex;
	flex-direction: column;
	gap: 20rpx;
}

.server-card {
	background: #fff;
	border-radius: 16rpx;
	padding: 24rpx;
	box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.05);
}

.server-info {
	margin-bottom: 20rpx;
}

.name {
	display: flex;
	align-items: center;
	gap: 12rpx;
	margin-bottom: 8rpx;
}

.label {
	font-size: 30rpx;
	font-weight: 500;
	color: #333;
}

.tag {
	background: #e8f3ff;
	color: #0284da;
	font-size: 24rpx;
	padding: 4rpx 12rpx;
	border-radius: 8rpx;
}

.address {
	font-size: 26rpx;
	color: #666;
}

.actions {
	display: flex;
	align-items: center;
	gap: 16rpx;
}

.action-btn {
	flex: 1;
	height: 72rpx;
	border-radius: 36rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	font-size: 26rpx;
	background: #f5f5f5;
	color: #666;
}

.action-btn.switch {
	background: #f0f9ff;
	color: #0284da;
}

.action-btn.switch.default {
	background: #e8f3ff;
}

.action-btn.delete {
	flex: 0 0 72rpx;
	padding: 0;
}

.empty-state {
	padding: 80rpx 0;
	text-align: center;
}

.empty-text {
	font-size: 28rpx;
	color: #999;
}

.add-btn {
	position: fixed;
	bottom: 40rpx;
	left: 40rpx;
	right: 40rpx;
	background: #0284da;
	height: 88rpx;
	border-radius: 44rpx;
	display: flex;
	align-items: center;
	justify-content: center;
}

.btn-text {
	color: #fff;
	font-size: 30rpx;
	font-weight: 500;
}
</style>
