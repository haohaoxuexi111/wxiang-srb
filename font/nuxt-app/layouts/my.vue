<template>
  <div>
    <!-- 自定义布局文件 my.vue，需要在使用自定义布局的页面(user.vue)配置 layout: 'my' -->
    <!-- 页面布局：公共头部  -->
    <div>自定义头部</div>

    <!-- 页面占位符，中间内容部分 -->
    <Nuxt />

    <!-- 页面布局：公共尾部 -->
    <div>自定义底部</div>
  </div>
</template>
