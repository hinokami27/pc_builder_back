package com.example.pcbuilder.web.rest;

import com.example.pcbuilder.model.Cpu;
import com.example.pcbuilder.model.Motherboard;
import com.example.pcbuilder.service.ComponentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/motherboard")
public class MotherboardAPI {

    private final ComponentService<Motherboard>  componentService;

    public MotherboardAPI(ComponentService<Motherboard> componentService) {
        this.componentService = componentService;
    }

    @GetMapping("/all")
    public List<Motherboard> getAll() {
        return componentService.findAll();
    }
    @GetMapping("/find")
    public Motherboard findByUrl(@RequestParam String url) {
        return componentService.findByUrl(url);
    }
}
