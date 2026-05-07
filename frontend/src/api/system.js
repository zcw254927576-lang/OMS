import request from '../utils/request'

export function getUserPage(params) {
  return request.get('/system/user/page', { params })
}

export function saveUser(data) {
  return request.post('/system/user', data)
}

export function updateUser(data) {
  return request.put('/system/user', data)
}

export function resetPassword(id) {
  return request.put(`/system/user/${id}/reset-pwd`)
}

export function getRoleList() {
  return request.get('/system/role')
}

export function saveRole(data) {
  return request.post('/system/role', data)
}

export function assignRoleMenu(roleId, menuIds) {
  return request.post(`/system/role/${roleId}/assign-menu`, menuIds)
}

export function getMenuTree() {
  return request.get('/system/menu')
}

export function getLogPage(params) {
  return request.get('/system/log/page', { params })
}
