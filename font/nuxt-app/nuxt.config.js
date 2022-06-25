// meta和SEO配置
module.exports = {
  head: {
    title: '标题',
    meta: [
      { charset: 'utf-8' },
      { name: 'viewport', content: 'width=device-width, initial-scale=1' },
      {
        hid: 'meta-key-words',
        name: 'keywords',
        content: '检索关键字',
      },
      {
        hid: 'description',
        name: 'description',
        content: '描述语句',
      },
    ],
    link: [{ rel: 'icon', type: 'image/x-icon', href: '/favicon.ico' }],
  },

  // 全局css定义
  css: ['~/assets/css/main.css'],

  // nuxt端口号设置，默认是3000
  server: {
    port: 3001,
  },

  modules: [
    '@nuxtjs/axios', // 引入axios模块到项目中
  ],

  axios: {
    baseURL: 'http://icanhazip.com',
  },

  Plugins: ['~/plugins/axios'],
}
