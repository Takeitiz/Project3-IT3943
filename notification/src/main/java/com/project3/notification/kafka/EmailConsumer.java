package com.project3.notification.kafka;

import com.project3.notification.dto.AuthEmailMessageDto;
import com.project3.notification.dto.EmailLocalsDto;
import com.project3.notification.dto.OrderEmailMessageDto;
import com.project3.notification.email.EmailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailConsumer {

    private final EmailService emailService;

    @Value("${app.client.url}")
    private String clientUrl;

    @Value("${app.icon.url}")
    private String appIconUrl;

    @KafkaListener(topics = "auth-email-topic")
    public void consumeAuthEmailMessages(AuthEmailMessageDto authEmailMessageDto) throws MessagingException {

        EmailLocalsDto locals = EmailLocalsDto.builder()
                .appLink(clientUrl)
                .appIcon(appIconUrl)
                .username(authEmailMessageDto.getUsername())
                .verifyLink(authEmailMessageDto.getVerifyLink())
                .resetLink(authEmailMessageDto.getResetLink())
                .otp(authEmailMessageDto.getOtp())
                .build();

        emailService.sendEmail(authEmailMessageDto.getTemplateName(), authEmailMessageDto.getSubject(), authEmailMessageDto.getReceiverEmail(), locals);
    }

    @KafkaListener(topics = "order-email-topic")
    public void consumeOrderEmailMessages(OrderEmailMessageDto orderMessageDto) throws MessagingException {

        EmailLocalsDto locals = EmailLocalsDto.builder()
                .appLink(clientUrl)
                .appIcon(appIconUrl)
                .username(orderMessageDto.getUsername())
                .sender(orderMessageDto.getSender())
                .offerLink(orderMessageDto.getOfferLink())
                .amount(orderMessageDto.getAmount())
                .buyerUsername(orderMessageDto.getBuyerUsername())
                .sellerUsername(orderMessageDto.getSellerUsername())
                .title(orderMessageDto.getTitle())
                .description(orderMessageDto.getDescription())
                .deliveryDays(orderMessageDto.getDeliveryDays())
                .orderId(orderMessageDto.getOrderId())
                .orderDue(orderMessageDto.getOrderDue())
                .requirements(orderMessageDto.getRequirements())
                .orderUrl(orderMessageDto.getOrderUrl())
                .originalDate(orderMessageDto.getOriginalDate())
                .newDate(orderMessageDto.getNewDate())
                .reason(orderMessageDto.getReason())
                .subject(orderMessageDto.getSubject())
                .header(orderMessageDto.getHeader())
                .type(orderMessageDto.getType())
                .message(orderMessageDto.getMessage())
                .serviceFee(orderMessageDto.getServiceFee())
                .total(orderMessageDto.getTotal())
                .build();

        if (orderMessageDto.getTemplateName().equals("order-placed.html")) {
            emailService.sendEmail("order-placed.html", orderMessageDto.getTemplateSubject(), orderMessageDto.getReceiverEmail(), locals);
            emailService.sendEmail("order-receipt.html", orderMessageDto.getTemplateSubject(), orderMessageDto.getReceiverEmail(), locals);
        } else {
            emailService.sendEmail(orderMessageDto.getTemplateName(), orderMessageDto.getTemplateSubject(), orderMessageDto.getReceiverEmail(), locals);
        }
    }
}
