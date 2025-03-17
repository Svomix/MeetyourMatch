package com.javanostra.meetyourmatch.persistance.api_service;

import com.javanostra.meetyourmatch.persistance.entity.Interest;
import com.javanostra.meetyourmatch.persistance.entity.ResponseDTO;
import com.javanostra.meetyourmatch.persistance.entity.UserProfileDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AccountApiService {

    @GET("/api/account/getInfo")
    Call<UserProfileDTO> getAccountInfo();

    @GET("/api/account/interests")
    Call<List<Interest>> getMyInterests();

    @POST("/api/account/setCity")
    Call<ResponseDTO> setCity(@Query("city") Long cityId);
}
