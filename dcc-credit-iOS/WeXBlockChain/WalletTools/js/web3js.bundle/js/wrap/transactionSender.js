(function() {

    TransactionSender = {
        _web3: {},
        _UUIDHash: Array(),
        _provider: "",
        //nonce 默认-1
        _nonce: -1,
        //key:合约名 value:合约事件对象
        _eventObj: {}
    };

    TransactionSender.getWeb3jObj = function(arg) {
        var web3 = TransactionSender._web3["web3"];
        if (web3 == null || web3 == undefined) {
            var provideAddr = "http://10.65.209.49:6789";
            try {
                var provideAddr = TransactionSender._provider;
               // ObjCLog("provider:" + TransactionSender._provider);
                var web3 = new Web3(new Web3.providers.HttpProvider(provideAddr));
                TransactionSender._web3["web3"] = web3;
            } catch (e) {
                onWeb3InitErr(e);
            }

        }
        return web3;
    }

    TransactionSender.toType = function(obj) {
        return ({}).toString.call(obj).match(/\s([a-zA-Z]+)/)[1].toLowerCase()
    }

    TransactionSender.callsss = function(contractName, methodName, argumentList) {
        argumentList = "Hu123456";
        ObjCLog("contractName: " + contractName + " methodName: " + methodName + " argumentList: " + argumentList);
        var contractInstance = ContractManager.getContract(contractName).storeObj;
        var result;
        result = contractInstance[methodName].call("Hu123456");
        ObjCLog("result:" + result);
        return result;
    }

    TransactionSender.call = function(contractName, methodName, argumentList) {

        var contractInstance = ContractManager.getContract(contractName).storeObj;
        ObjCLog(+" contractInstance: " + contractInstance + "contractName: " + contractName + " methodName: " + methodName + " argumentList: " + argumentList);
        var result;
        ObjCLog("contractInstance address:"+contractInstance.address);
        if (argumentList.indexOf("[") >= 0) {
            var arrayFromiOS = JSON.parse(argumentList);
            result = contractInstance[methodName].call.apply(this, arrayFromiOS)
        } else {
            result = contractInstance[methodName].call();
        }

        ObjCLog("result:" + result);
        return result;
    }


    TransactionSender.setProvider = function(providers) {
        TransactionSender._provider = providers;
    }

    function print_array(arr) {
        for (var key in arr) {
            if (typeof(arr[key]) == 'array' || typeof(arr[key]) == 'object') { //递归调用    
                ObjCLog(arr[key]);
            } else {
                ObjCLog(key + ' = ' + arr[key] + '<br>');
            }
        }
    }

    TransactionSender.getData = function(contractName, methodName, argumentList) {

        var contractInstance = ContractManager.getContract(contractName).storeObj;
        var result;
        if (argumentList.indexOf("[") >= 0) {
            var arrayFromiOS = JSON.parse(argumentList);
            ObjCLog("param "+arrayFromiOS);
            result = contractInstance[methodName].getData.apply(this, arrayFromiOS)
        } else {
            result = contractInstance[methodName].getData();
        }
        return result;
    }

    TransactionSender.sendRawTrasaction = function(UUID, contractName, methodName, argumentList, eventName, privateKeyHex, from) {

        var web3 = TransactionSender.getWeb3jObj();
        var contractInstance = ContractManager.getContract(contractName).storeObj;

        var externalAccout = from;
        var nonce = web3.nonce();

        //var nonceHex = web3.toHex(nonce);

        var executeData = TransactionSender.getData(contractName, methodName, argumentList);

        var rawTx = {
            gasPrice: 21000000000,
            gasLimit: 9999999999,
            value: 0,
            nonce: nonce,
            to: contractInstance.address,
            data: executeData
        }

        TransactionSender._nonce = nonce + 1;

        var Tx = EthJS.Tx;
        var tx = new Tx(rawTx);

        var privateKeyTxt = privateKeyHex;
        var Buffer = window.buffer.Buffer;
        var privateKey = new Buffer(privateKeyTxt, 'hex');
        tx.sign(privateKey);

        var serializedTx = tx.serialize();
        var serializedTxHex = "0x" + serializedTx.toString('hex');


        var hash = web3.eth.sendRawTransaction(serializedTxHex);

        if (hash.length > 0) {
            ObjCLog("sendRawTransaction hash: " + hash + "    UUID: " + UUID  + " from: " + externalAccout + " to: " + contractInstance.address + " privateKey: " + privateKeyHex + " data: " + executeData);
            TransactionSender._UUIDHash[hash] = UUID;
        }


        try {
            onMappingHashUUID(UUID, hash);
        } catch (e) {

        }


        // var receipt = web3.eth.getTransactionReceipt("0xdaef75dce2b804a19887eda3486322d861d026e9ab961e3a253f31f1db39dc7d");
        // var receiptJSON = JSON.stringify(receipt)
        // ObjCLog("Transaction hash :>>>>>>" + receiptJSON);

        //var event = contractInstance[eventName](
            // { _info: contractInstance.address },
            // {fromBlock: 0, toBlock: 'latest'}
        //);


        // event.watch(function(error, result) {
        //     var resultJSON = JSON.stringify(result)
        //     try {
        //         onContractEvent(resultJSON)
        //     } catch (e) {

        //     }
        // });


        // var myResults = event.get(function(error, logs) {
        //     var resultJSON = JSON.stringify(result)
        //     ObjCLog("----------------event.get------------------------->" + resultJSON);
        // });
        // ObjCLog("myResults" + JSON.stringify(myResults));
    }



    TransactionSender.watchEvent = function(contractName, eventName) {

        var web3 = TransactionSender.getWeb3jObj();
        var contractInstance = ContractManager.getContract(contractName).storeObj;
        var eventKey = contractName+eventName;
        ObjCLog("eventKey:"+eventKey);
        var event = TransactionSender._eventObj[eventKey];
        if ((typeof(x) == "undefined")) {
            event = contractInstance[eventName](
                 { _info: contractInstance.address },
                 {fromBlock: 0, toBlock: 'latest'}
            );
            ObjCLog("eventTransactionSender.watchEvent aways create event object!");
            TransactionSender._eventObj[eventKey] = event;
        }else{
            ObjCLog("eventTransactionSender.watchEvent using exist event object!");
        }

        event.watch(function(error, result) {
            var resultJSON = JSON.stringify(result)
            try {
                onContractEvent(resultJSON)
            } catch (e) {

            }
        });

    }


    TransactionSender.stopWatch = function(contractName, eventName) {
        var eventKey = contractName+eventName;
        var event = TransactionSender._eventObj[eventKey];
        if (typeof(event) == "undefined") {
            ObjCLog("eventTransactionSender.stopWatch can't find event object!");
        }

        event.stopWatching(function() {
            ObjCLog("eventTransactionSender.stopWatch did stop watcher!");
        });
    }



















})();
