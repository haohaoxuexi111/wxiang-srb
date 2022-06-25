// 定义前端API来调用后端接口

// 引入axios的初始化模块
import request from '@/utils/request' // 引入request模块，@ 是src的别名，第一个request是起的名字

// 导出默认模块
export default {
  // 定义模块成员，
  // 成员方法：获取积分等级列表
  list() {
    // 调用axios的初始化模块，发送axios请求，请求nginx服务器，再由nginx请求对应服务
    return request({
      url: '/admin/core/integralGrade/list',
      method: 'get', // 请求方式要和后端的保持一致，否则会报跨域请求错误
    })
  },

  removeById(id) {
    return request({
      url: '/admin/core/integralGrade/remove/' + id,
      method: 'delete',
    })
  },

  save(integralGrade) {
    return request({
      url: '/admin/core/integralGrade/save', // 请求路径
      method: 'post', // 请求方式
      data: integralGrade, // 关键字data会自动将integralGrade转换成json字符串
    })
  },

  getById(id) {
    return request({
      url: '/admin/core/integralGrade/getById/' + id,
      method: 'get',
    })
  },

  updateById(integralGrade) {
    return request({
      url: '/admin/core/integralGrade/update',
      method: 'put',
      data: integralGrade,
    })
  },
}
