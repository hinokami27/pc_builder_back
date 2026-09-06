package com.example.pcbuilder.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Data
@NoArgsConstructor
@Getter
@Setter
public class PcCase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String modelName;
    private Double priceMkd;
    private String storeName;

    @Column(unique = true, nullable = false)
    private String storeUrl;
    private String imgUrl;
    private String brand;
    private String formFactor;
    private String coolerHeight;
    private String gpuLength;

    public PcCase(String modelName, Double priceMkd, String storeName, String storeUrl, String imgUrl, String brand, String formFactor, String coolerHeight, String gpuLength) {
        this.modelName = modelName;
        this.priceMkd = priceMkd;
        this.storeName = storeName;
        this.storeUrl = storeUrl;
        this.imgUrl = imgUrl;
        this.brand = brand;
        this.formFactor = formFactor;
        this.coolerHeight = coolerHeight;
        this.gpuLength = gpuLength;
    }
}
