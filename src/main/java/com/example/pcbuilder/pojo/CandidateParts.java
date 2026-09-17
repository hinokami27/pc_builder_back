package com.example.pcbuilder.pojo;


import com.example.pcbuilder.model.*;

import java.util.List;

public record CandidateParts(
        List<Cpu> cpus,
        List<Gpu> gpus,
        List<Motherboard> motherboards,
        List<Ram> ram,
        List<Disk> storages,
        List<Psu> psus,
        List<PcCase> cases,
        List<Cooler> coolers
) {
}
