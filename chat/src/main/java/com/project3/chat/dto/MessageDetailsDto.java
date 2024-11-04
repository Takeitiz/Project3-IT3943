package com.project3.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageDetailsDto {

    private String sender;
    private String offerLink;
    private String amount;
    private String buyerUsername;
    private String sellerUsername;
    private String title;
    private String description;
    private String deliveryDays;
    private String template;

    public MessageDetailsDto(String sender, String amount, String buyerUsername, String sellerUsername, String title, String deliveryDays, String description, String template) {
        this.sender = sender;
        this.amount = amount;
        this.buyerUsername = buyerUsername;
        this.sellerUsername = sellerUsername;
        this.title = title;
        this.deliveryDays = deliveryDays;
        this.description = description;
        this.template = template;
    }
}
