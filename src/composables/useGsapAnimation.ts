/**
 * useGsapAnimation.ts
 * GSAP + ScrollTrigger 动画 composable
 * 封装常见的滚动驱动动画：文字错位、滑入、缩放、数字跳动、视差等
 */
import { onMounted, onUnmounted } from 'vue'
import gsap from 'gsap'
import { ScrollTrigger } from 'gsap/ScrollTrigger'

gsap.registerPlugin(ScrollTrigger)

// ─── 文字拆分工具 ───────────────────────────────────────────────────────────

/**
 * 将元素的文字内容按 <span class="word">word</span> 逐词拆分，
 * 配合 gsap.from(.char, { opacity: 0, yPercent: 100, stagger: 0.05 }) 做 reveal 动画
 */
export function splitTextByWord(el: HTMLElement): HTMLElement[] {
  const original = el.textContent || ''
  el.innerHTML = original
    .trim()
    .split(/\s+/)
    .map(w => `<span class="word-wrap" style="display:inline-block;overflow:hidden;"><span class="word" style="display:inline-block;">${w}</span></span>`)
    .join(' ')
  return Array.from(el.querySelectorAll<HTMLElement>('.word'))
}

/**
 * 将元素的文字内容按字符拆分，配合动画
 */
export function splitTextByChar(el: HTMLElement): HTMLElement[] {
  const original = el.textContent || ''
  el.innerHTML = original
    .trim()
    .split('')
    .map(c => `<span class="char-wrap" style="display:inline-block;overflow:hidden;"><span class="char" style="display:inline-block;">${c === ' ' ? '&nbsp;' : c}</span></span>`)
    .join('')
  return Array.from(el.querySelectorAll<HTMLElement>('.char'))
}

// ─── 动画预设工厂 ─────────────────────────────────────────────────────────────

export function useGsapAnimation() {
  const ctx = gsap.context(() => {})

  /** 文字逐词 reveal（Velvet Pour 核心效果） */
  function revealText(el: HTMLElement | null, options?: { delay?: number; stagger?: number; y?: number }) {
    if (!el) return
    const chars = splitTextByWord(el)
    gsap.from(chars, {
      yPercent: 120,
      opacity: 0,
      duration: 0.8,
      ease: 'power3.out',
      stagger: options?.stagger ?? 0.06,
      delay: options?.delay ?? 0,
    })
  }

  /** 卡片/元素从底部依次弹入 */
  function staggerSlideUp(els: HTMLElement[] | NodeListOf<Element>, options?: { delay?: number; stagger?: number; y?: number }) {
    if (!els || els.length === 0) return
    gsap.from(Array.from(els), {
      opacity: 0,
      y: options?.y ?? 80,
      duration: 0.9,
      ease: 'power3.out',
      stagger: options?.stagger ?? 0.12,
      delay: options?.delay ?? 0,
      scrollTrigger: {
        trigger: els[0] instanceof HTMLElement ? els[0] : undefined,
        start: 'top 88%',
        once: true,
      },
    })
  }

  /** 视差滚动效果 */
  function parallax(el: HTMLElement | null, speed: number = 0.4) {
    if (!el) return
    gsap.to(el, {
      yPercent: speed * 100,
      ease: 'none',
      scrollTrigger: {
        trigger: el,
        start: 'top bottom',
        end: 'bottom top',
        scrub: true,
      },
    })
  }

  /** 数字跳动计数动画 */
  function countUp(el: HTMLElement | null, target: number, suffix: string = '') {
    if (!el) return
    const obj = { val: 0 }
    gsap.to(obj, {
      val: target,
      duration: 2,
      ease: 'power2.out',
      onUpdate() {
        el.textContent = Math.round(obj.val).toLocaleString() + suffix
      },
      scrollTrigger: {
        trigger: el,
        start: 'top 85%',
        once: true,
      },
    })
  }

  /** clip-path reveal（从底部向上撑开） */
  function clipReveal(el: HTMLElement | null, options?: { delay?: number; duration?: number }) {
    if (!el) return
    gsap.from(el, {
      clipPath: 'inset(100% 0 0 0)',
      duration: options?.duration ?? 1.2,
      ease: 'power4.out',
      delay: options?.delay ?? 0,
      scrollTrigger: {
        trigger: el,
        start: 'top 85%',
        once: true,
      },
    })
  }

  /** scale + fade reveal */
  function scaleReveal(el: HTMLElement | null, options?: { delay?: number; scale?: number }) {
    if (!el) return
    gsap.from(el, {
      opacity: 0,
      scale: options?.scale ?? 0.88,
      duration: 1,
      ease: 'power3.out',
      delay: options?.delay ?? 0,
      scrollTrigger: {
        trigger: el,
        start: 'top 85%',
        once: true,
      },
    })
  }

  /** 固定 pinning 视差区块（Velvet Pour 招牌效果） */
  function createPinnedSection(
    triggerEl: HTMLElement | null,
    panels: { el: HTMLElement | null; animation: gsap.core.Tween | gsap.core.Timeline }[],
    options: { start?: string; end?: string; pin?: boolean; scrub?: boolean | number } = {}
  ) {
    if (!triggerEl) return
    const tl = gsap.timeline({
      scrollTrigger: {
        trigger: triggerEl,
        start: options.start ?? 'top top',
        end: options.end ?? '+=3000',
        pin: options.pin ?? true,
        scrub: options.scrub ?? 1,
      },
    })
    panels.forEach(({ el, animation }) => {
      if (el) tl.to(el, animation)
    })
    return tl
  }

  onMounted(() => {
    ctx
  })

  onUnmounted(() => {
    ctx.revert()
    ScrollTrigger.getAll().forEach(t => t.kill())
  })

  return {
    gsap,
    ScrollTrigger,
    revealText,
    staggerSlideUp,
    parallax,
    countUp,
    clipReveal,
    scaleReveal,
    createPinnedSection,
    splitTextByWord,
    splitTextByChar,
  }
}
