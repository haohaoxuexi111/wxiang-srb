import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router)

/* Layout */
import Layout from '@/layout' // '@/layout' 等价于 '@/layout/index.vue' 后面的index可以省略

/**
 * Note: sub-menu only appear when route children.length >= 1
 * Detail see: https://panjiachen.github.io/vue-element-admin-site/guide/essentials/router-and-nav.html
 *
 * hidden: true                   if set true, item will not show in the sidebar(default is false)
 * alwaysShow: true               if set true, will always show the root menu
 *                                if not set alwaysShow, when item has more than one children route,
 *                                it will becomes nested mode, otherwise not show the root menu
 * redirect: noRedirect           if set noRedirect will no redirect in the breadcrumb
 * name:'router-name'             the name is used by <keep-alive> (must set!!!)
 * meta : {
    roles: ['admin','editor']    control the page roles (you can set multiple roles)
    title: 'title'               the name show in sidebar and breadcrumb (recommend set)
    icon: 'svg-name'/'el-icon-x' the icon show in the sidebar
    breadcrumb: false            if set false, the item will hidden in breadcrumb(default is true)
    activeMenu: '/example/list'  if set path, the sidebar will highlight the path you set
  }
 */

/**
 * constantRoutes
 * a base page that does not have permission requirements
 * all roles can be accessed
 * 前端开发流程：路由 --> 页面组件(<template><script><style>) --> 前端API --> 后端接口
 */
export const constantRoutes = [
  {
    path: '/login',
    component: () => import('@/views/login/index'), // @/views/login/index 对应一个vue模板组件页面的位置
    hidden: true, // 不显示在菜单栏里，可以在其它地方使用
  },

  {
    path: '/404',
    component: () => import('@/views/404'),
    hidden: true,
  },

  {
    path: '/', // App.vue嵌layout中的index.vue，而index.vue嵌AppMain.vue、Navbar.vue、Sidebar中的index.vue
    component: Layout, // 布局组件 @/layout/index.vue，里面定义了侧边栏和导航栏
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index'), // 匹配到主内容区域组件的路由出口，会嵌到layout组件的主内容区域
        meta: { title: '首页', icon: 'dashboard' },
      },
    ],
  },

  {
    path: '/core/integral-grade',
    component: Layout, // Layout 包含整个页面的布局
    redirect: '/core/integral-grade/list', // 当访问 /core/integral-grade 时，自动跳转到 /core/integral-grade/list
    name: 'coreIntegralGrade',
    meta: { title: '积分等级管理', icon: 'el-icon-s-marketing' },
    // alwaysShow:false(默认值)时，若父节点下只有一个子节点，则不显示父节点
    // alwaysShow:true，任何时候都显示父节点和子节点。
    alwaysShow: true,
    children: [
      {
        path: 'list', // 路由地址，父路由+子路由才是完整的路由：/core/integral-grade/list
        name: 'coreIntegralGradeList', // name 不能相同
        component: () => import('@/views/core/integral-grade/list'),
        meta: { title: '积分等级列表', icon: 'el-icon-document' },
      },
      {
        path: 'create', // 完整路由：/core/integral-grade/create
        name: 'coreIntegralGradeCreate',
        component: () => import('@/views/core/integral-grade/form'),
        meta: { title: '编辑积分等级', icon: 'el-icon-plus' },
      },
      {
        path: 'edit/:id', // :id 是一个占位符，表示这部分url会和任何一个id拼接，是动态的。
        name: 'coreIntegralGradeEdit',
        component: () => import('@/views/core/integral-grade/form'),
        meta: { title: '编辑积分等级', icon: 'el-icon-edit' },
        hidden: true,
      },
    ],
  },

  {
    path: '/core/user-info',
    component: Layout,
    redirect: '/core/user-info/list',
    name: 'coreUserInfo',
    meta: { title: '会员管理', icon: 'user' },
    alwaysShow: true,
    children: [
      {
        path: 'list',
        name: 'coreUserInfoList',
        component: () => import('@/views/core/user-info/list'),
        meta: { title: '会员列表' },
      },
    ],
  },

  {
    path: '/core',
    component: Layout,
    redirect: '/core/dict/list',
    name: 'coreDict',
    meta: { title: '系统设置', icon: 'el-icon-setting' },
    alwaysShow: true,
    children: [
      {
        path: 'dict/list',
        name: '数据字典',
        component: () => import('@/views/core/dict/list'),
        meta: { title: '数据字典' },
      },
    ],
  },

  {
    path: '/example',
    component: Layout,
    redirect: '/example/table',
    name: 'Example',
    meta: { title: 'Example', icon: 'el-icon-menu' },
    children: [
      {
        path: 'table',
        name: 'Table',
        component: () => import('@/views/table/index'),
        meta: { title: 'Table', icon: 'table' },
      },
      {
        path: 'tree',
        name: 'Tree',
        component: () => import('@/views/tree/index'),
        meta: { title: 'Tree', icon: 'tree' },
      },
    ],
  },

  {
    path: '/form',
    component: Layout,
    meta: { title: '表单管理', icon: 'el-icon-more' },
    alwaysShow: true,
    children: [
      {
        path: 'index',
        name: 'Form',
        component: () => import('@/views/form/index'),
        meta: { title: 'Form', icon: 'form' },
      },
    ],
  },
]

/**
 * asyncRoutes
 * the routes that need to be dynamically loaded based on user roles
 */
export const asyncRoutes = [
  {
    path: '/nested',
    component: Layout,
    redirect: '/nested/menu1',
    name: 'Nested',
    meta: {
      title: 'Nested',
      icon: 'nested',
    },
    children: [
      {
        path: 'menu1',
        component: () => import('@/views/nested/menu1/index'), // Parent router-view
        name: 'Menu1',
        meta: { title: 'Menu1' },
        children: [
          {
            path: 'menu1-1',
            component: () => import('@/views/nested/menu1/menu1-1'),
            name: 'Menu1-1',
            meta: { title: 'Menu1-1' },
          },
          {
            path: 'menu1-2',
            component: () => import('@/views/nested/menu1/menu1-2'),
            name: 'Menu1-2',
            meta: { title: 'Menu1-2' },
            children: [
              {
                path: 'menu1-2-1',
                component: () =>
                  import('@/views/nested/menu1/menu1-2/menu1-2-1'),
                name: 'Menu1-2-1',
                meta: { title: 'Menu1-2-1' },
              },
              {
                path: 'menu1-2-2',
                component: () =>
                  import('@/views/nested/menu1/menu1-2/menu1-2-2'),
                name: 'Menu1-2-2',
                meta: { title: 'Menu1-2-2' },
              },
            ],
          },
          {
            path: 'menu1-3',
            component: () => import('@/views/nested/menu1/menu1-3'),
            name: 'Menu1-3',
            meta: { title: 'Menu1-3' },
          },
        ],
      },
      {
        path: 'menu2',
        component: () => import('@/views/nested/menu2/index'),
        meta: { title: 'menu2' },
      },
    ],
  },

  {
    path: 'external-link',
    component: Layout,
    children: [
      {
        path: 'https://panjiachen.github.io/vue-element-admin-site/#/',
        meta: { title: 'External Link', icon: 'link' },
      },
    ],
  },

  // 404 page must be placed at the end !!!
  { path: '*', redirect: '/404', hidden: true }, // 如果前面的路由都没有匹配到访问路径，则匹配到404页面
]

const createRouter = () =>
  new Router({
    // mode: 'history', // require service support
    scrollBehavior: () => ({ y: 0 }),
    routes: constantRoutes,
  })

const router = createRouter()

// Detail see: https://github.com/vuejs/vue-router/issues/1234#issuecomment-357941465
export function resetRouter() {
  const newRouter = createRouter()
  router.matcher = newRouter.matcher // reset router
}

export default router