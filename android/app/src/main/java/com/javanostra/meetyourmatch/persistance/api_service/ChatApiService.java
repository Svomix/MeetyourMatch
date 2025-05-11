package com.javanostra.meetyourmatch.persistance.api_service;

import androidx.annotation.Nullable;

import com.javanostra.meetyourmatch.persistance.entity.ChatInfoDTO;
import com.javanostra.meetyourmatch.persistance.entity.ChatMessage;
import com.javanostra.meetyourmatch.persistance.entity.GroupChatMessage;
import com.javanostra.meetyourmatch.persistance.entity.UserProfileDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ChatApiService {

    // findChatHistoryMessages(@PathVariable("recipientId") String recipientId) -> ResponseEntity<List<ChatMessage>>
    @GET("/api/chats/messagesHistory/{recipientId}")
    Call<List<ChatMessage>> getChatMessages(
            @Path("recipientId") String recipientId
    );

    // findChatRooms() -> ResponseEntity<List<ChatInfoDTO>>
    @GET("/api/chats/chatRooms")
    Call<List<ChatInfoDTO>> getUserChats();

    // createGroupChat(@RequestParam List<Long> memberIds, @RequestParam String groupName, @RequestParam(required = false) String avatar) -> ResponseEntity<Long>
    @POST("api/chats/groupChatCreate")
    Call<Long> createGroupChat(
            @Query("memberIds") List<Long> memberIds,
            @Query("groupName") String groupName,
            @Query("avatar") @Nullable String avatar
    );

    // findGroupChatHistory(@RequestParam Long groupChatId) -> ResponseEntity<List<GroupChatMessage>>
    @GET("api/chats/groupChatHistory")
    Call<List<GroupChatMessage>> getGroupChatHistory(
            @Query("groupChatId") Long groupChatId
    );
}