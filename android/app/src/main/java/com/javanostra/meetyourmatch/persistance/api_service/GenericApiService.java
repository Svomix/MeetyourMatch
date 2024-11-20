package com.javanostra.meetyourmatch.persistance.api_service;

import retrofit2.Call;
import retrofit2.http.*;

public interface GenericApiService<T> {

    @POST("{entity}")
    Call<Void> create(@Path("entity") String entity, @Body T data);

    @PUT("{entity}")
    Call<Void> update(@Path("entity") String entity, @Body T data);

    @DELETE("{entity}/{id}")
    Call<Void> delete(@Path("entity") String entity, @Path("id") int id);

    @GET("{entity}/{id}")
    Call<T> getById(@Path("entity") String entity, @Path("id") int id);

    @GET("{entity}")
    Call<T> getAll(@Path("id") int eventId);
}