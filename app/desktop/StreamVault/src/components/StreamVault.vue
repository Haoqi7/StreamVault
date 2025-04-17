<template>
  <div class="stream-vault">
    <!-- 顶部导航栏 -->
    <div class="header">
      <div class="header-content">
        <div class="search-box">
          <el-input
            v-model="searchQuery"
            placeholder="搜索视频"
            class="modern-search"
            clearable
            @keyup.enter="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
            <template #append>
              <el-button @click="handleSearch">
                搜索
              </el-button>
            </template>
          </el-input>
        </div>
        <div class="settings">
          <el-popover
            placement="bottom-end"
            :width="300"
            trigger="click"
          >
            <template #reference>
              <el-button class="settings-btn" circle>
                <el-icon><Setting /></el-icon>
              </el-button>
            </template>
            <div class="settings-panel">
              <h3>设置</h3>
              <el-form label-position="top">
                <el-form-item label="服务器地址">
                  <el-input v-model="settings.serverUrl" placeholder="请输入服务器地址" />
                </el-form-item>
                <el-form-item label="访问令牌">
                  <el-input v-model="settings.token" type="password" show-password placeholder="请输入访问令牌" />
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="saveSettings">保存设置</el-button>
                </el-form-item>
              </el-form>
            </div>
          </el-popover>
        </div>
      </div>
    </div>

    <!-- 内容区域 -->
    <div 
      class="content" 
      v-loading="loading"
      :element-loading-text="'加载中...'"
      :element-loading-background="'rgba(255, 255, 255, 0.9)'"
      v-infinite-scroll="loadMore"
      :infinite-scroll-disabled="disabled"
      :infinite-scroll-distance="10"
    >
      <!-- 媒体列表 -->
      <div class="media-list">
        <el-row :gutter="12">
          <el-col v-for="item in mediaList" :key="item.id" :xs="12" :sm="8" :md="6" :lg="4">
            <div class="media-card-wrapper" @click="handleVideoClick(item)">
              <el-card :body-style="{ padding: '0px' }" class="media-card" shadow="hover">
                <div class="media-cover-container">
                  <img 
                    :src="getFullMediaUrl(item.videocover)" 
                    class="media-cover"
                    :alt="item.videoname"
                  >
                  <div class="media-platform">
                    <el-tag :type="getPlatformType(item.videoplatform)" size="small" effect="dark">
                      {{ item.videoplatform }}
                    </el-tag>
                  </div>
                  <div class="media-overlay">
                    <el-icon class="play-icon"><VideoPlay /></el-icon>
                  </div>
                </div>
                <div class="media-info">
                  <h4 class="media-title" :title="item.videoname">{{ item.videoname }}</h4>
                  <p class="media-desc" :title="item.videodesc">{{ item.videodesc }}</p>
                  <div class="media-stats">
                    <span class="media-time">
                      <el-icon><Clock /></el-icon>
                      {{ formatTime(item.createtime) }}
                    </span>
                  </div>
                </div>
              </el-card>
            </div>
          </el-col>
        </el-row>
      </div>

      <!-- 加载状态 -->
      <div v-if="loading" class="loading-more">
        <el-icon class="loading-icon"><Loading /></el-icon>
        正在加载更多...
      </div>
      <div v-if="noMore" class="no-more">
        <el-divider>没有更多数据了</el-divider>
      </div>

      <!-- 视频预览弹窗 -->
      <el-dialog
        v-model="dialogVisible"
        :title="currentVideo?.videoname"
        width="70%"
        :fullscreen="isFullscreen"
        :append-to-body="true"
        destroy-on-close
        class="video-dialog"
      >
        <div class="video-player-container">
          <video
            v-if="currentVideo"
            ref="videoPlayer"
            class="video-player"
            controls
            :src="getFullMediaUrl(currentVideo.videounrealaddr)"
            @click="toggleFullscreen"
          ></video>
        </div>
        <template #footer>
          <span class="dialog-footer">
            <el-button @click="toggleFullscreen">
              {{ isFullscreen ? '退出全屏' : '全屏' }}
            </el-button>
            <el-button type="primary" @click="dialogVisible = false">
              关闭
            </el-button>
          </span>
        </template>
      </el-dialog>
    </div>
  </div>
</template>

<script>
import { ref, reactive, onMounted, watch, computed } from 'vue'
import { Setting, Clock, Loading, Search, VideoPlay } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'
import dayjs from 'dayjs'

// 创建axios实例
const createAxiosInstance = (baseURL) => {
  // 在开发环境下使用本地代理
  const isDev = process.env.NODE_ENV === 'development'
  return axios.create({
    // 开发环境使用相对路径，生产环境使用完整URL
    baseURL: isDev ? '' : baseURL,
    timeout: 10000
  })
}

export default {
  name: 'StreamVault',
  components: {
    Setting,
    Clock,
    Loading,
    Search,
    VideoPlay
  },
  setup() {
    const searchQuery = ref('')
    const settings = reactive({
      serverUrl: localStorage.getItem('serverUrl') || '',
      token: getTokenFromUrl() || localStorage.getItem('token') || ''
    })
    const loading = ref(false)
    const mediaList = ref([])
    const currentPage = ref(0) // 从0开始计数
    const pageSize = ref(15)
    const total = ref(0)
    const axiosInstance = ref(createAxiosInstance(settings.serverUrl))
    const dialogVisible = ref(false)
    const currentVideo = ref(null)
    const isFullscreen = ref(false)
    const videoPlayer = ref(null)
    const noMore = ref(false)
    const hasError = ref(false)

    // 计算是否禁用滚动加载
    const disabled = computed(() => {
      // 如果加载中、没有更多数据、或者服务器配置有误，都禁用无限滚动
      return loading.value || noMore.value || hasError.value
    })

    // 从 URL 获取 token
    function getTokenFromUrl() {
      const urlParams = new URLSearchParams(window.location.search)
      return urlParams.get('token')
    }

    // 获取完整的媒体URL
    const getFullMediaUrl = (url) => {
      if (!url || !settings.serverUrl) return ''
      return `${settings.serverUrl}${url}?apptoken=${settings.token}`
    }

    // 获取平台对应的标签类型
    const getPlatformType = (platform) => {
      const types = {
        '抖音': 'danger',
        '哔哩': 'primary',
        '快手': 'warning'
      }
      return types[platform] || 'info'
    }

    // 格式化时间
    const formatTime = (time) => {
      return dayjs(time).format('MM-DD HH:mm')
    }

    // 处理视频点击
    const handleVideoClick = (video) => {
      currentVideo.value = video
      dialogVisible.value = true
    }

    // 切换全屏
    const toggleFullscreen = () => {
      isFullscreen.value = !isFullscreen.value
    }

    // 加载更多数据
    const loadMore = async () => {
      if (loading.value || noMore.value || hasError.value) return
      
      if (!settings.token) {
        ElMessage.warning('请先设置访问令牌')
        hasError.value = true
        return
      }

      if (!settings.serverUrl) {
        ElMessage.warning('请先设置服务器地址')
        hasError.value = true
        return
      }

      loading.value = true
      try {
        // 创建表单数据
        const formData = new URLSearchParams()
        formData.append('pageNo', String(Number(currentPage.value) + 1))
        formData.append('pageSize', String(Number(pageSize.value)))
        
        if (searchQuery.value) {
          formData.append('videodesc', searchQuery.value)
          formData.append('videoname', searchQuery.value)
        }

        const response = await axiosInstance.value.post(
          `/api/findVideos?token=${settings.token}`,
          formData,
          {
            headers: {
              'Content-Type': 'application/x-www-form-urlencoded'
            }
          }
        )

        if (response.data && response.data.resCode === '000001') {
          if (response.data.record && response.data.record.content) {
            const newContent = response.data.record.content
            if (newContent.length > 0) {
              mediaList.value = [...mediaList.value, ...newContent]
              currentPage.value++
              total.value = response.data.record.totalElements || 0
              // 检查是否还有更多数据
              noMore.value = mediaList.value.length >= total.value
            } else {
              noMore.value = true
            }
          }
        } else {
          ElMessage.error(response.data.message || '获取视频列表失败')
        }
      } catch (error) {
        console.error('获取视频列表出错:', error)
        ElMessage.error('获取视频列表失败，请检查服务器地址和网络连接')
        hasError.value = true
      } finally {
        loading.value = false
      }
    }

    // 重置列表
    const resetList = () => {
      mediaList.value = []
      currentPage.value = 0  // 重置为0，因为loadMore时会+1变成1
      noMore.value = false
      hasError.value = false  // 重置错误状态
      loadMore()
    }

    // 处理搜索
    const handleSearch = () => {
      resetList()
    }

    // 保存设置
    const saveSettings = () => {
      localStorage.setItem('serverUrl', settings.serverUrl)
      localStorage.setItem('token', settings.token)
      ElMessage.success('设置已保存')
      // 保存设置后重新获取视频列表
      resetList()
    }

    // 监听设置变化
    watch(
      () => ({ token: settings.token, serverUrl: settings.serverUrl }),
      (newVal) => {
        axiosInstance.value = createAxiosInstance(newVal.serverUrl)
        if (newVal.token && newVal.serverUrl) {
          resetList()
        }
      },
      { deep: true }
    )

    // 组件挂载时获取视频列表
    onMounted(() => {
      if (settings.token && settings.serverUrl) {
        loadMore()
      }
    })

    return {
      searchQuery,
      settings,
      loading,
      mediaList,
      dialogVisible,
      currentVideo,
      isFullscreen,
      videoPlayer,
      noMore,
      disabled,
      handleSearch,
      saveSettings,
      getFullMediaUrl,
      getPlatformType,
      formatTime,
      handleVideoClick,
      toggleFullscreen,
      loadMore,
      hasError
    }
  }
}
</script>

<style scoped>
.stream-vault {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: #f5f7fa;
}

.header {
  padding: 16px 0;
  background-color: #fff;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-content {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-box {
  width: 460px;
}

.modern-search :deep(.el-input__wrapper) {
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.modern-search :deep(.el-input__inner) {
  height: 40px;
}

.settings-btn {
  font-size: 20px;
  transition: transform 0.2s ease;
}

.settings-btn:hover {
  transform: rotate(30deg);
}

.settings-panel {
  padding: 20px;
}

.settings-panel h3 {
  margin: 0 0 20px;
  font-size: 18px;
  color: #303133;
}

.content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  max-width: 1400px;
  margin: 0 auto;
  width: 100%;
  box-sizing: border-box;
}

.media-card-wrapper {
  margin-bottom: 16px;
  cursor: pointer;
}

.media-card {
  border-radius: 8px;
  overflow: hidden;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: none;
}

.media-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
}

.media-cover-container {
  position: relative;
  width: 100%;
  height: 0;
  padding-bottom: 56.25%; /* 16:9 比例 */
  overflow: hidden;
}

.media-cover {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.media-card:hover .media-cover {
  transform: scale(1.05);
}

.media-platform {
  position: absolute;
  top: 8px;
  right: 8px;
  z-index: 2;
}

.media-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.media-card:hover .media-overlay {
  opacity: 1;
}

.play-icon {
  font-size: 48px;
  color: #fff;
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.2));
}

.media-info {
  padding: 12px;
  background: #fff;
}

.media-title {
  margin: 0 0 8px;
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  line-height: 1.4;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.media-desc {
  margin: 0 0 8px;
  font-size: 12px;
  color: #606266;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  max-height: 36px;
  line-height: 1.5;
}

.media-stats {
  display: flex;
  align-items: center;
  gap: 12px;
  color: #909399;
  font-size: 12px;
}

.media-stats span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.loading-more {
  text-align: center;
  margin: 20px 0;
  color: #909399;
  font-size: 14px;
}

.loading-icon {
  animation: rotating 2s linear infinite;
  margin-right: 6px;
}

.no-more {
  padding: 20px 0;
  color: #909399;
}

@keyframes rotating {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

.video-dialog :deep(.el-dialog__header) {
  border-bottom: 1px solid #f0f0f0;
  padding: 16px 20px;
  margin: 0;
}

.video-player-container {
  width: 100%;
  display: flex;
  justify-content: center;
  background-color: #000;
  border-radius: 4px;
  overflow: hidden;
}

.video-player {
  width: 100%;
  max-height: 80vh;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 16px 20px;
  border-top: 1px solid #f0f0f0;
}

/* 响应式调整 */
@media screen and (max-width: 768px) {
  .search-box {
    width: 280px;
  }

  .header-content {
    padding: 0 16px;
  }

  .content {
    padding: 16px;
  }
}
</style>
