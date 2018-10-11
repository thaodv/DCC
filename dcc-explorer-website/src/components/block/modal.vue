<template>
  <div class="row blockSearch">
    <div class="row">
        <div class="container top-title-wrap search"><span class="title">Block Height：</span><small id="block-number">{{this.params}}</small></div>
    </div>
    <div style="padding: 0 10px" class="container search">
        <div class="row">
            <div class="section-title">Summary</div>
        </div>
        <div class="row section-body">
            <table class="detail-table">
                <tr>
                    <td>Height：</td>
                    <td>
                        <a id="block-pre" v-show="params>0" class="block-nav block-nav-pre" @click="dec()"><img src="../../assets/images/toLeft.svg" alt="-"></a>
                        <span id="block-number2">{{this.params}}</span>
                        <a id="block-next" v-show="maxHeight" class="block-nav block-nav-next" @click="add()"><img src="../../assets/images/toRight.svg" alt="+"></a>
                    </td>
                </tr>
                <tr>
                    <td>Hash：</td>
                    <td><span id="block-hash">{{this.summary.hash}}</span></td>
                </tr>
                <tr>
                    <td>TimeStamp：</td>
                    <td><span id="block-timestamp">{{this.summary.blockTimestamp | format}}</span></td>
                </tr>
                <tr>
                    <td>No. Of Transactions：</td>
                    <td><span id="block-txn">{{this.summary.transactionCount | percent()}}</span></td>
                </tr>
            </table>
        </div>
        <Loading v-show="loadStatus"></Loading>
        <div class="row">
            <div class="section-title">Detail</div>
        </div>
        <div class="row section-body">
            <div class="section-text">Transactions：</div>
            <table id="table-transaction" class="table table-striped table-hover table-index table-index-border">
                <thead>
                <tr>
                    <th> Hash </th>
                    <th> From </th>
                    <th> To </th>
                    <th> Block Height </th>
                    <th> Amount </th>
                    <th> Age </th>
                </tr>
                </thead>
                <tbody>
                  <tr v-for="(val,item) in detail.items" :key="item">
                     <td>
                        <router-link :to="{path:'/block/hash/'+ val.hash}" class="ellipse" :title="val.hash">{{val.hash | trimAddress}}</router-link>
                      </td>
                      <td>
                        <router-link :to="{path:'/transaction/address/'+ val.fromAddress}"  class="ellipse" :title="val.fromAddress">{{val.fromAddress | trimAddress}}</router-link>
                      </td>
                      <td>
                        <router-link :to="{path:'/transaction/address/'+val.toAddress}" class="ellipse" :title="val.toAddress">{{val.toAddress | trimAddress}}</router-link>
                      </td>
                      <td>
                        {{val.blockNumber}}
                      </td>
                      <td class="ellipse">{{val.tokenValue | wei}} DCC</td>
                      <td class="ellipse" :title="val.blockTimestamp | dateDiff">{{val.blockTimestamp | dateDiff}}</td>
                  </tr>
                  <tr v-show="this.summary.transactionCount===0">
                          <td valign="top" colspan="6" class="dataTables_empty">No data available in table</td>
                  </tr>
                </tbody>
            </table>
        </div>
        <Pagination class="pageWrap" :total="detail.totalElements" :display="10" @pagechange="pagechange"></Pagination>
    </div>
</div>
</template>
<script>
import Pagination from "../../common/pagination"
import Loading from "@/common/loading"
export default {
  data(){
    return{
      params:"",
      summary:{},
      detail:{},
      loadStatus:true,
      maxHeight:true,
    }
  },
  components:{Pagination,Loading},
  watch:{
    params(val){
        this.params = val
        this.$router.push({"path":'/block/search/'+val})
    },
    $route(){
      this.summary={};
      this.detail={};
      var aid = this.$route.params.aid;
      this.params == aid ?this.init() : this.params = aid;
    }
  },
  methods:{
    initData(resolve){
      const that = this;
      this.$axios.get("/block/"+this.params).then((json)=>{
        if(json.businessCode == "SUCCESS"){
          if(json.systemCode == "SUCCESS"){
            this.summary = json.result;
          }else {
             this.$toast.top(json.message)
                }
            } else {
               this.$toast.top(json.message)
                }
            }).catch(function(error) {
               that.$toast.top("System is busy, please try again later")
          })
    },
    initDetail(page,resolve){
      const _this = this;
      var params ={"blockNumber":this.params,"page":page,"pageSize":10}
      this.$axios.get("/transaction",params).then((json)=>{
        if(json.businessCode == "SUCCESS"){
          if(json.systemCode == "SUCCESS"){
             this.loadStatus= false;
            this.detail =  json.result;
          }else {
                  this.$toast.top(json.message)
                }
          } else {
              this.$toast.top(json.message)
              }
          }).catch(function(error) {
               _this.$toast.top("System is busy, please try again later")
          })
    },
    initNav(){
      const _this = this;
       this.$axios.get("/block/number").then((json)=>{
        if(json.businessCode == "SUCCESS"){
          if(json.systemCode == "SUCCESS"){
           
           if(this.params == json.result){
             this.maxHeight = false;
           }else{
             this.maxHeight = true;
           } 
          }else {
                  this.$toast.top(json.message)
                }
          } else {
              this.$toast.top(json.message)
              }
          })
    },
    dec(){
      --this.params;
      this.$router.push({"path":'/block/search/'+this.params})
    
    },
    add(){
      ++this.params;
      this.$router.push({"path":'/block/search/'+this.params});
    },
    pagechange(cur){
      this.initDetail(cur);
    },
    init(){
        const _this = this;
        this.loadStatus = true;
        var page = 1;
        const asyncFun =async function(){
          await _this.initNav();//判断height是否为最大值或最小值
          await _this.initData()
          await _this.initDetail(page)
        }
        asyncFun();
          
      }
  },
  mounted(){
    this.params = this.$route.params.aid;
    this.init();
  }
}
</script>

<style>
.blockSearch .search{
  float:none;
  margin:0 auto 20px;
}
</style>

