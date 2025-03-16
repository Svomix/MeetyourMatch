package com.javanostra.meetyourmatch.persistance.api_service;

import com.javanostra.meetyourmatch.persistance.entity.ResponseDTO;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface RegistrationApiService {
    @FormUrlEncoded
    @POST("/api/register")
    Call<ResponseDTO> register(
            @Field("username") String username,
            @Field("password") String password,
            @Field("email") String email
    );

    @FormUrlEncoded
    @POST("/api/register/verify")
    Call<ResponseDTO> verifyRegister(
            @Field("token")
            String code,

            @Field("email")
            String email
    );

    @FormUrlEncoded
    @PUT("/api/register/update-code")
    Call<ResponseDTO> updateCode(
            @Field("email")
            String email
    );
}
