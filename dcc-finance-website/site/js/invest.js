var ABI = [{
  constant: !1,
  inputs: [{
    name: "beneficiary",
    type: "address"
  }],
  name: "delegateWithdraw",
  outputs: [],
  payable: !1,
  stateMutability: "nonpayable",
  type: "function"
}, {
  constant: !0,
  inputs: [],
  name: "total",
  outputs: [{
    name: "",
    type: "uint256"
  }],
  payable: !1,
  stateMutability: "view",
  type: "function"
}, {
  constant: !0,
  inputs: [],
  name: "getUnlockPercent",
  outputs: [{
    name: "",
    type: "uint256"
  }],
  payable: !1,
  stateMutability: "view",
  type: "function"
}, {
  constant: !1,
  inputs: [],
  name: "unpause",
  outputs: [],
  payable: !1,
  stateMutability: "nonpayable",
  type: "function"
}, {
  constant: !0,
  inputs: [],
  name: "roundUnlockPercent",
  outputs: [{
    name: "",
    type: "uint256"
  }],
  payable: !1,
  stateMutability: "view",
  type: "function"
}, {
  constant: !0,
  inputs: [],
  name: "weiRaised",
  outputs: [{
    name: "",
    type: "uint256"
  }],
  payable: !1,
  stateMutability: "view",
  type: "function"
}, {
  constant: !1,
  inputs: [],
  name: "depositEth",
  outputs: [],
  payable: !0,
  stateMutability: "payable",
  type: "function"
}, {
  constant: !0,
  inputs: [{
    name: "",
    type: "address"
  }],
  name: "purchasedTokens",
  outputs: [{
    name: "",
    type: "uint256"
  }],
  payable: !1,
  stateMutability: "view",
  type: "function"
}, {
  constant: !0,
  inputs: [],
  name: "tokenSold",
  outputs: [{
    name: "",
    type: "uint256"
  }],
  payable: !1,
  stateMutability: "view",
  type: "function"
}, {
  constant: !0,
  inputs: [],
  name: "wallet",
  outputs: [{
    name: "",
    type: "address"
  }],
  payable: !1,
  stateMutability: "view",
  type: "function"
}, {
  constant: !1,
  inputs: [{
    name: "beneficiary",
    type: "address"
  }, {
    name: "overrideUnlockPercent",
    type: "uint256"
  }],
  name: "forceDelegateWithdraw",
  outputs: [],
  payable: !1,
  stateMutability: "nonpayable",
  type: "function"
}, {
  constant: !0,
  inputs: [],
  name: "paused",
  outputs: [{
    name: "",
    type: "bool"
  }],
  payable: !1,
  stateMutability: "view",
  type: "function"
}, {
  constant: !1,
  inputs: [{
    name: "beneficiaries",
    type: "address[]"
  }],
  name: "batchDelegateWithdraw",
  outputs: [],
  payable: !1,
  stateMutability: "nonpayable",
  type: "function"
}, {
  constant: !0,
  inputs: [{
    name: "",
    type: "address"
  }],
  name: "costs",
  outputs: [{
    name: "",
    type: "uint256"
  }],
  payable: !1,
  stateMutability: "view",
  type: "function"
}, {
  constant: !0,
  inputs: [{
    name: "",
    type: "address"
  }],
  name: "withdrawedTokens",
  outputs: [{
    name: "",
    type: "uint256"
  }],
  payable: !1,
  stateMutability: "view",
  type: "function"
}, {
  constant: !1,
  inputs: [{
    name: "_lowerLimitWeiPerHand",
    type: "uint256"
  }],
  name: "setLowerLimitWeiPerHand",
  outputs: [],
  payable: !1,
  stateMutability: "nonpayable",
  type: "function"
}, {
  constant: !1,
  inputs: [{
    name: "weiAmount",
    type: "uint256"
  }],
  name: "withdrawEth2Wallet",
  outputs: [],
  payable: !1,
  stateMutability: "nonpayable",
  type: "function"
}, {
  constant: !1,
  inputs: [{
    name: "_unlockTimestamp",
    type: "uint256"
  }],
  name: "setUnlockTimestamp",
  outputs: [],
  payable: !1,
  stateMutability: "nonpayable",
  type: "function"
}, {
  constant: !1,
  inputs: [],
  name: "pause",
  outputs: [],
  payable: !1,
  stateMutability: "nonpayable",
  type: "function"
}, {
  constant: !0,
  inputs: [],
  name: "owner",
  outputs: [{
    name: "",
    type: "address"
  }],
  payable: !1,
  stateMutability: "view",
  type: "function"
}, {
  constant: !0,
  inputs: [],
  name: "kycAndRate",
  outputs: [{
    name: "",
    type: "address"
  }],
  payable: !1,
  stateMutability: "view",
  type: "function"
}, {
  constant: !0,
  inputs: [],
  name: "unlockTimestamp",
  outputs: [{
    name: "",
    type: "uint256"
  }],
  payable: !1,
  stateMutability: "view",
  type: "function"
}, {
  constant: !1,
  inputs: [{
    name: "_keepEth",
    type: "bool"
  }],
  name: "setKeepEth",
  outputs: [],
  payable: !1,
  stateMutability: "nonpayable",
  type: "function"
}, {
  constant: !0,
  inputs: [],
  name: "keepEth",
  outputs: [{
    name: "",
    type: "bool"
  }],
  payable: !1,
  stateMutability: "view",
  type: "function"
}, {
  constant: !1,
  inputs: [{
    name: "tokenAmount",
    type: "uint256"
  }],
  name: "withdrawToken2Wallet",
  outputs: [],
  payable: !1,
  stateMutability: "nonpayable",
  type: "function"
}, {
  constant: !0,
  inputs: [],
  name: "getTokenOnSale",
  outputs: [{
    name: "",
    type: "uint256"
  }],
  payable: !1,
  stateMutability: "view",
  type: "function"
}, {
  constant: !0,
  inputs: [],
  name: "lowerLimitWeiPerHand",
  outputs: [{
    name: "",
    type: "uint256"
  }],
  payable: !1,
  stateMutability: "view",
  type: "function"
}, {
  constant: !1,
  inputs: [{
    name: "walletAddress",
    type: "address"
  }],
  name: "setWallet",
  outputs: [],
  payable: !1,
  stateMutability: "nonpayable",
  type: "function"
}, {
  constant: !0,
  inputs: [],
  name: "roundCoolDown",
  outputs: [{
    name: "",
    type: "uint256"
  }],
  payable: !1,
  stateMutability: "view",
  type: "function"
}, {
  constant: !1,
  inputs: [{
    name: "beneficiary",
    type: "address"
  }, {
    name: "tokenAmount",
    type: "uint256"
  }],
  name: "preserve",
  outputs: [],
  payable: !1,
  stateMutability: "nonpayable",
  type: "function"
}, {
  constant: !1,
  inputs: [{
    name: "newOwner",
    type: "address"
  }],
  name: "transferOwnership",
  outputs: [],
  payable: !1,
  stateMutability: "nonpayable",
  type: "function"
}, {
  constant: !0,
  inputs: [{
    name: "beneficiary",
    type: "address"
  }],
  name: "getWithdrawableAmount",
  outputs: [{
    name: "",
    type: "uint256"
  }],
  payable: !1,
  stateMutability: "view",
  type: "function"
}, {
  constant: !0,
  inputs: [],
  name: "token",
  outputs: [{
    name: "",
    type: "address"
  }],
  payable: !1,
  stateMutability: "view",
  type: "function"
}, {
  inputs: [],
  payable: !1,
  stateMutability: "nonpayable",
  type: "constructor"
}, {
  payable: !0,
  stateMutability: "payable",
  type: "fallback"
}, {
  anonymous: !1,
  inputs: [{
    indexed: !0,
    name: "beneficiary",
    type: "address"
  }, {
    indexed: !1,
    name: "amount",
    type: "uint256"
  }],
  name: "TokenWithdrawed",
  type: "event"
}, {
  anonymous: !1,
  inputs: [{
    indexed: !0,
    name: "beneficiary",
    type: "address"
  }, {
    indexed: !1,
    name: "value",
    type: "uint256"
  }, {
    indexed: !1,
    name: "amount",
    type: "uint256"
  }],
  name: "TokenPurchased",
  type: "event"
}, {
  anonymous: !1,
  inputs: [],
  name: "Pause",
  type: "event"
}, {
  anonymous: !1,
  inputs: [],
  name: "Unpause",
  type: "event"
}, {
  anonymous: !1,
  inputs: [{
    indexed: !0,
    name: "previousOwner",
    type: "address"
  }, {
    indexed: !0,
    name: "newOwner",
    type: "address"
  }],
  name: "OwnershipTransferred",
  type: "event"
}];
$(function () {
  var t = "0x53A5a24C01e7E999aca41377F7913D7541c40B1e",
    e = window.web3,
    n = (e = new Web3(new Web3.providers.HttpProvider("https://mainnet.infura.io/"))).eth.contract(ABI).at(t);
  e.eth.call({
    to: t,
    data: n.total.getData()
  }, function (a, i) {
    if (a);
    else {
      var s = new BigNumber(i);
      e.eth.call({
        to: t,
        data: n.tokenSold.getData()
      }, function (t, e) {
        if (t);
        else {
          var n = new BigNumber(e),
          var res = n/100000;
          $("#canInvest").html(a), $("#tokenSold").html(o);
          var p = (i / s.times(1e-18).toFixed(0) * 100).toFixed(2) + "%";
          $(".investProgress .progress .bar").css("width", p), $(".showProgress").find("span").html(p)
        }
      })
    }
  }), e.eth.call({
    to: t,
    data: n.unlockTimestamp.getData()
  }, function (a, i) {
    if (a);
    else {
      var s = 1e3 * e.toDecimal(i);
      e.eth.call({
        to: t,
        data: n.roundCoolDown.getData()
      }, function (t, n) {
        if (t);
        else
          for (var a = 1e3 * e.toDecimal(n), i = 0; i < 5; i++) {
            var o = new Date(s + a * i),
              p = o.getFullYear(),
              u = o.getMonth() + 1,
              r = o.getDate();
            $(".btm").find(".dateList").eq(i).find(".time").html(p + "." + u + "." + r)
          }
      })
    }
  }), e.eth.call({
    to: t,
    data: n.getUnlockPercent.getData()
  }, function (t, n) {
    if (t);
    else switch (e.toDecimal(n)) {
      case 25:
        $(".investPopover .middle").find("span").eq(0).css({
          backgroundColor: "#0db39e"
        }), $(".investPopover .middle").find("b").eq(0).css({
          backgroundColor: "#0db39e"
        }), $(".investPopover .middle").find("b").eq(1).css({
          backgroundColor: "#0db39e"
        }), $(".investPopover .btm .dateList").find(".line").eq(0).css({
          backgroundColor: "#0db39e"
        }), $(".investPopover .btm .dateList").find("b").eq(0).css({
          backgroundColor: "#0db39e"
        }), $(".investPopover .btm .dateList").find("b").eq(1).css({
          backgroundColor: "#0db39e"
        });
        break;
      case 50:
        $(".investPopover .middle").find("span").css({
          backgroundColor: "#0db39e"
        }), $(".investPopover .middle").find("b").css({
          backgroundColor: "#0db39e"
        }), $(".investPopover .middle").find("span").eq(2).css({
          backgroundColor: "transparent"
        }), $(".investPopover .middle").find("b").eq(3).css({
          backgroundColor: "transparent"
        }), $(".investPopover .middle").find("span").eq(3).css({
          backgroundColor: "transparent"
        }), $(".investPopover .middle").find("b").eq(4).css({
          backgroundColor: "transparent"
        }), $(".investPopover .btm .dateList").find(".line").css({
          backgroundColor: "#0db39e"
        }), $(".investPopover .btm .dateList").find("b").css({
          backgroundColor: "#0db39e"
        }), $(".investPopover .btm .dateList").find(".line").eq(2).css({
          backgroundColor: "transparent"
        }), $(".investPopover .btm .dateList").find(".line").eq(3).css({
          backgroundColor: "transparent"
        }), $(".investPopover .btm .dateList").find("b").eq(3).css({
          backgroundColor: "transparent"
        }), $(".investPopover .btm .dateList").find("b").eq(4).css({
          backgroundColor: "transparent"
        });
        break;
      case 75:
        $(".investPopover .middle").find("span").css({
          backgroundColor: "#0db39e"
        }), $(".investPopover .middle").find("b").css({
          backgroundColor: "#0db39e"
        }), $(".investPopover .middle").find("span").eq(3).css({
          backgroundColor: "transparent"
        }), $(".investPopover .middle").find("b").eq(4).css({
          backgroundColor: "transparent"
        }), $(".investPopover .btm .dateList").find(".line").css({
          backgroundColor: "#0db39e"
        }), $(".investPopover .btm .dateList").find("b").css({
          backgroundColor: "#0db39e"
        }), $(".investPopover .btm .dateList").find(".line").eq(3).css({
          backgroundColor: "transparent"
        }), $(".investPopover .btm .dateList").find("b").eq(4).css({
          backgroundColor: "transparent"
        });
        break;
      case 100:
        $(".investPopover .middle").find("span").css({
          backgroundColor: "#0db39e"
        }), $(".investPopover .middle").find("b").css({
          backgroundColor: "#0db39e"
        }), $(".investPopover .btm .dateList").find(".line").css({
          backgroundColor: "#0db39e"
        }), $(".investPopover .btm .dateList").find("b").css({
          backgroundColor: "#0db39e"
        })
    }
  }), $("#queryBtn").click(function () {
    var t = $("#investInput").val(),
      e = new RegExp("[\\u4E00-\\u9FFF]+", "g");
    if ("" == t) $("#queryErrTip").show().html("请输入钱包地址");
    else if (e.test(t)) $("#queryErrTip").show().html("钱包地址不能为汉字");
    else if (42 != t.length) $("#queryErrTip").show().html("钱包地址应由42位的字符组成");
    else {
      $("#queryErrTip").hide(), n.withdrawedTokens(t, function (t, e) {
        if (t) console.log(t);
        else {
          var n = new BigNumber(e).times(1e-18);
          $("#Issued").html(n.toFixed(2))
        }
      }), n.purchasedTokens(t, function (t, e) {
        if (t) console.log(t);
        else {
          var n = new BigNumber(e).times(1e-18);
          $("#investNum").html(n.toFixed(2))
        }
      });
      var a = parseInt($(window).width());
      a > 830 || 830 == a ? $(".queryInvest").css("marginBottom", "163px") : a < 830 && $(".queryInvest").css("marginBottom", "163px"), $(".investPopover").show()
    }
  })
});