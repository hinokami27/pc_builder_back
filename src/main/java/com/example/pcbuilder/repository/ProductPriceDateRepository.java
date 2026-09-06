package com.example.pcbuilder.repository;

import com.example.pcbuilder.model.ProductPriceDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ProductPriceDateRepository extends JpaRepository<ProductPriceDate, Long> {
    List<ProductPriceDate> findAllByStoreUrl(String storeUrl);
    ProductPriceDate findByStoreUrlAndDate(String storeUrl, LocalDate date);
}
