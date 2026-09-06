package com.example.pcbuilder.service;

import com.example.pcbuilder.model.ProductPriceDate;

import java.util.List;

public interface ProductPriceDateSevice {
    void deletePriceDate(Long id);
    void deleteAllPriceDates();
    List<ProductPriceDate> findAllPriceDatesByStoreUrl(String storeUrl);

}
