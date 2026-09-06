package com.example.pcbuilder.service.serviceImpl;

import com.example.pcbuilder.model.Ram;
import com.example.pcbuilder.repository.RamRepository;
import com.example.pcbuilder.service.ComponentService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RamServiceImpl implements ComponentService<Ram> {

    private final RamRepository ramRepository;

    public RamServiceImpl(RamRepository ramRepository) {
        this.ramRepository = ramRepository;
    }

    @Override
    public List<Ram> findAll() {
        return ramRepository.findAll();
    }

    @Override
    public Optional<Ram> findById(Long id) {
        return ramRepository.findById(id);
    }

    @Override
    public Ram findByUrl(String url) {
        return ramRepository.findByStoreUrl(url);
    }

    @Override
    public void deleteComponent(Long id) {
        ramRepository.deleteById(id);
    }

    @Override
    public void editComponent(Long id, Ram component) {
        Ram ram = ramRepository.findById(id).orElseThrow(RuntimeException::new);
        ram.setModelName(component.getModelName());
        ram.setBrand(component.getBrand());
        ram.setStoreName(component.getStoreName());
        ram.setStoreUrl(component.getStoreUrl());
        ram.setImgUrl(component.getImgUrl());
        ram.setPriceMkd(component.getPriceMkd());
        ram.setRamType(component.getRamType());
        ram.setRamSize(component.getRamSize());
        ram.setRamSpeed(component.getRamSpeed());
        ram.setLatency(component.getLatency());
        ram.setConfiguration(component.getConfiguration());
        ramRepository.save(ram);
    }

    @Override
    public void deleteAllComponents() {
        ramRepository.deleteAll();
    }
}
