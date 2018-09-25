package worhavah.certs.tools

enum class UserCertStatus {
    NONE,
    INCOMPLETE,
    DONE,
    ;

    fun isProcessing(): Boolean {
        return this == INCOMPLETE
    }
}