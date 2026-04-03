/// <reference types="vite/client" />

declare module '*.vue' {
  import type { DefineComponent } from 'vue'
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  const component: DefineComponent<object, object, any>
  export default component
}

declare module 'lottie-web'

declare module '*.lottie' {
  const content: any
  export default content
}

interface ImportMetaEnv {
  readonly VITE_APP_TITLE: string
  readonly VITE_API_BASE_URL: string
  readonly VITE_APP_AMAP_KEY: string
  readonly VITE_APP_AMAP_SECURITY_KEY: string
}

interface ImportMeta {
  readonly env: ImportMetaEnv
}
