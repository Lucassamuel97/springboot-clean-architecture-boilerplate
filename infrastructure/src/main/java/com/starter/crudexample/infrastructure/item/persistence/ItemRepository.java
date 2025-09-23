package com.starter.crudexample.infrastructure.item.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository  extends JpaRepository<ItemJpaEntity, String> {
    
}
