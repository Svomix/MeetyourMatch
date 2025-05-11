package com.javanostra.meetyourmatch.persistance.api_service;

import androidx.annotation.Nullable;

import com.javanostra.meetyourmatch.persistance.entity.Attribute;
import com.javanostra.meetyourmatch.persistance.entity.CommentDTO;
import com.javanostra.meetyourmatch.persistance.entity.CommentRequestDTO;
import com.javanostra.meetyourmatch.persistance.entity.Event;
import com.javanostra.meetyourmatch.persistance.entity.EventUploadDTO;
import com.javanostra.meetyourmatch.persistance.entity.FileUploadedDTO;
import com.javanostra.meetyourmatch.persistance.entity.FullEventDTO;
import com.javanostra.meetyourmatch.persistance.entity.Tag;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface EventApiService {

    // GET /recAndroid -> EventDTO
    @GET("/api/v1/events/recAndroid")
    Call<Event> findRec();

    // GET / -> Page<EventDTO>
    @GET("/api/v1/events")
    Call<PagedResponse<Event>> findAllEvents(
            @Query("page") Integer page,
            @Query("limit") Integer limit,
            @Query("s") @Nullable String search
    );

    // GET /pageout -> List<EventDTO>
    @GET("/api/v1/events/pageout")
    Call<List<Event>> findAllEventsPageout(
            @Query("offset") Integer offset,
            @Query("limit") Integer limit
    );

    // GET /{event_id} -> FullEventDTO
    @GET("/api/v1/events/{event_id}")
    Call<FullEventDTO> findEventById(
            @Path("event_id") Long eventId
    );

    // POST /{event_id}/comments -> CommentDTO
    @POST("/api/v1/events/{event_id}/comments")
    Call<CommentDTO> addComment(
            @Path("event_id") Long eventId,
            @Body CommentRequestDTO body
    );

    // DELETE /{event_id}/comments -> EventComment (или Void)
    @DELETE("/api/v1/events/{event_id}/comments")
    Call<Void> removeComment(
            @Path("event_id") Long eventId,
            @Query("id") Integer commentId
    );

    // POST /uploadEventImage -> FileUploadedDTO
    @Multipart
    @POST("/api/v1/events/uploadEventImage")
    Call<FileUploadedDTO> uploadEventImage(
            @Part MultipartBody.Part file
    );

    // POST /uploadEvent -> EventDTO
    @POST("/api/v1/events/uploadEvent")
    Call<Event> uploadEvent(
            @Body EventUploadDTO eventDto
    );

    @GET("/api/v1/events/{event_id}/tags")
    Call<List<Tag>> findEventTags(
            @Path("event_id") Long eventId
    );

    @POST("/api/v1/events")
    Call<Void> saveEvent(@Body Event event);

    @PUT("/api/v1/events")
    Call<Void> updateEvent(@Body Event event);

    @DELETE("/api/v1/events/{event_id}")
    Call<Void> deleteEvent(@Path("event_id") Long eventId);
}
