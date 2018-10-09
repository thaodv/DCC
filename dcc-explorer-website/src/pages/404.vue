<template>
  <div class="container nofound">
    <div class="row">
        <div class="col-xs-12 col-md-12">
            <div class="error-wrap">
                <div class="error-image">
                    <img src="../assets/images/404.png" />
                </div>
                <div class="error-bottom">
                    <div class="error-txt">Sorry, the page is not exist</div>
                    <div class="error-btn">
                        <router-link to="/index" type="button" class="btn btn-primary">BACK TO HOME</router-link>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
</template>
<style>
.nofound+footer{
    display: none;
}
</style>

