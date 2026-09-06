package com.example.pcbuilder.repository;

import com.example.pcbuilder.model.Motherboard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MotherboardRepository extends JpaRepository<Motherboard, Long> {
    List<Motherboard> findAllByStoreUrl(String storeUrl);
    Motherboard findByStoreUrl(String storeUrl);

    @Query("SELECT m FROM Motherboard m WHERE " +
            "(:socket IS NULL OR m.socket = :socket) AND " +
            "(:caserank IS NULL OR (CASE " +
            "  WHEN m.formFactor = 'EATX' THEN 4 " +
            "  WHEN m.formFactor = 'ATX' THEN 3 " +
            "  WHEN m.formFactor = 'MATX' THEN 2 " +
            "  WHEN m.formFactor = 'ITX' THEN 1 " +
            "  ELSE 0 END) <= :caserank) AND" +
            "(:ramType IS NULL OR m.ramType = :ramType)")
    List<Motherboard> findCompatibleMotherboards(@Param("socket") String socket, @Param("caserank") Integer caserank, @Param("ramType") String ramType);

}
