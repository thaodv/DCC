<template>
  <div class="row bottom-index" >
        <div class="col-xs-12 col-sm-10  col-lg-8 panel panel-default">
            <div class="panel-heading">
                <img src="../../assets/images/transactions.svg" alt="transactions">
                <h3 class="panel-title explorer-panel-title pull-left">&nbsp;&nbsp;Transactions</h3>
                <router-link to="transaction"  class="indexMore">More</router-link>
            </div>
            <div class="panel-body">
                <table id="table-transaction" class="table table-striped table-hover table-index">
                    <thead>
                    <tr>
                        <th> Hash </th>
                        <th> From </th>
                        <th> To </th>
                        <th> Block Height </th>
                        <th> Amount </th>
                        <th> Age </th>
                    </tr>
                    </thead>
                </table>
            </div>
        </div>
</div>
</template>

