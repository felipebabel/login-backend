package com.securityspring.controller;

import com.securityspring.util.Major;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface MajorsApi {

    @Operation(
            summary = "Get major by identifier",
            description = "Retrieve the details of a specific major by its identifier.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved the major"),
                    @ApiResponse(responseCode = "404", description = "Major not found")
            }
    )
    @GetMapping("/{id}")
    ResponseEntity<Major> getMajorById(@PathVariable Long id);

    @Operation(
            summary = "Get all majors without cache",
            description = "Retrieve a list of all majors without using cache.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of majors without cache"),
                    @ApiResponse(responseCode = "204", description = "No content")
            }
    )
    @GetMapping("/majors/without-cache")
    ResponseEntity<List<Major>> getAllMajors();
}
