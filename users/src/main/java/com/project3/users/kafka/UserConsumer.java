package com.project3.users.kafka;

import com.project3.users.dto.*;
import com.project3.users.entity.Buyer;
import com.project3.users.entity.Seller;
import com.project3.users.service.impl.BuyerServiceImpl;
import com.project3.users.service.impl.SellerServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserConsumer {

    private final BuyerServiceImpl buyerService;
    private final SellerServiceImpl sellerService;
    private final UserProducer userProducer;

    @KafkaListener(topics = "user-buyer-auth-topic")
    public void consumeUserBuyerAuthMessages(AuthBuyerMessageDetailsDto authBuyerMessageDetailsDto) throws MessagingException {
        String type = authBuyerMessageDetailsDto.getType();
        if (type.equals("auth")) {
            Buyer buyer = Buyer.builder()
                    .username(authBuyerMessageDetailsDto.getUsername())
                    .email(authBuyerMessageDetailsDto.getEmail())
                    .profilePicture(authBuyerMessageDetailsDto.getProfilePicture())
                    .country(authBuyerMessageDetailsDto.getCountry())
                    .purchasedGigs(new ArrayList<>())
                    .createdAt(authBuyerMessageDetailsDto.getCreatedAt())
                    .build();
            buyerService.createBuyer(buyer);
        }
    }

    @KafkaListener(topics = "user-buyer-gig-topic")
    public void consumeUserBuyerGigMessages(OrderMessageDto orderMessageDto) throws MessagingException {
        buyerService.updateBuyerPurchasedGigsProp(orderMessageDto);
    }

    @KafkaListener(topics = "user-seller-topic")
    public void consumeUserSellerMessages(OrderMessageDto orderMessageDto) throws MessagingException {
        String type = orderMessageDto.getType();
        if (type.equals("create-order")) {
            sellerService.updateSellerOngoingJobsProp(orderMessageDto.getSellerId(), orderMessageDto.getOngoingJobs());
        } else if (type.equals("approve-order")) {
            sellerService.updateSellerCompletedJobsProp(orderMessageDto);
        } else if (type.equals("cancel-order")) {
            sellerService.updateSellerCancelledJobsProp(orderMessageDto.getSellerId());
        }
    }

    @KafkaListener(topics = "update-gig-count-topic")
    public void consumeUpdateGigCountMessages(UpdateGigCountMessageDto updateGigCountMessageDto) throws MessagingException {
        sellerService.updateTotalGigsCount(updateGigCountMessageDto.getGigSellerId(), updateGigCountMessageDto.getCount());
    }

    @KafkaListener(topics = "jobber-review-user-topic")
    public void consumeReviewMessages(ReviewMessageDetailsDto reviewMessageDetails) throws MessagingException {
        String type = reviewMessageDetails.getType();
        if (type.equals("buyer-review")) {
            sellerService.updateSellerReview(reviewMessageDetails);
            userProducer.sendUpdateGigTopic(reviewMessageDetails);
        }
    }

    @KafkaListener(topics = "get-sellers-topic")
    public void consumeGetSellersMessages(Integer count) throws MessagingException {
        List<Seller> sellers = sellerService.getRandomSellers(count);
        SeedMessageDto seedMessageDto = new SeedMessageDto();
        seedMessageDto.setSellers(sellers);
        seedMessageDto.setCount(count);
        userProducer.sendSeedGigTopic(seedMessageDto);
    }

}
