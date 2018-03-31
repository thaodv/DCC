package com.wexmarket.android.network.cookie

import java.net.HttpCookie
import java.net.URI

/**
 * Created by lulingzhi on 2017/9/30.
 */

const val HTTP_PATH_SEPARATOR: Char = 0x2F.toChar()
const val HTTP_SECURE_SCHEME: String = "https"

fun HttpCookie.matches(uri: URI): Boolean {
    return domainMatches(this, uri.host) &&
            pathMatches(this.path, uri.path) &&
            secureMatches(this.secure, uri.scheme)
}

private fun secureMatches(secure: Boolean, scheme: String): Boolean {
    return !secure || scheme.equals(HTTP_SECURE_SCHEME, ignoreCase = true)
}

/**
 * <a href="https://tools.ietf.org/html/rfc6265#section-5.1.4"/>
 */
private fun pathMatches(cookiePath: String, requestPath: String): Boolean {
    return cookiePath == requestPath ||
            (requestPath.startsWith(cookiePath) &&
                    (cookiePath.endsWith(HTTP_PATH_SEPARATOR) || requestPath[cookiePath.length] == HTTP_PATH_SEPARATOR))
}

private fun domainMatches(cookie: HttpCookie, host: String?): Boolean {
    return when (cookie.version) {
        0 -> matchAccordingToObsoleteRFC2109(cookie.domain, host)
        1 -> HttpCookie.domainMatches(cookie.domain, host)
        else -> false
    }
}

/**
 * @see [java.net.InMemoryCookieStore.netscapeDomainMatches]
 */
private fun matchAccordingToObsoleteRFC2109(domain: String?, host: String?): Boolean {

    if (domain == null || host == null) {
        return false
    }

    // if there's no embedded dot in domain and domain is not .local
    val isLocalDomain = ".local".equals(domain, ignoreCase = true)
    var embeddedDotInDomain = domain.indexOf('.')
    if (embeddedDotInDomain == 0) {
        embeddedDotInDomain = domain.indexOf('.', 1)
    }
    if (!isLocalDomain && (embeddedDotInDomain == -1 || embeddedDotInDomain == domain.length - 1)) {
        return false
    }

    // if the host name contains no dot and the domain name is .local
    val firstDotInHost = host.indexOf('.')
    if (firstDotInHost == -1 && isLocalDomain) {
        return true
    }

    val domainLength = domain.length
    val lengthDiff = host.length - domainLength
    return when {
        lengthDiff == 0 -> // if the host name and the domain name are just string-compare euqal
            host.equals(domain, true)
        lengthDiff > 0 -> {
            // need to check H & D component
            val H = host.substring(0, lengthDiff)
            val D = host.substring(lengthDiff)

            D.equals(domain, ignoreCase = true)
        }
        lengthDiff == -1 -> // if domain is actually .host
            domain[0] == '.' && host.equals(domain.substring(1), true)
        else -> false
    }

}