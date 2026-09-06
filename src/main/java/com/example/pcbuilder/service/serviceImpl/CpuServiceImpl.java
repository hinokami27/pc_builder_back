package com.example.pcbuilder.service.serviceImpl;

import com.example.pcbuilder.model.Cpu;
import com.example.pcbuilder.repository.CpuRepository;
import com.example.pcbuilder.service.ComponentService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CpuServiceImpl implements ComponentService<Cpu> {

    private final CpuRepository cpuRepository;

    public CpuServiceImpl(CpuRepository cpuRepository) {
        this.cpuRepository = cpuRepository;
    }

    @Override
    public List<Cpu> findAll() {
        return cpuRepository.findAll();
    }

    @Override
    public Optional<Cpu> findById(Long id) {
        return cpuRepository.findById(id);
    }

    @Override
    public Cpu findByUrl(String url) {
        return  cpuRepository.findByStoreUrl(url);
    }

    @Override
    public void deleteComponent(Long id) {
        cpuRepository.deleteById(id);
    }

    @Override
    public void editComponent(Long id, Cpu component) {
        Cpu cpu = cpuRepository.findById(id).orElseThrow(RuntimeException::new);
        cpu.setModelName(component.getModelName());
        cpu.setPriceMkd(component.getPriceMkd());
        cpu.setStoreName(component.getStoreName());
        cpu.setStoreUrl(component.getStoreUrl());
        cpu.setImgUrl(component.getImgUrl());
        cpu.setPackageType(component.getPackageType());
        cpu.setBrand(component.getBrand());
        cpu.setSocket(component.getSocket());
        cpu.setCores(component.getCores());
        cpu.setThreads(component.getThreads());
        cpuRepository.save(cpu);
    }

    @Override
    public void deleteAllComponents() {
        cpuRepository.deleteAll();
    }
}
