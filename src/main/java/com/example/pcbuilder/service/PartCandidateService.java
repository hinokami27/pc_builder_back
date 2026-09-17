package com.example.pcbuilder.service;


import com.example.pcbuilder.pojo.CandidateParts;
import com.example.pcbuilder.repository.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PartCandidateService {

    private final CpuRepository cpuRepository;
    private final MotherboardRepository motherboardRepository;
    private final PsuRepository psuRepository;
    private final RamRepository ramRepository;
    private final CoolerRepository coolerRepository;
    private final GpuRepository gpuRepository;
    private final CaseRepository caseRepository;
    private final DiskRepository diskRepository;

    public PartCandidateService(
            CpuRepository cpuRepository, MotherboardRepository motherboardRepository, PsuRepository psuRepository, RamRepository ramRepository, CoolerRepository coolerRepository, GpuRepository gpuRepository, CaseRepository caseRepository, DiskRepository diskRepository
    ) {
        this.cpuRepository = cpuRepository;
        this.motherboardRepository = motherboardRepository;
        this.psuRepository = psuRepository;
        this.ramRepository = ramRepository;
        this.coolerRepository = coolerRepository;
        this.gpuRepository = gpuRepository;
        this.caseRepository = caseRepository;
        this.diskRepository = diskRepository;
    }

    public CandidateParts findCandidates(Double budget) {

        Double gpuMin = budget * 0.30;
        Double gpuMax = budget * 0.50;

        Double cpuMin = budget * 0.12;
        Double cpuMax = budget * 0.25;

        Double motherboardMin = budget * 0.07;
        Double motherboardMax = budget * 0.14;

        Double ramMin = budget * 0.05;
        Double ramMax = budget * 0.10;

        Double storageMin = budget * 0.04;
        Double storageMax = budget * 0.10;

        Double psuMin = budget * 0.05;
        Double psuMax = budget * 0.10;

        Double caseMin = budget * 0.03;
        Double caseMax = budget * 0.08;

        Double coolerMin = budget * 0.02;
        Double coolerMax = budget * 0.06;


        return new CandidateParts(
                cpuRepository.findTop10ByPriceMkdAfterOrderByPriceMkdAsc(cpuMin, cpuMax),
                gpuRepository.findTop10ByPriceMkdAfterOrderByPriceMkdAsc(gpuMin, gpuMax),
                motherboardRepository.findTop10ByPriceMkdAfterOrderByPriceMkdAsc(motherboardMin, motherboardMax),
                ramRepository.findTop10ByPriceMkdAfterOrderByPriceMkdAsc(ramMin, ramMax),
                diskRepository.findTop10ByPriceMkdAfterOrderByPriceMkdAsc(storageMin, storageMax),
                psuRepository.findTop10ByPriceMkdAfterOrderByPriceMkdAsc(psuMin, psuMax),
                caseRepository.findTop10ByPriceMkdAfterOrderByPriceMkdAsc(caseMin, caseMax),
                coolerRepository.findTop10ByPriceMkdAfterOrderByPriceMkdAsc(coolerMin,coolerMax)
        );
    }

//    private List<PartCandidate> convert(List<Part> parts) {
//
//        return parts.stream()
//                .map(this::toCandidate)
//                .toList();
//    }
//
//    private PartCandidate toCandidate(Part part) {
//
//        return new PartCandidate(
//                part.getId(),
//                part.getCategory().name(),
//                part.getName(),
//                part.getPrice(),
//                createSpecs(part)
//        );
//    }
//
//    private java.util.Map<String, Object> createSpecs(
//            Part part
//    ) {
//
//        /*
//         * Replace these with whatever fields your Part entity has.
//         */
//
//        return java.util.Map.of(
//                "description",
//                part.getDescription() == null
//                        ? ""
//                        : part.getDescription()
//        );
//    }
}