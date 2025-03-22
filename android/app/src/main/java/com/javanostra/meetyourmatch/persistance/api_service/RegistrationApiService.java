package com.javanostra.meetyourmatch.persistance.api_service;

import com.javanostra.meetyourmatch.persistance.entity.ResponseDTO;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

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
            @Field("token") String token,
            @Field("email") String email
    );

    @FormUrlEncoded
    @PUT("/api/register/update-code")
    Call<ResponseDTO> updateVerificationCode(
            @Field("email") String email
    );

    @GET("/api/register/check-user")
    Call<ResponseDTO> checkUserExists(
            @Query("username") String username,
            @Query("email") String email
    );
}
