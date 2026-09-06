package com.example.pcbuilder.pojo;


import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public record AiBuildRequest(
        @NotNull
        @DecimalMin("100")
        BigDecimal budget,

        @NotEmpty
        List<String> games,

        String resolution,

        Integer targetFps
) {
}