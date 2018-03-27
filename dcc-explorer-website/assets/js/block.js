var TransactionList = function() {

    const BIGDECIMAL_18_ZERO = new BigNumber(10).pow(18);
    var search = null;
    var txTable = null;
    var $txTable = $("#table-transaction");
    var block = null;

    var initParam = function() {
        search = getQueryString('search');
        if (!search) {
            alert("参数错误");
        }
    };

    var handleBlockNav = function() {
        $.ajax({
            url: baseUrl + "/juzix/block/number",
            type: "GET",
            success: function (data) {
                var d = data.result;
                if (search == d) {
                    $("#block-next").hide();
                }
                if (search == 0) {
                    $("#block-pre").hide();
                }
            }
        });
    };

    var initBlock = function() {

        $.ajax({
            url: baseUrl + "/juzix/block/" + search,
            type: "GET",
            success: function (data) {
                var d = data.result;
                block = d;
                $("#block-number").text(d.blockNumber);
                $("#block-number2").text(d.blockNumber);
                $("#block-pre").attr("href", 'block.html?search=' + (d.blockNumber == 0 ? 0 : d.blockNumber - 1));
                $("#block-next").attr("href", 'block.html?search=' + (d.blockNumber + 1));
                $("#block-hash").text(d.hash);
                $("#block-timestamp").text(new Date(d.blockTimestamp).format('yyyy-MM-dd hh:mm:ss'));
                $("#block-txn").text(d.transactionCount);
                initDataTables();
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

        txTable = $txTable.DataTable($.extend({}, defaultOptions, {
            ajax: function (data, callback) {
                _draw = data.draw;
                var params = {
                    blockNumber: block.blockNumber,
                    page: (data.start / data.length) + 1,
                    pageSize: 10
                };
                $.ajax({
                    url: baseUrl + "/juzix/transaction",
                    type: "POST",
                    data: params,
                    success: function (data) {
                        var d = data.result;
                        $("#total-block").text(d.totalElements);
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
                    return '<a href="account.html?address={0}">{1}</a>'.format(data, AddressUtil.trimAddress(data));
                }},
                {data:"toAddress", name:"toAddress", render: function(data) {
                    if (data) {
                        return '<a href="account.html?address={0}">{1}</a>'.format(data, AddressUtil.trimAddress(data));
                    }
                    return 'Contract Creation';
                }},
                {data:"blockNumber", name:"blockNumber"},
                {data:"dccValue", name:"dccValue", className:"td-no-wrap", render: function(data) {
                    if (data) {
                        return new BigNumber(data).dividedBy(BIGDECIMAL_18_ZERO).toFixed() + " DCC";
                    }
                    return "";
                }},
                {data:"blockTimestamp", name:"blockTimestamp", width: "150px", render: function(data) {
                    if (data) return dateDiff(data);
                    return '';
                }}
            ]
        }));
    };

    return {
        init: function() {
            initParam();
            handleBlockNav();
            initBlock();
        }
    };
}();

jQuery(document).ready(function() {
    TransactionList.init();
});