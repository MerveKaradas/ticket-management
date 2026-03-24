package com.kafein.ticket_management.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kafein.ticket_management.model.User;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE " +
            "(:query IS NULL OR " +
            "LOWER(CAST(u.name AS text)) LIKE LOWER(CONCAT('%', CAST(:query AS text), '%')) OR " +
            "LOWER(CAST(u.surname AS text)) LIKE LOWER(CONCAT('%', CAST(:query AS text), '%')) OR " +
            "LOWER(CAST(u.email AS text)) LIKE LOWER(CONCAT('%', CAST(:query AS text), '%')) OR " + 
            "LOWER(CAST(u.role AS text)) LIKE LOWER(CONCAT('%', CAST(:query AS text), '%')) OR " +
            "LOWER(CONCAT(CAST(u.name AS text), ' ', CAST(u.surname AS text))) LIKE LOWER(CONCAT('%', CAST(:query AS text), '%')))")
    Page<User> searchUsers(@Param("query") String query, Pageable pageable);

    List<User> findAllByActiveTrue();

}
