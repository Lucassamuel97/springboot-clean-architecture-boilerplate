# Spring Boot Clean Architecture Boilerplate

Boilerplate modular construído com Spring Boot e orientado pelos princípios da Clean Architecture. Ele serve como base para serviços de fácil manutenção, com separação clara entre as camadas de domínio, aplicação e infraestrutura. O projeto inclui autenticação JWT completa, integração com MySQL via Flyway e suíte de testes automatizados.

## Índice
- [Estrutura do Projeto](#estrutura-do-projeto)
- [Visão Arquitetural](#visão-arquitetural)
  - [Camada de Domínio](#camada-de-domínio)
  - [Camada de Aplicação](#camada-de-aplicação)
  - [Camada de Infraestrutura](#camada-de-infraestrutura)
- [Primeiros Passos](#primeiros-passos)
  - [Pré-requisitos](#pré-requisitos)
  - [Clonando o Repositório](#clonando-o-repositório)
- [Executando a Aplicação](#executando-a-aplicação)
  - [1. Subir MySQL com Docker](#1-subir-mysql-com-docker)
  - [2. Fazer o Build do Projeto](#2-fazer-o-build-do-projeto)
  - [3. Executar via Jar](#3-executar-via-jar)
  - [4. Alternativa: Gradle BootRun](#4-alternativa-gradle-bootrun)
- [Executando os Testes](#executando-os-testes)
- [Autenticação da API](#autenticação-da-api)
- [Resolução de Problemas](#resolução-de-problemas)
- [Comandos Úteis](#comandos-úteis)
- [Contribuindo](#contribuindo)

## Estrutura do Projeto

```
.
├── application/              # Casos de uso e serviços da aplicação
├── domain/                   # Regras de negócio e modelos
├── infrastructure/           # Frameworks, adaptadores e integrações externas
├── docs/                     # Documentação adicional
├── build.gradle.kts          # Configuração raiz do Gradle
├── docker-compose.yml        # Configuração local do MySQL
└── README.md / README_pt_br.md
```

## Visão Arquitetural

A base segue Clean Architecture para manter as regras de negócio independentes de frameworks e drivers. Cada módulo possui arquivo Gradle próprio e pode ser desenvolvido, testado e empacotado de maneira isolada.

### Camada de Domínio

diretório: `domain/`

- Entidades centrais, value objects e interfaces (`Gateways`).
- Não depende de Spring ou qualquer framework.
- Define contratos que a infraestrutura implementa.

### Camada de Aplicação

diretório: `application/`

- Implementa casos de uso e orquestra a lógica de domínio.
- Depende apenas do módulo `domain`.
- Contém portas de entrada/saída, DTOs e facades.

### Camada de Infraestrutura

diretório: `infrastructure/`

- Adapters para persistência (MySQL + Spring Data JPA), controllers web, segurança e serviços externos.
- Implementa `Gateways` concretos para atender aos contratos do domínio.
- Reúne configurações do Spring Boot, migrações Flyway, controllers REST e configuração de segurança.

### Autenticação

Detalhes completos sobre o fluxo JWT, componentes envolvidos e testes estão documentados em:

- [Implementação da Autenticação](docs/AUTHENTICATION_IMPLEMENTATION.md)

## Primeiros Passos

### Pré-requisitos
- Java 22 ou superior
- Gradle (utilize o wrapper incluso)
- Docker e Docker Compose
- Git

### Clonando o Repositório

```bash
git clone https://github.com/Lucassamuel97/springboot-clean-architecture-boilerplate.git
cd springboot-clean-architecture-boilerplate
```

## Executando a Aplicação

### 1. Subir MySQL com Docker

```bash
docker compose up -d
```

O comando lê `docker-compose.yml` e inicia uma instância MySQL 8 exposta na porta `3307`. Os dados ficam salvos no volume Docker `mysql_data`.

### 2. Fazer o Build do Projeto

```bash
./gradlew clean build
```

Executa testes, compila todos os módulos e gera o jar executável em `infrastructure/build/libs/`.

### 3. Executar via Jar

```bash
java -jar infrastructure/build/libs/infrastructure-0.0.1-SNAPSHOT.jar
```

A aplicação fica disponível em `http://localhost:8080`. Para customizar URL do banco, credenciais ou parâmetros de JWT, ajuste `application.yml` ou defina variáveis de ambiente.

### 4. Alternativa: Gradle BootRun

```bash
./gradlew :infrastructure:bootRun
```

Ideal para desenvolvimento local, habilitando Spring DevTools para hot reload.

## Executando os Testes

Rodar toda a suíte:

```bash
./gradlew test
```

Apenas testes do módulo de infraestrutura:

```bash
./gradlew :infrastructure:test
```

Relatórios ficam em `build/reports/tests/` na raiz ou dentro do `build/` de cada módulo.

## Autenticação da API

A API expõe endpoints de login e protege recursos com controle de acesso baseado em roles. Veja exemplos de uso, dicas de Swagger e recomendações de segurança:

- [Implementação da Autenticação](docs/AUTHENTICATION_IMPLEMENTATION.md)

## Resolução de Problemas

- **Erro de conexão com banco**: certifique-se de que o container `mysql-clean-architecture` está ativo em `localhost:3307`.
- **Falha nas migrações Flyway**: verifique os scripts em `infrastructure/src/main/resources/db/migration` e se o banco está consistente.
- **Problemas com JWT**: garanta que `app.jwt.secret` seja o mesmo ao gerar e validar tokens.
- **Conflito de porta**: a API usa `8080` por padrão. Mude com `SERVER_PORT` ou `--server.port=9090` ao rodar o jar.

## Comandos Úteis

| Tarefa | Comando |
|--------|---------|
| Subir MySQL | `docker compose up -d` |
| Derrubar MySQL | `docker compose down` |
| Build completo | `./gradlew clean build` |
| Gerar boot jar | `./gradlew :infrastructure:bootJar` |
| Executar jar | `java -jar infrastructure/build/libs/infrastructure-0.0.1-SNAPSHOT.jar` |
| Rodar testes | `./gradlew test` |

## Contribuindo

1. Faça um fork do projeto.
2. Crie uma branch: `git checkout -b feat/minha-feature`.
3. Use commits no formato Conventional Commits.
4. Garanta que os testes passam: `./gradlew clean test`.
5. Abra um pull request detalhando alterações e contexto.

Sugestões são bem-vindas! Aproveite a estrutura para construir serviços alinhados com Clean Architecture. 🚀
