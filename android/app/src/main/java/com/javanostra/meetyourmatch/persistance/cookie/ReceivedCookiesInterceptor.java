package com.javanostra.meetyourmatch.persistance.cookie;

import android.util.Log;

import okhttp3.Interceptor;
import okhttp3.Response;

import java.io.IOException;

public class ReceivedCookiesInterceptor implements Interceptor {
    private CookieManager cookieManager;

    public ReceivedCookiesInterceptor(CookieManager cookieManager) {
        this.cookieManager = cookieManager;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());

        if (response.headers("Set-Cookie").size() > 0) {
            String cookie = response.header("Set-Cookie");
            if (cookie != null) {
                String token = extractToken(cookie);
                cookieManager.saveCookie(token);
                Log.d("ReceivedInterceptor", "Set-Cookie: " + response.header("Set-Cookie"));
                Log.d("ReceivedInterceptor", "Token: " + token);
            }
        }
        return response;
    }

    private String extractToken(String cookie) {
        if (cookie.startsWith("accessToken=")) {
            return cookie.substring("accessToken=".length(), cookie.indexOf(";"));
        }
        return cookie;
    }
}

