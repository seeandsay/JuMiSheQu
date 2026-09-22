<template>
  <div class="search-page">
    <div class="container">
      <div v-if="keyword" class="search-header">
        搜索 "<span class="keyword-text">{{ keyword }}</span>" 的结果
      </div>
      <div v-if="results.length === 0 && !loading && searched" class="empty">
        <p class="empty-text">未找到相关剧集</p>
        <p class="empty-tip">该剧尚未收录，你可以创建它的剧评社区</p>
        <el-button type="primary" size="small" @click="openCreate">创建《{{ keyword }}》的剧评社区</el-button>
      </div>
      <div class="drama-list">
        <div v-for="item in results" :key="item.id" class="drama-card" @click="goDetail(item.id)">
          <img v-if="item.poster" :src="item.poster" class="poster" />
          <div v-else class="poster-placeholder">暂无海报</div>
          <div class="drama-info">
            <h3 class="name">{{ item.name }}</h3>
            <p class="genre" v-if="item.genre">{{ item.genre }}</p>
            <p class="desc" v-if="item.description">{{ item.description }}</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 创建剧集弹窗 -->
    <el-dialog title="创建剧集社区" :visible.sync="dialogVisible" width="480px">
      <el-form ref="createForm" :model="createForm" :rules="createRules" label-width="90px">
        <el-form-item label="剧集名称" prop="name">
          <el-input v-model="createForm.name" placeholder="请输入剧集名称"></el-input>
        </el-form-item>
        <el-form-item label="类型" prop="genre">
          <el-input v-model="createForm.genre" placeholder="如：悬疑,剧情,犯罪"></el-input>
        </el-form-item>
        <el-form-item label="上映日期" prop="releaseDate">
          <el-date-picker v-model="createForm.releaseDate" type="date" placeholder="选择上映日期" value-format="yyyy-MM-dd" style="width:100%"></el-date-picker>
        </el-form-item>
        <el-form-item label="简介" prop="description">
          <el-input v-model="createForm.description" type="textarea" :rows="4" placeholder="介绍一下这部剧"></el-input>
        </el-form-item>
        <el-form-item label="海报链接" prop="poster">
          <el-input v-model="createForm.poster" placeholder="可选，粘贴图片 URL 或点右侧 AI 生成">
            <el-button slot="append" :loading="posterGenerating" @click="generateAiPoster">AI 生成</el-button>
          </el-input>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="creating" @click="submitCreate">创建</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { searchDrama, createDrama } from '@/api/drama'
import { generatePoster } from '@/api/agent'

export default {
  name: 'Search',
  data() {
    return {
      keyword: '',
      results: [],
      loading: false,
      searched: false,
      dialogVisible: false,
      creating: false,
      posterGenerating: false,
      createForm: {
        name: '',
        genre: '',
        releaseDate: '',
        description: '',
        poster: ''
      },
      createRules: {
        name: [{ required: true, message: '请输入剧集名称', trigger: 'blur' }]
      }
    }
  },
  watch: {
    '$route.query.q': {
      immediate: true,
      handler(val) {
        this.keyword = val || ''
        if (this.keyword) {
          this.doSearch()
        } else {
          this.results = []
          this.searched = false
        }
      }
    }
  },
  methods: {
    async doSearch() {
      this.loading = true
      this.searched = true
      try {
        const res = await searchDrama(this.keyword)
        this.results = res.data || []
      } catch (e) {
        this.$message.error('搜索失败')
      } finally {
        this.loading = false
      }
    },
    goDetail(id) {
      this.$router.push(`/drama/${id}`)
    },
    openCreate() {
      if (!this.$store.state.token) {
        this.$router.push({ name: 'Login', query: { redirect: this.$route.fullPath } })
        return
      }
      this.createForm = {
        name: this.keyword,
        genre: '',
        releaseDate: '',
        description: '',
        poster: ''
      }
      this.dialogVisible = true
    },
    async generateAiPoster() {
      if (!this.createForm.name.trim()) {
        this.$message.warning('请先填写剧集名称')
        return
      }
      this.posterGenerating = true
      try {
        const res = await generatePoster(this.createForm.name, this.createForm.description)
        this.createForm.poster = res.posterUrl
        this.$message.success('海报已生成')
      } catch (e) {
        this.$message.error(e.response?.data?.detail || '生成失败')
      } finally {
        this.posterGenerating = false
      }
    },
    submitCreate() {
      this.$refs.createForm.validate(async valid => {
        if (!valid) return
        this.creating = true
        try {
          const res = await createDrama(this.createForm)
          this.$message.success('创建成功')
          this.dialogVisible = false
          this.$router.push(`/drama/${res.data.id}`)
        } catch (e) {
          this.$message.error(e.response?.data?.message || '创建失败')
        } finally {
          this.creating = false
        }
      })
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
.search-header {
  font-size: 15px;
  color: var(--dim);
  margin-bottom: 20px;
}
.keyword-text {
  color: var(--brass);
  font-weight: 600;
}
.empty {
  text-align: center;
  color: var(--dim);
  padding: 60px 0;
}
.empty-text {
  font-size: 15px;
  margin-bottom: 8px;
}
.empty-tip {
  font-size: 13px;
  margin-bottom: 20px;
}
.drama-card {
  display: flex;
  background: var(--panel);
  border: 1px solid var(--line);
  padding: 16px;
  border-radius: 8px;
  margin-bottom: 12px;
  cursor: pointer;
  transition: box-shadow .2s;
}
.drama-card:hover {
  box-shadow: 0 2px 8px rgba(0,0,0,.4);
}
.poster {
  width: 80px;
  height: 110px;
  border-radius: 4px;
  object-fit: cover;
  flex-shrink: 0;
}
.poster-placeholder {
  width: 80px;
  height: 110px;
  border-radius: 4px;
  background: var(--panel);
  border: 1px solid var(--line);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--dim);
  font-size: 12px;
  flex-shrink: 0;
}
.drama-info {
  margin-left: 16px;
  flex: 1;
  min-width: 0;
}
.name {
  font-size: 17px;
  color: var(--text);
  margin-bottom: 6px;
}
.genre {
  font-size: 13px;
  color: var(--dim);
  margin-bottom: 6px;
}
.desc {
  font-size: 14px;
  color: var(--dim);
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}
</style>
