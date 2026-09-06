package com.example.pcbuilder.web;

import com.example.pcbuilder.model.Cpu;
import com.example.pcbuilder.service.AnhochScraper;
import com.example.pcbuilder.service.ComponentService;
import com.example.pcbuilder.service.NeptunScraper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/cpu")
public class CpuController {

    private final ComponentService<Cpu> cpuService;
    private final AnhochScraper anhochScraper;
    private final NeptunScraper neptunScraper;

    public CpuController(ComponentService<Cpu> cpuService, AnhochScraper anhochScraper, NeptunScraper neptunScraper) {
        this.cpuService = cpuService;
        this.anhochScraper = anhochScraper;
        this.neptunScraper = neptunScraper;
    }

    @GetMapping("/scrape")
    public String triggerScrape() {
//        cpuService.deleteAllComponents();
        anhochScraper.scrapeCpus();
        neptunScraper.scrapeCpus();
        return "redirect:/cpu/showall";
    }
    @GetMapping("/showall")
    public String showAll(Model model){
        model.addAttribute("cpus", cpuService.findAll());
        return "viewCpu";
    }
    @GetMapping("/edit-form/{id}")
    public String getEditPage(@PathVariable Long id, Model model){
        if(cpuService.findById(id).isPresent()){
            Cpu cpu = cpuService.findById(id).get();
            model.addAttribute("cpu", cpu);
        }
        return "editCpuForm";
    }
    @GetMapping("/delete/{id}")
    public String deleteCpu(@PathVariable Long id){
        if(cpuService.findById(id).isPresent()){
            cpuService.deleteComponent(id);
        }
        return "redirect:/cpu/showall";
    }
    @PostMapping("/edit")
    public String editCpu(@RequestParam(required = false) Long id,
                          @RequestParam String modelName,
                          @RequestParam Double priceMkd,
                          @RequestParam String storeName,
                          @RequestParam String storeUrl,
                          @RequestParam String imgUrl,
                          @RequestParam String packageType,
                          @RequestParam String brand,
                          @RequestParam String cores,
                          @RequestParam String threads,
                          @RequestParam String socket){

        cpuService.editComponent(id, new Cpu(modelName, priceMkd, storeName, storeUrl, imgUrl, packageType, brand, socket, cores, threads));
        return "redirect:/cpu/showall";
    }
}
