package com.securityspring.infrastructure.adapters.vo;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import com.securityspring.domain.enums.GenderEnum;
import com.securityspring.domain.enums.LanguagesEnum;
import com.securityspring.domain.enums.RolesUserEnum;
import com.securityspring.domain.enums.StatusEnum;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserVO {

    private Long identifier;
    private String username;
    private String email;
    private String name;
    private StatusEnum status;
    private LocalDateTime lastAccessDate;
    private LocalDateTime loginDate;
    private LocalDateTime creationUserDate;
    private LocalDateTime updateDate;
    private int loginAttempt;
    private boolean forcePasswordChange;
    private RolesUserEnum role;
    private LanguagesEnum language;
    private LocalDate passwordChangeDate;
    private String phone;
    private GenderEnum gender;
    private LocalDate birthDate;
    private String city;
    private String state;
    private String address;
    private String zipCode;
    private String country;
    private String addressComplement;

}
