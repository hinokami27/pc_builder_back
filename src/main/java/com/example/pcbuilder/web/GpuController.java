package com.example.pcbuilder.web;


import com.example.pcbuilder.model.Gpu;
import com.example.pcbuilder.service.AnhochScraper;
import com.example.pcbuilder.service.ComponentService;
import com.example.pcbuilder.service.NeptunScraper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/gpu")
public class GpuController {

    private final AnhochScraper anhochScraper;
    private final NeptunScraper neptunScraper;
    private final ComponentService<Gpu> gpuService;

    public GpuController(AnhochScraper anhochScraper, NeptunScraper neptunScraper, ComponentService<Gpu> gpuService) {
        this.anhochScraper = anhochScraper;
        this.neptunScraper = neptunScraper;
        this.gpuService = gpuService;
    }

    @GetMapping("/scrape")
    public String triggerScrape() {
//        gpuService.deleteAllGpus();
        anhochScraper.scrapeGpus();
        neptunScraper.scrapeGpus();
        return "redirect:/gpu/showall";
    }
    @GetMapping("/showall")
    public String showAll(Model model){
        model.addAttribute("gpus", gpuService.findAll());
        return "viewGpu";
    }
    @GetMapping("/edit-form/{id}")
    public String getEditPage(@PathVariable Long id, Model model){
        if(gpuService.findById(id).isPresent()){
            Gpu gpu = gpuService.findById(id).get();
            model.addAttribute("gpu", gpu);
        }
        return "editGpuForm";
    }
    @GetMapping("/delete/{id}")
    public String deleteGpu(@PathVariable Long id){
        if(gpuService.findById(id).isPresent()){
            gpuService.deleteComponent(id);
        }
        return "redirect:/gpu/showall";
    }
    @PostMapping("/edit")
    public String editGpu(@RequestParam(required = false) Long id,
                          @RequestParam String modelName,
                          @RequestParam Double priceMkd,
                          @RequestParam String storeName,
                          @RequestParam String storeUrl,
                          @RequestParam String imgUrl,
                          @RequestParam String chipMaker,
                          @RequestParam String chipset,
                          @RequestParam String vram,
                          @RequestParam String brand,
                          @RequestParam Integer length){
        gpuService.editComponent(id, new Gpu(modelName, brand, vram, chipset, chipMaker, storeName, storeUrl, imgUrl, priceMkd, length));
        return "redirect:/gpu/showall";
    }
}
