package com.example.pcbuilder.model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@Getter
@Setter
public class ProductPriceDate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String storeUrl;
    private Double price;
    private LocalDate date;

    public ProductPriceDate(String storeUrl, Double price, LocalDate date) {
        this.storeUrl = storeUrl;
        this.price = price;
        this.date = date;
    }
}
