<template>
  <div class="row contractDetail">
    <div class="row">
        <div class="container top-title-wrap search"><span class="title">Contract Name：</span><small id="block-number">{{description.name}}</small></div>
    </div>
    <div style="padding: 0 10px" class="container search">
        <div class="row">
            <div class="section-title">Description</div>
        </div>
        <div class="row section-body">
            {{description.desc}}
        </div>
        <Loading v-show="loadStatus"></Loading>
        <div class="row">
            <div class="section-title">Data query</div>
        </div>
        <div class="row section-body">
            <table class="detail-table">
                <tr>
                    <td>Contract Name：</td>
                    <td>{{description.name}}</td>
                </tr>
                <tr>
                  <td>Contract Address:</td>
                  <td>{{params}}</td>
                </tr>
                <tr>
                    <td>Query method：</td>
                    <td>{{description.methods && description.methods["en"]}}</td>
                </tr>
                <tr>
                    <td>Address:</td>
                    <td>
                      <input type="text" class="form-control" placeholder="Please enter your wallet address" v-model="userAccount" @keyup.enter="query()"/>
                      <button class="btn s-btn" @click="query()">SEARCH</button>
                    </td>
                </tr>
                <tr style="margin-bottom:10px;">
                    <td style="vertical-align:top">Search Result：</td>
                    <td class="g-result">
                      <div class="s-bg">
                        <div v-for="(val,item) in originResult" :key="item" v-if="typeof originResult !== 'string'">{{item}}：{{val}}</div>
                        <div v-else>{{originResult}}</div>
                      </div>
                    </td>
                </tr>
                <tr v-show="result">
                    <td style="vertical-align:top" >Explanation：</td>
                    <td class="g-result">
                      <div class="s-bg">
                        <div v-if="typeof result !== 'string'">
                          <div v-for="(val,item) in result" :key="item">{{item}}：{{val}}</div>
                        </div>
                        <div  v-else>{{result}}</div>
                      </div>
                    </td>
                </tr>
               
            </table>
        </div>
    </div>
</div>
</template>
<script>
import Loading from "@/common/loading"
import {description} from "./description.js"
import {timestamp} from '@/filters.js'
const abi = require('ethjs-abi');
import web3 from 'web3'
import ABI from './abi.js'
export default {
  data(){
    return{
      params:"",
      name:"",
      summary:{},
      detail:{},
      userAccount:"",
      loadStatus:false,
      maxHeight:true,
      description:"",
      originResult:"",
      result:""
    }
  },
  components:{Loading},
  methods:{
    pagechange(cur){
      this.initDetail(cur);
    },
    query(){
      let that = this;
      this.result=this.originResult=""
     if(this.userAccount && this.userAccount.length ==42){
        if(this.description.name=="IPFS Data Storage"){
         // ipfs合约需要查询三项认证的接口
          class Interface{
            constructor(description,provider,contract,i){
              this.description = description;//合约描述文件
              this.provider = provider;//网关地址
              this.abiNum = this.description.abi[i];//abi的第几个方法
              this.contract = contract;//合约地址
              this.e ={};//web3
              this.n ={};
              this.init();
            }
            //初始化合约
            init(){
              this.e = window.web3;
              this.n = (this.e = new Web3(new Web3.providers.HttpProvider(this.provider))).eth.contract(this.description.abi).at(this.contract)
            }
            // 对data进行编码
            encode(userAccount,IdentityAddress){
              let _this = this;
              try{
                 let encodeData = abi.encodeEvent(_this.abiNum,[userAccount,IdentityAddress]);
                  return encodeData;
              }catch(err){
                that.$toast.top("Query error, invalid address value.");
              }
             
            }
            // web3请求网关
            use(userAccount,IdentityAddress){
              let _this = this;
              let encodeData = _this.encode(userAccount,IdentityAddress);
              return  new Promise(function(resolve,reject){
                if(encodeData){
                   var balance =_this.e.eth.call({"data":encodeData,"to":_this.contract});
                    let decodeData =abi.decodeMethod(_this.abiNum,balance)
                    let {_userAddress,_contractAddress,_version,_cipher,_token,_iv,_digest1,_digest2,_keyHashVersion} = decodeData;
                    let res = {_userAddress,_contractAddress,_version,_cipher,_token,_iv,_digest1,_digest2,_keyHashVersion}
                    resolve(res);
                }else{
                  resolve("")
                }
               
              })
            }
          }
          let i = new Interface(this.description,'https://explorer.dcc.finance/passport-gateway/restful/contract/1/web3/ipfs_metadata','0x1dddff96d22f5f46c06fef20a903f1d5bc38d628',6);
          let a1 = i.use(this.userAccount,"0xe1a903ed503cf7388b4f5f328b372081c95ebe4f")
          let a2 = i.use(this.userAccount,"0xbbe73e925ab76bb015730651aae34db990046d9e")
          let a3 =  i.use(this.userAccount,"0x819089c21a0206d83aa2857725b6abdf21f49a15")

          Promise.all([a1,a2,a3]).then(res=>{
            that.originResult = res;
            that.result = {"Identity Data Token":res[0]["_token"],"Bankcard Data Token":res[1]["_token"],"Call Records Data Token":res[2]["_token"]};
          }).catch(res=>{
            that.result = "Query error, please try again later."
          })
        }else{
          this.$axios.get1(`https://explorer.dcc.finance/passport-gateway/restful/dcc/cert/2/${this.description.business}/getData`,{"address":this.userAccount}).then(res=>{
            that.originResult = res.result.content;
            const {digest1,digest2,expired} = res.result.content;
            let obj = {};
            if(digest1 && digest2){
              const key1 =this.description.methods.cn+" Abstract Data1";
              const key2 = this.description.methods.cn + " Abstract Data2";
              obj[key1]= digest1,obj[key2]= digest2;
            }else{
                const key1 =this.description.methods.cn+" Abstract Data";
                obj[key1]= digest1;
            }
            obj['Expire Time'] = expired>0? timestamp(expired,'yy-mm-dd') :"";
            that.result = JSON.parse(JSON.stringify(obj));
          }).catch(res=>{
            that.result = "Query error, please try again later."
          })
        }
     }else{
        this.$toast.top("The address should be 42 characters")
     }
    }
  },
  mounted(){
    let data = this.$route.params.aid.split('&');
    this.params = data[0];
    let code = data[1].split("=")[1];
    this.description = description[code]
  }
}
</script>

<style>
.contractDetail .section-body{
  line-height:24px;
  margin-bottom:20px!important;
}
.contractDetail .search{
  float:none;
  margin:0 auto 20px;
}

.contractDetail table tr td:first-child{
  width:120px;
}
.contractDetail table tr td{
  white-space: nowrap;
}
.contractDetail .form-control{
  display:inline-block;
  width:360px;
}
.contractDetail .top-title-wrap{
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.contractDetail .detail-table td{
  height:60px;
}
.contractDetail td.g-result{
  width:900px;
  height:120px;
  padding:10px 0px;
}
.contractDetail td.g-result .s-bg{
  width:100%;
  height:100%;
  min-height:110px;
  white-space: normal;
  padding-left:10px;
  background:#eaeaea;
}
.s-btn{
  background:#7875e7;
  color:#fff;
  height:35px;
  margin-top:-4px;
  outline:none;
}
.s-btn:hover, .s-btn:focus, .s-btn.focus,.btn:focus, .btn:active:focus, .btn.active:focus, .btn.focus, .btn:active.focus, .btn.active.focus{
  color:#fff;
  outline:none;
}
</style>

