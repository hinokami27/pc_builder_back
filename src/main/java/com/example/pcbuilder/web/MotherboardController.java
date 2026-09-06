package com.example.pcbuilder.web;

import com.example.pcbuilder.model.Motherboard;
import com.example.pcbuilder.service.AnhochScraper;
import com.example.pcbuilder.service.ComponentService;
import com.example.pcbuilder.service.NeptunScraper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/motherboard")
public class MotherboardController {

    private final AnhochScraper anhochScraper;
    private final NeptunScraper neptunScraper;
    private final ComponentService<Motherboard> motherboardService;

    public MotherboardController(AnhochScraper anhochScraper, NeptunScraper neptunScraper, ComponentService<Motherboard> motherboardService) {
        this.anhochScraper = anhochScraper;
        this.neptunScraper = neptunScraper;
        this.motherboardService = motherboardService;
    }

    @GetMapping("/scrape")
    public String triggerScrape() {
//        motherboardService.deleteAllMotherboards();
        anhochScraper.scrapeMotherboards();
        neptunScraper.scrapeMotherboards();
        return "redirect:/motherboard/showall";
    }
    @GetMapping("/showall")
    public String showAll(Model model){
        model.addAttribute("motherboards", motherboardService.findAll());
        return "viewMotherboard";
    }
    @GetMapping("/edit-form/{id}")
    public String getEditPage(@PathVariable Long id, Model model){
        if(motherboardService.findById(id).isPresent()){
            Motherboard motherboard = motherboardService.findById(id).get();
            model.addAttribute("motherboard", motherboard);
        }
        return "editMotherboardForm";
    }
    @GetMapping("/delete/{id}")
    public String deleteMotherboard(@PathVariable Long id){
        if(motherboardService.findById(id).isPresent()){
            motherboardService.deleteComponent(id);
        }
        return "redirect:/motherboard/showall";
    }
    @PostMapping("/edit")
    public String editMotherboard(@RequestParam(required = false) Long id,
                          @RequestParam String modelName,
                          @RequestParam Double priceMkd,
                          @RequestParam String storeName,
                          @RequestParam String storeUrl,
                          @RequestParam String imgUrl,
                          @RequestParam String formFactor,
                          @RequestParam String chipset,
                          @RequestParam String ramSpeed,
                          @RequestParam String ramType,
                          @RequestParam String socket,
                          @RequestParam String brand){
        motherboardService.editComponent(id, new Motherboard(modelName,brand,formFactor,storeName,storeUrl,imgUrl,priceMkd,chipset,ramType,ramSpeed,socket));
        return "redirect:/motherboard/showall";
    }
}
