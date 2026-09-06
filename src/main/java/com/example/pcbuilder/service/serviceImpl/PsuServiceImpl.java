package com.example.pcbuilder.service.serviceImpl;

import com.example.pcbuilder.model.Psu;
import com.example.pcbuilder.repository.PsuRepository;
import com.example.pcbuilder.service.ComponentService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PsuServiceImpl implements ComponentService<Psu> {

    private final PsuRepository psuRepository;

    public PsuServiceImpl(PsuRepository psuRepository) {
        this.psuRepository = psuRepository;
    }

    @Override
    public List<Psu> findAll() {
        return psuRepository.findAll();
    }

    @Override
    public Optional<Psu> findById(Long id) {
        return psuRepository.findById(id);
    }

    @Override
    public Psu findByUrl(String url) {
        return psuRepository.findByStoreUrl(url);
    }

    @Override
    public void deleteComponent(Long id) {
        psuRepository.deleteById(id);
    }

    @Override
    public void editComponent(Long id, Psu component) {
        Psu psu = psuRepository.findById(id).orElseThrow(RuntimeException::new);
        psu.setModelName(component.getModelName());
        psu.setPriceMkd(component.getPriceMkd());
        psu.setStoreName(component.getStoreName());
        psu.setStoreUrl(component.getStoreUrl());
        psu.setImgUrl(component.getImgUrl());
        psu.setBrand(component.getBrand());
        psu.setWattage(component.getWattage());
        psu.setEfficiency(component.getEfficiency());
        psu.setModular(component.getModular());
        psuRepository.save(psu);
    }

    @Override
    public void deleteAllComponents() {
        psuRepository.deleteAll();
    }
}
