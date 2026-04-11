<template>
  <div class="admin-page">
    <div role="tablist" class="tabs tabs-lifted mb-4">
      <input type="radio" name="settings_tabs" role="tab" class="tab" aria-label="基本设置"
        :checked="activeTab === 'general'" @click="activeTab = 'general'" />
      <div role="tabpanel" class="tab-content bg-base-100 border-base-200 rounded-box p-6">
        <div v-if="activeTab === 'general'">
          <h3 class="font-semibold text-lg mb-4 pb-2 border-b border-base-300">系统基本设置</h3>
          <div class="space-y-4 max-w-2xl">
            <label class="form-control w-full">
              <div class="label"><span class="label-text font-semibold">系统名称</span></div>
              <input v-model="generalSettings.systemName" class="input input-bordered w-full"
                placeholder="智慧旅游管理系统" />
            </label>
            <label class="form-control w-full">
              <div class="label"><span class="label-text font-semibold">系统Logo</span></div>
              <input v-model="generalSettings.logo" class="input input-bordered w-full" placeholder="Logo URL" />
            </label>
            <label class="form-control w-full">
              <div class="label"><span class="label-text font-semibold">系统描述</span></div>
              <textarea v-model="generalSettings.description" class="textarea textarea-bordered w-full"
                rows="3" placeholder="系统描述"></textarea>
            </label>
            <label class="form-control w-full">
              <div class="label"><span class="label-text font-semibold">联系邮箱</span></div>
              <input v-model="generalSettings.email" class="input input-bordered w-full"
                placeholder="admin@example.com" type="email" />
            </label>
            <button class="btn btn-primary" @click="saveGeneral">保存设置</button>
          </div>
        </div>
      </div>

      <input type="radio" name="settings_tabs" role="tab" class="tab" aria-label="主题设置"
        :checked="activeTab === 'theme'" @click="activeTab = 'theme'" />
      <div role="tabpanel" class="tab-content bg-base-100 border-base-200 rounded-box p-6">
        <div v-if="activeTab === 'theme'">
          <h3 class="font-semibold text-lg mb-4 pb-2 border-b border-base-300">界面主题</h3>
          <div class="space-y-6 max-w-2xl">
            <div>
              <div class="label"><span class="label-text font-semibold">主题模式</span></div>
              <div class="flex gap-4 flex-wrap">
                <label class="label cursor-pointer gap-2">
                  <input v-model="themeSettings.mode" type="radio" value="light" class="radio radio-sm" />
                  <span>亮色模式</span>
                </label>
                <label class="label cursor-pointer gap-2">
                  <input v-model="themeSettings.mode" type="radio" value="dark" class="radio radio-sm" />
                  <span>暗色模式</span>
                </label>
                <label class="label cursor-pointer gap-2">
                  <input v-model="themeSettings.mode" type="radio" value="auto" class="radio radio-sm" />
                  <span>跟随系统</span>
                </label>
              </div>
            </div>
            <div>
              <div class="label"><span class="label-text font-semibold">主题颜色</span></div>
              <div class="flex gap-3 flex-wrap">
                <button v-for="c in colorOptions" :key="c.value"
                  class="btn btn-circle" :style="{ background: c.value, border: themeSettings.primaryColor === c.value ? '3px solid #000' : 'none' }"
                  @click="themeSettings.primaryColor = c.value">
                  <span v-if="themeSettings.primaryColor === c.value" class="text-white">
                    <i class="i-mdi-check text-sm"></i>
                  </span>
                </button>
              </div>
            </div>
            <button class="btn btn-primary" @click="saveTheme">保存设置</button>
          </div>
        </div>
      </div>

      <input type="radio" name="settings_tabs" role="tab" class="tab" aria-label="安全设置"
        :checked="activeTab === 'security'" @click="activeTab = 'security'" />
      <div role="tabpanel" class="tab-content bg-base-100 border-base-200 rounded-box p-6">
        <div v-if="activeTab === 'security'">
          <h3 class="font-semibold text-lg mb-4 pb-2 border-b border-base-300">账户安全</h3>
          <div class="space-y-4 max-w-2xl">
            <label class="form-control w-full">
              <div class="label"><span class="label-text font-semibold">当前密码</span></div>
              <input v-model="securitySettings.currentPassword" type="password" class="input input-bordered w-full"
                placeholder="请输入当前密码" />
            </label>
            <label class="form-control w-full">
              <div class="label"><span class="label-text font-semibold">新密码</span></div>
              <input v-model="securitySettings.newPassword" type="password" class="input input-bordered w-full"
                placeholder="请输入新密码" />
            </label>
            <label class="form-control w-full">
              <div class="label"><span class="label-text font-semibold">确认密码</span></div>
              <input v-model="securitySettings.confirmPassword" type="password" class="input input-bordered w-full"
                placeholder="请确认新密码" />
            </label>
            <button class="btn btn-primary" @click="updatePassword">修改密码</button>
          </div>

          <h3 class="font-semibold text-lg mt-8 mb-4 pb-2 border-b border-base-300">登录日志</h3>
          <div class="overflow-x-auto">
            <table class="table table-zebra w-full text-sm">
              <thead><tr><th>登录时间</th><th>IP地址</th><th>登录地点</th><th>设备</th></tr></thead>
              <tbody>
                <tr v-for="log in loginLogs" :key="log.time">
                  <td>{{ log.time }}</td><td class="font-mono">{{ log.ip }}</td>
                  <td>{{ log.location }}</td><td>{{ log.device }}</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>

      <input type="radio" name="settings_tabs" role="tab" class="tab" aria-label="系统信息"
        :checked="activeTab === 'about'" @click="activeTab = 'about'" />
      <div role="tabpanel" class="tab-content bg-base-100 border-base-200 rounded-box p-6">
        <div v-if="activeTab === 'about'">
          <h3 class="font-semibold text-lg mb-4 pb-2 border-b border-base-300">关于系统</h3>
          <div class="max-w-2xl">
            <table class="table w-full text-sm">
              <tbody>
                <tr><td class="font-semibold w-32">系统名称</td><td>智慧旅游管理系统</td></tr>
                <tr><td class="font-semibold">版本号</td><td>v1.0.0</td></tr>
                <tr><td class="font-semibold">技术栈</td><td>Vue3 + TypeScript + Tailwind + DaisyUI</td></tr>
                <tr><td class="font-semibold">前端框架</td><td>Vite 5</td></tr>
                <tr><td class="font-semibold">图表库</td><td>ECharts 5</td></tr>
                <tr><td class="font-semibold">构建时间</td><td>2026-04-10</td></tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'

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
  currentPassword: '', newPassword: '', confirmPassword: ''
})

const loginLogs = ref([
  { time: '2026-04-07 09:30:00', ip: '192.168.1.100', location: '上海市', device: 'Chrome / Windows' },
  { time: '2026-04-06 18:20:00', ip: '192.168.1.101', location: '北京市', device: 'Safari / macOS' }
])

function saveGeneral() { window.adminToast('设置已保存', 'success') }
function saveTheme() {
  localStorage.setItem('admin-theme-mode', themeSettings.mode)
  localStorage.setItem('admin-primary-color', themeSettings.primaryColor)
  window.adminToast('主题设置已保存', 'success')
}
function updatePassword() {
  if (!securitySettings.currentPassword) { window.adminToast('请输入当前密码', 'error'); return }
  if (!securitySettings.newPassword) { window.adminToast('请输入新密码', 'error'); return }
  if (securitySettings.newPassword !== securitySettings.confirmPassword) { window.adminToast('两次密码不一致', 'error'); return }
  window.adminToast('密码修改成功', 'success')
  securitySettings.currentPassword = ''
  securitySettings.newPassword = ''
  securitySettings.confirmPassword = ''
}
</script>
