package com.deportes.api.records;

public record LoginRequest(String email, String password, String fcmToken) {
}