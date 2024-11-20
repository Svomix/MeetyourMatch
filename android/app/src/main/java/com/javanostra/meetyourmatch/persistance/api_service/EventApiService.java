package com.javanostra.meetyourmatch.persistance.api_service;

import com.javanostra.meetyourmatch.persistance.entity.Attribute;
import com.javanostra.meetyourmatch.persistance.entity.Event;

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
    Call<List<Event>> findAllEvents(@Query("offset") Integer offset, @Query("limit") Integer limit);

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
