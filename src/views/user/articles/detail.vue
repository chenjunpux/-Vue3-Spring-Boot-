<template>
  <div class="article-detail">
    <h2>游记详情</h2>
    <p>ID: {{ $route.params.id }}</p>
    <el-button @click="$router.back()">返回</el-button>
  </div>
</template>

<script setup lang="ts">
</script>
