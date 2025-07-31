package com.securityspring.controller;

import com.securityspring.util.EmailDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "sendEmail", url = "${EMAIL_SERVICE_URL}")
public interface EmailApi {

        @PostMapping("/api/v1/email")
        void sendEmail(@RequestBody EmailDto emailDto);
}
