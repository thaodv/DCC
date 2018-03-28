package io.wexchain.passport.gateway.ctrlr.test;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by wuxinxin on 2018/1/18.
 */
@Controller
public class HelloController{

    @ResponseBody
    @RequestMapping("/hello")
    public  String m1() {
        return "success";
    }
}
