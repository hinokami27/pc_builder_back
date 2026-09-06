package com.example.pcbuilder.repository;

import com.example.pcbuilder.model.PcCase;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CaseRepository extends JpaRepository<PcCase, Long> {
    List<PcCase> findAllByStoreUrl(String storeUrl);
    PcCase findByStoreUrl(String storeUrl);

    @Query("SELECT c FROM PcCase c WHERE " +
            "(:coolerHeight IS NULL OR c.coolerHeight >= :coolerHeight) AND " +
            "(:gpuLength IS NULL OR c.gpuLength >= :gpuLength) AND " +
            "(:minRank IS NULL OR (CASE " +
            "  WHEN c.formFactor = 'EATX' THEN 4 " +
            "  WHEN c.formFactor = 'ATX' THEN 3 " +
            "  WHEN c.formFactor = 'mATX' THEN 2 " +
            "  WHEN c.formFactor = 'ITX' THEN 1 " +
            "  ELSE 0 END) >= :minRank)")
    List<PcCase> findCompatibleCases(
            @Param("coolerHeight") Integer coolerHeight,
            @Param("gpuLength") Integer gpuLength,
            @Param("minRank") Integer minRank
    );
}
