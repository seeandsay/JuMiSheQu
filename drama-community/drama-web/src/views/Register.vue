<template>
  <div class="register-page">
    <div class="form-card">
      <h2>注册</h2>
      <el-form ref="form" :model="form" :rules="rules" label-width="0">
        <el-form-item prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" prefix-icon="el-icon-mobile-phone"></el-input>
        </el-form-item>
        <el-form-item prop="nickname">
          <el-input v-model="form.nickname" placeholder="请输入昵称" prefix-icon="el-icon-user"></el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="请设置密码（6-20位）" prefix-icon="el-icon-lock" show-password></el-input>
        </el-form-item>
        <el-form-item prop="rePassword">
          <el-input v-model="form.rePassword" type="password" placeholder="请确认密码" prefix-icon="el-icon-lock" show-password></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleRegister" style="width:100%">注册</el-button>
        </el-form-item>
      </el-form>
      <p class="tip">已有账号？<router-link to="/login">去登录</router-link></p>
    </div>
  </div>
</template>

<script>
import { register } from '@/api/user'

export default {
  name: 'Register',
  data() {
    const validateRePassword = (rule, value, callback) => {
      if (value !== this.form.password) {
        callback(new Error('两次输入的密码不一致'))
      } else {
        callback()
      }
    }
    return {
      loading: false,
      form: {
        phone: '',
        nickname: '',
        password: '',
        rePassword: ''
      },
      rules: {
        phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
        nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
        password: [
          { required: true, message: '请设置密码', trigger: 'blur' },
          { min: 6, max: 20, message: '密码长度为6-20位', trigger: 'blur' }
        ],
        rePassword: [
          { required: true, message: '请确认密码', trigger: 'blur' },
          { validator: validateRePassword, trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    handleRegister() {
      this.$refs.form.validate(async valid => {
        if (!valid) return
        this.loading = true
        try {
          await register({
            phone: this.form.phone,
            nickname: this.form.nickname,
            password: this.form.password
          })
          this.$message.success('注册成功，请登录')
          this.$router.push('/login')
        } catch (e) {
          this.$message.error(e.response?.data?.message || '注册失败')
        } finally {
          this.loading = false
        }
      })
    }
  }
}
</script>

<style scoped>
.register-page {
  display: flex;
  justify-content: center;
  padding-top: 60px;
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
