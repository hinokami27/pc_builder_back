package com.example.pcbuilder.pojo;


public record GameFpsEstimate(
        String game,
        Integer fps1080p,
        Integer fps1440p,
        Integer fps4k
) {}