package com.backend.trading.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
@Slf4j
public class UserServiceClient {

    private final WebClient webClient;
    private final String apiPath;

    public UserServiceClient(
            @Value("${user.service.base-url}") String baseUrl,
            @Value("${user.service.api-path}") String apiPath) {
        this.apiPath = apiPath;
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
        log.info("UserServiceClient initialized with base URL: {} and API path: {}", baseUrl, apiPath);
    }

    @SuppressWarnings("unchecked")
    public Mono<Map<String, Object>> getUserById(Long userId) {
        String uri = apiPath + "/{id}";
        log.debug("Fetching user by ID: {} from URI: {}", userId, uri);
        
        return webClient.get()
                .uri(uri, userId)
                .retrieve()
                .bodyToMono(Map.class)
                .map(map -> (Map<String, Object>) map)
                .doOnSuccess(user -> log.info("Retrieved user: {}", user))
                .doOnError(error -> log.error("Error retrieving user {}: {}", userId, error.getMessage()));
    }

    @SuppressWarnings("unchecked")
    public Mono<Map<String, Object>> getUserByEmail(String email) {
        String uri = apiPath + "/email/{email}";
        log.debug("Fetching user by email: {} from URI: {}", email, uri);
        
        return webClient.get()
                .uri(uri, email)
                .retrieve()
                .bodyToMono(Map.class)
                .map(map -> (Map<String, Object>) map)
                .doOnSuccess(user -> log.info("Retrieved user by email: {}", user))
                .doOnError(error -> log.error("Error retrieving user by email {}: {}", email, error.getMessage()));
    }

    public Mono<Boolean> userExists(Long userId) {
        String uri = apiPath + "/{id}";
        log.debug("Checking if user exists: {} from URI: {}", userId, uri);
        
        return webClient.get()
                .uri(uri, userId)
                .retrieve()
                .toBodilessEntity()
                .map(response -> true)
                .onErrorReturn(false)
                .doOnSuccess(exists -> log.info("User {} exists: {}", userId, exists));
    }

    public Mono<Boolean> userExistsByEmail(String email) {
        String uri = apiPath + "/email/{email}";
        log.debug("Checking if user exists by email: {} from URI: {}", email, uri);
        
        return webClient.get()
                .uri(uri, email)
                .retrieve()
                .toBodilessEntity()
                .map(response -> true)
                .onErrorReturn(false)
                .doOnSuccess(exists -> log.info("User with email {} exists: {}", email, exists));
    }

    @SuppressWarnings("unchecked")
    public Mono<Map<String, Object>> getUserByUsername(String username) {
        String uri = apiPath + "/username/{username}";
        log.debug("Fetching user by username: {} from URI: {}", username, uri);
        
        return webClient.get()
                .uri(uri, username)
                .retrieve()
                .bodyToMono(Map.class)
                .map(map -> (Map<String, Object>) map)
                .doOnSuccess(user -> log.info("Retrieved user by username: {}", user))
                .doOnError(error -> log.error("Error retrieving user by username {}: {}", username, error.getMessage()));
    }
} 