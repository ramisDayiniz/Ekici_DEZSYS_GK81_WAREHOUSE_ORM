# MidEng GK8.1 Spring Data and ORM [GK/EK] - 4h

## DEZSYS_GK81_WAREHOUSE_ORM

##### Verfasser: Ramis Ekici

##### Datum: 10.03.2026

# Einführung

In dieser Übung wird die Interaktion zwischen einer Programmiersprache (Java/Spring Boot) und einer relationalen Datenbankschicht (MySQL) mittels Object-Relational Mapping (ORM) demonstriert.

Das Ziel ist es, ein Verständnis für das Spring Data JPA Modell zu entwickeln. Dabei wird zunächst ein einfaches Datenbankschema erstellt und anschließend in ein komplexeres Data-Warehouse-Modell überführt. Dieses Modell bildet eine 1:n Beziehung zwischen Lagerstandorten (Warehouses) und den darin gelagerten Produkten (Products) ab, um die persistente Speicherung und Verwaltung von 
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

##### **1. What is ORM and how is JPA used?**

**ORM (Object-Relational Mapping)** is a technique to connect object-oriented code (Java classes) to relational database tables (SQL). **JPA (Java Persistence API)** is the standard Java specification that defines how to manage this data. It allows you to save, update, and delete database records using Java objects instead of writing raw SQL queries.

##### **2. What is the application.properties used for and where must it be stored?**

This file is used to configure your application's environment. In this project, it stores the **database connection details** (URL, username, password).

- **Location:** It must be stored in the `src/main/resources` folder.

##### **3. Which annotations are frequently used for entity types?**

- `@Entity`: Marks a Java class as a database table.

- `@Id`: Defines the primary key of the table.

- `@GeneratedValue`: Tells the database to automatically generate the ID (e.g., auto-increment).

- `@OneToMany` / `@ManyToOne`: Defines the relationship between two tables (like Warehouse and Product). **Key point:** Every Entity class must have a **no-argument constructor** and an **ID**.

##### **4. What methods do you need for CRUD operations?**

By using the `JpaRepository` interface, you get these standard methods automatically:

- **Create/Update:** `save(entity)`

- **Read:** `findById(id)` or `findAll()`

- **Delete:** `deleteById(id)` or `deleteAll()`



### Entity

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
```

### Controller

```java
// EK: Einzelnes Produkt finden
	@GetMapping("/product/{pId}")
	public @ResponseBody Product getProduct(@PathVariable String pId) {
		return productRepository.findByProductID(pId).orElse(null);
	}

	// EK: Ein Warehouse updaten
	@PostMapping("/warehouse/update")
	public @ResponseBody String updateWarehouse(@RequestBody Warehouse warehouse) {
		// Da die warehouseID bereits existiert, überschreibt .save() den alten Datensatz
		warehouseRepository.save(warehouse);
		return "Warehouse updated";
	}
```

### Ergebnis

![](C:\Users\ekici\AppData\Roaming\marktext\images\2026-03-18-09-36-08-image.png)

![](C:\Users\ekici\AppData\Roaming\marktext\images\2026-03-18-09-37-19-image.png)

![](C:\Users\ekici\AppData\Roaming\marktext\images\2026-03-18-09-40-13-image.png)









# Vertiefung

### LLM

Für die Kommunikation zwischen dem Java Programm und LLM Modell brauchen wir ein dependency in build.gradle.

```editorconfig
implementation 'org.springframework.boot:spring-boot-starter-webflux' // Für den WebClient (Ollama Kommunikation)
```

Dann muss nicht sein, aber ist schön zu haben folgendes in application.propperties:

```editorconfig
# Zeigt detailliertere Fehler im Terminal
logging.level.org.springframework.web.client.RestTemplate=DEBUG
```

Und dann brauchen wir noch eine weitere Kontroller Klasse, die für LLM geeignet ist. Dabei müssen wir das LLM Daten eigeben, und Prompt eingeben. Am Ende wird dann die Vorhersage von dem LLM zurückgegeben.

```java
@RestController
public class LLMController {
    @Autowired
    private PurchaseRepository purchaseRepository;

    private final WebClient webClient = WebClient.create("http://localhost:11434");

    @GetMapping("/demo/ai/forecast")
    public String getForecast() {
        // 1. Daten aus DB holen
        List<Purchase> allPurchases = (List<Purchase>) purchaseRepository.findAll();

        // 2. Daten für die KI zusammenfassen (damit der Prompt nicht zu lang wird)
        // Wir gruppieren nach Lager und Produkt und summieren die Mengen
        String summary = allPurchases.stream()
                .collect(Collectors.groupingBy(
                        p -> "Lager " + p.getWarehouse().getWarehouseID() + " - " + p.getProduct().getProductName(),
                        Collectors.summingInt(Purchase::getAmount)
                )).toString();

        // 3. Prompt bauen
        String prompt = "Here is the sales summary for March: " + summary +
                ". Based on these numbers, provide a short sales forecast for April. " +
                "Be concise and professional.";

        // 4. Anfrage an Ollama senden
        Map<String, Object> requestBody = Map.of(
                "model", "llama3",
                "prompt", prompt,
                "stream", false
        );

        // 5. Daten senden
        Map response = webClient.post()
                .uri("/api/generate")
                // Die Daten einpacken in JSON
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        // der eigentliche Text der Vorhersage
        return (String) response.get("response");
    }
}
```



### Daten initialisieren

```java
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

                // WICHTIG: Hier die aktuelle Liste aus der DB holen
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
```





![](C:\Users\ekici\AppData\Roaming\marktext\images\2026-03-19-11-19-35-image.png)





![](C:\Users\ekici\AppData\Roaming\marktext\images\2026-03-19-11-19-57-image.png)