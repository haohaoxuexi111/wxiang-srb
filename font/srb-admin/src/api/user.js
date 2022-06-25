import request from '@/utils/request'

// 在request中有：create an axios instance
// const service = axios.create({
//   baseURL: process.env.VUE_APP_BASE_API, // url = base url + request url  此时VUE_APP_BASE_API为http://localhost
//   // withCredentials: true, // send cookies when cross-domain requests
//   timeout: 5000 // request timeout
// })

// 获取用户信息、登录、登出功能依然调用mock-server服务器的接口http://localhost:9528/dev-api/vue-admin-template/user/login，其它服务调用我们自己的nginx代理接口http://localhost
// 这里的三个方法返回20000状态码表示成功，而后端返回状态码0表示成功
export function login(data) {
  return request({
    baseURL: '/dev-api',
    url: '/vue-admin-template/user/login',
    method: 'post',
    data,
  })
}

export function getInfo(token) {
  return request({
    baseURL: '/dev-api',
    url: '/vue-admin-template/user/info',
    method: 'get',
    params: { token },
  })
}

export function logout() {
  return request({
    baseURL: '/dev-api',
    url: '/vue-admin-template/user/logout',
    method: 'post',
  })
}
