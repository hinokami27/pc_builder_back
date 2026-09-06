package com.example.pcbuilder.web.rest;

import com.example.pcbuilder.model.ProductPriceDate;
import com.example.pcbuilder.repository.ProductPriceDateRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/price")
public class PriceCheckAPI {

    private final ProductPriceDateRepository productPriceDateRepository;

    public PriceCheckAPI(ProductPriceDateRepository productPriceDateRepository) {
        this.productPriceDateRepository = productPriceDateRepository;
    }

    @GetMapping("/history")
    public List<ProductPriceDate> getPriceHistory(@RequestParam String url) {
        return productPriceDateRepository.findAllByStoreUrl(url);
    }
}
