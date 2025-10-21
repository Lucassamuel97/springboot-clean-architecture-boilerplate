package com.starter.crudexample.infrastructure.user.persistence;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<UserJpaEntity, String> {
    Page<UserJpaEntity> findAll(Specification<UserJpaEntity> specification, Pageable page);

    @Query("select u.id from User u where u.id in :ids")
    List<String> existsByIds(@Param("ids") List<String> ids);

    boolean existsByEmail(String email);

    java.util.Optional<UserJpaEntity> findByUsername(String username);
}
