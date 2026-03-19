package org.example.repo;

import org.example.Entity.Product;
import org.springframework.data.repository.CrudRepository;
import java.util.Optional;

public interface ProductRepository extends CrudRepository<Product, String> {
    // Hiermit kannst du direkt nach der Produkt-ID suchen
    Optional<Product> findByProductID(String productID);
}