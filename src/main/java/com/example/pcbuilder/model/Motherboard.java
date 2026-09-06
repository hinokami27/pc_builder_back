package com.example.pcbuilder.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Data
@Getter
@Setter
@NoArgsConstructor
public class Motherboard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String modelName;
    private String brand;
    private String formFactor;
    private String storeName;

    @Column(unique = true, nullable = false)
    private String storeUrl;
    private String imgUrl;
    private Double priceMkd;
    private String chipset;
    private String ramSpeed;
    private String ramType;
    private String socket;

    public Motherboard(String modelName, String brand, String formFactor, String storeName, String storeUrl, String imgUrl, Double priceMkd, String chipset, String ramSpeed, String ramType, String socket) {
        this.modelName = modelName;
        this.brand = brand;
        this.formFactor = formFactor;
        this.storeName = storeName;
        this.storeUrl = storeUrl;
        this.imgUrl = imgUrl;
        this.chipset = chipset;
        this.ramSpeed = ramSpeed;
        this.ramType = ramType;
        this.priceMkd = priceMkd;
        this.socket = socket;
    }
}
