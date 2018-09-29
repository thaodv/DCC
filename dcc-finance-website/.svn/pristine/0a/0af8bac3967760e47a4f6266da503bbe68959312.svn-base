       
       

$(function () {
  $.extend({
    setCookie:function(key,val){
      var date=new Date();
      var expiresHours=9;
      date.setTime(date.getTime()+expiresHours*24*3600*1000);
      document.cookie=key + "=" + encodeURIComponent(val) +";expires="+date.toGMTString()+"; path=/";
    },
    getCookie:function(key){
      var arr,reg = new RegExp("(^| )"+key+"=([^;]*)(;|$)");
     if(arr = document.cookie.match(reg)){
         return decodeURIComponent(arr[2]);
     }
     return null;
    }
  })
})