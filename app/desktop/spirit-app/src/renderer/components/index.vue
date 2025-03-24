<template>
  <div id="wrapper">
    <el-row>
      <el-col :span="24"><div class="title">视频地址提交</div></el-col>
    </el-row>
    <el-row>
      <el-col :span="24">
        <div>
          <el-input type="textarea" :rows="3" placeholder="请输入内容" v-model="urllink"></el-input>
        </div>
      </el-col>
    </el-row>
    <el-row>
      <el-col :span="24">
        <div class="submit">
          <el-button type="primary" @click="submit">提交</el-button>
        </div>
    </el-col>
    </el-row>
    <el-row class="serverconfig">
      <el-col :span="1"></el-col>
      <el-col :span="4">
        <div class="serverline">
          服务器地址
        </div>
      </el-col>
      <el-col :span="5">
        <div class="serverline">
          <el-input v-model="serveraddr" placeholder="请输入内容"></el-input>
        </div>
      </el-col>
      <el-col :span="1"></el-col>
      <el-col :span="4">
        <div class="serverline">
          服务器token
        </div>
      </el-col>
      <el-col :span="5">
        <div class="serverline">
          <el-input v-model="servertoken" placeholder="请输入内容"></el-input>
        </div>
      </el-col>
    </el-row>
    <el-row>
      <el-col :span="24">
        <div class="serverline">
          <el-button type="primary" @click="saveServer">保存服务器</el-button>
        </div>
    </el-col>
    </el-row>
    <div class="his" v-fo>
        <div v-for="data in historyList" :key="index">
          {{data}}
        </div>
    </div>
  </div>
</template>

<script>
const axios = require("axios");
export default {
  namespaced: true,
  name: 'index',
  data() {
    return {
      urllink: '',
      serveraddr:'',
      servertoken:'',
      historyList:[],
    }
  },
  created() {
    var that = this;
    that.serveraddr = localStorage.getItem("serveraddr");
    that.servertoken = localStorage.getItem("servertoken");
  },
  methods: {
    saveServer(){
      var that = this;
      console.log(that.serveraddr)
      localStorage.setItem("serveraddr",that.serveraddr);
      localStorage.setItem("servertoken",that.servertoken);
      that.$message({
          message: '保存成功',
          type: 'success'
      })
    },
    submit(){
      var that = this;
      //判断serveraddr和servertoken是否为空 如果为空则提示配置服务信息
      if(that.serveraddr == '' || that.servertoken == ''){
        that.$message({
          message: '请配置服务器信息',
          type: 'warning'
        })
        return false;
      }
      if(that.urllink == ''){
        that.$message({
          message: '请填写链接',
          type: 'warning'
        })
        return false;
      }
      var temp = that.historyList;
      temp.unshift(that.urllink);
      that.historyList = temp;
      // 通过axios 发送post 请求  请求地址是serveraddr 传入参数为video 值为urllink 和 servertoken
      axios.get(that.serveraddr+"/api/processingVideos?token="+that.servertoken+"&video="+that.urllink).then(function(response) {
        that.$message({
          message: '提交成功',
          type: 'success'
        })
        });
    },

  }
}
</script>

<style>
#wrapper .title{
  text-align: center;
  height: 5rem;
  line-height: 5rem;
}
#wrapper .submit{
  text-align: center;
  height: 5rem;
  line-height: 5rem;
}
#wrapper .serverline{
  text-align: center;
  height: 5rem;
  line-height: 5rem;
}
#wrapper .serverconfig{
  padding-left: 22%;
}
#wrapper  .his{
  display: inline-block;
  height: 160px;
  overflow-y: auto;
  width: 100%;
}
</style>