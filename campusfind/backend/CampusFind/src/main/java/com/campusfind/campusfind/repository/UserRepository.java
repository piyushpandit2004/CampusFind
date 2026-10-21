
package com.campusfind.campusfind.repository;

import com.campusfind.campusfind.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// REPOSITORY: User entity ke database operations handle karta hai.
public interface UserRepository extends JpaRepository<User, Long> {

    // Email se user search karne ke liye; login mein use hoga.
    Optional<User> findByEmail(String email);

    // Check karne ke liye ki email pehle se registered hai ya nahi.
    boolean existsByEmail(String email);
}
