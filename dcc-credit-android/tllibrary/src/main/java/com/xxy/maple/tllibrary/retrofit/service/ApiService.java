package com.xxy.maple.tllibrary.retrofit.service;

import com.xxy.maple.tllibrary.entity.BaseEntity;
import com.xxy.maple.tllibrary.entity.GaspriceNonceEntity;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by Gaoguanqi on 2018/5/30.
 */

public interface ApiService {
    @FormUrlEncoded
    @POST("open/balance/get_gasprice_nonce")
    Observable<BaseEntity<GaspriceNonceEntity>> getGaspriceNonce(@Field("type") String type, @Field("order_no") String order_no, @Field("address") String address, @Field("language") String language);

    @FormUrlEncoded
    @POST("order/order/request_chain")
    Observable<BaseEntity> requestChain(@Field("type") String type, @Field("order_no") String order_no,@Field("loan_address") String loan_address, @Field("lender_address") String lender_address, @Field("auth_sign") String auth_sign,@Field("sign") String sign);


}
