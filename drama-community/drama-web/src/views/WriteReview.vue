<template>
  <div class="write-page">
    <div class="container">
      <h2 class="title">发布评价</h2>
      <p class="drama-name" v-if="dramaName">正在评价：《{{ dramaName }}》</p>
      <el-form ref="form" :model="form" :rules="rules" label-width="0">
        <el-form-item prop="rating">
          <div class="rating-row">
            <span class="label">评分：</span>
            <el-rate v-model="form.rating" :max="10" show-score text-template="{value}分"></el-rate>
          </div>
        </el-form-item>
        <el-form-item prop="content">
          <div class="content-area">
            <el-input
              v-model="form.content"
              type="textarea"
              :rows="6"
              placeholder="写下你的评价（最多500字）"
              maxlength="500"
              show-word-limit
            ></el-input>
            <el-button class="ai-write-btn" icon="el-icon-magic-stick" size="small" @click="aiDialogVisible = true">AI 帮我写</el-button>
          </div>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleSubmit">提交评价</el-button>
          <el-button @click="goBack">取消</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- AI 写评价弹窗 -->
    <el-dialog title="AI 帮你写评价" :visible.sync="aiDialogVisible" width="440px">
      <el-input
        v-model="aiKeywords"
        type="textarea"
        :rows="4"
        placeholder="输入几个关键词或感受，例如：张译演技炸裂 剧情前期拖沓 结局意难平"
      ></el-input>
      <div slot="footer">
        <el-button @click="aiDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="aiGenerating" @click="generateReview">生成草稿</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { createReview } from '@/api/review'
import { getDramaDetail } from '@/api/drama'
import { writeReview } from '@/api/agent'

export default {
  name: 'WriteReview',
  data() {
    return {
      dramaName: '',
      loading: false,
      aiDialogVisible: false,
      aiGenerating: false,
      aiKeywords: '',
      form: {
        rating: 0,
        content: ''
      },
      rules: {
        rating: [
          { required: true, message: '请评分', trigger: 'change' },
          { type: 'number', min: 1, max: 10, message: '评分范围为1-10', trigger: 'change' }
        ],
        content: [
          { required: true, message: '请写下评价内容', trigger: 'blur' },
          { max: 500, message: '最多500字', trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.fetchDramaName()
  },
  methods: {
    async fetchDramaName() {
      try {
        const res = await getDramaDetail(this.$route.params.id)
        this.dramaName = res.data.name
      } catch (e) {
        // ignore
      }
    },
    handleSubmit() {
      this.$refs.form.validate(async valid => {
        if (!valid) return
        this.loading = true
        try {
          await createReview({
            dramaId: Number(this.$route.params.id),
            rating: this.form.rating,
            content: this.form.content
          })
          this.$message.success('评价发布成功')
          this.$router.push(`/drama/${this.$route.params.id}`)
        } catch (e) {
          this.$message.error(e.response?.data?.message || '发布失败')
        } finally {
          this.loading = false
        }
      })
    },
    goBack() {
      this.$router.push(`/drama/${this.$route.params.id}`)
    },
    async generateReview() {
      if (!this.aiKeywords.trim()) {
        this.$message.warning('先输入几个关键词或感受')
        return
      }
      this.aiGenerating = true
      try {
        const res = await writeReview(this.dramaName, this.aiKeywords.trim())
        // 防御性截断：AI 输出偶发超过 500 字，避免提交被后端拒绝
        this.form.content = (res.content || '').slice(0, 500)
        this.aiDialogVisible = false
        this.$message.success('已生成草稿，可以编辑后发布')
      } catch (e) {
        this.$message.error(e.response?.data?.detail || '生成失败')
      } finally {
        this.aiGenerating = false
      }
    }
  }
}
</script>

<style scoped>
.container {
  max-width: 700px;
  margin: 0 auto;
  padding: 24px 20px;
}
.title {
  font-size: 20px;
  color: var(--text);
  margin-bottom: 8px;
  padding-left: 12px;
  border-left: 3px solid var(--brass);
}
.drama-name {
  font-size: 14px;
  color: var(--dim);
  margin-bottom: 24px;
}
.rating-row {
  display: flex;
  align-items: center;
}
.label {
  font-size: 14px;
  color: var(--dim);
  margin-right: 12px;
}
.content-area {
  width: 100%;
}
.ai-write-btn {
  margin-top: 8px;
}
</style>
