package com.securityspring.application.service.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.Random;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import com.securityspring.application.service.api.EmailServiceApi;
import com.securityspring.domain.enums.StatusEnum;
import com.securityspring.domain.exception.CodeNotValidOrExpiredException;
import com.securityspring.domain.exception.UserNotFoundException;
import com.securityspring.domain.model.PasswordResetTokenEntity;
import com.securityspring.domain.model.UserEntity;
import com.securityspring.domain.port.PasswordResetTokenRepository;
import com.securityspring.domain.port.UserRepository;
import com.securityspring.infrastructure.config.ProjectProperties;
import com.securityspring.infrastructure.config.TokenJwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailServiceApi {

    @Autowired
    private JavaMailSender mailSender;

    private final ProjectProperties projectProperties;

    private final TokenJwtUtil tokenJwtUtil;

    private final PasswordResetTokenRepository passwordResetTokenRepository;

    static final Logger LOGGER = LoggerFactory.getLogger(EmailServiceImpl.class);

    private final UserRepository userRepository;

    @Autowired
    public EmailServiceImpl(final ProjectProperties projectProperties, final TokenJwtUtil tokenJwtUtil,
                            final PasswordResetTokenRepository repository,
                            final UserRepository userRepository) {
        this.projectProperties = projectProperties;
        this.tokenJwtUtil = tokenJwtUtil;
        this.passwordResetTokenRepository = repository;
        this.userRepository = userRepository;
    }

    @Override
    public void sendEmail(final String email,
                          final HttpServletRequest httpServletRequest) {
        LOGGER.info("Sending email");
        final UserEntity userEntity = this.userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Not found user with email: " + email));
        final String code = generateCode();
        final SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(this.projectProperties.getProperty("email-service.from"));
        message.setTo(email);
        message.setSubject("");
        message.setText(code);
        this.mailSender.send(message);
        this.saveToken(code, userEntity);
        //TODO LOG
        LOGGER.info("Email sent");
    }

    @Override
    public void sendEmail(final UserEntity userEntity) throws MessagingException, UnsupportedEncodingException {
        LOGGER.info("Sending email");
        final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
        final MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
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
        helper.setTo(userEntity.getEmail());
        helper.setSubject("Verify Your Email to Activate Your Account");
        helper.setText(message, true);
        helper.setFrom(this.projectProperties.getProperty("email-service.from"));

        this.mailSender.send(mimeMessage);
        this.saveToken(code, userEntity);
        //todo log
        LOGGER.info("Email sent");
    }

    @Override
    public UserEntity validateCode(final String code,
                             final String email,
                                   final HttpServletRequest httpServletRequest) {
        LOGGER.info("Validating code");
        final UserEntity userEntity = this.userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Not found user with email: " + email));
        PasswordResetTokenEntity token = this.passwordResetTokenRepository.findValidToken(userEntity, code, StatusEnum.ACTIVE, LocalDateTime.now())
                .orElseThrow(() -> new CodeNotValidOrExpiredException("Code not valid or expired."));
        this.userRepository.save(userEntity);
        token.setStatus(StatusEnum.INACTIVE);
        this.passwordResetTokenRepository.save(token);
        return userEntity;
    }

    private void saveToken(final String code, final UserEntity userEntity) {
        final PasswordResetTokenEntity entity = new PasswordResetTokenEntity();
        entity.setCode(code);
        entity.setStatus(StatusEnum.ACTIVE);
        entity.setExpirationDate(LocalDateTime.now().plusMinutes(15L));
        entity.setUser(userEntity);
        this.passwordResetTokenRepository.save(entity);
        // TODO LOG
    }

    public String generateCode() {
        final Random random = new Random();
        return String.valueOf(100000 + random.nextInt(900000));
    }
}
