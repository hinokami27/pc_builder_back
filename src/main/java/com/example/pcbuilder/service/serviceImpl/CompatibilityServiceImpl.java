package com.example.pcbuilder.service.serviceImpl;

import com.example.pcbuilder.model.*;
import com.example.pcbuilder.pojo.AiBuildDto;
import com.example.pcbuilder.pojo.AiBuildResponse;
import com.example.pcbuilder.pojo.PcBuild;
import com.example.pcbuilder.pojo.PcBuildDto;
import com.example.pcbuilder.repository.*;
import com.example.pcbuilder.service.CompatibilityService;
import org.springframework.stereotype.Service;
import com.example.pcbuilder.util.GpuTdpMap;
import com.example.pcbuilder.util.CpuTdpMap;
import com.example.pcbuilder.util.PsuRequirementMap;

import java.util.*;

@Service
public class CompatibilityServiceImpl implements CompatibilityService {

    private final CpuRepository cpuRepository;
    private final CoolerRepository coolerRepository;
    private final MotherboardRepository motherboardRepository;
    private final GpuRepository gpuRepository;
    private final RamRepository ramRepository;
    private final DiskRepository diskRepository;
    private final CaseRepository caseRepository;
    private final PsuRepository psuRepository;

    public CompatibilityServiceImpl(CpuRepository cpuRepository, CoolerRepository coolerRepository, MotherboardRepository motherboardRepository, GpuRepository gpuRepository, RamRepository ramRepository, DiskRepository diskRepository, CaseRepository caseRepository, PsuRepository psuRepository) {
        this.cpuRepository = cpuRepository;
        this.coolerRepository = coolerRepository;
        this.motherboardRepository = motherboardRepository;
        this.gpuRepository = gpuRepository;
        this.ramRepository = ramRepository;
        this.diskRepository = diskRepository;
        this.caseRepository = caseRepository;
        this.psuRepository = psuRepository;
    }


    @Override
    public PcBuild resolveBuild(PcBuildDto dto) {
        PcBuild pcBuild = new PcBuild();

        if(dto.cpuUrl != null) {
            pcBuild.setCpu(cpuRepository.findByStoreUrl(dto.cpuUrl));
        }
        if(dto.coolerUrl != null) {
            pcBuild.setCooler(coolerRepository.findByStoreUrl(dto.coolerUrl));
        }
        if(dto.motherboardUrl != null) {
            pcBuild.setMotherboard(motherboardRepository.findByStoreUrl(dto.motherboardUrl));
        }
        if(dto.gpuUrl != null) {
            pcBuild.setGpu(gpuRepository.findByStoreUrl(dto.gpuUrl));
        }
        if(dto.ramUrls != null){
            pcBuild.setRams(ramRepository.findAllByStoreUrlIn(dto.getRamUrls()));
        }
        if(dto.storageUrls != null){
            pcBuild.setDisks(diskRepository.findAllByStoreUrlIn(dto.getStorageUrls()));
        }
        if(dto.psuUrl != null){
            pcBuild.setPsu(psuRepository.findByStoreUrl(dto.psuUrl));
        }
        if(dto.caseUrl != null){
            pcBuild.setKase(caseRepository.findByStoreUrl(dto.caseUrl));
        }
        return pcBuild;
    }

    @Override
    public List<Cpu> getCompatibleCpus(PcBuild build) {
        if(build.motherboard != null) {
            return cpuRepository.findAllBySocket(build.motherboard.getSocket());
        }
            return cpuRepository.findAll();
    }

    @Override
    public List<Cooler> getCompatibleCoolers(PcBuild build) {
        Integer maxHeight = (build.kase != null) ? Integer.parseInt(build.kase.getCoolerHeight()) : null;
        String socket = (build.cpu != null) ? build.cpu.getSocket() : null;

        return coolerRepository.findCompatibleCoolers(socket, maxHeight);
    }

    @Override
    public List<Motherboard> getCompatibleMotherboards(PcBuild build) {
        int caseRank = 0;
        if (build.kase != null && build.kase.getFormFactor() != null) {
            caseRank = switch (build.kase.getFormFactor().toUpperCase()) {
                case "EATX" -> 4;
                case "ATX" -> 3;
                case "MATX", "MICRO-ATX" -> 2;
                case "ITX", "MINI-ITX" -> 1;
                default -> 0;
            };
        }

        String socket = (build.getCpu() != null) ? build.getCpu().getSocket() : null;
        String ramType = (!build.getRams().isEmpty()) ? build.getRams().get(0).getRamType() : null;

        return motherboardRepository.findCompatibleMotherboards(socket, caseRank, ramType);
    }

    @Override
    public List<Gpu> getCompatibleGpus(PcBuild build) {
        if(build.kase != null) {
            return gpuRepository.findAllByLengthLessThan(Integer.parseInt(build.kase.getGpuLength()));
        }
        return gpuRepository.findAll();
    }

    @Override
    public List<Ram> getCompatibleRams(PcBuild build) {
        if(build.motherboard != null) {
            return ramRepository.findAllByRamType(build.motherboard.getRamType());
        }
        return ramRepository.findAll();
    }

    @Override
    public List<Disk> getCompatibleDisks(PcBuild build) {
        return diskRepository.findAll();
    }

    @Override
    public List<PcCase> getCompatiblePcCases(PcBuild build) {
        Integer moborank = null;
        if (build.motherboard != null && build.motherboard.getFormFactor() != null) {
            moborank = switch (build.motherboard.getFormFactor().toUpperCase()) {
                case "EATX" -> 4;
                case "ATX" -> 3;
                case "MATX" -> 2;
                case "ITX" -> 1;
                default -> 0;
            };
        }

        Integer coolerHeight = null;
        if (build.cooler != null && build.cooler.getCoolerHeight() != null) {
            String digits = build.cooler.getCoolerHeight().replaceAll("[^0-9]", "");
            if (!digits.isEmpty()) {
                coolerHeight = Integer.parseInt(digits);
            }
        }

        Integer gpuLength = build.gpu != null ? build.gpu.getLength() : null;

        return caseRepository.findCompatibleCases(coolerHeight, gpuLength, moborank);
    }

    @Override
    public List<Psu> getCompatiblePsus(PcBuild build) {
        return psuRepository.findPsusWithMinWattage(calculateWattage(build));
    }

    @Override
    public Integer calculateWattage(PcBuild pcBuild) {
        if (pcBuild == null) {
            return 0;
        }
        int wattage = 0;
        double overhead = 1.2;
        int requiredWattage = 0;
        Map<String, Integer> cpuTdpMap = CpuTdpMap.getCpuTdpMap();
        Map<String, Integer> gpuTdpMap = GpuTdpMap.getGpuTdpMap();
        Map<String, Integer> psuRequirementMap = PsuRequirementMap.getGpuPsuRequirementMap();

        if (pcBuild.cpu != null && pcBuild.cpu.getModelName() != null) {
            Integer cpuTdp = cpuTdpMap.get(pcBuild.cpu.getModelName());
            if (cpuTdp != null) {
                wattage += cpuTdp;
            }
        }
        if (pcBuild.motherboard != null && pcBuild.motherboard.getFormFactor() != null) {
            int moboTdp = 0;
            switch (pcBuild.motherboard.getFormFactor().toUpperCase()) {
                case "EATX" -> moboTdp = 80;
                case "ATX" -> moboTdp = 70;
                case "MATX" -> moboTdp = 60;
                default -> moboTdp = 50;
            }
            wattage += moboTdp;
        }
        if (pcBuild.gpu != null && pcBuild.gpu.getChipset() != null) {
            Integer gpuTdp = gpuTdpMap.get(pcBuild.gpu.getChipset());
            requiredWattage = psuRequirementMap.get(pcBuild.gpu.getChipset());
            if (gpuTdp != null) {
                wattage += gpuTdp;
            }
        }
        if (pcBuild.rams != null && !pcBuild.rams.isEmpty()) {
            for (Ram ram : pcBuild.rams) {
                if (ram != null && ram.getRamType() != null) {
                    if ("DDR5".equalsIgnoreCase(ram.getRamType())) {
                        wattage += 8;
                    } else if ("DDR4".equalsIgnoreCase(ram.getRamType())) {
                        wattage += 5;
                    } else {
                        wattage += 5;
                    }
                }
            }
        }
        if (pcBuild.disks != null) {
            for (Disk disk : pcBuild.disks) {
                if (disk == null) continue;

                if ("SSD".equalsIgnoreCase(disk.getDiskType())) {
                    if ("NVMe".equalsIgnoreCase(disk.getSpeed())) {
                        wattage += 10;
                    } else {
                        wattage += 5;
                    }
                } else if ("HDD".equalsIgnoreCase(disk.getDiskType())) {
                    wattage += 20;
                }
            }
        }
        wattage = (int) Math.ceil(wattage * overhead);
        return Math.max(requiredWattage,wattage);
    }

    @Override
    public List<String> compatibilityCheck(PcBuild pcBuild) {
        List<String> compatibilityCheck = new ArrayList<>();

        if(pcBuild.cpu != null && pcBuild.cooler != null) {
            if(!pcBuild.cooler.getSockets().contains(pcBuild.cpu.getSocket())) {
                compatibilityCheck.add("The cpu and cooler sockets don't match. Select a cooler that supports " + pcBuild.cpu.getSocket() + " sockets.");
            }
        }
        if(pcBuild.cpu != null && pcBuild.motherboard != null) {
            if(!Objects.equals(pcBuild.cpu.getSocket(), pcBuild.motherboard.getSocket())) {
                compatibilityCheck.add("The cpu and motherboard sockets don't match. We recommend choosing a platform and building around that ex:AM5.");
            }
        }
        if(pcBuild.rams.size()>1){
            if(pcBuild.rams.stream().map(Ram::getRamType).distinct().count() > 1){
                compatibilityCheck.add("Selected ram sticks are not of the same type. Make sure they are the same and match the motherboards supported ram type.");
            }
            if(pcBuild.rams.stream().map(Ram::getRamSpeed).distinct().count() > 1){
                compatibilityCheck.add("Selected ram sticks are not of the same speed. They will all perform at the speed of the slowest stick.");
            }
        }else{
            //TODO CHECK IF RAM NULL
            if(!pcBuild.rams.isEmpty()){
                if(pcBuild.motherboard != null) {
                    if (!Objects.equals(pcBuild.rams.get(0).getRamType(), pcBuild.motherboard.getRamType())) {
                        compatibilityCheck.add("Selected ram is not supported by the motherboard. Choose " + pcBuild.motherboard.getRamType() + " ram.");
                    }
                }
            }
        }
        if(pcBuild.cooler != null && pcBuild.kase != null && !pcBuild.kase.getCoolerHeight().equals("N/A") && !pcBuild.cooler.getCoolerHeight().equals("N/A")) {
            if(pcBuild.cooler.getCoolerType().equals("Air Cooler")){
                if(Integer.parseInt(pcBuild.cooler.getCoolerHeight()) >= Integer.parseInt(pcBuild.kase.getCoolerHeight())){
                    compatibilityCheck.add("Cooler may not fit inside of the selected case. Please double check the vendor/manufacturer page.");
                }
            }
        }
        if(pcBuild.gpu != null && pcBuild.kase != null && !pcBuild.kase.getGpuLength().equals("N/A")) {
            if(pcBuild.gpu.getLength() >= Integer.parseInt(pcBuild.kase.getGpuLength())){
                compatibilityCheck.add("Graphics card may not fit inside of the selected case. Please double check the vendor/manufacturer page.");
            }
        }
        if(pcBuild.psu != null) {
            int needWattage = calculateWattage(pcBuild);
            if(Integer.parseInt(pcBuild.psu.getWattage().replace("W","")) < needWattage){
                compatibilityCheck.add("Selected psu may not provide the necessary wattage. Choose, at minimum, a " +needWattage + "W unit.");
            }
        }
        return compatibilityCheck;
    }

    @Override
    public AiBuildDto resolveAiBuild(AiBuildResponse response) {
        AiBuildDto aiBuildDto = new AiBuildDto();
        if (!response.possible()) {
            aiBuildDto.possible = false;
            aiBuildDto.pcBuild = null;
            aiBuildDto.price = null;
            aiBuildDto.reason = null;
            aiBuildDto.fpsEstimates = null;
            return aiBuildDto;
        }

        aiBuildDto.possible = true;
        PcBuild pcBuild = new PcBuild();
        Double totalPrice = 0.0;

        pcBuild.cpu = cpuRepository.getReferenceById(response.cpuId());
        totalPrice += pcBuild.cpu.getPriceMkd();

        if (response.coolerId() != null) {
            pcBuild.cooler = coolerRepository.getReferenceById(response.coolerId());
            totalPrice += pcBuild.cooler.getPriceMkd();
        } else {
            pcBuild.cooler = null;
        }

        pcBuild.gpu = gpuRepository.getReferenceById(response.gpuId());
        totalPrice += pcBuild.gpu.getPriceMkd();

        pcBuild.motherboard = motherboardRepository.getReferenceById(response.motherboardId());
        totalPrice += pcBuild.motherboard.getPriceMkd();

        pcBuild.rams = response.ramIds()
                .stream()
                .map(ramRepository::getReferenceById)
                .toList();

        for (Ram ram : pcBuild.rams) {
            totalPrice += ram.getPriceMkd();
        }

        pcBuild.disks = response.storageIds()
                .stream()
                .map(diskRepository::getReferenceById)
                .toList();

        for (Disk storage : pcBuild.disks) {
            totalPrice += storage.getPriceMkd();
        }

        pcBuild.psu = psuRepository.getReferenceById(response.psuId());
        totalPrice += pcBuild.psu.getPriceMkd();

        pcBuild.kase = caseRepository.getReferenceById(response.caseId());
        totalPrice += pcBuild.kase.getPriceMkd();

        if(compatibilityCheck(pcBuild).isEmpty()){
            aiBuildDto.pcBuild = pcBuild;
            aiBuildDto.price = totalPrice;
            aiBuildDto.reason = response.reason();
            aiBuildDto.fpsEstimates = response.fpsEstimates();
        }else{
            aiBuildDto.possible = false;
            aiBuildDto.pcBuild = null;
            aiBuildDto.price = null;
        }
        return aiBuildDto;
    }


}
