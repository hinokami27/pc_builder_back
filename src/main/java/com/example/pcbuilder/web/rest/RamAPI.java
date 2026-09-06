package com.example.pcbuilder.web.rest;

import com.example.pcbuilder.model.Cpu;
import com.example.pcbuilder.model.Ram;
import com.example.pcbuilder.service.ComponentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/ram")
public class RamAPI {

    private final ComponentService<Ram> componentService;

    public RamAPI(ComponentService<Ram> componentService) {
        this.componentService = componentService;
    }

    @GetMapping("/all")
    public List<Ram> getAll() {
        return componentService.findAll();
    }
    @GetMapping("/find")
    public Ram findByUrl(@RequestParam String url) {
        return componentService.findByUrl(url);
    }
}
