package com.example.pcbuilder.repository;

import com.example.pcbuilder.model.Cooler;
import com.example.pcbuilder.model.Motherboard;
import com.example.pcbuilder.model.PcCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CoolerRepository extends JpaRepository<Cooler, Long> {
    List<Cooler> findAllByStoreUrl(String storeUrl);
    Cooler findByStoreUrl(String storeUrl);

    @Query("SELECT c FROM Cooler c WHERE " +
            "(:socket IS NULL OR c.sockets LIKE %:socket%) AND " +
            "(:maxHeight IS NULL OR c.coolerHeight < :maxHeight)")
    List<Cooler> findCompatibleCoolers(@Param("socket") String socket, @Param("maxHeight") Integer maxHeight);

    List<Cooler> findTop10ByPriceMkdAfterOrderByPriceMkdAsc(Double minPrice, Double maxPrice);
}
