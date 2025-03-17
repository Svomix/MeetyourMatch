package com.javanostra.meetyourmatch.persistance.cookie;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class AddCookiesInterceptor implements Interceptor {
    private CookieManager cookieManager;

    public AddCookiesInterceptor(CookieManager cookieManager) {
        this.cookieManager = cookieManager;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();

        String token = cookieManager.getCookie();

        if (token != null && !token.isEmpty()) {
            builder.addHeader("Authorization", "Bearer " + token.split("=")[1].split(";")[0]);
        }

        return chain.proceed(builder.build());
    }
}

