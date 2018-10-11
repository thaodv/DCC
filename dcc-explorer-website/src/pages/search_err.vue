<template>
  <div class="container nofound">
    <div class="row col-lg-8" style="float:none">
        <div class="col-md-12 search-error">
            <div class="row search-error-wrap">
                <div class="icon-search-bar-warning"></div>
                <input type="text" id="search-bar-error" v-model="error" @keyup.enter="search()"/>
                <div id="btn-search-bar-error" class="col-sm-12" @click="search()">
                    <div class="icon-search-bar-error"></div>
                </div>
                <div style="clear: both"></div>
            </div>
            <div class="row">
                <div class="col-md-12 search-error-tips">
                    Sorry! This is an invalid search string.
                </div>
            </div>
        </div>
    </div>

</div>
</template>
<script>
export default {
    data(){
        return{
            error:""
        }
    },
    methods:{
        search(){
        this.$emit('change',this.error);
    }
    }

}
</script>

<style>
.nofound+footer{
    display:none;
}
</style>
