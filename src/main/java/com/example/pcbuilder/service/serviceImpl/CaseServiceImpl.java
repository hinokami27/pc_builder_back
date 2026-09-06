package com.example.pcbuilder.service.serviceImpl;

import com.example.pcbuilder.model.PcCase;
import com.example.pcbuilder.repository.CaseRepository;
import com.example.pcbuilder.service.ComponentService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CaseServiceImpl implements ComponentService<PcCase> {

    private final CaseRepository caseRepository;

    public CaseServiceImpl(CaseRepository caseRepository) {
        this.caseRepository = caseRepository;
    }

    @Override
    public List<PcCase> findAll() {
        return caseRepository.findAll();
    }

    @Override
    public Optional<PcCase> findById(Long id) {
        return caseRepository.findById(id);
    }

    @Override
    public PcCase findByUrl(String url) {
        return caseRepository.findByStoreUrl(url);
    }

    @Override
    public void deleteComponent(Long id) {
        caseRepository.deleteById(id);
    }

    @Override
    public void editComponent(Long id, PcCase component) {
        PcCase kase = caseRepository.findById(id).orElseThrow(RuntimeException::new);
        kase.setModelName(component.getModelName());
        kase.setPriceMkd(component.getPriceMkd());
        kase.setStoreName(component.getStoreName());
        kase.setStoreUrl(component.getStoreUrl());
        kase.setImgUrl(component.getImgUrl());
        kase.setBrand(component.getBrand());
        kase.setFormFactor(component.getFormFactor());
        kase.setCoolerHeight(component.getCoolerHeight());
        kase.setGpuLength(component.getGpuLength());
        caseRepository.save(kase);
    }

    @Override
    public void deleteAllComponents() {
        caseRepository.deleteAll();
    }
}
