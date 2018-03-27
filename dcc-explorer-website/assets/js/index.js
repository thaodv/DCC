var Index = function() {

    const BIGDECIMAL_18_ZERO = new BigNumber(10).pow(18);
    var blockTable = null;
    var $blockTable = $("#table-block");
    var txTable = null;
    var $txTable = $("#table-transaction");
    var chart = null;

    var latestUpdateTime = new Date().getTime();

    var refreshDatas = function() {
        setInterval(function() {
            $.ajax({
                url: baseUrl + "/juzix/statistics/index",
                type: "GET",
                success: function (data) {
                    var d = data.result;
                    updateChart(d.trendList);
                    $("#block-number").text(d.blockNumber);
                    $("#peer-number").text(d.nodeNumber);
                    $("#total-txn").text(d.totalTransactionNumber);
                    $("#gene-block-time").text(d.geneBlockTime / 1000);
                    $("#consensus-time").text(d.consensusTime / 1000)
                }
            });
            $blockTable.DataTable().ajax.reload();
            $txTable.DataTable().ajax.reload();

            /*var now = new Date().getTime();
            $("#update-network-time").text(parseInt((now - latestUpdateTime) / 1000));
            latestUpdateTime = now;*/

        }, 30000);
    };

    var initTopData = function() {
        $.ajax({
            url: baseUrl + "/juzix/statistics/index",
            type: "GET",
            success: function (data) {
                var d = data.result;
                handleChart(d.trendList);
                $("#block-number").text(d.blockNumber);
                $("#peer-number").text(d.nodeNumber);
                $("#total-txn").text(d.totalTransactionNumber);
                $("#gene-block-time").text(d.geneBlockTime / 1000);
                $("#consensus-time").text(d.consensusTime / 1000);
            }
        });
    };

    var updateChart = function(points) {
        var labels = [];
        var datas = [];
        for (var i = 0; i < points.length; i++) {
            labels.push(points[i].x + "'");
            datas.push(points[i].y);
        }
        chart.data.labels = labels;
        chart.data.datasets[0].data = datas;
        chart.update();
    };

    var handleChart = function(points) {
        var labels = [];
        var datas = [];
        for (var i = 0; i < points.length; i++) {
            labels.push(points[i].x + "'");
            datas.push(points[i].y);
        }


        var ctx = document.getElementById("chart-trend").getContext('2d');

        const grd = ctx.createLinearGradient(0, 0, 0, 500);
        grd.addColorStop(0, '#7871ea');
        grd.addColorStop(1, '#26c4fd');

        chart = new Chart(ctx, {
            type: 'line',
            data: {
                labels: labels,
                datasets: [{
                    label: 'Txn',
                    data: datas,
                    fillColor : "#fff"
                }]
            },
            options: {
                legend: {
                    display: false
                },
                elements: {
                    point: {
                        backgroundColor: '#7871ea'
                    },
                    line: {
                        tension: 0,
                        borderColor: '#7871ea',
                        backgroundColor: grd
                    }
                },
                scales: {
                    xAxes: [{
                        gridLines: {
                            display: true,
                            zeroLineColor: '#1d6881',
                            zeroLineWidth: 3
                        },
                        ticks: {
                            fontColor: '#7871ea'
                        }
                    }],
                    yAxes: [{
                        gridLines: {
                            display: true,
                            zeroLineColor: '#1d6881',
                            color: '#4d4d4d9d',
                            zeroLineWidth: 3
                        },
                        ticks: {
                            fontColor: '#7871ea'
                        }
                    }]
                }
            }
        });

        //updateChart(points);
    };

    var initDataTables = function() {
        var _draw = null;

        var defaultOptions = {
            serverSide: true,
            ordering: false,
            searching: false,
            processing: true,
            info: false,
            paging: false,
            dom: 'rt<"dt-bottom"<"dt-info"i><"dt-page-size"l>p<"dt-clear">><"dt-clear">',
        };

        blockTable = $blockTable.DataTable($.extend({}, defaultOptions, {
            ajax: function(data, callback) {
                _draw = data.draw;
                $.ajax({
                    url: baseUrl + "/juzix/block",
                    type: "POST",
                    data: {pageSize: 5},
                    success: function(data) {
                        var d = data.result;
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
                {data:"blockNumber", name:"blockNumber", render: function(data) {
                    return '<a href="block.html?search={0}">{1}</a>'.format(data, data);
                }},
                {data:"hash", name:"hash"},
                {data:"transactionCount", name:"transactionCount"},
               /* {data:"gasLimit", name:"gasLimit"},*/
                {data:"blockTimestamp", name:"blockTimestamp", render: function(data) {
                    if (data) return dateDiff(data);
                    return '';
                }}
            ]
        }));

        txTable = $txTable.DataTable($.extend({}, defaultOptions, {
            ajax: function(data, callback) {
                _draw = data.draw;
                $.ajax({
                    url: baseUrl + "/juzix/transaction",
                    type: "POST",
                    data: {pageSize: 5},
                    success: function(data) {
                        var d = data.result;
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
                    return '<a href="transaction.html?hash={0}" >{1}</a>'.format(data, AddressUtil.trimAddress(data));
                }},
                {data:"fromAddress", name:"fromAddress", render: function(data) {
                    if (data) {
                        return '<a href="account.html?address={0}" >{1}</a>'.format(data, AddressUtil.trimAddress(data));
                    } else {
                        return '';
                    }
                }},
                {data:"toAddress", name:"toAddress", render: function(data) {
                    if (data) {
                        return '<a href="account.html?address={0}" >{1}</a>'.format(data, AddressUtil.trimAddress(data));
                    } else {
                        return '';
                    }
                }},
                {data:"blockNumber", name:"blockNumber", render: function(data) {
                    return '<a href="block.html?search={0}" >{1}</a>'.format(data, data);
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
            initTopData();
            initDataTables();
            refreshDatas();
        }
    };
}();

jQuery(document).ready(function() {
    Index.init();
});