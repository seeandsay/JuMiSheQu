<template>
  <div class="detail-page">
    <div class="container">
      <!-- 剧集信息 -->
      <div class="drama-section" v-if="drama">
        <div class="drama-header">
          <img v-if="drama.poster" :src="drama.poster" class="poster" />
          <div v-else class="poster-placeholder">暂无海报</div>
          <div class="drama-info">
            <h2 class="name">{{ drama.name }}</h2>
            <p class="genre" v-if="drama.genre">类型：{{ drama.genre }}</p>
            <p class="release" v-if="drama.releaseDate">上映：{{ drama.releaseDate }}</p>
            <p class="desc" v-if="drama.description">{{ drama.description }}</p>
          </div>
        </div>
        <el-button type="primary" icon="el-icon-edit" @click="goWriteReview">写评价</el-button>
      </div>

      <!-- AI 口碑速览 -->
      <div class="ai-summary-card">
        <h3 class="ai-summary-title"><i class="el-icon-magic-stick"></i> AI 口碑速览</h3>
        <p v-if="summaryMessage" class="ai-summary-empty">{{ summaryMessage }}</p>
        <p v-else-if="!summaryData" class="ai-summary-empty">口碑分析中…</p>
        <template v-else>
          <p class="ai-summary-text">{{ summaryData.summary }}</p>
          <div class="ai-keywords">
            <span v-for="k in summaryData.positiveKeywords" :key="k" class="ai-tag pos">{{ k }}</span>
            <span v-for="k in summaryData.negativeKeywords" :key="k" class="ai-tag neg">{{ k }}</span>
          </div>
          <p class="ai-audience">适合人群：{{ summaryData.audience }}</p>
        </template>
      </div>

      <!-- 评价列表 -->
      <div class="reviews-section">
        <h3>全部评价</h3>
        <div v-if="reviews.length === 0 && !loading" class="empty">暂无评价，快来写第一条吧</div>
        <div v-for="item in reviews" :key="item.id" class="review-card">
          <div class="review-header">
            <span class="user-name">{{ item.userNickname }}</span>
            <el-rate v-model="item.rating" disabled :max="10" show-score text-template="{value}分" class="rate-compact"></el-rate>
          </div>
          <p class="review-content">{{ item.content }}</p>
          <span class="review-time">{{ formatTime(item.createTime) }}</span>
        </div>
      </div>
      <div class="pagination-wrapper" v-if="total > pageSize">
        <el-pagination
          background
          layout="prev, pager, next"
          :total="total"
          :page-size="pageSize"
          :current-page.sync="currentPage"
          @current-change="fetchReviews"
        ></el-pagination>
      </div>
    </div>
  </div>
</template>

<script>
import { getDramaDetail, getDramaReviews } from '@/api/drama'
import { getSummary } from '@/api/agent'

export default {
  name: 'DramaDetail',
  data() {
    return {
      drama: null,
      reviews: [],
      total: 0,
      currentPage: 1,
      pageSize: 10,
      loading: false,
      summaryData: null,
      summaryMessage: ''
    }
  },
  created() {
    this.fetchDrama()
    this.fetchReviews()
  },
  methods: {
    async fetchDrama() {
      try {
        const res = await getDramaDetail(this.$route.params.id)
        this.drama = res.data
        this.fetchSummary()
      } catch (e) {
        this.$message.error('加载剧集信息失败')
      }
    },
    async fetchSummary() {
      try {
        const res = await getSummary(this.drama.id, this.drama.name)
        if (res.message) {
          this.summaryMessage = res.message
        } else {
          this.summaryData = res.data
        }
      } catch (e) {
        this.summaryMessage = '口碑分析暂不可用'
      }
    },
    async fetchReviews() {
      this.loading = true
      try {
        const res = await getDramaReviews(this.$route.params.id, {
          page: this.currentPage,
          size: this.pageSize
        })
        this.reviews = res.data.records || []
        this.total = res.data.total || 0
      } catch (e) {
        this.$message.error('加载评价失败')
      } finally {
        this.loading = false
      }
    },
    goWriteReview() {
      if (!this.$store.state.token) {
        this.$message.warning('请先登录')
        this.$router.push({ name: 'Login', query: { redirect: this.$route.fullPath } })
        return
      }
      this.$router.push(`/drama/${this.drama.id}/review`)
    },
    formatTime(time) {
      if (!time) return ''
      const d = new Date(time)
      const pad = n => String(n).padStart(2, '0')
      return `${d.getFullYear()}-${pad(d.getMonth()+1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`
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
.drama-section {
  background: var(--panel);
  border: 1px solid var(--line);
  padding: 24px;
  border-radius: 8px;
  margin-bottom: 24px;
}
.drama-header {
  display: flex;
  margin-bottom: 16px;
}
.poster {
  width: 140px;
  height: 190px;
  border-radius: 6px;
  object-fit: cover;
  flex-shrink: 0;
}
.poster-placeholder {
  width: 140px;
  height: 190px;
  border-radius: 6px;
  background: var(--panel);
  border: 1px solid var(--line);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--dim);
  font-size: 13px;
  flex-shrink: 0;
}
.drama-info {
  margin-left: 24px;
  flex: 1;
}
.name {
  font-size: 22px;
  color: var(--text);
  margin-bottom: 10px;
}
.genre, .release {
  font-size: 14px;
  color: var(--dim);
  margin-bottom: 6px;
}
.desc {
  font-size: 14px;
  color: var(--dim);
  line-height: 1.8;
  margin-top: 10px;
}
.ai-summary-card {
  background: var(--panel);
  border: 1px solid var(--line);
  border-left: 3px solid var(--brass);
  padding: 16px 20px;
  border-radius: 8px;
  margin-bottom: 24px;
}
.ai-summary-title {
  font-size: 14px;
  color: var(--brass);
  margin-bottom: 10px;
}
.ai-summary-empty {
  font-size: 14px;
  color: var(--dim);
}
.ai-summary-text {
  font-size: 14px;
  color: var(--text);
  line-height: 1.7;
  margin-bottom: 10px;
}
.ai-keywords {
  margin-bottom: 10px;
}
.ai-tag {
  display: inline-block;
  padding: 2px 10px;
  border-radius: 12px;
  font-size: 12px;
  margin-right: 8px;
}
.ai-tag.pos {
  color: #7EC699;
  border: 1px solid rgba(126, 198, 153, .4);
}
.ai-tag.neg {
  color: #E06C75;
  border: 1px solid rgba(224, 108, 117, .4);
}
.ai-audience {
  font-size: 13px;
  color: var(--dim);
}
.reviews-section h3 {
  font-size: 18px;
  color: var(--text);
  margin-bottom: 16px;
  padding-left: 12px;
  border-left: 3px solid var(--brass);
}
.empty {
  text-align: center;
  color: var(--dim);
  padding: 40px 0;
}
.review-card {
  background: var(--panel);
  border: 1px solid var(--line);
  padding: 16px 20px;
  border-radius: 8px;
  margin-bottom: 10px;
}
.review-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}
.user-name {
  font-size: 14px;
  color: var(--text);
  font-weight: 500;
}
.rate-compact {
  display: inline-block;
}
.review-content {
  font-size: 15px;
  color: var(--text);
  line-height: 1.7;
  margin-bottom: 10px;
}
.review-time {
  font-size: 13px;
  color: var(--dim);
}
.pagination-wrapper {
  margin-top: 20px;
  text-align: center;
}
</style>
