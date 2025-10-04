package com.securityspring.infrastructure.adapters.api;

import jakarta.servlet.http.HttpServletRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import com.securityspring.domain.exception.ConfigNotFoundException;
import com.securityspring.infrastructure.adapters.dto.DefaultResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

public interface ConfigApi {


    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = DefaultResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid password"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    ResponseEntity<Object> setConfig(@RequestParam("configValue") final Long configValue,
                                     @RequestParam("configDescription") final String configDescription,
                                     @RequestParam("userRequired") final Long userRequired,
                                     final HttpServletRequest httpServletRequest);

    @Operation(summary = "Login", description = "Validates user credentials and logs in the user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = DefaultResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid password"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    ResponseEntity<Object> getPasswordExpiry(@RequestParam("configDescription") final String configDescription) throws ConfigNotFoundException;

}
