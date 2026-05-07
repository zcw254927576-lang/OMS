import { defineStore } from 'pinia'
import request from '../utils/request'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    user: null,
    menus: [],
    perms: []
  }),
  actions: {
    async login(credentials) {
      const res = await request.post('/auth/login', credentials)
      this.token = res.data.token
      localStorage.setItem('token', res.data.token)
      await this.getUserInfo()
    },
    async getUserInfo() {
      const res = await request.get('/auth/user-info')
      this.user = res.data.user
      this.menus = res.data.menus
      this.perms = res.data.perms || []
    },
    logout() {
      this.token = ''
      this.user = null
      this.menus = []
      this.perms = []
      localStorage.removeItem('token')
    }
  }
})
