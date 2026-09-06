package com.example.pcbuilder.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Entity
@Data
@NoArgsConstructor
@Getter
@Setter
public class Cooler {

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
    private String sockets;
    private String coolerType;
    private String radiatorSize;
    private String coolerHeight;

    public Cooler(String modelName, Double priceMkd, String storeName, String storeUrl, String imgUrl, String brand, String sockets, String coolerType, String radiatorSize, String coolerHeight) {
        this.modelName = modelName;
        this.priceMkd = priceMkd;
        this.storeName = storeName;
        this.storeUrl = storeUrl;
        this.imgUrl = imgUrl;
        this.brand = brand;
        this.sockets = sockets;
        this.coolerType = coolerType;
        this.radiatorSize = radiatorSize;
        this.coolerHeight = coolerHeight;
    }
}
