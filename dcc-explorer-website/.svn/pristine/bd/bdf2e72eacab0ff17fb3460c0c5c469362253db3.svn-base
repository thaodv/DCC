
var Decimal = require('decimal.js') 
// 千分符
export let percent = (num)=>{
    if(num== 0) return num;
   if(num){
    let res = num.toString().replace(/(\d)(?=(\d{3})+(\.|$))/g, '$1,');
    return res;
   }
}
//时间轴转换成正常格式
export let format = function(timestamp) {
    timestamp = timestamp== 10 ?timestamp*1000 : timestamp;
    var date = new Date(timestamp),//时间戳为10位需*1000，时间戳为13位的话不需乘1000
    Y = date.getFullYear() + '-',
    M = (date.getMonth()+1 < 10 ? '0'+(date.getMonth()+1) : date.getMonth()+1) + '-',
    D = fillZero(date.getDate()) + ' ',
    h = fillZero(date.getHours()) + ':',
    m = fillZero(date.getMinutes()) + ':',
    s = fillZero(date.getSeconds());
    return Y+M+D+h+m+s;
}
// 补零
function fillZero(val){
    return val<10?'0'+val:val;
}
export let wei = function(num){
    if(num){
        return new Decimal(num).div(new Decimal(10e17)).toNumber();
     }else{
         return 0;
     }
    }
   
//历史时间与当前时间对比
 /**
     * [dateDiff 算时间差]
     * @param  {[type=Number]} hisTime [历史时间戳，必传]
     * @param  {[type=Number]} nowTime [当前时间戳，不传将获取当前时间戳]
     * @return {[string]}         [string]
     */
export let dateDiff = function(hisTime, nowTime){
    var now = nowTime ? nowTime : new Date().getTime(),
        diffValue = now - hisTime,
        result='',
        second = 1000,
        minute = second * 60,
        hour = minute * 60,
        day = hour * 24,

        _day = diffValue / day,
        _dayHour = (diffValue % day) / hour,
        _hour =diffValue / hour,
        _hourMinute = (diffValue % hour) / minute,
        _min =diffValue/minute,
        _second = diffValue / second;


    var age = '';
    if (_day >= 1 && _dayHour >= 1) {
        var intDay = parseInt(_day);
        var intHour = parseInt(_dayHour);
        if (intDay == 1) {
            age += intDay + " day ";
        }
        if (intDay > 1) {
            age += intDay + " days ";
        }
        if (intHour == 1) {
            age += intHour + " hr ago";
        }
        if (intHour > 1) {
            age += intHour + " hrs ago";
        }
        return age;
    }
    if (_day >= 1 && _dayHour < 1) {
        var intDay = parseInt(_day);
        if (intDay == 1) {
            age += intDay + " day ago";
        }
        if (intDay > 1) {
            age += intDay + " days ago";
        }
        return age;
    }
    if (_hour >= 1 && _hourMinute >= 1) {
        var intHour = parseInt(_hour);
        var intMin = parseInt(_hourMinute);
        if (intHour == 1) {
            age += intHour + " hr ";
        }
        if (intHour > 1) {
            age += intHour + " hrs ";
        }
        if (intMin == 1) {
            age += intMin + " min ago";
        }
        if (intMin > 1) {
            age += intMin + " mins ago";
        }
        return age;
    }
    if (_hour >= 1 && _hourMinute < 1) {
        var intHour = parseInt(_hour);
        if (intHour == 1) {
            age += intHour + " hr ago";
        }
        if (intHour > 1) {
            age += intHour + " hrs ago";
        }
        return age;
    }
    if (parseInt(_min) > 1) {
        return parseInt(_min) + " mins ago";
    }
    if (parseInt(_min) == 1) {
        return parseInt(_min) + " min ago";
    }
    if (parseInt(_second) > 1) {
        return parseInt(_second) + " secs ago";
    }
    if (parseInt(_second) == 1) {
        return parseInt(_second) + " sec ago";
    }


   /* if (_day >= 1 && _dayHour >= 1) return parseInt(_day) + "天" + parseInt(_dayHour) + "小时前";
    if (_day >= 1 && _dayHour < 1) return parseInt(_day) + "天前";
    if(_hour >= 1 && _hourMinute >= 1) return parseInt(_hour) + "小时" + parseInt(_hourMinute) + "分前";
    if(_hour >= 1 && _hourMinute < 1) return parseInt(_hour) + "小时前";
    if(_min>=1) return parseInt(_min) + "分钟前";
    return parseInt(_second) + "秒前";*/
}
export let timestamp=function(a,week){
    if(a){
        var d = new Date(a);
        var year = d.getFullYear();
        var month = fillZero(d.getMonth() + 1);
        var day = fillZero(d.getDate());
        var hour = fillZero(d.getHours());
        var minutes = fillZero(d.getMinutes());
        var seconds = fillZero(d.getSeconds());
        if(week == 'yymmdd'){
            return  year+ ':' + month + ':' + day;
        }else{
            return  year+ '-' + month + '-' + day + ' ' + hour + ':' + minutes + ':' + seconds;
        }
    }
    function fillZero(num){
        return num<10?"0"+num: num;
    }
}

export let  trimAddress= function(address){
    if(address && address.length > 17){
        return address.substring(0, 10) + "..." + address.substr(address.length-10);
    }
    return address;
}
