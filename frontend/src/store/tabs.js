import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useTabsStore = defineStore('tabs', () => {
  const tabs = ref([])
  const activeTab = ref('')

  function addTab(route) {
    const path = route.path
    if (path === '/login') return
    const existing = tabs.value.find(t => t.path === path)
    if (!existing) {
      tabs.value.push({
        title: route.meta?.title || route.name || '未知',
        path,
        closable: path !== '/dashboard'
      })
    }
    activeTab.value = path
  }

  function removeTab(path) {
    const idx = tabs.value.findIndex(t => t.path === path)
    if (idx === -1) return null
    tabs.value.splice(idx, 1)
    if (tabs.value.length === 0) return '/dashboard'
    const nextIdx = Math.min(idx, tabs.value.length - 1)
    return tabs.value[nextIdx].path
  }

  function closeTabByPath(path) {
    const idx = tabs.value.findIndex(t => t.path === path)
    if (idx > -1) tabs.value.splice(idx, 1)
  }

  function initTabs() {
    tabs.value = [{ title: '仪表盘', path: '/dashboard', closable: false }]
    activeTab.value = '/dashboard'
  }

  return { tabs, activeTab, addTab, removeTab, closeTabByPath, initTabs }
})
