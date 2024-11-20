package com.javanostra.meetyourmatch.persistance.api_service;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface LoginApiService {
    @FormUrlEncoded
    @POST("/api/login")
    Call<ResponseBody> login(
            @Field("username") String username,
            @Field("password") String password
    );
}