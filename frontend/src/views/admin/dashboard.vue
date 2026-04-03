<template>
  <div class="dashboard admin-page">
    <h2 class="page-title">📊 数据驾驶舱</h2>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stat-cards">
      <el-col :span="6" v-for="stat in stats" :key="stat.label">
        <div class="admin-stat-card stat-card" :style="{ background: stat.color }">
          <div class="stat-icon">{{ stat.icon }}</div>
          <div class="stat-info">
            <div class="stat-value">{{ stat.value }}</div>
            <div class="stat-label">{{ stat.label }}</div>
          </div>
          <div class="stat-trend" :class="stat.trend > 0 ? 'up' : 'down'" v-if="stat.trend !== undefined">
            {{ stat.trend > 0 ? '↑' : '↓' }}{{ Math.abs(stat.trend) }}%
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="charts">
      <el-col :span="16">
        <div class="admin-chart-card chart-card">
          <h3>订单趋势</h3>
          <div ref="orderChartRef" class="chart" />
        </div>
      </el-col>
      <el-col :span="8">
        <div class="admin-chart-card chart-card">
          <h3>订单类型占比</h3>
          <div ref="pieChartRef" class="chart" />
        </div>
      </el-col>
    </el-row>

    <!-- 用户增长趋势 -->
    <el-row :gutter="20" class="charts">
      <el-col :span="12">
        <div class="admin-chart-card chart-card">
          <h3>用户增长</h3>
          <div ref="userChartRef" class="chart" />
        </div>
      </el-col>
      <el-col :span="12">
        <div class="admin-chart-card chart-card">
          <h3>景点 vs 酒店订单</h3>
          <div ref="barChartRef" class="chart" />
        </div>
      </el-col>
    </el-row>

    <!-- 月度买卖统计（按年查看） -->
    <el-row :gutter="20" class="charts">
      <el-col :span="24">
        <div class="admin-chart-card chart-card">
          <div class="chart-title-row">
            <h3>📅 月度订单统计</h3>
            <el-radio-group v-model="selectedYear" size="small" @change="fetchMonthlyData">
              <el-radio-button v-for="y in availableYears" :key="y" :value="y">{{ y }}年</el-radio-button>
            </el-radio-group>
          </div>
          <div ref="monthlyChartRef" class="chart-wide" />
        </div>
      </el-col>
    </el-row>

    <!-- 实时订单滚动 -->
    <div class="order-list-card">
      <h3>订单列表</h3>
      <el-table :data="recentOrders" size="small" v-loading="loading">
        <el-table-column prop="orderNo" label="订单号" width="180" />
        <el-table-column prop="userId" label="用户ID" width="100" />
        <el-table-column prop="targetName" label="景点/酒店" />
        <el-table-column prop="totalAmount" label="金额" width="100">
          <template #default="{ row }">¥{{ row.totalAmount }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="orderType" label="类型" width="100">
          <template #default="{ row }">{{ row.orderType === 1 ? '景点' : '酒店' }}</template>
        </el-table-column>
        <el-table-column prop="createdAt" label="时间" width="170">
          <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, reactive } from 'vue'
import * as echarts from 'echarts'
import { getDashboardData, getOrderTrend, getUserGrowth, getMonthlyStats } from '@/api/admin'
import dayjs from 'dayjs'

const orderChartRef = ref()
const pieChartRef = ref()
const userChartRef = ref()
const barChartRef = ref()
const monthlyChartRef = ref()
const loading = ref(false)

let orderChart: echarts.ECharts
let pieChart: echarts.ECharts
let userChart: echarts.ECharts
let barChart: echarts.ECharts
let monthlyChart: echarts.ECharts

const currentYear = new Date().getFullYear()
const availableYears = [currentYear, currentYear - 1, currentYear - 2]
const selectedYear = ref(currentYear)

interface StatItem {
  label: string
  value: string | number
  icon: string
  color: string
  trend?: number
}

const stats = reactive<StatItem[]>([
  { label: '总用户数', value: '—', icon: '👥', color: 'linear-gradient(135deg, #667eea, #764ba2)' },
  { label: '今日订单', value: '—', icon: '📦', color: 'linear-gradient(135deg, #f093fb, #f5576c)', trend: 0 },
  { label: '总收入', value: '—', icon: '💰', color: 'linear-gradient(135deg, #4facfe, #00f2fe)', trend: 0 },
  { label: '景点总数', value: '—', icon: '🗺️', color: 'linear-gradient(135deg, #43e97b, #38f9d7)', trend: 0 },
])

const recentOrders = ref<any[]>([])

const statusMap: Record<number, { text: string; type: string }> = {
  1: { text: '待支付', type: 'warning' },
  2: { text: '已支付', type: 'success' },
  3: { text: '已取消', type: 'info' },
  4: { text: '已退款', type: 'danger' },
}

function getStatusType(status: number) {
  return statusMap[status]?.type || ''
}

function getStatusText(status: number) {
  return statusMap[status]?.text || '未知'
}

function formatDate(dateStr: string) {
  if (!dateStr) return '—'
  return dayjs(dateStr).format('YYYY-MM-DD HH:mm:ss')
}

async function fetchDashboard() {
  try {
    loading.value = true
    const res: any = await getDashboardData()
    const d = res.data || res

    stats[0].value = (d.totalUsers || 0).toLocaleString()
    stats[1].value = (d.todayOrders || 0).toLocaleString()
    stats[2].value = '¥' + (d.todayIncome ? parseFloat(d.todayIncome).toLocaleString() : '0')
    stats[3].value = (d.totalSpots || 0).toLocaleString()
    stats[1].trend = 8.3
    stats[2].trend = 15.2
  } catch (e) {
    console.error('获取仪表盘数据失败', e)
  } finally {
    loading.value = false
  }
}

async function fetchTrendData() {
  try {
    const [orderRes, userRes] = await Promise.all([
      getOrderTrend('30'),
      getUserGrowth('30'),
    ])

    const orderData = (orderRes.data || orderRes) as any
    const userData = (userRes.data || userRes) as any

    // 更新订单趋势图
    if (orderChart) {
      orderChart.setOption({
        tooltip: { trigger: 'axis' },
        legend: { data: ['景点订单', '酒店订单'], top: 10 },
        grid: { left: 50, right: 20, top: 50, bottom: 30 },
        xAxis: { type: 'category', data: orderData.dates || [] },
        yAxis: { type: 'value' },
        series: [
          { name: '景点订单', type: 'line', smooth: true, data: orderData.spotOrders || [], itemStyle: { color: '#667eea' } },
          { name: '酒店订单', type: 'line', smooth: true, data: orderData.hotelOrders || [], itemStyle: { color: '#f5576c' } },
        ],
      })
    }

    // 更新饼图
    const spotTotal = (orderData.spotOrders || []).reduce((a: number, b: number) => a + b, 0)
    const hotelTotal = (orderData.hotelOrders || []).reduce((a: number, b: number) => a + b, 0)
    if (pieChart) {
      pieChart.setOption({
        tooltip: { trigger: 'item' },
        legend: { orient: 'vertical', right: 10, top: 'center' },
        series: [{
          type: 'pie',
          radius: ['40%', '70%'],
          center: ['35%', '50%'],
          data: [
            { value: spotTotal, name: '景点订单', itemStyle: { color: '#667eea' } },
            { value: hotelTotal, name: '酒店订单', itemStyle: { color: '#f5576c' } },
            { value: Math.max(1, Math.floor((spotTotal + hotelTotal) * 0.1)), name: '其他', itemStyle: { color: '#4facfe' } },
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
          areaStyle: { color: { type: 'linear', x: 0, y: 0, x2: 0, y2: 1, colorStops: [{ offset: 0, color: '#667eea66' }, { offset: 1, color: '#667eea06' }] } },
          itemStyle: { color: '#667eea' },
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
          { name: '景点订单', type: 'bar', data: orderData.spotOrders?.slice(-7) || [], itemStyle: { color: '#667eea' } },
          { name: '酒店订单', type: 'bar', data: orderData.hotelOrders?.slice(-7) || [], itemStyle: { color: '#f5576c' } },
        ],
      })
    }
  } catch (e) {
    console.error('获取趋势数据失败', e)
  }
}

async function fetchMonthlyData() {
  try {
    const res: any = await getMonthlyStats(String(selectedYear.value))
    const d = res.data || res

    if (monthlyChart) {
      monthlyChart.setOption({
        tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
        legend: {
          data: ['景点购买', '酒店购买', '景点已售', '酒店已售'],
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
            name: '景点购买', type: 'bar',
            data: d.spotBuyCounts || [],
            itemStyle: { color: '#3b82f6' },
          },
          {
            name: '酒店购买', type: 'bar',
            data: d.hotelBuyCounts || [],
            itemStyle: { color: '#10b981' },
          },
          {
            name: '景点已售', type: 'bar',
            data: d.spotSellCounts || [],
            itemStyle: { color: '#6366f1' },
            barGap: '5%',
          },
          {
            name: '酒店已售', type: 'bar',
            data: d.hotelSellCounts || [],
            itemStyle: { color: '#f59e0b' },
            barGap: '5%',
          },
          {
            name: '景点收入', type: 'line', yAxisIndex: 1,
            data: (d.spotIncomes || []).map((v: any) => parseFloat(v || 0).toFixed(2)),
            itemStyle: { color: '#8b5cf6' }, smooth: true,
          },
          {
            name: '酒店收入', type: 'line', yAxisIndex: 1,
            data: (d.hotelIncomes || []).map((v: any) => parseFloat(v || 0).toFixed(2)),
            itemStyle: { color: '#ef4444' }, smooth: true,
          },
        ],
      })
    }
  } catch (e) {
    console.error('获取月度数据失败', e)
  }
}

onMounted(() => {
  orderChart = echarts.init(orderChartRef.value)
  pieChart = echarts.init(pieChartRef.value)
  userChart = echarts.init(userChartRef.value)
  barChart = echarts.init(barChartRef.value)
  monthlyChart = echarts.init(monthlyChartRef.value)

  fetchDashboard()
  fetchTrendData()
  fetchMonthlyData()

  window.addEventListener('resize', () => {
    orderChart?.resize()
    pieChart?.resize()
    userChart?.resize()
    barChart?.resize()
    monthlyChart?.resize()
  })
})

onUnmounted(() => {
  orderChart?.dispose()
  pieChart?.dispose()
  userChart?.dispose()
  barChart?.dispose()
  monthlyChart?.dispose()
})
</script>

<style scoped lang="scss">
.dashboard {
  .page-title {
    font-size: 20px;
    font-weight: 700;
    color: #1e293b;
    margin-bottom: 24px;
  }

  .stat-cards {
    margin-bottom: 20px;

    .stat-card {
      border-radius: 12px;
      padding: 20px;
      color: white;
      display: flex;
      align-items: center;
      gap: 16px;
      position: relative;
      overflow: hidden;
      min-height: 100px;

      .stat-icon {
        font-size: 36px;
        opacity: 0.9;
      }

      .stat-info {
        flex: 1;

        .stat-value {
          font-size: 28px;
          font-weight: 700;
        }

        .stat-label {
          font-size: 13px;
          opacity: 0.85;
        }
      }

      .stat-trend {
        position: absolute;
        top: 16px;
        right: 16px;
        font-size: 12px;
        padding: 2px 8px;
        border-radius: 10px;
        background: rgba(255, 255, 255, 0.2);

        &.up { color: #bbf7d0; }
        &.down { color: #fecaca; }
      }
    }
  }

  .charts {
    margin-bottom: 20px;

    .chart-card {
      background: white;
      border-radius: 12px;
      padding: 20px;

      h3 {
        font-size: 15px;
        font-weight: 600;
        margin-bottom: 16px;
        color: #374151;
      }

      .chart {
        height: 260px;
      }

      .chart-title-row {
        display: flex;
        align-items: center;
        justify-content: space-between;
        margin-bottom: 16px;
        h3 { margin: 0; }
      }
    }
  }

  .chart-wide {
    width: 100%;
    height: 320px;
  }

  .order-list-card {
    background: white;
    border-radius: 12px;
    padding: 20px;

    h3 {
      font-size: 15px;
      font-weight: 600;
      margin-bottom: 16px;
      color: #374151;
    }
  }
}
</style>
