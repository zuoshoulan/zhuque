import { ref, watch } from 'vue'
import { useUserStore } from '@/stores/user'

export type ThemeMode = 'light' | 'dark' | 'auto'

const THEME_KEY = 'theme-mode'

// 获取存储的主题模式
function getStoredTheme(): ThemeMode {
  const stored = localStorage.getItem(THEME_KEY)
  if (stored === 'light' || stored === 'dark' || stored === 'auto') {
    return stored
  }
  return 'auto'
}

// 保存主题模式
function saveTheme(theme: ThemeMode) {
  localStorage.setItem(THEME_KEY, theme)
}

// 根据时间判断应该使用的主题
function getThemeByTime(): 'light' | 'dark' {
  const hour = new Date().getHours()
  // 6:00 - 18:00 为白天，使用亮色主题
  return hour >= 6 && hour < 18 ? 'light' : 'dark'
}

// 应用主题到document
function applyTheme(theme: 'light' | 'dark') {
  const html = document.documentElement
  if (theme === 'dark') {
    html.classList.add('dark')
  } else {
    html.classList.remove('dark')
  }
}

// 全局状态
const themeMode = ref<ThemeMode>(getStoredTheme())
const actualTheme = ref<'light' | 'dark'>(
  themeMode.value === 'auto' ? getThemeByTime() : themeMode.value
)

export function useTheme() {
  const userStore = useUserStore()

  // 初始化主题
  function initTheme() {
    const userPreference = userStore.userInfo?.themePreference as ThemeMode
    if (userPreference && userPreference !== 'auto') {
      themeMode.value = userPreference
    } else {
      themeMode.value = getStoredTheme()
    }

    updateActualTheme()
  }

  // 更新实际应用的主题
  function updateActualTheme() {
    if (themeMode.value === 'auto') {
      actualTheme.value = getThemeByTime()
    } else {
      actualTheme.value = themeMode.value
    }
    applyTheme(actualTheme.value)
  }

  // 设置主题
  async function setTheme(theme: ThemeMode) {
    themeMode.value = theme
    saveTheme(theme)
    updateActualTheme()

    // 同步到服务器
    try {
      await userStore.updateThemePreference(theme)
    } catch (error) {
      console.error('同步主题偏好失败:', error)
    }
  }

  // 获取当前实际主题
  function getCurrentTheme(): 'light' | 'dark' {
    return actualTheme.value
  }

  // 监听主题模式变化
  watch(themeMode, () => {
    updateActualTheme()
  })

  // 定时检查自动模式（每分钟检查一次）
  if (typeof window !== 'undefined') {
    setInterval(() => {
      if (themeMode.value === 'auto') {
        updateActualTheme()
      }
    }, 60000)
  }

  return {
    themeMode,
    actualTheme,
    initTheme,
    setTheme,
    getCurrentTheme
  }
}
