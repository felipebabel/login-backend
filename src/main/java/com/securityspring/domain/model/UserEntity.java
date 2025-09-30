package com.securityspring.domain.model;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import com.securityspring.domain.enums.LanguagesEnum;
import com.securityspring.domain.enums.RolesUserEnum;
import com.securityspring.domain.enums.StatusEnum;

@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "login_user")
public class UserEntity {

    @Id
    @Column(name = "CD_IDENTIFIER")
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "LOGIN_USER_SEQ"
    )
    @SequenceGenerator(
            name = "LOGIN_USER_SEQ",
            sequenceName = "LOGIN_USER_SEQ",
            allocationSize = 1
    )
    private Long identifier;

    @Column(name = "DS_USERNAME", length = 255, nullable = false, unique = true)
    private String username;

    @Column(name = "DS_PASSWORD", length = 255, nullable = false)
    private String password;

    @Column(name = "DS_EMAIL", length = 255, nullable = false, unique = true)
    private String email;

    @Column(name = "DS_NAME", length = 255, nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "DS_STATUS", length = 50, nullable = false)
    private StatusEnum status;

    @Column(name = "DT_LAST_ACCESS", nullable = false)
    private LocalDateTime lastAccessDate;

    @Column(name = "DT_LOGIN")
    private LocalDateTime loginDate;

    @Column(name = "DT_CREATION_USER", nullable = false)
    private LocalDateTime creationUserDate;

    @Builder.Default
    @Column(name = "DT_UPDATE", nullable = false)
    private LocalDateTime updateDate = LocalDateTime.now();

    @Column(name = "NR_LOGIN_ATTEMPT", nullable = false)
    private int loginAttempt;

    @Column(name = "IE_FORCE_PASSWORD_CHANGE", nullable = false)
    private boolean forcePasswordChange;

    @Enumerated(EnumType.STRING)
    @Column(name = "DS_ROLE", nullable = false)
    private RolesUserEnum role;

    @Enumerated(EnumType.STRING)
    @Column(name = "DS_LANGUAGE")
    private LanguagesEnum language;

    @Builder.Default
    @Column(name = "DT_PASSWORD_CHANGE", nullable = false)
    private LocalDateTime passwordChangeDate = LocalDateTime.now();

}
