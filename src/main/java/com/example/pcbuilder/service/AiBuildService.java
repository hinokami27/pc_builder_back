package com.example.pcbuilder.service;


import com.example.pcbuilder.config.OpenRouterClient;
import com.example.pcbuilder.pojo.AiBuildRequest;
import com.example.pcbuilder.pojo.AiBuildResponse;
import com.example.pcbuilder.pojo.CandidateParts;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Service;

@Service
public class AiBuildService {

    private final PartCandidateService candidateService;
    private final OpenRouterClient openRouterClient;
    private final CompatibilityService compatibilityService;

    public AiBuildService(
            PartCandidateService candidateService,
            OpenRouterClient openRouterClient,
            CompatibilityService compatibilityService
    ) {
        this.candidateService = candidateService;
        this.openRouterClient = openRouterClient;
        this.compatibilityService = compatibilityService;
    }

    public AiBuildResponse generateBuild(
            AiBuildRequest request
    ) throws JsonProcessingException {

        CandidateParts candidates =
                candidateService.findCandidates(
                        request.budget()
                );

        AiBuildResponse aiResponse =
                openRouterClient.recommendBuild(
                        request,
                        candidates
                );

        /*
         * IMPORTANT:
         *
         * Never trust the model's totalPrice.
         * Never trust that its IDs exist.
         * Never trust that its build is compatible.
         */

//        compatibilityService.validateAiBuild(
//                aiResponse,
//                request.budget()
//        );

        return aiResponse;
    }
}