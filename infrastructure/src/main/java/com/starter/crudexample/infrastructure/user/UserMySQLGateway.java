package com.starter.crudexample.infrastructure.user;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import com.starter.crudexample.domain.pagination.Pagination;
import com.starter.crudexample.domain.pagination.SearchQuery;
import com.starter.crudexample.domain.user.User;
import com.starter.crudexample.domain.user.UserGateway;
import com.starter.crudexample.domain.user.UserID;
import com.starter.crudexample.infrastructure.user.persistence.UserJpaEntity;
import com.starter.crudexample.infrastructure.user.persistence.UserRepository;
import com.starter.crudexample.infrastructure.utils.SpecificationUtils;

@Component
public class UserMySQLGateway implements UserGateway {

    private final UserRepository userRepository;

    public UserMySQLGateway(final UserRepository userRepository) {
        this.userRepository = Objects.requireNonNull(userRepository);
    }

    @Override
    public User create(User aUser) { return save(aUser); }

    @Override
    public void deleteById(UserID anId) { this.userRepository.deleteById(anId.getValue()); }

    @Override
    public Optional<User> findById(UserID anId) {
        return this.userRepository.findById(anId.getValue()).map(UserJpaEntity::toAggregate);
    }

    @Override
    public User update(User aUser) { return save(aUser); }

    @Override
    public Pagination<User> findAll(SearchQuery aQuery) {
        final var page = PageRequest.of(
            aQuery.page(),
            aQuery.perPage(),
            Sort.by(Sort.Direction.fromString(aQuery.direction()), aQuery.sort())
        );

        final var where = Optional.ofNullable(aQuery.terms())
            .filter(str -> !str.isBlank())
            .map(this::assembleSpecification)
            .orElse(null);

        final var pageResult = this.userRepository.findAll(where, page);

        return new Pagination<>(
            pageResult.getNumber(),
            pageResult.getSize(),
            pageResult.getTotalElements(),
            pageResult.map(UserJpaEntity::toAggregate).toList()
        );
    }

    @Override
    public List<UserID> existsByIds(Iterable<UserID> ids) {
        final var idList = StreamSupport.stream(ids.spliterator(), false)
            .map(UserID::getValue)
            .toList();

        return this.userRepository.existsByIds(idList).stream()
            .map(UserID::from)
            .toList();
    }

    @Override
    public boolean existsByEmail(String email) {
        return this.userRepository.existsByEmail(email);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return this.userRepository.findByUsername(username).map(UserJpaEntity::toAggregate);
    }

    private User save(final User aUser) {
        return this.userRepository.save(UserJpaEntity.from(aUser)).toAggregate();
    }

    private Specification<UserJpaEntity> assembleSpecification(final String terms) {
        Specification<UserJpaEntity> usernameSpec = SpecificationUtils.like("username", terms);
        Specification<UserJpaEntity> emailSpec = SpecificationUtils.like("email", terms);
        return usernameSpec.or(emailSpec);
    }
}
