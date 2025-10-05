package com.securityspring.infrastructure.adapters.api;

import jakarta.servlet.http.HttpServletRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import com.securityspring.domain.exception.ConfigNotFoundException;
import com.securityspring.infrastructure.adapters.dto.DefaultResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

public interface ConfigApi {

    @Operation(
            summary = "Set configuration",
            description = "Sets a configuration value with description and records the user who changed it."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Configuration set successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = DefaultResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid input parameters"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    ResponseEntity<Object> setConfig(
            @Parameter(description = "Value of the configuration", required = true, example = "12345")
            @RequestParam("configValue") final Long configValue,

            @Parameter(description = "Description of the configuration", required = true, example = "Max login attempts")
            @RequestParam("configDescription") final String configDescription,

            @Parameter(description = "Indicates who changed the configuration", required = true, example = "1")
            @RequestParam("userRequired") final Long userRequired,

            @Parameter(hidden = true)
            final HttpServletRequest httpServletRequest
    );

    @Operation(
            summary = "Get configuration",
            description = "Retrieves a configuration by its description."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Configuration retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = DefaultResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "Configuration not found")
    })
    ResponseEntity<Object> getConfig(
            @Parameter(description = "Description of the configuration to fetch", required = true, example = "Password expiry days")
            @RequestParam("configDescription") final String configDescription
    ) throws ConfigNotFoundException;
}
