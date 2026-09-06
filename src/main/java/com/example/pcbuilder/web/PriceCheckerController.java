package com.example.pcbuilder.web;

import com.example.pcbuilder.model.ProductPriceDate;
import com.example.pcbuilder.service.ProductPriceDateSevice;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/price")
public class PriceCheckerController {

    private final ProductPriceDateSevice productPriceDateSevice;

    public PriceCheckerController(ProductPriceDateSevice productPriceDateSevice) {
        this.productPriceDateSevice = productPriceDateSevice;
    }

    @GetMapping("/check")
    public String priceChecker(@RequestParam String storeUrl, Model model){
        List<ProductPriceDate> priceDates = productPriceDateSevice.findAllPriceDatesByStoreUrl(storeUrl);
        model.addAttribute("priceDates", priceDates);
        return "checkPrice";
    }
}
