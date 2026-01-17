package com.dulcefina.service;

import com.dulcefina.entity.Product;
import com.dulcefina.entity.Supplier;
import com.dulcefina.repository.ProductRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StockNotificationService {

    private final ProductRepository productRepository;
    private final EmailService emailService;

    public StockNotificationService(ProductRepository productRepository, EmailService emailService) {
        this.productRepository = productRepository;
        this.emailService = emailService;
    }

    @Scheduled(cron = "0 50 9 * * ?", zone = "America/Lima")
    @Transactional
    public void checkAndSendLowStockAlerts() {
        System.out.println("Ejecutando tarea programada: Verificación de stock bajo (9:50 AM America/Lima)...");

        List<Product> lowStockProducts = productRepository.findProductsWithLowStockNotNotified();

        List<Product> productsWithSupplier = lowStockProducts.stream()
                .filter(p -> p.getSupplier() != null && p.getSupplier().getEmail() != null)
                .toList();

        if (productsWithSupplier.isEmpty()) {
            System.out.println("No hay productos nuevos con stock bajo para notificar.");
            return;
        }

        Map<Supplier, List<Product>> productsBySupplier = productsWithSupplier.stream()
                .collect(Collectors.groupingBy(Product::getSupplier));

        for (Map.Entry<Supplier, List<Product>> entry : productsBySupplier.entrySet()) {
            Supplier supplier = entry.getKey();
            List<Product> productsToRestock = entry.getValue();

            try {
                emailService.sendRestockRequest(supplier, productsToRestock);

                for (Product p : productsToRestock) {
                    p.setLowStockNotified(true);
                }
                productRepository.saveAll(productsToRestock);

            } catch (Exception e) {
                System.err.println("Error al enviar correo de reposición a " + supplier.getEmail() + ": " + e.getMessage());
            }
        }
    }
}