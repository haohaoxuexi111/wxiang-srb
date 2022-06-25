import { Message } from 'element-ui'
import cookie from 'js-cookie'

export default function({ $axios, redirect }) {
  // 请求拦截器
  $axios.onRequest((config) => {
    let userInfo = cookie.get('userInfo')
    if (userInfo) {
      // debugger  解析json字符串
      userInfo = JSON.parse(userInfo)
      config.headers['token'] = userInfo.token
    }
    console.log('Making request to ' + config.url)
  })

  $axios.onRequestError((error) => {
    console.log('onRequestError', error) // for debug
  })

  // 响应拦截器
  $axios.onResponse((response) => {
    console.log('Reciving resposne', response)
    if (response.data.code === 0) {
      return response
    } else if (response.data.code === -211) {
      console.log('用户校验失败')
      // debugger  未登录
      cookie.set('userInfo', '')
      window.location.href = '/'
    } else {
      Message({
        message: response.data.message,
        type: 'error',
        duration: 5 * 1000,
      })
      return Promise.reject(response)
    }
  })

  //通信失败
  $axios.onResponseError((error) => {
    console.log('onResponseError', error) // for debug
  })
}
