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
public class Disk {

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
    private String size;
    private String diskType;
    private String speed;
    private String connector;

    public Disk(String modelName, Double priceMkd, String storeName, String storeUrl, String imgUrl, String brand, String size, String diskType, String connector, String speed) {
        this.modelName = modelName;
        this.priceMkd = priceMkd;
        this.storeName = storeName;
        this.storeUrl = storeUrl;
        this.imgUrl = imgUrl;
        this.brand = brand;
        this.size = size;
        this.diskType = diskType;
        this.connector = connector;
    }
}
