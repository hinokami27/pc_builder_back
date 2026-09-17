package com.example.pcbuilder.repository;

import com.example.pcbuilder.model.Cpu;
import com.example.pcbuilder.model.Disk;
import com.example.pcbuilder.model.PcCase;
import com.example.pcbuilder.model.Ram;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiskRepository extends JpaRepository<Disk, Long> {
    List<Disk> findAllByStoreUrl(String storeUrl);
    Disk findByStoreUrl(String storeUrl);

    List<Disk> findAllByStoreUrlIn(List<String> storeUrls);

    List<Disk> findTop10ByPriceMkdAfterOrderByPriceMkdAsc(Double minPrice, Double maxPrice);
}
