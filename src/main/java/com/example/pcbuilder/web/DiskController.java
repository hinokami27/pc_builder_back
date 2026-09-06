package com.example.pcbuilder.web;

import com.example.pcbuilder.model.Disk;
import com.example.pcbuilder.service.AnhochScraper;
import com.example.pcbuilder.service.ComponentService;
import com.example.pcbuilder.service.NeptunScraper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/disk")
public class DiskController {

    private final ComponentService<Disk> diskService;
    private final AnhochScraper anhochScraper;
    private final NeptunScraper neptunScraper;


    public DiskController(ComponentService<Disk> diskService, AnhochScraper anhochScraper, NeptunScraper neptunScraper) {
        this.diskService = diskService;
        this.anhochScraper = anhochScraper;
        this.neptunScraper = neptunScraper;
    }
    @GetMapping("/scrape")
    public String triggerScrape() {
//        diskService.deleteAllDisks();
        anhochScraper.scrapeDisks();
        neptunScraper.scrapeDisks();
        return "redirect:/disk/showall";
    }
    @GetMapping("/showall")
    public String showAll(Model model){
        model.addAttribute("disks", diskService.findAll());
        return "viewDisk";
    }
    @GetMapping("/edit-form/{id}")
    public String getEditPage(@PathVariable Long id, Model model){
        if(diskService.findById(id).isPresent()){
            Disk disk = diskService.findById(id).get();
            model.addAttribute("disk", disk);
        }
        return "editDiskForm";
    }
    @GetMapping("/delete/{id}")
    public String deleteDisk(@PathVariable Long id){
        if(diskService.findById(id).isPresent()){
            diskService.deleteComponent(id);
        }
        return "redirect:/disk/showall";
    }
    @PostMapping("/edit")
    public String editDisk(@RequestParam(required = false) Long id,
                          @RequestParam String modelName,
                          @RequestParam Double priceMkd,
                          @RequestParam String storeName,
                          @RequestParam String storeUrl,
                          @RequestParam String imgUrl,
                          @RequestParam String brand,
                          @RequestParam String size,
                          @RequestParam String diskType,
                          @RequestParam String connector,
                          @RequestParam String speed){

        diskService.editComponent(id, new Disk(modelName, priceMkd, storeName, storeUrl, imgUrl, brand, size, diskType, connector,speed));
        return "redirect:/disk/showall";
    }
}
