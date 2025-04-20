package com.javanostra.spring.core.vk;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javanostra.spring.core.exceptions.BaseCoreException;
import com.javanostra.spring.core.exceptions.InvalidVKAccessTokenException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
public class VkClient {

    @Value("${meetyourmatch.client-id}")
    private String clientId;

    public void checkAccessToken(String accessToken, String email) throws BaseCoreException {

        String formData = "client_id=" + URLEncoder.encode(clientId, StandardCharsets.UTF_8) +
                "&access_token=" + URLEncoder.encode(accessToken, StandardCharsets.UTF_8);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://id.vk.com/oauth2/user_info"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(formData))
                .build();

        HttpClient client = HttpClient.newHttpClient();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> data = mapper.readValue(response.body(), Map.class);
            Map<String, Object> user = (Map<String, Object>) data.get("user");

            if (user == null) {
                throw new InvalidVKAccessTokenException();
            }

            String vkEmail = (String) user.get("email");

            if (vkEmail == null || vkEmail.isEmpty() || !vkEmail.equals(email)) {
                throw new InvalidVKAccessTokenException();
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
