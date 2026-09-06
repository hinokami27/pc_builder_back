package com.example.pcbuilder.repository;

import com.example.pcbuilder.model.Cpu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CpuRepository extends JpaRepository<Cpu, Long> {
    List<Cpu> findAllByStoreUrl(String storeUrl);
    Cpu findByStoreUrl(String storeUrl);

    List<Cpu> findAllBySocket(String socket);

}
