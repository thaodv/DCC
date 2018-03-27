var TransactionList = function() {

    const BIGDECIMAL_18_ZERO = new BigNumber(10).pow(18);
    var txTable = null;
    var $txTable = $("#table-transaction");

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
            pageLength: 20,
            ajax: function (data, callback) {
                _draw = data.draw;
                var params = {
                    page: (data.start / data.length) + 1,
                    pageSize: 20
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
                    if (data) {
                        return '<a href="account.html?address={0}">{1}</a>'.format(data, AddressUtil.trimAddress(data));
                    } else {
                        return '';
                    }
                }},
                {data:"toAddress", name:"toAddress", render: function(data) {
                    if (data) {
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
    };

    return {
        init: function() {
            initDataTables();
        }
    };
}();

jQuery(document).ready(function() {
    TransactionList.init();
});