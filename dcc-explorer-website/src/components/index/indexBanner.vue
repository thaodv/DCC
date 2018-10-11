<template>
  <div class="banner-index row">
          <div class="container bannerWrap">
              <div class="col-md-8 chart-index">
                  <div class="chart-title">24h Transaction History</div>
                  <canvas id="chart-trend"></canvas>
                    <Loading v-show="canvasLoad"></Loading>
              </div>
              <div class="col-md-4">
                  <table class="table-banner">
                      <thead>
                          <tr><th colspan="2">Network Status<!--&nbsp;&nbsp;<small><span id="update-network-time">0</span>s前更新</small>--></th></tr>
                      </thead>
                      <tbody>
                          <tr><td>Peer Number：</td><td><span id="peer-number">{{this.nodeNumber}}</span></td></tr>
                          <tr><td>Block Height：</td><td><span id="block-number">{{this.blockNumber}}</span></td></tr>
                          <tr><td>Block Time：</td><td><span id="gene-block-time">{{this.geneBlockTime}}</span>s</td></tr>
                          <tr><td>Consensus Time：</td><td><span id="consensus-time">{{this.consensusTime}}</span>s</td></tr>
                      </tbody>
                  </table>
                  <div class="db-tx-number">
                      <div class="header">Total Txns</div>
                      <div class="body"><span id="total-txn">{{this.totalTransactionNumber | percent()}}</span></div>
                  </div>
              </div>
          </div>
    </div>
</template>
<script>
  import Chart from 'chart.js'
  import chartData from '../../data/chartData'
  import Loading from '../../common/loading'
export default {
  data(){
      return{
          points:[],
          labels:[],
          datas:[],
          nodeNumber:0,
          blockNumber:0,
          geneBlockTime:0,
          consensusTime:0,
          totalTransactionNumber:0,
          time:1,//判读是初次绘制图形还是更新图形,
          banner:{},
          chart:{},
          canvasLoad:false,//canvas加载状态


      }
  },
  components:{Loading},
  methods:{
    drawLine(){
        var ctx = document.getElementById("chart-trend");
        for (var i = 0; i < this.points.length; i++) {
            this.labels.push(this.points[i].x + "'");
            this.datas.push(this.points[i].y);
        }
        this.chart  = new Chart(ctx, {
            type: 'line',
            data: {
                labels: this.labels,
                datasets: [{
                    label: 'Txn',
                    data: this.datas,
                    fillColor : "#fff"
                }]
            },
            options: {
                legend: {
                    display: false
                },
                
                layout: {
                    padding: {
                        // left: 20,
                        // right: 50
                    }
                },
                elements: {
                    point: {
                        backgroundColor: '#c2b7e4'
                    },
                    line: {
                        tension: 0,
                        borderColor: '#c2b7e4'
                    }
                 
                },
                scales: {
                    xAxes: [{
                        gridLines: {
                            display: true,
                            zeroLineColor: '#c2b7e4',
                            zeroLineWidth: 1
                        },
                        ticks: {
                            fontColor: '#c2b7e4'
                        }
                    }],
                    yAxes: [{
                        gridLines: {
                            display: true,
                            zeroLineColor: '#fff',
                            color: '#4d4d4d9d',
                            zeroLineWidth: 1
                        },
                        ticks: {
                            fontColor: '#c2b7e4'
                        }
                    }]
                }
            }
        });

    },
    initData(){
        const that = this;
            this.$axios.get("/statistics/index").then((json)=>{
             if (json.systemCode == "SUCCESS") {
                if (json.businessCode == "SUCCESS") {
                    this.canvasLoad = false;
                    this.nodeNumber = json.result.nodeNumber;
                    this.blockNumber = json.result.blockNumber;
                    this.geneBlockTime = json.result.geneBlockTime / 1000;
                    this.consensusTime = json.result.consensusTime / 1000;
                    this.totalTransactionNumber = json.result.totalTransactionNumber;
                    this.points =json.result.trendList;
                    this.time == 1?this.drawLine():this.chart.update();
                    this.time = 2;
                } 
                } 
            })
       },
    loopData(){
        this.banner = setInterval(this.initData,15000)
    }
  },
  mounted(){
      this.canvasLoad = true;
        this.initData();
        this.loopData();
    
  },
  destroyed(){
       clearInterval(this.banner);
  }
}
</script>
<style>
.banner-index .container1 > div,.banner-index .container2 > div,.banner-index .container3 > div{
    background:#fff;
}
</style>

