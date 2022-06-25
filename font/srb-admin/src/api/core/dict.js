import request from '@/utils/request'

export default {
  listByParentId(parentId) {
    return request({
      url: `/admin/core/dict/listByParentId/${parentId}`, // 这里要使用 ` `(飘)符号括起来，而不是单引号
      method: 'get',
    })
  },
}
