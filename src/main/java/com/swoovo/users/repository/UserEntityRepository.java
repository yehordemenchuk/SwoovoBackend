package com.swoovo.users.repository;

import com.swoovo.users.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Optional;

@Repository
public interface UserEntityRepository extends JpaRepository<UserEntity,Long> {
    Optional<UserEntity> findByNameAndSurname(String name, String surname);

    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByPhoneNumber(BigInteger phoneNumber);
}
