package com.starter.crudexample.infrastructure.item.persistence;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ItemRepository  extends JpaRepository<ItemJpaEntity, String> {
    Page<ItemJpaEntity> findAll(Specification<ItemJpaEntity> specification, Pageable page);

    @Query(value = "select c.id from Item c where c.id in :ids")
    List<String> existsByIds(@Param("ids") List<String> ids);
}
