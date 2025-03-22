package com.javanostra.meetyourmatch.persistance.entity;

import javax.net.ssl.HttpsURLConnection;

public class ResponseDTO {
    int code = HttpsURLConnection.HTTP_OK;
    String message;

    public String getMessage() {
        return message;
    }

    public int getCode() {
        return code;
    }
}
