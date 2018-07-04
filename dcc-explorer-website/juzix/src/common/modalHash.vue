<template>
  <div class="blockSearch">
    <div class="row">
        <div class="container top-title-wrap search"><span class="title">Transaction：</span><small id="block-number" :title="params">{{this.params}}</small></div>
    </div>
    <div style="padding: 0 10px" class="container search detail-section-wrap">
        <div class="row">
            <div class="section-title">Summary</div>
        </div>
        <div class="section-body">
            <table class="detail-table">
                <tr v-show="false">
                    <td>Height：</td>
                    <td>
                        <a id="block-pre" class="block-nav block-nav-pre" @click="dec()"></a>
                        <span id="block-number2">{{this.params}}</span>
                        <a id="block-next" class="block-nav block-nav-next" @click="add()"></a>
                    </td>
                </tr>
                <tr>
                    <td>Hash：</td>
                    <td><span id="block-hash">{{this.summary.hash }}</span></td>
                </tr>
                <tr>
                    <td>Status：</td>
                    <td><span id="block-timestamp">Success</span></td>
                </tr>
                  <tr>
                    <td>Block Height：</td>
                    <td><router-link id="block-timestamp" :to="{path:'/block/search/'+this.summary.blockNumber}">{{this.summary.blockNumber}}</router-link></td>
                </tr>
                <tr>
                    <td>From：</td>
                    <td><router-link id="block-timestamp" :to="{path:'/transaction/address/'+this.summary.fromAddress}">{{this.summary.fromAddress}}</router-link></td>
                </tr>
                <tr>
                    <td>To：</td>
                    <td><router-link id="block-timestamp" :to="{path:'/transaction/address/'+this.summary.toAddress}">{{this.summary.toAddress}}</router-link></td>
                </tr>
                <tr>
                    <td>Amount：</td>
                    <td><span id="block-txn">{{this.summary.dccValue | wei}} DCC</span></td>
                </tr>
                 <tr>
                    <td>DCC Transfer：</td>
                    <td><span v-show="this.detail.fromAddress" id="block-txn">{{this.summary.dccValue | wei}} DCC from 
                      <router-link  :to="{path:'/transaction/address/'+this.detail.fromAddress}">{{this.detail.fromAddress | trimAddress}}</router-link> to <router-link :to="{path:'/transaction/address/'+this.detail.toAddress}">{{this.detail.toAddress | trimAddress}}</router-link>
                      </span></td>
                </tr>
                 <tr>
                    <td>TimeStamp：</td>
                    <td><span id="block-txn">{{this.summary.blockTimestamp | format}}</span></td>
                </tr>
            </table>
        </div>
        <div class="row detail-section-wrap">
            <div class="row section-title">Detail</div>
            <div class="row section-body">
                <table class="detail-table">
                    <tr>
                        <td style="vertical-align: top">Input Data：</td>
                        <td id="td-table-input-data">
                            <table class="table-input-data">
                                <tr v-if="this.detail.name"><td>Name: </td><td id="input-data-name">{{this.detail.name}}</td></tr>
                                <tr v-if="this.detail.type"><td>Types: </td><td id="input-data-types">{{this.detail.type}}</td></tr>
                                <tr>
                                <td style="vertical-align: top">Inputs: </td>
                                <td id="input-data-params" >
                                   <div v-for="(val,i) in detail.inputs" :key="i" style="white-space:normal">{{"["+(i+1)+"]"}} {{val}}</div>
                                </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                </table>
            </div>
    </div>
    </div>
</div>
</template>
<script>
const web3 = require('web3');
const abiDecoder = require('abi-decoder');
export default {
  data(){
    return{
      params:"",
      summary:{},
      detail:{
          "name":"",
          "type":"",
          "inputs":[],
          "fromAddress":"",
          "toAddress":""
      }
    }
  },
  methods:{
    initData(){
        const that = this;
      this.$axios.get("/transaction/"+this.params).then((json)=>{
        if(json.businessCode == "SUCCESS"){
          if(json.systemCode == "SUCCESS"){
            this.summary = json.result;
          }else {
                this.$toast.top(json.message)
                }
            } else {
                this.$toast.top(json.message)
                }
            })
            .catch(function (error) {
                that.$toast.top("System is busy, please try again later");
            })
      this.initDetail();
      this.fetchInputData();
    },
    initDetail(){
        const that = this;
      var data = {"page": 1, "pageSize": 99999999, "transactionHash": this.params}
      this.$axios.post("/tokenTransfer/dcc",data).then((json)=>{
        if(json.businessCode == "SUCCESS"){
          if(json.systemCode == "SUCCESS"){
              if(json.result.items.length >0){
                this.detail.fromAddress =json.result.items[0].fromAddress;
                this.detail.toAddress = json.result.items[0].toAddress;
              }
          }else {
                this.$toast.top(json.message)
                }
          } else {
              this.$toast.top(json.message)
              }
          })
          .catch(function(error) {
               that.$toast.top("System is busy, please try again later")
          })
    },
    fetchInputData(){
        const that = this;
        this.$axios.get("/transaction/data/"+this.params).then((json)=>{
        if(json.businessCode == "SUCCESS"){
          if(json.systemCode == "SUCCESS"){
            let t = json.result;
            if(t.abi){
                abiDecoder.addABI(JSON.parse(t.abi));
                //获取input name值和transfer type值
                const decodedData = abiDecoder.decodeMethod(t.inputData);
                this.detail["name"] = decodedData.name;
                const type = decodedData.params.map((val)=>{
                        return val.type
                 })
                this.detail["type"] = type.join(',');
                 if (t.inputData) {
                      let onlyParams = t.inputData.substring(10);
                      let arr = this.chunkString(onlyParams, 64);
                     this.detail["inputs"] = arr;
                  }
            }else{
                this.detail.inputs.push(t.inputData);
                
            }  

          }else {
               this.$toast.top(json.message)
                }
          } else {
               this.$toast.top(json.message)
              }
          })
          .catch(function (error) {
               that.$toast.top("System is busy, please try again later")
          })
    },
    chunkString(str, length){
        return str.match(new RegExp('.{1,' + length + '}', 'g'));
    }

  },
  mounted(){
    this.params = this.$route.params.aid;
    this.initData();
  }
}
</script>

<style>
.blockSearch .search{
  float:none;
  margin:0 auto 20px;
}
</style>

