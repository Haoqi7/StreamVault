<template>
	<view>
		<view class="serverList">
			<view class="server" v-for="(item,index) in serverlist">
				<view class="left">
					<view class="line">服务器名称:{{item.servername}}</view>
					<view class="line">地址:{{item.server}}</view>
				</view>
				<view class="right">
					<view class="line" @click="swichServer(index)">{{item.default == 'y'?'默认':'切换'}}</view>
					<view class="line" @click="deleteServer(index)">删除</view>
				</view>
			</view>
			
		</view>
		
		<view class="option">
			<button @click="pageAddServer()">新增服务器</button>
		</view>
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
.serverList{
	text-align: center;
	margin-top: 5%;
}
.serverList .server{
	display: inline-block;
	width: 95%;
	text-align: left;
	border-bottom: 1px solid #959393;
	height: 4rem;
	line-height: 4rem;
}
.serverList .server .left{
	display: inline-block;
	float: left;
	width: 80%;

}
.serverList .server .line{
	height: 2rem;
	line-height: 2rem;

}
.serverList .server .right{
	display: inline-block;
	float: left;
	width: 20%;
}
.option{
	position: absolute;
	bottom: 2rem;
	width: 100%;
}
.option button{
	background-color: #0284da;
	color: #fff;
	border-radius: 12rem;
	width: 10rem;
}
</style>
