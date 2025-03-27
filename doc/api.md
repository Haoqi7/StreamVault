### 推送接口

```
接口地址为 http://ip:port/api/processingVideos
参数为
     token   自己后台设置的
     video   链接或分享口令

请求类型 http post
```


### 获取视频列表接口

```
接口地址为 http://ip:port/api/findVideos
参数为
     token   自己后台设置的  必填
     pageNo 页数  必填
	 pageSize  每页数量   必填
     videodesc   选填
     videoname   选填
     videoplatform   选填
请求类型 http post
```

### 资源请求问题 
如果通过接口获得视频播放链接或者视频缩略图  在访问是 请追加?apptoken=xxxx 参数 否则无法访问


### 书签提交方式
```
javascript:(function(){
    var token = "你的token";  
    var url = window.location.href; 
    fetch("http://ip:port/api/processingVideos", {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
        body: "token=" + encodeURIComponent(token) + "&video=" + encodeURIComponent(url)
    }).then(response => response.json())
      .then(data => alert("请求成功: " + JSON.stringify(data)))
      .catch(error => alert("请求失败: " + error));
})();
```