$(function(){
    var $windowH = parseInt($(window).height())
    var $windwW = parseInt($(window).width())

    function Render(){
        $("#banner").css("height",$windowH+"px")
        $("body").css("width",$windwW+"px");
        if($windwW > 1023){
            if($windowH <600){
                $(".banner .BannerLogo").css("marginBottom","-223px");
            }else{
                $(".banner .BannerLogo").css("marginBottom","-193px");
            }
        }

    }
    // Render();

    if(Number($(document).scrollTop()) == 0){
        if($windwW > 767){
            $(".navbar-inverse").css("borderColor","transparent")
        }

    }
    $(window).resize(function() {
        $windowH = parseInt($(window).height())
        $windwW = parseInt($(window).width())
        // Render()
    });

    $("body").children().click(function () {
        //这里不要写任何代码  解决iPhone safari中Document事件不触发的解决方案 [冒泡]
    });
    $(document).bind("click", function(e) {
        if($(e.target).closest(".queryInvest").length == 0){
            // $(".investPopover").hide();
            // $(".queryInvest").css({"marginBottom":"50px"})
        }
        //  if($(e.target).closest(".queryInvest").length == 0){

        // }
        // $('#navbar').collapse('hide');





    });



    $("#navbar .navlist").click(function(){
        var Index = $(this).index();
        $(this).addClass("active").siblings().removeClass("active");
        var $windwW = parseInt($(window).width())
        var scrollTopV = 0;
        if($windwW < 768){
            $("#navbar").removeClass("in");
            scrollTopV = 50;
        }else{
            $("#navbar").addClass("in");
            scrollTopV = 105;
        }

        // switch(Index){
        //     case 0:
        //         var ScrollTop = Number($("#banner").offset().top)+"px"

        //         $("html,body").animate({scrollTop: "0px"}, 2000);

        //         $(".navbar-inverse").show();
        //         break;
        //     case 1:

        //         $("html,body").animate({scrollTop:($(".vision").offset().top-scrollTopV)+"px"}, 2000);


        //         //$(".navbar-inverse").hide();
        //         $(".navbar-inverse").show();
        //         break;
        //     case 2:
        //         $("html,body").animate({scrollTop:($(".CoreTeam").offset().top-scrollTopV)+"px"}, 2000);
        //         // $("html,body").animate({scrollTop: $windowH*2+"px"}, 2000);
        //         //$(".navbar-inverse").hide();
        //         $(".navbar-inverse").show();
        //         break;

        //     case 3:
        //         var ScrollTop = Number($(".Roadmap").offset().top-scrollTopV)+"px"

        //         $("html,body").animate({scrollTop: ScrollTop}, 2000);

        //         $(".navbar-inverse").show();
        //         break;
        //     case 4:
        //         var ScrollTop = Number($(".incentivePlan").offset().top-scrollTopV)+"px"

        //         $("html,body").animate({scrollTop: ScrollTop}, 2000);

        //         $(".navbar-inverse").show();

        //         break;
        //     case 5:
        //         var ScrollTop = Number($(".incentivePlan").offset().top-scrollTopV)+"px"

        //         $("html,body").animate({scrollTop: ScrollTop}, 2000);

        //         $(".navbar-inverse").show();

        //         //  $("#navbar").addClass("in");
        //         //$(".languageMenu").show();
        //         //$(".navbar-inverse").show();
        //         break;
        // }
    })

    var language ={
        changeText:function(lan){
            subscribe();
          $('[set-lan]').each(function(){
            var me = $(this);
            var a = me.attr('set-lan').split(':');
            var p = a[0];   //文字放置位置
            var m = a[1];   //文字的标识
            if(m.indexOf("[")>0){
             var arr = m.split('[');
              m = arr[0];
              var index = arr[1].substring(0,arr[1].length-1);
            }
        
            //选取语言文字
            switch(lan){
                case 'en':
                    var t = en[m];  //这里cn[m]中的cn是上面定义的json字符串的变量名，m是json中的键，用此方式读取到json中的值
                    if(typeof t == "object"){
                      t = en[m][index]
                    }
                    $("#token_pay").attr("src","../token/1.png");
                    $("#token_pie").attr("src","../token/6.png")
                    break;
                case 'vn':
                    var t = vn[m];
                    if(typeof t == "object"){
                      t = vn[m][index]
                     }
                     $("#token_pay").attr("src","../token/vn1.png");
                     $("#token_pie").attr("src","../token/vn6.png");
                    break;
                case 'kn':
                var t = kn[m];
                if(typeof t == "object"){
                    t = kn[m][index]
                    }
                    $("#token_pay").attr("src","../token/1.png");
                    $("#token_pie").attr("src","../token/6.png");
                break;
                default:
                    var t = en[m];
            }
        
            //如果所选语言的json中没有此内容就选取其他语言显示
            if(t==undefined) t = en[m];
        
            if(t==undefined) return true;   //如果还是没有就跳出
        
            //文字放置位置有（html,val等，可以自己添加）
            switch(p){
                case 'html':
                    me.html(t);
                    break;
                case 'val':
                case 'value':
                    me.val(t);
                    break;
                default:
                    me.html(t);
            }
        
          });
        }
      }
    //   pc
        $(".selLang li").on("click",function(){
            var value = $(this).html();
            $("#lankind").html(value);
            var lan = $(this).attr('data-lang')
            $.setCookie("lan",JSON.stringify([lan,value]))
            language.changeText(lan);
        })
        var lanVal = JSON.parse($.getCookie('lan'));
        if(lanVal){
            var lan = lanVal;
            $("#lankind").html(lan[1]);
            var $windwW = parseInt($(window).width())
            if($windwW < 768){
                var selArr = $('.h5Lan ul').find("li");
                selArr.each(function(i,val){
                  if(val.innerText == lan[1]){
                    $(this).addClass("h5Active").siblings().removeClass("h5Active");
                  }
                });
            }
            language.changeText(lan[0]);
            
    
        }else{
            // $.setCookie('lan',JSON.stringify(["en","English"]));
            subscribe();
        }
        //h5 
        $(".h5Lan li").on("click",function(){
            var value = $(this).html();
            var lan = $(this).attr('data-lang');
            $(this).addClass("h5Active").siblings().removeClass("h5Active");
            $.setCookie('lan',JSON.stringify([lan,value]))
            language.changeText(lan);
        })

        function subscribe(){
            // h5上邮箱订阅
            var lanVal = JSON.parse($.getCookie('lan'));
            var lan =lanVal?lanVal:["en"];
            var $windowH = parseInt($(window).height())
            var $windwW = parseInt($(window).width())
            if ($windwW < 500) {
                switch(lan[0]){
                case "en":$("#investInput").attr("placeholder", "Your email");break;
                case "vn":$("#investInput").attr("placeholder", "Email của Anh");break;
                case "kn":$("#investInput").attr("placeholder", "메일");break;
                default:break;
                }
            } else {
                switch(lan[0]){
                case "en":$("#investInput").attr("placeholder", "Please enter your email address here");break;
                case "vn":$("#investInput").attr("placeholder", "Hãy nhập địa chỉ email của bạn ở đây.");break;
                case "kn":$("#investInput").attr("placeholder", "메일 주소를 적어주세요");break;
                default:break;
                }
            }
        }



   })