package org.example.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import org.springframework.data.annotation.Id;

@Entity
public class Purchase {
    @jakarta.persistence.Id // Das ist die richtige für MySQL/JPA
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String purchaseDateTime;
    private Integer amount;

    @ManyToOne
    private Warehouse warehouse;

    @ManyToOne
    private Product product;

    public String getPurchaseDateTime() {
        return purchaseDateTime;
    }

    public void setPurchaseDateTime(String purchaseDateTime) {
        this.purchaseDateTime = purchaseDateTime;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
