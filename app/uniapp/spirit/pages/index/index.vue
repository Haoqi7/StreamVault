<template>
	<view class="content">
		<textarea class="url" placeholder="输入或粘贴分享" v-model="originaladdress"></textarea>
		<view class="option">
			<button class="submit" @click="pushMessage()">提交</button>
		</view>
		<view class="service"  @click="serverList()">
			<view class="servername">服务器：{{servername}}</view>
			<uni-icons type="gear-filled" size="20"></uni-icons>
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
	.content{
		width: 100%;
		text-align: center;
	}
	.url{
		display: inline-block;
		border: 1px solid #bfbcbc;
		border-radius: 5px;
		margin-top: 4%;
	}
	.option{
		margin: 12% 26%;
		margin-bottom: 6%;
	}
	.option .submit{
		background-color: #0284da;
		color: #fff;
		border-radius: 12rem;
	}
	.servername{
		height: 1.5rem;
		line-height: 1.5rem;
	}
</style>
