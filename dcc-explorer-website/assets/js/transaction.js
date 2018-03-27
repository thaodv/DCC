var Transaction = function() {

    var hash = null;
    const BIGDECIMAL_18_ZERO = new BigNumber(10).pow(18);

    var initParam = function() {
        hash = getQueryString('hash');
        if (!hash) {
            alert("参数错误");
        }
    };

    var fetchTransaction = function() {

        $.ajax({
            url: baseUrl + "/juzix/transaction/" + hash,
            type: "GET",
            success: function (data) {
                var d = data.result;
                $("#tx-hash").text(d.hash);
                $("#tx-hash2").text(d.hash);
                $("#block-number").html('<a href="block.html?search={0}">{0}</a>'.format(d.blockNumber));
                $("#from-address").html('<a href="account.html?address={0}">{0}</a>'.format(d.fromAddress));
                if (d.toAddress) {
                    $("#to-address").html('<a href="account.html?address={0}">{0}</a>'.format(d.toAddress));
                } else {
                    $("#to-address").html('Contract Creation');
                }

                $("#block-timestamp").text(new Date(d.blockTimestamp).format("yyyy-MM-dd hh:mm:ss"));
                $("#dcc-value").text(new BigNumber(d.dccValue).dividedBy(BIGDECIMAL_18_ZERO).toFixed());

                fetchTokenTransfer(d.hash);
                //fetchInputData();
            }
        });
    };

    var fetchTokenTransfer = function(txHash) {

        $.ajax({
            url: baseUrl + "/juzix/tokenTransfer/dcc",
            type: "POST",
            data: {page: 1, pageSize: 99999999, transactionHash: txHash},
            success: function (data) {
                var d = data.result;
                if (d.items) {
                    var $token = $(".token-transfer-list").empty();
                    for (var i = 0; i < d.items.length; i++) {
                        var html = '<div class="transfer-item"><span class="dcc-value">{0}</span> DCC from <a href="account.html?address={1}">{2}</a> to <a href="account.html?address={3}">{4}</a></div>';
                        var value = new BigNumber(d.items[i].value).dividedBy(BIGDECIMAL_18_ZERO).toFixed();
                        html = html.format(value,
                            d.items[i].fromAddress,
                            AddressUtil.trimAddress(d.items[i].fromAddress),
                            d.items[i].toAddress,
                            AddressUtil.trimAddress(d.items[i].toAddress));
                        $token.append(html);
                    }
                }
            }
        });
    };

    var decode = function(abi, data) {

        //const abi = JSON.parse(abiInput.value.trim());
        const decoder = new InputDataDecoder(abi);

        // if copied and pasted from etherscan only get data we need
        const data = data.trim()
            .replace(/(?:[\s\S]*MethodID: (.*)[\s\S])?[\s\S]?\[\d\]:(.*)/gi, '$1$2')

        dataInput.value = data

        const result = decoder.decodeData(data);

        console.log(result)

        try {
            return JSON.stringify(result, null, 2);
        } catch(error) {
        }
    };

    var fetchInputData = function() {
        $.ajax({
            url: baseUrl + "/juzix/transaction/data/" + hash,
            type: "GET",
            success: function (data) {
                var d = data.result;
                $("#tx-input-data").val(decode(d.abi, d.inputData));
            }
        });
    };

    return {
        init: function() {
            initParam();
            fetchTransaction();
        }
    };
}();

jQuery(document).ready(function() {
    Transaction.init();
});