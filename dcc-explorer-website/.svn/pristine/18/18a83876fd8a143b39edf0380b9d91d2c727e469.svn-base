<template>
  <div class="wrapper">
     <Header ref="dccHeader" :hdCon="msg"></Header>
      <keep-alive include="contract">
        <router-view class="indexWrapper" @change="change"></router-view>
      </keep-alive>
      <Footer></Footer>
  </div>
</template>

<script>
import Header from './header'
import Footer from './footer'
export default {
  name: 'HelloWorld',
  data () {
    return {
      msg: ''
    }
  },
  components:{Header,Footer},
  updated(){
    window.scrollTo(0,0);
  },
  methods:{
    change(val){
      this.$refs.dccHeader.searchCon(val);
    }
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
.wrapper{
  height:100%;
}
.topic{
  padding:20px;
  text-align:center;
  font-size:18px;
  color:red;
  background:#fab82b;
}
.wrapper,.indexWrapper{
  padding:0!important;
  width:100%!important;
}
.indexWrapper{
  min-height:calc(100% - 189px);
}

h1, h2 {
  font-weight: normal;
}
ul {
  list-style-type: none;
  padding: 0;
}
li {
  display: inline-block;
  margin: 0 10px;
}
a {
  color: #42b983;
}

</style>
