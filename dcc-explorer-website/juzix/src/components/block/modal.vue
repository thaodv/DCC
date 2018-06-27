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
                    <td>No Of Transactions：</td>
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
                        <router-link :to="{path:'/block/search/'+val.blockNumber}" class="ellipse" :title="val.blockNumber">{{val.blockNumber}}</router-link>
                      </td>
                      <td class="ellipse">{{val.dccValue | wei}} DCC</td>
                      <td class="ellipse" :title="val.blockTimestamp | dateDiff">{{val.blockTimestamp | dateDiff}}</td>
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
      time:1,//第一次舰艇
      loadStatus:true,
      maxHeight:true
    }
  },
  components:{Pagination,Loading},
  watch:{
    params(val){
      this.time++;
      if(this.time > 2){
        this.params = val
        this.init();
        this.$router.replace({"path":'/block/search/'+val})
      }
    },
    $route(){
      this.params = this.$route.params.aid;
    }
  },
  methods:{
    initData(resolve){
      const that = this;
      this.$axios.get("/block/"+this.params).then((json)=>{
        if(json.businessCode == "SUCCESS"){
          if(json.systemCode == "SUCCESS"){
            this.summary = json.result;
            resolve();
          }else {
             this.$toast.top(json.message)
                }
            } else {
               this.$toast.top(json.message)
                }
            })
    },
    initDetail(page,resolve){
      const _this = this;
      var params ={"blockNumber":this.params,"page":page,"pageSize":10}
      this.$axios.get("/transaction",params).then((json)=>{
        if(json.businessCode == "SUCCESS"){
          if(json.systemCode == "SUCCESS"){
            this.detail =  json.result;
             resolve();
          }else {
                  this.$toast.top(json.message)
                }
          } else {
              this.$toast.top(json.message)
              }
          })
    },
    initNav(){
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
    
    },
    add(){
      ++this.params;
    },
    pagechange(cur){
      this.initDetail(cur);
    },
    init(){
        const _this = this;
        this.loadStatus = true;
          var page = 1;
          this.initNav();//判断height是否为最大值或最小值
          let a = new Promise(function(resolve,reject){_this.initData(resolve,reject)})
          let b = new Promise(function(resolve,reject){_this.initDetail(page,resolve,reject)})
          a.then(function(){
            _this.loadStatus = false;
          },function(){
            _this.loadStatus = false;
          })
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

