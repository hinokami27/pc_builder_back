package com.example.pcbuilder.service.serviceImpl;

import com.example.pcbuilder.model.*;
import com.example.pcbuilder.repository.*;
import com.example.pcbuilder.service.AnhochScraper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


@Service
public class AnhochScraperImpl implements AnhochScraper {

    private final CpuRepository cpuRepository;
    private final MotherboardRepository motherboardRepository;
    private final GpuRepository gpuRepository;
    private final RamRepository ramRepository;
    private final DiskRepository diskRepository;
    private final PsuRepository psuRepository;
    private final CaseRepository caseRepository;
    private final CoolerRepository coolerRepository;
    private final ProductPriceDateRepository productPriceDateRepository;

    public AnhochScraperImpl(CpuRepository cpuRepository, MotherboardRepository motherboardRepository, GpuRepository gpuRepository, RamRepository ramRepository, DiskRepository diskRepository, PsuRepository psuRepository, CaseRepository caseRepository, CoolerRepository coolerRepository, ProductPriceDateRepository productPriceDateRepository) {
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
    @Transactional
    public void scrapeCpus() {
        String url = "https://www.anhoch.com/products?query=&brand[0]=29&brand[1]=28&categories[0]=procesori&tag=&fromPrice=0&toPrice=349980&inStockOnly=1&sort=priceLowToHigh&perPage=130&page=1\n";
        try {
            String jsonResponse = Jsoup.connect(url)
                    .ignoreContentType(true)
                    .userAgent("Mozilla/5.0")
                    .header("Accept", "application/json")
                    .execute()
                    .body();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(jsonResponse);

            JsonNode products = root.get("products").get("data");

            for (JsonNode node : products) {
                String storeUrl = "https://www.anhoch.com/products/" + node.get("slug").asText();
                double price = node.get("price")
                        .get("inCurrentCurrency")
                        .get("amount").asDouble();
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
                cpu.setModelName(node.get("name").asText());
                cpu.setPriceMkd(price);
                cpu.setStoreUrl("https://www.anhoch.com/products/" + node.get("slug").asText());
                cpu.setStoreName("Anhoch");
                String imgUrl = node.path("base_image").path("path").asText("");
                cpu.setImgUrl(imgUrl);
                if(cpu.getImgUrl() == null || cpu.getImgUrl().isEmpty()){
                    cpu.setImgUrl("https://www.anhoch.com/themes/storefront/public/images/image-placeholder.png");
                }

                String name = cpu.getModelName();

                if(name.toLowerCase().contains("amd")) {
                   String[] splitname = name.split(" ");
                   String packageType = "";
                   String newName = splitname[1] + " " + splitname[2] + " " + splitname[3] + " " + splitname[4];
                   if(name.toLowerCase().contains("box")) { packageType = "BOX"; }
                   if(name.toLowerCase().contains("tray")) { packageType = "TRAY"; }
                   cpu.setModelName(newName.trim());
                   cpu.setPackageType(packageType);
                }
                if(name.toLowerCase().contains("intel")) {
                    if(name.toLowerCase().contains("ultra")) {
                        String[] splitname = name.split(" ");
                        String packageType = "TRAY";
                        String newName = splitname[1] + " " + splitname[2] + " " + splitname[3] + " " + splitname[4] + " " + splitname[5];
                        if(name.toLowerCase().contains("box")) { packageType = "BOX"; }
                        cpu.setModelName(newName.trim());
                        cpu.setPackageType(packageType);
                    }
                    else{
                        String[] splitname = name.split(" ");
                        String packageType = "BOX";
                        String newName = splitname[1] + " " + splitname[2] + " " + splitname[3];
                        if(name.toLowerCase().contains("tray")) {
                            packageType = "TRAY";
                        }
                        cpu.setModelName(newName.trim());
                        cpu.setPackageType(packageType);
                    }

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
                }
                if (name.contains("AM4")) cpu.setSocket("AM4");
                else if (name.contains("AM5")) cpu.setSocket("AM5");
                else if (name.contains("LGA1700") || name.contains("LGA 1700") || name.toLowerCase().contains("raptor lake")) cpu.setSocket("LGA1700");
                else if (name.contains("LGA1851")) cpu.setSocket("LGA1851");

                if(name.toLowerCase().contains("amd")) cpu.setBrand("AMD");
                else if (name.toLowerCase().contains("intel")) cpu.setBrand("Intel");


                cpuRepository.save(cpu);
            }
        } catch (IOException e) {
            System.out.println("no jason");
        }
    }

    @Override
    public void scrapeMotherboards() {
        String url = "https://www.anhoch.com/products?query=&categories[0]=matichni-plochi&tag=&fromPrice=0&toPrice=349980&inStockOnly=1&sort=priceLowToHigh&perPage=130&page=1";
        try{
            String jsonResponse = Jsoup.connect(url)
                    .ignoreContentType(true)
                    .userAgent("Mozilla/5.0")
                    .header("Accept", "application/json")
                    .execute()
                    .body();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(jsonResponse);

            JsonNode products = root.get("products").get("data");
            for (JsonNode node : products) {
                String storeUrl = "https://www.anhoch.com/products/" + node.get("slug").asText();
                double price = node.get("price")
                        .get("inCurrentCurrency")
                        .get("amount").asDouble();
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
                motherboard.setPriceMkd(price);
                motherboard.setModelName(node.get("name").asText());

                motherboard.setStoreUrl("https://www.anhoch.com/products/" + node.get("slug").asText());
                motherboard.setStoreName("Anhoch");
                String imgUrl = node.path("base_image").path("path").asText("");
                motherboard.setImgUrl(imgUrl);
                if(motherboard.getImgUrl() == null || motherboard.getImgUrl().isEmpty()){
                    motherboard.setImgUrl("https://www.anhoch.com/themes/storefront/public/images/image-placeholder.png");
                }

                String name = motherboard.getModelName();
                String[] splitname = name.split(" ");
                StringBuilder newName = new StringBuilder();

                List<String> motherboardBrands = List.of("ASUS", "MSI", "Gigabyte", "ASRock", "Biostar");
                List<String> sockets = List.of("AM4", "AM5", "LGA 1700", "LGA 1200", "LGA 1851");
                List<String> chipsets =
                        List.of("A520", "A620", "B550", "B650", "B840", "B850", "X870", "X870E",
                                "H410", "H610", "H810", "B760", "B840", "B860", "Z790", "Z890");

                boolean add = true;
                for(int i = 1; i < splitname.length; i++) {
                        if(splitname[i].toLowerCase().contains("mhz")){
                          motherboard.setRamSpeed(splitname[i].substring(0, 7));
                        }
                        if(splitname[i].equals("DDR4")){
                            motherboard.setRamType("DDR4");
                            add = false;
                        }
                        if(splitname[i].equals("DDR5")){
                            motherboard.setRamType("DDR5");
                            add = false;
                        }
                        if(splitname[i].equals("AM4") ||
                                splitname[i].equals("AM5") || splitname[i].equals("LGA 1200") ||
                                splitname[i].equals("LGA 1700")|| splitname[i].equals("LGA 1851")){
                            add = false;
                        }
                        if(add) {
                            newName.append(" ").append(splitname[i]);
                        }

                    for (String chipset : chipsets) {
                        if(splitname[i].contains(chipset)){
                            motherboard.setChipset(chipset);
                        if(splitname[i].toLowerCase().contains("m")){
                            motherboard.setFormFactor("mATX");
                        }
//                        else if(splitname[i].toLowerCase().contains("e")){
//                            motherboard.setFormFactor("EATX");
//                        }
                        else if(splitname[i].toLowerCase().contains("i")){
                            motherboard.setFormFactor("ITX");
                        }
                        else{
                            motherboard.setFormFactor("ATX");
                        }
                        }
                    }

                }

                motherboard.setModelName(newName.toString().trim());
                for (String brand : motherboardBrands) {
                    if(node.get("name").asText().contains(brand)){
                        motherboard.setBrand(brand);
                        break;
                    }
                }
                for (String socket : sockets) {
                    if(node.get("name").asText().contains(socket)){
                        motherboard.setSocket(socket);
                        break;
                    }
                }


                motherboardRepository.save(motherboard);
            }
        }
        catch (IOException e){
            System.out.println("no jason");
        }
    }

    @Override
    public void scrapeGpus() {
        String url = "https://www.anhoch.com/products?query=&brand[0]=26&brand[1]=185&brand[2]=89&categories[0]=grafichki-karti&tag=&fromPrice=0&toPrice=349980&inStockOnly=1&sort=priceLowToHigh&perPage=130&page=1";
        List<String> gpuUrls = new ArrayList<>();
        try{
            String jsonResponse = Jsoup.connect(url)
                    .ignoreContentType(true)
                    .userAgent("Mozilla/5.0")
                    .header("Accept", "application/json")
                    .execute()
                    .body();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(jsonResponse);

            JsonNode products = root.get("products").get("data");
            for (JsonNode node : products) {
                gpuUrls.add("https://www.anhoch.com/products/" + node.get("slug").asText());
            }
            for (String gpuUrl : gpuUrls) {
                try {
                    Document doc = Jsoup.connect(gpuUrl)
                            .userAgent("Mozilla/5.0")
                            .get();

                    String storeUrl = gpuUrl;
                    Element price = doc.getElementsByClass("product-price").first();
                    Gpu gpu = gpuRepository.findByStoreUrl(storeUrl);
                    if(gpu != null) {
                        if(productPriceDateRepository.findByStoreUrlAndDate(storeUrl, LocalDate.now()) == null) {
                            ProductPriceDate productPriceDate = new ProductPriceDate();
                            productPriceDate.setStoreUrl(storeUrl);
                            productPriceDate.setDate(LocalDate.now());
                            productPriceDate.setPrice(Double.parseDouble(price.text().replace(",", "").
                                    replace(".", "").
                                    replaceAll("[^\\d.]", "")) / 100);
                            productPriceDateRepository.save(productPriceDate);
                        }
                        gpu.setPriceMkd(Double.parseDouble(price.text().replace(",", "").
                                replace(".", "").
                                replaceAll("[^\\d.]", "")) / 100);
                        gpuRepository.save(gpu);
                        continue;
                    }
                    gpu = new Gpu();
                    gpu.setPriceMkd(Double.parseDouble(price.text().replace(",", "").
                            replace(".", "").
                            replaceAll("[^\\d.]", "")) / 100);
                    Element modelName = doc.getElementsByClass("product-name").first();
                    Element imgUrl = doc.select("div.base-image-slide img").first();
                    String brand = modelName.text().split(" ")[0];
                    String[] model = modelName.text().split(" ");
                    StringBuilder newName = new StringBuilder();
                    for(int i = 1; i < model.length; i++) {
                        if(model[i].equalsIgnoreCase("2gb") ||
                                model[i].equalsIgnoreCase("6gb") ||
                                model[i].equalsIgnoreCase("8gb") ||
                                model[i].equalsIgnoreCase("12gb") ||
                                model[i].equalsIgnoreCase("16gb") ||
                                model[i].equalsIgnoreCase("24gb") ||
                                model[i].equalsIgnoreCase("32gb") ||
                                model[i].equalsIgnoreCase("2g") ||
                                model[i].equalsIgnoreCase("6g") ||
                                model[i].equalsIgnoreCase("8g") ||
                                model[i].equalsIgnoreCase("12g") ||
                                model[i].equalsIgnoreCase("16g") ||
                                model[i].equalsIgnoreCase("24g") ||
                                model[i].equalsIgnoreCase("32g")){
                            gpu.setVram(model[i]);
                            break;
                        }
                        newName.append(model[i]).append(" ");
                    }
                    if(modelName.toString().toLowerCase().contains("geforce")){
                        gpu.setChipMaker("Nvidia");
                    }
                    else{
                        gpu.setChipMaker("AMD");
                    }
                    gpu.setBrand(brand);
                    gpu.setModelName(newName.toString().replace("PLUS","").replace("  "," ").trim());
                    gpu.setStoreName("Anhoch");
                    gpu.setStoreUrl(gpuUrl);
                    if (imgUrl != null) {
                        gpu.setImgUrl(imgUrl.attr("src"));
                    }
                    gpu.setPriceMkd(Double.parseDouble(price.text().replace(",", "").
                            replace(".", "").
                            replaceAll("[^\\d.]", "")) / 100);
                    List<String> nvidiaGpus = List.of("GT710","RTX 3050", "RTX 3060", "RTX 3060 Ti", "RTX 3070", "RTX 3070 Ti", "RTX 3080", "RTX 3080 Ti", "RTX 3090", "RTX 3090 Ti",
                            "RTX 4060", "RTX 4060 Ti", "RTX 4070", "RTX 4070 Super", "RTX 4070 Ti", "RTX 4070 Ti Super", "RTX 4080", "RTX 4080 Super", "RTX 4090",
                            "RTX 5050", "RTX 5060", "RTX 5060 Ti", "RTX 5070", "RTX 5070 Ti", "RTX 5080", "RTX 5090");
                    List<String> amdGpus = List.of("RX 6500 XT", "RX 6600", "RX 6600 XT", "RX 6650 XT", "RX 6700", "RX 6700 XT", "RX 6750 XT", "RX 6800", "RX 6800 XT", "RX 6900 XT", "RX 6950 XT",
                            "RX 7600", "RX 7600 XT", "RX 7700 XT", "RX 7800 XT", "RX 7900 GRE", "RX 7900 XT", "RX 7900 XTX",
                            "RX 9060", "RX 9060 XT", "RX 9070", "RX 9070 XT");
                    if(gpu.getChipMaker().equalsIgnoreCase("Nvidia")){
                        for(String ngpu : nvidiaGpus){
                            if(modelName.toString().contains(ngpu)){
                                gpu.setChipset(ngpu);
                            }
                        }
                    }
                    if(gpu.getChipMaker().equalsIgnoreCase("AMD")){
                        for(String agpu : amdGpus){
                            if(modelName.toString().contains(agpu)){
                                gpu.setChipset(agpu);
                            }
                        }
                    }

                    Element descriptionElement = doc.selectFirst("#description");
                    String description = descriptionElement.text();
                    System.out.println(description);

                    Pattern labelPattern = Pattern.compile("(?i)(card dimension|card size|dimension)[:\\s(mm)]+");
                    Matcher labelMatcher = labelPattern.matcher(description);
                    if (labelMatcher.find()) {
                        String searchZone = description.substring(labelMatcher.end());

                        Pattern numPattern = Pattern.compile("(\\d+\\.\\d+|\\d+)");
                        Matcher numMatcher = numPattern.matcher(searchZone);

                        List<Integer> triplet = new ArrayList<>();
                        while (numMatcher.find() && triplet.size() < 3) {
                            int val = (int) Double.parseDouble(numMatcher.group());
                            if (val > 10) {
                                triplet.add(val);
                            }
                        }
                        if (!triplet.isEmpty()) {
                            gpu.setLength(Collections.max(triplet));
                        }
                    }
                    gpuRepository.save(gpu);
                } catch (IOException e) {
                    System.out.println("Error fetching HTML from: " + gpuUrl + " - " + e.getMessage());
                }
//                break;
            }
        }
        catch (IOException e){
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void scrapeRams() {
        String url = "https://www.anhoch.com/products?query=&categories[0]=desktop-ram-memorii&tag=&fromPrice=0&toPrice=349980&inStockOnly=1&sort=priceLowToHigh&perPage=130&page=1";

        try {
            String jsonResponse = Jsoup.connect(url)
                    .ignoreContentType(true)
                    .userAgent("Mozilla/5.0")
                    .header("Accept", "application/json")
                    .execute()
                    .body();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(jsonResponse);

            JsonNode products = root.get("products").get("data");

            for (JsonNode node : products) {
                String storeUrl = "https://www.anhoch.com/products/" + node.get("slug").asText();
                double price = node.get("price")
                        .get("inCurrentCurrency")
                        .get("amount").asDouble();
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
                ram.setPriceMkd(price);
                ram.setModelName(node.get("name").asText());

                ram.setStoreUrl("https://www.anhoch.com/products/" + node.get("slug").asText());
                ram.setStoreName("Anhoch");

                String imgUrl = node.path("base_image").path("path").asText("");
                ram.setImgUrl(imgUrl);
                if(ram.getImgUrl() == null || ram.getImgUrl().isEmpty()){
                    ram.setImgUrl("https://www.anhoch.com/themes/storefront/public/images/image-placeholder.png");
                }
                String name = ram.getModelName();
                String[] splitname = name.split(" ");
                ram.setRamSize(splitname[1]);
                ram.setRamType(splitname[2]);
                ram.setRamSpeed(splitname[3]);
                ram.setBrand(splitname[4]);
                if(name.contains("2x")){
                    ram.setConfiguration("Kit of 2x"+(Integer.parseInt(splitname[1].replace("GB",""))/2) + "GB");
                }
                else{
                    ram.setConfiguration("Single Stick");
                }
                ram.setModelName("");
                for(int i = 1; i < splitname.length; i++){
                    if(splitname[i].equalsIgnoreCase("fury")){
                        ram.setModelName(splitname[i] + " " + splitname[i+1]);
                    }
                    if(splitname[i].toLowerCase().contains("cl")){
                        ram.setLatency(splitname[i]);
                    }
                }
                if(ram.getBrand().equals("Netac")){
                    if(name.contains("Netac Shadow")){
                        ram.setModelName("Netac Shadow");
                        if(name.contains("RGB")){
                            ram.setModelName("Netac Shadow RGB");
                            if(name.contains("Silver")){
                                ram.setModelName("Netac Shadow RGB Silver");
                            }
                        }
                    }
                }
                if(ram.getBrand().equals("Patriot")){
                    if(name.contains("Patriot Viper Venom")){
                        ram.setModelName("Patriot Viper Venom");
                    }
                }
                if(ram.getModelName().isEmpty()){
                    ram.setModelName(name);
                }
                ramRepository.save(ram);
            }
        }catch (IOException e){
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void scrapeDisks() {
        String url = "https://www.anhoch.com/products?query=&categories[0]=diskovi-i-skladiranje&tag=&fromPrice=1212&toPrice=349980&inStockOnly=1&sort=priceLowToHigh&perPage=130&page=1";

        try {
            String jsonResponse = Jsoup.connect(url)
                    .ignoreContentType(true)
                    .userAgent("Mozilla/5.0")
                    .header("Accept", "application/json")
                    .execute()
                    .body();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(jsonResponse);

            JsonNode products = root.get("products").get("data");

            for (JsonNode node : products) {
                String storeUrl = "https://www.anhoch.com/products/" + node.get("slug").asText();
                double price = node.get("price")
                        .get("inCurrentCurrency")
                        .get("amount").asDouble();
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
                disk.setPriceMkd(price);
                disk.setModelName(node.get("name").asText());

                disk.setStoreUrl("https://www.anhoch.com/products/" + node.get("slug").asText());
                disk.setStoreName("Anhoch");

                String imgUrl = node.path("base_image").path("path").asText("");
                disk.setImgUrl(imgUrl);
                if(disk.getImgUrl() == null || disk.getImgUrl().isEmpty()){
                    disk.setImgUrl("https://www.anhoch.com/themes/storefront/public/images/image-placeholder.png");
                }
                String name = disk.getModelName();
                String[] splitname = name.split(" ");
                disk.setDiskType(splitname[0]);
                String diskSpeed = "";
                if(splitname[1].equalsIgnoreCase("M.2") ){
                    if(name.toLowerCase().contains("sata")){
                        disk.setConnector("M.2");
                        diskSpeed = "SATA3";
                    }
                    else{
                        disk.setConnector("M.2");
                        diskSpeed = "NVMe";
                    }
                }
                if(splitname[1].equalsIgnoreCase("2.5\"")){
                    disk.setConnector("SATA");
                    diskSpeed = "SATA3";
                }
                if(splitname[0].equalsIgnoreCase("HDD")){
                    disk.setConnector("SATA");
                    diskSpeed = "SATA3";
                }
                disk.setSpeed(diskSpeed);
                for(String word : splitname){
                    if(word.toLowerCase().contains("gb") ||word.toLowerCase().contains("tb")){
                        disk.setSize(word);
                    }
                }
                String diskBrand = "";
                List<String> brands = List.of("Adata", "Gigabyte", "Kingston", "Netac", "Crucial", "Samsung", "Seagate", "Synology", "Toshiba", "Verbatim", "Western Digital");
                for(String brand : brands){
                    if(name.contains(brand)){
                        diskBrand = brand;
                    }
                }
                disk.setBrand(diskBrand);
                if(disk.getBrand().equals("Adata") ||
                        disk.getBrand().equals("Gigabyte")){
                    disk.setModelName(splitname[2] + " " + splitname[3]);
                }
                if(disk.getBrand().equals("Kingston")){
                    if(disk.getSpeed().equals("NVMe")){
                        StringBuilder newname = new StringBuilder();
                        for(int i = 2; i<splitname.length; i++){
                            if(splitname[i].equalsIgnoreCase("2280") || splitname[i].equalsIgnoreCase("nvme")){
                                break;
                            }
                            newname.append(splitname[i]).append(" ");
                        }
                        disk.setModelName(newname.toString());
                    }
                    else{
                        disk.setModelName(splitname[2] + " " + splitname[3]);
                    }
                }
                if(disk.getBrand().equals("Netac")){
                    if(disk.getSpeed().equals("NVMe")){
                        disk.setModelName(splitname[3]+ " " + splitname[5] + " " + splitname[6]);
                    }
                    else{
                        disk.setModelName(splitname[2] + " " + splitname[3]);
                    }
                }
                if(disk.getBrand().equals("Crucial")){
                    disk.setModelName(splitname[2] + " " + splitname[4]);
                }
                if(disk.getBrand().equals("Samsung")){
                    StringBuilder newname = new StringBuilder();

                    for(int i = 2; i < splitname.length; i++){
                        boolean add = true;
                        if(splitname[i].contains("GB") || splitname[i].contains("TB")){
                            break;
                        }
                        if(splitname[i].equals("NVMe")){
                            add = false;
                        }
                        if(add){
                            newname.append(splitname[i]).append(" ");
                        }
                    }
                    disk.setModelName(newname.toString().trim());
                }
                if(disk.getBrand().equals("Seagate")){
                    disk.setModelName(splitname[3] + " " + splitname[4]);
                }
                if(disk.getBrand().equals("Synology")){
                    disk.setModelName(splitname[3] + " " + splitname[4] + " " + splitname[5] + " " + splitname[6]);
                }
                if(disk.getBrand().equals("Toshiba") || disk.getBrand().equals("Western Digital")){
                    StringBuilder newname = new StringBuilder();

                    for(int i = 3; i < splitname.length; i++){
                        if(splitname[i].equals("SATA3")){
                            break;
                        }
                        newname.append(splitname[i]).append(" ");
                    }
                    disk.setModelName(newname.toString().trim());
                }
                if(disk.getBrand().equals("Verbatim")){
                    for(int i = 2; i < splitname.length; i++){
                        if(splitname[i].equals("Verbatim")){
                            disk.setModelName(splitname[i] + " " + splitname[i+1]);
                            break;
                        }
                    }
                }
                diskRepository.save(disk);
            }
        }catch (IOException e){
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void scrapePsus() {
        String url = "https://www.anhoch.com/products?query=&categories[0]=napojuvanja&tag=&fromPrice=0&toPrice=349980&inStockOnly=1&sort=priceLowToHigh&perPage=130&page=1";

        try {
            String jsonResponse = Jsoup.connect(url)
                    .ignoreContentType(true)
                    .userAgent("Mozilla/5.0")
                    .header("Accept", "application/json")
                    .execute()
                    .body();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(jsonResponse);

            JsonNode products = root.get("products").get("data");

            for (JsonNode node : products) {
                String storeUrl = "https://www.anhoch.com/products/" + node.get("slug").asText();
                double price = node.get("price")
                        .get("inCurrentCurrency")
                        .get("amount").asDouble();
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
                psu.setPriceMkd(price);
                psu.setModelName(node.get("name").asText());


                psu.setStoreUrl("https://www.anhoch.com/products/" + node.get("slug").asText());
                psu.setStoreName("Anhoch");

                String imgUrl = node.path("base_image").path("path").asText("");
                psu.setImgUrl(imgUrl);
                if (psu.getImgUrl() == null || psu.getImgUrl().isEmpty()) {
                    psu.setImgUrl("https://www.anhoch.com/themes/storefront/public/images/image-placeholder.png");
                }
                String name = psu.getModelName();
                String[] splitname = name.split(" ");
                List<String> brands = List.of("BE Quiet!", "Corsair", "Xilence", "Seasonic", "Deepcool", "Genesis", "Gigabyte", "Matrix", "MSI", "NZXT", "White Shark", "SBOX", "Sharkoon", "Thermaltake");
                List<String> psuWattages = List.of("300W",
                        "400W", "450W", "500W", "550W", "600W",
                        "650W", "700W", "750W", "800W", "850W",
                        "900W", "1000W", "1200W", "1300W", "1500W"
                );
                List<String> ratings = List.of("80Plus", "80+","80 plus", "bronze", "silver", "gold", "plat.", "platinum", "titanium", "diamond");
                String brandName = "";
                for(String brand : brands){
                    if(name.contains(brand)){
                        brandName = brand;
                    }
                }
                psu.setBrand(brandName);
                for(String psuWattage : psuWattages){
                    if(name.contains(psuWattage)){
                        psu.setWattage(psuWattage);
                    }
                }
                if(name.contains("Modular")){
                    psu.setModular("Modular");
                }
                else{
                    psu.setModular("Non Modular");
                }

                psu.setEfficiency("N/A");
                for(int i=0 ; i < ratings.size(); i++){
                    if(name.contains("Cybenetics") && !brandName.equals("Deepcool")){
                        if(i == 3){
                            if(name.toLowerCase().contains(ratings.get(i))){
                                psu.setEfficiency("Cybenetics Bronze");
                            }
                        }
                        if(i == 4){
                            if(name.toLowerCase().contains(ratings.get(i))){
                                psu.setEfficiency("Cybenetics Silver");
                            }
                        }
                        if(i == 5){
                            if(name.toLowerCase().contains(ratings.get(i))){
                                psu.setEfficiency("Cybenetics Gold");
                            }
                        }
                        if(i == 6 || i==7){
                            if(name.toLowerCase().contains(ratings.get(i))){
                                psu.setEfficiency("Cybenetics Platinum");
                            }
                        }
                        if(i == 8){
                            if(name.toLowerCase().contains(ratings.get(i))){
                                psu.setEfficiency("Cybenetics Titanium");
                            }
                        }
                        if(i == 9){
                            if(name.toLowerCase().contains(ratings.get(i))){
                                psu.setEfficiency("Cybenetics Diamond");
                            }
                        }
                    }
                    else{
                        if(i==0 || i==1){
                            if(name.contains(ratings.get(i))){
                                psu.setEfficiency("80+");
                            }
                        }
                        if(i==2){
                            if(name.toLowerCase().contains(ratings.get(i).toLowerCase())){
                                psu.setEfficiency("80+");
                            }
                        }
                        if(i == 3){
                            if(name.toLowerCase().contains(ratings.get(i))){
                                psu.setEfficiency("80+ Bronze");
                            }
                        }
                        if(i == 4){
                            if(name.toLowerCase().contains(ratings.get(i))){
                                psu.setEfficiency("80+ Silver");
                            }
                        }
                        if(i == 5){
                            if(name.toLowerCase().contains(ratings.get(i))){
                                psu.setEfficiency("80+ Gold");
                            }
                        }
                        if(i == 6 || i==7){
                            if(name.toLowerCase().contains(ratings.get(i))){
                                psu.setEfficiency("80+ Platinum");
                            }
                        }
                        if(i == 8){
                            if(name.toLowerCase().contains(ratings.get(i))){
                                psu.setEfficiency("80+ Titanium");
                            }
                        }
                    }
                }

                StringBuilder newname = new StringBuilder();
                if(brandName.equals("BE Quiet!") ||
                    brandName.equals("Corsair") ||
                    brandName.equals("Deepcool") ||
                    brandName.equals("Gigabyte") ||
                    brandName.equals("White Shark") ||
                    brandName.equals("MSI") ||
                    brandName.equals("NZXT") ||
                    brandName.equals("Genesis")){
                    for(int i=2 ; i < splitname.length; i++){
                        if(splitname[i].startsWith("80")){
                            break;
                        }
                        newname.append(splitname[i]).append(" ");
                    }
                    psu.setModelName(newname.toString().trim());
                }

                if(brandName.equals("Sharkoon") || brandName.equals("Seasonic")){
                    for(int i=2 ; i < splitname.length; i++){
                        if(splitname[i].equals("ATX")){
                            break;
                        }
                        newname.append(splitname[i]).append(" ");
                    }
                    psu.setModelName(newname.toString().trim());
                }
                if(brandName.equals("Thermaltake")){
                    for(int i=2 ; i < splitname.length; i++){
                        if(splitname[i].endsWith("Plus") || splitname[i].endsWith("0W")){
                            break;
                        }
                        newname.append(splitname[i]).append(" ");
                    }
                    psu.setModelName(newname.toString().trim());
                }
                if(brandName.equals("Xilence")){
                    newname.append(splitname[2]).append(" ").append(splitname[3]).append(" ").append(splitname[4]).append(" ").append(splitname[5]);
                    psu.setModelName(newname.toString().trim());
                }
                psuRepository.save(psu);
            }
        }catch (IOException e){
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void scrapeCases() {
        String url = "https://www.anhoch.com/products?query=&categories[0]=kukjishta&tag=&fromPrice=0&toPrice=349980&inStockOnly=1&sort=priceLowToHigh&perPage=130&page=1";
        List<String> caseUrls = new ArrayList<>();
        try{
            String jsonResponse = Jsoup.connect(url)
                    .ignoreContentType(true)
                    .userAgent("Mozilla/5.0")
                    .header("Accept", "application/json")
                    .execute()
                    .body();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(jsonResponse);

            JsonNode products = root.get("products").get("data");
            for (JsonNode node : products) {
                caseUrls.add("https://www.anhoch.com/products/" + node.get("slug").asText());
            }
            for (String caseUrl : caseUrls) {
                try {
                    Document doc = Jsoup.connect(caseUrl)
                            .userAgent("Mozilla/5.0")
                            .get();

                    String storeUrl = caseUrl;
                    Element price = doc.getElementsByClass("product-price").first();
                    PcCase kase = caseRepository.findByStoreUrl(storeUrl);
                    if(kase != null) {
                        if(productPriceDateRepository.findByStoreUrlAndDate(storeUrl, LocalDate.now()) == null) {
                            ProductPriceDate productPriceDate = new ProductPriceDate();
                            productPriceDate.setStoreUrl(storeUrl);
                            productPriceDate.setDate(LocalDate.now());
                            productPriceDate.setPrice(Double.parseDouble(price.text().substring(0,13).replace(",", "").
                                    replace(".", "").
                                    replaceAll("[^\\d.]", "")) / 100);
                            productPriceDateRepository.save(productPriceDate);
                        }
                        kase.setPriceMkd(Double.parseDouble(price.text().substring(0,13).replace(",", "").
                                replace(".", "").
                                replaceAll("[^\\d.]", "")) / 100);
                        caseRepository.save(kase);
                        continue;
                    }
                    kase = new PcCase();
                    kase.setPriceMkd(Double.parseDouble(price.text().substring(0,13).replace(",", "").
                            replace(".", "").
                            replaceAll("[^\\d.]", "")) / 100);
                    Element modelName = doc.getElementsByClass("product-name").first();

                    Element imgUrl = doc.select("div.base-image-slide img").first();
                    String brand = "";
                    String[] model = modelName.text().split(" ");
                    StringBuilder newName = new StringBuilder();
                    kase.setPriceMkd(Double.parseDouble(price.text().substring(0,13).replace(",", "").
                            replace(".", "").
                            replaceAll("[^\\d.]", "")) / 100);
                    kase.setStoreName("Anhoch");
                    kase.setStoreUrl(caseUrl);
                    if (imgUrl != null) {
                        kase.setImgUrl(imgUrl.attr("src"));
                    }
                    List<String> brands = List.of(
                            "Antec", "Corsair", "Deepcool", "Genesis", "Gigabyte", "Matrix",
                            "White Shark", "Antec", "Fury",
                            "MSI", "Natec", "NZXT", "Razer",
                            "SAMA", "SBOX", "Sharkoon", "Thermaltake","DarkFlash"
                    );
                    for(String brandName : brands){
                        if(modelName.text().toLowerCase().contains(brandName.toLowerCase())){
                            brand = brandName;
                        }
                    }
                    kase.setBrand(brand);
                    if(modelName.text().toLowerCase().contains("atx")){
                        kase.setFormFactor("ATX");
                    }
                    if(modelName.text().toLowerCase().contains("itx")){
                        kase.setFormFactor("ITX");
                    }
                    if(modelName.text().toLowerCase().contains("e-atx")){
                        kase.setFormFactor("EATX");
                    }
                    if(modelName.text().toLowerCase().contains("micro")){
                        kase.setFormFactor("mATX");
                    }
                    doc.select("br").after("\\n");
                    String cleanText = doc.getElementById("description").text().replaceAll("\\\\n", "\n");

                    String[] lines = cleanText.split("\n");
                    boolean gpufound = false;
                    boolean cpufound = false;
                    for (String line : lines) {
                        Pattern pattern = Pattern.compile("\\d+");
                        Matcher matcher = pattern.matcher(line);
//                      if ((line.contains("mm") || line.contains("cm")) && (line.contains("GPU") || line.toLowerCase().contains("vga") || line.toLowerCase().contains("graphics"))) {
                        if ( !gpufound && line.length() < 167  && (line.toLowerCase().contains("gpu") || line.toLowerCase().contains("vga") || line.toLowerCase().contains("graphics"))) {
                            if (matcher.find()) {
                                if(matcher.group().length() > 1){
                                    if(matcher.group().length() == 2){
                                        String gpulength = matcher.group();
                                        kase.setModelName(line);
                                        kase.setGpuLength(gpulength + "0");
                                    }
                                    else{
                                        String gpulength = matcher.group();
                                        kase.setModelName(line);
                                        kase.setGpuLength(gpulength);
                                    }
                                    gpufound = true;
                                }
                            }
                        }
                        if (line.contains(":") && line.toLowerCase().contains("cpu")) {
                            if (matcher.find()) {
                                if(matcher.group().length() < 3){
                                    String cpulength = matcher.group();
                                    kase.setCoolerHeight(cpulength + "0");
                                }
                                else{
                                    String cpulength = matcher.group();
                                    kase.setCoolerHeight(cpulength);
                                }
                                cpufound = true;
                            }
                        }
                    }

                    if(brand.equals("Corsair") || brand.equals("Deepcool") || brand.equals("Genesis") || brand.equals("Gigabyte") ||
                        brand.equals("Matrix") || brand.equals("MSI") || brand.equals("Natec") || brand.equals("NZXT") ||
                        brand.equals("Razer") || brand.equals("Sharkoon") || brand.equals("SAMA") || brand.equals("Antec") ||
                        brand.equals("SBOX") || brand.equals("Fury")){
                        boolean add = false;
                        for(int i = 2; i < model.length; i++){
                            if(model[i].equals(brand)){
                                add = true;
                            }
                            if(model[i].startsWith("w/")){
                                add = false;
                            }
                            if(add){
                                newName.append(model[i]).append(" ");
                            }
                        }
                    }
                    if(brand.equals("Thermaltake")){
                        boolean add = false;
                        for(int i = 2; i < model.length; i++){
                            if(model[i].equals(brand)){
                                add = true;
                            }
                            if(model[i].startsWith("w/") || model[i].startsWith("Mid")){
                                add = false;
                            }
                            if(add){
                                newName.append(model[i]).append(" ");
                            }
                        }
                        if(newName.toString().contains("The Tower 500")){
                            kase.setCoolerHeight("275");
                            kase.setGpuLength("325");
                        }
                        if(newName.toString().contains("The Tower 300")){
                            kase.setCoolerHeight("210");
                            kase.setGpuLength("280");
                        }
                    }
                    if(brand.equals("White Shark")){
                        boolean add = false;
                        for(int i = 2; i < model.length; i++){
                            if(model[i].equals("White")){
                                add = true;
                            }
                            if(model[i].startsWith("w/")){
                                add = false;
                            }
                            if(add){
                                newName.append(model[i]).append(" ");
                            }
                        }
                    }
                    if(brand.equals("DarkFlash")){
                        boolean add = false;
                        for(int i = 2; i < model.length; i++){
                            if(model[i].equals("DarkFlash")){
                                add = true;
                            }
                            if(model[i].startsWith("w/")){
                                add = false;
                            }
                            if(add){
                                newName.append(model[i]).append(" ");
                            }
                        }
                    }
                    kase.setModelName(newName.toString().trim());
                    caseRepository.save(kase);


                }catch (IOException e){
                System.out.println(e.getMessage());
                }
            }
    }catch (IOException e){
        System.out.println(e.getMessage());}
    }

    @Override
    public void scrapeCoolers() {
        String url = "https://www.anhoch.com/products?query=&categories[0]=vozdushni-ladilnici&tag=&fromPrice=300&toPrice=349980&inStockOnly=1&sort=priceLowToHigh&perPage=130&page=1";
        List<String> coolerUrls = new ArrayList<>();
        try{
            String jsonResponse = Jsoup.connect(url)
                    .ignoreContentType(true)
                    .userAgent("Mozilla/5.0")
                    .header("Accept", "application/json")
                    .execute()
                    .body();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(jsonResponse);

            JsonNode products = root.get("products").get("data");
            for (JsonNode node : products) {
                coolerUrls.add("https://www.anhoch.com/products/" + node.get("slug").asText());
            }
            for (String coolerUrl : coolerUrls) {
                try {
                    Document doc = Jsoup.connect(coolerUrl)
                            .userAgent("Mozilla/5.0")
                            .get();

                    String storeUrl = coolerUrl;
                    Element price = doc.getElementsByClass("product-price").first();
                    Cooler cooler = coolerRepository.findByStoreUrl(storeUrl);
                    if(cooler != null) {
                        if(productPriceDateRepository.findByStoreUrlAndDate(storeUrl, LocalDate.now()) == null) {
                            ProductPriceDate productPriceDate = new ProductPriceDate();
                            productPriceDate.setStoreUrl(storeUrl);
                            productPriceDate.setDate(LocalDate.now());
                            productPriceDate.setPrice(Double.parseDouble(price.text().substring(0,11).replace(",", "").
                                    replace(".", "").
                                    replaceAll("[^\\d.]", "")) / 100);
                            productPriceDateRepository.save(productPriceDate);
                        }
                        cooler.setPriceMkd(Double.parseDouble(price.text().substring(0,11).replace(",", "").
                                replace(".", "").
                                replaceAll("[^\\d.]", "")) / 100);
                        coolerRepository.save(cooler);
                        continue;
                    }
                    cooler = new Cooler();
                    cooler.setPriceMkd(Double.parseDouble(price.text().substring(0,11).replace(",", "").
                            replace(".", "").
                            replaceAll("[^\\d.]", "")) / 100);
                    Element modelName = doc.getElementsByClass("product-name").first();

                    Element imgUrl = doc.select("div.base-image-slide img").first();
                    String brand = "";
                    String[] model = modelName.text().split(" ");
                    StringBuilder newName = new StringBuilder();
                    cooler.setPriceMkd(Double.parseDouble(price.text().substring(0,11).replace(",", "").
                            replace(".", "").
                            replaceAll("[^\\d.]", "")) / 100);
                    cooler.setStoreName("Anhoch");
                    cooler.setStoreUrl(coolerUrl);
                    cooler.setCoolerType("Air Cooler");
                    cooler.setImgUrl(imgUrl.attr("src"));
                    List<String> brands = List.of("Arctic", "Be Quiet!", "Deepcool", "MSI", "Noctua", "Sharkoon", "Thermaltake");
                    for(String brandName : brands){
                        if(modelName.text().toLowerCase().contains(brandName.toLowerCase())){
                            brand = brandName;
                        }
                    }
                    cooler.setBrand(brand);
                    cooler.setRadiatorSize("N/A");
                    List<String> supportedSockets = new ArrayList<>();

                    doc.select("br").after("\\n");
                    String cleanText = doc.getElementById("description").text().replaceAll("\\\\n", "\n");
                    String[] lines = cleanText.split("\n");

                    List<String> sockets = List.of("1851", "1700", "1200", "1151", "1150", "1155", "AM5", "AM4");

                    ArrayList<Integer> numbers = new ArrayList<>();
                    for (String line : lines) {
                        for(String socket : sockets){
                            if(line.contains(socket)){
                                if(socket.startsWith("A")){
                                    supportedSockets.add("AMD " + socket);
                                }
                                else{
                                    supportedSockets.add("LGA " + socket);
                                }
                            }
                        }
                        if(line.contains("mm")){
                            Pattern pattern = Pattern.compile("\\d+");
                            Matcher matcher = pattern.matcher(line);


                            while (matcher.find()) {
                                int num = Integer.parseInt(matcher.group());
                                if(num < 191){
                                    numbers.add(num);
                                }
                            }

                            if (!numbers.isEmpty()) {
                                int largest = Collections.max(numbers);
                                cooler.setCoolerHeight(String.valueOf(largest));
                            }
                        }
                    }
                    if(!supportedSockets.isEmpty()){
                        cooler.setSockets(String.join(", ", supportedSockets));
                    }
                    else{
                        cooler.setSockets("N/A");
                    }
                    if(brand.equals("Arctic")){
                        boolean add = false;
                        for(int i = 2; i < model.length; i++){
                            if(model[i].equals("Alpine")){
                                add = true;
                            }
                            if(model[i].equals("AMD") || model[i].equals("Intel")){
                                add = false;
                            }
                            if(add){
                                newName.append(model[i]).append(" ");
                            }
                        }
                    }
                    if(brand.equals("Be Quiet!") || brand.equals("Deepcool") || brand.equals("MSI") || brand.equals("Noctua") || brand.equals("Sharkoon")){
                        boolean add = false;
                        for(int i = 1; i < model.length; i++){
                            if(model[i].equals("AMD") || model[i].startsWith("BK") || model[i].equalsIgnoreCase("all") || model[i].equals("Intel")){
                                break;
                            }
                            newName.append(model[i]).append(" ");
                            if(brand.equals("Sharkoon")){
                                if(modelName.text().contains("Black")){
                                    newName.append("Black").append(" ");
                                }
                                else{
                                    newName.append("White").append(" ");
                                }
                            }
                        }
                    }
                    if(brand.equals("Thermaltake")){
                        newName.append(modelName.text()).append(" ");
                    }
                    cooler.setModelName(newName.toString().trim());

                    coolerRepository.save(cooler);


                }catch (Exception e){
                System.out.println(e.getMessage());}
            }
    }catch (IOException e){
        System.out.println(e.getMessage());}
        url = "https://www.anhoch.com/products?query=&categories[0]=vodeno-ladenje&tag=&fromPrice=0&toPrice=349980&inStockOnly=1&sort=priceLowToHigh&perPage=150&page=1";
        coolerUrls = new ArrayList<>();
        try{
            String jsonResponse = Jsoup.connect(url)
                    .ignoreContentType(true)
                    .userAgent("Mozilla/5.0")
                    .header("Accept", "application/json")
                    .execute()
                    .body();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(jsonResponse);

            JsonNode products = root.get("products").get("data");
            for (JsonNode node : products) {
                coolerUrls.add("https://www.anhoch.com/products/" + node.get("slug").asText());
            }
            for (String coolerUrl : coolerUrls) {
                try {
                    Document doc = Jsoup.connect(coolerUrl)
                            .userAgent("Mozilla/5.0")
                            .get();

                    String storeUrl = coolerUrl;
                    Element price = doc.getElementsByClass("product-price").first();
                    Cooler cooler = coolerRepository.findByStoreUrl(storeUrl);
                    if(cooler != null) {
                        if(productPriceDateRepository.findByStoreUrlAndDate(storeUrl, LocalDate.now()) == null) {
                            ProductPriceDate productPriceDate = new ProductPriceDate();
                            productPriceDate.setStoreUrl(storeUrl);
                            productPriceDate.setDate(LocalDate.now());
                            productPriceDate.setPrice(Double.parseDouble(price.text().substring(0,11).replace(",", "").
                                    replace(".", "").
                                    replaceAll("[^\\d.]", "")) / 100);
                            productPriceDateRepository.save(productPriceDate);
                        }
                        cooler.setPriceMkd(Double.parseDouble(price.text().substring(0,11).replace(",", "").
                                replace(".", "").
                                replaceAll("[^\\d.]", "")) / 100);
                        coolerRepository.save(cooler);
                        continue;
                    }
                    cooler = new Cooler();
                    Element modelName = doc.getElementsByClass("product-name").first();
                    Element imgUrl = doc.select("div.base-image-slide img").first();
                    String brand = "";
                    String[] model = modelName.text().split(" ");
                    StringBuilder newName = new StringBuilder();
                    cooler.setPriceMkd(Double.parseDouble(price.text().substring(0,11).replace(",", "").
                            replace(".", "").
                            replaceAll("[^\\d.]", "")) / 100);
                    cooler.setStoreName("Anhoch");
                    cooler.setStoreUrl(coolerUrl);
                    cooler.setCoolerType("AIO");
                    cooler.setImgUrl(imgUrl.attr("src"));
                    List<String> brands = List.of("Be Quiet!", "Deepcool", "Genesis", "Gigabyte", "MSI", "NZXT");
                    for(String brandName : brands){
                        if(modelName.text().toLowerCase().contains(brandName.toLowerCase())){
                            brand = brandName;
                        }
                    }
                    cooler.setBrand(brand);
                    cooler.setCoolerHeight("N/A");
                    List<String> supportedSockets = new ArrayList<>();

                    doc.select("br").after("\\n");
                    String cleanText = doc.getElementById("description").text().replaceAll("\\\\n", "\n");
                    String[] lines = cleanText.split("\n");

                    List<String> sockets = List.of("1851", "1700", "1200", "1151", "1150", "1155", "AM5", "AM4");

                    ArrayList<Integer> radSizes = new ArrayList<>();
                    for (String line : lines) {
                        for(String socket : sockets){
                            if(line.contains(socket)){
                                if(socket.startsWith("A")){
                                    supportedSockets.add("AMD " + socket);
                                }
                                else{
                                    supportedSockets.add("LGA " + socket);
                                }
                            }
                        }
                        if(line.contains("mm")){
                            Pattern pattern = Pattern.compile("\\d+");
                            Matcher matcher = pattern.matcher(line);

                            while (matcher.find()) {
                                int num = Integer.parseInt(matcher.group());
                                if(num == 120 || num == 140 || num == 240 || num == 280 || num == 360 || num == 420){
                                    radSizes.add(num);
                                }
                            }
                            if(!radSizes.isEmpty()){
                                int largest = Collections.max(radSizes);
                                cooler.setRadiatorSize(String.valueOf(largest));
                            }
                        }
                    }
                    if(!supportedSockets.isEmpty()){
                        cooler.setSockets(String.join(", ", supportedSockets));
                    }
                    else{
                        cooler.setSockets("N/A");
                    }

                    if(brand.equals("Be Quiet!") ){
                        boolean add = false;
                        for(int i = 2; i < model.length; i++){
                            if(model[i].equals("AMD") || model[i].startsWith("36") || model[i].equalsIgnoreCase("sockets") ||
                               model[i].startsWith("w/") || model[i].equals("240") || model[i].equals("360")){
                                break;
                            }
                            newName.append(model[i]).append(" ");
                        }
                    }
                    if(brand.equals("Gigabyte") || brand.equals("MSI") || brand.equals("NZXT") || brand.equals("Genesis")|| brand.equals("Deepcool")){
                        for(int i = 2; i < model.length; i++){
                            if(model[i].equalsIgnoreCase("all") || model[i].startsWith("LGA") || model[i].startsWith("w/") || model[i].equalsIgnoreCase("sockets") ){
                                break;
                            }
                            newName.append(model[i]).append(" ");
                        }
                    }
                    List<String> radiatorSizes = List.of("120", "140", "240", "280", "360", "420");
                    for(String radiatorSize : radiatorSizes){
                        if(modelName.text().toLowerCase().contains(radiatorSize)){
                            cooler.setRadiatorSize(radiatorSize);
                        }
                    }
                    if(brand.equals("Deepcool")){
                        if(modelName.text().contains("720")){
                            cooler.setRadiatorSize("360");
                        }
                        if(modelName.text().contains("520")){
                            cooler.setRadiatorSize("240");
                        }
                    }


                    cooler.setModelName(newName.toString().trim());

                    coolerRepository.save(cooler);


                }catch (Exception e){
                    System.out.println(e.getMessage());}
            }
        }catch (IOException e){
            System.out.println(e.getMessage());}
    }
}

