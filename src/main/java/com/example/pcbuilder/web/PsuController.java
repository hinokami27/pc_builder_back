package com.example.pcbuilder.web;

import com.example.pcbuilder.model.Psu;
import com.example.pcbuilder.service.AnhochScraper;
import com.example.pcbuilder.service.ComponentService;
import com.example.pcbuilder.service.NeptunScraper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/psu")
public class PsuController {

    private final ComponentService<Psu> psuService;
    private final AnhochScraper anhochScraper;
    private final NeptunScraper neptunScraper;

    public PsuController(ComponentService<Psu> psuService, AnhochScraper anhochScraper, NeptunScraper neptunScraper) {
        this.psuService = psuService;
        this.anhochScraper = anhochScraper;
        this.neptunScraper = neptunScraper;
    }

    @GetMapping("/scrape")
    public String triggerScrape() {
//        psuService.deleteAllPsus();
        anhochScraper.scrapePsus();
        neptunScraper.scrapePsus();
        return "redirect:/psu/showall";
    }
    @GetMapping("/showall")
    public String showAll(Model model){
        model.addAttribute("psus", psuService.findAll());
        return "viewPsu";
    }
    @GetMapping("/edit-form/{id}")
    public String getEditPage(@PathVariable Long id, Model model){
        if(psuService.findById(id).isPresent()){
            Psu psu = psuService.findById(id).get();
            model.addAttribute("psu", psu);
        }
        return "editRamForm";
    }
    @GetMapping("/delete/{id}")
    public String deletePsu(@PathVariable Long id){
        if(psuService.findById(id).isPresent()){
            psuService.deleteComponent(id);
        }
        return "redirect:/psu/showall";
    }
    @PostMapping("/edit")
    public String editRam(@RequestParam(required = false) Long id,
                          @RequestParam String modelName,
                          @RequestParam Double priceMkd,
                          @RequestParam String storeName,
                          @RequestParam String storeUrl,
                          @RequestParam String imgUrl,
                          @RequestParam String efficiency,
                          @RequestParam String modular,
                          @RequestParam String wattage,
                          @RequestParam String brand){
        psuService.editComponent(id, new Psu(modelName, priceMkd, storeName, storeUrl, imgUrl, brand, wattage, efficiency, modular));
        return "redirect:/psu/showall";
    }
}
