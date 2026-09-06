package com.example.pcbuilder.web;

import com.example.pcbuilder.model.Cooler;
import com.example.pcbuilder.model.Cpu;
import com.example.pcbuilder.model.PcCase;
import com.example.pcbuilder.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/cooler")
public class CoolerController {

    private final ComponentService<Cooler> coolerService;
    private final AnhochScraper anhochScraper;
    private final NeptunScraper neptunScraper;

    public CoolerController(ComponentService<Cooler> coolerService, AnhochScraper anhochScraper, NeptunScraper neptunScraper) {
        this.coolerService = coolerService;
        this.anhochScraper = anhochScraper;
        this.neptunScraper = neptunScraper;
    }

    @GetMapping("/scrape")
    public String triggerScrape() {
//        coolerService.deleteAllComponents();
        neptunScraper.scrapeCoolers();
        anhochScraper.scrapeCoolers();
        return "redirect:/cooler/showall";
    }
    @GetMapping("/showall")
    public String showAll(Model model){
        model.addAttribute("coolers", coolerService.findAll());
        return "viewCooler";
    }
    @GetMapping("/edit-form/{id}")
    public String getEditPage(@PathVariable Long id, Model model){
        if(coolerService.findById(id).isPresent()){
            Cooler cooler = coolerService.findById(id).get();
            model.addAttribute("cooler", cooler);
        }
        return "editCoolerForm";
    }
    @GetMapping("/delete/{id}")
    public String deleteCooler(@PathVariable Long id){
        if(coolerService.findById(id).isPresent()){
            coolerService.deleteComponent(id);
        }
        return "redirect:/cooler/showall";
    }
    @PostMapping("/edit")
    public String editCooler(@RequestParam(required = false) Long id,
                          @RequestParam String modelName,
                          @RequestParam Double priceMkd,
                          @RequestParam String storeName,
                          @RequestParam String storeUrl,
                          @RequestParam String imgUrl,
                          @RequestParam String sockets,
                          @RequestParam String brand,
                          @RequestParam String radiatorSize,
                          @RequestParam String coolerHeight,
                          @RequestParam String coolerType){

        coolerService.editComponent(id, new Cooler(modelName, priceMkd, storeName, storeUrl, imgUrl, brand, sockets,  coolerType,  radiatorSize,  coolerHeight));
        return "redirect:/cooler/showall";
    }
}
