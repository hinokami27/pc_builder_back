package com.example.pcbuilder.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OpenRouterResponse(
        List<Choice> choices
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Choice(
            Message message
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Message(
            String content
    ) {}
}