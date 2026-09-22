<template>
  <div class="profile-page">
    <div class="container">
      <h2>个人中心</h2>
      <div class="info-card" v-if="userInfo">
        <p><span class="label">昵称：</span>{{ userInfo.nickname }}</p>
        <p v-if="userInfo.phone"><span class="label">手机号：</span>{{ userInfo.phone }}</p>
        <p v-if="userInfo.email"><span class="label">邮箱：</span>{{ userInfo.email }}</p>
      </div>

      <h3 class="section-title">我的评价</h3>
      <div v-if="myReviews.length === 0 && !loading" class="empty">暂无评价</div>
      <div v-for="item in myReviews" :key="item.id" class="review-card">
        <div class="review-header">
          <router-link :to="`/drama/${item.dramaId}`" class="drama-link">{{ item.dramaName }}</router-link>
          <el-rate v-model="item.rating" disabled :max="10" show-score text-template="{value}分" class="rate-compact"></el-rate>
        </div>
        <p class="content">{{ item.content }}</p>
        <span class="time">{{ formatTime(item.createTime) }}</span>
      </div>
      <div class="pagination-wrapper" v-if="total > pageSize">
        <el-pagination
          background
          layout="prev, pager, next"
          :total="total"
          :page-size="pageSize"
          :current-page.sync="currentPage"
          @current-change="fetchMyReviews"
        ></el-pagination>
      </div>
    </div>
  </div>
</template>

<script>
import { getProfile } from '@/api/user'
import { getMyReviews } from '@/api/review'
import { mapState } from 'vuex'

export default {
  name: 'UserProfile',
  data() {
    return {
      myReviews: [],
      total: 0,
      currentPage: 1,
      pageSize: 10,
      loading: false
    }
  },
  computed: {
    ...mapState(['userInfo'])
  },
  created() {
    this.fetchProfile()
    this.fetchMyReviews()
  },
  methods: {
    async fetchProfile() {
      try {
        const res = await getProfile()
        this.$store.commit('SET_USER_INFO', res.data)
      } catch (e) {
        // ignore
      }
    },
    async fetchMyReviews() {
      this.loading = true
      try {
        const res = await getMyReviews({ page: this.currentPage, size: this.pageSize })
        this.myReviews = res.data.records || []
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
.info-card {
  background: var(--panel);
  border: 1px solid var(--line);
  padding: 24px;
  border-radius: 8px;
  margin-bottom: 32px;
  line-height: 2.2;
}
.info-card p {
  font-size: 15px;
  color: var(--text);
}
.label {
  color: var(--dim);
}
.section-title {
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
.drama-link {
  font-size: 15px;
  color: var(--brass);
  text-decoration: none;
}
.drama-link:hover {
  text-decoration: underline;
}
.rate-compact {
  display: inline-block;
}
.content {
  font-size: 15px;
  color: var(--text);
  line-height: 1.7;
  margin-bottom: 10px;
}
.time {
  font-size: 13px;
  color: var(--dim);
}
.pagination-wrapper {
  margin-top: 20px;
  text-align: center;
}
</style>
