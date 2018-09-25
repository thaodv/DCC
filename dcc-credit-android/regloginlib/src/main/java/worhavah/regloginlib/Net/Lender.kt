package worhavah.regloginlib.Net

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Lender(
    @SerializedName("code")
    val code: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("logoUrl")
    val logoUrl: String,
    @SerializedName("defaultConfig")
    val defaultConfig: Boolean
): Serializable