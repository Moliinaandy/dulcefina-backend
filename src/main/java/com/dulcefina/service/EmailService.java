package com.dulcefina.service;

import com.dulcefina.entity.Order;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.springframework.scheduling.annotation.Async;
import com.dulcefina.entity.Product;
import com.dulcefina.entity.Supplier;
import java.util.List;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendVerificationCode(String toEmail, String code) {
        MimeMessage message = emailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Tu Código de Verificación para Dulcefina");


            Context context = new Context();
            context.setVariable("verificationCode", code); // The ${verificationCode} in HTML
            context.setVariable("currentYear", java.time.Year.now().getValue()); // The ${currentYear}

            String htmlContent = templateEngine.process("email/verification-email", context);

            helper.setText(htmlContent, true);

            ClassPathResource logoResource = new ClassPathResource("static/images/logo-dulcefina.png");
            helper.addInline("logoDulcefina", logoResource);

            emailSender.send(message);
            System.out.println("Correo HTML (Thymeleaf) de verificación enviado a " + toEmail);

        } catch (MessagingException e) {
            System.err.println("Error al construir el correo HTML (Thymeleaf) para " + toEmail + ": " + e.getMessage());
            throw new RuntimeException("Error al construir el correo HTML.", e);
        } catch (MailException exception) {
            System.err.println("Error al enviar correo HTML (Thymeleaf) a " + toEmail + ": " + exception.getMessage());
            throw new RuntimeException("Error al enviar el correo de verificación.", exception);
        }
    }

    @Async
    public void sendOrderConfirmation(String toEmail, Order order) {
        MimeMessage message = emailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Confirmación de Pedido Dulcefina #" + order.getOrderId());

            Context context = new Context();
            context.setVariable("order", order);
            context.setVariable("customerName", order.getCustomerName());
            context.setVariable("currentYear", java.time.Year.now().getValue());

            String htmlContent = templateEngine.process("email/order-confirmation", context);

            helper.setText(htmlContent, true);

            ClassPathResource logoResource = new ClassPathResource("static/images/logo-dulcefina.png");
            helper.addInline("logoDulcefina", logoResource);

            emailSender.send(message);
            System.out.println("Correo de confirmación de pedido #" + order.getOrderId() + " enviado a " + toEmail);

        } catch (MessagingException e) {
            System.err.println("Error al construir el correo de confirmación para " + toEmail + ": " + e.getMessage());
            throw new RuntimeException("Error al construir el correo de confirmación.", e);
        } catch (MailException exception) {
            System.err.println("Error al enviar correo de confirmación a " + toEmail + ": " + exception.getMessage());
            throw new RuntimeException("Error al enviar el correo de confirmación.", exception);
        } catch (Exception e) {
            System.err.println("FALLO ASÍNCRONO: No se pudo enviar el correo de confirmación para el pedido #" + order.getOrderId() + ": " + e.getMessage());
        }
    }

    @Async
    public void sendRestockRequest(Supplier supplier, List<Product> products) {
        MimeMessage message = emailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(supplier.getEmail());
            helper.setSubject("Solicitud de Reposición de Stock - Dulcefina");

            Context context = new Context();
            context.setVariable("supplierName", supplier.getName());
            context.setVariable("products", products);
            context.setVariable("currentYear", java.time.Year.now().getValue());

            String htmlContent = templateEngine.process("email/order-restock", context);

            helper.setText(htmlContent, true);

            ClassPathResource logoResource = new ClassPathResource("static/images/logo-dulcefina.png");
            helper.addInline("logoDulcefina", logoResource);

            emailSender.send(message);
            System.out.println("Correo de reposición de stock enviado a " + supplier.getEmail());

        } catch (Exception e) {
            System.err.println("FALLO ASÍNCRONO: No se pudo enviar correo de reposición a " + supplier.getEmail() + ": " + e.getMessage());
        }
    }

    @Async
    public void sendImmediateLowStockAlert(Product product) {
        if (product.getSupplier() == null || product.getSupplier().getEmail() == null) {
            System.err.println("ALERTA DE STOCK: Producto " + product.getName() +
                    " tiene stock bajo pero no tiene proveedor o email asignado.");
            return;
        }

        Supplier supplier = product.getSupplier();
        MimeMessage message = emailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(supplier.getEmail());
            helper.setSubject("ALERTA INMEDIATA: Stock Bajo de " + product.getName() + " - Dulcefina");

            Context context = new Context();
            context.setVariable("supplierName", supplier.getName());
            context.setVariable("product", product);
            context.setVariable("currentYear", java.time.Year.now().getValue());

            String htmlContent = templateEngine.process("email/immediate-low-stock", context);

            helper.setText(htmlContent, true);

            ClassPathResource logoResource = new ClassPathResource("static/images/logo-dulcefina.png");
            helper.addInline("logoDulcefina", logoResource);

            emailSender.send(message);
            System.out.println("Correo de alerta de stock inmediato enviado a " + supplier.getEmail() +
                    " para el producto " + product.getName());

        } catch (Exception e) {
            System.err.println("FALLO ASÍNCRONO: No se pudo enviar correo de alerta inmediata a " +
                    supplier.getEmail() + ": " + e.getMessage());
        }
    }

}