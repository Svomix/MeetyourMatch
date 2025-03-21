package com.javanostra.meetyourmatch.persistance.api_service;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ImageApiService {

        @Multipart
        @POST("/api/v1/images/upload")
        Call<ResponseBody> uploadImage(@Part MultipartBody.Part image);

        @GET("/api/v1/images/download/{filename}")
        Call<ResponseBody> downloadImage(@Path("filename") String filename);
}
