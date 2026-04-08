import { ref } from 'vue'

// 高德地图组合式函数
export function useAmap(containerId: string, options: {
  longitude: number
  latitude: number
  zoom?: number
  title?: string
  markers?: Array<{ lng: number; lat: number; title: string; type?: 'spot' | 'hotel' }>
}) {
  const map = ref<any>(null)

  function initMap() {
    if (typeof window === 'undefined' || !(window as any).AMap) {
      console.warn('AMap 未加载')
      return
    }

    const AMap = (window as any).AMap

    map.value = new AMap.Map(containerId, {
      zoom: options.zoom ?? 15,
      center: [options.longitude, options.latitude],
      viewMode: '2D',
    })

    // 添加标尺和工具条
    map.value.addControl(new AMap.Scale())
    map.value.addControl(new AMap.ToolBar({ position: 'RB' }))

    // 单个标记
    if (options.title && (!options.markers || options.markers.length === 0)) {
      const marker = new AMap.Marker({
        position: [options.longitude, options.latitude],
        title: options.title,
        anchor: 'bottom-center',
      })
      const infoWindow = new AMap.InfoWindow({
        content: `<div style="padding:8px 12px;font-size:13px;"><b>${options.title}</b></div>`,
        offset: new AMap.Pixel(0, -30),
      })
      marker.on('click', () => infoWindow.open(map.value, marker.getPosition()))
      map.value.add(marker)
    }

    // 多个标记（景点/酒店聚合）
    if (options.markers && options.markers.length > 0) {
      const aMarkers = options.markers.map((m) => {
        const color = m.type === 'hotel' ? '#f97316' : '#3b82f6'
        const emoji = m.type === 'hotel' ? '🏨' : '📍'
        const mk = new AMap.Marker({
          position: [m.lng, m.lat],
          title: m.title,
          anchor: 'bottom-center',
          content: `<div style="
            background:${color};
            width:36px;height:36px;
            border-radius:50% 50% 50% 0;
            transform:rotate(-45deg);
            display:flex;align-items:center;justify-content:center;
            box-shadow:0 2px 10px rgba(0,0,0,0.3);
            border:2px solid white;
          ">
            <div style="transform:rotate(45deg);color:white;font-size:12px;font-weight:bold;">
              ${emoji}
            </div>
          </div>`,
        })
        const info = new AMap.InfoWindow({
          content: `<div style="padding:10px 14px;font-size:13px;max-width:200px;"><b>${m.title}</b></div>`,
          offset: new AMap.Pixel(0, -40),
        })
        mk.on('click', () => info.open(map.value, mk.getPosition()))
        return mk
      })
      map.value.add(aMarkers)

      // 自适应视野
      if (aMarkers.length > 1) {
        map.value.setFitView(aMarkers, false, [60, 60, 60, 60])
      }
    }
  }

  return { map, initMap }
}
