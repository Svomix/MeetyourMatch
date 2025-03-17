package com.javanostra.meetyourmatch.persistance.api_service;

import com.javanostra.meetyourmatch.persistance.entity.Attribute;
import com.javanostra.meetyourmatch.persistance.entity.Interest;
import com.javanostra.meetyourmatch.persistance.entity.User;
import com.javanostra.meetyourmatch.persistance.entity.UserAuthority;
import com.javanostra.meetyourmatch.persistance.entity.UserEvent;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UserApiService {

    // Получить все полномочия текущего пользователя
    @GET("/api/v1/users/authorities")
    Call<List<UserAuthority>> getMyAuthorities();

    // Получить всех пользователей с пагинацией
    @GET("/api/v1/users")
    Call<List<User>> getAllUsers(
            @Query("offset") int offset,
            @Query("limit") int limit
    );

    // Получить пользователя по ID
    @GET("/api/v1/users/{user_id}")
    Call<User> getUserById(@Path("user_id") Long userId);

    // Получить события пользователя с пагинацией
    @GET("/api/v1/users/{user_id}/events")
    Call<List<UserEvent>> getUserEvents(
            @Path("user_id") Long userId,
            @Query("offset") int offset,
            @Query("limit") int limit
    );

    // Получить событие пользователя по ID
    @GET("/api/v1/users/{user_id}/events/{event_id}")
    Call<UserEvent> getUserEventById(
            @Path("user_id") Long userId,
            @Path("event_id") Long eventId
    );

    // Получить атрибуты пользователя с пагинацией
    @GET("/api/v1/users/{user_id}/attributes")
    Call<List<Attribute>> getUserAttributes(
            @Path("user_id") Long userId,
            @Query("offset") int offset,
            @Query("limit") int limit
    );

    // Создать нового пользователя
    @POST("/api/v1/users")
    Call<Void> createUser(@Body User user);

    // Создать новое событие пользователя
    @POST("/api/v1/users/events")
    Call<Void> createUserEvent(@Body UserEvent event);

    // Добавить атрибут к пользователю
    @POST("/api/v1/users/{user_id}/attributes/{attr_id}")
    Call<Void> createUserAttribute(
            @Path("user_id") Long userId,
            @Path("attr_id") Long attrId,
            @Body String value
    );

    // Обновить пользователя
    @PUT("/api/v1/users")
    Call<Void> updateUser(@Body User user);

    // Обновить событие пользователя
    @PUT("/api/v1/users/events")
    Call<Void> updateUserEvent(@Body UserEvent event);

    // Удалить пользователя по ID
    @DELETE("/api/v1/users/{id}")
    Call<Void> deleteUserById(@Path("id") Long id);

    // Удалить событие пользователя по ID
    @DELETE("/api/v1/users/events/{event_id}")
    Call<Void> deleteUserEventById(@Path("event_id") Long eventId);

    // Удалить атрибут у пользователя
    @DELETE("/api/v1/users/{user_id}/attributes/{attr_id}")
    Call<Void> deleteUserAttribute(
            @Path("user_id") Long userId,
            @Path("attr_id") Long attrId
    );

    @GET("/api/interests")
    Call<List<Interest>> getAllInterests();
}
