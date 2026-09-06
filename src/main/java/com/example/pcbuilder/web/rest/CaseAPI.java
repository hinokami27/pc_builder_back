package com.example.pcbuilder.web.rest;

import com.example.pcbuilder.model.PcCase;
import com.example.pcbuilder.service.ComponentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/case")
public class CaseAPI {

    private final ComponentService<PcCase> componentService;

    public CaseAPI(ComponentService<PcCase> componentService) {
        this.componentService = componentService;
    }

    @GetMapping("/all")
    public List<PcCase> getAll() {
        return componentService.findAll();
    }

    @GetMapping("/find")
    public PcCase findByUrl(@RequestParam String url) {
        return componentService.findByUrl(url);
    }
}
