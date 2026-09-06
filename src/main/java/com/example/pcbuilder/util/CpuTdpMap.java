package com.example.pcbuilder.util;

import java.util.HashMap;
import java.util.Map;

public class CpuTdpMap {
    public static Map<String, Integer> getCpuTdpMap() {
        Map<String, Integer> cpuTdp = new HashMap<>();

        cpuTdp.put("AMD Athlon 3000G Dual-Core", 35);
        cpuTdp.put("AMD Ryzen 3 1200", 65);
        cpuTdp.put("AMD Ryzen 3 3200G", 65);
        cpuTdp.put("AMD Ryzen 5 5500", 65);
        cpuTdp.put("AMD Ryzen 5 5600X", 65);
        cpuTdp.put("AMD Ryzen 7 5800X", 105);
        cpuTdp.put("AMD Ryzen 7 5800X3D", 105);
        cpuTdp.put("AMD Ryzen 5 7500F", 65);
        cpuTdp.put("AMD Ryzen 5 7500X3D", 120);
        cpuTdp.put("AMD Ryzen 5 7600", 65);
        cpuTdp.put("AMD Ryzen 5 7600X", 105);
        cpuTdp.put("AMD Ryzen 7 7700X", 105);
        cpuTdp.put("AMD Ryzen 7 7800X3D", 120);
        cpuTdp.put("AMD Ryzen 9 7900X", 170);
        cpuTdp.put("AMD Ryzen 5 8400F", 65);
        cpuTdp.put("AMD Ryzen 5 8500G", 65);
        cpuTdp.put("AMD Ryzen 7 8700F", 65);
        cpuTdp.put("AMD Ryzen 7 8700G", 65);
        cpuTdp.put("AMD Ryzen 5 9600X", 65);
        cpuTdp.put("AMD Ryzen 7 9700X", 65);
        cpuTdp.put("AMD Ryzen 7 9800X3D", 120);
        cpuTdp.put("AMD Ryzen 7 9850X3D", 120);
        cpuTdp.put("AMD Ryzen 9 9900X", 120);
        cpuTdp.put("AMD Ryzen 9 9900X3D", 120);
        cpuTdp.put("AMD Ryzen 9 9950X", 170);
        cpuTdp.put("AMD Ryzen 9 9950X3D", 170);
        cpuTdp.put("AMD Ryzen 9 9950X3D2", 170);

        cpuTdp.put("Intel Core i3-12100", 60);
        cpuTdp.put("Intel Core i3-12100F", 58);
        cpuTdp.put("Intel Core i7-13700F", 65);
        cpuTdp.put("Intel Core i3-14100F", 58);
        cpuTdp.put("Intel Core i5-14400", 65);
        cpuTdp.put("Intel Core i5-14400F", 65);
        cpuTdp.put("Intel Core i5-14600K", 125);
        cpuTdp.put("Intel Core i7-14700K", 125);
        cpuTdp.put("Intel Core i7-14700KF", 125);
        cpuTdp.put("Intel Core i9-14900K", 125);
        cpuTdp.put("Intel Core i9-14900KF", 125);

        cpuTdp.put("Intel Core Ultra 5 225", 65);
        cpuTdp.put("Intel Core Ultra 5 225F", 65);
        cpuTdp.put("Intel Core Ultra 5 245KF", 125);
        cpuTdp.put("Intel Core Ultra 5 250K", 125);
        cpuTdp.put("Intel Core Ultra 5 250KF", 125);
        cpuTdp.put("Intel Core Ultra 7 265K", 125);
        cpuTdp.put("Intel Core Ultra 7 265KF", 125);
        cpuTdp.put("Intel Core Ultra 7 270K", 125);
        cpuTdp.put("Intel Core Ultra 9 285K", 125);

        // ==========================================
        // Key Missing Processors from Similar Generations
        // ==========================================

        // AMD Zen 1 / 1000 Series
        cpuTdp.put("AMD Ryzen 5 1600", 65);
        cpuTdp.put("AMD Ryzen 5 1600X", 95);
        cpuTdp.put("AMD Ryzen 7 1700", 65);
        cpuTdp.put("AMD Ryzen 7 1700X", 95);
        cpuTdp.put("AMD Ryzen 7 1800X", 95);

        // AMD Zen 2 / 3000 Series
        cpuTdp.put("AMD Ryzen 5 3600", 65);
        cpuTdp.put("AMD Ryzen 5 3600X", 95);
        cpuTdp.put("AMD Ryzen 7 3700X", 65);
        cpuTdp.put("AMD Ryzen 7 3800X", 105);
        cpuTdp.put("AMD Ryzen 9 3900X", 105);
        cpuTdp.put("AMD Ryzen 9 3950X", 105);

        // AMD Zen 3 / 5000 Series
        cpuTdp.put("AMD Ryzen 5 5600", 65);
        cpuTdp.put("AMD Ryzen 5 5600G", 65);
        cpuTdp.put("AMD Ryzen 7 5700X", 65);
        cpuTdp.put("AMD Ryzen 7 5700X3D", 105);
        cpuTdp.put("AMD Ryzen 7 5700G", 65);
        cpuTdp.put("AMD Ryzen 9 5900X", 105);
        cpuTdp.put("AMD Ryzen 9 5950X", 105);

        // AMD Zen 4 / 7000 Series
        cpuTdp.put("AMD Ryzen 7 7700", 65);
        cpuTdp.put("AMD Ryzen 9 7900", 65);
        cpuTdp.put("AMD Ryzen 9 7900X3D", 120);
        cpuTdp.put("AMD Ryzen 9 7950X", 170);
        cpuTdp.put("AMD Ryzen 9 7950X3D", 120);

        // Intel 12th Gen (Alder Lake)
        cpuTdp.put("Intel Core i5-12400", 65);
        cpuTdp.put("Intel Core i5-12400F", 65);
        cpuTdp.put("Intel Core i5-12600K", 125);
        cpuTdp.put("Intel Core i5-12600KF", 125);
        cpuTdp.put("Intel Core i7-12700K", 125);
        cpuTdp.put("Intel Core i7-12700KF", 125);
        cpuTdp.put("Intel Core i9-12900K", 125);
        cpuTdp.put("Intel Core i9-12900KF", 125);

        // Intel 13th Gen (Raptor Lake)
        cpuTdp.put("Intel Core i3-13100", 60);
        cpuTdp.put("Intel Core i3-13100F", 58);
        cpuTdp.put("Intel Core i5-13400", 65);
        cpuTdp.put("Intel Core i5-13400F", 65);
        cpuTdp.put("Intel Core i5-13600K", 125);
        cpuTdp.put("Intel Core i5-13600KF", 125);
        cpuTdp.put("Intel Core i7-13700K", 125);
        cpuTdp.put("Intel Core i7-13700KF", 125);
        cpuTdp.put("Intel Core i9-13900K", 125);
        cpuTdp.put("Intel Core i9-13900KF", 125);
        cpuTdp.put("Intel Core i9-13900KS", 150);

        // Intel 14th Gen (Raptor Lake Refresh)
        cpuTdp.put("Intel Core i3-14100", 60);
        cpuTdp.put("Intel Core i5-14600KF", 125);
        cpuTdp.put("Intel Core i9-14900KS", 150);

        // Intel Core Ultra 200 Series (Arrow Lake)
        cpuTdp.put("Intel Core Ultra 5 245K", 125);
        cpuTdp.put("Intel Core Ultra 7 265", 65);
        cpuTdp.put("Intel Core Ultra 7 265F", 65);
        cpuTdp.put("Intel Core Ultra 9 285", 65);
        cpuTdp.put("Intel Core Ultra 9 285KF", 125);

        return cpuTdp;
    }
}