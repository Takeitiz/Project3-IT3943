package com.project3.auth.service;

import com.mongodb.client.MongoClient;
import com.project3.auth.dto.AuthBuyerMessageDetailsDto;
import com.project3.auth.entity.Auth;
import com.project3.auth.kafka.AuthProducer;
import com.project3.auth.repository.AuthRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static ch.qos.logback.core.util.StringUtil.capitalizeFirstLetter;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final AuthRepository authRepository;
    private final AuthProducer authProducer;
    private final MongoTemplate mongoTemplate;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    public Auth createAuthUser(Auth auth) {
        Auth savedAuth = authRepository.save(auth);

        AuthBuyerMessageDetailsDto messageDetails = new AuthBuyerMessageDetailsDto();
        messageDetails.setUsername(savedAuth.getUsername());
        messageDetails.setEmail(savedAuth.getEmail());
        messageDetails.setProfilePicture(savedAuth.getProfilePicture());
        messageDetails.setCountry(savedAuth.getCountry());
        messageDetails.setCreatedAt(LocalDateTime.now());
        messageDetails.setType("auth");

        authProducer.sendUserBuyerTopic(messageDetails);

        savedAuth.setPassword(null);
        return savedAuth;
    }

    public Auth getAuthUserById(String id) {
        Auth user = authRepository.findById(id).orElse(null);
        assert user != null;
        user.setPassword(null);
        return user;
    }

    public Auth getUserByUsernameOrEmail(String username, String email) {
        Query query = new Query();
        query.addCriteria(new Criteria().orOperator(
                Criteria.where("username").is(capitalizeFirstLetter(username)),
                Criteria.where("email").is(email.toLowerCase())
        ));
        return mongoTemplate.findOne(query, Auth.class);
    }

    public Auth getUserByUsername(String username) {
        Query query = new Query();
        query.addCriteria(Criteria.where("username").is(capitalizeFirstLetter(username)));
        return mongoTemplate.findOne(query, Auth.class);
    }

    public Auth getUserByEmail(String email) {
        Query query = new Query();
        query.addCriteria(Criteria.where("email").is(email.toLowerCase()));
        return mongoTemplate.findOne(query, Auth.class);
    }

    public Auth getAuthUserByVerificationToken(String token) {
        Query query = new Query();
        query.addCriteria(Criteria.where("emailVerificationToken").is(token));
        Auth auth = mongoTemplate.findOne(query, Auth.class);
        System.out.println(auth);
        assert auth != null;
        auth.setPassword(null);
        return auth;
    }

    public Auth getAuthUserByPasswordToken(String token) {
        Query query = new Query();
        query.addCriteria(Criteria.where("passwordResetToken").is(token)
                .and("passwordResetExpires").gt(LocalDateTime.now()));
        return mongoTemplate.findOne(query, Auth.class);
    }

    public Auth getAuthUserByOTP(String otp) {
        Query query = new Query();
        query.addCriteria(Criteria.where("otp").is(otp)
                .and("otpExpiration").gt(LocalDateTime.now()));
        return mongoTemplate.findOne(query, Auth.class);
    }

    public void updateVerifyEmailField(String authId, int emailVerified, String emailVerificationToken) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(authId));

        Update update = new Update();
        update.set("emailVerified", emailVerified);
        if (emailVerificationToken != null) {
            update.set("emailVerificationToken", emailVerificationToken);
        }
        FindAndModifyOptions options = new FindAndModifyOptions().returnNew(true);
        mongoTemplate.findAndModify(query, update, options, Auth.class);
    }

    public void updatePasswordToken(String authId, String token, LocalDateTime tokenExpiration) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(authId));

        Update update = new Update();
        update.set("passwordResetToken", token);
        update.set("passwordResetExpires", tokenExpiration);
        FindAndModifyOptions options = new FindAndModifyOptions().returnNew(true);
        mongoTemplate.findAndModify(query, update, options, Auth.class);
    }

    public void updatePassword(String authId, String password) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(authId));

        Update update = new Update();
        update.set("password", password);
        update.set("passwordResetToken", "");
        update.set("passwordResetExpires", LocalDateTime.now());

        FindAndModifyOptions options = new FindAndModifyOptions().returnNew(true);
        mongoTemplate.findAndModify(query, update, options, Auth.class);
    }

    public void updateUserOTP(String authId, String otp, LocalDateTime otpExpiration, String browserName, String deviceType) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(authId));

        Update update = new Update();
        update.set("otp", otp);
        update.set("otpExpiration", otpExpiration);

        if (browserName != null && !browserName.isEmpty()) {
            update.set("browserName", browserName);
        }
        if (deviceType != null && !deviceType.isEmpty()) {
            update.set("deviceType", deviceType);
        }
        FindAndModifyOptions options = new FindAndModifyOptions().returnNew(true);

        mongoTemplate.findAndModify(query, update, options, Auth.class);

    }

    public String signToken(String id, String email, String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", id);
        claims.put("email", email);
        claims.put("username", username);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS256, jwtSecret)
                .compact();
    }

    public String hashPassword(String password) {
        return encoder.encode(password);
    }

    public boolean comparePassword(String rawPassword, String hashedPassword) {
        return encoder.matches(rawPassword, hashedPassword);
    }
}
