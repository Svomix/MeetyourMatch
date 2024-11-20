package com.javanostra.meetyourmatch.persistance.api_service;

import com.javanostra.meetyourmatch.persistance.entity.City;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface CityApiService {
    @GET("/api/city")
    Call<List<City>> getCities();
}
