package com.project3.auth.controller;

import com.project3.auth.dto.*;
import com.project3.auth.entity.Auth;
import com.project3.auth.kafka.AuthProducer;
import com.project3.auth.service.AuthService;
import com.project3.auth.service.CloudinaryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(path="/api/v1/auth", produces = {MediaType.APPLICATION_JSON_VALUE})
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final CloudinaryService cloudinaryService;
    private final AuthProducer authProducer;

    @PostMapping("/signup")
    public ResponseEntity<?> create(@Valid @RequestBody SignupDto signupDto) {
        if (authService.getUserByUsernameOrEmail(signupDto.getUsername(), signupDto.getEmail()) != null) {
            return ResponseEntity.badRequest().body("Invalid credentials. Email or Username', 'SignUp create() method error");
        }

        String profilePublicId = UUID.randomUUID().toString();
        Map uploadResult = cloudinaryService.upload(signupDto.getProfilePicture(), profilePublicId, true, true);

        if (uploadResult.get("public_id") == null) {
            ResponseEntity.badRequest().body("File upload error. Try again', 'SignUp create() method error");
        }

        String randomCharacters = generateRandomHexToken(20);

        Auth user = Auth.builder()
                .username(StringUtils.capitalize(signupDto.getUsername()))
                .email(signupDto.getEmail().toLowerCase())
                .profilePublicId(profilePublicId)
                .password(authService.hashPassword(signupDto.getPassword()))
                .country(signupDto.getCountry())
                .profilePicture((String) uploadResult.get("secure_url"))
                .emailVerificationToken(randomCharacters)
                .emailVerified(0)
                .createdAt(LocalDateTime.now())
                .browserName(signupDto.getBrowserName())
                .deviceType(signupDto.getDeviceType())
                .build();

        Auth savedUser = authService.createAuthUser(user);
        String verificationLink = "http://localhost:3000/confirm_email?v_token=" + randomCharacters;

        AuthEmailMessageDto messageDetails = AuthEmailMessageDto.builder()
                .receiverEmail(savedUser.getEmail())
                .verifyLink(verificationLink)
                .templateName("verify-email.html")
                .subject("Verify Email")
                .build();

        authProducer.sendAuthEmailTopic(messageDetails);

        String userJWT = authService.signToken(savedUser.getId(), savedUser.getEmail(), savedUser.getUsername());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new SignupResponseDto(
                        "User created successfully",
                        savedUser,
                        userJWT
                ));
    }

    @PostMapping("/signin")
    public ResponseEntity<?> read(@Valid @RequestBody SigninDto signinDto) {

        Auth existingUser = isEmail(signinDto.getUsername())
                ? authService.getUserByEmail(signinDto.getUsername())
                : authService.getUserByUsername(signinDto.getUsername());

        if (existingUser == null) {
            return ResponseEntity.badRequest().body("Invalid credentials', 'SignIn read() method error");
        }

        if (!authService.comparePassword(signinDto.getPassword(), existingUser.getPassword())) {
            return ResponseEntity.badRequest().body("Invalid credentials', 'SignIn read() method error");
        }

        String userJWT = authService.signToken(existingUser.getId(), existingUser.getEmail(), existingUser.getUsername());
        existingUser.setPassword(null);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SigninResponseDto("User login successfully", existingUser, userJWT, "", ""));
    }

    @PutMapping("/verify-email")
    public ResponseEntity<?> update(@RequestBody VerifyEmailRequestDto verifyEmailRequestDto) {
        Auth existingUser = authService.getAuthUserByVerificationToken(verifyEmailRequestDto.getToken());
        if (existingUser == null) {
            return ResponseEntity.badRequest().body("Verification token is either invalid or is already used.', 'VerifyEmail update() method error");
        }
        authService.updateVerifyEmailField(existingUser.getId(), 1, null);
        Auth user = authService.getAuthUserById(existingUser.getId());
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto("Email verified successfully.", user));
    }

    @PutMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody EmailDto emailDto) {
        Auth existingUser = authService.getUserByEmail(emailDto.getEmail());
        if (existingUser == null) {
            return ResponseEntity.badRequest().body("Invalid credentials', 'Password forgotPassword() method error");
        }
        String randomCharacters = generateRandomHexToken(20);
        LocalDateTime expiryTime = LocalDateTime.now().plusHours(1);
        authService.updatePasswordToken(existingUser.getId(), randomCharacters, expiryTime);
        String resetLink = "http://localhost:3000/reset_password?token="+randomCharacters;
        AuthEmailMessageDto messageDetails = AuthEmailMessageDto.builder()
                .receiverEmail(existingUser.getEmail())
                .resetLink(resetLink)
                .username(existingUser.getUsername())
                .templateName("forgot-password.html")
                .subject("Forgot Password")
                .build();

        authProducer.sendAuthEmailTopic(messageDetails);

        return ResponseEntity.status(HttpStatus.OK).body(new MessageResponseDto("Password reset email sent."));
    }

    @PutMapping("/reset-password/{token}")
    public ResponseEntity<?> resetPassword(@PathVariable String token, @Valid @RequestBody PasswordDto passwordDto) {
        if (!passwordDto.getPassword().equals(passwordDto.getConfirmPassword())) {
            return ResponseEntity.badRequest().body("Passwords do not match', 'Password resetPassword() method error");
        }
        Auth existingUser = authService.getAuthUserByPasswordToken(token);
        if (existingUser == null) {
            return ResponseEntity.badRequest().body("Reset token has expired', 'Password resetPassword() method error");
        }
        String hashedPassword = authService.hashPassword(passwordDto.getPassword());
        authService.updatePassword(existingUser.getId(), hashedPassword);
        AuthEmailMessageDto messageDetails = AuthEmailMessageDto.builder()
                .receiverEmail(existingUser.getEmail())
                .username(existingUser.getUsername())
                .templateName("reset-password-success.html")
                .subject("Reset Password Success")
                .build();

        authProducer.sendAuthEmailTopic(messageDetails);
        return ResponseEntity.status(HttpStatus.OK).body(new MessageResponseDto("Password successfully updated."));
    }

    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordDto changePasswordDto) {
        Auth existingUser = authService.getUserByUsername(changePasswordDto.getUsername());
        if (existingUser == null) {
            return ResponseEntity.badRequest().body("Invalid password', 'Password changePassword() method error");
        }
        String hashedPassword = authService.hashPassword(changePasswordDto.getNewPassword());
        authService.updatePassword(existingUser.getId(), hashedPassword);
        AuthEmailMessageDto messageDetails = AuthEmailMessageDto.builder()
                .receiverEmail(existingUser.getEmail())
                .username(existingUser.getUsername())
                .templateName("reset-password-success.html")
                .subject("Reset Password Success")
                .build();

        authProducer.sendAuthEmailTopic(messageDetails);
        return ResponseEntity.status(HttpStatus.OK).body(new MessageResponseDto("Password successfully updated."));
    }

    @GetMapping("/currentuser/{username}")
    public ResponseEntity<?> getCurrentUser(@PathVariable String username) {
        Auth user = null;
        Auth existingUser = authService.getUserByUsername(username);
        if (existingUser != null) {
            user = existingUser;
        }
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto("Authenticated user", user));
    }

    @PostMapping("/resend-email")
    public ResponseEntity<?> resendEmail(@RequestBody ResendEmailDto resendEmailDto) {
        Auth existingUser = authService.getUserByEmail(resendEmailDto.getEmail());
        if (existingUser == null) {
            return ResponseEntity.badRequest().body("Email is invalid', 'CurrentUser resentEmail() method error");
        }
        String randomCharacters = generateRandomHexToken(20);
        String verificationLink = "http://localhost:3000/confirm_email?v_token="+randomCharacters;
        authService.updateVerifyEmailField(resendEmailDto.getUserId(), 0, randomCharacters);
        AuthEmailMessageDto messageDetails = AuthEmailMessageDto.builder()
                .receiverEmail(existingUser.getEmail().toLowerCase())
                .verifyLink(verificationLink)
                .templateName("verify-email.html")
                .subject("Verify Email")
                .build();
        authProducer.sendAuthEmailTopic(messageDetails);
        Auth updatedUser = authService.getAuthUserById(existingUser.getId());
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto("Email verification sent", updatedUser));
    }

    @GetMapping("/refresh-token/{username}")
    public ResponseEntity<?> refreshToken(@PathVariable String username) {
        Auth existingUser = authService.getUserByUsername(username);
        existingUser.setPassword(null);
        String userJWT = authService.signToken(existingUser.getId(), existingUser.getEmail(), existingUser.getUsername());
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SignupResponseDto(
                        "Refresh token",
                        existingUser,
                        userJWT
                ));
    }

    private String generateRandomHexToken(int bytes) {
        SecureRandom secureRandom = new SecureRandom();
        byte[] token = new byte[bytes];
        secureRandom.nextBytes(token);
        return HexFormat.of().formatHex(token);
    }

    private boolean isEmail(String value) {
        return EmailValidator.getInstance().isValid(value);
    }
}
