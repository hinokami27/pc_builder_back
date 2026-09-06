package com.example.pcbuilder.service.serviceImpl;

import com.example.pcbuilder.model.Motherboard;
import com.example.pcbuilder.repository.MotherboardRepository;
import com.example.pcbuilder.service.ComponentService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MotherboardServiceImpl implements ComponentService<Motherboard> {

    private final MotherboardRepository motherboardRepository;

    public MotherboardServiceImpl(MotherboardRepository motherboardRepository) {
        this.motherboardRepository = motherboardRepository;
    }

    @Override
    public List<Motherboard> findAll() {
        return motherboardRepository.findAll();
    }

    @Override
    public Optional<Motherboard> findById(Long id) {
        return motherboardRepository.findById(id);
    }

    @Override
    public Motherboard findByUrl(String url) {
        return  motherboardRepository.findByStoreUrl(url);
    }

    @Override
    public void deleteComponent(Long id) {
        motherboardRepository.deleteById(id);
    }

    @Override
    public void editComponent(Long id, Motherboard component) {
        Motherboard motherboard = motherboardRepository.findById(id).orElseThrow(RuntimeException::new);
        motherboard.setModelName(component.getModelName());
        motherboard.setBrand(component.getBrand());
        motherboard.setFormFactor(component.getFormFactor());
        motherboard.setStoreName(component.getStoreName());
        motherboard.setStoreUrl(component.getStoreUrl());
        motherboard.setImgUrl(component.getImgUrl());
        motherboard.setPriceMkd(component.getPriceMkd());
        motherboard.setChipset(component.getChipset());
        motherboard.setRamSpeed(component.getRamSpeed());
        motherboard.setRamType(component.getRamType());
        motherboard.setSocket(component.getSocket());
        motherboardRepository.save(motherboard);
    }

    @Override
    public void deleteAllComponents() {
        motherboardRepository.deleteAll();
    }
}
