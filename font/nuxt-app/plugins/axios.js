export default function({ $axios, redirect }) {
  $axios.onRequest((config) => {
    config.log('执行请求拦截器' + config.url)
  })

  $axios.onResponse((response) => {
    console.log('执行响应拦截器')
    return response
  })

  $axios.onError((error) => {
    console.log(error)
  })
  // 需要在nuxt.config.js中配置才会生效
}
