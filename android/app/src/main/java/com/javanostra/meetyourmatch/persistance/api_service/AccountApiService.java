package com.javanostra.meetyourmatch.persistance.api_service;

import com.javanostra.meetyourmatch.persistance.entity.ResponseDTO;
import com.javanostra.meetyourmatch.persistance.entity.UserProfileDTO;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AccountApiService {

    @GET("/api/account/getInfo")
    Call<UserProfileDTO> getAccountInfo();

    @POST("/api/account/setCity")
    Call<ResponseDTO> setCity(@Query("city") Long cityId);

    @POST("/api/account/setEmail")
    Call<ResponseDTO> setEmail(@Query("email") String email);

    @POST("/api/account/setUsername")
    Call<ResponseDTO> setUsername(@Query("username") String username);

    @POST("/api/account/setImage")
    Call<ResponseDTO> setImage(@Query("avatar_path") String avatar_path);
}
