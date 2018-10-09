<template>
  <div class="top-index container" style="position:relative">
          <div class="panel panel-default" style="width:100%">
              <div class="panel-heading">
                 <img class="block_img" src="../assets/images/block.svg" alt="">
                  <h3 class="panel-title explorer-panel-title pull-left">&nbsp;&nbsp;Blocks</h3>
                  <slot name="more"></slot>
              </div>
              <Loading v-show="loadStatus"></Loading>
              <div class="panel-body">
                  <table id="table-block" class="table-responsive table table-striped table-hover table-index">
                      <thead>
                      <tr>
                          <th> Height </th>
                          <th> Hash </th>
                          <th> Txn </th>
                          <th> Age </th>
                      </tr>
                      </thead>
                       <tbody>
                          <tr v-for="(val,index) in this.blockData" :key="index">
                            <td>
                              <router-link :to="{path:'/block/search/'+val.blockNumber}">{{val.blockNumber}}</router-link></td>
                            <td :title="val.hash">{{val.hash}}</td>
                            <!-- <td :title="val.hash" class="h5Menu">{{val.hash | trimAddress}}</td> -->
                            <td>{{val.transactionCount}}</td>
                            <td>{{val.blockTimestamp | dateDiff}}</td>
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
props:['block','loading'],
watch:{
    block(val){
       this.blockData = this.block;
    },
    loading(val){
        this.loadStatus = val;
    }
},
  data(){
      return{
          blockData:this.block,
          loadStatus:this.loading
      }
  },
  components:{Loading}
}
</script>


