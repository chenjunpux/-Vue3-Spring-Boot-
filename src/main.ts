import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

import App from './App.vue'
import router from './router'
import './styles/index.css'
import './styles/admin.scss'

const app = createApp(App)

app.use(createPinia())
app.use(router)
app.use(ElementPlus)

// 点击外部关闭指令
app.directive('click-outside', {
  mounted(el, binding) {
    el._clickOutside = (e: MouseEvent) => {
      if (!el.contains(e.target as Node)) binding.value(e)
    }
    document.addEventListener('click', el._clickOutside)
  },
  unmounted(el) {
    document.removeEventListener('click', el._clickOutside)
  },
})

app.mount('#app')
