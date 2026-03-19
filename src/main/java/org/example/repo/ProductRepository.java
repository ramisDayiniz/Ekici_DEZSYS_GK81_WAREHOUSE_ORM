package org.example.repo;

import org.example.Entity.Product;
import org.springframework.data.repository.CrudRepository;
import java.util.Optional;

public interface ProductRepository extends CrudRepository<Product, String> {
    Optional<Product> findByProductID(String productID);
}