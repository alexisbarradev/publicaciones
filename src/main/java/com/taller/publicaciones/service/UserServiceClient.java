package com.taller.publicaciones.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class UserServiceClient {

    private final RestTemplate restTemplate;
    private final String apiPath;
    private final String baseUrl;

    public UserServiceClient(
            @Value("${user.service.base-url}") String baseUrl,
            @Value("${user.service.api-path}") String apiPath) {
        this.baseUrl = baseUrl;
        this.apiPath = apiPath;
        this.restTemplate = new RestTemplate();
    }

    public Map<String, Object> getUserById(Long userId) {
        String url = baseUrl + apiPath + "/" + userId;
        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        return response.getBody();
    }

    public boolean userExists(Long userId) {
        try {
            String url = baseUrl + apiPath + "/" + userId;
            restTemplate.getForEntity(url, Map.class);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
} 