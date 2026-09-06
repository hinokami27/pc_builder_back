package com.example.pcbuilder.repository;

import com.example.pcbuilder.model.Psu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PsuRepository extends JpaRepository<Psu, Long> {
    List<Psu> findAllByStoreUrl(String storeUrl);
    Psu findByStoreUrl(String storeUrl);

    @Query(value = "SELECT * FROM psu WHERE CAST(REGEXP_REPLACE(wattage, '[^0-9]', '') AS INT) >= :minWattage", nativeQuery = true)
    List<Psu> findPsusWithMinWattage(@Param("minWattage") Integer minWattage);
}
