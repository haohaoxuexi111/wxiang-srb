<template>
  <div class="app-container">
    <div style="margin-botton: 10px">
      <!-- 导入Excel按钮 -->
      <el-button
        @click="dialogVisible = true"
        type="primary"
        size="mini"
        icon="el-icon-upload2"
      >
        导入Excel
      </el-button>
      <!-- Excel导出按钮 -->
      <el-button
        @click="exportData"
        type="primary"
        size="mini"
        icon="el-icon-download"
      >
        导出Excel
      </el-button>
      <!-- dialog对话框 -->
      <el-dialog title="数据字典导入" :visible.sync="dialogVisible" width="30%">
        <el-form>
          <el-form-item label="请选择要上传的Excel文件">
            <el-upload
              :auto-upload="true"
              :multiple="false"
              :limit="1"
              :on-exceed="fileUploadExceed"
              :on-success="fileUploadSuccess"
              :on-error="fileUploadError"
              :action="BASE_API + '/admin/core/dict/import'"
              name="file"
              accept="application/vnd.ms-excel,application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            >
              <!-- 
              :auto-upload="true" 选好上传文件之后，自动上传
              :multiple="false" 选择上传文件时，是否允许一次选择多个文件
              :limit="1" 一次只能上传一个文件
              :on-exceed="fileUploadExceed" 当上传的文件多于 :limit="1" 个的时候自动触发这个方法
              :on-success="fileUploadSuccess" 通信成功执行的钩子函数
              :on-error="fileUploadError" 通信失败执行的钩子函数
              :action elementui 内部的调用地址，没有调用request中封装的axios
              name 文件变量名为file，后端接收file变量
              accept 表示只接收xls、xlsx类型的文件 
              -->
              <el-button size="small" type="primary">点击上传</el-button>
            </el-upload>
          </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
        </div>
      </el-dialog>

      <el-table :data="list" border row-key="id" lazy :load="load">
        <el-table-column label="名称" align="left" prop="name">
          <!-- <template slot-scope="scope">
            <span>{{ scope.row.name }}</span>
          </template> -->
        </el-table-column>
        <el-table-column label="编码" prop="dictCode">
          <!-- <template slot-scope="{ row }">    {row} 结构赋值的方式，等价于 "scope.row" 
            <span>{{ row.dictCode }}</span>
          </template> -->
        </el-table-column>
        <el-table-column label="值" align="left" prop="value">
          <!-- <template slot-scope="scope">
            <span>{{ scope.row.value }}</span>
          </template> -->
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script>
import dictApi from '@/api/core/dict'

export default {
  data() {
    return {
      dialogVisible: false, // 对话框是否显示
      BASE_API: process.env.VUE_APP_BASE_API, // 从环境变量中获取后端接口地址，这里是 http://localhost
      list: [], // 数据字典列表
    }
  },

  created() {
    this.fetchData()
  },

  methods: {
    // 获取数据字典列表
    fetchData() {
      dictApi.listByParentId(1).then((response) => {
        this.list = response.data.list
      })
    },

    // 上传多于一个文件时
    fileUploadExceed() {
      this.$message.warning('只能选取一个文件')
    },

    // 文件上传成功时，执行的回调(钩子)函数  通信成功
    fileUploadSuccess(response) {
      if (response.code === 0) {
        // 业务成功
        this.$message.success('数据导入成功')
        this.dialogVisible = false
      } else {
        // 通信成功，业务失败
        this.$message.error(response.message)
      }
    },
    // 上传失败回调，通信失败
    fileUploadError(error) {
      this.$message.error('数据导入失败')
    },
    exportData() {
      // 出于安全因素的考虑，javascript是不能够保存文件到本地的，所以ajax考虑到了这点，只是接受xml,ajax,json格式的返回值，二进制的返回格式就会抛出异常。
      window.location.href = this.BASE_API + '/admin/core/dict/export'
    },

    // 加载类别的儿子节点
    load(tree, treeNode, resolve) {
      // 获取数据
      dictApi.listByParentId(tree.id).then((response) => {
        resolve(response.data.list)
      })
    },
  },
}
</script>
