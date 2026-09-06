package com.example.pcbuilder.pojo;

import com.example.pcbuilder.model.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class PcBuild {

    public Cpu cpu;
    public Cooler cooler;
    public Motherboard motherboard;
    public Gpu gpu;
    public List<Ram> rams;
    public List<Disk> disks;
    public PcCase kase;
    public Psu psu;

    public PcBuild(Cpu cpu, Cooler cooler, Motherboard motherboard, Gpu gpu, List<Ram> rams, List<Disk> disks, PcCase kase, Psu psu) {
        this.cpu = cpu;
        this.cooler = cooler;
        this.motherboard = motherboard;
        this.gpu = gpu;
        this.rams = rams;
        this.disks = disks;
        this.kase = kase;
        this.psu = psu;
    }
}
