package io.wexchain.dcc.service.frontend.common.enums;

import com.weihui.basic.lang.common.lang.StringUtil;

/**
 * Created by yy on 2017/7/18.
 */
public enum ErrorCode {


    SUCCESS("提交成功"),

    /** 提交成功*/
    APPLY_SUCCESS("提交成功"),

    /** 系统内部错误*/
    SYSTEM_ERROR("系统繁忙"),

    /** 参数校验未通过*/
    ILLEGAL_ARGUMENT("参数校验未通过"),

    FILE_UPLOAD_ERROR("文件上传错误"),

    FILE_DOWNLOAD_ERROR("文件下载错误"),


    /** 业务异常开始*/


    IMG_CODE_ERROR("验证码错误"),

    AUTHENTICATION_FAILURE("登录失败"),
    AUTHENTICATION_FORBIDDEN("登录失败"),

    ADMIN_EXIST("用户已存在"),

    OPERATION_FALIED("操作失败"),

    PUT_ADMIN_INFO_FAIL("查询用户信息失败"),

    /** 业务异常结束*/

    /** 其它错误*/
    OTHER_ERROR("其它错误"),

    BUSINESS_LOGIC_ERROR("业务处理异常"),

    FEE_CODE_NOT_EXIST("费用账户不存在")

    ;

    /** 描述 */
    private String message;

    private ErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public static ErrorCode getByName(String name) {
        if (StringUtil.isBlank(name)) {
            return null;
        }

        for (ErrorCode type : values()) {
            if (type.name().equals(name)) {
                return type;
            }
        }
        return null;
    }
}
