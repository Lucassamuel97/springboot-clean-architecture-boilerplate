package com.starter.crudexample.infrastructure.item.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository  extends JpaRepository<ItemJpaEntity, String> {
    Page<ItemJpaEntity> findAll(Specification<ItemJpaEntity> specification, Pageable page);
}
