package com.javanostra.meetyourmatch.persistance.api_service;

import com.javanostra.meetyourmatch.persistance.entity.Interest;
import com.javanostra.meetyourmatch.persistance.entity.ResponseDTO;
import com.javanostra.meetyourmatch.persistance.entity.UserActionDTO;
import com.javanostra.meetyourmatch.persistance.entity.UserProfileDTO;

import java.util.List;
import java.util.Optional;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface AccountApiService {

    @GET("/api/account/getInfo")
    Call<UserProfileDTO> getAccountInfo();

    @GET("/api/account/events/{event_id}/actions")
    Call<UserActionDTO> getEventAction(
            @Path("event_id") Long eventId
    );

    @POST("/api/account/events/{event_id}/calendar")
    Call<UserActionDTO> setCalendar(
            @Path("event_id") Long eventId,
            @Query("value") Boolean value
    );

    @GET("/api/account/events/calendar")
    Call<List<UserActionDTO>> getCalendar(
    );

    @POST("/api/account/events/{event_id}/like")
    Call<UserActionDTO> setLiked(
            @Path("event_id") Long eventId,
            @Query("value") Boolean value
    );

    @POST("/api/account/events/{event_id}/dislike")
    Call<UserActionDTO> setDisliked(
            @Path("event_id") Long eventId,
            @Query("value") Boolean value
    );

    @POST("/api/account/setCity")
    Call<ResponseDTO> setCity(@Query("city") Long cityId);

    @POST("/api/account/setEmail")
    Call<ResponseDTO> setEmail(@Query("email") String email);

    @POST("/api/account/setUsername")
    Call<ResponseDTO> setUsername(@Query("username") String username);

    @POST("/api/account/setImage")
    Call<ResponseDTO> setImage(@Query("avatar_path") String avatar_path);

    @POST("/api/account/interests")
    Call<String> addInterest(@Query("id") Integer interestId);

    @DELETE("/api/account/interests")
    Call<String> deleteInterest(@Query("id") Integer interestId);

    @GET("/api/account/interests")
    Call<List<Interest>> getMyInterests();
}
