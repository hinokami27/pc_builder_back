package com.example.pcbuilder.service;

import com.example.pcbuilder.model.*;
import com.example.pcbuilder.pojo.PcBuild;
import com.example.pcbuilder.pojo.PcBuildDto;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface CompatibilityService {
    PcBuild resolveBuild(PcBuildDto pcBuildDto);
    List<Cpu> getCompatibleCpus(PcBuild build);
    List<Cooler> getCompatibleCoolers(PcBuild build);
    List<Motherboard> getCompatibleMotherboards(PcBuild build);
    List<Gpu> getCompatibleGpus(PcBuild build);
    List<Ram> getCompatibleRams(PcBuild build);
    List<Disk> getCompatibleDisks(PcBuild build);
    List<PcCase> getCompatiblePcCases(PcBuild build);
    List<Psu> getCompatiblePsus(PcBuild build);

    Integer calculateWattage(PcBuild pcBuild);

    List<String> compatibilityCheck(PcBuild pcBuild);
}
