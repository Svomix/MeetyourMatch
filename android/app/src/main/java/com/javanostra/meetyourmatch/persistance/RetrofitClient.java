package com.javanostra.meetyourmatch.persistance;

import android.content.Context;

import com.javanostra.meetyourmatch.persistance.cookie.AddCookiesInterceptor;
import com.javanostra.meetyourmatch.persistance.cookie.CookieManager;
import com.javanostra.meetyourmatch.persistance.cookie.ReceivedCookiesInterceptor;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = "http://10.0.2.2:8080";
    private static Retrofit retrofit;

    public static Retrofit getRetrofit(Context context) {
        if (retrofit == null) {
            CookieManager cookieManager = new CookieManager(context);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new ReceivedCookiesInterceptor(cookieManager))
                    .addInterceptor(new AddCookiesInterceptor(cookieManager))
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}