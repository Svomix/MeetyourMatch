package com.javanostra.meetyourmatch.persistance.api_service;

import android.service.autofill.UserData;

import com.javanostra.meetyourmatch.persistance.entity.Attribute;
import com.javanostra.meetyourmatch.persistance.entity.Event;
import com.javanostra.meetyourmatch.persistance.entity.Interest;
import com.javanostra.meetyourmatch.persistance.entity.ResponseDTO;
import com.javanostra.meetyourmatch.persistance.entity.Tag;
import com.javanostra.meetyourmatch.persistance.entity.User;
import com.javanostra.meetyourmatch.persistance.entity.UserAuthority;
import com.javanostra.meetyourmatch.persistance.entity.UserEvent;
import com.javanostra.meetyourmatch.persistance.entity.UserEventDTO;
import com.javanostra.meetyourmatch.persistance.entity.UserProfileDTO;

import java.util.List;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UserApiService {

    @GET("/api/v1/users/authorities")
    Call<List<UserAuthority>> getMyAuthorities();

    @GET("/api/v1/users")
    Call<List<User>> getAllUsers(
            @Query("offset") int offset,
            @Query("limit") int limit
    );

    @GET("/api/v1/users/{user_id}")
    Call<User> getUserById(@Path("user_id") Long userId);

    @GET("/api/v1/users/{user_id}/events/{event_id}")
    Call<UserEventDTO> getUserEventById(
            @Path("user_id") Long userId,
            @Path("event_id") Long eventId
    );

    @GET("/api/v1/users/events/{event_id}/liked")
    Call<Integer> getLikes(@Path("event_id") Long eventId);

    @PUT("/api/v1/users/{user_id}/events/{event_id}/liked")
    Call<Void> setLiked(@Path("user_id") Long userId, @Path("event_id") Long eventId);

    @PUT("/api/v1/users/{user_id}/events/{event_id}/disliked")
    Call<Void> setDisliked(@Path("user_id") Long userId, @Path("event_id") Long eventId);

    @PUT("/api/v1/users/{user_id}/events/{event_id}/calendar")
    Call<Void> setCalendar(@Path("user_id") Long userId, @Path("event_id") Long eventId);

    @GET("/api/v1/users/{user_id}/tags")
    Call<List<Tag>> getUserTags(
            @Path("user_id") Long userId
    );

    @GET("/api/v1/users/{user_id}/attributes")
    Call<List<Attribute>> getUserAttributes(
            @Path("user_id") Long userId,
            @Query("offset") int offset,
            @Query("limit") int limit
    );

    @POST("/api/v1/users")
    Call<Void> createUser(@Body User user);

    @POST("/api/v1/users/events")
    Call<Void> createUserEvent(@Body UserEvent event);

    @POST("/api/v1/users/{user_id}/attributes/{attr_id}")
    Call<Void> createUserAttribute(
            @Path("user_id") Long userId,
            @Path("attr_id") Long attrId,
            @Body Long value
    );

    @DELETE("/api/v1/users/{user_id}/attributes/{attr_id}/{value}")
    Call<Void> deleteUserAttribute(
            @Path("user_id") Long userId,
            @Path("attr_id") Long attrId,
            @Path("value") String value
    );

    @PUT("/api/v1/users")
    Call<ResponseDTO> updateUser(@Body UserProfileDTO user);

    @PUT("/api/v1/users/events")
    Call<Void> updateUserEvent(@Body UserEvent event);

    @DELETE("/api/v1/users/{id}")
    Call<Void> deleteUserById(@Path("id") Long id);

    @DELETE("/api/v1/users/events/{event_id}")
    Call<Void> deleteUserEventById(@Path("event_id") Long eventId);

    @DELETE("/api/v1/users/{user_id}/attributes/{attr_id}")
    Call<Void> deleteUserAttribute(
            @Path("user_id") Long userId,
            @Path("attr_id") Long attrId
    );

    @GET("/api/interests")
    Call<List<Interest>> getAllInterests();

    @POST("/api/v1/users/sendResetCode")
    Call<ResponseDTO> sendResetPasswordCode(@Query("email") String email);

    @GET("/api/v1/users/checkResetCode")
    Call<ResponseDTO> checkResetPasswordCode(@Query("code") String code, @Query("email") String email);

    @PUT("/api/v1/users/updatePassword")
    Call<ResponseDTO> updatePassword(@Query("password") String password, @Query("email") String email);
}
