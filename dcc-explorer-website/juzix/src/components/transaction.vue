<template>
  <div class="row indexCon block container">
      <Transactions :transaction="transactionData" :loading="moreBlockLoading">
        <span slot="more" class="blockRecords">{{this.totalElements}} records found</span>
      </Transactions>
      <Pagination class="container pageWrap" :total="totalElements" :display="20" @pagechange="pagechange"></Pagination>
    </div>
</template>
<script>
import Transactions from '../common/transactions'
import Pagination from '../common/pagination'
export default {
  data(){
    return{
     transactionData:[],
     totalElements:0,
     pageTotal:0,
     moreBlockLoading:false
    }
  },
  components:{Transactions,Pagination},
  methods:{
    block(params){
      const that = this;
          this.$axios.post("/transaction",params).then((json)=>{
             if (json.systemCode == "SUCCESS") {
                if (json.businessCode == "SUCCESS") {
                  this.moreBlockLoading = false;
                   this.transactionData = json.result.items;
                   this.totalElements = json.result.totalElements;
                   this.pageTotal = +json.result.totalPages;
                } else {
                  this.moreBlockLoading = false;
                    this.$toast.top(json.message)
                }
            } else {
              this.moreBlockLoading = false;
               this.$toast.top(json.message)
                }
            })
            .catch(function (error) {
                that.$toast.top("System is busy, please try again later")
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
.pageWrap{
  float:none;
  margin:0 auto;
}
</style>


