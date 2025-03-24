### docker cli
#### 旧版本 0.0.3
```
docker run --name spirit -d -p 28081:28081 -v /home/spirit/log:/app/log -v /home/spirit/video:/app/resources/video -v /home/spirit/cover:/app/resources/cover -v /home/spirit/db:/app/db -v /home/spirit/tmp:/tmp qingfeng2336/spirit:latest
```
#### 新版本(latest,dev,steam) 
```
docker run --name spirit -d -p 28081:28081 -v /home/spirit/log:/app/log -v /home/spirit:/app/resources -v /home/spirit/db:/app/db -v /home/spirit/tmp:/tmp qingfeng2336/spirit:latest
```

### 普通分支
基于alpine镜像  支持多架构
### steamn分支介绍
```
注意此分支目前加入了 steamcmd 并且构建镜像从alpine变更为ubuntu 并且不再支持arm架构

当前支持两种模式 可自行选择  但请知道steam 只能在一个客户端上登录保持 如果其他客户端登录会顶掉当前客户端
第一种方式 
容器使用
steamcmd +login account password +workshop_download_item xxxx xxx +quit 进行下载
也就是说你需要配置帐号和密码 到本地  此帐号不能有手机令牌有邮箱令牌
在挂载目录db文件夹下新建
account.txt文件
内容为
account:xxxxx
password:xxxxx
之后重启容器即可

第二种方式 自己登录好steam 保持后台 允许手机令牌 或者邮箱令牌
部署之后 exec 进入容器终端输入
screen -S steamcmd   # 创建终端
steamcmd             #运行steamcmd
当终端显示Steam> 时 输出
login account password 回车
如果有令牌会提示输入令牌
显示OK 登陆完成后终端恢复到 Steam> 时
按下键盘ctrl+a+d 挂起当前终端  然后正常退出终端即可  注意 必须是ctrl+a+d
程序将通过
screen -x steamcmd -X  stuff "workshop_download_item xxxxx xxxxxxx \n" 发送下载命令
并保持监听 和处理/root/.steam/logs/workshop_log.txt文件

之后像接口推送https://steamcommunity.com/sharedfiles/filedetails/?id=1234567这样的链接即可下载

需要账号内拥有wallpaper 


第一种方式  重启容器无需任何操作 第二种方式 一旦docker 容器退出 需要重新登录steam 并保持后台 

如果您使用本分支 就代表您已经知悉 本容器不会操作您steam帐号上除workshop_download_item以外的事情
并且了解本容器并不会对您托管的accout.txt 文件保证安全性 本容器并不会对您的帐号进行其他操作。无论您
使用哪种方式 您的帐号都在您的本地 本容器未进行其他操作 您需要自己保证帐号安全 否则 请不要使用分支
```

### 挂载目录及端口
docker 部署下 容器端口 为28081
0.0.3 版本号 挂载目录
```
/app/resources/video       请注意此处  挂载目录必须与系统配置用推送给Aria2的下载目录一致   
                           如果不使用Aria2 只使用内置http 下载器  就正常挂载此目录
/app/resources/cover
/app/db
/app/log
/tmp
```
latest,dev,steam 版本号 挂载目录
```
/app/resources
/app/resources
/app/db
/app/log
/tmp
```
### 后台默认账号及密码
```
后台地址是 http://ip:port/admin/login
默认帐号为admin 默认密码为123456
```