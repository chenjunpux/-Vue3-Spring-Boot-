<template>
  <div class="admin-page">
    <el-tabs v-model="activeTab" class="settings-tabs">
      <!-- 基本设置 -->
      <el-tab-pane label="基本设置" name="general">
        <div class="admin-card settings-card">
          <h3>系统基本设置</h3>
          <el-form :model="generalSettings" label-width="120px">
            <el-form-item label="系统名称">
              <el-input v-model="generalSettings.systemName" placeholder="智慧旅游管理系统" />
            </el-form-item>
            <el-form-item label="系统Logo">
              <el-input v-model="generalSettings.logo" placeholder="Logo URL" />
            </el-form-item>
            <el-form-item label="系统描述">
              <el-input v-model="generalSettings.description" type="textarea" :rows="3" placeholder="系统描述" />
            </el-form-item>
            <el-form-item label="联系邮箱">
              <el-input v-model="generalSettings.email" placeholder="admin@example.com" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="saveGeneral">保存设置</el-button>
            </el-form-item>
          </el-form>
        </div>
      </el-tab-pane>

      <!-- 主题设置 -->
      <el-tab-pane label="主题设置" name="theme">
        <div class="admin-card settings-card">
          <h3>界面主题</h3>
          <el-form label-width="120px">
            <el-form-item label="主题模式">
              <el-radio-group v-model="themeSettings.mode">
                <el-radio label="light">亮色模式</el-radio>
                <el-radio label="dark">暗色模式</el-radio>
                <el-radio label="auto">跟随系统</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="主题颜色">
              <div class="color-options">
                <div
                  v-for="color in colorOptions"
                  :key="color.value"
                  class="color-item"
                  :class="{ active: themeSettings.primaryColor === color.value }"
                  :style="{ background: color.value }"
                  @click="themeSettings.primaryColor = color.value"
                >
                  <i v-if="themeSettings.primaryColor === color.value" class="i-mdi-check"></i>
                </div>
              </div>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="saveTheme">保存设置</el-button>
            </el-form-item>
          </el-form>
        </div>
      </el-tab-pane>

      <!-- 安全设置 -->
      <el-tab-pane label="安全设置" name="security">
        <div class="admin-card settings-card">
          <h3>账户安全</h3>
          <el-form label-width="120px">
            <el-form-item label="当前密码">
              <el-input type="password" v-model="securitySettings.currentPassword" placeholder="请输入当前密码" show-password />
            </el-form-item>
            <el-form-item label="新密码">
              <el-input type="password" v-model="securitySettings.newPassword" placeholder="请输入新密码" show-password />
            </el-form-item>
            <el-form-item label="确认密码">
              <el-input type="password" v-model="securitySettings.confirmPassword" placeholder="请确认新密码" show-password />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="updatePassword">修改密码</el-button>
            </el-form-item>
          </el-form>
        </div>

        <div class="admin-card settings-card mt-4">
          <h3>登录日志</h3>
          <el-table :data="loginLogs" stripe size="small">
            <el-table-column prop="time" label="登录时间" width="180" />
            <el-table-column prop="ip" label="IP地址" width="150" />
            <el-table-column prop="location" label="登录地点" />
            <el-table-column prop="device" label="设备" />
          </el-table>
        </div>
      </el-tab-pane>

      <!-- 系统信息 -->
      <el-tab-pane label="系统信息" name="about">
        <div class="admin-card settings-card">
          <h3>关于系统</h3>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="系统名称">智慧旅游管理系统</el-descriptions-item>
            <el-descriptions-item label="版本号">v1.0.0</el-descriptions-item>
            <el-descriptions-item label="技术栈">Vue3 + TypeScript + Element Plus</el-descriptions-item>
            <el-descriptions-item label="前端框架">Vite + UnoCSS</el-descriptions-item>
            <el-descriptions-item label="图表库">ECharts 5</el-descriptions-item>
            <el-descriptions-item label="构建时间">2026-04-07</el-descriptions-item>
          </el-descriptions>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'

const activeTab = ref('general')

const generalSettings = reactive({
  systemName: '智慧旅游管理系统',
  logo: '',
  description: '专业的智慧旅游管理平台',
  email: 'admin@travel.com'
})

const themeSettings = reactive({
  mode: 'light',
  primaryColor: '#3b82f6'
})

const colorOptions = [
  { label: '蓝色', value: '#3b82f6' },
  { label: '青色', value: '#06b6d4' },
  { label: '绿色', value: '#10b981' },
  { label: '紫色', value: '#8b5cf6' },
  { label: '粉色', value: '#ec4899' },
  { label: '橙色', value: '#f59e0b' }
]

const securitySettings = reactive({
  currentPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const loginLogs = ref([
  { time: '2026-04-07 09:30:00', ip: '192.168.1.100', location: '上海市', device: 'Chrome / Windows' },
  { time: '2026-04-06 18:20:00', ip: '192.168.1.101', location: '北京市', device: 'Safari / macOS' }
])

function saveGeneral() {
  ElMessage.success('设置已保存')
}

function saveTheme() {
  localStorage.setItem('admin-theme-mode', themeSettings.mode)
  localStorage.setItem('admin-primary-color', themeSettings.primaryColor)
  ElMessage.success('主题设置已保存')
}

function updatePassword() {
  if (!securitySettings.currentPassword) return ElMessage.warning('请输入当前密码')
  if (!securitySettings.newPassword) return ElMessage.warning('请输入新密码')
  if (securitySettings.newPassword !== securitySettings.confirmPassword) return ElMessage.warning('两次密码不一致')
  ElMessage.success('密码修改成功')
  securitySettings.currentPassword = ''
  securitySettings.newPassword = ''
  securitySettings.confirmPassword = ''
}
</script>

<style scoped lang="scss">
.settings-tabs {
  :deep(.el-tabs__header) {
    background: #fff;
    border-radius: 12px;
    padding: 8px;
    margin-bottom: 20px;
    box-shadow: 0 1px 3px rgba(0,0,0,0.05);
  }

  .dark :deep(.el-tabs__header) {
    background: #1f2937;
  }
}

.settings-card {
  padding: 24px;

  h3 {
    font-size: 16px;
    font-weight: 600;
    margin-bottom: 20px;
    padding-bottom: 12px;
    border-bottom: 1px solid #e5e7eb;
  }

  .dark & h3 {
    border-color: #374151;
  }
}

.color-options {
  display: flex;
  gap: 12px;
}

.color-item {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  transition: transform 0.2s;

  &:hover {
    transform: scale(1.1);
  }

  &.active {
    box-shadow: 0 0 0 3px rgba(0, 0, 0, 0.2);
  }
}
</style>
