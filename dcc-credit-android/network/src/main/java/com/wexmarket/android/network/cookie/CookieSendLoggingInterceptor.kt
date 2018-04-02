package com.wexmarket.android.network.cookie

import android.text.TextUtils
import android.util.Log

import java.io.IOException

class CookieSendLoggingInterceptor : okhttp3.Interceptor {
    //	@Override
    //	public Response intercept(Interceptor.Chain chain) throws IOException {
    //		Request request = chain.request();
    //		String cookie = request.headers().get("Cookie");
    //		Lg.d("Okhttp", request.url().toString(), "Cookie:", cookie);
    //		String cookie2 = request.headers().get("Cookie2");
    //		if (!TextUtils.isEmpty(cookie2)) {
    //			Lg.d("Okhttp", "Cookie2:", cookie2);
    //		}
    //		return chain.proceed(request);
    //	}

    @Throws(IOException::class)
    override fun intercept(chain: okhttp3.Interceptor.Chain): okhttp3.Response {
        val request = chain.request()
        val cookie = request.headers().get("Cookie")
        Log.d("Okhttp3", "Cookie:$cookie")
        val cookie2 = request.headers().get("Cookie2")
        if (!TextUtils.isEmpty(cookie2)) {
            Log.d("Okhttp3", """Cookie2:$cookie2""")
        }
        return chain.proceed(request)
    }
}