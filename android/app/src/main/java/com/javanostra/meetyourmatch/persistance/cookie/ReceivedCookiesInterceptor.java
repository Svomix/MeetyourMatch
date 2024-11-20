package com.javanostra.meetyourmatch.persistance.cookie;

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
                cookieManager.saveCookie(cookie);
            }
        }
        return response;
    }
}

