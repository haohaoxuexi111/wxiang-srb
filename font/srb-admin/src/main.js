import Vue from 'vue'

import 'normalize.css/normalize.css' // A modern alternative to CSS resets 基础css样式设置

import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import locale from 'element-ui/lib/locale/lang/zh-CN' // lang i18n

import '@/styles/index.scss' // global css  全局css样式

import App from './App' // 引入根组件 /src/App.vue
import store from './store' // 存储前端信息的工具，类似后端的session
import router from './router' // 路由模块

import '@/icons' // icon  扩展图标系统
import '@/permission' // permission control  角色权限控制系统

/** 关于模拟接口服务器的配置
 * If you don't want to use mock-server
 * you want to use MockJs for mock api
 * you can execute: mockXHR()
 *
 * Currently MockJs will be used in the production environment,
 * please remove it before going online ! ! !
 */
if (process.env.NODE_ENV === 'production') {
  const { mockXHR } = require('../mock')
  mockXHR()
}

// set ElementUI lang to EN   将 element-ui 挂载到 vue 中
Vue.use(ElementUI, { locale })
// 如果想要中文版 element-ui，按如下方式声明
// Vue.use(ElementUI)

Vue.config.productionTip = false

new Vue({
  el: '#app', // 要渲染的节点是 'srb-admin/public/index.html' 中的div
  router, // 挂载  /src/router/index.js 中导出的路由模块
  store,
  render: (h) => h(App), // render渲染引擎，挂载组件
})
