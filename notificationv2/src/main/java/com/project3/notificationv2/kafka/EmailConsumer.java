package com.project3.notificationv2.kafka;

import com.project3.notificationv2.dto.AuthEmailMessageDto;
import com.project3.notificationv2.dto.EmailLocalsDto;
import com.project3.notificationv2.dto.OrderMessageDto;
import com.project3.notificationv2.email.EmailService;
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
    public void consumeOrderEmailMessages(OrderMessageDto orderMessageDto) throws MessagingException {

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
                .declineReason(orderMessageDto.getDeclineReason())
                .build();

        if (orderMessageDto.getTemplateName().equals("order-placed.html")) {
            emailService.sendEmail("order-placed.html", orderMessageDto.getTemplateSubject(), orderMessageDto.getSellerEmail(), locals);
            emailService.sendEmail("order-receipt.html", orderMessageDto.getTemplateSubject(), orderMessageDto.getBuyerEmail(), locals);
        } else {
            emailService.sendEmail(orderMessageDto.getTemplateName(), orderMessageDto.getTemplateSubject(), orderMessageDto.getBuyerEmail(), locals);
        }
    }
}
