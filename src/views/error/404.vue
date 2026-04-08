<template>
  <div class="not-found-page">
    <div class="content">
      <div class="code">404</div>
      <h1>页面不存在</h1>
      <p>抱歉，您访问的页面已走丢到平行世界了~</p>
      <el-button type="primary" size="large" @click="$router.push('/home')">
        返回首页
      </el-button>
    </div>
  </div>
</template>

<style scoped lang="scss">
.not-found-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea, #764ba2);

  .content {
    text-align: center;
    color: white;

    .code {
      font-size: 160px;
      font-weight: 900;
      line-height: 1;
      opacity: 0.3;
      margin-bottom: 24px;
    }

    h1 {
      font-size: 32px;
      font-weight: 700;
      margin-bottom: 12px;
    }

    p {
      font-size: 16px;
      opacity: 0.8;
      margin-bottom: 32px;
    }
  }
}
</style>
