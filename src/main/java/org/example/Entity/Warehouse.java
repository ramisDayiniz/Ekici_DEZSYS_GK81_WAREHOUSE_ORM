package org.example.Entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Warehouse {
    @Id
    private String warehouseID;

    private String warehouseName;
    private String warehouseCity;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "warehouse_id")
    private List<Product> productData;

    public String getWarehouseID() { return warehouseID; }
    public void setWarehouseID(String warehouseID) { this.warehouseID = warehouseID; }

    public String getWarehouseName() { return warehouseName; }
    public void setWarehouseName(String warehouseName) { this.warehouseName = warehouseName; }

    public String getWarehouseCity() { return warehouseCity; }
    public void setWarehouseCity(String warehouseCity) { this.warehouseCity = warehouseCity; }

    public List<Product> getProductData() { return productData; }
    public void setProductData(List<Product> productData) { this.productData = productData; }
}