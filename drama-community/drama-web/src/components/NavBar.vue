<template>
  <div class="navbar">
    <div class="navbar-inner">
      <router-link to="/" class="logo"><span class="logo-mark">剧</span>迷社区</router-link>
      <div class="search-box">
        <el-input
          v-model="keyword"
          placeholder="搜索剧集..."
          size="small"
          clearable
          @keyup.enter.native="handleSearch"
          @clear="handleSearch"
        >
          <el-button slot="append" icon="el-icon-search" @click="handleSearch"></el-button>
        </el-input>
      </div>
      <div class="nav-links">
        <router-link to="/agent/chat" class="nav-link ai-link">AI 找剧</router-link>
        <template v-if="isLoggedIn">
          <router-link to="/user/profile" class="nav-link">{{ username }}</router-link>
          <span class="nav-link logout-btn" @click="handleLogout">退出</span>
        </template>
        <template v-else>
          <router-link to="/login" class="nav-link">登录</router-link>
          <router-link to="/register" class="nav-link">注册</router-link>
        </template>
      </div>
    </div>
  </div>
</template>

<script>
import { mapState } from 'vuex'

export default {
  name: 'NavBar',
  data() {
    return {
      keyword: ''
    }
  },
  computed: {
    ...mapState(['token', 'userInfo']),
    isLoggedIn() {
      return !!this.token
    },
    username() {
      return this.userInfo ? this.userInfo.nickname : ''
    }
  },
  methods: {
    handleSearch() {
      const q = this.keyword.trim()
      if (q) {
        this.$router.push({ name: 'Search', query: { q } })
      } else {
        this.$router.push({ name: 'Search', query: {} })
      }
    },
    handleLogout() {
      this.$store.commit('SET_TOKEN', '')
      this.$store.commit('SET_USER_INFO', null)
      this.$router.push('/')
    }
  }
}
</script>

<style scoped>
.navbar {
  background: #12161F;
  border-bottom: 1px solid var(--line);
  position: sticky;
  top: 0;
  z-index: 100;
}
.navbar-inner {
  max-width: 960px;
  margin: 0 auto;
  padding: 0 20px;
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.logo {
  font-size: 19px;
  font-weight: bold;
  color: var(--brass);
  text-decoration: none;
  flex-shrink: 0;
  letter-spacing: .12em;
}
.logo-mark {
  display: inline-block;
  width: 26px;
  height: 26px;
  line-height: 24px;
  text-align: center;
  border: 1.5px solid var(--brass);
  border-radius: 5px;
  margin-right: 8px;
  font-size: 14px;
  letter-spacing: 0;
  vertical-align: 2px;
}
.search-box {
  width: 280px;
  margin: 0 20px;
}
.nav-links {
  display: flex;
  gap: 20px;
  align-items: center;
  flex-shrink: 0;
}
.nav-link {
  font-size: 14px;
  color: var(--dim);
  text-decoration: none;
  cursor: pointer;
}
.nav-link:hover {
  color: var(--brass);
}
.logout-btn {
  color: var(--dim);
}
.logout-btn:hover {
  color: #E06C75;
}
</style>
