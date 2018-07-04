package com.wexmarket.android.network

import android.app.Application
import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.wexmarket.android.network.cookie.CookieSendLoggingInterceptor
import com.wexmarket.android.network.cookie.CookieStoreFactory
import io.reactivex.schedulers.Schedulers
import okhttp3.CookieJar
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.File
import java.net.CookieManager
import java.net.CookiePolicy
import java.net.CookieStore
import java.net.HttpCookie
import java.util.concurrent.TimeUnit

/**
 * Created by lulingzhi on 2017/9/28.
 */

/**
 * just provides network api impl
 * by okhttp-retrofit-rx
 */
class Networking {
    private val cookieStore: CookieStore
    val okHttpClient: OkHttpClient
    private val retrofit: Retrofit
    val networkGson:Gson

    constructor(context: Application, debug: Boolean = BuildConfig.DEBUG) {
        networkGson = createGson()
        cookieStore = CookieStoreFactory.create(context,{
            networkGson.fromJson(it,HttpCookie::class.java)
        },{
            networkGson.toJson(it)
        })
        val cookieJar = JavaNetCookieJar(CookieManager(cookieStore, DEFAULT_COOKIE_POLICY))
        okHttpClient = createOkHttp(cookieJar, cacheDir(context), debug)
        retrofit = createRetrofit(okHttpClient, networkGson, "https://www.wexmarket.com")
    }

    fun <T> createApi(api: Class<T>,baseUrl: String): T {
        return retrofit.newBuilder().baseUrl(baseUrl).build().create(api)
    }

    companion object {
        private const val CACHE_SIZE: Long = 20 * 1024 * 1024
        private const val DEFAULT_OKHTTP_CACHE_DIR_NAME = "ok_cache_wn"
        private const val READ_TIMEOUT: Long = 60
        private const val CONNECT_TIMEOUT: Long = 30
        private const val LOG_TAG_OKHTTP = "OkHttp"
        private val DEFAULT_COOKIE_POLICY = CookiePolicy.ACCEPT_ORIGINAL_SERVER
        fun createOkHttp(cookieJar: CookieJar, cacheDir: File?, debug: Boolean): OkHttpClient {

            return OkHttpClient.Builder()
                    .apply { if (cacheDir != null) cache(okhttp3.Cache(cacheDir, CACHE_SIZE)) }
                    .cookieJar(cookieJar)
                    .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                    .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                    .apply {
                        if (debug) {
                            addInterceptor(HttpLoggingInterceptor {
                                Log.d(LOG_TAG_OKHTTP, it)
                            }.setLevel(HttpLoggingInterceptor.Level.BODY))
                            addNetworkInterceptor(CookieSendLoggingInterceptor())
                        }
                    }
                    .build()
        }

        private fun cacheDir(context: Context): File? = File(context.externalCacheDir, DEFAULT_OKHTTP_CACHE_DIR_NAME)

        fun createRetrofit(okHttpClient: OkHttpClient, gson: Gson, baseUrl: String): Retrofit {
            return Retrofit.Builder()
                    .client(okHttpClient)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
//                    .validateEagerly(true)
                    .baseUrl(baseUrl)
                    .build()
        }

        private fun createGson(): Gson {
            return GsonBuilder()
//                    .registerTypeAdapterFactory(EnumAdapterFactory())
                    .create()
        }
    }
}
