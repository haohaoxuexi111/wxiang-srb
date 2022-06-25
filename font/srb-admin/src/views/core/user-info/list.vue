<template>
  <div class="app-container">
    <!-- 查询表单 -->
    <!-- :inline表明这是一个行内表单，多个item在一行显示 -->
    <el-form :inline="true" class="demo-form-inline">
      <el-form-item label="手机号">
        <el-input v-model="searchObj.mobile" placeholder="手机号" />
      </el-form-item>

      <el-form-item label="用户类型">
        <!-- clearable表示选好之后可以点击x，清除选择 -->
        <el-select v-model="searchObj.userType" placeholder="请选择" clearable>
          <el-option label="投资人" value="1" />
          <el-option label="借款人" value="2" />
        </el-select>
      </el-form-item>

      <el-form-item label="用户状态">
        <el-select v-model="searchObj.status" placeholder="请选择" clearable>
          <el-option label="正常" value="1" />
          <el-option label="锁定" value="0" />
        </el-select>
      </el-form-item>

      <el-button type="primary" icon="el-icon-search" @click="fetchData()">
        查询
      </el-button>
      <el-button type="default" @click="resetData()">清空</el-button>
    </el-form>

    <!-- 列表 -->
    <el-table :data="list" border stripe>
      <el-table-column label="序号" width="50">
        <template slot-scope="scope">
          {{ (page - 1) * limit + scope.$index + 1 }}
        </template>
      </el-table-column>

      <el-table-column label="用户类型" width="100">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.userType === 1" type="success" size="mini">
            投资人
          </el-tag>
          <el-tag
            v-else-if="scope.row.userType === 2"
            type="warning"
            size="mini"
          >
            借款人
          </el-tag>
        </template>
      </el-table-column>

      <el-table-column prop="mobile" label="手机号" />
      <!-- prop和后端实体类UserInfo的属性名对应 -->
      <el-table-column prop="name" label="用户姓名" />
      <el-table-column prop="idCard" label="身份证号" />
      <el-table-column prop="integral" label="用户积分" />
      <el-table-column prop="createTime" label="注册时间" width="100" />

      <el-table-column label="绑定状态" width="90">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.bindStatus === 0" type="warning" size="mini">
            未绑定
          </el-tag>
          <el-tag
            v-else-if="scope.row.bindStatus === 1"
            type="success"
            size="mini"
          >
            已绑定
          </el-tag>
        </template>
      </el-table-column>

      <el-table-column label="用户状态" width="90">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.status === 0" type="danger" size="mini">
            锁定
          </el-tag>
          <el-tag v-else type="success" size="mini">正常</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="200">
        <template slot-scope="scope">
          <el-button
            v-if="scope.row.status == 1"
            type="primary"
            size="mini"
            @click="lock(scope.row.id, 0)"
          >
            锁定
          </el-button>
          <el-button
            v-else
            type="danger"
            size="mini"
            @click="lock(scope.row.id, 1)"
          >
            解锁
          </el-button>
          <el-button
            type="primary"
            size="mini"
            @click="showLoginRecord(scope.row.id)"
          >
            登录日志
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页组件 -->
    <el-pagination
      :current-page="page"
      :total="total"
      :page-size="limit"
      :page-sizes="[3, 4, 5]"
      style="padding: 30px 0"
      layout="->, total, sizes, prev, pager, next, jumper"
      @size-change="changePageSize"
      @current-change="changeCurrentPage"
    />
    <!-- layout是组件布局，设置要显示的组件名，->表示将组件置于右边，
    @size-change每页显示条目数事件，用户选定page-sizes中的一个值时触发，@current-change当前页事件，current-page改变时触发 -->

    <!-- 用户登录日志dialog -->
    <el-dialog title="登录日志" :visible.sync="dialogTableVisible">
      <el-table :data="loginRecordList" border stripe>
        <el-table-column type="index" />
        <el-table-column prop="ip" label="IP地址" />
        <el-table-column prop="createTime" label="登录时间" />
      </el-table>
    </el-dialog>
  </div>
</template>

<script>
// 引入api
import userInfoApi from '@/api/core/user-info'

export default {
  data() {
    return {
      list: null, // 数据列表
      total: 0, // 总记录数
      page: 1, // 默认页码
      limit: 10, // 每页记录数
      searchObj: {}, // 查询条件
      loginRecordList: [], // 会员登录日志
      dialogTableVisible: false, // 对话框是否显示
    }
  },

  created() {
    this.fetchData()
  },

  methods: {
    changePageSize(size) {
      // el-pagination组件内部会传入回调参数，会根据用户的选择传入对应的page-sizes中的一个值
      this.limit = size
      this.fetchData()
    },

    changeCurrentPage(page) {
      // el-pagination组件内部会传入回调参数，传入用户选择的当前页码
      this.page = page
      this.fetchData()
    },

    fetchData() {
      userInfoApi
        .getPageList(this.page, this.limit, this.searchObj)
        .then((response) => {
          this.list = response.data.pageModel.records
          this.total = response.data.pageModel.total
        })
    },

    resetData() {
      // 还原表单
      this.searchObj = {}
      // 重新查询
      this.fetchData()
    },

    lock(id, status) {
      userInfoApi.lock(id, status).then((response) => {
        this.$message.success(response.message)
        this.fetchData()
      })
    },

    showLoginRecord(id) {
      this.dialogTableVisible = true
      userInfoApi.getUserLoginRecordTop50(id).then((response) => {
        this.loginRecordList = response.data.list
      })
    },
  },
}
</script>
