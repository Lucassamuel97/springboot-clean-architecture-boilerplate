package com.starter.crudexample.infrastructure.user;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;

import com.starter.crudexample.MySQLGatewayTest;
import com.starter.crudexample.domain.pagination.SearchQuery;
import com.starter.crudexample.domain.user.Role;
import com.starter.crudexample.domain.user.User;
import com.starter.crudexample.domain.user.UserID;
import com.starter.crudexample.infrastructure.user.persistence.UserJpaEntity;
import com.starter.crudexample.infrastructure.user.persistence.UserRepository;

@MySQLGatewayTest
public class UserMySQLGatewayTest {

	@Autowired
	private UserMySQLGateway userGateway;

	@Autowired
	private UserRepository userRepository;

	@Test
	public void testDependencies() {
		Assertions.assertNotNull(userGateway);
		Assertions.assertNotNull(userRepository);
	}

	@Test
	public void givenAValidUser_whenCallsCreate_shouldPersistIt() {
		// given
		final var expectedUsername = "johndoe";
		final var expectedEmail = "john.doe@example.com";
		final var expectedPassword = "123456";
		final var expectedRoles = List.of(Role.USER);
		final var expectedActive = true;

		final var aUser = User.newUser(expectedUsername, expectedEmail, expectedPassword, expectedRoles, expectedActive);
		final var expectedId = aUser.getId();

		Assertions.assertEquals(0, userRepository.count());

		// when
		final var actualUser = userGateway.create(User.with(aUser));

		// then
		Assertions.assertEquals(1, userRepository.count());

		Assertions.assertEquals(expectedId, actualUser.getId());
		Assertions.assertEquals(expectedUsername, actualUser.getUsername());
		Assertions.assertEquals(expectedEmail, actualUser.getEmail());
		Assertions.assertEquals(expectedPassword, actualUser.getPassword());
		Assertions.assertEquals(expectedRoles, actualUser.getRoles());
		Assertions.assertEquals(expectedActive, actualUser.isActive());
		Assertions.assertNotNull(actualUser.getCreatedAt());
		Assertions.assertNotNull(actualUser.getUpdatedAt());

		final var actualEntity = userRepository.findById(expectedId.getValue()).get();

		Assertions.assertEquals(expectedId.getValue(), actualEntity.getId());
		Assertions.assertEquals(expectedUsername, actualEntity.getUsername());
		Assertions.assertEquals(expectedEmail, actualEntity.getEmail());
		Assertions.assertEquals(expectedPassword, actualEntity.getPassword());
		Assertions.assertEquals(expectedRoles, actualEntity.getRoles());
		Assertions.assertEquals(expectedActive, actualEntity.isActive());
		Assertions.assertNotNull(actualEntity.getCreatedAt());
		Assertions.assertNotNull(actualEntity.getUpdatedAt());
	}

	@Test
	public void givenAValidUser_whenCallsUpdate_shouldRefreshIt() {
		// given
		final var expectedUsername = "johnny";
		final var expectedEmail = "johnny.doe@example.com";
		final var expectedPassword = "abcdef";
		final var expectedRoles = List.of(Role.ADMIN, Role.USER);
		final var expectedActive = false;

		final var aUser = User.newUser("johndoe", "john.doe@example.com", "123456", List.of(Role.USER), true);
		final var expectedId = aUser.getId();

		final var currentUser = userRepository.saveAndFlush(UserJpaEntity.from(aUser));

		Assertions.assertEquals(1, userRepository.count());
		Assertions.assertEquals(expectedId.getValue(), currentUser.getId());
		Assertions.assertEquals(aUser.getUsername(), currentUser.getUsername());

		// when
		final var actualUser = userGateway.update(
			User.with(aUser).update(expectedUsername, expectedEmail, expectedPassword, expectedRoles, expectedActive)
		);

		// then
		Assertions.assertEquals(1, userRepository.count());

		Assertions.assertEquals(expectedId, actualUser.getId());
		Assertions.assertEquals(expectedUsername, actualUser.getUsername());
		Assertions.assertEquals(expectedEmail, actualUser.getEmail());
		Assertions.assertEquals(expectedPassword, actualUser.getPassword());
		Assertions.assertEquals(expectedRoles, actualUser.getRoles());
		Assertions.assertEquals(expectedActive, actualUser.isActive());
		Assertions.assertEquals(aUser.getCreatedAt(), actualUser.getCreatedAt());
		Assertions.assertTrue(aUser.getUpdatedAt().isBefore(actualUser.getUpdatedAt()));

		final var actualEntity = userRepository.findById(expectedId.getValue()).get();

		Assertions.assertEquals(expectedId.getValue(), actualEntity.getId());
		Assertions.assertEquals(expectedUsername, actualEntity.getUsername());
		Assertions.assertEquals(expectedEmail, actualEntity.getEmail());
		Assertions.assertEquals(expectedPassword, actualEntity.getPassword());
		Assertions.assertEquals(expectedRoles, actualEntity.getRoles());
		Assertions.assertEquals(expectedActive, actualEntity.isActive());
		Assertions.assertEquals(aUser.getCreatedAt(), actualEntity.getCreatedAt());
		Assertions.assertTrue(aUser.getUpdatedAt().isBefore(actualEntity.getUpdatedAt()));
	}

	@Test
	public void givenTwoUsersAndOnePersisted_whenCallsExistsByIds_shouldReturnPersistedID() {
		// given
		final var aUser = User.newUser("johndoe", "john.doe@example.com", "123456", List.of(Role.USER), true);
		final var expectedId = aUser.getId();

		Assertions.assertEquals(0, userRepository.count());
		userRepository.saveAndFlush(UserJpaEntity.from(aUser));
		Assertions.assertEquals(1, userRepository.count());

		// when
		final var actual = userGateway.existsByIds(List.of(expectedId, UserID.from("123")));

		// then
		Assertions.assertEquals(1, actual.size());
		Assertions.assertTrue(actual.contains(expectedId));
		Assertions.assertEquals(expectedId.getValue(), actual.get(0).getValue());
	}

	@Test
	public void givenAValidUser_whenCallsDeleteById_shouldDeleteIt() {
		// given
		final var aUser = User.newUser("johndoe", "john.doe@example.com", "123456", List.of(Role.USER), true);
		final var expectedId = aUser.getId();

		Assertions.assertEquals(0, userRepository.count());
		userRepository.saveAndFlush(UserJpaEntity.from(aUser));
		Assertions.assertEquals(1, userRepository.count());

		// when
		userGateway.deleteById(expectedId);

		// then
		Assertions.assertEquals(0, userRepository.count());
	}

	@Test
	public void givenAnInvalidId_whenCallsDeleteById_shouldBeIgnored() {
		// given
		final var aUser = User.newUser("johndoe", "john.doe@example.com", "123456", List.of(Role.USER), true);

		Assertions.assertEquals(0, userRepository.count());
		userRepository.saveAndFlush(UserJpaEntity.from(aUser));
		Assertions.assertEquals(1, userRepository.count());

		// when
		userGateway.deleteById(UserID.from("123"));

		// then
		Assertions.assertEquals(1, userRepository.count());
	}

	@Test
	public void givenAValidUser_whenCallsFindById_shouldReturnIt() {
		// given
		final var aUser = User.newUser("johndoe", "john.doe@example.com", "123456", List.of(Role.USER), true);
		final var expectedId = aUser.getId();

		Assertions.assertEquals(0, userRepository.count());
		userRepository.saveAndFlush(UserJpaEntity.from(aUser));
		Assertions.assertEquals(1, userRepository.count());

		// when
		final var actualUser = userGateway.findById(expectedId);

		// then
		Assertions.assertTrue(actualUser.isPresent());

		Assertions.assertEquals(expectedId, actualUser.get().getId());
		Assertions.assertEquals(aUser.getUsername(), actualUser.get().getUsername());
		Assertions.assertEquals(aUser.getEmail(), actualUser.get().getEmail());
		Assertions.assertEquals(aUser.getPassword(), actualUser.get().getPassword());
		Assertions.assertEquals(aUser.getRoles(), actualUser.get().getRoles());
		Assertions.assertEquals(aUser.isActive(), actualUser.get().isActive());
		Assertions.assertEquals(aUser.getCreatedAt(), actualUser.get().getCreatedAt());
		Assertions.assertEquals(aUser.getUpdatedAt(), actualUser.get().getUpdatedAt());
	}

	@Test
	public void givenAnInvalidId_whenCallsFindById_shouldReturnEmpty() {
		// given
		final var aUser = User.newUser("johndoe", "john.doe@example.com", "123456", List.of(Role.USER), true);

		Assertions.assertEquals(0, userRepository.count());
		userRepository.saveAndFlush(UserJpaEntity.from(aUser));
		Assertions.assertEquals(1, userRepository.count());

		// when
		final var actual = userGateway.findById(UserID.from("123"));

		// then
		Assertions.assertTrue(actual.isEmpty());
	}

	@Test
	public void givenEmptyUsers_whenCallsFindAll_shouldReturnEmpty() {
		// given
		final var expectedPage = 0;
		final var expectedPerPage = 10;
		final var expectedTerms = "";
		final var expectedSort = "username";
		final var expectedDirection = "asc";
		final var expectedTotal = 0;

		final var aQuery = new SearchQuery(
				expectedPage,
				expectedPerPage,
				expectedTerms,
				expectedSort,
				expectedDirection);

		// when
		final var actualResult = userGateway.findAll(aQuery);

		// then
		Assertions.assertEquals(expectedPage, actualResult.currentPage());
		Assertions.assertEquals(expectedPerPage, actualResult.perPage());
		Assertions.assertEquals(expectedTotal, actualResult.total());
		Assertions.assertTrue(actualResult.items().isEmpty());
	}

	@ParameterizedTest
	@CsvSource({
			"ali,0,10,1,1,alice",
			"bob,0,10,1,1,bob",
			"car,0,10,1,1,carol",
			"eve,0,10,1,1,eve",
	})
	public void givenAValidTerm_whenCallsFindAll_shouldReturnFiltered(
			final String expectedTerms,
			final int expectedPage,
			final int expectedPerPage,
			final int expectedItemsCount,
			final long expectedTotal,
			final String expectedUsername) {
		// given
		this.mockUsers();

		final var aQuery = new SearchQuery(
				expectedPage,
				expectedPerPage,
				expectedTerms,
				"username",
				"asc");

		// when
		final var actualResult = userGateway.findAll(aQuery);

		// then
		Assertions.assertEquals(expectedPage, actualResult.currentPage());
		Assertions.assertEquals(expectedPerPage, actualResult.perPage());
		Assertions.assertEquals(expectedItemsCount, actualResult.items().size());
		Assertions.assertEquals(expectedTotal, actualResult.total());
		Assertions.assertEquals(expectedUsername, actualResult.items().get(0).getUsername());
	}

    @ParameterizedTest
    @CsvSource({
	    "username,asc,0,10,5,5,alice",
	    "username,desc,0,10,5,5,eve",
	    "createdAt,asc,0,10,5,5,alice",
	    "createdAt,desc,0,10,5,5,eve",
    })
	public void givenAValidSortAndDirection_whenCallsFindAll_shouldReturnSorted(
		final String expectedSort,
			final String expectedDirection,
			final int expectedPage,
			final int expectedPerPage,
			final int expectedItemsCount,
			final long expectedTotal,
			final String expectedFirstUsername
	) {
		// given
		this.mockUsers();

		final var aQuery = new SearchQuery(
				expectedPage,
				expectedPerPage,
				"",
				expectedSort,
				expectedDirection);

		// when
		final var actualResult = userGateway.findAll(aQuery);

		// then
		Assertions.assertEquals(expectedPage, actualResult.currentPage());
		Assertions.assertEquals(expectedPerPage, actualResult.perPage());
		Assertions.assertEquals(expectedItemsCount, actualResult.items().size());
		Assertions.assertEquals(expectedTotal, actualResult.total());
		Assertions.assertEquals(expectedFirstUsername, actualResult.items().get(0).getUsername());
	}

	@Test
	public void givenAnExistingEmail_whenCallsExistsByEmail_shouldReturnTrue() {
		// given
		final var aUser = User.newUser("alice", "alice@example.com", "123456", List.of(Role.USER), true);
		userRepository.saveAndFlush(UserJpaEntity.from(aUser));

		// when
		final var exists = userGateway.existsByEmail("alice@example.com");
		final var notExists = userGateway.existsByEmail("nobody@example.com");

		// then
		Assertions.assertTrue(exists);
		Assertions.assertFalse(notExists);
	}

	private void mockUsers() {
		// Salvando individualmente para garantir diferenças (sequenciais) em createdAt
		userRepository.saveAndFlush(UserJpaEntity.from(User.newUser("alice", "alice@example.com", "123456", List.of(Role.USER), true)));
		userRepository.saveAndFlush(UserJpaEntity.from(User.newUser("bob", "bob@example.com", "123456", List.of(Role.USER), true)));
		userRepository.saveAndFlush(UserJpaEntity.from(User.newUser("carol", "carol@example.com", "123456", List.of(Role.USER), true)));
		userRepository.saveAndFlush(UserJpaEntity.from(User.newUser("dave", "dave@example.com", "123456", List.of(Role.USER), true)));
	userRepository.saveAndFlush(UserJpaEntity.from(User.newUser("eve", "eve@example.com", "123456", List.of(Role.USER), true)));
	}
}
