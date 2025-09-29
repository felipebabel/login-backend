package com.securityspring.domain.model;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "login_config")
public class ConfigEntity {

    @Id
    @Column(name = "CD_IDENTIFIER")
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "LOGIN_CONFIG_SEQ"
    )
    @SequenceGenerator(
            name = "LOGIN_CONFIG_SEQ",
            sequenceName = "LOGIN_CONFIG_SEQ",
            allocationSize = 1
    )
    private Long identifier;

    @Column(name = "DS_KEY", nullable = false, unique = true)
    private String key;

    @Column(name = "DS_VALUE", nullable = false)
    private String value;

    @Builder.Default
    @Column(name = "DT_UPDATE", nullable = false)
    private LocalDateTime updateDate = LocalDateTime.now();
}
