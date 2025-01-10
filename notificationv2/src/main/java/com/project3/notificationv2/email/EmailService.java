package com.project3.notificationv2.email;

import com.project3.notificationv2.dto.EmailLocalsDto;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Async
    public void sendEmail(String templateName, String subject, String receiverEmail, EmailLocalsDto emailLocalsDto) throws MessagingException {

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, UTF_8.name());
        messageHelper.setFrom("contact@project3.com");

        Context context = new Context();
        context.setVariable("sender", emailLocalsDto.getSender());
        context.setVariable("appLink", emailLocalsDto.getAppLink());
        context.setVariable("appIcon", emailLocalsDto.getAppIcon());
        context.setVariable("offerLink", emailLocalsDto.getOfferLink());
        context.setVariable("amount", emailLocalsDto.getAmount());
        context.setVariable("buyerUsername", emailLocalsDto.getBuyerUsername());
        context.setVariable("sellerUsername", emailLocalsDto.getSellerUsername());
        context.setVariable("title", emailLocalsDto.getTitle());
        context.setVariable("description", emailLocalsDto.getDescription());
        context.setVariable("deliveryDays", emailLocalsDto.getDeliveryDays());
        context.setVariable("orderId", emailLocalsDto.getOrderId());
        context.setVariable("orderDue", emailLocalsDto.getOrderDue());
        context.setVariable("requirements", emailLocalsDto.getRequirements());
        context.setVariable("orderUrl", emailLocalsDto.getOrderUrl());
        context.setVariable("originalDate", emailLocalsDto.getOriginalDate());
        context.setVariable("newDate", emailLocalsDto.getNewDate());
        context.setVariable("reason", emailLocalsDto.getReason());
        context.setVariable("subject", emailLocalsDto.getSubject());
        context.setVariable("header", emailLocalsDto.getHeader());
        context.setVariable("type", emailLocalsDto.getType());
        context.setVariable("message", emailLocalsDto.getMessage());
        context.setVariable("serviceFee", emailLocalsDto.getServiceFee());
        context.setVariable("total", emailLocalsDto.getTotal());
        context.setVariable("username", emailLocalsDto.getUsername());
        context.setVariable("verifyLink", emailLocalsDto.getVerifyLink());
        context.setVariable("resetLink", emailLocalsDto.getResetLink());
        context.setVariable("otp", emailLocalsDto.getOtp());
        context.setVariable("declineReason", emailLocalsDto.getDeclineReason());

        try {
            String htmlTemplate = templateEngine.process(templateName, context);
            messageHelper.setText(htmlTemplate, true);

            messageHelper.setTo(receiverEmail);
            mailSender.send(mimeMessage);
            log.info(String.format("INFO - Email successfully sent to %s with template %s ", receiverEmail, templateName));
        } catch (MessagingException e) {
            log.warn("WARNING - Cannot send Email to {} ", receiverEmail, e);
        }

    }
}
