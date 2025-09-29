package com.securityspring.domain.model;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import com.securityspring.domain.enums.RolesUserEnum;
import com.securityspring.domain.enums.StatusEnum;

@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "login_password_reset_tokens")
public class PasswordResetTokenEntity {

    @Id
    @Column(name = "CD_IDENTIFIER")
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "LOGIN_PASSWORD_RESET_TOKENS_SEQ"
    )
    @SequenceGenerator(
            name = "LOGIN_PASSWORD_RESET_TOKENS_SEQ",
            sequenceName = "LOGIN_PASSWORD_RESET_TOKENS_SEQ",
            allocationSize = 1
    )
    private Long identifier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CD_USER", referencedColumnName = "CD_IDENTIFIER")
    private UserEntity user;

    @Column(name = "DS_CODE", length = 6, nullable = false)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(name = "DS_STATUS", length = 50, nullable = false)
    private StatusEnum status;

    @Column(name = "DT_EXPIRATION", nullable = false)
    private LocalDateTime expirationDate;

}
