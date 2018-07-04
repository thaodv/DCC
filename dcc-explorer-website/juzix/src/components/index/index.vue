<template>
  <div class="container">
    <!-- banner部分 -->
    <IndexBanner></IndexBanner>
    <div class="row indexCon">
      <!-- block部分 -->
    <ConTop :block="blockData" :loading="blockLoading">
      <router-link slot="more" to="block" class="indexMore">More</router-link>
    </ConTop>
    <ConMiddle :supplyDcc="supplyDcc" :totalDcc="totalDcc"></ConMiddle>
    <ConBottom :transaction="transactionData" :loading="transactionsLoading" class="container">
      <router-link slot="more" to="transaction"  class="indexMore">More</router-link>
    </ConBottom> 
    </div>
  </div>
</template>
<script>
import IndexBanner from './indexBanner'
import ConTop from '../../common/block'
import ConMiddle from './conMiddle'
import ConBottom from '../../common/transactions'
import abi from '@/untils/abi'
import web3 from 'web3'
export default {
  name:"index",
  data(){
      return{
        blockData:[],
        transactionData:[],
        blockLoading:false,
        transactionsLoading:false,
        supplyDcc:{"transactionPerDay":0,"dccTotalSupply":0},
        totalDcc:0,
        home:{}//定时器
      }
  },
  methods:{
    block(){
          this.$axios.get("/block",{"pageSize":5}).then((json)=>{
             if (json.systemCode == "SUCCESS") {
                if (json.businessCode == "SUCCESS") {
                  this.blockLoading = false;
                   this.blockData = json.result.items;
                } 
              } 
            })
    },
    transactions(){
     const that = this;
      this.$axios.get("/transaction",{"pageSize":5}).then((json)=>{
            if (json.systemCode == "SUCCESS") {
              if (json.businessCode == "SUCCESS") {
                this.transactionsLoading = false;
                  this.transactionData = json.result.items;

              }
            } 
          })
    },
    supply(){
        const _this = this;
        this.$axios.get("/statistics/dcc").then((json)=>{
          if (json.systemCode == "SUCCESS") {
            if (json.businessCode == "SUCCESS") {
              this.supplyDcc = json.result;
            } 
          }
        })
       
        var t = "0xffa93aacf49297d51e211817452839052fdfb961",
        e = window.web3,
        n = (e = new Web3(new Web3.providers.HttpProvider("https://mainnet.infura.io/"))).eth.contract(abi).at(t);
        new Promise(function(resolve,reject){
          var balance =n.balanceOf.call("0xaCA094cfa0e1E635743f3B2aA88dC2314Df2645B");
          resolve(balance);
        }).then(function(balance){
            var s = new BigNumber(balance);
            var total = s.toString(10);
            var num = e.fromWei(total+"","ether");
           _this.totalDcc = num;
        })
    },
    updateData(){
      const _this = this;
      this.home =setInterval(function(){
        _this.blockLoading = true;
        _this.transactionsLoading = true;
        _this.block();
        _this.transactions();
        _this.supply();
      },30000)
      
     }

  },
  components:{IndexBanner,ConTop,ConMiddle,ConBottom},
  mounted(){
    this.blockLoading = true;
    this.transactionsLoading = true;
    this.block();
    this.transactions();
    this.updateData();
    this.supply();
  },
  destroyed(){
      clearInterval(this.home);
  }
}
</script>

