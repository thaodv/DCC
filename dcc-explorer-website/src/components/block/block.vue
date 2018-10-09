<template>
  <div class="row indexCon block">
      <Block :block="blockData" :loading="moreBlockLoading">
        <span slot="more" class="blockRecords">{{this.totalElements}} records found</span>
      </Block>
      <Pagination class="container pageWrap" :total="totalElements" :display="20" @pagechange="pagechange"></Pagination>
    </div>
</template>
<script>
import Block from '../../common/block'
import Pagination from '../../common/pagination'
export default {
  data(){
    return{
     blockData:[],
     totalElements:0,
     pageTotal:0,
     moreBlockLoading:false
    }
  },
  components:{Block,Pagination},
  methods:{
    block(params){
      const _this = this;
          this.$axios.get("/block",params).then((json)=>{
             if (json.systemCode == "SUCCESS") {
                if (json.businessCode == "SUCCESS") {
                  this.moreBlockLoading = false;
                   this.blockData = json.result.items;
                   this.totalElements = json.result.totalElements;
                   this.pageTotal = +json.result.totalPages;
                } else {
                   this.$toast.top(json.message)
                }
            } else {
                this.$toast.top(json.message)
                }
            })
            .catch(function (error) {
              _this.$toast.top("System is busy, please try again later")
            })
    },
    pagechange(cur){
      var params={"page":cur,"pageSize":20};
      this.moreBlockLoading = true;
      this.block(params)
    }
  },
  mounted(){
    this.moreBlockLoading = true;
     var params = {"page":1,"pageSize":20};
    this.block(params);

  }
}
</script>
<style>
.block .top-index{
  display:flex;
}
.block .panel-default>.panel-heading{
  text-align:left;
}
.blockRecords{
  margin-left:20px;
  font-size:14px;
  line-height:30px;
}
</style>


