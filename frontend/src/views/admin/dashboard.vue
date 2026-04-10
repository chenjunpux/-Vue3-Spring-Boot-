<template>
  <div class="admin-page">

    <!-- ========== 统计卡片（DaisyUI stats） ========== -->
    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 mb-6">
      <div v-for="(stat, k) in statsData" :key="k" class="stats shadow">
        <div class="stat">
          <div :class="`stat-figure text-${stat.colorClass}`">
            <component :is="stat.icon" class="w-8 h-8" />
          </div>
          <div class="stat-title">{{ stat.label }}</div>
          <div :class="`stat-value text-${stat.colorClass}`">{{ stat.value }}</div>
          <div class="stat-desc" :class="stat.trend >= 0 ? 'text-green-500 font-bold' : 'text-red-500 font-bold'">
            {{ stat.trend >= 0 ? '↗' : '↙' }} {{ Math.abs(stat.trend) }}%
          </div>
        </div>
      </div>
    </div>

    <!-- ========== 图表区域 ========== -->
    <!-- 第一行：折线图 + 饼图 -->
    <div class="grid grid-cols-1 lg:grid-cols-3 gap-4 mb-4">
      <!-- 订单趋势 -->
      <div class="bg-base-100 rounded-lg shadow border border-base-300 p-4 lg:col-span-2">
        <div class="flex items-center justify-between mb-4">
          <h3 class="font-semibold">订单趋势</h3>
          <div class="join">
            <button class="join-item btn btn-sm" :class="trendDays === '7' ? 'btn-primary' : 'btn-ghost'" @click="setTrendDays('7')">近7天</button>
            <button class="join-item btn btn-sm" :class="trendDays === '30' ? 'btn-primary' : 'btn-ghost'" @click="setTrendDays('30')">近30天</button>
          </div>
        </div>
        <div ref="orderChartRef" class="h-64"></div>
      </div>

      <!-- 饼图 -->
      <div class="bg-base-100 rounded-lg shadow border border-base-300 p-4">
        <div class="mb-4">
          <h3 class="font-semibold">订单类型占比</h3>
        </div>
        <div ref="pieChartRef" class="h-64"></div>
      </div>
    </div>

    <!-- 第二行：用户增长 + 柱状图 -->
    <div class="grid grid-cols-1 lg:grid-cols-2 gap-4 mb-4">
      <div class="bg-base-100 rounded-lg shadow border border-base-300 p-4">
        <div class="mb-4">
          <h3 class="font-semibold">用户增长</h3>
        </div>
        <div ref="userChartRef" class="h-56"></div>
      </div>

      <div class="bg-base-100 rounded-lg shadow border border-base-300 p-4">
        <div class="mb-4">
          <h3 class="font-semibold">景点 vs 酒店订单</h3>
        </div>
        <div ref="barChartRef" class="h-56"></div>
      </div>
    </div>

    <!-- 月度统计 -->
    <div class="bg-base-100 rounded-lg shadow border border-base-300 p-4 mb-4">
      <div class="flex items-center justify-between mb-4 flex-wrap gap-2">
        <h3 class="font-semibold">月度订单统计</h3>
        <div class="join">
          <button
            v-for="y in availableYears"
            :key="y"
            class="join-item btn btn-sm"
            :class="selectedYear === y ? 'btn-primary' : 'btn-ghost'"
            @click="setYear(y)"
          >{{ y }}年</button>
        </div>
      </div>
      <div ref="monthlyChartRef" class="h-72"></div>
    </div>

    <!-- 最新订单列表 -->
    <div class="bg-base-100 rounded-lg shadow border border-base-300 overflow-hidden">
      <div class="flex items-center justify-between p-4 border-b border-base-300">
        <h3 class="font-semibold">最新订单</h3>
        <router-link to="/admin/orders" class="btn btn-primary btn-sm">
          查看更多
        </router-link>
      </div>

      <!-- 加载状态 -->
      <div v-if="loading" class="flex items-center justify-center p-8">
        <span class="loading loading-spinner loading-lg text-primary"></span>
      </div>

      <!-- 表格 -->
      <div v-else class="overflow-x-auto">
        <table class="table table-zebra w-full text-sm">
          <thead>
            <tr>
              <th>订单号</th>
              <th>用户ID</th>
              <th>景点/酒店</th>
              <th>金额</th>
              <th>状态</th>
              <th>类型</th>
              <th>下单时间</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="row in recentOrders" :key="row.orderNo">
              <td class="font-mono text-xs">{{ row.orderNo }}</td>
              <td>{{ row.userId }}</td>
              <td>{{ row.targetName }}</td>
              <td class="text-green-600 font-medium">¥{{ row.totalAmount }}</td>
              <td>
                <span :class="getStatusBadgeClass(row.status)" class="badge badge-sm">
                  {{ getStatusText(row.status) }}
                </span>
              </td>
              <td>{{ row.orderType === 1 ? '景点' : '酒店' }}</td>
              <td>{{ formatDate(row.createdAt) }}</td>
            </tr>
            <tr v-if="recentOrders.length === 0">
              <td colspan="7" class="text-center py-8 text-neutral/50">暂无数据</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'
import { getDashboardData, getOrderTrend, getUserGrowth, getMonthlyStats } from '@/api/admin'
import dayjs from 'dayjs'

// Heroicons
import {
  UserGroupIcon,
  ShoppingCartIcon,
  CurrencyDollarIcon,
  MapPinIcon,
} from '@heroicons/vue/24/outline'

// 图表 refs
const orderChartRef = ref<HTMLElement>()
const pieChartRef = ref<HTMLElement>()
const userChartRef = ref<HTMLElement>()
const barChartRef = ref<HTMLElement>()
const monthlyChartRef = ref<HTMLElement>()

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
    icon: UserGroupIcon,
    colorClass: 'primary',
    trend: 12.5,
  },
  {
    label: '今日订单',
    value: '—',
    icon: ShoppingCartIcon,
    colorClass: 'success',
    trend: 8.3,
  },
  {
    label: '总收入',
    value: '—',
    icon: CurrencyDollarIcon,
    colorClass: 'warning',
    trend: 15.2,
  },
  {
    label: '景点总数',
    value: '—',
    icon: MapPinIcon,
    colorClass: 'secondary',
    trend: 5.7,
  },
])

// 状态映射
const statusMap: Record<number, { text: string; cls: string }> = {
  1: { text: '待支付', cls: 'badge-warning' },
  2: { text: '已支付', cls: 'badge-success' },
  3: { text: '已取消', cls: 'badge-neutral' },
  4: { text: '已退款', cls: 'badge-error' },
}

function getStatusBadgeClass(status: number) {
  return statusMap[status]?.cls || 'badge-neutral'
}

function getStatusText(status: number) {
  return statusMap[status]?.text || '未知'
}

function formatDate(dateStr: string) {
  if (!dateStr) return '—'
  return dayjs(dateStr).format('YYYY-MM-DD HH:mm')
}

function setTrendDays(days: string) {
  trendDays.value = days
  fetchTrendData()
}

function setYear(year: number) {
  selectedYear.value = year
  fetchMonthlyData()
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
  if (orderChartRef.value) orderChart = echarts.init(orderChartRef.value)
  if (pieChartRef.value) pieChart = echarts.init(pieChartRef.value)
  if (userChartRef.value) userChart = echarts.init(userChartRef.value)
  if (barChartRef.value) barChart = echarts.init(barChartRef.value)
  if (monthlyChartRef.value) monthlyChart = echarts.init(monthlyChartRef.value)
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
