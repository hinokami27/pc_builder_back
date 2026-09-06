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
public class Ram {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String brand;
    private String modelName;
    private String storeName;

    @Column(unique = true, nullable = false)
    private String storeUrl;
    private String imgUrl;
    private Double priceMkd;
    private String ramType;
    private String ramSize;
    private String ramSpeed;
    private String latency;
    private String configuration;

    public Ram(String modelName, String brand, String storeName, String storeUrl, String imgUrl, Double priceMkd, String ramType, String ramSize, String ramSpeed, String latency, String configuration) {
        this.modelName = modelName;
        this.brand = brand;
        this.storeName = storeName;
        this.storeUrl = storeUrl;
        this.imgUrl = imgUrl;
        this.priceMkd = priceMkd;
        this.ramType = ramType;
        this.ramSize = ramSize;
        this.ramSpeed = ramSpeed;
        this.latency = latency;
        this.configuration = configuration;
    }
}
