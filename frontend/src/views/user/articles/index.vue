<template>
  <div class="articles-page">

    <!-- Hero -->
    <div class="articles-hero" ref="articlesHeroRef">
      <div class="hero-bg-overlay" />
      <div class="hero-content">
        <span class="hero-eyebrow" ref="articlesEyebrowRef">📝 旅行故事</span>
        <h1 class="hero-title-gsap" ref="articlesTitleRef">精选游记</h1>
        <p class="hero-sub" ref="articlesSubRef">来自真实旅行者的第一手体验分享</p>
      </div>
    </div>

    <!-- 搜索 + 分类标签 -->
    <div class="search-filter-bar" ref="categoryBarRef">
      <div class="search-wrap">
        <el-input
          v-model="searchKw"
          placeholder="搜索目的地、游记标题..."
          size="large"
          clearable
          @input="handleSearch"
          style="width: 300px;"
        >
          <template #prefix><span i="ep-search" /></template>
        </el-input>
      </div>
      <div class="category-tags">
        <span
          v-for="cat in categories"
          :key="cat"
          class="cat-tag"
          :class="{ active: activeCategory === cat }"
          @click="activeCategory = cat; currentPage = 1; loadList()"
        >
          {{ cat }}
        </span>
      </div>
    </div>

    <!-- 文章瀑布流网格 -->
    <div class="articles-grid" ref="articlesGridRef">
      <div
        v-for="article in articles"
        :key="article.id"
        class="article-card gsap-article-card"
      >
        <!-- 封面图 -->
        <div class="article-cover">
          <img :src="article.cover" :alt="article.title" />
          <div class="cover-gradient" />
          <div class="cover-meta">
            <span class="article-category">{{ article.category }}</span>
          </div>
        </div>

        <!-- 内容 -->
        <div class="article-body">
          <h3 class="article-title">{{ article.title }}</h3>
          <p class="article-excerpt">{{ article.excerpt }}</p>

          <div class="article-footer">
            <div class="author-info">
              <div class="author-avatar">{{ article.author.slice(0, 1) }}</div>
              <span class="author-name">{{ article.author }}</span>
            </div>
            <div class="article-stats">
              <span class="stat-item">❤️ {{ article.likes }}</span>
              <span class="stat-item">💬 {{ article.comments }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <el-empty v-if="articles.length === 0" description="暂无相关游记" />

    <!-- 分页 -->
    <div v-if="total > pageSize" class="pagination">
      <el-pagination
        v-model:current-page="currentPage"
        :page-size="pageSize"
        :total="total"
        layout="total, prev, pager, next"
        @current-change="loadList"
        background
      />
    </div>

  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import gsap from 'gsap'
import { ScrollTrigger } from 'gsap/ScrollTrigger'
import { splitTextByWord } from '@/composables/useGsapAnimation'
import { getArticleListApi } from '@/api/article'

gsap.registerPlugin(ScrollTrigger)

const articlesHeroRef = ref<HTMLElement | null>(null)
const articlesEyebrowRef = ref<HTMLElement | null>(null)
const articlesTitleRef = ref<HTMLElement | null>(null)
const articlesSubRef = ref<HTMLElement | null>(null)
const categoryBarRef = ref<HTMLElement | null>(null)
const articlesGridRef = ref<HTMLElement | null>(null)
const activeCategory = ref('全部')
const searchKw = ref('')
const currentPage = ref(1)
const total = ref(0)
const pageSize = 16

const categories = ['全部', '攻略', '美食', '摄影', '亲子', '蜜月', '户外', '文化']

// 从后端API获取
const articles = ref<any[]>([])

async function loadList() {
  try {
    const params: any = {
      page: currentPage.value,
      pageSize: pageSize,
      status: 1,
    }
    if (activeCategory.value !== '全部') {
      params.category = activeCategory.value
    }
    if (searchKw.value.trim()) {
      params.keyword = searchKw.value.trim()
    }
    const res: any = await getArticleListApi(params)
    const data = res.data || res
    articles.value = (data.records || []).map((a: any) => ({
      id: a.id,
      title: a.title,
      excerpt: a.content ? a.content.substring(0, 80) + '…' : '',
      author: `用户${a.userId}`,
      category: a.tags ? a.tags.split(',')[0] : '攻略',
      cover: a.coverImage || `https://picsum.photos/seed/article${a.id}/600/400`,
      likes: a.likeCount || 0,
      comments: a.commentCount || 0,
      tags: a.tags || '',
    }))
    total.value = data.total || 0
  } catch (e) {
    console.error('加载游记列表失败', e)
  }
}

let debounceTimer: ReturnType<typeof setTimeout>
function handleSearch() {
  currentPage.value = 1
  clearTimeout(debounceTimer)
  debounceTimer = setTimeout(() => {
    loadList()
  }, 400)
}

function initGsapAnimations() {
  const tl = gsap.timeline({ delay: 0.2 })
  if (articlesEyebrowRef.value) {
    tl.fromTo(articlesEyebrowRef.value,
      { opacity: 0, y: 20, filter: 'blur(8px)' },
      { opacity: 1, y: 0, filter: 'blur(0px)', duration: 0.7, ease: 'power3.out' }
    )
  }
  if (articlesTitleRef.value) {
    const words = splitTextByWord(articlesTitleRef.value)
    tl.fromTo(words,
      { yPercent: 120, opacity: 0, filter: 'blur(6px)' },
      { yPercent: 0, opacity: 1, filter: 'blur(0px)', duration: 1, ease: 'power4.out', stagger: 0.04 },
      '-=0.4'
    )
  }
  if (articlesSubRef.value) {
    tl.fromTo(articlesSubRef.value,
      { opacity: 0, y: 20 },
      { opacity: 1, y: 0, duration: 0.8, ease: 'power3.out' },
      '-=0.5'
    )
  }

  if (categoryBarRef.value) {
    gsap.fromTo(categoryBarRef.value.querySelector('.search-wrap'),
      { opacity: 0, y: 20 },
      { opacity: 1, y: 0, duration: 0.6, ease: 'power3.out', delay: 0.3 }
    )
    gsap.fromTo(categoryBarRef.value.querySelectorAll('.cat-tag'),
      { opacity: 0, y: 20 },
      { opacity: 1, y: 0, duration: 0.6, ease: 'power3.out', stagger: 0.06, delay: 0.4 }
    )
  }

  animateArticlesGrid()
}

function animateArticlesGrid() {
  if (!articlesGridRef.value) return
  const cards = articlesGridRef.value.querySelectorAll<HTMLElement>('.gsap-article-card')
  if (!cards.length) return

  // 瀑布流错位效果
  gsap.fromTo(Array.from(cards),
    { opacity: 0, y: 80, scale: 0.95 },
    {
      opacity: 1, y: 0, scale: 1,
      duration: 0.85, ease: 'power3.out', stagger: 0.1,
      scrollTrigger: { trigger: articlesGridRef.value, start: 'top 82%', once: true },
    }
  )

  // 图片 hover 视差
  cards.forEach((card) => {
    const img = card.querySelector<HTMLElement>('.article-cover img')
    if (!img) return
    card.addEventListener('mousemove', (e) => {
      const rect = card.getBoundingClientRect()
      const x = (e.clientX - rect.left) / rect.width - 0.5
      const y = (e.clientY - rect.top) / rect.height - 0.5
      gsap.to(img, { xPercent: x * 8, yPercent: y * 8, scale: 1.05, duration: 0.5, ease: 'power2.out' })
    })
    card.addEventListener('mouseleave', () => {
      gsap.to(img, { xPercent: 0, yPercent: 0, scale: 1, duration: 0.5, ease: 'power2.out' })
    })
  })
}

onMounted(() => {
  loadList()
  initGsapAnimations()
})


onUnmounted(() => {
  ScrollTrigger.getAll().forEach(t => t.kill())
})
</script>

<style scoped lang="scss">
.articles-page {
  min-height: 100vh;
  background: #f8fafc;

  /* ── Hero ── */
  .articles-hero {
    position: relative;
    height: 340px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: linear-gradient(135deg, #0f0f23 0%, #1e1b4b 50%, #312e81 100%);
    overflow: hidden;

    .hero-bg-overlay {
      position: absolute;
      inset: 0;
      background: url('https://images.unsplash.com/photo-1534430480872-3498386e7856?w=1920&q=60') center/cover no-repeat;
      opacity: 0.2;
    }

    .hero-content {
      position: relative;
      z-index: 2;
      text-align: center;
      padding: 0 24px;
    }

    .hero-eyebrow {
      display: inline-block;
      font-size: 14px;
      color: rgba(255,255,255,0.6);
      letter-spacing: 3px;
      text-transform: uppercase;
      margin-bottom: 16px;
    }

    .hero-title-gsap {
      font-size: clamp(48px, 7vw, 90px);
      font-weight: 900;
      color: white;
      margin-bottom: 16px;
    }

    .hero-sub {
      font-size: 18px;
      color: rgba(255,255,255,0.55);
    }
  }

  /* ── 搜索 + 分类标签 ── */
  .search-filter-bar {
    max-width: 1280px;
    margin: 0 auto;
    padding: 32px 24px 0;
    display: flex;
    flex-direction: column;
    gap: 16px;

    .search-wrap {
      display: flex;
      justify-content: center;
    }

    .category-tags {
      display: flex;
      gap: 12px;
      flex-wrap: wrap;
      justify-content: center;
    }
  }

  /* ── 分类标签 ── */
  .category-tags {
    display: flex;
    gap: 12px;
    flex-wrap: wrap;
    justify-content: center;

    .cat-tag {
      padding: 8px 20px;
      border-radius: 30px;
      font-size: 14px;
      font-weight: 500;
      color: #64748b;
      background: white;
      border: 1.5px solid #e2e8f0;
      cursor: pointer;
      transition: all 0.25s;
      user-select: none;

      &:hover {
        color: #667eea;
        border-color: rgba(102,126,234,0.4);
        background: rgba(102,126,234,0.05);
        transform: translateY(-2px);
      }

      &.active {
        color: white;
        background: linear-gradient(135deg, #667eea, #764ba2);
        border-color: transparent;
        font-weight: 700;
        box-shadow: 0 6px 20px rgba(102,126,234,0.35);
        transform: translateY(-2px);
      }
    }
  }

  /* ── 瀑布流网格 ── */
  .articles-grid {
    max-width: 1280px;
    margin: 24px auto 40px;
    padding: 0 24px;
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 28px;

    @media (max-width: 900px) {
      grid-template-columns: repeat(2, 1fr);
    }

    @media (max-width: 600px) {
      grid-template-columns: 1fr;
    }

    .article-card {
      border-radius: 20px;
      overflow: hidden;
      background: white;
      cursor: pointer;
      box-shadow: 0 4px 20px rgba(0,0,0,0.06);
      transition: box-shadow 0.3s, transform 0.3s;
      will-change: transform, opacity;

      &:hover {
        transform: translateY(-8px);
        box-shadow: 0 24px 70px rgba(102,126,234,0.18);

        .article-cover img { transform: scale(1.08); }
        .article-title { color: #667eea; }
      }

      .article-cover {
        position: relative;
        height: 220px;
        overflow: hidden;

        img {
          width: 100%;
          height: 100%;
          object-fit: cover;
          transition: transform 0.5s;
          will-change: transform;
        }

        .cover-gradient {
          position: absolute;
          bottom: 0;
          left: 0;
          right: 0;
          height: 60%;
          background: linear-gradient(to top, rgba(0,0,0,0.5), transparent);
          pointer-events: none;
        }

        .cover-meta {
          position: absolute;
          top: 12px;
          left: 12px;

          .article-category {
            font-size: 11px;
            font-weight: 700;
            color: white;
            background: rgba(102,126,234,0.85);
            backdrop-filter: blur(8px);
            padding: 4px 12px;
            border-radius: 20px;
            border: 1px solid rgba(255,255,255,0.25);
          }
        }
      }

      .article-body {
        padding: 20px;

        .article-title {
          font-size: 17px;
          font-weight: 700;
          color: #1e293b;
          line-height: 1.4;
          margin-bottom: 10px;
          transition: color 0.2s;
          overflow: hidden;
          display: -webkit-box;
          -webkit-line-clamp: 2;
          -webkit-box-orient: vertical;
        }

        .article-excerpt {
          font-size: 13px;
          color: #94a3b8;
          line-height: 1.7;
          margin-bottom: 16px;
          overflow: hidden;
          display: -webkit-box;
          -webkit-line-clamp: 3;
          -webkit-box-orient: vertical;
        }

        .article-footer {
          display: flex;
          align-items: center;
          justify-content: space-between;
          border-top: 1px solid #f1f5f9;
          padding-top: 14px;

          .author-info {
            display: flex;
            align-items: center;
            gap: 8px;

            .author-avatar {
              width: 28px;
              height: 28px;
              border-radius: 50%;
              background: linear-gradient(135deg, #667eea, #764ba2);
              color: white;
              font-size: 12px;
              font-weight: 700;
              display: flex;
              align-items: center;
              justify-content: center;
            }

            .author-name {
              font-size: 12px;
              color: #64748b;
              font-weight: 500;
            }
          }

          .article-stats {
            display: flex;
            gap: 12px;

            .stat-item {
              font-size: 12px;
              color: #94a3b8;
            }
          }
        }
      }
    }
  }
}

.pagination {
  display: flex;
  justify-content: center;
  padding: 40px 0;
}
</style>
