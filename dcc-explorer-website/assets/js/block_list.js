var BlockList = function() {

    var blockTable = null;
    var $blockTable = $("#table-block");

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

        blockTable = $blockTable.DataTable($.extend({}, defaultOptions, {
            pageLength: 20,
            ajax: function (data, callback) {
                _draw = data.draw;
                var params = {
                    page: (data.start / data.length) + 1,
                    pageSize: 20
                };
                $.ajax({
                    url: baseUrl + "/juzix/block",
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
            columns: [
                {data: "blockNumber", name: "blockNumber", render: function (data) {
                    return '<a href="block.html?search={0}">{1}</a>'.format(data, data);
                }},
                {data: "hash", name: "hash"},
                {data: "transactionCount", name: "transactionCount", render: function (data, type, row) {
                    return '<a href="block.html?search={0}">{1}</a>'.format(row.blockNumber, data);
                }},
                {data: "blockTimestamp", name: "blockTimestamp", width: "150px", render: function (data) {
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
    BlockList.init();
});