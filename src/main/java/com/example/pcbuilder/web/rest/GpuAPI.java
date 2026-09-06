package com.example.pcbuilder.web.rest;

import com.example.pcbuilder.model.Cpu;
import com.example.pcbuilder.model.Gpu;
import com.example.pcbuilder.model.Psu;
import com.example.pcbuilder.service.ComponentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/gpu")
public class GpuAPI {

    private final ComponentService<Gpu> componentService;

    public GpuAPI(ComponentService<Gpu> componentService) {
        this.componentService = componentService;
    }

    @GetMapping("/all")
    public List<Gpu> getAll() {
        return componentService.findAll();
    }
    @GetMapping("/find")
    public Gpu findByUrl(@RequestParam String url) {
        return componentService.findByUrl(url);
    }
}
