# MidEng GK8.1 Spring Data and ORM [GK/EK] - 4h

## DEZSYS_GK81_WAREHOUSE_ORM

##### Verfasser: Ramis Ekici

##### Datum: 10.03.2026

# Einführung

In dieser Übung wird die Interaktion zwischen einer Programmiersprache 
(Java/Spring Boot) und einer relationalen Datenbankschicht (MySQL) mittels Object-Relational 
Mapping (ORM) demonstriert.

Das Ziel ist es, ein Verständnis für das Spring Data JPA Modell zu entwickeln. Dabei wird zunächst
ein einfaches Datenbankschema erstellt und anschließend in ein komplexeres Data-Warehouse-Modell 
überführt. Dieses Modell bildet eine 1:n Beziehung zwischen Lagerstandorten (Warehouses) und den 
darin gelagerten Produkten (Products) ab, um die persistente Speicherung und Verwaltung von 
Warenbeständen zu ermöglichen.

# Vorbereitung

Zuerst muss das Repo von dem Professor geklont bzw. in ein anderen Repo kopiert werden.
Dafür das Repo vom Professor klonen und dann:

```editorconfig
git remote remove

git remote remove origin

git remote add origin https://github.com/ramisDayiniz/Ekici_DEZSYS_GK81_WAREHOUSE_ORM.git

git push -f --set-upstream origin main
```

Ein Docker mySql Container:

```editorconfig
docker run --name mysql-warehouse -e MYSQL_ROOT_PASSWORD=password -e MYSQL_DATABASE=db_example -p 3306:3306 -d mysql:8.0
```

Und in main/resources Ordner application.propperties, damit das Java Programm unser DAtenbank finden kann.

```editorconfig
spring.datasource.url=jdbc:mysql://localhost:3306/db_example
spring.datasource.username=root
spring.datasource.password=password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

Nun können wir mit eigentlichen Programmieren beginnen.

# GK

## Fragen

### **1. What is ORM and how is JPA used?**

**ORM (Object-Relational Mapping)** is a technique to connect object-oriented code (Java classes) to relational database tables (SQL). **JPA (Java Persistence API)** is the standard Java specification that defines how to manage this data. It allows you to save, update, and delete database records using Java objects instead of writing raw SQL queries.

### **2. What is the application.properties used for and where must it be stored?**

This file is used to configure your application's environment. In this project, it stores the **database connection details** (URL, username, password).

- **Location:** It must be stored in the `src/main/resources` folder.

### **3. Which annotations are frequently used for entity types?**

- `@Entity`: Marks a Java class as a database table.

- `@Id`: Defines the primary key of the table.

- `@GeneratedValue`: Tells the database to automatically generate the ID (e.g., auto-increment).

- `@OneToMany` / `@ManyToOne`: Defines the relationship between two tables (like Warehouse and Product). **Key point:** Every Entity class must have a **no-argument constructor** and an **ID**.

### **4. What methods do you need for CRUD operations?**

By using the `JpaRepository` interface, you get these standard methods automatically:

- **Create/Update:** `save(entity)`

- **Read:** `findById(id)` or `findAll()`

- **Delete:** `deleteById(id)` or `deleteAll()`

## Entity

```java
package org.example.Entity;

import jakarta.persistence.*;
import java.util.List;
@Entity
public class Warehouse {
    @Id // Wichtig: jakarta.persistence.Id nutzen!
    private String warehouseID; // z.B. "001"

    private String warehouseName;
    private String warehouseCity;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "warehouse_id") // Erzeugt einen Fremdschlüssel in der Produkt-Tabelle
    private List<Product> productData;

    // Getter und Setter
}
```

```java
package org.example.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Product {
    @Id // Wichtig: jakarta.persistence.Id nutzen!
    private String productID; // z.B. "00-443175"

    private String productName;
    private Integer productQuantity;

    // Getter und Setter
```

### Repository

```java
package org.example.Repository;
import org.example.Entity.Warehouse;
import org.springframework.data.repository.CrudRepository;

public interface WarehouseRepository extends CrudRepository<Warehouse, String> { }
```

### Controller

```java
@GetMapping(path="/warehouses")
    public @ResponseBody Iterable<Warehouse> getAllWarehouses() {
        return warehouseRepository.findAll();
    }
```

### application.properties

```editorconfig
spring.datasource.url=jdbc:mysql://localhost:3306/db_example
spring.datasource.username=root
spring.datasource.password=password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

### Ergebnis

![](C:\Users\ekici\AppData\Roaming\marktext\images\2026-03-10-11-31-24-image.png)

### Postman Test

![](C:\Users\ekici\AppData\Roaming\marktext\images\2026-03-18-08-54-53-image.png)

![](C:\Users\ekici\AppData\Roaming\marktext\images\2026-03-18-08-55-45-image.png)

![](C:\Users\ekici\AppData\Roaming\marktext\images\2026-03-18-09-08-53-image.png)

### Datanbank Test

![](C:\Users\ekici\AppData\Roaming\marktext\images\2026-03-18-08-56-22-image.png)



# EK

### Crudrepository

typische Methoden von dem **CrudRepository** Interface sind folgende:

- `findAll()`: Gibt alle Datensätze zurück.

- `findById(ID id)`: Sucht einen Datensatz anhand des Primärschlüssels.

- `findAllById(Iterable ids)`: Sucht mehrere Datensätze anhand einer Liste von IDs.

- `count()`: Gibt die Anzahl der Datensätze zurück.

- `existsById(ID id)`: Prüft, ob ein Datensatz existiert.

Für die speziellen Suchen nach `datawarehouseID` und `productID` müssen wir das Interface anpassen. Spring Data JPA ist so schlau, dass es SQL-Abfragen anhand von Methodennamen generieren kann (**Query Methods**).

```java
Optional<Warehouse> findByWarehouseID(String warehouseID);
Optional<Product> findByProductID(String productID);
```

### Purchase Klasse

Wir brauchen eine neue Entity `Purchase`, um Verkäufe zu tracken. Ein Kauf bezieht sich auf ein Produkt und ein Lager.

```java
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
```

### Daten initialisieren

```java

```







### Ergebnis

![](C:\Users\ekici\AppData\Roaming\marktext\images\2026-03-18-09-36-08-image.png)

![](C:\Users\ekici\AppData\Roaming\marktext\images\2026-03-18-09-37-19-image.png)

![](C:\Users\ekici\AppData\Roaming\marktext\images\2026-03-18-09-40-13-image.png)