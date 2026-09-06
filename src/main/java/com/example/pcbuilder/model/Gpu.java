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
public class Gpu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String modelName;
    private String brand;
    private String vram;
    private String chipset;
    private String chipMaker;
    private String storeName;

    @Column(unique = true, nullable = false)
    private String storeUrl;
    private String imgUrl;
    private Double priceMkd;
    private Integer length;

    public Gpu(String modelName, String brand, String vram, String chipset, String chipMaker, String storeName, String storeUrl, String imgUrl, Double priceMkd, Integer length) {
        this.modelName = modelName;
        this.brand = brand;
        this.vram = vram;
        this.chipset = chipset;
        this.chipMaker = chipMaker;
        this.storeName = storeName;
        this.storeUrl = storeUrl;
        this.imgUrl = imgUrl;
        this.priceMkd = priceMkd;
        this.length = length;
    }
}
