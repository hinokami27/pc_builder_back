package com.example.pcbuilder.util;

import java.util.HashMap;
import java.util.Map;

public class GpuTdpMap {
    public static Map<String, Integer> getGpuTdpMap() {
        Map<String, Integer> baseGpuTdp = new HashMap<>();

        // ==========================================
        // Base GPUs requested (Deduplicated)
        // ==========================================
        baseGpuTdp.put("GT710", 19);
        baseGpuTdp.put("RTX 3050", 130);
        baseGpuTdp.put("RTX 5050", 130);
        baseGpuTdp.put("RTX 5060", 145);
        baseGpuTdp.put("RTX 5060 Ti", 180);
        baseGpuTdp.put("RTX 5070", 250);
        baseGpuTdp.put("RTX 5070 Ti", 300);
        baseGpuTdp.put("RTX 5080", 360);
        baseGpuTdp.put("RTX 5090", 575);

        baseGpuTdp.put("RX 9060 XT", 160);
        baseGpuTdp.put("RX 9070 GRE", 220);
        baseGpuTdp.put("RX 9070", 220);
        baseGpuTdp.put("RX 9070 XT", 304);

        // ==========================================
        // Missing Base GPUs from Neighboring Generations
        // ==========================================

        // Legacy / Budget GT Series
        baseGpuTdp.put("GT 1030", 30);

        // RTX 30 Series (Ampere)
        baseGpuTdp.put("RTX 3060", 170);
        baseGpuTdp.put("RTX 3060 Ti", 200);
        baseGpuTdp.put("RTX 3070", 220);
        baseGpuTdp.put("RTX 3070 Ti", 290);
        baseGpuTdp.put("RTX 3080", 320);
        baseGpuTdp.put("RTX 3080 Ti", 350);
        baseGpuTdp.put("RTX 3090", 350);
        baseGpuTdp.put("RTX 3090 Ti", 450);

        // RTX 40 Series (Ada Lovelace)
        baseGpuTdp.put("RTX 4060", 115);
        baseGpuTdp.put("RTX 4060 Ti", 160);
        baseGpuTdp.put("RTX 4070", 200);
        baseGpuTdp.put("RTX 4070 SUPER", 220);
        baseGpuTdp.put("RTX 4070 Ti", 285);
        baseGpuTdp.put("RTX 4070 Ti SUPER", 285);
        baseGpuTdp.put("RTX 4080", 320);
        baseGpuTdp.put("RTX 4080 SUPER", 320);
        baseGpuTdp.put("RTX 4090", 450);

        // RX 7000 Series (RDNA 3)
        baseGpuTdp.put("RX 7600", 165);
        baseGpuTdp.put("RX 7600 XT", 190);
        baseGpuTdp.put("RX 7700 XT", 245);
        baseGpuTdp.put("RX 7800 XT", 263);
        baseGpuTdp.put("RX 7900 GRE", 268);
        baseGpuTdp.put("RX 7900 XT", 315);
        baseGpuTdp.put("RX 7900 XTX", 355);

        return baseGpuTdp;
    }
}
