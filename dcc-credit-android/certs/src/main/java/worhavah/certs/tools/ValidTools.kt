package worhavah.certs.tools
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Created by sisel on 2018/2/9.
 */

object  ValidTools {
        @JvmStatic
        fun    isEmailValid(email:String ) :Boolean{
                var isValid = false
                var expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"
                var inputStr:CharSequence = email
                var pattern :Pattern= Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
                var matcher:Matcher = pattern.matcher(inputStr)
                if (matcher.matches()) {
                        isValid = true
                }
                return isValid
        }
        @JvmStatic
        fun isNumeric(str:String ):Boolean {
                for (  c in str.toCharArray()) {
                        if (!Character.isDigit(c))
                                return false
                }
                return true
        }


}