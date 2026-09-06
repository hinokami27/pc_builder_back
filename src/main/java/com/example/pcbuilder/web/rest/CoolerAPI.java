package com.example.pcbuilder.web.rest;


import com.example.pcbuilder.model.Cooler;
import com.example.pcbuilder.model.Cpu;
import com.example.pcbuilder.service.ComponentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/cooler")
public class CoolerAPI {

    private final ComponentService<Cooler> componentService;

    public CoolerAPI(ComponentService<Cooler> componentService) {
        this.componentService = componentService;
    }

    @GetMapping("/all")
    public List<Cooler> getAll() {
        return componentService.findAll();
    }
    @GetMapping("/find")
    public Cooler findByUrl(@RequestParam String url) {
        return componentService.findByUrl(url);
    }
}
