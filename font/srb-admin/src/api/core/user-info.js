import request from '@/utils/request'

export default {
  // 分页条件查询api
  getPageList(page, limit, searchObj) {
    return request({
      url: `/admin/core/userInfo/list/${page}/${limit}`, // 拼接了参数，所以，这里不是使用单引号，而是使用` `(飘)
      method: 'get', // 如果是get方式的请求，传递json参数要使用params，如果是有请求体(post)的传参，就使用data，并且后端参数用@RequestBody修饰
      params: searchObj,
    })
  },

  // 锁定用户api
  lock(id, status) {
    return request({
      url: `/admin/core/userInfo/lock/${id}/${status}`,
      method: 'put',
    })
  },

  // 获取会员登录记录api
  getUserLoginRecordTop50(userId) {
    return request({
      url: `/admin/core/userLoginRecord/listTop50/${userId}`,
      method: 'get',
    })
  },
}
