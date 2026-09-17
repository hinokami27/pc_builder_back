package com.example.pcbuilder.config;

import java.util.List;

public record OpenRouterRequest(
        String model,
        List<Message> messages,
        Double temperature,
        Integer max_tokens
) {

    public record Message(
            String role,
            String content
    ) {}
}