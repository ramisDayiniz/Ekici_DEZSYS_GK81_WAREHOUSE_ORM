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
            // 1. Grundlagen: Warenhäuser und Produkte anlegen
            if (wRepo.count() == 0) {
                Warehouse w1 = new Warehouse();
                w1.setWarehouseID("001");
                w1.setWarehouseName("Linz Bahnhof");
                w1.setWarehouseCity("Linz");

                List<Product> products1 = new ArrayList<>();
                for (int i = 1; i <= 5; i++) {
                    Product p = new Product();
                    p.setProductID("PROD-00" + i);
                    p.setProductName("Bio Saft " + i);
                    p.setProductQuantity(100 * i);
                    products1.add(p);
                }
                w1.setProductData(products1);

                Warehouse w2 = new Warehouse();
                w2.setWarehouseID("002");
                w2.setWarehouseName("Wien West");
                w2.setWarehouseCity("Wien");

                wRepo.save(w1);
                wRepo.save(w2);
                System.out.println("GK Warehouse-Daten geladen!");
            }

            // 2. Erweitert: 30 Käufe anlegen
            if (pRepo.count() == 0) {
                Warehouse w = wRepo.findById("001").orElse(null);
                if (w != null && !w.getProductData().isEmpty()) {
                    Product p = w.getProductData().get(0);
                    List<Purchase> purchases = new ArrayList<>();
                    for (int i = 1; i <= 30; i++) {
                        Purchase sale = new Purchase();
                        sale.setPurchaseDateTime("2026-03-18 10:00:" + (i < 10 ? "0" + i : i));
                        sale.setAmount(i + 2);
                        sale.setWarehouse(w);
                        sale.setProduct(p);
                        purchases.add(sale);
                    }
                    pRepo.saveAll(purchases);
                    System.out.println("30 EK Purchase Records geladen!");
                }
            }
        };
    }
}