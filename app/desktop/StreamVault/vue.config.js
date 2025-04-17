const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: true,
  pluginOptions: {
    electronBuilder: {
      builderOptions: {
        // 设置应用程序ID
        appId: 'com.streamvault.app',
        // 产品名称
        productName: 'StreamVault',
        // 便携版配置
        win: {
          target: [
            {
              target: 'portable',
              arch: ['x64']
            }
          ],
          // 图标设置
          icon: 'build/icons/icon.ico'
        },
        // 便携版特定设置
        portable: {
          artifactName: 'StreamVault-Portable-${version}.${ext}'
        },
        // 不创建安装程序
        nsis: false
      }
    }
  }
})
