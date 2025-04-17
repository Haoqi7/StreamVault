<template>
  <div id="app">
    <StreamVault/>
  </div>
</template>

<script>
import StreamVault from './components/StreamVault.vue'

export default {
  name: 'App',
  components: {
    StreamVault
  }
}
</script>

<style>
html, body {
  margin: 0;
  padding: 0;
  height: 100%;
}

#app {
  font-family: 'PingFang SC', 'Microsoft YaHei', Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  height: 100%;
  color: #2c3e50;
  background-color: #f6f7f9;
}
</style>
