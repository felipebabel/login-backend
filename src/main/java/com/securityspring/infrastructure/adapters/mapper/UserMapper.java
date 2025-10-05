package com.securityspring.infrastructure.adapters.mapper;

import com.securityspring.domain.model.UserEntity;
import com.securityspring.infrastructure.adapters.vo.UserVO;

public class UserMapper {

    private UserMapper() {}

    public static UserVO toVO(UserEntity entity) {
        if (entity == null) {
            return null;
        }

        return new UserVO(
                entity.getIdentifier(),
                entity.getUsername(),
                entity.getEmail(),
                entity.getName(),
                entity.getStatus(),
                entity.getLastAccessDate(),
                entity.getLoginDate(),
                entity.getCreationUserDate(),
                entity.getUpdateDate(),
                entity.getLoginAttempt(),
                entity.isForcePasswordChange(),
                entity.getRole(),
                entity.getLanguage(),
                entity.getPasswordChangeDate(),
                entity.getPhone(),
                entity.getGender(),
                entity.getBirthDate(),
                entity.getCity(),
                entity.getState(),
                entity.getAddress(),
                entity.getZipCode(),
                entity.getCountry(),
                entity.getAddressComplement()
        );
    }
}
