package org.example;

import org.example.Entity.Product;
import org.example.Entity.Warehouse;
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
    public CommandLineRunner initData(WarehouseRepository warehouseRepository) {
        return args -> {
            // Falls schon Daten da sind, nicht nochmal neu anlegen (optional)
            if (warehouseRepository.count() == 0) {
                // Warehouse 1
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

                // Warehouse 2
                Warehouse w2 = new Warehouse();
                w2.setWarehouseID("002");
                w2.setWarehouseName("Wien West");
                w2.setWarehouseCity("Wien");

                List<Product> products2 = new ArrayList<>();
                for (int i = 6; i <= 10; i++) {
                    Product p = new Product();
                    p.setProductID("PROD-00" + i);
                    p.setProductName("Snack Mix " + i);
                    p.setProductQuantity(50 * i);
                    products2.add(p);
                }
                w2.setProductData(products2);

                warehouseRepository.save(w1);
                warehouseRepository.save(w2);
                System.out.println("GK Warehouse-Daten erfolgreich geladen!");
            }
        };
    }
}