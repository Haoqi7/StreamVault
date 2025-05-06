<template>
	<view class="container">
		<view class="card">
			<view class="card-header">
				<text class="title">添加服务器</text>
			</view>
			<view class="form-group">
				<view class="form-item">
					<text class="label">服务器名称</text>
					<input 
						class="input" 
						v-model="servername" 
						placeholder="给服务器起个名字" 
						placeholder-class="placeholder"
					/>
				</view>
				
				<view class="form-item">
					<text class="label">服务器地址</text>
					<input 
						class="input" 
						v-model="server" 
						placeholder="http://xxx.com 或 http://ip" 
						placeholder-class="placeholder"
					/>
				</view>
				
				<view class="form-item">
					<text class="label">端口</text>
					<input 
						class="input" 
						v-model="port" 
						placeholder="请输入端口号" 
						placeholder-class="placeholder"
					/>
				</view>
				
				<view class="form-item">
					<text class="label">Token</text>
					<input 
						class="input" 
						v-model="token" 
						placeholder="请输入授权token" 
						placeholder-class="placeholder"
					/>
				</view>
			</view>
		</view>
		
		<button class="submit-btn" @click="saveServer()">
			<text class="btn-text">保存服务器</text>
		</button>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				serverlist:[],
				servername:"",
				server:"",
				port:"",
				token:""
			}
		},
		onShow() {
			var serverlist = uni.getStorageSync('serverlist')
			if(serverlist!=""){
				this.serverlist = serverlist;
			}
			
		},
		methods: {
			saveServer:function(){
				var that =  this;
				if(that.serverlist.length <9){
					if(that.servername != "" && that.server != "" && that.port != "" && that.token != "" ){
						var defaultstatus = "n";
						if(that.serverlist == 0){
							defaultstatus = "y";
						}
						var data = {
							servername:that.servername,
							server:that.server,
							port:that.port,
							token:that.token,
							default:defaultstatus
						}
						var temp = that.serverlist;
						temp.push(data);
						that.serverlist = temp;
						uni.setStorageSync('serverlist',temp)
						uni.showModal({
							content: '保存成功',
							showCancel:false,
							success: function (res) {
								if (res.confirm) {
									uni.navigateBack({});
								} 
							}
						});
					}else{
						return;
					}
				}else{
					return;
				}
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

.card {
	background: #fff;
	border-radius: 16rpx;
	padding: 30rpx;
	margin-bottom: 30rpx;
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

.form-group {
	display: flex;
	flex-direction: column;
	gap: 24rpx;
}

.form-item {
	display: flex;
	flex-direction: column;
	gap: 12rpx;
}

.label {
	font-size: 28rpx;
	color: #666;
	font-weight: 500;
}

.input {
	height: 88rpx;
	background: #f8f8f8;
	border-radius: 12rpx;
	padding: 0 24rpx;
	font-size: 28rpx;
	color: #333;
}

.placeholder {
	color: #999;
}

.submit-btn {
	background: #0284da;
	height: 88rpx;
	border-radius: 44rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	margin: 40rpx 30rpx;
}

.btn-text {
	color: #fff;
	font-size: 30rpx;
	font-weight: 500;
}
</style>
