package com.javanostra.meetyourmatch.persistance.api_service;

import com.javanostra.meetyourmatch.persistance.entity.Interest;
import com.javanostra.meetyourmatch.persistance.entity.ResponseDTO;
import com.javanostra.meetyourmatch.persistance.entity.User;
import com.javanostra.meetyourmatch.persistance.entity.UserAuthority;
import com.javanostra.meetyourmatch.persistance.entity.UserInterest;
import com.javanostra.meetyourmatch.persistance.entity.UserProfileDTO;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UserApiService {

    // getMyAuthorities() -> List<UserAuthority>
    @GET("/api/v1/users/authorities")
    Call<List<UserAuthority>> getMyAuthorities();

    // findAllUsers(...) -> Page<UserProfileDTO>
    @GET("/api/v1/users")
    Call<PagedResponse<UserProfileDTO>> getAllUsers(
            @Query("offset") int offset,
            @Query("limit") int limit,
            @Query("name_pattern") String namePattern
    );

    // findUserById(@PathVariable("user_id") Long userId) -> ResponseEntity<UserProfileDTO>
    @GET("/api/v1/users/{user_id}")
    Call<UserProfileDTO> getUserById(
            @Path("user_id") Long userId
    );

    // checkUserIfExistsByEmail(@PathVariable("email") String email) -> Boolean
    @GET("/api/v1/users/exists/{email}")
    Call<Boolean> checkUserIfExistsByEmail(@Path("email") String email);

    // saveUser(@RequestBody User user) -> void
    @POST("/api/v1/users")
    Call<Void> createUser(@Body User user);

//    // saveUserEvent(@RequestBody UserActions event) -> void
//    @POST("/api/v1/users/events")
//    Call<Void> createUserEvent(@Body UserAc event);

    // updateUser(@RequestBody User user) -> void
    @PUT("/api/v1/users")
    Call<Void> updateUser(@Body User user);

//    // updateUserEvent(@RequestBody UserActions event) -> void
//    @PUT("/api/v1/users/events")
//    Call<Void> updateUserEvent(@Body UserActions event);

    // deleteUserById(@PathVariable Long id) -> void
    @DELETE("/api/v1/users/{id}")
    Call<Void> deleteUserById(@Path("id") Long id);

//    // deleteUserEventById(@PathVariable("event_id") Long eventId) -> void
//    @DELETE("/api/v1/users/events/{event_id}")
//    Call<Void> deleteUserEventById(@Path("event_id") Long eventId);

    // sendResetPasswordCode(@RequestParam("email") String email) -> ResponseDTO
    @POST("/api/v1/users/sendResetCode")
    Call<ResponseDTO> sendResetPasswordCode(@Query("email") String email);

    // updatePassword(@RequestParam("password") String password, @RequestParam("code") String code, @RequestParam("email") String email) -> ResponseDTO
    @PUT("/api/v1/users/updatePassword")
    Call<ResponseDTO> checkResetPasswordCode(@Query("password") String password, @Query("code") String code, @Query("email") String email);

    // updateResetCode(@RequestParam("email") String email) -> ResponseDTO
    @PUT("/api/v1/users/updateCode")
    Call<ResponseDTO> updateResetCode(@Query("email") String email);

    // findConnectedUsers() -> ResponseEntity<List<User>>
    @GET("/api/v1/users/users")
    Call<List<User>> findConnectedUsers();

    // getUserEventById
    // getLikes
    // setLiked
    // setDisliked
    // setCalendar
    // getUserTags
    // getUserAttributes
    // createUserAttribute
    // deleteUserAttribute
    // getAllInterests
    // checkResetPasswordCode

    @GET("/api/interests")
    Call<List<Interest>> getAllInterests();

    @GET("/api/v1/users/{user_id}/online_status")
    Call<Timestamp> getLastSeenForUser(@Path("user_id") Long userId);
}