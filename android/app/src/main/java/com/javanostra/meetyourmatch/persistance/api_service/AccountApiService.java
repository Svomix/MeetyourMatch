package com.javanostra.meetyourmatch.persistance.api_service;

import com.javanostra.meetyourmatch.persistance.entity.UserRelationDTO;
import com.javanostra.meetyourmatch.persistance.entity.ResponseDTO;
import com.javanostra.meetyourmatch.persistance.entity.UserActionDTO;
import com.javanostra.meetyourmatch.persistance.entity.UserProfileDTO;
import com.javanostra.meetyourmatch.persistance.entity.UserInterest;

import java.util.List;
import java.util.Set;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface AccountApiService {

    // === CURRENT ACCOUNT ===

    // getAccountInfo() -> ResponseEntity<FullUserProfileDTO>
    @GET("/api/account/getInfo")
    Call<UserProfileDTO> getAccountInfo();

    // === INTERESTS ===

    // getMyInterests() -> ResponseEntity<Set<UserInterest>>
    @GET("/api/account/interests")
    Call<Set<UserInterest>> getMyInterests();

    // addInterest(@RequestParam("id") Integer id) -> ResponseEntity<String>
    @POST("/api/account/interests")
    Call<String> addInterest(
            @Query("id") Integer id
    );

    // removeInterest(@RequestParam("id") Integer id) -> ResponseEntity<String>
    @DELETE("/api/account/interests")
    Call<String> deleteInterest(
            @Query("id") Integer id
    );

    // === EVENT MANIPULATION ===

    // setLiked(@PathVariable("event_id") Long eventId, @RequestParam("value") Optional<Boolean> value) -> ResponseEntity<UserActionDTO>
    @POST("/api/account/events/{event_id}/like")
    Call<UserActionDTO> setLiked(
            @Path("event_id") Long eventId,
            @Query("value") Boolean value
    );

    // setDisliked(@PathVariable("event_id") Long eventId, @RequestParam("value") Optional<Boolean> value) -> ResponseEntity<UserActionDTO>
    @POST("/api/account/events/{event_id}/dislike")
    Call<UserActionDTO> setDisliked(
            @Path("event_id") Long eventId,
            @Query("value") Boolean value
    );

    // setCalendar(@PathVariable("event_id") Long eventId, @RequestParam("value") Optional<Boolean> value) -> ResponseEntity<UserActionDTO>
    @POST("/api/account/events/{event_id}/calendar")
    Call<UserActionDTO> setCalendar(
            @Path("event_id") Long eventId,
            @Query("value") Boolean value
    );

    // getEventAction(@PathVariable("event_id") Long eventId) -> ResponseEntity<UserActionDTO>
    @GET("/api/account/events/{event_id}/actions")
    Call<UserActionDTO> getEventAction(
            @Path("event_id") Long eventId
    );

    // getCalendar() -> ResponseEntity<List<UserActionDTO>>
    @GET("/api/account/events/calendar")
    Call<List<UserActionDTO>> getCalendar();

    // === USER DATA ===

    // setUsername(@RequestParam("username") String username) -> ResponseDTO
    @POST("/api/account/setName")
    Call<ResponseDTO> setUsername(
            @Query("username") String username
    );

    // setEmail(@RequestParam("email") String email) -> ResponseDTO
    @POST("/api/account/setEmail")
    Call<ResponseDTO> setEmail(
            @Query("email") String email
    );

    // setPassword(@RequestParam("password") String password) -> ResponseDTO
    @POST("/api/account/setPassword")
    Call<ResponseDTO> setPassword(
            @Query("password") String password
    );

    // setCity(@RequestParam("city") Long city_id) -> ResponseEntity<ResponseDTO>
    @POST("/api/account/setCity")
    Call<ResponseDTO> setCity(
            @Query("city") Long cityId
    );

    // setImage(@RequestParam("file") MultipartFile file) -> ResponseEntity<ResponseDTO>
    @Multipart
    @POST("/api/account/setImage")
    Call<ResponseDTO> setImage(
            @Part MultipartBody.Part file
    );

    // === RELATIONS ===

    // getUserRelation(@RequestParam("user_id") Long userId) -> RelationUserDTO
    @GET("/api/account/getRelation")
    Call<UserRelationDTO> getUserRelation(
            @Query("user_id") Long userId
    );

    // getFriends(...) -> Page<UserProfileDTO>
    @GET("/api/account/friends")
    Call<PagedResponse<UserProfileDTO>> getFriends(
            @Query("page") Integer page,
            @Query("limit") Integer limit,
            @Query("name_pattern") String namePattern
    );

    // deleteFriend(@RequestParam("friend_id") Long friendId) -> ResponseDTO
    @DELETE("/api/account/friends")
    Call<ResponseDTO> deleteFriend(
            @Query("friend_id") Long friendId
    );

    // getBlocked(...) -> Page<UserProfileDTO>
    @GET("/api/account/blocked")
    Call<PagedResponse<UserProfileDTO>> getBlocked(
            @Query("page") Integer page,
            @Query("limit") Integer limit,
            @Query("name_pattern") String namePattern
    );

    // addBlocked(@RequestParam("blocked_id") Long blockedId) -> ResponseDTO
    @POST("/api/account/blocked")
    Call<ResponseDTO> addBlocked(
            @Query("blocked_id") Long blockedId
    );

    // deleteBlocked(@RequestParam("blocked_id") Long blockedId) -> ResponseDTO
    @DELETE("/api/account/blocked")
    Call<ResponseDTO> deleteBlocked(
            @Query("blocked_id") Long blockedId
    );

    // === FRIEND REQUESTS ===

    // getOutgoingFriendRequests(...) -> Page<UserProfileDTO>
    @GET("/api/account/requests/outgoing")
    Call<PagedResponse<UserProfileDTO>> getIngoingFriendRequests( // SUKA CHTO ZA HUETA??? POCHEMY INGOING ETO ISHODASCHIE OT POLZOVATELA ?!?!?!?!?!
            @Query("page") Integer page,
            @Query("limit") Integer limit,
            @Query("name_pattern") String namePattern
    );

    // getIngoingFriendRequests(...) -> Page<UserProfileDTO>
    @GET("/api/account/requests/ingoing")
    Call<PagedResponse<UserProfileDTO>> getOutgoingFriendRequests(
            @Query("page") Integer page,
            @Query("limit") Integer limit,
            @Query("name_pattern") String namePattern
    );

    // sendFriendRequest(@RequestParam("user_id") Long userId) -> ResponseDTO
    @POST("/api/account/requests")
    Call<ResponseDTO> sendFriendRequest(
            @Query("user_id") Long userId
    );

    // acceptFriendRequest(@RequestParam("user_id") Long userId) -> ResponseDTO
    @PUT("/api/account/requests")
    Call<ResponseDTO> acceptFriendRequest(
            @Query("user_id") Long userId
    );

    // deleteFriendRequest(@RequestParam("user_id") Long userId) -> ResponseDTO
    @DELETE("/api/account/requests")
    Call<ResponseDTO> deleteFriendRequest(
            @Query("user_id") Long userId
    );
}