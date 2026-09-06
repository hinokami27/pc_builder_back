package com.example.pcbuilder.service;

import com.example.pcbuilder.model.Cpu;

import java.util.List;
import java.util.Optional;

public interface ComponentService<T> {
    List<T> findAll();
    Optional<T> findById(Long id);
    T findByUrl(String url);
    void deleteComponent(Long id);
    void editComponent(Long id, T component);
    void deleteAllComponents();
}
