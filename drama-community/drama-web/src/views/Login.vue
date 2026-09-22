<template>
  <div class="login-page">
    <div class="form-card">
      <h2>登录</h2>
      <el-form ref="form" :model="form" :rules="rules" label-width="0">
        <el-form-item prop="account">
          <el-input v-model="form.account" placeholder="请输入手机号或邮箱" prefix-icon="el-icon-user"></el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" prefix-icon="el-icon-lock" show-password></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleLogin" style="width:100%">登录</el-button>
        </el-form-item>
      </el-form>
      <p class="tip">还没有账号？<router-link to="/register">立即注册</router-link></p>
    </div>
  </div>
</template>

<script>
import { login } from '@/api/user'

export default {
  name: 'Login',
  data() {
    return {
      loading: false,
      form: {
        account: '',
        password: ''
      },
      rules: {
        account: [{ required: true, message: '请输入手机号或邮箱', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
      }
    }
  },
  methods: {
    handleLogin() {
      this.$refs.form.validate(async valid => {
        if (!valid) return
        this.loading = true
        try {
          const res = await login(this.form)
          this.$store.commit('SET_TOKEN', res.data.token)
          this.$store.commit('SET_USER_INFO', res.data.userInfo)
          this.$message.success('登录成功')
          const redirect = this.$route.query.redirect || '/'
          this.$router.push(redirect)
        } catch (e) {
          this.$message.error(e.response?.data?.message || '登录失败')
        } finally {
          this.loading = false
        }
      })
    }
  }
}
</script>

<style scoped>
.login-page {
  display: flex;
  justify-content: center;
  padding-top: 80px;
}
.form-card {
  width: 380px;
  background: var(--panel);
  border: 1px solid var(--line);
  padding: 40px 36px;
  border-radius: 8px;
}
.form-card h2 {
  text-align: center;
  margin-bottom: 28px;
  font-size: 22px;
  color: var(--text);
}
.tip {
  text-align: center;
  font-size: 14px;
  color: var(--dim);
}
.tip a {
  color: var(--brass);
}
</style>
