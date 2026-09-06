package com.example.pcbuilder.web.rest;

import com.example.pcbuilder.model.Cpu;
import com.example.pcbuilder.model.Psu;
import com.example.pcbuilder.service.ComponentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/psu")
public class PsuAPI {

    private final ComponentService<Psu> componentService;

    public PsuAPI(ComponentService<Psu> componentService) {
        this.componentService = componentService;
    }

    @GetMapping("/all")
    public List<Psu> getAll() {
        return componentService.findAll();
    }
    @GetMapping("/find")
    public Psu findByUrl(@RequestParam String url) {
        return componentService.findByUrl(url);
    }
}
