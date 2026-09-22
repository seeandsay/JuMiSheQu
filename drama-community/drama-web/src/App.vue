<template>
  <div id="app">
    <nav-bar />
    <router-view />
  </div>
</template>

<script>
import NavBar from '@/components/NavBar.vue'

export default {
  name: 'App',
  components: { NavBar }
}
</script>

<style>
:root {
  --bg: #0F1218;
  --panel: #161B24;
  --line: #262D3A;
  --brass: #D9A441;
  --text: #E6E9EF;
  --dim: #97A0AE;
}

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}
body {
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Microsoft YaHei', sans-serif;
  background-color: var(--bg);
  color: var(--text);
}
#app {
  min-height: 100vh;
}

/* 滚动条 */
::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}
::-webkit-scrollbar-thumb {
  background: var(--line);
  border-radius: 4px;
}
::selection {
  background: rgba(217, 164, 65, .3);
}

/* Element UI 深色适配 */
.el-input__inner {
  background: var(--panel);
  border-color: var(--line);
  color: var(--text);
}
.el-input__inner:hover {
  border-color: #3A4353;
}
.el-input__inner:focus {
  border-color: var(--brass);
}
.el-input__inner::placeholder {
  color: #5C6675;
}
.el-input-group__append {
  background: var(--panel);
  border-color: var(--line);
  color: var(--dim);
}
.el-textarea__inner {
  background: var(--panel);
  border-color: var(--line);
  color: var(--text);
}
.el-textarea__inner:focus {
  border-color: var(--brass);
}
.el-button--primary {
  background: var(--brass);
  border-color: var(--brass);
  color: #1A1409;
}
.el-button--primary:hover,
.el-button--primary:focus {
  background: #E8C77C;
  border-color: #E8C77C;
  color: #1A1409;
}
.el-button--default {
  background: var(--panel);
  border-color: var(--line);
  color: var(--text);
}
.el-button--default:hover {
  border-color: var(--brass);
  color: var(--brass);
  background: var(--panel);
}
.el-rate .el-rate__icon {
  color: #39404E;
}
.el-rate .el-rate__item.is-active .el-rate__icon {
  color: var(--brass);
}
.el-rate__text {
  color: var(--brass);
  font-weight: 600;
  text-shadow: 0 0 8px rgba(217, 164, 65, .45);
}
.el-pagination {
  background: transparent;
}
.el-pagination button,
.el-pagination .el-pager li {
  background: var(--panel);
  color: var(--dim);
}
.el-pagination .el-pager li.active {
  color: var(--brass);
}
.el-form-item__error {
  color: #E06C75;
}
</style>
