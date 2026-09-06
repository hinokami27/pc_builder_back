package com.example.pcbuilder.service.serviceImpl;

import com.example.pcbuilder.model.Cooler;
import com.example.pcbuilder.repository.CoolerRepository;
import com.example.pcbuilder.service.ComponentService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CoolerServiceImpl implements ComponentService<Cooler> {

    private final CoolerRepository coolerRepository;

    public CoolerServiceImpl(CoolerRepository coolerRepository) {
        this.coolerRepository = coolerRepository;
    }

    @Override
    public List<Cooler> findAll() {
        return coolerRepository.findAll();
    }

    @Override
    public Optional<Cooler> findById(Long id) {
        return coolerRepository.findById(id);
    }

    @Override
    public Cooler findByUrl(String url) {
        return coolerRepository.findByStoreUrl(url);
    }

    @Override
    public void deleteComponent(Long id) {
        coolerRepository.deleteById(id);
    }

    @Override
    public void editComponent(Long id, Cooler component) {
        Cooler cooler = coolerRepository.findById(id).orElseThrow(RuntimeException::new);
        cooler.setModelName(component.getModelName());
        cooler.setPriceMkd(component.getPriceMkd());
        cooler.setStoreName(component.getStoreName());
        cooler.setStoreUrl(component.getStoreUrl());
        cooler.setImgUrl(component.getImgUrl());
        cooler.setBrand(component.getBrand());
        cooler.setSockets(component.getSockets());
        cooler.setCoolerType(component.getCoolerType());
        cooler.setRadiatorSize(component.getRadiatorSize());
        cooler.setCoolerHeight(component.getCoolerHeight());
        coolerRepository.save(cooler);
    }

    @Override
    public void deleteAllComponents() {
        coolerRepository.deleteAll();
    }
}
