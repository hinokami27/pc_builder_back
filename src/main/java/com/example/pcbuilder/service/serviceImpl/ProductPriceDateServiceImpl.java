package com.example.pcbuilder.service.serviceImpl;

import com.example.pcbuilder.model.ProductPriceDate;
import com.example.pcbuilder.repository.ProductPriceDateRepository;
import com.example.pcbuilder.service.ProductPriceDateSevice;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductPriceDateServiceImpl implements ProductPriceDateSevice {

    private final ProductPriceDateRepository productPriceDateRepository;

    public ProductPriceDateServiceImpl(ProductPriceDateRepository productPriceDateRepository) {
        this.productPriceDateRepository = productPriceDateRepository;
    }

    @Override
    public void deletePriceDate(Long id) {
        productPriceDateRepository.deleteById(id);
    }

    @Override
    public void deleteAllPriceDates() {
        productPriceDateRepository.deleteAll();
    }

    @Override
    public List<ProductPriceDate> findAllPriceDatesByStoreUrl(String storeUrl) {
        return productPriceDateRepository.findAllByStoreUrl(storeUrl);
    }
}
