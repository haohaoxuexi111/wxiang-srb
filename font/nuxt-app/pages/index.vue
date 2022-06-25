<template>
  <div>
    <h1>主页面</h1>
    <NuxtLink to="/about"> 关于我们 </NuxtLink>&nbsp;
    <NuxtLink to="/user"> 用户中心 </NuxtLink>&nbsp;
    <NuxtLink to="/lend">
      lend/index.vue
    </NuxtLink>

    <p>您的ip是：{{ ip1 }}</p>
  </div>
</template>

<script>
export default {
  // 服务器端数据渲染，解构赋值{$axios} 相当于 page.$axios
  //asynData(page) {
  async asyncData({ $axios }) {
    // 前端服务器中的同步操作，'/' 使用的是nuxt.config.js中的baseURL
    let response = await $axios.$get('/')
    return {
      ip1: response,
    }
  },

  data() {
    return {
      ip: null,
    }
  },

  // 客户端渲染，异步请求操作
  created() {
    // nuxt整合axios之get请求方式
    // this.$axios.get('http://icanhazip.com').then((res) => {
    //   console.log(res)
    //   this.ip = res.data
    // })
    // this.$axios.$get('http://icanhazip.com').then((res) => {
    //   // 只返回data，由res接收
    //   console.log(res)
    //   this.ip = res
    // })
  },
}
</script>
