<template>
  <div class="row bottom-index container" style="position:relative">
        <div class="row panel panel-default" style="width:100%">
            <div class="panel-heading">
                <img src="../assets/images/transactions.svg" alt="transactions">
                <h3 class="panel-title explorer-panel-title pull-left">&nbsp;&nbsp;Transactions</h3>
                 <slot name="more"></slot>
            </div>
            <Loading v-show="loadStatus"></Loading>
            <div class="panel-body">
                <table id="table-transaction" class="table table-striped table-hover table-index" width="100%">
                    <thead>
                    <tr>
                        <th class="ellipse" title="Hash"> Hash </th>
                        <th class="ellipse" title="From"> From </th>
                        <th class="ellipse" title="To"> To </th>
                        <th class="ellipse" title="Block Height">Block Height</th>
                        <th class="ellipse" title="Amount"> Amount </th>
                        <th class="ellipse" title="Age"> Age </th>
                    </tr>
                    </thead>
                     <tbody>
                          <tr v-for="(val,index) in this.transactionData" :key="index">
                             <td>
                                <router-link :to="{path:'/block/hash/'+ val.hash}" class="ellipse" :title="val.hash">{{val.hash | trimAddress}}</router-link>
                            </td>
                            <td >
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
        </div>
</div>
</template>
<script>
import Loading from "./loading"
export default {
props:['transaction','loading'],
watch:{
    loading(val){
        this.loadStatus = val;
    },
    transaction(val){
        this.transactionData = val;
    }
},
components:{Loading},
  data(){
      return{
        transactionData:this.transaction,
        loadStatus:this.loading
       
      }
  }
}
</script>


