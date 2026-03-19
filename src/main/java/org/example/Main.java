package org.example;

import org.example.Entity.Product;
import org.example.Entity.Purchase;
import org.example.Entity.Warehouse;
import org.example.repo.PurchaseRepository;
import org.example.repo.WarehouseRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
        System.out.println("Application started!");
    }


    @Bean
    public CommandLineRunner initAllData(WarehouseRepository wRepo, PurchaseRepository pRepo) {
        return args -> {
            // 1. Sicherstellen, dass Warenhäuser existieren
            if (wRepo.count() == 0) {
                wRepo.save(createWarehouse("001", "Linz Bahnhof", "Linz"));
                wRepo.save(createWarehouse("002", "Wien West", "Wien"));
                System.out.println("Warenhäuser neu angelegt.");
            }

            // 2. Daten für die Vertiefung
            if (pRepo.count() < 300) {
                pRepo.deleteAll();

                List<Warehouse> warehouses = new ArrayList<>();
                wRepo.findAll().forEach(warehouses::add);

                if (warehouses.isEmpty()) {
                    System.out.println("Fehler: Keine Warenhäuser gefunden!");
                    return;
                }

                List<Purchase> bigData = new ArrayList<>();
                java.util.Random rand = new java.util.Random();

                for (int i = 0; i < 300; i++) {
                    Purchase sale = new Purchase();

                    // Zufälliges Warehouse wählen
                    Warehouse randomW = warehouses.get(rand.nextInt(warehouses.size()));

                    // Sicherstellen, dass das Warehouse Produkte hat
                    List<Product> prods = randomW.getProductData();
                    if (prods == null || prods.isEmpty()) continue;

                    Product randomP = prods.get(rand.nextInt(prods.size()));

                    int day = rand.nextInt(28) + 1;
                    sale.setPurchaseDateTime("2026-03-" + (day < 10 ? "0" + day : day) + " 12:00:00");
                    sale.setAmount(rand.nextInt(10) + 1);
                    sale.setWarehouse(randomW);
                    sale.setProduct(randomP);
                    bigData.add(sale);
                }
                pRepo.saveAll(bigData);
                System.out.println("300 Vertiefung Records erfolgreich geladen!");
            }
        };
    }

    // Hilfsmethode um Code sauber zu halten
    private Warehouse createWarehouse(String id, String name, String city) {
        Warehouse w = new Warehouse();
        w.setWarehouseID(id);
        w.setWarehouseName(name);
        w.setWarehouseCity(city);
        List<Product> products = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            Product p = new Product();
            p.setProductID(id + "-P" + i);
            p.setProductName("Produkt " + i);
            p.setProductQuantity(100);
            products.add(p);
        }
        w.setProductData(products);
        return w;
    }
}