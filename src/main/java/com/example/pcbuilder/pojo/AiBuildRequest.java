package com.example.pcbuilder.pojo;


import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record AiBuildRequest(
        @NotNull
        @Positive
        Double budget,

        @NotEmpty
        List<String> games
) {}