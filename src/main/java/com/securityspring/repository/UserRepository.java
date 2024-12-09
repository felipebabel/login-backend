package com.securityspring.repository;

import com.securityspring.util.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

    @Query("SELECT u FROM User u WHERE u.username = :username and u.password = :password")
    Optional<User> findUserByPassword(@Param("username") final String username, @Param("password") final String password);

    @Query("SELECT u FROM User u WHERE u.username = :username")
    Optional<User> findUser(@Param("username") final String username);
}

