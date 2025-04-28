package com.javanostra.meetyourmatch.persistance.api_service;

import com.javanostra.meetyourmatch.persistance.entity.ChatMessage;
import com.javanostra.meetyourmatch.persistance.entity.UserProfileDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ChatApiService {

    // findChatMessages(@PathVariable("recipientId") String recipientId) -> ResponseEntity<List<ChatMessage>>
    @GET("/api/chats/messages/{recipientId}")
    Call<List<ChatMessage>> getChatMessages(
            @Path("recipientId") String recipientId
    );

    // findChatMessages() -> ResponseEntity<List<UserProfileDTO>>
    @GET("/api/chats/chatRooms")
    Call<List<UserProfileDTO>> getUserChats();

}