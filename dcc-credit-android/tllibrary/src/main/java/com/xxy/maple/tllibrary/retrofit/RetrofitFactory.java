package com.xxy.maple.tllibrary.retrofit;

import com.xxy.maple.tllibrary.BuildConfig;
import com.xxy.maple.tllibrary.activity.TlBrowserActivity;
import com.xxy.maple.tllibrary.retrofit.service.ApiService;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitFactory {

    // Retrofit是基于OkHttpClient的，可以创建一个OkHttpClient进行一些配置
    private static OkHttpClient httpClient = new OkHttpClient.Builder()
            .dns(new TlDns())
            // 添加通用的Header
            .addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request.Builder builder = chain.request().newBuilder();
                    // 替换为自己的token
                    //builder.addheader("Authorization",token );
                    String token = TlBrowserActivity.address;
                    builder.addHeader("Authorization",token);
                    return chain.proceed(builder.build());
                }
            })
            .build();

    private static ApiService apiService = new Retrofit.Builder()
            .baseUrl(BuildConfig.TL_SERVER_URL)
            // 添加Gson转换器
            .addConverterFactory(GsonConverterFactory.create())
            // 添加Retrofit到RxJava的转换器
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(httpClient)
            .build()
            .create(ApiService.class);

    public static ApiService getInstance() {
        return apiService;
    }


}
