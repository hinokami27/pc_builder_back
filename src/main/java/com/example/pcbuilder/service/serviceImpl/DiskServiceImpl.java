package com.example.pcbuilder.service.serviceImpl;

import com.example.pcbuilder.model.Disk;
import com.example.pcbuilder.repository.DiskRepository;
import com.example.pcbuilder.service.ComponentService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DiskServiceImpl implements ComponentService<Disk> {


    private final DiskRepository diskRepository;

    public DiskServiceImpl(DiskRepository diskRepository) {
        this.diskRepository = diskRepository;
    }


    @Override
    public List<Disk> findAll() {
        return diskRepository.findAll();
    }

    @Override
    public Optional<Disk> findById(Long id) {
        return diskRepository.findById(id);
    }

    @Override
    public Disk findByUrl(String url) {
        return diskRepository.findByStoreUrl(url);
    }

    @Override
    public void deleteComponent(Long id) {
        diskRepository.deleteById(id);
    }

    @Override
    public void editComponent(Long id, Disk component) {
        Disk disk = diskRepository.findById(id).orElseThrow(RuntimeException::new);
        disk.setModelName(component.getModelName());
        disk.setPriceMkd(component.getPriceMkd());
        disk.setStoreName(component.getStoreName());
        disk.setStoreUrl(component.getStoreUrl());
        disk.setImgUrl(component.getImgUrl());
        disk.setBrand(component.getBrand());
        disk.setSize(component.getSize());
        disk.setDiskType(component.getDiskType());
        disk.setConnector(component.getConnector());
        disk.setSpeed(component.getSpeed());
        diskRepository.save(disk);
    }

    @Override
    public void deleteAllComponents() {
        diskRepository.deleteAll();
    }
}
