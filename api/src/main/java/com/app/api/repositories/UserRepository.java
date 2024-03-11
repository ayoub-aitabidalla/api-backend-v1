package com.app.api.repositories;


import com.app.api.entities.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    @Query(value = "SELECT * FROM users u WHERE u.username = ?1", nativeQuery = true)
    UserEntity findByUsername(String username);

    UserEntity findByResetPasswordToken(String token);

}
