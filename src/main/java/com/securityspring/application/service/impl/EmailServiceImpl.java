package com.securityspring.application.service.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.Random;
import jakarta.servlet.http.HttpServletRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.securityspring.application.service.api.EmailServiceApi;
import com.securityspring.application.service.api.LogServiceApi;
import com.securityspring.domain.enums.StatusEnum;
import com.securityspring.domain.exception.CodeNotValidOrExpiredException;
import com.securityspring.domain.exception.UserNotFoundException;
import com.securityspring.domain.model.PasswordResetTokenEntity;
import com.securityspring.domain.model.UserEntity;
import com.securityspring.domain.port.PasswordResetTokenRepository;
import com.securityspring.domain.port.UserRepository;
import com.securityspring.infrastructure.adapters.mapper.UserMapper;
import com.securityspring.infrastructure.adapters.vo.UserVO;
import com.securityspring.infrastructure.config.ProjectProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailServiceApi {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailServiceImpl.class);

    private final ProjectProperties projectProperties;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final UserRepository userRepository;
    private final LogServiceApi logService;

    @Autowired
    public EmailServiceImpl(ProjectProperties projectProperties,
                            PasswordResetTokenRepository passwordResetTokenRepository,
                            UserRepository userRepository,
                            LogServiceApi logService) {
        this.projectProperties = projectProperties;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.userRepository = userRepository;
        this.logService = logService;
    }

    @Override
    public void sendEmail(final String email, final HttpServletRequest httpServletRequest) {
        LOGGER.info("Sending email");
        final UserEntity userEntity = this.userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Not found user with email: " + email));
        final String code = generateCode();

        final String message = "<p>Hello " + userEntity.getName() + ",</p>"
                + "<p>We received a request to verify your email address.</p>"
                + "<p>Your verification code is:</p>"
                + "<p style=\"font-size:18px; font-weight:bold;\">" + code + "</p>"
                + "<p><i>⚠ This code is valid for 15 minutes and can only be used once.</i></p>"
                + "<p>If you did not request this verification, please ignore this email.</p>"
                + "<p>Best regards,<br/>Login Project Team</p>";

        this.sendEmailViaBrevo(userEntity.getEmail(), "Your Email Verification Code", message);

        this.saveToken(code, userEntity);
        this.logService.setLog("EMAIL SENT", String.format("Email sent to %s", email), httpServletRequest);
        LOGGER.info("Email sent");
    }

    @Override
    public void sendEmail(final UserEntity userEntity, final HttpServletRequest httpServletRequest) throws UnsupportedEncodingException {
        LOGGER.info("Sending email");
        final String frontUrl = this.projectProperties.getProperty("front-end.url");
        final String verificationLink = frontUrl + "/#/validate-code-email?flow=email-verification&email="
                + URLEncoder.encode(userEntity.getEmail(), "UTF-8");
        final String code = generateCode();

        String message = "<p>Hello " + userEntity.getName() + ",</p>"
                + "<p>Thank you for signing up on <b>Login project</b>!</p>"
                + "<p>To activate your account, please click the button below:</p>"
                + "<p><a href=\"" + verificationLink + "\" "
                + "style=\"display:inline-block;padding:10px 20px;margin:10px 0;"
                + "background:#4CAF50;color:white;text-decoration:none;border-radius:5px;\">"
                + "Activate Account</a></p>"
                + "<p>And enter this code manually: <b>" + code + "</b></p>"
                + "<p><i>⚠ This code is valid for 15 minutes and can only be used once.</i></p>"
                + "<p>If you did not request this registration, please ignore this email.</p>"
                + "<p>Best regards,<br/>Login Project Team</p>";

        this.sendEmailViaBrevo(userEntity.getEmail(), "Verify Your Email to Activate Your Account", message);

        this.saveToken(code, userEntity);
        this.logService.setLog("EMAIL SENT", String.format("Email to activate account sent to %s", userEntity.getEmail()),
                httpServletRequest);
        LOGGER.info("Email sent");
    }

    private void sendEmailViaBrevo(String to, String subject, String htmlContent) {
        try {
            String apiKey = this.projectProperties.getProperty("brevo.api-key");
            String emailFrom = this.projectProperties.getProperty("email-service.from");
            HttpClient client = HttpClient.newHttpClient();

            String payload = """
        {
          "sender": {"name": "Login Project", "email": "%s"},
          "to": [{"email": "%s"}],
          "subject": "%s",
          "htmlContent": "%s"
        }
        """.formatted(emailFrom, to, subject, htmlContent.replace("\"", "\\\"").replace("\n", ""));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.brevo.com/v3/smtp/email"))
                    .header("accept", "application/json")
                    .header("api-key", apiKey)
                    .header("content-type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(payload))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                LOGGER.info("Brevo email sent successfully to {}", to);
            } else {
                LOGGER.error("Failed to send Brevo email: {} - {}", response.statusCode(), response.body());
            }

        } catch (IOException | InterruptedException e) {
            LOGGER.error("Error sending email via Brevo", e);
        }
    }

    @Override
    public UserVO validateCode(final String code, final String email, final HttpServletRequest httpServletRequest) {
        LOGGER.info("Validating code");
        final UserEntity userEntity = this.userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Not found user with email: " + email));
        PasswordResetTokenEntity token = this.passwordResetTokenRepository.findValidToken(userEntity, code, StatusEnum.ACTIVE, LocalDateTime.now())
                .orElseThrow(() -> new CodeNotValidOrExpiredException("Code not valid or expired."));
        this.userRepository.save(userEntity);
        token.setStatus(StatusEnum.INACTIVE);
        this.passwordResetTokenRepository.save(token);
        this.logService.setLog("CODE VALIDATED", String.format("Code validate for email: %s", userEntity.getEmail()),
                httpServletRequest);
        return UserMapper.toVO(userEntity);
    }

    @Override
    public int deleteOldTokens() {
        return this.passwordResetTokenRepository.deleteOldTokens(LocalDateTime.now());
    }

    private void saveToken(final String code, final UserEntity userEntity) {
        final PasswordResetTokenEntity entity = new PasswordResetTokenEntity();
        entity.setCode(code);
        entity.setStatus(StatusEnum.ACTIVE);
        entity.setExpirationDate(LocalDateTime.now().plusMinutes(15L));
        entity.setUser(userEntity);
        this.passwordResetTokenRepository.save(entity);
    }

    public String generateCode() {
        final Random random = new Random();
        return String.valueOf(100000 + random.nextInt(900000));
    }
}
