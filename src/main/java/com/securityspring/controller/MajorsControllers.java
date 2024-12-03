package com.securityspring.controller;

import com.securityspring.service.MajorService;
import com.securityspring.util.Major;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/major")
public class MajorsControllers implements MajorsApi {

    private final MajorService majorService;

    @Autowired
    public MajorsControllers(MajorService majorService) {
        this.majorService = majorService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Major> getMajorById(@PathVariable("id") final Long identifier) {
        Major major = this.majorService.getMajor(identifier);
        if (Objects.nonNull(major)) {
            return ResponseEntity.ok(major);

        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<Major>> getAllMajors() {
        List<Major> majors = this.majorService.getAllMajors();
        if (majors != null && !majors.isEmpty()) {
            return ResponseEntity.ok(majors);
        } else {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
    }
}
