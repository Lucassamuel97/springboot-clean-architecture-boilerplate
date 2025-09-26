package com.starter.crudexample.infrastructure.item;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.starter.crudexample.domain.item.Item;
import com.starter.crudexample.domain.item.ItemGateway;
import com.starter.crudexample.domain.item.ItemID;
import com.starter.crudexample.domain.pagination.Pagination;
import com.starter.crudexample.domain.pagination.SearchQuery;
import com.starter.crudexample.infrastructure.item.persistence.ItemJpaEntity;
import com.starter.crudexample.infrastructure.item.persistence.ItemRepository;

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
    public Item update(Item anItem) {
        // TODO: Implementar atualização no banco de dados
        throw new UnsupportedOperationException("Método update não implementado ainda");
    }

    @Override
    public Pagination<Item> findAll(SearchQuery aQuery) {
        // TODO: Implementar busca paginada no banco de dados
        throw new UnsupportedOperationException("Método findAll não implementado ainda");
    }

    @Override
    public List<ItemID> existsByIds(Iterable<ItemID> ids) {
        // TODO: Implementar verificação de existência no banco de dados
        throw new UnsupportedOperationException("Método existsByIds não implementado ainda");
    }

    private Item save(final Item anItem) {
        return this.itemRepository.save(ItemJpaEntity.from(anItem)).toAggregate();
    }
}
