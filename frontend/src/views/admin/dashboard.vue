<template>
  <div class="admin-page admin-dashboard">
    <!-- 统计卡片 -->
    <div class="stats-grid">
      <div v-for="(stat, index) in statsData" :key="index" class="stat-card" :style="{ '--accent': stat.color }">
        <div class="stat-icon" :style="{ background: stat.bgColor }">
          <i :class="stat.icon"></i>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ stat.value }}</div>
          <div class="stat-label">{{ stat.label }}</div>
          <div class="stat-trend" :class="stat.trend >= 0 ? 'up' : 'down'">
            <i :class="stat.trend >= 0 ? 'i-mdi-arrow-up' : 'i-mdi-arrow-down'"></i>
            {{ Math.abs(stat.trend) }}%
          </div>
        </div>
      </div>
    </div>

    <!-- 图表区域 -->
    <div class="charts-grid">
      <div class="chart-card chart-large">
        <div class="chart-header">
          <h3>订单趋势</h3>
          <div class="chart-actions">
            <el-radio-group v-model="trendDays" size="small" @change="fetchTrendData">
              <el-radio-button label="7">近7天</el-radio-button>
              <el-radio-button label="30">近30天</el-radio-button>
            </el-radio-group>
          </div>
        </div>
        <div ref="orderChartRef" class="chart-container"></div>
      </div>

      <div class="chart-card chart-small">
        <div class="chart-header">
          <h3>订单类型占比</h3>
        </div>
        <div ref="pieChartRef" class="chart-container"></div>
      </div>
    </div>

    <!-- 第二行图表 -->
    <div class="charts-grid">
      <div class="chart-card">
        <div class="chart-header">
          <h3>用户增长</h3>
        </div>
        <div ref="userChartRef" class="chart-container"></div>
      </div>

      <div class="chart-card">
        <div class="chart-header">
          <h3>景点 vs 酒店订单</h3>
        </div>
        <div ref="barChartRef" class="chart-container"></div>
      </div>
    </div>

    <!-- 月度统计 -->
    <div class="chart-card chart-full">
      <div class="chart-header">
        <h3>月度订单统计</h3>
        <div class="chart-actions">
          <el-radio-group v-model="selectedYear" size="small" @change="fetchMonthlyData">
            <el-radio-button v-for="y in availableYears" :key="y" :label="y">{{ y }}年</el-radio-button>
          </el-radio-group>
        </div>
      </div>
      <div ref="monthlyChartRef" class="chart-container chart-tall"></div>
    </div>

    <!-- 订单列表 -->
    <div class="order-list-card">
      <div class="card-header">
        <h3>最新订单</h3>
        <el-button type="primary" text @click="$router.push('/admin/orders')">
          查看更多 <i class="i-mdi-arrow-right ml-1"></i>
        </el-button>
      </div>
      <el-table :data="recentOrders" v-loading="loading" stripe>
        <el-table-column prop="orderNo" label="订单号" width="180" />
        <el-table-column prop="userId" label="用户ID" width="100" />
        <el-table-column prop="targetName" label="景点/酒店" min-width="150" />
        <el-table-column prop="totalAmount" label="金额" width="100">
          <template #default="{ row }">
            <span class="text-green-600 font-medium">¥{{ row.totalAmount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="orderType" label="类型" width="80">
          <template #default="{ row }">
            <span>{{ row.orderType === 1 ? '景点' : '酒店' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="下单时间" width="170">
          <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'
import { getDashboardData, getOrderTrend, getUserGrowth, getMonthlyStats } from '@/api/admin'
import dayjs from 'dayjs'

// 图表 refs
const orderChartRef = ref()
const pieChartRef = ref()
const userChartRef = ref()
const barChartRef = ref()
const monthlyChartRef = ref()

// 图表实例
let orderChart: echarts.ECharts
let pieChart: echarts.ECharts
let userChart: echarts.ECharts
let barChart: echarts.ECharts
let monthlyChart: echarts.ECharts

// 状态
const loading = ref(false)
const trendDays = ref('7')
const currentYear = new Date().getFullYear()
const availableYears = [currentYear, currentYear - 1, currentYear - 2]
const selectedYear = ref(currentYear)
const recentOrders = ref<any[]>([])

// 统计数据
const statsData = reactive([
  {
    label: '总用户数',
    value: '—',
    icon: 'i-mdi-account',
    color: '#3b82f6',
    bgColor: 'rgba(59, 130, 246, 0.1)',
    trend: 12.5
  },
  {
    label: '今日订单',
    value: '—',
    icon: 'i-mdi-cart',
    color: '#10b981',
    bgColor: 'rgba(16, 185, 129, 0.1)',
    trend: 8.3
  },
  {
    label: '总收入',
    value: '—',
    icon: 'i-mdi-cash-register',
    color: '#f59e0b',
    bgColor: 'rgba(245, 158, 11, 0.1)',
    trend: 15.2
  },
  {
    label: '景点总数',
    value: '—',
    icon: 'i-mdi-map-marker',
    color: '#8b5cf6',
    bgColor: 'rgba(139, 92, 246, 0.1)',
    trend: 5.7
  }
])

// 状态映射
const statusMap: Record<number, { text: string; type: string }> = {
  1: { text: '待支付', type: 'warning' },
  2: { text: '已支付', type: 'success' },
  3: { text: '已取消', type: 'info' },
  4: { text: '已退款', type: 'danger' },
}

function getStatusType(status: number) {
  return statusMap[status]?.type || 'info'
}

function getStatusText(status: number) {
  return statusMap[status]?.text || '未知'
}

function formatDate(dateStr: string) {
  if (!dateStr) return '—'
  return dayjs(dateStr).format('YYYY-MM-DD HH:mm')
}

// 获取仪表盘数据
async function fetchDashboard() {
  try {
    loading.value = true
    const res: any = await getDashboardData()
    const d = res.data || res

    statsData[0].value = (d.totalUsers || 0).toLocaleString()
    statsData[1].value = (d.todayOrders || 0).toLocaleString()
    statsData[2].value = '¥' + ((d.todayIncome ? parseFloat(d.todayIncome) : 0) / 100).toLocaleString()
    statsData[3].value = (d.totalSpots || 0).toLocaleString()

    if (d.recentOrders) {
      recentOrders.value = d.recentOrders.slice(0, 10)
    }
  } catch (e) {
    console.error('获取仪表盘数据失败', e)
  } finally {
    loading.value = false
  }
}

// 获取趋势数据
async function fetchTrendData() {
  try {
    const [orderRes, userRes] = await Promise.all([
      getOrderTrend(trendDays.value),
      getUserGrowth(trendDays.value),
    ])

    const orderData = (orderRes.data || orderRes) as any
    const userData = (userRes.data || userRes) as any

    // 订单趋势图
    if (orderChart) {
      orderChart.setOption({
        tooltip: { trigger: 'axis' },
        legend: { data: ['景点订单', '酒店订单'], top: 10 },
        grid: { left: 50, right: 20, top: 50, bottom: 30 },
        xAxis: { type: 'category', data: orderData.dates || [] },
        yAxis: { type: 'value' },
        series: [
          {
            name: '景点订单',
            type: 'line',
            smooth: true,
            data: orderData.spotOrders || [],
            itemStyle: { color: '#3b82f6' },
            areaStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: 'rgba(59, 130, 246, 0.3)' },
                { offset: 1, color: 'rgba(59, 130, 246, 0.05)' }
              ])
            }
          },
          {
            name: '酒店订单',
            type: 'line',
            smooth: true,
            data: orderData.hotelOrders || [],
            itemStyle: { color: '#10b981' },
            areaStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: 'rgba(16, 185, 129, 0.3)' },
                { offset: 1, color: 'rgba(16, 185, 129, 0.05)' }
              ])
            }
          },
        ],
      })
    }

    // 饼图
    const spotTotal = (orderData.spotOrders || []).reduce((a: number, b: number) => a + b, 0)
    const hotelTotal = (orderData.hotelOrders || []).reduce((a: number, b: number) => a + b, 0)
    if (pieChart) {
      pieChart.setOption({
        tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
        legend: { orient: 'vertical', right: 10, top: 'center' },
        series: [{
          type: 'pie',
          radius: ['40%', '70%'],
          center: ['35%', '50%'],
          avoidLabelOverlap: false,
          itemStyle: { borderRadius: 8, borderColor: '#fff', borderWidth: 2 },
          label: { show: false },
          emphasis: {
            label: { show: true, fontSize: 14, fontWeight: 'bold' }
          },
          data: [
            { value: spotTotal, name: '景点订单', itemStyle: { color: '#3b82f6' } },
            { value: hotelTotal, name: '酒店订单', itemStyle: { color: '#10b981' } },
          ],
        }],
      })
    }

    // 用户增长图
    if (userChart) {
      userChart.setOption({
        tooltip: { trigger: 'axis' },
        grid: { left: 50, right: 20, top: 20, bottom: 30 },
        xAxis: { type: 'category', data: userData.dates || [] },
        yAxis: { type: 'value' },
        series: [{
          type: 'line',
          smooth: true,
          data: userData.counts || [],
          itemStyle: { color: '#8b5cf6' },
          areaStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: 'rgba(139, 92, 246, 0.3)' },
              { offset: 1, color: 'rgba(139, 92, 246, 0.05)' }
            ])
          }
        }],
      })
    }

    // 柱状图
    if (barChart) {
      barChart.setOption({
        tooltip: { trigger: 'axis' },
        legend: { data: ['景点订单', '酒店订单'], top: 10 },
        grid: { left: 50, right: 20, top: 50, bottom: 30 },
        xAxis: { type: 'category', data: orderData.dates?.slice(-7) || [] },
        yAxis: { type: 'value' },
        series: [
          {
            name: '景点订单',
            type: 'bar',
            data: orderData.spotOrders?.slice(-7) || [],
            itemStyle: { color: '#3b82f6', borderRadius: [4, 4, 0, 0] }
          },
          {
            name: '酒店订单',
            type: 'bar',
            data: orderData.hotelOrders?.slice(-7) || [],
            itemStyle: { color: '#10b981', borderRadius: [4, 4, 0, 0] }
          },
        ],
      })
    }
  } catch (e) {
    console.error('获取趋势数据失败', e)
  }
}

// 获取月度数据
async function fetchMonthlyData() {
  try {
    const res: any = await getMonthlyStats(String(selectedYear.value))
    const d = res.data || res

    if (monthlyChart) {
      monthlyChart.setOption({
        tooltip: {
          trigger: 'axis',
          axisPointer: { type: 'shadow' }
        },
        legend: {
          data: ['景点订单', '酒店订单', '景点收入', '酒店收入'],
          top: 35,
        },
        grid: { left: 60, right: 30, top: 75, bottom: 40 },
        xAxis: { type: 'category', data: d.months || [] },
        yAxis: [
          { type: 'value', name: '订单数', position: 'left' },
          { type: 'value', name: '收入(元)', position: 'right', splitLine: { show: false } },
        ],
        series: [
          {
            name: '景点订单',
            type: 'bar',
            data: d.spotBuyCounts || [],
            itemStyle: { color: '#3b82f6', borderRadius: [4, 4, 0, 0] },
          },
          {
            name: '酒店订单',
            type: 'bar',
            data: d.hotelBuyCounts || [],
            itemStyle: { color: '#10b981', borderRadius: [4, 4, 0, 0] },
          },
          {
            name: '景点收入',
            type: 'line',
            yAxisIndex: 1,
            data: (d.spotIncomes || []).map((v: any) => parseFloat(v || 0).toFixed(2)),
            itemStyle: { color: '#8b5cf6' },
            smooth: true,
          },
          {
            name: '酒店收入',
            type: 'line',
            yAxisIndex: 1,
            data: (d.hotelIncomes || []).map((v: any) => parseFloat(v || 0).toFixed(2)),
            itemStyle: { color: '#f59e0b' },
            smooth: true,
          },
        ],
      })
    }
  } catch (e) {
    console.error('获取月度数据失败', e)
  }
}

// 初始化图表
function initCharts() {
  orderChart = echarts.init(orderChartRef.value)
  pieChart = echarts.init(pieChartRef.value)
  userChart = echarts.init(userChartRef.value)
  barChart = echarts.init(barChartRef.value)
  monthlyChart = echarts.init(monthlyChartRef.value)

  // 响应式
  window.addEventListener('resize', handleResize)
}

function handleResize() {
  orderChart?.resize()
  pieChart?.resize()
  userChart?.resize()
  barChart?.resize()
  monthlyChart?.resize()
}

onMounted(() => {
  initCharts()
  fetchDashboard()
  fetchTrendData()
  fetchMonthlyData()
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  orderChart?.dispose()
  pieChart?.dispose()
  userChart?.dispose()
  barChart?.dispose()
  monthlyChart?.dispose()
})
</script>

<style scoped lang="scss">
.admin-dashboard {
  max-width: 1400px;
  margin: 0 auto;
}

// 统计卡片网格
.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 24px;

  @media (max-width: 1200px) {
    grid-template-columns: repeat(2, 1fr);
  }

  @media (max-width: 640px) {
    grid-template-columns: 1fr;
  }
}

.stat-card {
  background: #ffffff;
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: flex-start;
  gap: 16px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  border: 1px solid #e5e7eb;
  transition: all 0.3s;

  &:hover {
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    transform: translateY(-2px);
  }
}

:global(.dark) .stat-card {
  background: #1f2937;
  border-color: #374151;
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: var(--accent);
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #111827;
  line-height: 1.2;
}

:global(.dark) .stat-value {
  color: #f9fafb;
}

.stat-label {
  font-size: 14px;
  color: #6b7280;
  margin-top: 4px;
}

:global(.dark) .stat-label {
  color: #9ca3af;
}

.stat-trend {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  font-weight: 500;
  margin-top: 8px;
  padding: 2px 8px;
  border-radius: 4px;

  &.up {
    color: #10b981;
    background: rgba(16, 185, 129, 0.1);
  }

  &.down {
    color: #ef4444;
    background: rgba(239, 68, 68, 0.1);
  }
}

// 图表网格
.charts-grid {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 20px;
  margin-bottom: 24px;

  @media (max-width: 1024px) {
    grid-template-columns: 1fr;
  }
}

.chart-card {
  background: #ffffff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  border: 1px solid #e5e7eb;
}

:global(.dark) .chart-card {
  background: #1f2937;
  border-color: #374151;
}

.chart-full {
  margin-bottom: 24px;
}

.chart-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;

  h3 {
    font-size: 16px;
    font-weight: 600;
    color: #111827;
  }

  :global(.dark) & h3 {
    color: #f9fafb;
  }
}

.chart-container {
  height: 280px;
}

.chart-tall {
  height: 320px;
}

// 订单列表卡片
.order-list-card {
  background: #ffffff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  border: 1px solid #e5e7eb;
}

:global(.dark) .order-list-card {
  background: #1f2937;
  border-color: #374151;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;

  h3 {
    font-size: 16px;
    font-weight: 600;
    color: #111827;
  }

  :global(.dark) & h3 {
    color: #f9fafb;
  }
}
</style>
