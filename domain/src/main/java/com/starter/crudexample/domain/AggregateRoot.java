package com.starter.crudexample.domain;

import java.util.List;

import com.starter.crudexample.domain.event.DomainEvent;

public abstract class AggregateRoot<ID extends Identifier> extends Entity<ID> {

    protected AggregateRoot(ID id) {
        super(id);
    }

    protected AggregateRoot(final ID id, final List<DomainEvent> events) {
        super(id, events);
    }
}
