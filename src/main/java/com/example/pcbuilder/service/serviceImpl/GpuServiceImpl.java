package com.example.pcbuilder.service.serviceImpl;

import com.example.pcbuilder.model.Gpu;
import com.example.pcbuilder.repository.GpuRepository;
import com.example.pcbuilder.service.ComponentService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GpuServiceImpl implements ComponentService<Gpu> {

    private final GpuRepository gpuRepository;

    public GpuServiceImpl(GpuRepository gpuRepository) {
        this.gpuRepository = gpuRepository;
    }

    @Override
    public List<Gpu> findAll() {
        return gpuRepository.findAll();
    }

    @Override
    public Optional<Gpu> findById(Long id) {
        return gpuRepository.findById(id);
    }

    @Override
    public Gpu findByUrl(String url) {
        return  gpuRepository.findByStoreUrl(url);
    }

    @Override
    public void deleteComponent(Long id) {
        gpuRepository.deleteById(id);
    }

    @Override
    public void editComponent(Long id, Gpu component) {
        Gpu gpu = gpuRepository.findById(id).orElseThrow(RuntimeException::new);
        gpu.setModelName(component.getModelName());
        gpu.setBrand(component.getBrand());
        gpu.setVram(component.getVram());
        gpu.setChipset(component.getChipset());
        gpu.setChipMaker(component.getChipMaker());
        gpu.setStoreName(component.getStoreName());
        gpu.setStoreUrl(component.getStoreUrl());
        gpu.setImgUrl(component.getImgUrl());
        gpu.setPriceMkd(component.getPriceMkd());
        gpu.setLength(component.getLength());
        gpuRepository.save(gpu);
    }

    @Override
    public void deleteAllComponents() {
        gpuRepository.deleteAll();
    }

}
