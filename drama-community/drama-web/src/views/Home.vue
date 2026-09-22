<template>
  <div class="home-page">
    <el-carousel class="banner-carousel" height="480px" :interval="4000" arrow="hover">
      <el-carousel-item v-for="b in banners" :key="b.id" @click.native="goDetail(b.id)">
        <div class="banner-slide">
          <img class="banner-bg" :src="b.poster" alt="" />
          <img class="banner-poster" :src="b.poster" alt="" />
          <div class="banner-title">{{ b.name }}</div>
          <span class="banner-btn" @click.stop="goDetail(b.id)">查看剧评 →</span>
        </div>
      </el-carousel-item>
    </el-carousel>
    <div class="container">
      <h2 class="page-title">最新评价</h2>
      <div v-if="reviews.length === 0 && !loading" class="empty">
        <p>暂无评价，快去搜索剧集发表你的看法吧</p>
      </div>
      <div class="feed-list">
        <div v-for="item in reviews" :key="item.id" class="feed-card">
          <div class="feed-header">
            <router-link :to="`/drama/${item.dramaId}`" class="drama-name">{{ item.dramaName }}</router-link>
            <span class="rating">
              <el-rate v-model="item.rating" disabled :max="10" show-score text-template="{value}分" class="rate-compact"></el-rate>
            </span>
          </div>
          <p class="feed-content">{{ item.content }}</p>
          <div class="feed-footer">
            <span class="user-name">{{ item.userNickname }}</span>
            <span class="time">{{ formatTime(item.createTime) }}</span>
          </div>
        </div>
      </div>
      <div class="pagination-wrapper" v-if="total > 0">
        <el-pagination
          background
          layout="prev, pager, next"
          :total="total"
          :page-size="pageSize"
          :current-page.sync="currentPage"
          @current-change="fetchFeed"
        ></el-pagination>
      </div>
    </div>
  </div>
</template>

<script>
import { getFeed } from '@/api/review'

export default {
  name: 'Home',
  data() {
    return {
      reviews: [],
      total: 0,
      currentPage: 1,
      pageSize: 10,
      loading: false,
      banners: [
        { id: 1, name: '甄嬛传', poster: '/images/banners/甄嬛传.jpeg' },
        { id: 3, name: '琅琊榜', poster: '/images/banners/琅琊榜.jpg' },
        { id: 4, name: '狂飙', poster: '/images/banners/狂飙.jpg' },
        { id: 5, name: '三体', poster: '/images/banners/三体.jpeg' },
        { id: 6, name: '繁花', poster: '/images/banners/繁花.jpg' },
        { id: 7, name: '漫长的季节', poster: '/images/banners/漫长的季节.jpg' }
      ]
    }
  },
  created() {
    this.fetchFeed()
  },
  methods: {
    async fetchFeed() {
      this.loading = true
      try {
        const res = await getFeed({ page: this.currentPage, size: this.pageSize })
        this.reviews = res.data.records || []
        this.total = res.data.total || 0
      } catch (e) {
        this.$message.error('加载失败')
      } finally {
        this.loading = false
      }
    },
    formatTime(time) {
      if (!time) return ''
      const d = new Date(time)
      const pad = n => String(n).padStart(2, '0')
      return `${d.getFullYear()}-${pad(d.getMonth()+1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`
    },
    goDetail(id) {
      this.$router.push(`/drama/${id}`)
    }
  }
}
</script>

<style scoped>
.container {
  max-width: 960px;
  margin: 0 auto;
  padding: 24px 20px;
}
.banner-carousel {
  margin-bottom: 24px;
}
.banner-slide {
  position: relative;
  width: 100%;
  height: 100%;
  cursor: pointer;
  background: #000;
  overflow: hidden;
}
.banner-bg {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  filter: blur(30px);
  transform: scale(1.2);
  opacity: .45;
}
.banner-poster {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  height: 100%;
  object-fit: contain;
  z-index: 1;
}
.banner-title {
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  padding: 32px 20px 12px;
  font-size: 18px;
  font-weight: 600;
  color: #fff;
  background: linear-gradient(transparent, rgba(0,0,0,.65));
  z-index: 2;
}
.banner-btn {
  position: absolute;
  right: 16px;
  bottom: 14px;
  z-index: 3;
  padding: 6px 14px;
  border: 1px solid var(--brass);
  border-radius: 20px;
  color: var(--brass);
  font-size: 13px;
  background: rgba(15, 18, 24, .6);
  cursor: pointer;
  opacity: 0;
  transition: opacity .25s;
}
.banner-slide:hover .banner-btn {
  opacity: 1;
}
.page-title {
  font-size: 20px;
  color: var(--text);
  margin-bottom: 20px;
  padding-left: 12px;
  border-left: 3px solid var(--brass);
}
.empty {
  text-align: center;
  color: var(--dim);
  padding: 60px 0;
}
.feed-card {
  background: var(--panel);
  border: 1px solid var(--line);
  padding: 20px 24px;
  border-radius: 8px;
  margin-bottom: 12px;
}
.feed-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}
.drama-name {
  font-size: 16px;
  font-weight: 600;
  color: var(--brass);
  text-decoration: none;
}
.drama-name:hover {
  text-decoration: underline;
}
.rate-compact {
  display: inline-block;
}
.feed-content {
  font-size: 15px;
  color: var(--text);
  line-height: 1.7;
  margin-bottom: 12px;
}
.feed-footer {
  display: flex;
  justify-content: space-between;
  font-size: 13px;
  color: var(--dim);
}
.pagination-wrapper {
  margin-top: 24px;
  text-align: center;
}
</style>
