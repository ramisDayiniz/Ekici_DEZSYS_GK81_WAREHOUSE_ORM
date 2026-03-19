package org.example;

import org.example.Entity.Purchase;
import org.example.repo.PurchaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
