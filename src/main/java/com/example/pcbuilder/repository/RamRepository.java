package com.example.pcbuilder.repository;

import com.example.pcbuilder.model.Ram;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RamRepository extends JpaRepository<Ram, Long> {
    List<Ram> findAllByStoreUrl(String storeUrl);
    Ram findByStoreUrl(String storeUrl);

    List<Ram> findAllByRamType(String ramType);

    List<Ram> findAllByStoreUrlIn(List<String> storeUrls);
}
