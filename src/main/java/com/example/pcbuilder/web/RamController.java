package com.example.pcbuilder.web;

import com.example.pcbuilder.model.Ram;
import com.example.pcbuilder.service.AnhochScraper;
import com.example.pcbuilder.service.ComponentService;
import com.example.pcbuilder.service.NeptunScraper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/ram")
public class RamController {

    private final ComponentService<Ram> ramService;
    private final AnhochScraper anhochScraper;
    private final NeptunScraper neptunScraper;

    public RamController(ComponentService<Ram> ramService, AnhochScraper anhochScraper, NeptunScraper neptunScraper) {
        this.ramService = ramService;
        this.anhochScraper = anhochScraper;
        this.neptunScraper = neptunScraper;
    }
    @GetMapping("/scrape")
    public String triggerScrape() {
//        ramService.deleteAllRam();
        anhochScraper.scrapeRams();
        neptunScraper.scrapeRams();
        return "redirect:/ram/showall";
    }
    @GetMapping("/showall")
    public String showAll(Model model){
        model.addAttribute("rams", ramService.findAll());
        return "viewRam";
    }
    @GetMapping("/edit-form/{id}")
    public String getEditPage(@PathVariable Long id, Model model){
        if(ramService.findById(id).isPresent()){
            Ram ram = ramService.findById(id).get();
            model.addAttribute("ram", ram);
        }
        return "editRamForm";
    }
    @GetMapping("/delete/{id}")
    public String deleteRam(@PathVariable Long id){
        if(ramService.findById(id).isPresent()){
            ramService.deleteComponent(id);
        }
        return "redirect:/ram/showall";
    }
    @PostMapping("/edit")
    public String editRam(@RequestParam(required = false) Long id,
                                  @RequestParam String modelName,
                                  @RequestParam Double priceMkd,
                                  @RequestParam String storeName,
                                  @RequestParam String storeUrl,
                                  @RequestParam String imgUrl,
                                  @RequestParam String ramSize,
                                  @RequestParam String ramType,
                                  @RequestParam String ramSpeed,
                                  @RequestParam String latency,
                                  @RequestParam String configuration,
                                  @RequestParam String brand){
        ramService.editComponent(id, new Ram(modelName, brand,storeName,storeUrl,imgUrl,priceMkd,ramType,ramSize,ramSpeed,latency,configuration));
        return "redirect:/ram/showall";
    }
}
