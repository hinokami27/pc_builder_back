package com.example.pcbuilder.pojo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class PcBuildDto {
    public String cpuUrl;
    public String coolerUrl;
    public String motherboardUrl;
    public String gpuUrl;
    public List<String> ramUrls;
    public List<String> storageUrls;
    public String caseUrl;
    public String psuUrl;
}