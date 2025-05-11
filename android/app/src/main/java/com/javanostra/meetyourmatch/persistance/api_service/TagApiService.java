package com.javanostra.meetyourmatch.persistance.api_service;

import com.javanostra.meetyourmatch.persistance.entity.Tag; // Ваш клиентский класс Tag

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface TagApiService {

    @GET("/api/v1/tags")
    Call<List<Tag>> getAllTags();

    @GET("/api/v1/tags/{id}")
    Call<Tag> getTagById(@Path("id") long tagId);

    @POST("/api/v1/tags")
    Call<Void> createTag(@Body Tag tag);

    @PUT("/api/v1/tags")
    Call<Void> updateTag(@Body Tag tag);

    @DELETE("/api/v1/tags/{id}")
    Call<Void> deleteTag(@Path("id") long tagId);

}