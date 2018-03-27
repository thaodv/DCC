var Account = function() {

    const BIGDECIMAL_18_ZERO = new BigNumber(10).pow(18);
    var address = null;
    var tradeTable = null;
    var $tradeTable = $("#table-trade");
    var transferTable = null;
    var $transferTable = $("#table-transfer");
    var block = null;

    var initParam = function() {
        address = getQueryString('address');
        if (!address) {
            alert("参数错误");
        }
        $("#address,#address2").text(address);
    };

    var fetchBalance = function() {

        $.ajax({
            url: baseUrl + "/juzix/tokenBalance/dcc/" + address,
            type: "GET",
            success: function (data) {
                var d = data.result;
                $("#dcc-balance").text(new BigNumber(d).dividedBy(BIGDECIMAL_18_ZERO).toFixed());
            }
        });
    };

    var initDataTables = function() {
        var _draw = null;

        var defaultOptions = {
            serverSide: true,
            ordering: false,
            searching: false,
            processing: true,
            info: false,
            paging: true,
            dom: 'rt<"dt-bottom"<"pull-right custom-pagination"p>>'
        };

        tradeTable = $tradeTable.DataTable($.extend({}, defaultOptions, {
            ajax: function (data, callback) {
                _draw = data.draw;
                var params = {
                    address: address,
                    transactionType: 'TRADE',
                    page: (data.start / data.length) + 1,
                    pageSize: 10
                };
                $.ajax({
                    url: baseUrl + "/juzix/transaction",
                    type: "POST",
                    data: params,
                    success: function (data) {
                        var d = data.result;
                        $("#total-trade-num").text(d.totalElements);
                        callback({
                            draw: _draw,
                            recordsTotal: d.totalElements,
                            recordsFiltered: d.totalElements,
                            data: d.items == null ? [] : d.items
                        });
                    }
                });
            },
            columns : [
                {data:"hash", name:"hash", render: function(data) {
                    return '<a href="transaction.html?hash={0}">{1}</a>'.format(data, AddressUtil.trimAddress(data));
                }},
                {data:"fromAddress", name:"fromAddress", render: function(data) {
                    if (data === address) {
                        return  AddressUtil.trimAddress(data);
                    }
                    return '<a href="account.html?address={0}">{1}</a>'.format(data, AddressUtil.trimAddress(data));
                }},
                {data:"toAddress", name:"toAddress", render: function(data) {
                    if (data) {
                        if (data === address) {
                            return  AddressUtil.trimAddress(data);
                        }
                        return '<a href="account.html?address={0}">{1}</a>'.format(data, AddressUtil.trimAddress(data));
                    } else {
                        return 'Contract Creation';
                    }
                }},
                {data:"blockNumber", name:"blockNumber", render: function(data) {
                    return '<a href="block.html?search={0}">{0}</a>'.format(data);
                }},
                {data:"dccValue", name:"dccValue", className:"td-no-wrap", render: function(data) {
                    if (data) {
                        return new BigNumber(data).dividedBy(BIGDECIMAL_18_ZERO).toFixed() + " DCC";
                    }
                    return "";
                }},
                {data:"blockTimestamp", name:"blockTimestamp", render: function(data) {
                    if (data) return dateDiff(data);
                    return '';
                }}
            ]
        }));

        transferTable = $transferTable.DataTable($.extend({}, defaultOptions, {
            ajax: function (data, callback) {
                _draw = data.draw;
                var params = {
                    address: address,
                    page: (data.start / data.length) + 1,
                    pageSize: 10
                };
                $.ajax({
                    url: baseUrl + "/juzix/tokenTransfer/dcc",
                    type: "POST",
                    data: params,
                    success: function (data) {
                        var d = data.result;
                        $("#total-transfer-num").text(d.totalElements);
                        callback({
                            draw: _draw,
                            recordsTotal: d.totalElements,
                            recordsFiltered: d.totalElements,
                            data: d.items == null ? [] : d.items
                        });
                    }
                });
            },
            columns : [
                {data:"transactionHash", name:"transactionHash", render: function(data) {
                    return '<a href="transaction.html?hash={0}">{1}</a>'.format(data, AddressUtil.trimAddress(data));
                }},
                {data:"fromAddress", name:"fromAddress", render: function(data) {
                    if (data === address) {
                        return  AddressUtil.trimAddress(data);
                    }
                    return '<a href="account.html?address={0}">{1}</a>'.format(data, AddressUtil.trimAddress(data));
                }},
                {data:"toAddress", name:"toAddress", render: function(data) {
                    if (data === address) {
                        return  AddressUtil.trimAddress(data);
                    }
                    return '<a href="account.html?address={0}">{1}</a>'.format(data, AddressUtil.trimAddress(data));
                }},
                {data:"blockNumber", name:"blockNumber", render: function(data) {
                    return '<a href="block.html?search={0}">{0}</a>'.format(data);
                }},
                {data:"value", name:"value", className:"td-no-wrap", render: function(data) {
                    if (data) {
                        return new BigNumber(data).dividedBy(BIGDECIMAL_18_ZERO).toFixed() + " DCC";
                    }
                    return "";
                }},
                {data:"blockTimestamp", name:"blockTimestamp", render: function(data) {
                    if (data) return dateDiff(data);
                    return '';
                }}
            ]
        }));
    };

    var fixWidth = function() {
        $("#tab-transfer").click(function(){
           $transferTable.width($tradeTable.width());
        });
    };

    return {
        init: function() {
            initParam();
            fetchBalance();
            initDataTables();
            fixWidth();
        }
    };
}();

jQuery(document).ready(function() {
    Account.init();
});