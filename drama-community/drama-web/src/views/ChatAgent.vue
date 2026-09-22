<template>
  <div class="chat-page">
    <div class="chat-container">
      <div class="chat-header">
        <h2 class="chat-title"><i class="el-icon-magic-stick"></i> AI 找剧助手</h2>
        <p class="chat-subtitle">告诉我你想看什么，我帮你从社区剧库里找</p>
      </div>

      <div class="chat-body" ref="chatBody">
        <div v-if="messages.length === 0" class="chat-welcome">
          <p>试试这样问：</p>
          <div class="sample-list">
            <span class="sample" @click="sendSample('想看类似狂飙的犯罪剧')">想看类似狂飙的犯罪剧</span>
            <span class="sample" @click="sendSample('有什么好看的悬疑剧？')">有什么好看的悬疑剧？</span>
            <span class="sample" @click="sendSample('推荐一部适合下饭的古装剧')">推荐一部适合下饭的古装剧</span>
          </div>
        </div>

        <div v-for="(m, idx) in messages" :key="idx" class="chat-row" :class="m.role">
          <div class="chat-bubble" :class="m.role">
            {{ m.content }}
          </div>
          <div v-if="m.recommendations && m.recommendations.length" class="rec-list">
            <div v-for="r in m.recommendations" :key="r.dramaId" class="rec-card" @click="$router.push(`/drama/${r.dramaId}`)">
              <span class="rec-name">{{ r.name }}</span>
              <span class="rec-reason">{{ r.reason }}</span>
              <span class="rec-arrow">→</span>
            </div>
          </div>
        </div>

        <div v-if="sending" class="chat-row assistant">
          <div class="chat-bubble assistant thinking">正在思考…</div>
        </div>
      </div>

      <div class="chat-input-bar">
        <el-input
          v-model="inputText"
          placeholder="描述你想看的剧…"
          @keyup.enter.native="send"
        ></el-input>
        <el-button type="primary" :loading="sending" @click="send">发送</el-button>
      </div>
    </div>
  </div>
</template>

<script>
import { chat } from '@/api/agent'

export default {
  name: 'ChatAgent',
  data() {
    return {
      inputText: '',
      sending: false,
      messages: []
    }
  },
  methods: {
    async send() {
      const text = this.inputText.trim()
      if (!text || this.sending) return
      this.inputText = ''
      this.messages.push({ role: 'user', content: text })
      this.sending = true
      this.scrollToBottom()
      try {
        // 只把最近 10 条发给后端，避免上下文过长
        const apiMessages = this.messages.slice(-10).map(m => ({ role: m.role, content: m.content }))
        const res = await chat(apiMessages)
        this.messages.push({
          role: 'assistant',
          content: res.reply || '抱歉，我没想好怎么回答，换个问法试试？',
          recommendations: res.recommendations || []
        })
      } catch (e) {
        this.messages.push({
          role: 'assistant',
          content: e.response?.data?.detail || 'AI 服务暂不可用，请确认 Agent 服务已启动并配置了 API key'
        })
      } finally {
        this.sending = false
        this.scrollToBottom()
      }
    },
    sendSample(text) {
      this.inputText = text
      this.send()
    },
    scrollToBottom() {
      this.$nextTick(() => {
        const el = this.$refs.chatBody
        if (el) el.scrollTop = el.scrollHeight
      })
    }
  }
}
</script>

<style scoped>
.chat-page {
  min-height: calc(100vh - 56px);
  display: flex;
  justify-content: center;
}
.chat-container {
  width: 760px;
  max-width: 96vw;
  display: flex;
  flex-direction: column;
  padding: 24px 0;
}
.chat-header {
  text-align: center;
  margin-bottom: 16px;
}
.chat-title {
  font-size: 20px;
  color: var(--text);
  margin-bottom: 6px;
}
.chat-title i {
  color: var(--brass);
  margin-right: 6px;
}
.chat-subtitle {
  font-size: 13px;
  color: var(--dim);
}
.chat-body {
  flex: 1;
  background: var(--panel);
  border: 1px solid var(--line);
  border-radius: 8px;
  padding: 20px;
  height: 480px;
  overflow-y: auto;
}
.chat-welcome {
  text-align: center;
  color: var(--dim);
  padding-top: 80px;
}
.sample-list {
  margin-top: 16px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
}
.sample {
  padding: 8px 16px;
  border: 1px solid var(--line);
  border-radius: 18px;
  font-size: 13px;
  color: var(--dim);
  cursor: pointer;
  transition: all .2s;
}
.sample:hover {
  border-color: var(--brass);
  color: var(--brass);
}
.chat-row {
  margin-bottom: 16px;
  display: flex;
  flex-direction: column;
}
.chat-row.user {
  align-items: flex-end;
}
.chat-row.assistant {
  align-items: flex-start;
}
.chat-bubble {
  max-width: 78%;
  padding: 10px 14px;
  border-radius: 10px;
  font-size: 14px;
  line-height: 1.7;
  white-space: pre-wrap;
  word-break: break-word;
}
.chat-bubble.user {
  background: var(--brass);
  color: #1A1409;
  border-bottom-right-radius: 2px;
}
.chat-bubble.assistant {
  background: #1C2230;
  border: 1px solid var(--line);
  color: var(--text);
  border-bottom-left-radius: 2px;
}
.chat-bubble.thinking {
  color: var(--dim);
}
.rec-list {
  margin-top: 8px;
  width: 78%;
}
.rec-card {
  display: flex;
  align-items: center;
  background: #1C2230;
  border: 1px solid var(--line);
  border-radius: 8px;
  padding: 10px 14px;
  margin-bottom: 8px;
  cursor: pointer;
  transition: border-color .2s;
}
.rec-card:hover {
  border-color: var(--brass);
}
.rec-name {
  color: var(--brass);
  font-size: 14px;
  font-weight: 600;
  margin-right: 12px;
  flex-shrink: 0;
}
.rec-reason {
  flex: 1;
  font-size: 13px;
  color: var(--dim);
}
.rec-arrow {
  color: var(--brass);
  font-size: 14px;
}
.chat-input-bar {
  display: flex;
  gap: 10px;
  margin-top: 12px;
}
.chat-input-bar .el-input {
  flex: 1;
}
</style>
