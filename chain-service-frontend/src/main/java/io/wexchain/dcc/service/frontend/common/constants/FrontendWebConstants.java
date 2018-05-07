package io.wexchain.dcc.service.frontend.common.constants;

import java.math.BigDecimal;

/**
 * <p>站点常量持有器</p>
 * @author yanyi
 */
public interface FrontendWebConstants {
    /** 地址后缀*/
    static final String URL_SUFFIX                  = ".htm";
    /** 邮箱标识*/
    static final String LAYOUT_TAG                  = "@";
    /**图片验证码*/
    static final String IMG_CAPTCHA_CODE            = "imgCaptchaCode";
    /**exist是否存在*/
    static final String EXIST                       = "exist";
    /**文件路径*/
    static final String FILE_PATH                   = "filePath";
    /**
     * 通用请求参数
     * */
    static final String EMAIL                       = "email";
    /**SessionId*/
    static final String SID                         = "sid";
    /**code*/
    static final String CODE                        = "c";


    /** 营业执照图片*/
    static final String YYZZ                        = "yyzz";
    /** 组织机构代码证图片*/
    static final String ZZJG                        = "zzjg";
    /** 税务登记图片*/
    static final String SWDJ                        = "swdj";
    /** 结算开户许可证图片*/
    static final String JSKH                        = "jskh";
    /** 法人身份证正面*/
    static final String FRSFZZ                      = "frsfzz";
    /** 法人身份证背面*/
    static final String FRSFZB                      = "frsfzb";
    /** 签名版本*/
    static final String SIGN_VERSION                 = "1.0";
    /** 平台加签私钥*/
    static final String PLATFORM_SIGN_PRIVATEKEY                 = "platform.sign.privateKey";
    /** 平台验签公钥*/
    static final String PLATFORM_SIGN_PUBLICKEY                = "platform.sign.publicKey";

    /**
     * 通用常量
     */
    static final String BLANK                 = "";
    static final String SPACE                 = " ";
    static final String BANG                  = "!";
    static final String QUESTION_MARK         = "?";
    static final String COMMA                 = ",";
    static final String POINT                 = ".";
    static final String COLON                 = ":";
    static final String SEMICOLON             = ";";
    static final String QUOTE                 = "'";
    static final String SINGLE_QUOTE          = "\'";
    static final String DOUBLE_QUOTE          = "\"";
    static final String STAR                  = "*";
    static final String PLUS                  = "+";
    static final String DASH                  = "-";
    static final String EQUAL                 = "=";
    static final String SLASH                 = "/";
    static final String BACK_SLASH            = "\\";
    static final String PIPE                  = "|";
    static final String UNDERLINE             = "_";
    static final String DOLOR                 = "$";
    static final String AT                    = "@";
    static final String CROSS_HATCH           = "#";
    static final String PERCENT               = "%";
    static final String AND                   = "&";
    static final String CIRCUMFLEX            = "^";
    static final String TILDE                 = "~";
    static final String LEFT_BRACE            = "{";
    static final String RIGHT_BRACE           = "}";
    static final String LEFT_BRACKET          = "[";
    static final String RIGHT_BRACKET         = "]";
    static final String LEFT_ANGLE_BRACKET    = "<";
    static final String RIGHT_ANGLE_BRACKET   = ">";
    static final String LEFT_PARENTHESES      = "(";
    static final String RIGHT_PARENTHESES     = ")";
    static final String LINE_CHANGE_SYMBOL    = "\n";
    static final String ENTER_SYMBOL          = "\r";
    static final String TAB_SYMBOL            = "\t";
    static final String IMG_JPG            = ".jpg";

    /** 合作方referer */
    String HEADER_REFFER                      = "referer";

    /**
     * 人民币种
     */
    static final String CNY            = "CNY";
    /**
     * 默认域名
     */
    static final String DEFAULT_DOMAIN            = "wexmarket.com";

    /**
     * 默认支持账户类型参数
     */
    static final String DEFAULT_SUPPORTED_ACCOUNT_TYPES           = "BASIC"+COMMA+"SAVING_POT"+COMMA+"BANK";
    /**
     * 默认开始时间
     */
    static final String DEFAULT_SHORT_TIME         = "20170101";

    /**
     * 订单状态成功
     */
    static final String SETTLEMENT_TYPE_SUCCESS = "success";
    /**
     * 订单状态失败
     */
    static final String SETTLEMENT_TYPE_FAIL = "fail";
    /**
     * 订单状态进行中
     */
    static final String SETTLEMENT_TYPE_SOLVED = "solved";
    /**
     * 订单状态出款拒绝
     */
    static final String SETTLEMENT_TYPE_REJECTED = "rejected";


    /**默认每页数据量*/
    static final int DEFAULT_PAGE_SIZE                 = 2000;

    static final BigDecimal AMOUNT_MULTIPLY = new BigDecimal("10000");

    static final String ID = "ID";
    static final String COMMUNICATION_LOG = "COMMUNICATION_LOG";
    static final String BANK_CARD = "BANK_CARD";
}
