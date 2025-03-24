<template>
     <view>
		<view class="videolist">
			<view class="videobox" v-for="item in list" @tap="palyVideo(item.videounrealaddr)">
				<view class="video"  v-bind:style="{background:' url('+item.videocover+')',backgroundSize:'100%',backgroundRepeat:'no-repeat', backgroundPosition:'center center'}"></view>
				<view class="title">{{item.videoname}}</view>
				<view class="desc">{{item.videodesc}}</view>
			</view>
			
		</view>
		 
		<view>
			<uni-popup ref="videoPlay" type="dialog">
				<video id="myVideo" :src="videoPlay"  controls show-fullscreen-btn></video>
			</uni-popup>
		</view>
	 </view>
</template>

<script>
	export default {
		data() {
			return {
				list: [],
				fetchPageNum: 1,
				videoPlay:null
			}
		},
		onLoad() {
			this.getData(this.fetchPageNum);
		},
		onPullDownRefresh() {
			this.fetchPageNum =1;
			this.getData(this.fetchPageNum);
		},
		onReachBottom() {
			this.getData(this.fetchPageNum);
		},
		methods: {
			palyVideo(e){
				this.videoPlay =encodeURI(e);
				this.$refs.videoPlay.open()
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
							    icon: 'success',
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

		}
	}
</script>

<style>
	page{
		background: #f3f3f3;
	}
	.videolist{
		padding-left: 0.8rem;
		padding-right: 0.8rem;
	}
	.grid{
		padding-top: 10px;
	}
	.videobox{
		display: inline-block;
		text-align: center;
		vertical-align: middle;
		height: 15rem;
		width: 43.7%;
		float: left;
		margin: 0.5%;
		background-color: #fff;
		padding: 0.5rem;
		border-radius: 0.5rem;
	}
	.videobox .video{
		height: 11rem;
		/* width: 45%; */
		margin: 2%;
	}
	.videobox .title{
		font-size: 0.8rem;
		text-overflow: -o-ellipsis-lastline;
		overflow: hidden;				
		text-overflow: ellipsis;		
		display: -webkit-box;			
		-webkit-line-clamp: 1;		
		line-clamp: 1;					
		-webkit-box-orient: vertical;	
	}
	.videobox .desc{
		font-size: 0.8rem;
		text-overflow: -o-ellipsis-lastline;
		overflow: hidden;				
		text-overflow: ellipsis;		
		display: -webkit-box;			
		-webkit-line-clamp: 2;		
		line-clamp: 2;					
		-webkit-box-orient: vertical;	
	}
	.videoPlay{
		display: inline-block;
		position: absolute;
		z-index: 999;
		top: 5%;
	}
</style>
