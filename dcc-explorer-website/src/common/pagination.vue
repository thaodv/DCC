<template>
  <nav>
    <ul class="page">
      <li :class="{'disabled': current == 1,'first':1}"><a href="javascript:;" @click="setCurrent(1)"> First </a></li>
      <li :class="{'disabled': current == 1,'previous':1}"><a href="javascript:;" @click="setCurrent(current - 1)">Prev</a></li>
      <li class="pc" v-for="(p,i) in grouplist" :class="{'active': current == p.val}" :key="i">
        <a href="javascript:;" @click="setCurrent(p.val)"> {{ p.text }} </a>
      </li>
      <li class="h5">
        <a href="javascript:;" style="font-size:14px;padding-top:9px">{{current}} of {{page}} </a>
      </li>
      <li :class="{'disabled': current == page,'next':1}"><a href="javascript:;" @click="setCurrent(current + 1)">Next</a></li>
      <li :class="{'disabled': current == page,'last':1}"><a href="javascript:;" @click="setCurrent(page)"> Last </a></li>
    </ul>
  </nav>
</template>

<script type="es6">
  export default{
    data(){
      return {
        current: this.currentPage
      }
    },
    props: {
      total: {// 数据总条数
        type: Number,
        default: 0
      },
      display: {// 每页显示条数
        type: Number,
        default: 10
      },
      currentPage: {// 当前页码
        type: Number,
        default: 1
      },
      pagegroup: {// 分页条数
        type: Number,
        default: 5,
        coerce: function (v) {
          v = v > 0 ? v : 5;
          return v % 2 === 1 ? v : v + 1;
        }
      }
    },
    computed: {
      page: function () { // 总页数
        return Math.ceil(this.total / this.display);
      },
      grouplist: function () { // 获取分页页码
        var len = this.page, temp = [], list = [], count = Math.floor(this.pagegroup / 2), center = this.current;
        if (len <= this.pagegroup) {
          while (len--) {
            temp.push({text: this.page - len, val: this.page - len});
          }
          ;
          return temp;
        }
        while (len--) {
          temp.push(this.page - len);
        }
        ;
        var idx = temp.indexOf(center);
        (idx < count) && ( center = center + count - idx);
        (this.current > this.page - count) && ( center = this.page - count);
        temp = temp.splice(center - count - 1, this.pagegroup);
        do {
          var t = temp.shift();
          list.push({
            text: t,
            val: t
          });
        } while (temp.length);
        if (this.page > this.pagegroup) {
          (this.current > count + 1) && list.unshift({text: '...', val: list[0].val - 1});
          (this.current < this.page - count) && list.push({text: '...', val: list[list.length - 1].val + 1});
        }
        return list;
      }
    },
    methods: {
      setCurrent: function (idx) {
        if (this.current != idx && idx > 0 && idx < this.page + 1) {
          this.current = idx;
          this.$emit('pagechange', this.current);
        }
      }
    }
  }
</script>
<style lang="scss">
  .page {
    overflow: hidden;
    display: table;
    // margin: 0 auto;
    /*width: 100%;*/
    height: 50px;
    float:right;
    margin-top:30px;
  .first a,  .last a{
    background-color: #e8f1ff;
  }
  li {
    float: left;
    height: 30px;
    border-radius: 5px;
    margin: 3px;
    color: #666;

  &:hover {
    background: #888;

  a {
    color: #fff;
  }

  }
  a {
    display: block;
    min-width:30px;
    // height: 30px;
    text-align: center;
    padding: 6px 10px;    
    // line-height: 30px;
    font-size: 14px;
    border-radius: 5px;
    color:#888;
    text-decoration: none
  }

  }
  .active {
    background:#9c96f5;

  a {
    color: #fff;
  }

  }
  }

</style>