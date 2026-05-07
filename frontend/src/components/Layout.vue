<template>
  <el-container style="height: 100vh">
    <!-- 侧边栏 -->
    <el-aside :width="isCollapse ? '64px' : '220px'" style="background: #304156; transition: width 0.3s">
      <div class="logo" style="height: 60px; display: flex; align-items: center; justify-content: center; color: #fff; font-size: 20px; font-weight: bold; border-bottom: 1px solid rgba(255,255,255,0.1)">
        <span v-if="!isCollapse">OMS 管理系统</span>
        <span v-else>OMS</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        :collapse-transition="false"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
        router
      >
        <template v-for="menu in menuList" :key="menu.id">
          <el-sub-menu v-if="menu.children && menu.children.length" :index="menu.path">
            <template #title>
              <el-icon><component :is="menu.icon || 'Menu'" /></el-icon>
              <span>{{ menu.menuName }}</span>
            </template>
            <el-menu-item v-for="child in menu.children" :key="child.id" :index="menu.path + '/' + child.path">
              <el-icon><component :is="child.icon || 'Document'" /></el-icon>
              <span>{{ child.menuName }}</span>
            </el-menu-item>
          </el-sub-menu>
          <el-menu-item v-else :index="menu.path">
            <el-icon><component :is="menu.icon || 'Menu'" /></el-icon>
            <span>{{ menu.menuName }}</span>
          </el-menu-item>
        </template>
      </el-menu>
    </el-aside>

    <el-container>
      <!-- 顶部 -->
      <el-header style="background: #fff; border-bottom: 1px solid #e6e6e6; display: flex; align-items: center; justify-content: space-between; height: 60px; padding: 0 20px">
        <div style="display: flex; align-items: center">
          <el-button @click="isCollapse = !isCollapse" text>
            <el-icon><Fold /></el-icon>
          </el-button>
          <el-breadcrumb separator="/" style="margin-left: 16px">
            <el-breadcrumb-item :to="{ path: '/dashboard' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-if="currentTitle">{{ currentTitle }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div style="display: flex; align-items: center; gap: 16px">
          <el-dropdown>
            <span style="cursor: pointer; display: flex; align-items-center; gap: 6px">
              <el-avatar :size="32" icon="UserFilled" />
              <span>{{ authStore.user?.realName || authStore.user?.username }}</span>
            </span>
            <template #dropdown>
              <el-dropdown-item @click="handleLogout">
                <el-icon><SwitchButton /></el-icon> 退出登录
              </el-dropdown-item>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 多页签 -->
      <div style="background: #fff; padding: 0 12px; border-bottom: 1px solid #e6e6e6; display: flex; align-items: center">
        <el-tabs
          v-model="tabsStore.activeTab"
          type="card"
          style="flex:1; margin-bottom: -1px"
          @tab-click="handleTabClick"
          @tab-remove="handleTabRemove"
        >
          <el-tab-pane
            v-for="tab in tabsStore.tabs"
            :key="tab.path"
            :label="tab.title"
            :name="tab.path"
            :closable="tab.closable"
          />
        </el-tabs>
      </div>

      <!-- 主体内容 -->
      <el-main style="background: #f0f2f5; padding: 20px; overflow-y: auto">
        <router-view v-slot="{ Component, route }">
          <keep-alive :max="15">
            <component :is="Component" :key="route.fullPath" />
          </keep-alive>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '../store/auth'
import { useTabsStore } from '../store/tabs'
import routes from '../router'

const authStore = useAuthStore()
const tabsStore = useTabsStore()
const route = useRoute()
const router = useRouter()
const isCollapse = ref(false)

const activeMenu = computed(() => route.path)

const currentTitle = computed(() => route.meta?.title || '')

const menuList = computed(() => {
  const menus = authStore.menus
  return menus
})

// 路由变化时自动添加标签页
watch(() => route.path, () => {
  tabsStore.addTab(route)
})

function handleTabClick(tab) {
  if (tab.props.name !== route.path) {
    router.push(tab.props.name)
  }
}

function handleTabRemove(name) {
  const next = tabsStore.removeTab(name)
  if (next && next !== route.path) {
    router.push(next)
  }
}

onMounted(async () => {
  if (!authStore.user) {
    try {
      await authStore.getUserInfo()
    } catch (e) {
      router.push('/login')
      return
    }
  }
  tabsStore.initTabs()
  tabsStore.addTab(route)
})

function handleLogout() {
  authStore.logout()
  router.push('/login')
}
</script>
