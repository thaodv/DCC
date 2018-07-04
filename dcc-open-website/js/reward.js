$(function(){
  var reward ={
    elRanking:document.getElementById('con'),
    elTime:document.getElementById("rewardTime"),
    elFa:document.getElementsByClassName("fa"),
    ABI:[],
    ranking:[{"ID":'0xbbe...6d9e',"value":600,"dcc":500.00},{"ID":'0xbbe...6d9e',"value":600,"dcc":500.00},{"ID":'0xbbe...6d9e',"value":600,"dcc":500.00},{"ID":'0xbbe...6d9e',"value":600,"dcc":500.00},{"ID":'0xbbe...6d9e',"value":600,"dcc":500.00}],
    init:function(){
      this.rankingReq();
      this.time();
      this.loop() //开启定时器
    },
    loop:function(){
      var that= this;
      clearInterval(timer);
      var timer = setInterval(function(){
        var myDate = new Date();
        var hh =  myDate.getHours();
        var min =  myDate.getMinutes();
        var seconds= myDate.getSeconds();
        if(hh == 0 && min == 0 && seconds == 0){
          that.time();
          that.rankingReq();
          clearInterval(timer);
          setInterval(function(){
            that.time();
            that.rankingReq()
          }, 24 * 60 * 60 * 1000);
        }
      },1000)
    },

    rankingReq:function(){
      // var a = "http://10.65.209.21:9416/dcc-oa-frontend";
      var a = "http://10.65.178.50:9413/dcc-oa-frontend";
      // var a = "http://open.dcc.finance/dcc-oa-frontend"
      "funcopen.dcc.finance" === window.location.host && (a = "http://10.65.178.50:9413/dcc-oa-frontend"),
      "open.dcc.finance" === window.location.host && (a = "http://open.dcc.finance/dcc-oa-frontend")
      $.ajax({
        url:a+"/marketing/rewardLog/getStatisticsInfo",
        method: "post",
        success:function(json){
          if(json.systemCode == "SUCCESS" && json.businessCode == "SUCCESS"){
            var data = json.result;
            var s1 = new BigNumber(data.totalAmount).dividedBy(1e18).toString(10);
            var s2 = new BigNumber(data.yesterdayAmount).dividedBy(1e18).toString(10);
            reward.elFa[0].innerHTML = per(s1,4) + " DCC";
            reward.elFa[1].innerHTML = per(s2,4) + " DCC";
            reward.elFa[2].innerHTML = per(data.yesterdayPersonNumber,0);
          }
        },
        error:function(){
          // alert("发生错误,请稍后重试")
          console.log("发生错误,请稍后重试")
        }
      })
      $.ajax({
        url:a+"/marketing/eco/queryRankList",
        method: "post",
        success:function(json){
          if(json.systemCode == "SUCCESS" && json.businessCode == "SUCCESS"){
            var resultList = json.resultList;
            if(resultList && resultList.length>0){
              var newList = resultList.map(function(val,i){
                return {"tip":val.address,"address":trimAddress(val.address,5),"score":per(val.score,0),"amount":per(new BigNumber(val.amount).dividedBy(1e18).toString(10),4)}

              })
              json = newList;
            }
            var data ={"resultList":json}
            var html = template("js-tmp",data)
            reward.elRanking.innerHTML = html;
        
          }
        },
        error:function(){
          console.log("发生错误,请稍后重试")
        }
      })
    },
    
    time:function(){
      var myDate = new Date();
      var yy =  myDate.getFullYear();        //获取当前年份(2位)
      var mm =  myDate.getMonth()+1;    //获取完整的年份(4位,1970-????)
      var dd =  myDate.getDate();       //获取当前月份(0-11,0代表1月)

      var clock =yy + "-";
      if(mm < 10)clock += "0";
        clock += mm + "-";
      if(dd < 10)clock += "0";
      clock += dd + " ";
      this.elTime.innerHTML = clock;
    },
    percent:function(){
      a.toFixed(2).replace(/(\d)(?=(\d{3})+\.)/g, '$1,')
    }

  }
  reward.init();
})