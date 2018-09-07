$(function(){
  // 首页倒计时，售卖情况
  var time={
    eles:$(".saleTime"),
    faultDate:1527508800000,
    hourStatus:false,
    endStatus:false,
    totalDcc:1000000000000000000,
    ABI:[
      {
        "constant": true,
        "inputs": [],
        "name": "name",
        "outputs": [
          {
            "name": "",
            "type": "string"
          }
        ],
        "payable": false,
        "stateMutability": "view",
        "type": "function"
      },
      {
        "constant": false,
        "inputs": [
          {
            "name": "tokenAddress",
            "type": "address"
          }
        ],
        "name": "setToken",
        "outputs": [],
        "payable": false,
        "stateMutability": "nonpayable",
        "type": "function"
      },
      {
        "constant": false,
        "inputs": [
          {
            "name": "beneficiary",
            "type": "address"
          }
        ],
        "name": "delegateWithdraw",
        "outputs": [],
        "payable": false,
        "stateMutability": "nonpayable",
        "type": "function"
      },
      {
        "constant": false,
        "inputs": [
          {
            "name": "_rate",
            "type": "uint256"
          }
        ],
        "name": "setRate",
        "outputs": [],
        "payable": false,
        "stateMutability": "nonpayable",
        "type": "function"
      },
      {
        "constant": false,
        "inputs": [],
        "name": "unpause",
        "outputs": [],
        "payable": false,
        "stateMutability": "nonpayable",
        "type": "function"
      },
      {
        "constant": true,
        "inputs": [],
        "name": "weiRaised",
        "outputs": [
          {
            "name": "",
            "type": "uint256"
          }
        ],
        "payable": false,
        "stateMutability": "view",
        "type": "function"
      },
      {
        "constant": false,
        "inputs": [],
        "name": "depositEth",
        "outputs": [],
        "payable": true,
        "stateMutability": "payable",
        "type": "function"
      },
      {
        "constant": true,
        "inputs": [
          {
            "name": "",
            "type": "address"
          }
        ],
        "name": "purchasedTokens",
        "outputs": [
          {
            "name": "",
            "type": "uint256"
          }
        ],
        "payable": false,
        "stateMutability": "view",
        "type": "function"
      },
      {
        "constant": true,
        "inputs": [],
        "name": "tokenSold",
        "outputs": [
          {
            "name": "",
            "type": "uint256"
          }
        ],
        "payable": false,
        "stateMutability": "view",
        "type": "function"
      },
      {
        "constant": true,
        "inputs": [],
        "name": "wallet",
        "outputs": [
          {
            "name": "",
            "type": "address"
          }
        ],
        "payable": false,
        "stateMutability": "view",
        "type": "function"
      },
      {
        "constant": true,
        "inputs": [],
        "name": "paused",
        "outputs": [
          {
            "name": "",
            "type": "bool"
          }
        ],
        "payable": false,
        "stateMutability": "view",
        "type": "function"
      },
      {
        "constant": true,
        "inputs": [],
        "name": "getRate",
        "outputs": [
          {
            "name": "",
            "type": "uint256"
          }
        ],
        "payable": false,
        "stateMutability": "view",
        "type": "function"
      },
      {
        "constant": false,
        "inputs": [
          {
            "name": "beneficiaries",
            "type": "address[]"
          }
        ],
        "name": "batchDelegateWithdraw",
        "outputs": [],
        "payable": false,
        "stateMutability": "nonpayable",
        "type": "function"
      },
      {
        "constant": true,
        "inputs": [
          {
            "name": "",
            "type": "address"
          }
        ],
        "name": "costs",
        "outputs": [
          {
            "name": "",
            "type": "uint256"
          }
        ],
        "payable": false,
        "stateMutability": "view",
        "type": "function"
      },
      {
        "constant": true,
        "inputs": [
          {
            "name": "",
            "type": "address"
          }
        ],
        "name": "withdrawedTokens",
        "outputs": [
          {
            "name": "",
            "type": "uint256"
          }
        ],
        "payable": false,
        "stateMutability": "view",
        "type": "function"
      },
      {
        "constant": false,
        "inputs": [
          {
            "name": "weiAmount",
            "type": "uint256"
          }
        ],
        "name": "withdrawEth2Wallet",
        "outputs": [],
        "payable": false,
        "stateMutability": "nonpayable",
        "type": "function"
      },
      {
        "constant": false,
        "inputs": [
          {
            "name": "_tokenTotal",
            "type": "uint256"
          }
        ],
        "name": "setTokenTotal",
        "outputs": [],
        "payable": false,
        "stateMutability": "nonpayable",
        "type": "function"
      },
      {
        "constant": false,
        "inputs": [],
        "name": "pause",
        "outputs": [],
        "payable": false,
        "stateMutability": "nonpayable",
        "type": "function"
      },
      {
        "constant": true,
        "inputs": [],
        "name": "owner",
        "outputs": [
          {
            "name": "",
            "type": "address"
          }
        ],
        "payable": false,
        "stateMutability": "view",
        "type": "function"
      },
      {
        "constant": true,
        "inputs": [],
        "name": "hasMintAuthorization",
        "outputs": [
          {
            "name": "",
            "type": "bool"
          }
        ],
        "payable": false,
        "stateMutability": "view",
        "type": "function"
      },
      {
        "constant": true,
        "inputs": [],
        "name": "kyc",
        "outputs": [
          {
            "name": "",
            "type": "address"
          }
        ],
        "payable": false,
        "stateMutability": "view",
        "type": "function"
      },
      {
        "constant": false,
        "inputs": [
          {
            "name": "kycAddress",
            "type": "address"
          }
        ],
        "name": "setKyc",
        "outputs": [],
        "payable": false,
        "stateMutability": "nonpayable",
        "type": "function"
      },
      {
        "constant": true,
        "inputs": [],
        "name": "getSelfHoldTokenAmount",
        "outputs": [
          {
            "name": "",
            "type": "uint256"
          }
        ],
        "payable": false,
        "stateMutability": "view",
        "type": "function"
      },
      {
        "constant": false,
        "inputs": [
          {
            "name": "_keepEth",
            "type": "bool"
          }
        ],
        "name": "setKeepEth",
        "outputs": [],
        "payable": false,
        "stateMutability": "nonpayable",
        "type": "function"
      },
      {
        "constant": true,
        "inputs": [],
        "name": "tokenTotal",
        "outputs": [
          {
            "name": "",
            "type": "uint256"
          }
        ],
        "payable": false,
        "stateMutability": "view",
        "type": "function"
      },
      {
        "constant": true,
        "inputs": [],
        "name": "keepEth",
        "outputs": [
          {
            "name": "",
            "type": "bool"
          }
        ],
        "payable": false,
        "stateMutability": "view",
        "type": "function"
      },
      {
        "constant": false,
        "inputs": [
          {
            "name": "tokenAmount",
            "type": "uint256"
          }
        ],
        "name": "withdrawToken2Wallet",
        "outputs": [],
        "payable": false,
        "stateMutability": "nonpayable",
        "type": "function"
      },
      {
        "constant": false,
        "inputs": [
          {
            "name": "_name",
            "type": "string"
          }
        ],
        "name": "setName",
        "outputs": [],
        "payable": false,
        "stateMutability": "nonpayable",
        "type": "function"
      },
      {
        "constant": true,
        "inputs": [],
        "name": "getTokenOnSale",
        "outputs": [
          {
            "name": "",
            "type": "uint256"
          }
        ],
        "payable": false,
        "stateMutability": "view",
        "type": "function"
      },
      {
        "constant": false,
        "inputs": [
          {
            "name": "walletAddress",
            "type": "address"
          }
        ],
        "name": "setWallet",
        "outputs": [],
        "payable": false,
        "stateMutability": "nonpayable",
        "type": "function"
      },
      {
        "constant": false,
        "inputs": [
          {
            "name": "newOwner",
            "type": "address"
          }
        ],
        "name": "transferOwnership",
        "outputs": [],
        "payable": false,
        "stateMutability": "nonpayable",
        "type": "function"
      },
      {
        "constant": true,
        "inputs": [
          {
            "name": "beneficiary",
            "type": "address"
          }
        ],
        "name": "getWithdrawableAmount",
        "outputs": [
          {
            "name": "",
            "type": "uint256"
          }
        ],
        "payable": false,
        "stateMutability": "view",
        "type": "function"
      },
      {
        "constant": true,
        "inputs": [],
        "name": "token",
        "outputs": [
          {
            "name": "",
            "type": "address"
          }
        ],
        "payable": false,
        "stateMutability": "view",
        "type": "function"
      },
      {
        "inputs": [],
        "payable": false,
        "stateMutability": "nonpayable",
        "type": "constructor"
      },
      {
        "payable": true,
        "stateMutability": "payable",
        "type": "fallback"
      },
      {
        "anonymous": false,
        "inputs": [
          {
            "indexed": true,
            "name": "beneficiary",
            "type": "address"
          },
          {
            "indexed": false,
            "name": "amount",
            "type": "uint256"
          }
        ],
        "name": "TokenWithdrawed",
        "type": "event"
      },
      {
        "anonymous": false,
        "inputs": [
          {
            "indexed": true,
            "name": "beneficiary",
            "type": "address"
          },
          {
            "indexed": false,
            "name": "value",
            "type": "uint256"
          },
          {
            "indexed": false,
            "name": "amount",
            "type": "uint256"
          }
        ],
        "name": "TokenPurchased",
        "type": "event"
      },
      {
        "anonymous": false,
        "inputs": [],
        "name": "Pause",
        "type": "event"
      },
      {
        "anonymous": false,
        "inputs": [],
        "name": "Unpause",
        "type": "event"
      },
      {
        "anonymous": false,
        "inputs": [
          {
            "indexed": true,
            "name": "previousOwner",
            "type": "address"
          },
          {
            "indexed": true,
            "name": "newOwner",
            "type": "address"
          }
        ],
        "name": "OwnershipTransferred",
        "type": "event"
      }
    ],
    saleToken:0,
    tokenTotal:0,
    totalNum:0,
    ethTime:0,
    endTime:0,
    ingSaling:{},//定时器
    timeDiff:function(){
      var stime = Date.parse(new Date(this.faultDate)); 
      var etime = Date.parse(new Date());
      if(!this.hourStatus){
        var onehour = Date.parse(new Date(this.faultDate))- etime - 3600*1000;
        this.hourStatus = onehour <=0 ?true:false;
      }  
      var zero = stime - etime;
      this.endStatus = zero <= 0?true:false;
      var usedTime = stime - etime;  //两个时间戳相差的毫秒数  
      var days=Math.floor(usedTime/(24*3600*1000));  
      //计算出小时数  
      var leave1=usedTime%(24*3600*1000);    //计算天数后剩余的毫秒数  
      var hours=Math.floor(leave1/(3600*1000));
      //计算相差分钟数  
      var leave2=leave1%(3600*1000);        //计算小时数后剩余的毫秒数  
      var minutes=Math.floor(leave2/(60*1000));  
      var sec =(leave2 - minutes*60*1000)/1000;
      var  count =[days,hours,minutes,sec]; 
      return count;  
    },
    saling:function(){
      e.eth.call({
        to: t,
        data: n.tokenSold.getData()
      }, function (a, i) {
        if (a);
        else {
          var s = new BigNumber(i);
          var saleNumber = s.toString(10);
          time.saleToken = +e.fromWei(saleNumber+"","ether");
          var f = time.saleToken.toFixed(2).replace(/(\d)(?=(\d{3})+\.)/g, '$1,');
          var res = time.saleToken*100 / time.totalNum +"%";
          $('#beforeSalePro').css("width",res).next(".proMark").css("left",res).html(f+" DCC");
          $('#progressNum').css("width",res).next(".proMark").css("left",res).html(f+" DCC");
         
        }
      })
    },
    getRate:function(){
      e.eth.call({
        to: t,
        data: n.getRate.getData()
      }, function (a, i) {
        if (a);
        else {
          var s = new BigNumber(i);
          var saleNumber = s.toString(10);
          // var num = +e.fromWei(saleNumber+"","ether");
          var f = saleNumber.replace(/(\d)(?=(?:\d{3})+$)/g, '$1,');
          $('#saleTip span').text(f);
        }
      })
    },
    getTotal:function(resolve){
      e.eth.call({
        to: t,
        data: n.tokenTotal.getData()
      }, function (a, i) {
        if (a);
        else {
          var s = new BigNumber(i);
          var saleNumber = s.toString(10);
          var num = e.fromWei(saleNumber+"","ether");
          time.totalNum = num;
          time.tokenTotal= num.replace(/(\d)(?=(?:\d{3})+$)/g, '$1,');
          $('#saleTip em').text(time.tokenTotal);
          $("#beforeSaleProTotal").html(time.tokenTotal+" DCC");
          $("#totalToken").html(time.tokenTotal+" DCC")
          if(resolve) resolve();
         
        }
      })
    },
    interval:function(){
      $("#joinKyc").hide();
      $("#ingSale").show();
      $("#beforeSalTip").show();  
      time.getRate();//1eth的价格
      new Promise(function(resolve,reject){
        time.getTotal(resolve);//总共的dcc
      }).then(function(){
        time.saling();//销售数量
        time.ingSaling = setInterval(function(){
          time.saling()//销售数量
        },30*1000);

      })
    },
    init:function(){
        var count = time.timeDiff();
        time.eles.each(function(i,element){
          element.innerText = count[i];
        })
        if(time.hourStatus&&!time.endStatus){
          if(time.ethTime == 0){
           time.interval();
           time.ethTime = 1;
          }   
        }
        if(time.endStatus){
          if(time.endTime == 0){          
            time.getTotal()
            $("#ingSale").hide();
            $("#beforeSale").hide();
            $("#afterSale").show();
            time.endTime = 1;
          }
           time.saling();
          clearInterval(setInter);
          clearInterval(time.ingSaling);
          new Promise(function(resolve,reject){
            time.getTotal(resolve);//总共的dcc
          }).then(function(){
            var Interval = setInterval(function(){
              time.saling();
            },30*1000)
          })
         
        }
    }
  }
  var t = "0x2148671A8b4c9d1229ede9B0a8c974BEf0786544",
  e = window.web3,
  n = (e = new Web3(new Web3.providers.HttpProvider("https://mainnet.infura.io/"))).eth.contract(time.ABI).at(t);
  // time.init();
  // var setInter = setInterval(time.init,1000);

  // 邮箱订阅
  var a = "//10.65.103.67:9001";
  "www.hypercredit.com" === window.location.host && (a = "//10.65.209.17:9403/dcc-finance-backend"), "dcc.finance" === window.location.host && (a = "//dcc.finance/dcc-finance-backend"), $("#queryBtn").click(function () {
  var lanVal =  JSON.parse($.getCookie('lan'))
  var lan = lanVal?lanVal : ["en"];
    var err ={tip1:"",tip2:"","tip3":"","tip4":"","tip5":""};
    switch(lan[0]){
      case "en":
        err.tip1 = en.emailTip[0];
        err.tip2 = en.emailTip[1];
        err.tip3 = en.emailTip[2];
        err.tip4 = en.emailTip[3];
        err.tip5 = en.emailTip[4];
      break;
      case "vn":
        err.tip1 = vn.emailTip[0];
        err.tip2 = vn.emailTip[1];
        err.tip3 = vn.emailTip[2];
        err.tip4 = vn.emailTip[3];
        err.tip5 = vn.emailTip[4];
        break;
      case "kn":
      err.tip1 = kn.emailTip[0];
      err.tip2 = kn.emailTip[1];
      err.tip3 = kn.emailTip[2];
      err.tip4 = kn.emailTip[3];
      err.tip5 = kn.emailTip[4];
      break;
      default:break;
    }
    
    var t = $("#investInput").val(),
      e = new RegExp("^[A-Za-z\\d]+([-_.][A-Za-z\\d]+)*@([A-Za-z\\d]+[-.])+[A-Za-z\\d]{2,5}$", "g");
    "" == t ? $("#queryErrTip").show().html(err.tip5) : e.test(t) ? ($("#queryErrTip").hide(), $.ajax({
      url:a+"/email/subscribe",
      data: {
        email: t
      },
      method: "post",
      success: function (t) {
        t && "SUCCESS" === t.systemCode && "SUCCESS" === t.businessCode ? ($("#sub-tips").text(err.tip1).fadeIn(), setTimeout(function () {
          $("#sub-tips").fadeOut()
        }, 3e3)) : "EMAIL_SUBSCRIBED" === t.businessCode ? ($("#sub-tips").text(err.tip2).fadeIn(), setTimeout(function () {
          $("#sub-tips").fadeOut()
        }, 3e3)) : ($("#sub-tips").text(err.tip3).fadeIn(), setTimeout(function () {
          $("#sub-tips").fadeOut()
        }, 3e3))
      },
      error: function () {
        $("#sub-tips").text(err.tip3).fadeIn(), setTimeout(function () {
          $("#sub-tips").fadeOut()
        }, 3e3)
      },
      complete: function () {
        $("#investInput").val("")
      }
    })) : $("#queryErrTip").show().html(err.tip4)
  })
  
})