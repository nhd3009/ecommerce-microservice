package com.nhd.notification_service.service;

import java.util.HashMap;
import java.util.Map;

import com.nhd.commonlib.event.order_notification.OrderNotificationEvent;
import com.nhd.commonlib.event.order_notification.OrderReturnApprovedEvent;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;


import freemarker.template.Configuration;
import freemarker.template.Template;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    private final Configuration freemarkerConfig;

    public void sendOrderEmail(OrderNotificationEvent event) {
        try {
            Map<String, Object> model = new HashMap<>();
            model.put("orderId", event.getOrderId());
            model.put("message", event.getMessage());
            model.put("email", event.getEmail());
            model.put("status", event.getStatus());
            model.put("items", event.getItems());
            model.put("totalAmount", event.getTotalAmount());

            Template template = freemarkerConfig.getTemplate("order-template.html");
            String htmlBody = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom("no-reply@frieren.com", "Frieren E-commerce Team");
            helper.setTo(event.getEmail());
            helper.setSubject(event.getStatus().equals("PENDING") ? "Your order #" + event.getOrderId() + " has been placed successfully!" 
                                                        : "Your order #" + event.getOrderId() + " from Frieren E-commerce is " + event.getStatus());
            helper.setText(htmlBody, true);

            mailSender.send(message);
            log.info("[MAIL SENT] to {}", event.getEmail());
        } catch (Exception e) {
            log.error("[MAIL ERROR] Failed to send to {}: {}", event.getEmail(), e.getMessage());
        }
    }

    public void sendOrderReturnApprovedEmail(OrderReturnApprovedEvent event){
        try {
            Map<String, Object> model = new HashMap<>();
            model.put("orderId", event.getOrderId());
            model.put("returnId", event.getReturnId());
            model.put("productName", event.getProductName());
            model.put("quantity", event.getQuantity());
            model.put("refundAmount", event.getRefundAmount());
            model.put("refundMethod", event.getRefundMethod());
            model.put("receiver", event.getReceiver());
            model.put("note", event.getNote());
            model.put("approvedAt", event.getApprovedAt());

            Template template = freemarkerConfig.getTemplate("order-return-approved.html");
            String htmlBody = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("no-reply@frieren.com", "Frieren E-commerce Team");
            helper.setTo(event.getEmail());
            helper.setSubject("Your return request has been approved – Order #" + event.getOrderId());
            helper.setText(htmlBody, true);

            mailSender.send(message);
            log.info("[RETURN APPROVED MAIL SENT] to {}", event.getEmail());

        } catch (Exception e) {
            log.error("[RETURN APPROVED MAIL ERROR] Failed to send to {}: {}", event.getEmail(), e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
