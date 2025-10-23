package com.starter.crudexample;

import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.data.repository.CrudRepository;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.starter.crudexample.infrastructure.item.persistence.ItemRepository;
import com.starter.crudexample.infrastructure.user.persistence.UserRepository;

import java.util.Collection;
import java.util.List;

public class MySQLCleanUpExtension implements BeforeEachCallback {

    @Override
    public void beforeEach(final ExtensionContext context) {
        final var appContext = SpringExtension.getApplicationContext(context);
        final var testClass = context.getRequiredTestClass();

        // Para testes E2E, mantém os usuários (necessários para autenticação)
        // Para testes de integração, limpa tudo
        if (isE2ETest(testClass)) {
            // Limpa apenas Items nos testes E2E
            cleanUp(List.of(
                appContext.getBean(ItemRepository.class)
            ));
        } else {
            // Limpa tudo nos testes de integração
            cleanUp(List.of(
                appContext.getBean(ItemRepository.class),
                appContext.getBean(UserRepository.class)
            ));
        }
    }

    private boolean isE2ETest(Class<?> testClass) {
        // Verifica se a classe de teste está no pacote e2e ou tem a anotação @E2ETest
        return testClass.getPackage().getName().contains(".e2e.") ||
               testClass.isAnnotationPresent(E2ETest.class);
    }

    private void cleanUp(final Collection<CrudRepository<?, ?>> repositories) {
        repositories.forEach(CrudRepository::deleteAll);
    }
}
