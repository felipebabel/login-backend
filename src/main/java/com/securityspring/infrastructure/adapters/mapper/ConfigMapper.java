package com.securityspring.infrastructure.adapters.mapper;

import com.securityspring.domain.model.ConfigEntity;
import com.securityspring.infrastructure.adapters.vo.ConfigVO;

public class ConfigMapper {

    private ConfigMapper() {}

    public static ConfigVO toVO(ConfigEntity entity) {
        return new ConfigVO(entity.getKey(), entity.getValue(), entity.getUpdateDate());
    }
}
