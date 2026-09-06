package com.example.pcbuilder.web.rest;

import com.example.pcbuilder.model.Cpu;
import com.example.pcbuilder.service.ComponentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/cpu")
public class CpuAPI {

    private final ComponentService<Cpu> componentService;

    public CpuAPI(ComponentService<Cpu> componentService) {
        this.componentService = componentService;
    }

    @GetMapping("/all")
    public List<Cpu> getAll() {
        return componentService.findAll();
    }
    @GetMapping("/find")
    public Cpu findByUrl(@RequestParam String url) {
        return componentService.findByUrl(url);
    }
}
