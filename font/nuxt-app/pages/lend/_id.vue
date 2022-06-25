<template>
  <div>
    <h2>动态路由</h2>
    这个投资产品为：{{ id }}
  </div>
</template>

<script>
export default {
  data() {
    return {
      id: null,
    }
  },

  created() {
    this.id = this.$route.params.id
  },
}
</script>
