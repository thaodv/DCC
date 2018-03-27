var baseUrl = "http://explorer.dcc.finance/chain-observer/restful";

(function($){
    $.fn.serializeJson=function(){
        var serializeObj={};
        var array=this.serializeArray();
        var str=this.serialize();
        $(array).each(function(){
            if(serializeObj[this.name]){
                if($.isArray(serializeObj[this.name])){
                    serializeObj[this.name].push(this.value);
                }else{
                    serializeObj[this.name]=[serializeObj[this.name],this.value];
                }
            }else{
                serializeObj[this.name]=this.value;
            }
        });
        return serializeObj;
    };
})(jQuery);

Date.prototype.format = function(format){
    /*
     * eg:format="yyyy-MM-dd hh:mm:ss";
     */
    var o = {
        "M+": this.getMonth() + 1,                       // month
        "d+": this.getDate(),                            // day
        "h+": this.getHours(),                           // hour
        "m+": this.getMinutes(),                         // minute
        "s+": this.getSeconds(),                         // second
        "q+": Math.floor((this.getMonth() + 3) / 3),     // quarter
        "S": this.getMilliseconds()                      // millisecond
    };

    if(/(y+)/.test(format)){
        format = format.replace(RegExp.$1, (this.getFullYear() + '').substr(4 - RegExp.$1.length));
    }

    for(var k in o){
        if (new RegExp("(" + k + ")").test(format)){
            format = format.replace(RegExp.$1, RegExp.$1.length == 1 ? o[k] : ("00" + o[k]).substr(("" + o[k]).length));
        }
    }
    return format;
};

function initDataTables(ctx, config) {
    return $("#" + ctx).DataTable({
        ajax: config.ajax,
        serverSide: true,
        ordering:  true,
        searching: false,
        processing: true,
        info: true,
        columns: config.columns,
        initComplete: config.initComplete,

        order: config.order,
        dom: 'rt<"dt-bottom"<"dt-info"i><"dt-page-size"l>p<"dt-clear">><"dt-clear">'

    });
}

function getQueryString(name) {
    var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
    var r = window.location.search.substr(1).match(reg);
    if(r!==null)return  unescape(r[2]); return null;
}


String.prototype.format = function() {
    var str = this;
    for (var i = 0; i < arguments.length; i++) {
        var reg = new RegExp("\\{" + i + "\\}", "gm");
        str = str.replace(reg, arguments[i]);
    }
    return str;
};

(function(window){
    /**
     * [dateDiff 算时间差]
     * @param  {[type=Number]} hisTime [历史时间戳，必传]
     * @param  {[type=Number]} nowTime [当前时间戳，不传将获取当前时间戳]
     * @return {[string]}         [string]
     */
    var dateDiff = function(hisTime, nowTime){
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
    window.dateDiff = dateDiff
})(window);


var AddressUtil = {
    trimAddress: function(address) {
        if(address.length > 17){
            return address.substring(0, 17) + "...";
        }
        return address;
    }
};

function checkResult(result, success, fail) {
    if (result.businessCode === 'SUCCESS' && result.systemCode === 'SUCCESS') {
        success(result.result);
    } else {
        fail(result);
    }
}


/* Nav search bar */
$(function() {
    $('#txt-navbar-search').on('keypress', function(event) {
        if (event.keyCode === 13) {
            $('.navbar-search .icon-search').trigger('click');
        }
    });
    $(".navbar-search .icon-search").click(function() {
        var param = $.trim($("#txt-navbar-search").val());
        $.ajax({
            url: baseUrl + "/juzix/search/" + param,
            type: "GET",
            success: function (data) {
                checkResult(data, function(result) {
                    switch (result.type) {
                        case 'BLOCK': window.location.href = baseContextPath + 'block.html?search=' + result.data.hash; break;
                        case 'TRANSACTION': window.location.href = baseContextPath + 'transaction.html?hash=' + result.data.hash; break;
                        case 'ADDRESS': window.location.href = baseContextPath + 'account.html?address=' + result.data; break;
                    }
                }, function(res) {
                    location.href = baseContextPath + "error_pages/search_error.html?search=" + param;
                });
            }
        });
    });
});


