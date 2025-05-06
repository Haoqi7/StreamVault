<template>
	<view class="container">
		<view class="card">
			<view class="card-header">
				<text class="title">StreamVault</text>
			</view>
			<view class="card-content">
				<textarea class="input-area" 
					placeholder="请输入或粘贴视频分享链接" 
					placeholder-class="placeholder"
					v-model="originaladdress">
				</textarea>
				<button class="submit-btn" @click="pushMessage()">
					<text class="btn-text">提交链接</text>
				</button>
			</view>
		</view>
		
		<view class="server-card" @click="serverList()">
			<view class="server-info">
				<uni-icons type="gear-filled" size="20" color="#0284da"></uni-icons>
				<text class="server-text">当前服务器：{{servername || '未选择'}}</text>
			</view>
			<uni-icons type="right" size="16" color="#999"></uni-icons>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				originaladdress:"",
				servername:"",
				serveraddr:"",
				serverport:"",
				servertoken:""
			}
		},
		onLoad() {
			// this.initServer();
		},
		onShow() {
			this.loadServer();
			uni.getClipboardData({
				success: (res) => {
					this.originaladdress = res.data
				}
			});
		},
		methods: {
			loadServer:function(){
					var that = this;
					var serverlist = uni.getStorageSync('serverlist');
					for(var i =0;i<serverlist.length;i++){
						if(serverlist[i].default == 'y'){
							that.servername = serverlist[i].servername
							that.serveraddr = serverlist[i].server
							that.serverport = serverlist[i].port
							that.servertoken = serverlist[i].token
						}
					}
					if(that.servername=="" && serverlist.length !=0){
						that.servername = serverlist[0].servername
						that.serveraddr = serverlist[0].server
						that.serverport = serverlist[0].port
						that.servertoken = serverlist[0].token
					}
					uni.setStorageSync('serveraddr',that.serveraddr)
					uni.setStorageSync('serverport',that.serverport)
					uni.setStorageSync('servertoken',that.servertoken)
			},
			serverList:function(){
				uni.navigateTo({
					url:"/pages/server/serverlist"
				})
			},
			pushMessage:function(){
				if(this.originaladdress != "" && this.serveraddr != "" && this.serverport != "" && this.servertoken != ""){
					var api =this.serveraddr+":"+this.serverport+"/api/processingVideos";
					var option ={
						token:this.servertoken,
						video:this.originaladdress
					}
					uni.showLoading({
						title:"正在获取数据"
					})
					uni.request({
						url: api,
						method: "POST",
						header: {
							'content-type': 'application/x-www-form-urlencoded' // 默认值
						},
						data:option,
						success(res) {
							if(res.data.resCode =="000001" && res.data.message != null){
								uni.hideLoading();
								uni.showToast({
									    title: res.data.message,
									    duration: 2000,
										icon: 'success'
									});
							}
							
						}
					})
					
				}
			},
			saveServer:function(){
				if(this.serveraddr != ""){
					uni.setStorageSync('serveraddr',this.serveraddr)
				}
				if(this.serverport != ""){
					uni.setStorageSync('serverport',this.serverport)
				}
				if(this.servertoken != ""){
					uni.setStorageSync('servertoken',this.servertoken)
				}
				uni.showToast({
					title: '保存成功',
					duration: 2000
				});
			},
		}
	}
</script>

<style>
.container {
	min-height: 100vh;
	padding: 20rpx;
	background-color: #f5f5f5;
}

.card {
	background: #fff;
	border-radius: 16rpx;
	padding: 30rpx;
	margin-top: 20rpx;
	box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.05);
}

.card-header {
	margin-bottom: 30rpx;
}

.title {
	font-size: 32rpx;
	font-weight: 600;
	color: #333;
}

.card-content {
	display: flex;
	flex-direction: column;
	gap: 30rpx;
}

.input-area {
	width: 100%;
	height: 200rpx;
	background: #f8f8f8;
	border-radius: 12rpx;
	padding: 20rpx;
	font-size: 28rpx;
	color: #333;
}

.placeholder {
	color: #999;
	font-size: 28rpx;
}

.submit-btn {
	background: #0284da;
	height: 88rpx;
	border-radius: 44rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	margin-top: 20rpx;
}

.btn-text {
	color: #fff;
	font-size: 30rpx;
	font-weight: 500;
}

.server-card {
	background: #fff;
	border-radius: 16rpx;
	padding: 30rpx;
	margin-top: 30rpx;
	display: flex;
	align-items: center;
	justify-content: space-between;
	box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.05);
}

.server-info {
	display: flex;
	align-items: center;
	gap: 16rpx;
}

.server-text {
	font-size: 28rpx;
	color: #333;
}
</style>
