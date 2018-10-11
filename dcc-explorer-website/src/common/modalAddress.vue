<template>
  <div class="row blockSearch">
    <div class="row">
        <div class="container top-title-wrap search"><span class="title">Address：</span><small id="block-number">{{this.address}}</small></div>
    </div>
    <div style="padding: 0 10px" class="container search">
        <div class="row">
            <div class="section-title">Summary</div>
        </div>
        <div class="row section-body">
            <table class="detail-table">
                    <tr>
                        <td>Address：</td>
                        <td>
                            <span id="address2">{{this.address}}</span>
                        </td>
                    </tr>
                    <tr>
                        <td>DCC Balance：</td>
                        <td><span id="dcc-balance">{{this.dccBalance}}</span> DCC</td>
                    </tr>
                    <tr>
                        <td>No. Of Transactions：</td>
                        <td><span id="total-trade-num">{{this.totalElements | percent()}}</span></td>
                    </tr>
                    <tr>
                        <td>No. Of Transfers：</td>
                        <td><span id="total-transfer-num">{{this.transfers | percent()}}</span></td>
                    </tr>
                </table>
        </div>
        <div class="row">
            <div class="section-title">Detail</div>
        </div>
         <Loading v-show="loading"></Loading>
        <div class="row section-body">
          <div class="juzix-nav-tabs">
                    <ul id="myTab" class="nav nav-tabs">
                        <li  :class="{active:num==1}" @click="tab(1)"><a data-toggle="tab">Transactions</a></li>
                        <li :class="{active:num==2}"  @click="tab(2)"><a data-toggle="tab" id="tab-transfer">Transfers</a></li>
                    </ul>
            </div>
            <!-- Transactions -->
             <div class="tab-content" v-show="num==1">
                <div class="tab-pane fade in active" id="trade"> 
                    <table id="table-trade" class="table table-striped table-hover table-index table-nav">
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
                        <tr v-for="(val,item) in detail.items" :key="item" v-show="totalElements >0">
                          <td>
                              <router-link :to="{path:'/block/hash/'+ val.hash}" class="ellipse" :title="val.hash">{{val.hash | trimAddress}}</router-link>
                            </td>
                            <td v-show="!from">
                              <router-link :to="{path:'/transaction/address/'+ val.fromAddress}"  class="ellipse" :title="val.fromAddress">{{val.fromAddress | trimAddress}}</router-link>
                            </td>
                            <td class="ellipse" :title="val.fromAddress" v-show="from">
                              {{val.fromAddress | trimAddress}}
                            </td>
                            <td v-show="from">
                              <router-link :to="{path:'/transaction/address/'+val.toAddress}" class="ellipse" :title="val.toAddress">{{val.toAddress | trimAddress}}</router-link>
                            </td>
                            <td class="ellipse" :title="val.toAddress" v-show="!from">
                              {{val.toAddress | trimAddress}}
                            </td>
                            <td>
                              <router-link :to="{path:'/block/search/'+val.blockNumber}" class="ellipse" :title="val.blockNumber">{{val.blockNumber}}</router-link>
                            </td>
                            <td class="ellipse">{{val.tokenValue | wei}} DCC</td>
                            <td class="ellipse" :title="val.blockTimestamp | dateDiff">{{val.blockTimestamp | dateDiff()}}</td>
                        </tr>
                        <tr v-show="totalElements===0">
                          <td valign="top" colspan="6" class="dataTables_empty">No data available in table</td>
                        </tr>
                      </tbody>
                    </table>
                </div>
                 <Pagination class="pageWrap" :total="detail.totalElements" :display="10" @pagechange="pagechange"></Pagination>
              </div>
              <!--Transfers  -->
              <div  class="tab-content" v-show="num==2">
                <div class="tab-pane fade in active" id="trade"> 
                    <table id="table-trade" class="table table-striped table-hover table-index table-nav">
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
                        <tr v-for="(val,item) in transferData.items" :key="item" v-show="transfers>0">
                          <td>
                              <router-link :to="{path:'/block/hash/'+ val.transactionHash}" class="ellipse" :title="val.transactionHash">{{val.transactionHash | trimAddress}}</router-link>
                            </td>
                            <td v-show="!from">
                              <router-link :to="{path:'/transaction/address/'+ val.fromAddress}"  class="ellipse" :title="val.fromAddress">{{val.fromAddress | trimAddress}}</router-link>
                            </td>
                            <td class="ellipse" :title="val.fromAddress" v-show="from">
                              {{val.fromAddress | trimAddress}}
                            </td>
                            <td v-show="from">
                              <router-link :to="{path:'/transaction/address/'+val.toAddress}" class="ellipse" :title="val.toAddress">{{val.toAddress | trimAddress}}</router-link>
                            </td>
                            <td class="ellipse" :title="val.toAddress" v-show="!from">
                              {{val.toAddress | trimAddress}}
                            </td>
                            <td>
                              <router-link :to="{path:'/block/search/'+val.blockNumber}" class="ellipse" :title="val.blockNumber">{{val.blockNumber}}</router-link>
                            </td>
                            <td class="ellipse">{{val.value | wei}} DCC</td>
                            <td class="ellipse" :title="val.blockTimestamp | dateDiff">{{val.blockTimestamp | dateDiff()}}</td>
                        </tr>
                        <tr v-show="transfers===0">
                          <td valign="top" colspan="6" class="dataTables_empty">No data available in table</td>
                        </tr>
                      </tbody>
                    </table>
                </div>
                  <Pagination class="pageWrap" :total="transferData.totalElements" :display="10" @pagechange="pagechange"></Pagination>
              </div>
        </div>
    </div>
</div>
</template>
<script>
import Pagination from "./pagination"
import Loading  from "./loading"
import tab1 from './transactions.vue'
export default {
  data(){
    return{
      address:"",
      totalElements:"",
      transfers:"",
      detail:{},
      transferData:{},
      dccBalance:0,
      from:false,
      loading:false,
      num:1
    }
  },
  components:{Pagination,Loading},
  watch:{
    address(val){
      this.address = val;
      this.update();
    },
    $route(){
      this.num =1;
      this.totalElements = "";
      this.transfers = "";
      this.detail = this.transferData= {};
      this.dccBalance = "";
      this.switch1="No data available in table"
      var aid = this.$route.params.aid;
      this.address == aid ? this.update(): this.address = aid;
    }
  },
  methods:{
    update(){
        this.loading =true;
        this.initData();
        this.balance();
        this.initDetail(); 
    },
    initData(page=1){
      const _this = this;
       var params = {"address":this.address,"transactionType": 'TRADE',"page": page,"pageSize": 10};
      this.$axios.post("/transaction/",params).then((json)=>{
        if(json.businessCode == "SUCCESS"){
          if(json.systemCode == "SUCCESS"){
            this.loading = false;
            this.totalElements = json.result.totalElements;
            this.detail = json.result;
            if(json.result.items.length >0 && this.address == json.result.items[0].fromAddress){
              this.from = true;
            }else{
              this.from = false;
            }
          }else {
                   this.$toast.top(json.message)
                }
            } else {
                this.$toast.top(json.message)
                }
            })
            .catch(function (error) {
              _this.$toast.top("The system is busy, please try again later")
            })
    },
    initDetail(page=1){
       const _this = this;
       var params = {"address":this.address,"page": page,"pageSize": 10};
      this.$axios.post("/tokenTransfer/dcc",params).then((json)=>{
        if(json.businessCode == "SUCCESS"){
          if(json.systemCode == "SUCCESS"){
             this.loading = false;
            this.transferData =  json.result;
            this.transfers = json.result.totalElements;
          }else {
               this.$toast.top(json.message)
                }
          } else {
              this.$toast.top(json.message)
              }
          })
          .catch(function (error) {
              _this.$toast.top("The system is busy, please try again later")
          })
    },
    balance(){
      const _this = this;
       this.$axios.get("/tokenBalance/dcc/"+this.address).then((json)=>{
        if(json.businessCode == "SUCCESS"){
          if(json.systemCode == "SUCCESS"){
             var d = json.result;
             const BIGDECIMAL_18_ZERO = new BigNumber(10).pow(18);
             let balance = new BigNumber(d).dividedBy(BIGDECIMAL_18_ZERO).toFixed();
             this.dccBalance = balance;
          }else {
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
      this.loading = true;
      if(this.num==1){
        this.initData(cur);
      }else{
        this.initDetail(cur);
      }
      
    },
    tab(val){
      this.num = val;
    }
  },
  mounted(){
    this.loading=true;
    this.address = this.$route.params.aid.split("?time")[0];
  }
}
</script>

<style>
.blockSearch .search{
  float:none;
  margin:0 auto 20px;
}
</style>

