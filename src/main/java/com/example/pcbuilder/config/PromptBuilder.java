package com.example.pcbuilder.service;

import com.example.pcbuilder.pojo.AiBuildRequest;
import com.example.pcbuilder.pojo.CandidateParts;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PromptBuilder {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String buildPrompt(
            AiBuildRequest request,
            CandidateParts candidates
    ) {

        try {

            String candidatesJson =
                    objectMapper.writeValueAsString(candidates);

            return """
                    You are a PC build selection engine.

                    Your task is to select the best compatible gaming PC build
                    from the provided candidate components.

                    USER REQUIREMENTS:

                    Budget: %.2f MKD

                    Games:
                    %s


                    CANDIDATE COMPONENTS:

                    %s


                    IMPORTANT RULES:

                    1. Use ONLY components from the provided candidates.
                    2. Never invent components, prices, specifications, or IDs.
                    3. Every selected component ID MUST exist in the provided candidate data.
                    4. The total component price must not exceed the user's budget.
                    5. Maximize gaming performance, prioritizing GPU performance.
                    6. Ensure all selected components are compatible.
                    7. RAM may contain MULTIPLE modules or kits.
                    8. Storage may contain MULTIPLE drives.
                    9. RAM and storage selections may contain different products.
                    10. Do not select duplicate RAM or storage unless there is a good reason.
                    11. If a CPU is a BOX processor and includes a stock cooler, set coolerId to null.
                    12. Do NOT add an aftermarket cooler when the CPU already includes a stock cooler.
                    13. coolerId is optional and may be null.
                    14. Never add an aftermarket cooler if doing so would cause the build to exceed the budget.
                    15. The final total price must account for EVERY selected component.
                    16. If no build can reasonably satisfy the user's gaming requirements within the budget, return possible=false.
                    17. When possible=false, do not select any components.
                    18. Do not explain calculations.
                    19. Do not list candidates.
                    20. Do not output reasoning.
                    21. Do not output markdown.
                    22. Do not output code fences.
                    23. Return ONLY ONE JSON OBJECT.
                    24. Do NOT return URLs.
                    25. Return database IDs only.
                    26. The reason must be no more than 2 sentences.
                    27. Do not output anything before or after the JSON object.


                    ID SELECTION RULES:

                    - cpuId: exactly one CPU ID.
                    - gpuId: exactly one GPU ID.
                    - motherboardId: exactly one motherboard ID.
                    - ramIds: one or more RAM product IDs.
                    - storageIds: one or more storage product IDs.
                    - psuId: exactly one PSU ID.
                    - caseId: exactly one case ID.
                    - coolerId: either one cooler ID or null.

                    Every ID must come directly from the candidate data.

                    Never invent an ID.

                    Do not return component names.

                    Do not return component URLs.


                    RAM:

                    ramIds contains the selected RAM products.

                    Multiple RAM products are allowed.

                    Example:

                    "ramIds": [101, 102]

                    means both RAM products are selected.

                    Different RAM products may be selected if they are compatible
                    and make sense for the build.

                    Prefer a sensible amount of RAM for the requested games.

                    Do not select multiple RAM products simply to increase the
                    number of components.

                    Ensure all selected RAM is compatible with the motherboard.


                    STORAGE:

                    storageIds contains the selected storage products.

                    Multiple storage drives are allowed.

                    Example:

                    "storageIds": [201, 202]

                    means both storage products are selected.

                    Different storage products may be selected.

                    Select multiple drives when doing so provides a sensible
                    configuration and remains within budget.

                    Do not select unnecessary storage just to use more of the budget.


                    COOLER:

                    A cooler is optional.

                    If the selected CPU is a BOX processor and includes a stock
                    cooler, return:

                    "coolerId": null

                    Do not select an aftermarket cooler in this situation.

                    Never exceed the budget because of a cooler.


                    COMPATIBILITY:

                    Pay particular attention to:

                    - CPU socket ↔ motherboard socket
                    - CPU generation ↔ motherboard compatibility
                    - RAM type ↔ motherboard RAM support
                    - RAM configuration ↔ motherboard support
                    - GPU physical dimensions ↔ case GPU clearance
                    - CPU cooler compatibility ↔ CPU socket
                    - CPU cooler height ↔ case cooler clearance
                    - PSU wattage ↔ total system power
                    - Motherboard ↔ selected CPU
                    - Storage interface ↔ motherboard support

                    Avoid fundamentally incompatible components.


                    GAMING PERFORMANCE:

                    The primary objective is gaming performance.

                    Prioritize the GPU when making tradeoffs.

                    Use the provided component specifications and your knowledge
                    of gaming hardware to estimate FPS.

                    FPS estimates are estimates only and are not guaranteed.

                    You MUST provide one FPS estimate for EVERY requested game.

                    Each game MUST have FPS estimates for:

                    - 1080p
                    - 1440p
                    - 4K

                    Do not ask the user for a resolution.

                    Do not ask the user for a target FPS.

                    Use reasonable High/Ultra gaming settings when estimating FPS.

                    Each FPS estimate MUST use this structure:

                    {
                      "game": "Game name",
                      "fps1080p": 0,
                      "fps1440p": 0,
                      "fps4k": 0
                    }


                    IMPOSSIBLE BUILDS:

                    If the requested games cannot reasonably be played at
                    acceptable gaming performance within the budget using the
                    provided candidates, return possible=false.

                    Do NOT force a build when the requested performance is
                    unrealistic.

                    Do NOT provide alternative builds.

                    Do NOT list rejected components.

                    Simply return possible=false with a short reason.


                    OUTPUT FORMAT:

                    If the build is impossible, return:

                    {
                      "possible": false,
                      "reason": "Build impossible for the requested games within the budget.",
                      "cpuId": null,
                      "gpuId": null,
                      "motherboardId": null,
                      "ramIds": [],
                      "storageIds": [],
                      "psuId": null,
                      "caseId": null,
                      "coolerId": null,
                      "totalPrice": null,
                      "fpsEstimates": []
                    }


                    If the build is possible, return:

                    {
                      "possible": true,
                      "cpuId": 0,
                      "gpuId": 0,
                      "motherboardId": 0,
                      "ramIds": [0],
                      "storageIds": [0],
                      "psuId": 0,
                      "caseId": 0,
                      "coolerId": null,
                      "totalPrice": 0,
                      "reason": "Short explanation of why this build was selected.",
                      "fpsEstimates": [
                        {
                          "game": "Game name",
                          "fps1080p": 0,
                          "fps1440p": 0,
                          "fps4k": 0
                        }
                      ]
                    }


                    FINAL OUTPUT RULES:

                    Return ONLY ONE valid JSON object.

                    Do not show your reasoning.

                    Do not show calculations.

                    Do not discuss candidate components.

                    Do not explain why individual components were rejected.

                    Do not output markdown.

                    Do not output code fences.

                    Do not output text before the JSON.

                    Do not output text after the JSON.

                    Return ONLY valid JSON.
                    """.formatted(
                    request.budget(),
                    String.join(", ", request.games()),
                    candidatesJson
            );

        } catch (JsonProcessingException e) {

            throw new RuntimeException(
                    "Failed to serialize candidate components to JSON",
                    e
            );
        }
    }
}