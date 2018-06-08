(function() {

    WalletManager = {
        _from: "",
        _publicly: "",
        _privateKey: "",
        _isActivate: false,
        _web3: {},
    };

    WalletManager.loadWalletFile = function(walletJson, password, UUID) {
        var keythereum = window.keythereum;
        var keyObject = eval("(" + walletJson + ')');
        var generatedKey;
        try {
            generatedKey = keythereum.recover(password, keyObject);
        } catch (e) {
            onKeyError(UUID, e);
        }
        var generatedKey = keythereum.recover(password, keyObject);
        WalletManager._from = keyObject.address;
        WalletManager._privateKey = generatedKey;
        WalletManager._isActivate = true;
        ObjCLog("🍺" + "recoverKey:" + generatedKey + " address: " + keyObject.address);
    }

    WalletManager.cleanWalletFile = function() {
        WalletManager._from = "";
        WalletManager._privateKey = "";
        WalletManager._isActivate = false;
    }

    WalletManager.recoveryPrivateKey = function(walletJson, password) {

    }

    WalletManager.recoveryPublicKey = function(walletJson, password) {

    }

    WalletManager.generateWallet = function(password) {

        /*
        var keythereumObj = window.keythereum;
        var params = {
            keyBytes: 32,
            ivBytes: 16
        };
        var dk = keythereumObj.create(params);
        var options = {
            kdf: "scrypt",
            cipher: "aes-128-ctr",
            kdfparams: {
                n: (1 << 4),
                dklen: 32,
                r: 8,
                p: 1,
                prf: "hmac-sha256"
            }
        };
        ObjCLog("begin dump");
        keythereumObj.dump(password, dk.privateKey, dk.salt, dk.iv, options, function(keyObject) {
            var obj = JSON.stringify(keyObject);
            //onKeyGenerateCallback(obj);
            ObjCLog("wallet:"+obj);
        });
        */
        
        var keythereumObj = window.keythereum;

        var params = {
            keyBytes: 32,
            ivBytes: 16
        };

        // synchronous
        var dk = keythereumObj.create(params);
        var n = 1<<4;
        ObjCLog("kdfparams n:"+n);
        var options = {
            kdf: "scrypt",
            cipher: "aes-128-ctr",
            kdfparams: {
                n: n,
                dklen: 32,
                prf: "hmac-sha256"
            }
        };
        ObjCLog("begin_dump");
        var keyObject = keythereumObj.dump(password, dk.privateKey, dk.salt, dk.iv, options);
        ObjCLog("end_dump");
        return JSON.stringify(keyObject);

        // asynchronous
        // keythereumObj.dump("1111", dk.privateKey, dk.salt, dk.iv, options, function(keyObject) {
        //     var obj = JSON.stringify(keyObject);
        // });

    }

    WalletManager.getFromAddress = function() {
        if (WalletManager._from.length > 0) {
            return WalletManager._from;
        }
        return "0x0098c263c4498c9c544c88b1357e3b15ce3c3444";
    }

    WalletManager.getPrivateKey = function() {
        if (WalletManager._privateKey.length > 0) {
            return WalletManager._privateKey;
        }
        return "f8ebf968d3c91ec8ee4c6fee44de1d43464c4f0268bcc148c46056e87e649143";
    }

})();
