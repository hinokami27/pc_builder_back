package com.example.pcbuilder.web.rest;

import com.example.pcbuilder.model.*;
import com.example.pcbuilder.pojo.PcBuild;
import com.example.pcbuilder.pojo.PcBuildDto;
import com.example.pcbuilder.service.CompatibilityService;
import com.example.pcbuilder.service.ComponentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/compatible")
public class CompatibilityAPI {

    private final CompatibilityService compatibilityService;

    public CompatibilityAPI(CompatibilityService compatibilityService) {
        this.compatibilityService = compatibilityService;
    }

    @PostMapping("/cpus")
    public List<Cpu> getCompatibleCpus(@RequestBody PcBuildDto dto) {
        return compatibilityService.getCompatibleCpus(compatibilityService.resolveBuild(dto));
    }

    @PostMapping("/coolers")
    public List<Cooler> getCompatibleCoolers(@RequestBody PcBuildDto dto) {
        return compatibilityService.getCompatibleCoolers(compatibilityService.resolveBuild(dto));
    }

    @PostMapping("/motherboards")
    public List<Motherboard> getCompatibleMotherboards(@RequestBody PcBuildDto dto) {
        return compatibilityService.getCompatibleMotherboards(compatibilityService.resolveBuild(dto));
    }

    @PostMapping("/gpus")
    public List<Gpu> getCompatibleGpus(@RequestBody PcBuildDto dto) {
        return compatibilityService.getCompatibleGpus(compatibilityService.resolveBuild(dto));
    }

    @PostMapping("/rams")
    public List<Ram> getCompatibleRam(@RequestBody PcBuildDto dto) {
        return compatibilityService.getCompatibleRams(compatibilityService.resolveBuild(dto));
    }

    @PostMapping("/psus")
    public List<Psu> getCompatiblePsus(@RequestBody PcBuildDto dto) {
        return compatibilityService.getCompatiblePsus(compatibilityService.resolveBuild(dto));
    }

    @PostMapping("/disks")
    public List<Disk> getCompatibleStorage(@RequestBody PcBuildDto dto) {
        return compatibilityService.getCompatibleDisks(compatibilityService.resolveBuild(dto));
    }

    @PostMapping("/cases")
    public List<PcCase> getCompatibleCases(@RequestBody PcBuildDto dto) {
        return compatibilityService.getCompatiblePcCases(compatibilityService.resolveBuild(dto));
    }
    @PostMapping("/wattage")
    public Integer calculateWattage(@RequestBody PcBuildDto dto) {
        return compatibilityService.calculateWattage(compatibilityService.resolveBuild(dto));
    }
    @PostMapping("/check")
    public List<String> compatibleCheck(@RequestBody PcBuildDto dto) {
        return compatibilityService.compatibilityCheck(compatibilityService.resolveBuild(dto));
    }
}
