<template>
  <div class="row indexCon contracts ">
      <div class="row panel panel-default container">
            <div class="panel-heading">
                <img class="contracts_img" src="@/assets/images/contracts.png" alt="Contracts">
                <em class="panel-title explorer-panel-title">&nbsp;&nbsp;Contracts</em>
                <span class="blockRecords">{{this.totalElements}} records found</span>
            </div>
            <Loading v-show="loadStatus"></Loading>
            <div class="panel-body">
                <table id="table-transaction" class="table table-striped table-hover table-index" width="100%">
                    <thead>
                    <tr>
                        <th class="ellipse" title="Hash"> Contract Address </th>
                        <th class="ellipse" title="From"> Contract name </th>
                        <th class="ellipse" title="To"> Register Time </th>
                    </tr>
                    </thead>
                     <tbody>
                          <tr v-for="(val,index) in this.tableData" :key="index">
                             <td>
                                <router-link :to="{path:'/contracts/detail/'+ val.address+'&name='+val.code}" class="ellipse" :title="val.hash">{{val.address | trimAddress}}</router-link>
                            </td>
                            <td class="ellipse">{{val.name}}</td>
                            <td class="ellipse">{{val.time}}</td>
                          </tr>
                      </tbody>
                </table>
            </div>
        </div>
    </div>
</template>
<script>
import Pagination from '../../common/pagination'
import Loading from "@/common/loading"
import {description} from './description.js'
export default {
  name:"contract",
  data(){
    return{
     tableData:[],
     totalElements:0,
     pageTotal:0,
     moreBlockLoading:false,
     loadStatus:false
    }
  },
  components:{Loading,Pagination},
  methods:{
    init(params){
      const that = this;
      this.$axios.get("/contract/info").then((json)=>{
          if (json.systemCode == "SUCCESS") {
            if (json.businessCode == "SUCCESS") {
              this.moreBlockLoading = false;
              let res = json.resultList;
              res.forEach(val => {
                val.name = description[val.code].name;
                val.time = description[val.code].time;
              });
                this.tableData = res;
                this.totalElements = res.length;
            } else {
              this.moreBlockLoading = false;
              this.$toast.top(json.message)
            }
        } else {
          this.moreBlockLoading = false;
            this.$toast.top(json.message)
            }
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
    this.init(params);

  }
}
</script>
<style>
.wrapper .contracts .panel-default>.panel-heading{
  text-align:left;
}
.contracts_img{
  height:25px;
}
.contracts_img+em{
  vertical-align: middle;
}
.contracts .blockRecords{
vertical-align: bottom;
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


