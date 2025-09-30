package com.securityspring.domain.model;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "login_logs")
public class LogsEntity {

    @Id
    @Column(name = "CD_IDENTIFIER")
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "LOGIN_LOGS_SEQ"
    )
    @SequenceGenerator(
            name = "LOGIN_LOGS_SEQ",
            sequenceName = "LOGIN_LOGS_SEQ",
            allocationSize = 1
    )
    private Long identifier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CD_USER", referencedColumnName = "CD_IDENTIFIER")
    private UserEntity user;
    @Column(
            name = "DS_ACTION",
            length = 255,
            nullable = false
    )
    private String action;
    @Column(
            name = "DS_DESCRIPTION",
            length = 255,
            nullable = false
    )
    private String description;
    @Column(
            name = "DS_ADDRESS"
    )
    private String ipAddress;
    @Column(
            name = "DS_DEVICE_NAME"
    )
    private String deviceName;
    @Column(
            name = "DT_UPDATE",
            nullable = false
    )
    private LocalDateTime date;
}