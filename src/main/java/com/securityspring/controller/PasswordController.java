package com.securityspring.controller;

import com.securityspring.service.PasswordService;
import com.securityspring.util.DefaultResponse;
import lombok.AllArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/api/v1/password")
public class PasswordController implements PasswordApi{

    private final PasswordService passwordService;

    static final Logger LOGGER = LoggerFactory.getLogger("PasswordController");

    public PasswordController(PasswordService passwordService) {
        this.passwordService = passwordService;
    }

    @Override
    @PostMapping("/encrypt")
    public ResponseEntity<DefaultResponse> encryptPassword(@RequestParam("password") final String password) throws BadRequestException {
        LOGGER.info("Encrypting password");
        final String passwordEncrypted = this.passwordService.encryptPassword(password);
        LOGGER.info("Password Encrypted successfully");
        return new ResponseEntity<>(DefaultResponse.builder().message("Password encrypted: " + passwordEncrypted)
                .build(), HttpStatus.OK);
    }

    @Override
    @PostMapping("/decrypt")
    public ResponseEntity<DefaultResponse> decryptPassword(@RequestParam("password") final String password) throws Exception {
        LOGGER.info("Decrypting password");
        final String passwordDecrypted = this.passwordService.decryptPassword(password);
        LOGGER.info("Password Decrypted successfully");
        return ResponseEntity.ok(DefaultResponse.builder().message("Password decrypted: " + passwordDecrypted).build());
    }
}
