package com.javanostra.meetyourmatch.persistance.api_service;

import com.javanostra.meetyourmatch.persistance.entity.UserProfileDTO;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AccountApiService {

    @GET("/api/account/getInfo")
    Call<UserProfileDTO> getAccountInfo();

    @POST("/api/account/setCity")
    Call<String> setCity(@Query("city") Long cityId);
}
