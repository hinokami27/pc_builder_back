package com.example.pcbuilder.web.rest;



import com.example.pcbuilder.pojo.AiBuildDto;
import com.example.pcbuilder.pojo.AiBuildRequest;
import com.example.pcbuilder.pojo.AiBuildResponse;
import com.example.pcbuilder.pojo.GameFpsEstimate;
import com.example.pcbuilder.service.AiBuildService;
import com.example.pcbuilder.service.CompatibilityService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ai-builds")
@CrossOrigin(origins = "http://localhost:5173")
public class AiBuildController {

    private final AiBuildService aiBuildService;
    private final CompatibilityService compatibilityService;

    public AiBuildController(
            AiBuildService aiBuildService, CompatibilityService compatibilityService
    ) {
        this.aiBuildService = aiBuildService;
        this.compatibilityService = compatibilityService;
    }

    @PostMapping("/generate")
    public AiBuildDto generateBuild(
            @Valid @RequestBody AiBuildRequest request
    ) throws JsonProcessingException {

        AiBuildResponse response =
                aiBuildService.generateBuild(request);

        return compatibilityService.resolveAiBuild(response);
    }

//    @PostMapping("/generate")
//    public AiBuildDto generateBuild(
//            @Valid @RequestBody AiBuildRequest request
//    ) throws JsonProcessingException {
//
//        AiBuildResponse response = new AiBuildResponse(
//                true,
//                909L,
//                56L,
//                102L,
//                List.of(45L, 45L),
//                List.of(75L),
//                25L,
//                5L,
//                null,
//                71297.0,
//                "Ryzen 5 7600 BOX with included cooler paired with RTX 5060 Ti delivers excellent 1080p/1440p gaming performance within budget. AM5 platform offers upgrade path, 16GB DDR5-6000 and 500GB NVMe provide balanced specs.",
//                List.of(new GameFpsEstimate("Cyberpunk 2077", 95, 65, 35),
//                        new GameFpsEstimate("Counter-Strike 2", 350, 280, 160),
//                        new GameFpsEstimate("Red Dead Redemption 2", 85, 60, 32))
//        );
//        return compatibilityService.resolveAiBuild(response);
//    }
}