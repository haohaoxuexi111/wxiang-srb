<template>
  <div>
    <!-- 积分等级输入表单 -->
    <el-form label-width="120px">
      <el-form-item label="借款额度">
        <el-input-number v-model="integralGrade.borrowAmount" :min="0" />
        <!-- v-model既可以接收用户输入新增记录，也可以接收后端查询结果回显数据 -->
        <!-- :min="0"数字输入框的最小值为0 -->
      </el-form-item>
      <el-form-item label="积分区间开始">
        <el-input-number v-model="integralGrade.integralStart" :min="0" />
      </el-form-item>
      <el-form-item label="积分区间结束">
        <el-input-number v-model="integralGrade.integralEnd" :min="0" />
      </el-form-item>
      <el-form-item>
        <el-button
          :disabled="saveBtnDisabled"
          type="primary"
          @click="saveOrUpdate()"
          icon="el-icon-check"
        >
          <!-- type="primary" 主要按钮，通过 type 属性指定 primary、success、info、warning、danger、text 其中的值设置按钮样式 -->
          保存
        </el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
// 引入api模块
import integralGradeApi from '@/api/core/integral-grade'

export default {
  data() {
    return {
      saveBtnDisabled: false, // disabled属性绑定的值，表示是否禁用保存按钮，防止表单重复提交
      integralGrade: {}, // 积分等级对象
    }
  },

  created() {
    // 当路由中存在id属性的时候，就调用接口，回显表单
    if (this.$route.params.id) {
      this.fetchById(this.$route.params.id)
    }
  },

  methods: {
    fetchById(id) {
      integralGradeApi.getById(id).then((response) => {
        this.integralGrade = response.data.record // record 是后端传的map的key
      })
    },

    // create和edit路由都调用这个方法
    saveOrUpdate() {
      // 禁用保存按钮
      this.saveBtnDisabled = true
      // 因为修改和新增操作都需要点击保存按钮，因此，需要判断当前是新增保存还是修改保存
      // 根据表单是否有回显数据 this.integralGrade.id 来判断当前是何种操作
      if (!this.integralGrade.id) {
        //在JavaScript中： 0为false，其他数值为true；undefined和null转为布尔值都是false
        // 调用新增
        this.saveData()
      } else {
        // 调用更新
        this.updateData(this.integralGrade)
      }
    },
    saveData() {
      integralGradeApi.save(this.integralGrade).then((response) => {
        // 调用integralGradeApi中的save方法，处理axios回调
        // this.$message({
        //   type: 'success',
        //   message: response.message
        // })
        this.$message.success(response.message) // 以上代码的简写形式
        this.$router.push('/core/integral-grade/list') // 保存记录之后进行路由跳转
      })
    },
    updateData() {
      integralGradeApi.updateById(this.integralGrade).then((response) => {
        this.$message.success(response)
        this.$router.push('/core/integral-grade/list')
      })
    },
  },
}
</script>

// scoped 表示当前样式只对本页面有效。
<style scoped></style>
