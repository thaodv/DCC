<template>
  <div class="row middle-index">
    <div class="col-md-10 row" style="margin:0 auto">
      <div class="col-sm-12 col-md-4 panel panel-default middlePerDay">
        <div class="panel-heading">Transactions per day</div>
        <div class="panel-body">
          <em v-for="(val,i) in perDay" :key="i">{{val}}</em>
        </div>
        <span class="cmNotice smallsize-font">Number of transactions in the last 24 hours.</span>
      </div>
       <div class="col-sm-12 col-md-4 panel panel-default middleDcc">
         <div class="panel-heading">Mainnet DCC Circulation</div>
          <span>{{supplyDcc.dccTotalSupply | percent()}}</span>
       </div>
        <div class="col-sm-12 col-md-4 panel panel-default middleDcc">
          <div class="panel-heading">Locked DCC on Ethereum</div>
          <span>{{totalDcc | percent()}}</span>
          <span class="cmNotice smallsize-font"><a href="https://etherscan.io/token/0xffa93aacf49297d51e211817452839052fdfb961?a=0xaca094cfa0e1e635743f3b2aa88dc2314df2645b" target="_blank">View in Etherscan</a></span>
        </div>
    </div>
  </div>
</template>
<script>
export default {
  props:['supplyDcc',"totalDcc"],
  computed:{
    perDay(){
      return (this.supplyDcc.transactionPerDay+'').split('');
    }
  }
}
</script>


