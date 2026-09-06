package com.example.pcbuilder.web;

import com.example.pcbuilder.model.PcCase;
import com.example.pcbuilder.service.AnhochScraper;
import com.example.pcbuilder.service.ComponentService;
import com.example.pcbuilder.service.NeptunScraper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/case")
public class CaseController {

    private final ComponentService<PcCase> caseService;
    private final AnhochScraper anhochScraper;
    private final NeptunScraper neptunScraper;

    public CaseController(ComponentService<PcCase> caseService, AnhochScraper anhochScraper, NeptunScraper neptunScraper) {
        this.caseService = caseService;
        this.anhochScraper = anhochScraper;
        this.neptunScraper = neptunScraper;
    }
    @GetMapping("/scrape")
    public String triggerScrape() {
//        caseService.deleteAllCases();
        anhochScraper.scrapeCases();
        neptunScraper.scrapeCases();
        return "redirect:/case/showall";
    }
    @GetMapping("/showall")
    public String showAll(Model model){
        model.addAttribute("cases", caseService.findAll());
        return "viewCase";
    }
    @GetMapping("/edit-form/{id}")
    public String getEditPage(@PathVariable Long id, Model model){
        if(caseService.findById(id).isPresent()){
            PcCase kase = caseService.findById(id).get();
            model.addAttribute("case", kase);
        }
        return "editCaseForm";
    }
    @GetMapping("/delete/{id}")
    public String deleteCase(@PathVariable Long id){
        if(caseService.findById(id).isPresent()){
            caseService.deleteComponent(id);
        }
        return "redirect:/case/showall";
    }
    @PostMapping("/edit")
    public String editCpu(@RequestParam(required = false) Long id,
                          @RequestParam String modelName,
                          @RequestParam Double priceMkd,
                          @RequestParam String storeName,
                          @RequestParam String storeUrl,
                          @RequestParam String imgUrl,
                          @RequestParam String formFactor,
                          @RequestParam String brand,
                          @RequestParam String gpuLength,
                          @RequestParam String coolerHeight){

        caseService.editComponent(id, new PcCase( modelName, priceMkd, storeName, storeUrl, imgUrl, brand, formFactor, coolerHeight, gpuLength));
        return "redirect:/case/showall";
    }
}
