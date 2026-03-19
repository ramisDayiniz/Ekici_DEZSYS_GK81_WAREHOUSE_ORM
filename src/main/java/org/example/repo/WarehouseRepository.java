package org.example.repo;
import org.example.Entity.Product;
import org.example.Entity.Warehouse;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface WarehouseRepository extends CrudRepository<Warehouse, String> {
    Optional<Warehouse> findByWarehouseID(String warehouseID);
}
