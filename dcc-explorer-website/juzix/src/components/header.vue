<template>
   <div class="row navbar-main">
       <div class="col-3 col-sm-2" >
            <a class="navbar-brand" href="index.html">
                <img class="img-logo" src="../assets/images/logo白.svg"/>
            </a>
       </div>
       <div class="col-6 col-sm-10 row pcMenu">
           <div class="navbar-items col-sm-9 col-md-8">
                    <router-link to="/index" active-class="hello">Home</router-link>
                    <router-link to="/block" active-class="hello">Blocks</router-link>
                    <router-link to="/transaction" active-class="hello">Transactions</router-link>
                    <a href="http://open.dcc.finance" target="_blank" active-class="hello">Open Platform</a>
            </div>
            <div class="navbar-search col-sm-3 col-md-4">
                <input type="text" id="txt-navbar-search" placeholder="Search by Address / Txhash / Block" @keyup.enter="searchCon()" v-model="search" />
                <div class="icon-search"  @click="searchCon()"></div>
            </div>
       </div>
       <div class="col-6 col-sm-10 row h5Menu" v-show="navStatus">
           <div class="navbar-items col-sm-9 col-md-8" @click="navStatus=!navStatus">
                    <router-link to="/" active-class="hello" exact>Home</router-link>
                    <router-link to="/block" active-class="hello" >Blocks</router-link>
                    <router-link to="/transaction" active-class="hello">Transactions</router-link>
                    <a href="http://open.dcc.finance" target="_blank" active-class="hello">Open Platform</a>
            </div>
            <div class="navbar-search col-sm-3 col-md-3">
                <input type="text" id="txt-navbar-search" placeholder="Search by Address / Txhash / Block" @keyup.enter="searchCon()" v-model="search" />
                <div class="icon-search"  @click.enter="searchCon()"></div>
            </div>
       </div>
       <div style="position:absolute;top:22px;right:15px;font-size:22px;color:#fff" class="h5Menu" @click="navStatus=!navStatus"><span class="glyphicon glyphicon-align-justify" aria-hidden="true"></span></div>
    </div>
  
</template>
<script>
export default {
    data(){
        return {
            navStatus:false,
            column:false,
            search:"",
        }
    },
    methods:{
        searchCon(params){
        this.navStatus =false;
        this.search= params?params:this.search;
        const that = this;
        this.$axios.get("/search/"+this.search).then((json)=>{
            this.checkResult(json, function(result) {
                    that.search = "";
                    switch (result.type) {
                        case 'BLOCK': that.$router.push({"path":'/block/search/'+result.data.blockNumber}); break;
                        case 'TRANSACTION':that.$router.push({"path":'/block/hash/'+result.data.hash}) ; break;
                        case 'ADDRESS': that.$router.push({"path":'/transaction/address/'+result.data}) ; break;
                    }
                }, function(res) {
                     that.search = "";
                    that.$router.push({"path":'/err/'+params}) ;
                })
        })
        },
        checkResult(result, success, fail){
            if (result.businessCode === 'SUCCESS' && result.systemCode === 'SUCCESS') {
                success(result.result);
            } else {
                fail(result);
            }
        }
    }
   
}
</script>
<style>
.hello{
    color: #4cc4f9!important;
}
</style>


