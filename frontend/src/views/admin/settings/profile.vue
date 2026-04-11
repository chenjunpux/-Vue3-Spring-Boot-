<template>
  <div class="max-w-2xl">
    <div class="bg-base-100 rounded-lg shadow border border-base-300 p-6">
      <h3 class="font-semibold text-lg mb-6 pb-3 border-b border-base-300">个人信息</h3>

      <!-- 头像 -->
      <div class="flex items-center gap-6 mb-8">
        <div class="avatar">
          <div class="w-24 rounded-full">
            <img :src="avatarUrl || defaultAvatar" :alt="profile.nickname" />
          </div>
        </div>
        <div>
          <p class="text-sm text-neutral/60 mb-2">点击头像可更换（功能开发中）</p>
          <button class="btn btn-sm btn-outline">上传新头像</button>
        </div>
      </div>

      <div class="space-y-5">
        <!-- 用户名 -->
        <label class="form-control w-full">
          <div class="label"><span class="label-text font-semibold">用户名</span></div>
          <input v-model="profile.username" class="input input-bordered w-full" placeholder="用户名" />
        </label>

        <!-- 昵称 -->
        <label class="form-control w-full">
          <div class="label"><span class="label-text font-semibold">昵称</span></div>
          <input v-model="profile.nickname" class="input input-bordered w-full" placeholder="显示名称" />
        </label>

        <!-- 邮箱 -->
        <label class="form-control w-full">
          <div class="label"><span class="label-text font-semibold">邮箱</span></div>
          <input v-model="profile.email" class="input input-bordered w-full" type="email" placeholder="admin@example.com" />
        </label>

        <!-- 手机号 -->
        <label class="form-control w-full">
          <div class="label"><span class="label-text font-semibold">手机号</span></div>
          <input v-model="profile.phone" class="input input-bordered w-full" placeholder="手机号码" />
        </label>

        <!-- 角色 -->
        <label class="form-control w-full">
          <div class="label"><span class="label-text font-semibold">角色</span></div>
          <input :value="roleText" class="input input-bordered w-full" disabled />
          <div class="label"><span class="label-text-alt text-neutral/40">管理员角色不可修改</span></div>
        </label>

        <!-- 个人简介 -->
        <label class="form-control w-full">
          <div class="label"><span class="label-text font-semibold">个人简介</span></div>
          <textarea v-model="profile.bio" class="textarea textarea-bordered w-full" rows="3"
            placeholder="简单介绍一下自己..."></textarea>
        </label>
      </div>

      <div class="mt-6 pt-4 border-t border-base-300 flex gap-3">
        <button class="btn btn-primary" :class="{ 'btn-disabled': saving }" @click="handleSave">
          <span v-if="saving" class="loading loading-spinner loading-sm"></span>
          {{ saving ? '保存中...' : '保存修改' }}
        </button>
        <button class="btn btn-ghost" @click="handleReset">重置</button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()
const saving = ref(false)
const defaultAvatar = 'https://placehold.co/200x200/3b82f6/ffffff?text=Admin'

const profile = reactive({
  username: '',
  nickname: '',
  email: '',
  phone: '',
  bio: '',
})

const avatarUrl = computed(() => userStore.userInfo?.avatar || '')
const roleText = computed(() => {
  const role = userStore.userInfo?.role
  if (role === 2) return '超级管理员'
  if (role === 1) return '普通管理员'
  return '用户'
})

function handleReset() {
  profile.username = userStore.userInfo?.username || ''
  profile.nickname = userStore.userInfo?.nickname || ''
  profile.email = userStore.userInfo?.email || ''
  profile.phone = userStore.userInfo?.phone || ''
  profile.bio = userStore.userInfo?.bio || ''
}

async function handleSave() {
  if (!profile.nickname) {
    ElMessage.warning('请填写昵称')
    return
  }
  try {
    saving.value = true
    await new Promise(resolve => setTimeout(resolve, 500))
    // TODO: 调用更新个人信息的 API
    ElMessage.success('个人信息已保存')
  } catch {
    ElMessage.error('保存失败')
  } finally {
    saving.value = false
  }
}

onMounted(() => {
  handleReset()
})
</script>
