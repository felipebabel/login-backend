package com.securityspring.infrastructure.adapters.repository;

import java.util.Optional;
import com.securityspring.domain.model.ConfigEntity;
import com.securityspring.domain.port.ConfigRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DatabaseConfigRepository extends ConfigRepository {

    @Override
    @Query("select c FROM ConfigEntity c"
            + " WHERE c.key = :key")
    Optional<ConfigEntity> findByKey(@Param("key") final String key);

}

