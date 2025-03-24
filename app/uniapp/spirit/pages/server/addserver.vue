<template>
	<view>
		<view class="line">
			<view class="title">名称:</view>
			<view class="input"><input v-model="servername" placeholder="小明1号" /></view>
		</view>
		<view class="line">
			<view class="title">服务器:</view>
			<view class="input"><input v-model="server" placeholder="http://xxx.com or http://ip " /></view>
		</view>
		<view class="line">
			<view class="title">端口:</view>
			<view class="input"><input v-model="port" placeholder="11166"/></view>
		</view>
		<view class="line">
			<view class="title">token:</view>
			<view class="input"><input v-model="token" placeholder="app token" /></view>
		</view>
		<view class="option">
			<button @click="saveServer()">保存</button>
		</view>
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
.line{
	height: 3.5rem;
	line-height: 3.5rem;
	border-bottom: 1px solid #d3d0d0;
	margin: 1% 3%;
}
.line .title{
	width: 20%;
	float: left;
}
.line .input,input{
	width: 80%;
	float: left;
	height: 3.5rem;
	line-height: 3.5rem;
}
.option{
	margin-top:5%;
}
.option button{
	background-color: #0284da;
	color: #fff;
	border-radius: 12rem;
	width: 10rem;
}
</style>
