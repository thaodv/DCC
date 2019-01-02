package worhavah.regloginlib.tools

import java.util.regex.Pattern


private val passwordPattern = Pattern.compile("^[A-Za-z0-9`~!@#$%^&*()+=|{}':;',\"\\[\\].\\\\<>/\\-_?~]+$")// ???

fun isPasswordValid(pw: String?): Boolean {
    return pw != null && pw.length in (8..20) && passwordPattern.matcher(pw).matches()
}

fun isPhoneNumValid(phoneNum: String?): Boolean {
    return phoneNum != null && phoneNum.length == 11
}
