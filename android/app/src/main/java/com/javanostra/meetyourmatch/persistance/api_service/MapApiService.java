package com.javanostra.meetyourmatch.persistance.api_service;

import com.javanostra.meetyourmatch.persistance.entity.MapObjectDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface MapApiService {

    @GET("/api/maps/locations")
    Call<List<MapObjectDTO>> getAllMapObjects();

}