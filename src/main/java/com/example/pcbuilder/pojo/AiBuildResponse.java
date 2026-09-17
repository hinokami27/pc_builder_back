package com.example.pcbuilder.pojo;

import java.util.List;

public record AiBuildResponse(
        Boolean possible,
        Long cpuId,
        Long gpuId,
        Long motherboardId,
        List<Long> ramIds,
        List<Long> storageIds,
        Long psuId,
        Long caseId,
        Long coolerId,
        Double totalPrice,
        String reason,
        List<GameFpsEstimate> fpsEstimates
) {}