<template>
  <router-view />
</template>

<script setup>
</script>

<style>
/* ── 全局基础样式 ── */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html, body, #app {
  height: 100%;
  font-family: -apple-system, BlinkMacSystemFont, 'PingFang SC', 'Microsoft YaHei', 'Helvetica Neue', Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  background: #f0f2f5;
  color: #1d2b3a;
}

/* ── 自定义滚动条 ── */
::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}
::-webkit-scrollbar-track {
  background: transparent;
}
::-webkit-scrollbar-thumb {
  background: rgba(0, 0, 0, 0.12);
  border-radius: 3px;
}
::-webkit-scrollbar-thumb:hover {
  background: rgba(0, 0, 0, 0.25);
}

/* ── 文本选中色 ── */
::selection {
  background: rgba(64, 158, 255, 0.25);
}

/* ── 全局过渡 ── */
a {
  transition: color 0.2s;
}

/* ── Element Plus 覆盖 ── */
.el-card {
  border-radius: 12px !important;
  border: none !important;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06) !important;
  transition: box-shadow 0.25s;
}
.el-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1) !important;
}

.el-table {
  border-radius: 8px;
  overflow: hidden;
}

/* 表格行悬停 */
.el-table__row:hover td {
  background: #f8faff !important;
}

/* 表单标签 */
.el-form-item__label {
  font-weight: 500;
  color: #4a5568;
}

/* 对话框美化 */
.el-dialog {
  border-radius: 16px !important;
  overflow: hidden;
}
.el-dialog__header {
  border-bottom: 1px solid #f0f0f0;
  padding: 20px 24px 16px;
}
.el-dialog__body {
  padding: 24px;
}
.el-dialog__footer {
  border-top: 1px solid #f0f0f0;
  padding: 12px 24px 20px;
}
</style>
