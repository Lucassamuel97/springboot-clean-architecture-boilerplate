package com.starter.crudexample.infrastructure.item;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import com.starter.crudexample.domain.item.Item;
import com.starter.crudexample.domain.item.ItemGateway;
import com.starter.crudexample.domain.item.ItemID;
import com.starter.crudexample.domain.pagination.Pagination;
import com.starter.crudexample.domain.pagination.SearchQuery;
import com.starter.crudexample.infrastructure.item.persistence.ItemJpaEntity;
import com.starter.crudexample.infrastructure.item.persistence.ItemRepository;
import com.starter.crudexample.utils.SpecificationUtils;

@Component
public class ItemMySQLGateway implements ItemGateway {

    private final ItemRepository itemRepository;

    public ItemMySQLGateway(final ItemRepository itemRepository) {
        this.itemRepository = Objects.requireNonNull(itemRepository);
    }

    @Override
    public Item create(Item anItem) {
        return save(anItem);
    }

    @Override
    public void deleteById(ItemID anId) {
        this.itemRepository.deleteById(anId.getValue());
    }

    @Override
    public Optional<Item> findById(final ItemID anId) {
        return this.itemRepository.findById(anId.getValue())
                .map(ItemJpaEntity::toAggregate);
    }

    @Override
    public Item update(final Item anItem) {
        return save(anItem);
    }

    @Override
    public Pagination<Item> findAll(SearchQuery aQuery) {
        final var page = PageRequest.of(
                aQuery.page(),
                aQuery.perPage(),
                Sort.by(Sort.Direction.fromString(aQuery.direction()), aQuery.sort()));

        final var where = Optional.ofNullable(aQuery.terms())
                .filter(str -> !str.isBlank())
                .map(this::assembleSpecification)
                .orElse(null);

        final var pageResult = this.itemRepository.findAll(where, page);

        return new Pagination<>(
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.map(ItemJpaEntity::toAggregate).toList());
    }

    @Override
    public List<ItemID> existsByIds(Iterable<ItemID> ids) {
        // TODO: Implementar verificação de existência no banco de dados
        throw new UnsupportedOperationException("Método existsByIds não implementado ainda");
    }

    private Item save(final Item anItem) {
        return this.itemRepository.save(ItemJpaEntity.from(anItem)).toAggregate();
    }

    private Specification<ItemJpaEntity> assembleSpecification(final String terms) {
        return SpecificationUtils.like("name", terms);
    }
}
