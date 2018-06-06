(function() {

    Contract = {
        ABI: [],
        address: "",
        name: "",
        version: "",
        storeObj: Object
    };

    ContractManager = {
        _contracts: {}
    };

    ContractManager.initRegisterContract = function(ABI, address, version) {
        //ObjCLog(">>>>>>>>begin load register"+ABI);
        var web3 = TransactionSender.getWeb3jObj();
        var REGContract = web3.eth.contract(ABI);
        regAddress = address;
        _registerInstance = REGContract.at(regAddress);

        var registerInstance = Object.create(Contract);
        registerInstance.ABI = ABI;
        registerInstance.address = address;
        registerInstance.version = version;
        registerInstance.storeObj = _registerInstance;

        ContractManager._contracts["RegisterManager"] = registerInstance;
        //ObjCLog(" contractName: RegisterManager" + " address:" + address);
        ObjCLog(">>>>>>>>web3" + web3);
        onRegisterInitDone();
    }

    ContractManager.initContract = function(contractName, ABI, address, version) {
        var register = ContractManager._contracts["RegisterManager"].storeObj;

        var queryAddress = register.getContractAddress.call(contractName, version);

        if (queryAddress != "0x0000000000000000000000000000000000000000") {
            address = queryAddress;
            ObjCLog("use queryAddress");
        }else if(address === null || address == "(null)") {
            address = "0x0000000000000000000000000000000000000000";
            ObjCLog("use zero address");
        }else{
            ObjCLog("use default address");
        }
        ObjCLog("queryAddress:" + queryAddress + " contractName:" + contractName + " version: " + version + " using address:" + address);

        var web3 = TransactionSender.getWeb3jObj();
        var ContractABI = web3.eth.contract(ABI);
        contractInstance = ContractABI.at(address);

        var newInstance = Object.create(Contract);
        newInstance.ABI = ABI;
        newInstance.address = address;
        newInstance.version = version;
        newInstance.storeObj = contractInstance;

        ContractManager._contracts[contractName] = newInstance;
        onContractsInitDone(contractName);
    }

    ContractManager.getContract = function(contractName) {
        return ContractManager._contracts[contractName];
    }





})();
