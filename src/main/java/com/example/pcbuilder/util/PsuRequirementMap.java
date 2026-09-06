package com.example.pcbuilder.util;

import java.util.HashMap;
import java.util.Map;

public class PsuRequirementMap {

    /**
     * Returns a Map of Base GPU Model Names to Manufacturer Recommended PSU Wattages (in Watts).
     */
    public static Map<String, Integer> getGpuPsuRequirementMap() {
        Map<String, Integer> psuRequirements = new HashMap<>();

        // ==========================================
        // Base GPUs Requested
        // ==========================================
        psuRequirements.put("GT710", 300);
        psuRequirements.put("RTX 3050", 300);
        psuRequirements.put("RTX 5050", 450);
        psuRequirements.put("RTX 5060", 550);
        psuRequirements.put("RTX 5060 Ti", 600);
        psuRequirements.put("RTX 5070", 650);
        psuRequirements.put("RTX 5070 Ti", 750);
        psuRequirements.put("RTX 5080", 850);
        psuRequirements.put("RTX 5090", 1000);

        psuRequirements.put("RX 9060 XT", 550);
        psuRequirements.put("RX 9070 GRE", 650);
        psuRequirements.put("RX 9070", 650);
        psuRequirements.put("RX 9070 XT", 750);

        // ==========================================
        // Common Missing GPUs (Neighboring Gens)
        // ==========================================

        // Legacy / Low Power
        psuRequirements.put("GT 1030", 300);

        // RTX 30 Series (Ampere)
        psuRequirements.put("RTX 3060", 550);
        psuRequirements.put("RTX 3060 Ti", 600);
        psuRequirements.put("RTX 3070", 650);
        psuRequirements.put("RTX 3070 Ti", 750);
        psuRequirements.put("RTX 3080", 750);
        psuRequirements.put("RTX 3080 Ti", 750);
        psuRequirements.put("RTX 3090", 750);
        psuRequirements.put("RTX 3090 Ti", 850);

        // RTX 40 Series (Ada Lovelace)
        psuRequirements.put("RTX 4060", 550);
        psuRequirements.put("RTX 4060 Ti", 550);
        psuRequirements.put("RTX 4070", 650);
        psuRequirements.put("RTX 4070 SUPER", 650);
        psuRequirements.put("RTX 4070 Ti", 700);
        psuRequirements.put("RTX 4070 Ti SUPER", 700);
        psuRequirements.put("RTX 4080", 750);
        psuRequirements.put("RTX 4080 SUPER", 750);
        psuRequirements.put("RTX 4090", 850);

        // RX 7000 Series (RDNA 3)
        psuRequirements.put("RX 7600", 550);
        psuRequirements.put("RX 7600 XT", 600);
        psuRequirements.put("RX 7700 XT", 700);
        psuRequirements.put("RX 7800 XT", 700);
        psuRequirements.put("RX 7900 GRE", 700);
        psuRequirements.put("RX 7900 XT", 750);
        psuRequirements.put("RX 7900 XTX", 800);

        return psuRequirements;
    }
}