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
public class Psu {

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
    private String wattage;
    private String efficiency;
    private String modular;

    public Psu(String modelName, Double priceMkd, String storeName, String storeUrl, String imgUrl, String brand, String wattage, String efficiency, String modular) {
        this.modelName = modelName;
        this.priceMkd = priceMkd;
        this.storeName = storeName;
        this.storeUrl = storeUrl;
        this.imgUrl = imgUrl;
        this.brand = brand;
        this.wattage = wattage;
        this.efficiency = efficiency;
        this.modular = modular;
    }
}
