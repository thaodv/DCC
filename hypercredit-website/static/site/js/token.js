$(function(){
  var ABI=[{constant:!1,inputs:[{name:"beneficiary",type:"address"}],name:"delegateWithdraw",outputs:[],payable:!1,stateMutability:"nonpayable",type:"function"},{constant:!0,inputs:[],name:"total",outputs:[{name:"",type:"uint256"}],payable:!1,stateMutability:"view",type:"function"},{constant:!0,inputs:[],name:"getUnlockPercent",outputs:[{name:"",type:"uint256"}],payable:!1,stateMutability:"view",type:"function"},{constant:!1,inputs:[],name:"unpause",outputs:[],payable:!1,stateMutability:"nonpayable",type:"function"},{constant:!0,inputs:[],name:"roundUnlockPercent",outputs:[{name:"",type:"uint256"}],payable:!1,stateMutability:"view",type:"function"},{constant:!0,inputs:[],name:"weiRaised",outputs:[{name:"",type:"uint256"}],payable:!1,stateMutability:"view",type:"function"},{constant:!1,inputs:[],name:"depositEth",outputs:[],payable:!0,stateMutability:"payable",type:"function"},{constant:!0,inputs:[{name:"",type:"address"}],name:"purchasedTokens",outputs:[{name:"",type:"uint256"}],payable:!1,stateMutability:"view",type:"function"},{constant:!0,inputs:[],name:"tokenSold",outputs:[{name:"",type:"uint256"}],payable:!1,stateMutability:"view",type:"function"},{constant:!0,inputs:[],name:"wallet",outputs:[{name:"",type:"address"}],payable:!1,stateMutability:"view",type:"function"},{constant:!1,inputs:[{name:"beneficiary",type:"address"},{name:"overrideUnlockPercent",type:"uint256"}],name:"forceDelegateWithdraw",outputs:[],payable:!1,stateMutability:"nonpayable",type:"function"},{constant:!0,inputs:[],name:"paused",outputs:[{name:"",type:"bool"}],payable:!1,stateMutability:"view",type:"function"},{constant:!1,inputs:[{name:"beneficiaries",type:"address[]"}],name:"batchDelegateWithdraw",outputs:[],payable:!1,stateMutability:"nonpayable",type:"function"},{constant:!0,inputs:[{name:"",type:"address"}],name:"costs",outputs:[{name:"",type:"uint256"}],payable:!1,stateMutability:"view",type:"function"},{constant:!0,inputs:[{name:"",type:"address"}],name:"withdrawedTokens",outputs:[{name:"",type:"uint256"}],payable:!1,stateMutability:"view",type:"function"},{constant:!1,inputs:[{name:"_lowerLimitWeiPerHand",type:"uint256"}],name:"setLowerLimitWeiPerHand",outputs:[],payable:!1,stateMutability:"nonpayable",type:"function"},{constant:!1,inputs:[{name:"weiAmount",type:"uint256"}],name:"withdrawEth2Wallet",outputs:[],payable:!1,stateMutability:"nonpayable",type:"function"},{constant:!1,inputs:[{name:"_unlockTimestamp",type:"uint256"}],name:"setUnlockTimestamp",outputs:[],payable:!1,stateMutability:"nonpayable",type:"function"},{constant:!1,inputs:[],name:"pause",outputs:[],payable:!1,stateMutability:"nonpayable",type:"function"},{constant:!0,inputs:[],name:"owner",outputs:[{name:"",type:"address"}],payable:!1,stateMutability:"view",type:"function"},{constant:!0,inputs:[],name:"kycAndRate",outputs:[{name:"",type:"address"}],payable:!1,stateMutability:"view",type:"function"},{constant:!0,inputs:[],name:"unlockTimestamp",outputs:[{name:"",type:"uint256"}],payable:!1,stateMutability:"view",type:"function"},{constant:!1,inputs:[{name:"_keepEth",type:"bool"}],name:"setKeepEth",outputs:[],payable:!1,stateMutability:"nonpayable",type:"function"},{constant:!0,inputs:[],name:"keepEth",outputs:[{name:"",type:"bool"}],payable:!1,stateMutability:"view",type:"function"},{constant:!1,inputs:[{name:"tokenAmount",type:"uint256"}],name:"withdrawToken2Wallet",outputs:[],payable:!1,stateMutability:"nonpayable",type:"function"},{constant:!0,inputs:[],name:"getTokenOnSale",outputs:[{name:"",type:"uint256"}],payable:!1,stateMutability:"view",type:"function"},{constant:!0,inputs:[],name:"lowerLimitWeiPerHand",outputs:[{name:"",type:"uint256"}],payable:!1,stateMutability:"view",type:"function"},{constant:!1,inputs:[{name:"walletAddress",type:"address"}],name:"setWallet",outputs:[],payable:!1,stateMutability:"nonpayable",type:"function"},{constant:!0,inputs:[],name:"roundCoolDown",outputs:[{name:"",type:"uint256"}],payable:!1,stateMutability:"view",type:"function"},{constant:!1,inputs:[{name:"beneficiary",type:"address"},{name:"tokenAmount",type:"uint256"}],name:"preserve",outputs:[],payable:!1,stateMutability:"nonpayable",type:"function"},{constant:!1,inputs:[{name:"newOwner",type:"address"}],name:"transferOwnership",outputs:[],payable:!1,stateMutability:"nonpayable",type:"function"},{constant:!0,inputs:[{name:"beneficiary",type:"address"}],name:"getWithdrawableAmount",outputs:[{name:"",type:"uint256"}],payable:!1,stateMutability:"view",type:"function"},{constant:!0,inputs:[],name:"token",outputs:[{name:"",type:"address"}],payable:!1,stateMutability:"view",type:"function"},{inputs:[],payable:!1,stateMutability:"nonpayable",type:"constructor"},{payable:!0,stateMutability:"payable",type:"fallback"},{anonymous:!1,inputs:[{indexed:!0,name:"beneficiary",type:"address"},{indexed:!1,name:"amount",type:"uint256"}],name:"TokenWithdrawed",type:"event"},{anonymous:!1,inputs:[{indexed:!0,name:"beneficiary",type:"address"},{indexed:!1,name:"value",type:"uint256"},{indexed:!1,name:"amount",type:"uint256"}],name:"TokenPurchased",type:"event"},{anonymous:!1,inputs:[],name:"Pause",type:"event"},{anonymous:!1,inputs:[],name:"Unpause",type:"event"},{anonymous:!1,inputs:[{indexed:!0,name:"previousOwner",type:"address"},{indexed:!0,name:"newOwner",type:"address"}],name:"OwnershipTransferred",type:"event"}]
  var saleABI =[
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
];
    var t = "0x53A5a24C01e7E999aca41377F7913D7541c40B1e",
     e = window.web3, 
     n = (e = new Web3(new Web3.providers.HttpProvider("https://mainnet.infura.io/"))).eth.contract(ABI).at(t);
     e.eth.call({ to: t, data: n.total.getData() }, function (a, i) { 
        if (a); 
        else { 
         var s = new BigNumber(i); 
         e.eth.call({ to: t, data: n.tokenSold.getData() }, function (t, e) { 
             if (t); 
             else { 
                var n = new BigNumber(e), 
                a = s.minus(n);
                a = a.times(1e-18).toFixed(0);
                var i = n.times(1e-18), o = i.toFixed(0);
                $("#canInvest").html(a), $("#tokenSold").html(o);
                var p = (i / s.times(1e-18).toFixed(0) * 100).toFixed(2) + "%";
                 $(".investProgress .progress .bar").css("width", p), $(".showProgress").find("span").html(p) 
                } 
            }) 
        } 
    }),
    e.eth.call({ to: t, data: n.unlockTimestamp.getData() }, function (a, i) { 
    if (a);
    else {
            var s = 1e3 * e.toDecimal(i);
        }
	 })

        $("#tokenQueryBtn").click(function () { 
					errMsg();
            var t = $("#tokenInput").val(), e = new RegExp("((?=[\x21-\x7e]+)[^A-Za-z0-9])");
            e.test(t);
            if ("" == t) $("#tokenQueryErrTip").show().html(err.tip1);
                else if (e.test(t)) $("#tokenQueryErrTip").show().html(err.tip2); 
                else if (42 != t.length) $("#tokenQueryErrTip").show().html(err.tip3); 
                else { $("#tokenQueryErrTip").hide(), n.withdrawedTokens(t, function (t, e) {
                    if (t) console.log(t); 
                    else { 
                        var n = new BigNumber(e).times(1e-18);
                         $("#Issued").html(n.toFixed(2).replace(/(\d)(?=(\d{3})+\.)/g, '$1,')) 
                        } }), 
                        n.purchasedTokens(t, function (t, e) {
                            if (t) console.log(t); 
                            else { 
                                var n = new BigNumber(e).times(1e-18); 
                                $("#investNum").html(n.toFixed(2).replace(/(\d)(?=(\d{3})+\.)/g, '$1,')) } });
                                 var a = parseInt($(window).width());
																	a > 830 || 830 == a ? $(".investPop").css("marginBottom", "215px") : a < 830 && $(".investPop").css("marginBottom", "250px"), $(".tokenInvest").show() 
																} 
															}) 
        
        
        var add = "0x2148671A8b4c9d1229ede9B0a8c974BEf0786544",
        contract = (eth = new Web3(new Web3.providers.HttpProvider("https://mainnet.infura.io/"))).eth.contract(saleABI).at(add);
				
				var lan = [];
				var err ={tip1:"",tip2:"","tip3":"","tip4":"","tip5":""};
        $("#saleQueryBtn").click(function () { 
					errMsg();
            var t = $("#saleInput").val(), e = new RegExp("((?=[\x21-\x7e]+)[^A-Za-z0-9])");
            e.test(t);
            if ("" == t) $("#saleQueryErrTip").show().html(err.tip1);
                else if (e.test(t)) $("#saleQueryErrTip").show().html(err.tip2); 
                else if (42 != t.length) $("#saleQueryErrTip").show().html(err.tip3); 
                else { $("#saleQueryErrTip").hide(), contract.withdrawedTokens(t, function (t, e) {
                    if (t) console.log(t); 
                    else { 
                        var n = new BigNumber(e).times(1e-18); 
                        $("#saleIssued").html(n.toFixed(2).replace(/(\d)(?=(\d{3})+\.)/g, '$1,')) } }), contract.purchasedTokens(t, function (t, e) { if (t) console.log(t); else { var n = new BigNumber(e).times(1e-18); $("#saleInvestNum").html(n.toFixed(2).replace(/(\d)(?=(\d{3})+\.)/g, '$1,')) } }); var a = parseInt($(window).width()); a > 830 || 830 == a ? $("#saleBlock  .tokenInfo").css("marginBottom", "215px") : a < 830 && $("#saleBlock .tokenInfo").css("marginBottom", "250px"), $(".saleInvest").show() } }) 

			function errMsg(){
				var lanVal =JSON.parse($.getCookie('lan'))
				lan = lanVal?lanVal : ["en"];
				err ={tip1:"",tip2:"","tip3":"","tip4":"","tip5":""};
				switch(lan[0]){
					case "en":
						err.tip1 = en.token_scanner[0];
						err.tip2 = en.token_scanner[1];
						err.tip3 = en.token_scanner[2];
					break;
					case "vn":
						err.tip1 = vn.tokenScanner[0];
						err.tip2 = vn.tokenScanner[1];
						err.tip3 = vn.tokenScanner[2];
						break;
					case "kn":
					err.tip1 = en.token_scanner[0];
					err.tip2 = en.token_scanner[1];
					err.tip3 = en.token_scanner[2];
					break;
					default:break;
				}
			}
})
