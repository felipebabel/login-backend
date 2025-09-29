package com.securityspring.domain.port;

import java.util.Optional;
import com.securityspring.domain.model.ConfigEntity;

public interface ConfigRepository extends EntityRepository<ConfigEntity, Long> {

    Optional<ConfigEntity> findByKey(final String key);

}
