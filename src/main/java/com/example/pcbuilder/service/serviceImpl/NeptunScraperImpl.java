package com.example.pcbuilder.service.serviceImpl;

import com.example.pcbuilder.model.*;
import com.example.pcbuilder.repository.*;
import com.example.pcbuilder.service.NeptunScraper;
import org.apache.logging.log4j.util.Strings;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.boot.json.JsonParser;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@Service
public class NeptunScraperImpl implements NeptunScraper {

    private final CpuRepository cpuRepository;
    private final MotherboardRepository motherboardRepository;
    private final GpuRepository gpuRepository;
    private final RamRepository ramRepository;
    private final DiskRepository diskRepository;
    private final PsuRepository psuRepository;
    private final CaseRepository caseRepository;
    private final CoolerRepository coolerRepository;
    private final ProductPriceDateRepository productPriceDateRepository;

    public NeptunScraperImpl(CpuRepository cpuRepository, MotherboardRepository motherboardRepository, GpuRepository gpuRepository, RamRepository ramRepository, DiskRepository diskRepository, PsuRepository psuRepository, CaseRepository caseRepository, CoolerRepository coolerRepository, ProductPriceDateRepository productPriceDateRepository) {
        this.cpuRepository = cpuRepository;
        this.motherboardRepository = motherboardRepository;
        this.gpuRepository = gpuRepository;
        this.ramRepository = ramRepository;
        this.diskRepository = diskRepository;
        this.psuRepository = psuRepository;
        this.caseRepository = caseRepository;
        this.coolerRepository = coolerRepository;
        this.productPriceDateRepository = productPriceDateRepository;
    }

    @Override
    public void scrapeCpus() {
        String url = "https://www.neptun.mk/NeptunCategories/LoadProductsForCategory";
        try {


            String jsonPayload = "{"
                    + "\"CategoryId\": 335,"
                    + "\"Sort\": 4,"
                    + "\"Manufacturers\": [],"
                    + "\"Recomended\": false,"
                    + "\"PriceRange\": null,"
                    + "\"ItemsPerPage\": 100,"
                    + "\"CurrentPage\": 1"
                    + "}";

            String jsonResponse = Jsoup.connect(url)
                    .method(Connection.Method.POST) // Must be POST to send a body
                    .ignoreContentType(true)
                    .userAgent("Mozilla/5.0")
                    .header("Content-Type", "application/json") // Tell the server you're sending JSON
                    .header("Accept", "application/json")
                    .header("Referer", "https://www.neptun.mk/")
                    .requestBody(jsonPayload) // This attaches the payload
                    .execute()
                    .body();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(jsonResponse);

            JsonNode products = root.get("Batch").get("Items");


            for (JsonNode node : products) {
                String storeUrl = "https://www.neptun.mk/categories/procesori/" + node.get("Url").asText();
                double price = node.get("RegularPrice").asDouble();
                Cpu cpu = cpuRepository.findByStoreUrl(storeUrl);
                if(cpu != null) {
                    if(productPriceDateRepository.findByStoreUrlAndDate(storeUrl, LocalDate.now()) == null) {
                        ProductPriceDate productPriceDate = new ProductPriceDate();
                        productPriceDate.setStoreUrl(storeUrl);
                        productPriceDate.setDate(LocalDate.now());
                        productPriceDate.setPrice(price);
                        productPriceDateRepository.save(productPriceDate);
                    }
                    cpu.setPriceMkd(price);
                    cpuRepository.save(cpu);
                    continue;
                }

                cpu = new Cpu();
                cpu.setModelName(node.get("Title").asText());
                cpu.setPriceMkd(price);

                cpu.setStoreUrl(storeUrl);
                cpu.setStoreName("Neptun");
                String imgUrl = "https://www.neptun.mk/" + node.path("Thumbnail").asText("");
                cpu.setImgUrl(imgUrl);
                if(cpu.getImgUrl() == null || cpu.getImgUrl().isEmpty()){
                    cpu.setImgUrl("https://www.anhoch.com/themes/storefront/public/images/image-placeholder.png");
                }
                String name = cpu.getModelName();
                if(name.toLowerCase().contains("amd")) {
                    String[] splitname = name.trim().split(" ");
                    String packageType = "BOX";
                    String newName = splitname[1] + " " + splitname[2] + " " + splitname[3] + " " + splitname[4];
                    if(name.toLowerCase().contains("box")) { packageType = "BOX"; }
                    if(name.toLowerCase().contains("tray")) { packageType = "TRAY"; }
                    cpu.setModelName(newName);
                    cpu.setPackageType(packageType);
                }
                if(name.toLowerCase().contains("intel")) {
                    String[] splitname = name.trim().split(" ");
                    String packageType = "";
                    String newName;
                    if(name.toLowerCase().contains("ultra")) {
                        newName = splitname[1] + " " + splitname[2] + " " + splitname[3] + " " + splitname[4] + " " + splitname[5];
                        if(name.toLowerCase().contains("box")) { packageType = "BOX"; }
                    }
                    else{
                        newName = splitname[1] + " " + splitname[2] + " " + splitname[3];
                        if(name.toLowerCase().contains("box")) {
                            packageType = "BOX";
                        }
                        if(name.toLowerCase().contains("tray")) {
                            packageType = "TRAY";
                        }
                    }
                    cpu.setModelName(newName);
                    cpu.setPackageType(packageType);

                }
                record CpuSpecs(int cores, int threads) {}

                cpu.setCores("N/A");
                cpu.setThreads("N/A");
                Map<String, CpuSpecs> LOOKUP_MAP = new HashMap<>();
                {
                    LOOKUP_MAP.put("INTEL CORE ULTRA 5 225", new CpuSpecs(10, 10));
                    LOOKUP_MAP.put("INTEL CORE ULTRA 5 225F", new CpuSpecs(10, 10));
                    LOOKUP_MAP.put("INTEL CORE ULTRA 5 245KF", new CpuSpecs(14, 14));
                    LOOKUP_MAP.put("INTEL CORE ULTRA 5 250K", new CpuSpecs(18, 18));
                    LOOKUP_MAP.put("INTEL CORE ULTRA 5 250KF", new CpuSpecs(18, 18));
                    LOOKUP_MAP.put("INTEL CORE ULTRA 5 250КF", new CpuSpecs(18, 18));
                    LOOKUP_MAP.put("INTEL CORE ULTRA 7 265K", new CpuSpecs(20, 20));
                    LOOKUP_MAP.put("INTEL CORE ULTRA 7 265KF", new CpuSpecs(20, 20));
                    LOOKUP_MAP.put("INTEL CORE ULTRA 7 270K", new CpuSpecs(20, 20));
                    LOOKUP_MAP.put("INTEL CORE ULTRA 9 285K", new CpuSpecs(24, 24));
                    LOOKUP_MAP.put("INTEL CORE I3-12100F", new CpuSpecs(4, 8));
                    LOOKUP_MAP.put("INTEL CORE I5-12400F", new CpuSpecs(6, 12));
                    LOOKUP_MAP.put("INTEL CORE I5-13400F", new CpuSpecs(10, 16));
                    LOOKUP_MAP.put("INTEL CORE I7-13700F", new CpuSpecs(16, 24));
                    LOOKUP_MAP.put("INTEL CORE I7-13700K", new CpuSpecs(16, 24));
                    LOOKUP_MAP.put("INTEL CORE I9-13900K", new CpuSpecs(24, 32));
                    LOOKUP_MAP.put("INTEL CORE I5-14400", new CpuSpecs(10, 16));
                    LOOKUP_MAP.put("INTEL CORE I5-14400F", new CpuSpecs(10, 16));
                    LOOKUP_MAP.put("INTEL CORE I5-14600K", new CpuSpecs(14, 20));
                    LOOKUP_MAP.put("INTEL CORE I7-14700K", new CpuSpecs(20, 28));
                    LOOKUP_MAP.put("INTEL CORE I7-14700KF", new CpuSpecs(20, 28));
                    LOOKUP_MAP.put("INTEL CORE I9-14900K", new CpuSpecs(24, 32));
                    LOOKUP_MAP.put("INTEL CORE I9-14900KF", new CpuSpecs(24, 32));
                    LOOKUP_MAP.put("AMD RYZEN 5 7500F", new CpuSpecs(6, 12));
                    LOOKUP_MAP.put("AMD RYZEN 5 7500X3D", new CpuSpecs(6, 12));
                    LOOKUP_MAP.put("AMD RYZEN 5 7600", new CpuSpecs(6, 12));
                    LOOKUP_MAP.put("AMD RYZEN 5 7600X", new CpuSpecs(6, 12));
                    LOOKUP_MAP.put("AMD RYZEN 7 7700X", new CpuSpecs(8, 16));
                    LOOKUP_MAP.put("AMD RYZEN 7 7800X3D", new CpuSpecs(8, 16));
                    LOOKUP_MAP.put("AMD RYZEN 9 7900X", new CpuSpecs(12, 24));
                    LOOKUP_MAP.put("AMD RYZEN 9 7950X", new CpuSpecs(16, 32));
                    LOOKUP_MAP.put("AMD RYZEN 9 7950X3D", new CpuSpecs(16, 32));
                    LOOKUP_MAP.put("AMD RYZEN 5 8400F", new CpuSpecs(6, 12));
                    LOOKUP_MAP.put("AMD RYZEN 5 8500G", new CpuSpecs(6, 12));
                    LOOKUP_MAP.put("AMD RYZEN 7 8700F", new CpuSpecs(8, 16));
                    LOOKUP_MAP.put("AMD RYZEN 7 8700G", new CpuSpecs(8, 16));
                    LOOKUP_MAP.put("AMD RYZEN 5 9600X", new CpuSpecs(6, 12));
                    LOOKUP_MAP.put("AMD RYZEN 7 9700X", new CpuSpecs(8, 16));
                    LOOKUP_MAP.put("AMD RYZEN 7 9800X3D", new CpuSpecs(8, 16));
                    LOOKUP_MAP.put("AMD RYZEN 7 9850X3D", new CpuSpecs(8, 16));
                    LOOKUP_MAP.put("AMD RYZEN 9 9900X", new CpuSpecs(12, 24));
                    LOOKUP_MAP.put("AMD RYZEN 9 9900X3D", new CpuSpecs(12, 24));
                    LOOKUP_MAP.put("AMD RYZEN 9 9950X", new CpuSpecs(16, 32));
                    LOOKUP_MAP.put("AMD RYZEN 9 9950X3D", new CpuSpecs(16, 32));
                    LOOKUP_MAP.put("AMD RYZEN 9 9950X3D2", new CpuSpecs(16, 32));
                    LOOKUP_MAP.put("AMD RYZEN 3 1200", new CpuSpecs(4, 4));
                    LOOKUP_MAP.put("AMD RYZEN 3 3200G", new CpuSpecs(4, 4));
                    LOOKUP_MAP.put("AMD RYZEN 5 3600", new CpuSpecs(6, 12));
                    LOOKUP_MAP.put("AMD RYZEN 5 5500", new CpuSpecs(6, 12));
                    LOOKUP_MAP.put("AMD RYZEN 5 5600", new CpuSpecs(6, 12));
                    LOOKUP_MAP.put("AMD RYZEN 5 5600X", new CpuSpecs(6, 12));
                    LOOKUP_MAP.put("AMD RYZEN 7 5700X", new CpuSpecs(8, 16));
                    LOOKUP_MAP.put("AMD RYZEN 7 5800X", new CpuSpecs(8, 16));
                    LOOKUP_MAP.put("AMD RYZEN 7 5800X3D", new CpuSpecs(8, 16));
                    LOOKUP_MAP.put("AMD RYZEN 9 5900X", new CpuSpecs(12, 24));
                }
                CpuSpecs cpuSpecs = LOOKUP_MAP.get(cpu.getModelName().toUpperCase());
                if (cpuSpecs != null) {
                    cpu.setCores(String.valueOf(cpuSpecs.cores));
                    cpu.setThreads(String.valueOf(cpuSpecs.threads));
                } else {
                    System.out.println("MISSING KEY IN MAP: [" + cpu.getModelName().toUpperCase().trim() + "]");
                }
                if (name.contains("AM4")) cpu.setSocket("AM4");
                else if (name.contains("AM5")) cpu.setSocket("AM5");
                else if (name.contains("LGA1700") || name.contains("LGA 1700") || name.toLowerCase().contains("raptor lake")) cpu.setSocket("LGA1700");
                else if (name.contains("LGA1851")) cpu.setSocket("LGA1851");

                if(name.toLowerCase().contains("amd")) cpu.setBrand("AMD");
                else if (name.toLowerCase().contains("intel")) cpu.setBrand("Intel");

                cpuRepository.save(cpu);
            }
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    @Override
    public void scrapeMotherboards() {
        String url = "https://www.neptun.mk/NeptunCategories/LoadProductsForCategory";
        try {


            String jsonPayload = "{"
                    + "\"CategoryId\": 332,"
                    + "\"Sort\": 4,"
                    + "\"Manufacturers\": [],"
                    + "\"Recomended\": false,"
                    + "\"PriceRange\": null,"
                    + "\"ItemsPerPage\": 100,"
                    + "\"CurrentPage\": 1"
                    + "}";

            String jsonResponse = Jsoup.connect(url)
                    .method(Connection.Method.POST) // Must be POST to send a body
                    .ignoreContentType(true)
                    .userAgent("Mozilla/5.0")
                    .header("Content-Type", "application/json") // Tell the server you're sending JSON
                    .header("Accept", "application/json")
                    .header("Referer", "https://www.neptun.mk/")
                    .requestBody(jsonPayload) // This attaches the payload
                    .execute()
                    .body();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(jsonResponse);

            JsonNode products = root.get("Batch").get("Items");


            for (JsonNode node : products) {
                String storeUrl = "https://www.neptun.mk/categories/maticni-ploci/" + node.get("Url").asText();
                double price = node.get("RegularPrice").asDouble();
                Motherboard motherboard = motherboardRepository.findByStoreUrl(storeUrl);
                if(motherboard != null) {
                    if(productPriceDateRepository.findByStoreUrlAndDate(storeUrl, LocalDate.now()) == null) {
                        ProductPriceDate productPriceDate = new ProductPriceDate();
                        productPriceDate.setStoreUrl(storeUrl);
                        productPriceDate.setDate(LocalDate.now());
                        productPriceDate.setPrice(price);
                        productPriceDateRepository.save(productPriceDate);
                    }
                    motherboard.setPriceMkd(price);
                    motherboardRepository.save(motherboard);
                    continue;
                }

                motherboard = new Motherboard();
                motherboard.setModelName(node.get("Title").asText());
                motherboard.setPriceMkd(price);

                motherboard.setStoreUrl("https://www.neptun.mk/categories/maticni-ploci/" + node.get("Url").asText());
                motherboard.setStoreName("Neptun");
                String imgUrl = "https://www.neptun.mk/" + node.path("Thumbnail").asText("");
                motherboard.setImgUrl(imgUrl);
                if(motherboard.getImgUrl() == null || motherboard.getImgUrl().isEmpty()){
                    motherboard.setImgUrl("https://www.anhoch.com/themes/storefront/public/images/image-placeholder.png");
                }
                String name = motherboard.getModelName();
                String[] splitname = name.trim().split(" ");
                StringBuilder newName = new StringBuilder();

                List<String> motherboardBrands = List.of("ASUS", "MSI", "Gigabyte", "ASRock");
                List<String> sockets = List.of("AM4", "AM5", "LGA 1700", "LGA 1200", "LGA 1851");
                List<String> chipsets = List.of("A520", "A620", "B550", "B650", "B840", "B850", "X870", "X870E",
                                "H410", "H610", "H810", "B760", "B840", "B860", "Z790", "Z890");

                for(String motherboardBrand : motherboardBrands){
                    if(name.contains(motherboardBrand)){
                        motherboard.setBrand(motherboardBrand);
                    }
                }
                for(String socket : sockets){
                    if(name.contains(socket)){
                        motherboard.setSocket(socket);
                    }
                }
                for(int i = 1; i < splitname.length; i++) {
                    for (String chipset : chipsets) {
                        if(splitname[i].contains(chipset)){
                            motherboard.setChipset(chipset);
                            if(splitname[i].toLowerCase().contains("m")){
                                motherboard.setFormFactor("mATX");
                            }
                            else if(splitname[i].toLowerCase().contains("e")){
                                motherboard.setFormFactor("EATX");
                            }
                            else if(splitname[i].toLowerCase().contains("i")){
                                motherboard.setFormFactor("ITX");
                            }
                            else{
                                motherboard.setFormFactor("ATX");
                            }
                        }
                    }
                }
                if(name.contains("ATX")){
                    motherboard.setFormFactor("ATX");
                }
                if(name.contains("mATX")){
                    motherboard.setFormFactor("mATX");
                }
                for(String word : splitname){
                    if(word.toLowerCase().contains("mhz")){
                        motherboard.setRamSpeed(word.replace("+", ""));
                    }
                }
                if(name.contains("DDR5")){
                    motherboard.setRamType("DDR5");
                }
                else {
                    motherboard.setRamType("DDR4");
                }
                if(motherboard.getBrand().equals("ASUS") ||
                   motherboard.getBrand().equals("Gigabyte") || motherboard.getBrand().equals("MSI")){
                    for(int i = 1; i < splitname.length; i++) {
                        if (splitname[i].equals("AM4") ||
                                splitname[i].equals("AM5") || splitname[i].equals("ATX") ||
                                splitname[i].equals("LGA1700") || splitname[i].equals("DDR4") ||
                                splitname[i].equals("DDR5")) {
                            break;
                        }
                        newName.append(splitname[i]).append(" ");
                    }
                }

                motherboard.setModelName(newName.toString().replace("GIGABYTE","Gigabyte").trim());

                motherboardRepository.save(motherboard);
            }
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void scrapeGpus() {
        String url = "https://www.neptun.mk/NeptunCategories/LoadProductsForCategory";
        try {


            String jsonPayload = "{"
                    + "\"CategoryId\": 333,"
                    + "\"Sort\": 4,"
                    + "\"Manufacturers\": [],"
                    + "\"Recomended\": false,"
                    + "\"PriceRange\": null,"
                    + "\"ItemsPerPage\": 100,"
                    + "\"CurrentPage\": 1"
                    + "}";

            String jsonResponse = Jsoup.connect(url)
                    .method(Connection.Method.POST) // Must be POST to send a body
                    .ignoreContentType(true)
                    .userAgent("Mozilla/5.0")
                    .header("Content-Type", "application/json") // Tell the server you're sending JSON
                    .header("Accept", "application/json")
                    .header("Referer", "https://www.neptun.mk/")
                    .requestBody(jsonPayload) // This attaches the payload
                    .execute()
                    .body();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(jsonResponse);

            JsonNode products = root.get("Batch").get("Items");


            for (JsonNode node : products) {
                String storeUrl = "https://www.neptun.mk/categories/graficki-karticki/" + node.get("Url").asText();
                double price = node.get("RegularPrice").asDouble();
                Gpu gpu = gpuRepository.findByStoreUrl(storeUrl);
                if(gpu != null) {
                    if(productPriceDateRepository.findByStoreUrlAndDate(storeUrl, LocalDate.now()) == null) {
                        ProductPriceDate productPriceDate = new ProductPriceDate();
                        productPriceDate.setStoreUrl(storeUrl);
                        productPriceDate.setDate(LocalDate.now());
                        productPriceDate.setPrice(price);
                        productPriceDateRepository.save(productPriceDate);
                    }
                    gpu.setPriceMkd(price);
                    gpuRepository.save(gpu);
                    continue;
                }

                gpu = new Gpu();
                gpu.setModelName(node.get("Title").asText());
                gpu.setPriceMkd(price);

                gpu.setStoreUrl("https://www.neptun.mk/categories/graficki-karticki/" + node.get("Url").asText());
                gpu.setStoreName("Neptun");
                String imgUrl = "https://www.neptun.mk/" + node.path("Thumbnail").asText("");
                gpu.setImgUrl(imgUrl);
                if(gpu.getImgUrl() == null || gpu.getImgUrl().isEmpty()){
                    gpu.setImgUrl("https://www.anhoch.com/themes/storefront/public/images/image-placeholder.png");
                }
                String name = gpu.getModelName();
                String[] splitname = name.trim().split(" ");
                StringBuilder newName = new StringBuilder();

                Document doc = Jsoup.connect(gpu.getStoreUrl())
                        .userAgent("Mozilla/5.0")
                        .get();

                String spec = doc.getElementsByAttributeValueContaining("name","description").first().attr("content");
                String[] specs = spec.split("•");

                List<String> gpuBrands = List.of("ASUS", "Gigabyte", "MSI", "Zotac");
                for(String gpuBrand : gpuBrands){
                    if(name.contains(gpuBrand)){
                        gpu.setBrand(gpuBrand);
                    }
                }
                for(String line : specs){
                    if(line.contains("mm")){
                        Matcher matcher = Pattern.compile("\\d+").matcher(line);
                        List<Integer> values = new ArrayList<>();
                        while (matcher.find()) {
                            values.add(Integer.parseInt(matcher.group()));
                        }
                        if (!values.isEmpty()) {
                            gpu.setLength(Collections.max(values));
                        }
                    }
                }
                for(String word : splitname){
                    if(word.toLowerCase().contains("gb")){
                        gpu.setVram(word);
                    }
                }
                if(name.toLowerCase().contains("geforce") || name.toLowerCase().contains("rtx")){
                    gpu.setChipMaker("Nvidia");
                }
                else{
                    gpu.setChipMaker("AMD");
                }
                List<String> nvidiaGpus = List.of("RTX 3050", "RTX 3060", "RTX 3060 Ti", "RTX 3070", "RTX 3070 Ti", "RTX 3080", "RTX 3080 Ti", "RTX 3090", "RTX 3090 Ti",
                        "RTX 4060", "RTX 4060 Ti", "RTX 4070", "RTX 4070 Super", "RTX 4070 Ti", "RTX 4070 Ti Super", "RTX 4080", "RTX 4080 Super", "RTX 4090",
                        "RTX 5050", "RTX 5060", "RTX 5060 Ti", "RTX 5070", "RTX 5070 Ti", "RTX 5080", "RTX 5090");
                List<String> amdGpus = List.of("RX 6500 XT", "RX 6600", "RX 6600 XT", "RX 6650 XT", "RX 6700", "RX 6700 XT", "RX 6750 XT", "RX 6800", "RX 6800 XT", "RX 6900 XT", "RX 6950 XT",
                        "RX 7600", "RX 7600 XT", "RX 7700 XT", "RX 7800 XT", "RX 7900 GRE", "RX 7900 XT", "RX 7900 XTX",
                        "RX 9060", "RX 9060 XT", "RX 9070", "RX 9070 XT");
                if(gpu.getChipMaker().equalsIgnoreCase("Nvidia")){
                    for(String ngpu : nvidiaGpus){
                        if(name.contains(ngpu)){
                            gpu.setChipset(ngpu);
                        }
                    }
                }
                if(gpu.getChipMaker().equalsIgnoreCase("AMD")){
                    for(String agpu : amdGpus){
                        if(name.contains(agpu)){
                            gpu.setChipset(agpu);
                        }
                    }
                }
                if(gpu.getBrand().equals("Gigabyte")){
                    if(gpu.getChipMaker().equals("Nvidia")){
                        boolean add = false;
                        newName.append("GeForce").append(" ");
                        for(String word : splitname){
                            if(word.equalsIgnoreCase("rtx")){
                                add = true;
                            }
                            if(word.endsWith("GB")){
                                add = false;
                            }
                            if(add){
                                newName.append(word).append(" ");
                            }
                        }
                        gpu.setModelName(newName.toString().replace("3 Fan", "").replace("  ", " ")
                                .replace("OC MAX", "MAX OC").replace("WindForce", "WINDFORCE").replace("Windforce","WINDFORCE").replace("Gaming","GAMING").trim());
                    }
                    else{
                        boolean add = false;
                        for(String word : splitname){
                            if(word.equalsIgnoreCase("radeon")){
                                add = true;
                            }
                            if(word.endsWith("GB")){
                                add = false;
                            }
                            if(add){
                                newName.append(word).append(" ");
                            }
                        }
                        gpu.setModelName(newName.toString().replace("3 Fan", "").replace("  ", " ").trim());
                        if(name.contains("Gaming OC")){
                            gpu.setModelName(newName.append("GAMING OC").toString().replace("3 Fan", "").replace("  ", " ").trim());
                        }
                    }
                }
                if(gpu.getBrand().equals("MSI")){
                    if(gpu.getChipMaker().equals("Nvidia")){
                        boolean add = false;
                        newName.append("GeForce").append(" ");
                        for(String word : splitname){
                            if(word.equalsIgnoreCase("rtx")){
                                add = true;
                            }
                            if(word.endsWith("GB")){
                                add = false;
                            }
                            if(add){
                                newName.append(word).append(" ");
                            }
                        }
                        gpu.setModelName(newName.toString().replace("3 Fan", "").replace("  ", " ").replace("Ventus", "VENTUS").trim());
                    }
                }
                if(gpu.getBrand().equals("Zotac")){
                    if(gpu.getChipMaker().equals("Nvidia")){
                        boolean add = false;
                        newName.append("GeForce").append(" ");
                        for(String word : splitname){
                            if(word.equalsIgnoreCase("rtx")){
                                add = true;
                            }
                            if(word.endsWith("GB")){
                                add = false;
                            }
                            if(add){
                                newName.append(word).append(" ");
                            }
                        }
                        gpu.setModelName(newName.toString().replace("3 Fan", "").replace("  ", " ").trim());
                        if(name.contains("Solid OC")){
                            gpu.setModelName(newName.append("SOLID OC").toString().replace("3 Fan", "").replace("  ", " ").trim());
                        }
                    }
                }
                if(gpu.getBrand().equals("ASUS")){
                    if(gpu.getChipMaker().equals("Nvidia")){
                        boolean add = false;
                        if(name.toLowerCase().contains("dual")){
                            newName.append("Dual").append(" ");
                        }
                        if(name.toLowerCase().contains("prime")){
                            newName.append("PRIME").append(" ");
                        }
                        for(String word : splitname){
                            if(word.equalsIgnoreCase("geforce")){
                                add = true;
                            }
                            if(word.endsWith("GB")){
                                add = false;
                            }
                            if(add){
                                newName.append(word).append(" ");
                            }
                        }
                        gpu.setModelName(newName.toString().replace("Edition", "").replace("3 Fan", "").replace("  ", " ").trim());
                    }
                    if(gpu.getChipMaker().equals("AMD")){
                        boolean add = false;
                        if(name.toLowerCase().contains("dual")){
                            newName.append("Dual").append(" ");
                        }
                        if(name.toLowerCase().contains("prime")){
                            newName.append("PRIME").append(" ");
                        }
                        for(String word : splitname){
                            if(word.equalsIgnoreCase("radeon")){
                                add = true;
                            }
                            if(word.endsWith("GB")){
                                add = false;
                            }
                            if(add){
                                newName.append(word).append(" ");
                            }
                        }
                        gpu.setModelName(newName.toString().replace("Edition", "").replace("3 Fan", "").replace("  ", " ").trim());
                    }
                }
                gpuRepository.save(gpu);
            }
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void scrapeRams() {
        String url = "https://www.neptun.mk/NeptunCategories/LoadProductsForCategory";
        try {
            String jsonPayload = "{"
                    + "\"CategoryId\": 341,"
                    + "\"Sort\": 4,"
                    + "\"Manufacturers\": [],"
                    + "\"Recomended\": false,"
                    + "\"PriceRange\": null,"
                    + "\"ItemsPerPage\": 100,"
                    + "\"CurrentPage\": 1"
                    + "}";

            String jsonResponse = Jsoup.connect(url)
                    .method(Connection.Method.POST)
                    .ignoreContentType(true)
                    .userAgent("Mozilla/5.0")
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .header("Referer", "https://www.neptun.mk/")
                    .requestBody(jsonPayload)
                    .execute()
                    .body();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(jsonResponse);

            JsonNode products = root.get("Batch").get("Items");


            for (JsonNode node : products) {
                String storeUrl = "https://www.neptun.mk/categories/dimm-desktop-memorii/" + node.get("Url").asText();
                double price = node.get("RegularPrice").asDouble();
                Ram ram = ramRepository.findByStoreUrl(storeUrl);
                if(ram != null) {
                    if(productPriceDateRepository.findByStoreUrlAndDate(storeUrl, LocalDate.now()) == null) {
                        ProductPriceDate productPriceDate = new ProductPriceDate();
                        productPriceDate.setStoreUrl(storeUrl);
                        productPriceDate.setDate(LocalDate.now());
                        productPriceDate.setPrice(price);
                        productPriceDateRepository.save(productPriceDate);
                    }
                    ram.setPriceMkd(price);
                    ramRepository.save(ram);
                    continue;
                }

                ram = new Ram();
                ram.setModelName(node.get("Title").asText().substring(9).trim());
                ram.setPriceMkd(price);

                ram.setStoreUrl("https://www.neptun.mk/categories/dimm-desktop-memorii/" + node.get("Url").asText());
                ram.setStoreName("Neptun");
                String imgUrl = "https://www.neptun.mk/" + node.path("Thumbnail").asText("");
                ram.setImgUrl(imgUrl);
                if (ram.getImgUrl() == null || ram.getImgUrl().isEmpty()) {
                    ram.setImgUrl("https://www.anhoch.com/themes/storefront/public/images/image-placeholder.png");
                }
                String name = ram.getModelName();
                String[] splitname = name.trim().split(" ");
                String newname= "";
                String ramsize = "";

                if(name.contains("Kingston")){
                    ram.setBrand("Kingston");
                }
                if(name.contains("DDR4")){
                    ram.setRamType("DDR4");
                }
                else if(name.contains("DDR5")){
                    ram.setRamType("DDR5");
                }
                for(int i = 1; i<splitname.length; i++){
                    if(splitname[i].toLowerCase().endsWith("mhz")){
                        ram.setRamSpeed(splitname[i]);
                    }
                    if(splitname[i].toLowerCase().startsWith("cl")){
                        ram.setLatency(splitname[i]);
                    }
                    if(splitname[i].toLowerCase().endsWith("gb") && !splitname[i].equals("RGB")){
                        ramsize = splitname[i];
                        ram.setRamSize(ramsize);
                    }
                    if(splitname[i].equalsIgnoreCase("fury")){
                        newname = splitname[i] + " " + splitname[i+1];
                    }
                }
                int size = Integer.parseInt(ramsize.replace("GB",""));
                if(name.toLowerCase().contains("kit")){
                    ram.setConfiguration("Kit of 2x" + size/2 + "GB");
                }
                else{
                    ram.setConfiguration("Single Stick");
                }
                if(!newname.isEmpty()){
                    ram.setModelName(newname);
                }

                ramRepository.save(ram);
            }
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void scrapeDisks() {
        String url = "https://www.neptun.mk/NeptunCategories/LoadProductsForCategory";
        try {
            String jsonPayload = "{"
                    + "\"CategoryId\": 340,"
                    + "\"Sort\": 4,"
                    + "\"Manufacturers\": [],"
                    + "\"Recomended\": false,"
                    + "\"PriceRange\": null,"
                    + "\"ItemsPerPage\": 100,"
                    + "\"CurrentPage\": 1"
                    + "}";

            String jsonResponse = Jsoup.connect(url)
                    .method(Connection.Method.POST)
                    .ignoreContentType(true)
                    .userAgent("Mozilla/5.0")
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .header("Referer", "https://www.neptun.mk/")
                    .requestBody(jsonPayload)
                    .execute()
                    .body();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(jsonResponse);

            JsonNode products = root.get("Batch").get("Items");


            for (JsonNode node : products) {
                String storeUrl = "https://www.neptun.mk/categories/ssd-diskovi/" + node.get("Url").asText();
                double price = node.get("RegularPrice").asDouble();
                Disk disk = diskRepository.findByStoreUrl(storeUrl);
                if(disk != null) {
                    if(productPriceDateRepository.findByStoreUrlAndDate(storeUrl, LocalDate.now()) == null) {
                        ProductPriceDate productPriceDate = new ProductPriceDate();
                        productPriceDate.setStoreUrl(storeUrl);
                        productPriceDate.setDate(LocalDate.now());
                        productPriceDate.setPrice(price);
                        productPriceDateRepository.save(productPriceDate);
                    }
                    disk.setPriceMkd(price);
                    diskRepository.save(disk);
                    continue;
                }

                disk = new Disk();
                disk.setModelName(node.get("Title").asText().trim());
                if(disk.getModelName().contains("Samsung T9")){
                    continue;
                }
                disk.setPriceMkd(price);

                disk.setStoreUrl("https://www.neptun.mk/categories/ssd-diskovi/" + node.get("Url").asText());
                disk.setStoreName("Neptun");
                String imgUrl = "https://www.neptun.mk/" + node.path("Thumbnail").asText("");
                disk.setImgUrl(imgUrl);
                if (disk.getImgUrl() == null || disk.getImgUrl().isEmpty()) {
                    disk.setImgUrl("https://www.anhoch.com/themes/storefront/public/images/image-placeholder.png");
                }
                String name = disk.getModelName();
                String[] splitname = name.trim().split(" ");
                List<String> diskbrands = List.of("Kingston", "Samsung", "Western");
                StringBuilder newname = new StringBuilder();

                for(String brand : diskbrands){
                    if(name.contains(brand)){
                        disk.setBrand(brand);
                        if(brand.equals("Western")){
                            disk.setBrand("Western Digital");
                        }
                    }
                }

                boolean add = false;
                for(String word : splitname){
                    if(word.toLowerCase().endsWith("tb") || word.toLowerCase().endsWith("gb")){
                        disk.setSize(word);
                    }
                    if(disk.getBrand().equals("Kingston") || disk.getBrand().equals("Samsung") || disk.getBrand().equals("Western Digital")){
                        if(word.equals("Kingston") || word.equals("Samsung") || word.equals("Western") || word.equals("WesternDigital")){
                            add = true;
                        }
                        if(word.equals("2.5\"") || word.equals("NVMe")){
                            add = false;
                        }
                        if(add){
                            newname.append(word).append(" ");
                        }
                    }
                }
                disk.setModelName(newname.toString().replace("WesternDigital","Western Digital").trim());

                if(name.contains("SSD")){
                    disk.setDiskType("SSD");
                }
                if(name.contains("SATA")){
                    disk.setSpeed("SATA3");
                    disk.setConnector("SATA");
                }
                if(name.contains("NVMe")){
                    disk.setSpeed("NVMe");
                    disk.setConnector("M.2");
                }
                if(price > 12000){
                    System.out.println("hello");
                }
                if(!disk.getDiskType().isEmpty()){
                    diskRepository.save(disk);
                }
            }
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void scrapePsus() {
        String url = "https://www.neptun.mk/NeptunCategories/LoadProductsForCategory";
        try {
            String jsonPayload = "{"
                    + "\"CategoryId\": 337,"
                    + "\"Sort\": 4,"
                    + "\"Manufacturers\": [],"
                    + "\"Recomended\": false,"
                    + "\"PriceRange\": null,"
                    + "\"ItemsPerPage\": 100,"
                    + "\"CurrentPage\": 1"
                    + "}";

            String jsonResponse = Jsoup.connect(url)
                    .method(Connection.Method.POST)
                    .ignoreContentType(true)
                    .userAgent("Mozilla/5.0")
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .header("Referer", "https://www.neptun.mk/")
                    .requestBody(jsonPayload)
                    .execute()
                    .body();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(jsonResponse);

            JsonNode products = root.get("Batch").get("Items");


            for (JsonNode node : products) {
                String storeUrl = "https://www.neptun.mk/categories/napojuvanja/" + node.get("Url").asText();
                double price = node.get("RegularPrice").asDouble();
                Psu psu = psuRepository.findByStoreUrl(storeUrl);
                if(psu != null) {
                    if(productPriceDateRepository.findByStoreUrlAndDate(storeUrl, LocalDate.now()) == null) {
                        ProductPriceDate productPriceDate = new ProductPriceDate();
                        productPriceDate.setStoreUrl(storeUrl);
                        productPriceDate.setDate(LocalDate.now());
                        productPriceDate.setPrice(price);
                        productPriceDateRepository.save(productPriceDate);
                    }
                    psu.setPriceMkd(price);
                    psuRepository.save(psu);
                    continue;
                }

                psu = new Psu();
                psu.setModelName(node.get("Title").asText().trim());
                psu.setPriceMkd(price);

                psu.setStoreUrl("https://www.neptun.mk/categories/napojuvanja/" + node.get("Url").asText());
                psu.setStoreName("Neptun");
                String imgUrl = "https://www.neptun.mk/" + node.path("Thumbnail").asText("");
                psu.setImgUrl(imgUrl);
                if (psu.getImgUrl() == null || psu.getImgUrl().isEmpty()) {
                    psu.setImgUrl("https://www.anhoch.com/themes/storefront/public/images/image-placeholder.png");
                }
                String name = psu.getModelName();
                String[] splitname = name.trim().split(" ");
                List<String> brands = Arrays.asList("Be Quiet!", "Corsair", "MSI", "NZXT", "Rampage", "White Shark","ASUS","Cougar");
                List<String> ratings = Arrays.asList("80 plus", "bronze", "silver", "gold", "platinum", "titanium");
                StringBuilder newname = new StringBuilder();
                for(String brand : brands){
                    if(name.contains(brand)){
                        psu.setBrand(brand);
                    }
                }
                if(name.contains("Fully Modular") || name.contains("RM750x") || name.contains("RM1000e")){
                    psu.setModular("Modular");
                }
                else{
                    psu.setModular("Non Modular");
                }
                for(String word : splitname){
                    if(word.endsWith("W")){
                        psu.setWattage(word);
                        break;
                    }
                    newname.append(word).append(" ");
                }
                psu.setModelName(newname.toString().replace("PSU","").trim());
                if(name.toLowerCase().contains("80 plus")){
                    psu.setEfficiency("80+");
                }
                if(name.toLowerCase().contains("bronze")){
                    psu.setEfficiency("80+ Bronze");
                }
                if(name.toLowerCase().contains("silver")){
                    psu.setEfficiency("80+ Silver");
                }
                if(name.toLowerCase().contains("gold")){
                    psu.setEfficiency("80+ Gold");
                }
                if(name.toLowerCase().contains("platinum")){
                    psu.setEfficiency("80+ Platinum");
                }
                if(name.toLowerCase().contains("titanium")){
                    psu.setEfficiency("80+ Titanium");
                }
                psuRepository.save(psu);
            }
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void scrapeCases() {
        String url = "https://www.neptun.mk/NeptunCategories/LoadProductsForCategory";
        try {


            String jsonPayload = "{"
                    + "\"CategoryId\": 336,"
                    + "\"Sort\": 4,"
                    + "\"Manufacturers\": [],"
                    + "\"Recomended\": false,"
                    + "\"PriceRange\": null,"
                    + "\"ItemsPerPage\": 100,"
                    + "\"CurrentPage\": 1"
                    + "}";

            String jsonResponse = Jsoup.connect(url)
                    .method(Connection.Method.POST) // Must be POST to send a body
                    .ignoreContentType(true)
                    .userAgent("Mozilla/5.0")
                    .header("Content-Type", "application/json") // Tell the server you're sending JSON
                    .header("Accept", "application/json")
                    .header("Referer", "https://www.neptun.mk/")
                    .requestBody(jsonPayload) // This attaches the payload
                    .execute()
                    .body();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(jsonResponse);

            JsonNode products = root.get("Batch").get("Items");


            for (JsonNode node : products) {
                String storeUrl = "https://www.neptun.mk/categories/kukjishta/" + node.get("Url").asText();
                double price = node.get("RegularPrice").asDouble();
                PcCase kase = caseRepository.findByStoreUrl(storeUrl);
                if(kase != null) {
                    if(productPriceDateRepository.findByStoreUrlAndDate(storeUrl, LocalDate.now()) == null) {
                        ProductPriceDate productPriceDate = new ProductPriceDate();
                        productPriceDate.setStoreUrl(storeUrl);
                        productPriceDate.setDate(LocalDate.now());
                        productPriceDate.setPrice(price);
                        productPriceDateRepository.save(productPriceDate);
                    }
                    kase.setPriceMkd(price);
                    caseRepository.save(kase);
                    continue;
                }

                kase = new PcCase();
                kase.setModelName(node.get("Title").asText());
                kase.setPriceMkd(price);

                kase.setStoreUrl("https://www.neptun.mk/categories/kukjishta/" + node.get("Url").asText());
                kase.setStoreName("Neptun");
                String imgUrl = "https://www.neptun.mk/" + node.path("Thumbnail").asText("");
                kase.setImgUrl(imgUrl);
                if (kase.getImgUrl() == null || kase.getImgUrl().isEmpty()) {
                    kase.setImgUrl("https://www.anhoch.com/themes/storefront/public/images/image-placeholder.png");
                }
                String name = kase.getModelName();
                String[] splitname = name.trim().split(" ");
                StringBuilder newName = new StringBuilder();

                Document doc = Jsoup.connect(kase.getStoreUrl())
                        .userAgent("Mozilla/5.0")
                        .get();

                String spec = doc.getElementsByAttributeValueContaining("name", "description").first().attr("content");
                String[] specs = spec.split("•");

                List<String> brands = new ArrayList<>(Arrays.asList(
                        "Be Quiet!",
                        "Corsair",
                        "Cougar",
                        "ENDORFY",
                        "NZXT",
                        "Raidmax",
                        "Rampage",
                        "White Shark",
                        "Zeiron"
                ));
                for(String brand : brands){
                    if(name.contains(brand)){
                        kase.setBrand(brand);
                    }
                }
                kase.setFormFactor("ATX");
                if(name.contains("M-ATX")){
                    kase.setFormFactor("mATX");
                }
                if(name.contains("E-ATX")){
                    kase.setFormFactor("EATX");
                }
                for(int i = 2;i<splitname.length;i++){
                    if(splitname[i].equals("Tempered")){
                        break;
                    }
                    newName.append(splitname[i]).append(" ");
                }
                kase.setCoolerHeight("N/A");
                kase.setGpuLength("N/A");
                for(String line : specs){
                    if(line.toLowerCase().contains("cpu")){
                        Pattern pattern = Pattern.compile("\\d+");
                        Matcher matcher = pattern.matcher(line);
                        if (matcher.find()) {
                           kase.setCoolerHeight(matcher.group());
                        }
                    }
                    if(line.toLowerCase().contains("gpu") ||
                       line.toLowerCase().contains("graphics card") ||
                       line.toLowerCase().contains("vga")){
                        Pattern pattern = Pattern.compile("\\d+");
                        Matcher matcher = pattern.matcher(line);
                        if (matcher.find()) {
                            kase.setGpuLength(matcher.group());
                        }
                    }
                }
                kase.setModelName(newName.toString().trim());
                caseRepository.save(kase);
            }
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void scrapeCoolers() {
        String url = "https://www.neptun.mk/NeptunCategories/LoadProductsForCategory";
        try {


            String jsonPayload = "{"
                    + "\"CategoryId\": 374,"
                    + "\"Sort\": 4,"
                    + "\"Manufacturers\": [],"
                    + "\"Recomended\": false,"
                    + "\"PriceRange\": null,"
                    + "\"ItemsPerPage\": 100,"
                    + "\"CurrentPage\": 1"
                    + "}";

            String jsonResponse = Jsoup.connect(url)
                    .method(Connection.Method.POST) // Must be POST to send a body
                    .ignoreContentType(true)
                    .userAgent("Mozilla/5.0")
                    .header("Content-Type", "application/json") // Tell the server you're sending JSON
                    .header("Accept", "application/json")
                    .header("Referer", "https://www.neptun.mk/")
                    .requestBody(jsonPayload) // This attaches the payload
                    .execute()
                    .body();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(jsonResponse);

            JsonNode products = root.get("Batch").get("Items");


            for (JsonNode node : products) {
                String storeUrl = "https://www.neptun.mk/categories/cpu-cooler/" + node.get("Url").asText();
                double price = node.get("RegularPrice").asDouble();
                Cooler cooler = coolerRepository.findByStoreUrl(storeUrl);
                if(cooler != null) {
                    if(productPriceDateRepository.findByStoreUrlAndDate(storeUrl, LocalDate.now()) == null) {
                        ProductPriceDate productPriceDate = new ProductPriceDate();
                        productPriceDate.setStoreUrl(storeUrl);
                        productPriceDate.setDate(LocalDate.now());
                        productPriceDate.setPrice(price);
                        productPriceDateRepository.save(productPriceDate);
                    }
                    cooler.setPriceMkd(price);
                    coolerRepository.save(cooler);
                    continue;
                }

                cooler = new Cooler();
                cooler.setModelName(node.get("Title").asText());
                cooler.setPriceMkd(price);

                cooler.setStoreUrl("https://www.neptun.mk/categories/cpu-cooler/" + node.get("Url").asText());
                cooler.setStoreName("Neptun");
                String imgUrl = "https://www.neptun.mk/" + node.path("Thumbnail").asText("");
                cooler.setImgUrl(imgUrl);
                if (cooler.getImgUrl() == null || cooler.getImgUrl().isEmpty()) {
                    cooler.setImgUrl("https://www.anhoch.com/themes/storefront/public/images/image-placeholder.png");
                }
                String name = cooler.getModelName();
                String[] splitname = name.trim().split(" ");
                StringBuilder newName = new StringBuilder();

                List<String> brands = new ArrayList<>(Arrays.asList(
                        "Be Quiet!",
                        "Arctic",
                        "ARCTIC",
                        "Cougar",
                        "ENDORFY",
                        "NZXT",
                        "Raidmax",
                        "Rampage",
                        "Spartan",
                        "Zeiron"
                ));

                Document doc = Jsoup.connect(cooler.getStoreUrl())
                        .userAgent("Mozilla/5.0")
                        .get();
                String spec = doc.select("product-details").attr("data-productModel");

                spec = spec.split("ShortDescriptionAl")[0];
                String[] specs = spec.split("•");

                String shortspec = doc.getElementsByAttributeValueContaining("name", "description").first().attr("content");
                String[] shortspecs = shortspec.split("•");

                if(price>3500){
                    System.out.println("hello");
                }
                List<String> intelSockets = List.of("1851", "1700", "1200", "1151", "1150", "1155", "115X");
                List<String> amdSockets = List.of("AM4","AM5");
                Set<String> supportedSockets = new HashSet<>();
                for(String brand : brands){
                    if(name.contains(brand)){
                        cooler.setBrand(brand);
                        if(brand.equals("ARCTIC")){
                            cooler.setBrand("Arctic");
                        }
                        if(brand.equals("Spartan")){
                            cooler.setBrand("ENDORFY");
                        }
                    }
                }
                if(splitname[1].equals("Cooler")){
                    cooler.setCoolerType("Air Cooler");
                    cooler.setRadiatorSize("N/A");
                }
                if(splitname[1].equals("AIO")){
                    cooler.setCoolerType("AIO");
                    cooler.setCoolerHeight("N/A");
                }
                for(String intelSocket : intelSockets){
                    if(spec.contains(intelSocket) || shortspec.contains(intelSocket) || name.contains(intelSocket)){
                        supportedSockets.add("LGA " + intelSocket);
                    }
                }
                for(String amdSocket : amdSockets){
                    if(spec.contains(amdSocket) || shortspec.contains(amdSocket) || name.contains(amdSocket)){
                        supportedSockets.add("AMD " + amdSocket);
                    }
                }
                List<Integer> list = new ArrayList<>();
                for(String line : specs){
                    if((line.toLowerCase().contains("dimensions") ||
                            line.toLowerCase().contains("height")) && line.contains("mm")) {
                        if(cooler.getCoolerType().equals("Air Cooler")){
                            Pattern pattern = Pattern.compile("\\d+");
                            Matcher matcher = pattern.matcher(line);
                            while (matcher.find()) {
                                int number = Integer.parseInt(matcher.group());
                                if(number < 180) list.add(Integer.parseInt(matcher.group()));                            }
                        }}
                }
                for(String line : shortspecs){
                    if((line.toLowerCase().contains("dimensions") ||
                            line.toLowerCase().contains("height")) && line.contains("mm")) {
                        if(cooler.getCoolerType().equals("Air Cooler")){
                            Pattern pattern = Pattern.compile("\\d+");
                            Matcher matcher = pattern.matcher(line);
                            while (matcher.find()) {
                                int number = Integer.parseInt(matcher.group());
                                if(number < 180) list.add(Integer.parseInt(matcher.group()));
                            }
                        }}
                }
                List<String> radSizes = List.of("120", "140", "240", "280", "360", "420");
                if(cooler.getCoolerType().equals("AIO")){
                    for(String radSize : radSizes){
                        if(name.contains(radSize)){
                            cooler.setRadiatorSize(radSize);
                        }
                    }
                }

                if(splitname[1].equals("Cooler")){
                    if(!list.isEmpty()){
                        cooler.setCoolerHeight(String.valueOf(Collections.max(list)));
                    }
                    else{
                        cooler.setCoolerHeight("N/A");
                    }
                }
                cooler.setSockets(String.join(", ", supportedSockets).replace("LGA 115X","LGA 1151, LGA 1150, LGA 1155"));

                if(cooler.getBrand().equals("ENDORFY")){
                    newName.append("ENDORFY").append(" ");
                    for(int i = 2;i <splitname.length;i++){
                        if(splitname[i].startsWith("w/")){
                            break;
                        }
                    newName.append(splitname[i]).append(" ");
                    }
                    cooler.setModelName(newName.toString().replace("ENDORFY ENDORFY","ENDORFY").trim());
                }
                if(cooler.getBrand().equals("Rampage")){
                    if(cooler.getCoolerType().equals("Air Cooler")){
                        for(int i = 2;i <splitname.length;i++){
                            if(splitname[i].startsWith("w/")){
                                break;
                            }
                            newName.append(splitname[i]).append(" ");
                        }
                        cooler.setModelName(newName.toString().replace("Temperature Sensor","").replace("Temperature Censor","").replace("  ", " ").trim());
                    }
                    else{
                        for(int i = 3; i <splitname.length; i++){
                            if(splitname[i].startsWith("All")){
                                break;
                            }
                            newName.append(splitname[i]).append(" ");
                        }
                        cooler.setModelName(newName.toString().replace("360mm","").replace("  "," ").replace("W/Temperature Display","").trim());
                    }
                }
                if(cooler.getBrand().equals("Arctic")){
                    if(cooler.getCoolerType().equals("Air Cooler")){
                        for(int i = 2;i <splitname.length;i++){
                            if(splitname[i].startsWith("LGA") || splitname[i].equals("All")){
                                break;
                            }
                            newName.append(splitname[i]).append(" ");
                        }
                        cooler.setModelName(newName.toString().replace("2x120mm Fans","").replace("  ", " ").trim());
                    }
                    else{
                        for(int i = 3; i <splitname.length; i++){
                            if(splitname[i].startsWith("All") || splitname[i].startsWith("LGA")){
                                break;
                            }
                            newName.append(splitname[i]).append(" ");
                        }
                        cooler.setModelName(newName.toString().replace("360mm","").replace("240","").replace("  "," ").trim());

                    }
                }
                if(cooler.getBrand().equals("Cougar")){
                    if(cooler.getCoolerType().equals("Air Cooler")){
                        for(int i = 2;i <splitname.length;i++){
                            if(splitname[i].startsWith("w/") || splitname[i].equals("All")){
                                break;
                            }
                            newName.append(splitname[i]).append(" ");
                        }
                        cooler.setModelName(newName.toString().replace("  ", " ").trim());
                    }
                    else{
                        for(int i = 3; i <splitname.length; i++){
                            if(splitname[i].endsWith("mm") || splitname[i].startsWith("LGA")){
                                break;
                            }
                            newName.append(splitname[i]).append(" ");
                        }
                        cooler.setModelName(newName.toString().replace("  "," ").trim());

                    }
                }
                if(cooler.getBrand().equals("Be Quiet!")){
                    if(cooler.getCoolerType().equals("Air Cooler")){
                        for(int i = 2;i <splitname.length;i++){
                            if(splitname[i].startsWith("w/")){
                                break;
                            }
                            newName.append(splitname[i]).append(" ");
                        }
                        cooler.setModelName(newName.toString().replace("  ", " ").trim());
                    }
                    else{
                        for(int i = 3; i <splitname.length; i++){
                            if(splitname[i].equals("All")){
                                break;
                            }
                            newName.append(splitname[i]).append(" ");
                        }
                        cooler.setModelName(newName.toString().replace("360mm","").replace("A-RGB","ARGB").replace("  "," ").trim());
                    }
                }
                if(cooler.getBrand().equals("NZXT")){
                    if(cooler.getCoolerType().equals("Air Cooler")){
                        for(int i = 2;i <splitname.length;i++){
                            if(splitname[i].startsWith("LGA")){
                                break;
                            }
                            newName.append(splitname[i]).append(" ");
                        }
                        cooler.setModelName(newName.toString().replace("  ", " ").trim());
                    }
                    else{
                        for(int i = 3; i <splitname.length; i++){
                            if(splitname[i].startsWith("All") || splitname[i].startsWith("LGA")){
                                break;
                            }
                            newName.append(splitname[i]).append(" ");
                        }
                        cooler.setModelName(newName.toString().replace("  "," ").trim());
                    }
                }
                coolerRepository.save(cooler);
            }
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
