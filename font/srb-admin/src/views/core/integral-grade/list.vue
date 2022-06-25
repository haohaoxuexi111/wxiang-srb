<template>
  <div class="app-container">
    <!-- 表格 -->
    <el-table :data="list" border stripe>
      <!-- stripe隔行换色 -->
      <el-table-column type="index" width="50" />
      <el-table-column prop="borrowAmount" label="借款额度" />
      <el-table-column prop="integralStart" label="积分区间开始" />
      <el-table-column prop="integralEnd" label="积分区间结束" />
      <el-table-column prop="updateTime" label="更新时间" />
      <el-table-column label="操作">
        <template slot-scope="scope">
          <!-- scope相对于当前正在遍历的上下文对象，scope.row.id获取当前行的id，并作为参数传递 -->
          <router-link
            :to="'/core/integral-grade/edit/' + scope.row.id"
            style="margin-right: 5px"
          >
            <!--  路由路径是我们在 /router/index.js 中定义的菜单路径 -->
            <el-button type="primary" size="mini" icon="el-icon-edit">
              <!-- 单击修改按钮跳转到路由：/core/integral-grade/edit/:id -->
              修改
            </el-button>
          </router-link>

          <el-button
            type="danger"
            size="mini"
            icon="el-icon-delete"
            @click="removeById(scope.row.id)"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
// 引入api模块
import integralGradeApi from '@/api/core/integral-grade'

// 这里导出的模板会被 src/router/index.js 中的路由调用
export default {
  data() {
    return {
      list: [], // 积分等级列表
    }
  },

  created() {
    this.fetchData()
  },

  methods: {
    fetchData() {
      // 调用前端api @/core/integral-grade 中定义的list方法
      integralGradeApi.list().then((response) => {
        this.list = response.data.list
      })
    },

    removeById(id) {
      console.log('id', id)
      // debugger 在此处设置断点进行断点调试
      this.$confirm('此操作将永久删除该记录，是否继续？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      })
        // .then(() => {  // 点击确定
        //   integralGradeApi.removeById(id).then((response) => {  // 调用接口删除记录
        //     this.$message({
        //       showClose: true,
        //       message: response.message,
        //       type: 'seccess',
        //     })
        //     this.fetchData() // 删除成功，重新获取列表数据
        //   })
        // })
        // 用串联回调代替嵌套回调，提高代码可读性
        .then(() => {
          // 点击确定删除
          return integralGradeApi.removeById(id) // 调用接口删除记录
        })
        .then((response) => {
          // removeById方法回调
          this.$message({
            showClose: true,
            message: response.message,
            type: 'seccess',
          })
          this.fetchData() // 删除成功，重新获取列表数据
        })
        .catch((error) => {
          // utils/request.js中的响应拦截器会在请求发生错误时执行：return Promise.reject(error)，这时也会执行这里
          console.log('catch的error', error)

          // 点击了取消删除
          if (error === 'calcel') {
            this.$message({
              type: 'info',
              message: '已取消删除',
            })
          }
        })
    },
  },
}
</script>

<style scoped></style>
