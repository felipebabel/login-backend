package com.securityspring.application.service.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Random;
import jakarta.servlet.http.HttpServletRequest;
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
    public static final String PORTUGUESE = "PORTUGUESE";
    public static final String SPANISH = "SPANISH";
    public static final String GERMAN = "GERMAN";

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
    public void sendEmail(final String email,
                          final String lang,
                          final HttpServletRequest httpServletRequest) {
        LOGGER.info("Sending email");
        final UserEntity userEntity = this.userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Not found user with email: " + email));
        final String code = generateCode();
        final String message = buildEmailMessage(userEntity, code, lang);

        this.sendEmailViaBrevo(userEntity.getEmail(), getEmailSubject(lang), message);
        this.saveToken(code, userEntity);
        this.logService.setLog("EMAIL SENT", String.format("Email sent to %s", email), httpServletRequest);
        LOGGER.info("Email sent");
    }

    private String buildEmailMessage(UserEntity user, String code, String lang) {
        return switch (lang.toUpperCase()) {
            case PORTUGUESE -> """
            <p>Olá %s,</p>
            <p>Recebemos uma solicitação para verificar seu e-mail.</p>
            <p>Seu código de verificação é:</p>
            <p style="font-size:18px; font-weight:bold;">%s</p>
            <p><i>⚠ Este código é válido por 15 minutos e só pode ser usado uma vez.</i></p>
            <p>Se você não solicitou esta verificação, ignore este e-mail.</p>
            <p>Atenciosamente,<br/>Equipe Login Project</p>
            """.formatted(user.getName(), code);
            case SPANISH -> """
            <p>Hola %s,</p>
            <p>Hemos recibido una solicitud para verificar su correo electrónico.</p>
            <p>Su código de verificación es:</p>
            <p style="font-size:18px; font-weight:bold;">%s</p>
            <p><i>⚠ Este código es válido por 15 minutos y sólo puede usarse una vez.</i></p>
            <p>Si no solicitó esta verificación, ignore este correo electrónico.</p>
            <p>Saludos,<br/>Equipo Login Project</p>
            """.formatted(user.getName(), code);
            case GERMAN -> """
            <p>Hallo %s,</p>
            <p>Wir haben eine Anfrage zur Überprüfung Ihrer E-Mail-Adresse erhalten.</p>
            <p>Ihr Verifizierungscode lautet:</p>
            <p style="font-size:18px; font-weight:bold;">%s</p>
            <p><i>⚠ Dieser Code ist 15 Minuten gültig und kann nur einmal verwendet werden.</i></p>
            <p>Wenn Sie diese Anfrage nicht gestellt haben, ignorieren Sie bitte diese E-Mail.</p>
            <p>Mit freundlichen Grüßen,<br/>Login Project Team</p>
            """.formatted(user.getName(), code);
            default -> """
            <p>Hello %s,</p>
            <p>We received a request to verify your email address.</p>
            <p>Your verification code is:</p>
            <p style="font-size:18px; font-weight:bold;">%s</p>
            <p><i>⚠ This code is valid for 15 minutes and can only be used once.</i></p>
            <p>If you did not request this verification, please ignore this email.</p>
            <p>Best regards,<br/>Login Project Team</p>
            """.formatted(user.getName(), code);
        };
    }

    private String getEmailSubject(String lang) {
        return switch (lang.toUpperCase()) {
            case PORTUGUESE -> "Seu código de verificação de e-mail";
            case SPANISH -> "Su código de verificación de correo electrónico";
            case GERMAN -> "Ihr E-Mail-Verifizierungscode";
            default -> "Your Email Verification Code";
        };
    }


    @Override
    public void sendEmail(final UserEntity userEntity,
                          final String lang,
                          final HttpServletRequest httpServletRequest) throws UnsupportedEncodingException {
        LOGGER.info("Sending email");

        final String frontUrl = this.projectProperties.getProperty("front-end.url");
        final String verificationLink = frontUrl + "/login/#/validate-code-email?flow=email-verification&email="
                + URLEncoder.encode(userEntity.getEmail(), StandardCharsets.UTF_8);
        final String code = generateCode();

        final String message = buildActivationEmailMessage(userEntity, code, verificationLink, lang);
        final String subject = getActivationEmailSubject(lang);

        this.sendEmailViaBrevo(userEntity.getEmail(), subject, message);

        this.saveToken(code, userEntity);
        this.logService.setLog("EMAIL SENT",
                String.format("Activation email sent to %s", userEntity.getEmail()),
                httpServletRequest);
        LOGGER.info("Email sent");
    }

    private String buildActivationEmailMessage(UserEntity user, String code, String link, String lang) {
        return switch (lang.toUpperCase()) {
            case PORTUGUESE -> """
            <p>Olá %s,</p>
            <p>Obrigado por se cadastrar no <b>Login Project</b>!</p>
            <p>Para ativar sua conta, clique no botão abaixo:</p>
            <p><a href="%s" style="display:inline-block;padding:10px 20px;margin:10px 0;
            background:#4CAF50;color:white;text-decoration:none;border-radius:5px;">Ativar Conta</a></p>
            <p>E insira este código manualmente: <b>%s</b></p>
            <p><i>⚠ Este código é válido por 15 minutos e só pode ser usado uma vez.</i></p>
            <p>Se você não solicitou este registro, ignore este e-mail.</p>
            <p>Atenciosamente,<br/>Equipe Login Project</p>
            """.formatted(user.getName(), link, code);

            case SPANISH -> """
            <p>Hola %s,</p>
            <p>Gracias por registrarte en <b>Login Project</b>!</p>
            <p>Para activar tu cuenta, haz clic en el botón a continuación:</p>
            <p><a href="%s" style="display:inline-block;padding:10px 20px;margin:10px 0;
            background:#4CAF50;color:white;text-decoration:none;border-radius:5px;">Activar Cuenta</a></p>
            <p>Y usa este código manualmente: <b>%s</b></p>
            <p><i>⚠ Este código es válido por 15 minutos y sólo puede usarse una vez.</i></p>
            <p>Si no solicitaste este registro, ignora este correo.</p>
            <p>Saludos,<br/>Equipo Login Project</p>
            """.formatted(user.getName(), link, code);

            case GERMAN -> """
            <p>Hallo %s,</p>
            <p>Danke für Ihre Registrierung bei <b>Login Project</b>!</p>
            <p>Um Ihr Konto zu aktivieren, klicken Sie auf den folgenden Button:</p>
            <p><a href="%s" style="display:inline-block;padding:10px 20px;margin:10px 0;
            background:#4CAF50;color:white;text-decoration:none;border-radius:5px;">Konto Aktivieren</a></p>
            <p>Und geben Sie diesen Code manuell ein: <b>%s</b></p>
            <p><i>⚠ Dieser Code ist 15 Minuten gültig und kann nur einmal verwendet werden.</i></p>
            <p>Wenn Sie diese Registrierung nicht angefordert haben, ignorieren Sie diese E-Mail.</p>
            <p>Mit freundlichen Grüßen,<br/>Login Project Team</p>
            """.formatted(user.getName(), link, code);

            default -> """
            <p>Hello %s,</p>
            <p>Thank you for signing up on <b>Login Project</b>!</p>
            <p>To activate your account, please click the button below:</p>
            <p><a href="%s" style="display:inline-block;padding:10px 20px;margin:10px 0;
            background:#4CAF50;color:white;text-decoration:none;border-radius:5px;">Activate Account</a></p>
            <p>And enter this code manually: <b>%s</b></p>
            <p><i>⚠ This code is valid for 15 minutes and can only be used once.</i></p>
            <p>If you did not request this registration, please ignore this email.</p>
            <p>Best regards,<br/>Login Project Team</p>
            """.formatted(user.getName(), link, code);
        };
    }

    private String getActivationEmailSubject(String lang) {
        return switch (lang.toUpperCase()) {
            case PORTUGUESE -> "Ative sua conta no Login Project";
            case SPANISH -> "Activa tu cuenta en Login Project";
            case GERMAN -> "Aktivieren Sie Ihr Konto bei Login Project";
            default -> "Verify Your Email to Activate Your Account";
        };
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
