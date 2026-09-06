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
public class Cpu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String modelName;
    private Double priceMkd;
    private String storeName;

    @Column(unique = true, nullable = false)
    private String storeUrl;
    private String imgUrl;
    private String packageType;
    private String brand;
    private String socket;
    private String cores;
    private String threads;

    public Cpu(String modelName, Double priceMkd,String storeName, String storeUrl, String imgUrl, String packageType, String brand, String socket, String cores, String threads) {
        this.modelName = modelName;
        this.priceMkd = priceMkd;
        this.storeName = storeName;
        this.storeUrl = storeUrl;
        this.imgUrl = imgUrl;
        this.packageType = packageType;
        this.brand = brand;
        this.socket = socket;
        this.cores = cores;
        this.threads = threads;
    }
}
