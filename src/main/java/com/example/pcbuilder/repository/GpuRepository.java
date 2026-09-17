package com.example.pcbuilder.repository;

import com.example.pcbuilder.model.Gpu;
import com.example.pcbuilder.model.PcCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface GpuRepository extends JpaRepository<Gpu, Long> {
    List<Gpu> findAllByStoreUrl(String storeUrl);
    Gpu findByStoreUrl(String storeUrl);
    List<Gpu> findAllByLengthLessThan(int length);

    List<Gpu> findTop10ByPriceMkdAfterOrderByPriceMkdAsc(Double minPrice, Double maxPrice);

}
