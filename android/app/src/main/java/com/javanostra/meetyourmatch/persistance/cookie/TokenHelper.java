package com.javanostra.meetyourmatch.persistance.cookie;

import android.util.Base64;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

public class TokenHelper {

    public static String extractUsernameFromToken(String token) {
        if (token == null || !token.contains(".")) {
            return null;
        }

        String[] parts = token.split("\\.");
        if (parts.length < 2) {
            return null;
        }

        String payloadBase64 = parts[1];

        try {
            byte[] decodedBytes = Base64.decode(payloadBase64, Base64.URL_SAFE);
            String decodedPayload = new String(decodedBytes, "UTF-8");

            Gson gson = new Gson();
            JsonObject payloadJson = gson.fromJson(decodedPayload, JsonObject.class);

            if (payloadJson.has("preferred_username") && !payloadJson.get("preferred_username").isJsonNull()) {
                return payloadJson.get("preferred_username").getAsString();
            } else if (payloadJson.has("sub") && !payloadJson.get("sub").isJsonNull()) {
                return payloadJson.get("sub").getAsString();
            } else if (payloadJson.has("email") && !payloadJson.get("email").isJsonNull()) {
                return payloadJson.get("email").getAsString();
            }

            return null;

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return null;
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}