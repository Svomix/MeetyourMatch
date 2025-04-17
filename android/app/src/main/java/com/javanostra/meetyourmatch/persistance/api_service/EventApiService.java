package com.javanostra.meetyourmatch.persistance.api_service;

import com.javanostra.meetyourmatch.persistance.entity.Attribute;
import com.javanostra.meetyourmatch.persistance.entity.CommentDTO;
import com.javanostra.meetyourmatch.persistance.entity.CommentRequestDTO;
import com.javanostra.meetyourmatch.persistance.entity.Event;
import com.javanostra.meetyourmatch.persistance.entity.FullEventDTO;
import com.javanostra.meetyourmatch.persistance.entity.Tag;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface EventApiService {

    @GET("/api/v1/events")
    Call<PagedResponse<Event>> findAllEvents(
            @Query("offset") Integer offset,
            @Query("limit") Integer limit
    );

    @GET("/api/v1/events/pageout")
    Call<List<Event>> findAllEventsPageout(
            @Query("offset") Integer offset,
            @Query("limit") Integer limit
    );

    @GET("/api/v1/events/{event_id}")
    Call<FullEventDTO> getFullEventDTO(
            @Path("event_id") Long eventId
    );

    @POST("/api/v1/events/{event_id}/comments")
    Call<CommentDTO> addComment(
            @Path("event_id") Long eventId,
            @Body CommentRequestDTO body
    );

    @GET("/api/v1/events/{event_id}/tags")
    Call<List<Tag>> findEventTags(
            @Path("event_id") Long eventId
    );

    @GET("/api/v1/events/{event_id}")
    Call<Event> findEventById(@Path("event_id") Long eventId);

    @GET("/api/v1/events/{event_id}/attributes")
    Call<List<Attribute>> findEventAttributesByEventId(@Path("event_id") Long eventId, @Query("offset") Integer offset, @Query("limit") Integer limit);

    @POST("/api/v1/events/{event_id}/attributes/{attr_id}")
    Call<Void> createEventAttributeByEventId(@Path("event_id") Long eventId, @Path("attr_id") Long attrId, @Body String value);

    @DELETE("/api/v1/events/{event_id}/attributes/{attr_id}")
    Call<Void> deleteEventAttributeByAttrId(@Path("event_id") Long eventId, @Path("attr_id") Long attrId);

    @POST("/api/v1/events")
    Call<Void> saveEvent(@Body Event event);

    @PUT("/api/v1/events")
    Call<Void> updateEvent(@Body Event event);

    @DELETE("/api/v1/events/{event_id}")
    Call<Void> deleteEvent(@Path("event_id") Long eventId);
}
