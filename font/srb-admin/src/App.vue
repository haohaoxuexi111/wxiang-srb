<template>
  <!-- 根组件嵌套显示路由path对应的子组件 -->
  <div id="app">
    <!-- 显示组件模板的路由出口，模板在/views/*.vue，每个vue文件都有一个template，template和component绑定，路由path和组件对应，可以在 /router/index.js中查看对应关系 -->
    <router-view />
  </div>
</template>

<script>
export default {
  name: 'App',
}
</script>
