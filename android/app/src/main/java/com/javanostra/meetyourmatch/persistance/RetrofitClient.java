package com.javanostra.meetyourmatch.persistance;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.javanostra.meetyourmatch.persistance.cookie.AddCookiesInterceptor;
import com.javanostra.meetyourmatch.persistance.cookie.CookieManager;
import com.javanostra.meetyourmatch.persistance.cookie.ReceivedCookiesInterceptor;

import java.sql.Timestamp;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = "http://46.0.192.151:8081";
    private static final String BASE_URL_EMULATOR = "http://10.0.2.2:8080";
    private static final String BASE_URL_PHYSIC = "http://192.168.1.65:8080";

    private static Retrofit retrofit = null;
    private static Gson customGson = null;

    private static synchronized Gson getCustomGson() {
        if (customGson == null) {
            Log.d("RetrofitClient", "Creating custom Gson instance...");
            customGson = new GsonBuilder()
                    .registerTypeAdapter(Timestamp.class, new GsonTypeAdapters.TimestampTypeAdapter())
                    .registerTypeAdapter(Date.class, new GsonTypeAdapters.DateTypeAdapter())
                    // .serializeNulls()
                    .create();
        }
        return customGson;
    }


    public static Retrofit getRetrofit(Context context) {
        if (retrofit == null) {
            synchronized (RetrofitClient.class) {
                if (retrofit == null) {
                    Log.d("RetrofitClient", "Creating new Retrofit instance...");
                    Context appContext = context.getApplicationContext();
                    CookieManager cookieManager = new CookieManager(appContext);

                    HttpLoggingInterceptor logging = new HttpLoggingInterceptor(message -> Log.d("OkHttp", message));
                    logging.setLevel(HttpLoggingInterceptor.Level.BODY);

                    OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder()
                            .addInterceptor(new ReceivedCookiesInterceptor(cookieManager))
                            .addInterceptor(new AddCookiesInterceptor(cookieManager))
                            .addInterceptor(logging)
                            .connectTimeout(20, TimeUnit.SECONDS)
                            .readTimeout(30, TimeUnit.SECONDS)
                            .writeTimeout(30, TimeUnit.SECONDS);

                    retrofit = new Retrofit.Builder()
                            .baseUrl(BASE_URL_EMULATOR)
                            .client(httpClientBuilder.build())
                            .addConverterFactory(GsonConverterFactory.create(getCustomGson()))
                            .build();

                    Log.d("RetrofitClient", "Retrofit instance created and configured.");
                }
            }
        } else {
            Log.v("RetrofitClient", "Returning existing Retrofit instance.");
        }
        return retrofit;
    }

    public static synchronized void resetInstance() {
        retrofit = null;
        customGson = null;
        Log.d("RetrofitClient", "Retrofit instance and custom Gson have been reset.");
    }
}